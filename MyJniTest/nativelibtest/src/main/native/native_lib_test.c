#include <jni.h>
#include <stdbool.h>

JNIEXPORT jstring JNICALL
Java_com_hank_nativelibtest_NativeLib_nativeSayHello(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "Hello! World! V1.0");
}