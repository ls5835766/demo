/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.tb_robot.api.face;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.baidu.idl.facesdk.FaceInfo;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public interface FaceHandler {

    float yawScale = 0.8f;

    class FaceCache implements Comparable<FaceCache> {
        public FaceInfo faceInfo;
        public Bitmap croppedImage;
        public int detectValue;
        private String trackId;

        public String getTrackId() {
            return trackId;
        }

        public void setTrackId(String trackId) {
            this.trackId = trackId;
        }

//        private int compareArea(FaceCache o) {
//            //            int a1 = croppedImage.getWidth() * croppedImage.getHeight();
//            //            int a2 = o.croppedImage.getWidth() * o.croppedImage.getHeight();
//
//            float a1 = Math.abs(faceInfo.headPose[1]) + Math.abs(yawScale * faceInfo.headPose[2]) - croppedImage
//                    .getWidth() * croppedImage.getHeight() / 100000;
//            float a2 = Math.abs(o.faceInfo.headPose[1]) + Math.abs(yawScale * o.faceInfo.headPose[2]) - o
//                    .croppedImage.getWidth() * o.croppedImage.getHeight() / 100000;
//            //            return Float.signum(a1 - a2);
//
//            if (a1 < a2) {
//                return 1;
//            }
//
//            if (a1 > a2) {
//                return -1;
//            }
//            return 0;
//
//            //            return Long.signum(a1 - a2);
//        }

        private int compareArea(FaceCache o) {

            double a1 = Math.sqrt(Math.pow(faceInfo.headPose[0], 2) + Math.pow(faceInfo.headPose[1], 2) + Math
                    .pow(faceInfo.headPose[2], 2));
            double a2 = Math.sqrt(Math.pow(o.faceInfo.headPose[0], 2) + Math.pow(o.faceInfo.headPose[1], 2) + Math
                    .pow(o.faceInfo.headPose[2], 2));

            if (a1 < a2) {
                return 1;
            }

            if (a1 > a2) {
                return -1;
            }
            return 0;
        }

        @Override
        public int compareTo(@NonNull FaceCache o) {
            if (o.detectValue == 0) {
                if (this.detectValue == 0) {
                    return compareArea(o);
                } else {
                    return -1;
                }
            } else {
                if (this.detectValue == 0) {
                    return 1;
                } else {
                    return compareArea(o);
                }
            }
        }
    }

    class FaceCollection {
        int faceID;
        SortedSet<FaceCache> faces = new ConcurrentSkipListSet<>(new Comparator<FaceCache>() {

            public int compareArea(FaceCache o1, FaceCache o2) {

                float[] headPose1 = o1.faceInfo.headPose;
                float[] headPose2 = o2.faceInfo.headPose;
                double a1 = Math.sqrt(Math.pow(headPose1[0], 2) + Math.pow(headPose1[1], 2) + Math
                        .pow(headPose1[2], 2));
                double a2 = Math.sqrt(Math.pow(headPose2[0], 2) + Math.pow(headPose2[1], 2) + Math
                        .pow(headPose2[2], 2));

                if (a1 < a2) {
                    return 1;
                }

                if (a1 > a2) {
                    return -1;
                }
                return 0;
            }

            @Override
            public int compare(@NonNull FaceCache o1, @NonNull FaceCache o2) {
                if (o1.detectValue == 0) {
                    if (o2.detectValue == 0) {
                        return compareArea(o1, o2);
                    } else {
                        return -1;
                    }
                } else {
                    if (o2.detectValue == 0) {
                        return 1;
                    } else {
                        return compareArea(o1, o2);
                    }
                }
            }
        });

        int frameIndex;
        FaceCache lastVerified;
        String uuid = UUID.randomUUID().toString();
    }

    boolean shouldHandle(Bitmap bitmap);

    void handleFaces(List<FaceCache> faces);
}
