//
// Created by 海 on 2015/11/5.
//
#include "com_mcy_geo3d_lib_NStringUtil.h"

JNIEXPORT jstring JNICALL Java_com_mcy_geo3d_lib_NStringUtil_getNativeString
        (JNIEnv *env, jclass obj){
    return (*env)->NewStringUTF(env,"String from Native!");
}
