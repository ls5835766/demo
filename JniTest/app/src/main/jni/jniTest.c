#include <jni.h>
#include <string.h>
#include "../../../../../JniProject/app/src/main/jni/baiduaudio_tangdi_com_jnitest_JNiClassUntils.h"
#include "./utils/android_log_print.h"
#include "./local_logic_c/easy_encrypt.h"


//
// Created by td on 2017/10/24.
//

JNIEXPORT jstring JNICALL Java_baiduaudio_tangdi_com_jnitest_JNiClassUntils_helloJni
  (JNIEnv *env, jobject obj){
     return (*env)->NewStringUTF(env,"This just a test for Android Studio NDK JNI developer!");
  }



JNIEXPORT jstring JNICALL Java_baiduaudio_tangdi_com_jnitest_JNiClassUntils_getKeys
        (JNIEnv *env, jobject obj, jstring name){

    //声明局部量
    char key[KEY_SIZE] = {0};
    memset(key, 0, sizeof(key));

    char temp[KEY_NAME_SIZE] = {0};

    //将java传入的name转换为本地utf的char*
    const char* pName = (*env)->GetStringUTFChars(env, name, NULL);

    if (NULL != pName) {
        strcpy(temp, pName);
        strcpy(key, generateKeyRAS(temp));

        //java的name对象不需要再使用，通知虚拟机回收name
        (*env)->ReleaseStringUTFChars(env, name, pName);
    }

    return (*env)->NewStringUTF(env, key);
}

