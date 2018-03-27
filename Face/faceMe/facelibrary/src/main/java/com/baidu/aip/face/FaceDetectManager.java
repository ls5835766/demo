/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.aip.face;

import java.util.ArrayList;

import com.baidu.aip.ImageFrame;
import com.baidu.idl.facesdk.FaceInfo;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

/**
 * 封装了人脸检测的整体逻辑。
 */
public class FaceDetectManager {
    /**
     * 该回调用于回调，人脸检测结果。当没有人脸时，infos为null,status为 ErrCode.ErrCode.NO_FACE_DETECTED
     */
    public interface OnFaceDetectListener {
        void onDetectFace(int status, FaceInfo[] infos, ImageFrame imageFrame);
    }

    /**
     * 图片源，获取检测图片。
     */
    private ImageSource imageSource;
    /**
     * 人脸检测事件监听器
     */
    private OnFaceDetectListener listener;
    private FaceFilter faceFilter = new FaceFilter();
    private HandlerThread processThread;
    private Handler processHandler;
    private ImageFrame lastFrame;

    private ArrayList<FaceProcessor> preProcessors = new ArrayList<>();

    /**
     * 设置人脸检测监听器，检测后的结果会回调。
     * @param listener 监听器
     */
    public void setOnFaceDetectListener(OnFaceDetectListener listener) {
        this.listener = listener;
    }

    /**
     * 设置图片帧来源
     * @param imageSource 图片来源
     */
    public void setImageSource(ImageSource imageSource) {
        this.imageSource = imageSource;
    }

    /**
     * @return 返回图片来源
     */
    public ImageSource getImageSource() {
        return this.imageSource;
    }

    /**
     * 增加处理回调，在人脸检测前会被回调。
     * @param processor 图片帧处理回调
     */
    public void addPreProcessor(FaceProcessor processor) {
        preProcessors.add(processor);
    }

    /**
     * 设置人检跟踪回调。
     * @param onTrackListener 人脸回调
     */
    public void setOnTrackListener(FaceFilter.OnTrackListener onTrackListener) {
        faceFilter.setOnTrackListener(onTrackListener);
    }

    /**
     * @return 返回过虑器
     */
    public FaceFilter getFaceFilter() {
        return faceFilter;
    }

    public void start() {
        this.imageSource.addOnFrameAvailableListener(onFrameAvailableListener);
        processThread = new HandlerThread("process");
        processThread.setPriority(Thread.MAX_PRIORITY);
        processThread.start();
        processHandler = new Handler(processThread.getLooper());
        this.imageSource.start();
    }

    private Runnable processRunnable = new Runnable() {
        @Override
        public void run() {
            if (lastFrame == null || processThread == null || !processThread.isAlive()) {
                return;
            }
            int[] argb;
            int width;
            int height;
            ArgbPool pool;
            synchronized (lastFrame) {
                argb = lastFrame.getArgb();
                width = lastFrame.getWidth();
                height = lastFrame.getHeight();
                pool = lastFrame.getPool();
                lastFrame = null;
            }
            process(argb, width, height, pool);
        }
    };

    public void stop() {
        this.imageSource.stop();
        this.imageSource.removeOnFrameAvailableListener(onFrameAvailableListener);
        if (processThread != null) {
            processThread.quit();
            processThread = null;
        }
        if (processHandler != null) {
            processHandler = null;
        }
    }

    private void process(int[] argb, int width, int height, ArgbPool pool) {
        int value;

        ImageFrame frame = imageSource.borrowImageFrame();
        frame.setArgb(argb);
        frame.setWidth(width);
        frame.setHeight(height);
        frame.setPool(pool);
        //        frame.retain();

        for (FaceProcessor processor : preProcessors) {
            if (processor.process(this, frame)) {
                break;
            }
        }

        long startime = System.currentTimeMillis();
        value = FaceDetector.getInstance().detect(frame.getArgb(), frame.getWidth(), frame.getHeight());
        FaceInfo[] faces = FaceDetector.getInstance().getTrackedFaces();
        Log.e("wtf", "FaceDetectManager process " + (System.currentTimeMillis() - startime));
//        value = FaceSDKManager.getInstance().getFaceTracker().prepare_data_for_verify(
//                frame.getArgb(), frame.getHeight(), frame.getWidth(), FaceSDK.ImgType.ARGB.ordinal(),
//                FaceTracker.ActionType.RECOGNIZE.ordinal());
//        LogUtil.e("wtf", "FaceDetectManager process ");
//        FaceInfo[] faces = FaceSDKManager.getInstance().getFaceTracker().get_TrackedFaceInfo();

        faceFilter.filter(faces, frame);
        if (listener != null) {
            listener.onDetectFace(value, faces, frame);
        }
        frame.release();

    }

    private OnFrameAvailableListener onFrameAvailableListener = new OnFrameAvailableListener() {
        @Override
        public void onFrameAvailable(ImageFrame imageFrame) {
            lastFrame = imageFrame;
            if (processThread == null || !processThread.isAlive()) {
                return;
            }
            processHandler.post(processRunnable);
        }
    };
}
