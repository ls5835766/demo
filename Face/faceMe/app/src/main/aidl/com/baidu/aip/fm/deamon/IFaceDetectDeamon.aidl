// IFaceDetectDeamon.aidl
package com.baidu.aip.fm.deamon;

// Declare any non-default types here with import statements

interface IFaceDetectDeamon {

    void start(int width,int height);

    void tranferData(in int[] bimap,int size);
//
//    void ww(int[] data);
//
    int stop();
}
