/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.tb_robot.api.face;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;

import com.baidu.tb_robot.api.face.APIService;
import com.baidu.tb_robot.api.face.FaceHandler;
import com.baidu.tb_robot.api.face.OnResultListener;
import com.baidu.tb_robot.api.face.exception.FaceError;
import com.baidu.tb_robot.api.face.model.RegResult;
import com.baidu.tb_robot.api.face.utils.ImageUtil;
import com.baidu.tb_robot.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.UUID;

public class VerifyFaceHandler implements FaceHandler {

    static class FaceEvent {
        static final int OnEnter = 1;
        static final int OnUpdate = 2;
        static final int OnLeave = 3;
    }

    private int maxCache = 100;


    private SparseArray<FaceCollection> cacheMap = new SparseArray<>();

    public void handleFace(FaceCollection collection, FaceCache faceCache) {
        SortedSet<FaceCache> cache = collection.faces;
        collection.frameIndex += 1;
        if (faceCache == null) {
            if (!cache.isEmpty()) {
                LogUtil.i("xx", "check clear ++++++++++++++++++");
                check(Collections.max(cache), FaceEvent.OnLeave);
                cache.clear();
            }
            LogUtil.i("xx", "face null");
        } else {
            // TODO use object pool;

            boolean verified = false;

            if (!cache.isEmpty()) {
                if (faceCache.detectValue == 0) {
                    boolean hasUpload = false;
                    for (FaceCache fc : cache) {
                        if (fc.detectValue == 0) {
                            hasUpload = true;
                            break;
                        }
                    }

                    if (!hasUpload) {
                        LogUtil.i("xx", "check 0 ++++++++++++++++++" + cache);
                        check(faceCache, FaceEvent.OnEnter);
                        collection.lastVerified = faceCache;
                        verified = true;
                    }
                }

                FaceCache first = cache.first();
                int compare = faceCache.compareTo(first);
                if (compare > 0) {
                    if (cache.size() >= maxCache) {
                        cache.remove(first);
                    }
                    cache.add(faceCache);
                } else {
                    if (cache.size() < maxCache) {
                        cache.add(faceCache);
                    }
                }

                FaceCache max = Collections.max(cache);
                if (!verified && collection.frameIndex % 10 == 0 && max != collection.lastVerified) {

                    check(max, FaceEvent.OnUpdate);
                    collection.lastVerified = max;
                }

            } else {
                cache.add(faceCache);
                if (faceCache.detectValue == 0) {
                    LogUtil.i("xx", "check first ++++++++++++++++++" + cache);
                    check(faceCache, FaceEvent.OnEnter);
                    collection.lastVerified = faceCache;
                }
            }
        }
    }

    private void check(FaceCache faceCache, int faceEvent) {

        try {
            final File file = File.createTempFile(UUID.randomUUID().toString() + "", ".jpg");
            ImageUtil.resize(faceCache.croppedImage, file, 300, 300);

            uploadToOpenAPI(file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadToOpenAPI(final File file) {
        APIService.getInstance().checkface(new OnResultListener<RegResult>() {
            @Override
            public void onResult(RegResult result) {
                LogUtil.i("xx", "check result" + result);
                if (result != null) {
                    for (RegResult.ResultBean re:result.getResult()){
                        Log.e("zhangy",re.getAge()+"");
                    }
                }
                if (file != null) {
                    file.delete();
                }
            }

            @Override
            public void onError(FaceError error) {
                Log.e("xx", "check error" + error);
                if (file != null) {
                    file.delete();
                }
            }
        }, file);
    }

    @Override
    public void handleFaces(List<FaceCache> faces) {
        if (faces == null) {
            clearCache();
        } else {
            HashSet<Integer> ids = new HashSet<>();

            for (FaceCache faceCache : faces) {
                FaceCollection faceCollection = cacheMap.get(faceCache.faceInfo.face_id);
                if (faceCollection == null) {
                    // TODO use object pool;
                    faceCollection = new FaceCollection();
                    faceCollection.faceID = faceCache.faceInfo.face_id;
                    cacheMap.append(faceCache.faceInfo.face_id, faceCollection);
                }
                faceCache.setTrackId(faceCollection.uuid);
                handleFace(faceCollection, faceCache);
                int faceID = faceCache.faceInfo.face_id;
                ids.add(faceID);
            }

            for (int i = 0; i < cacheMap.size(); i++) {
                int key = cacheMap.keyAt(i);
                if (!ids.contains(key)) {
                    // TODO left;
                    FaceCollection cache = cacheMap.get(key);
                    handleFace(cache, null);
                    cacheMap.remove(key);
                    // TODO recycle;
                }
            }
        }
    }

    private void clearCache() {
        for (int i = 0; i < cacheMap.size(); i++) {
            int key = cacheMap.keyAt(i);
            handleFace(cacheMap.get(key), null);
            // TODO recycle
        }
        cacheMap.clear();
    }


    @Override
    public boolean shouldHandle(Bitmap bitmap) {

        return true;
    }

}
