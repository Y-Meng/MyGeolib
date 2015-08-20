#include <jni.h>
#include <string.h>
#include <android/log.h>
#define DEBUG_TAG "mgeo"

extern "C" {
void Java_com_mcy_geolib_Mgeo_log(JNIEnv* env, jobject thiz, jstring log) {
	jboolean isCopy;
	const char* szlog = env->GetStringUTFChars(log, &isCopy);
	__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK: [%s]", szlog);
	env->ReleaseStringUTFChars(log, szlog);
}

jstring Java_com_mcy_geolib_Mgeo_getString(JNIEnv* env, jobject thiz) {
	return env->NewStringUTF("Hello From Jni!");
}
}

