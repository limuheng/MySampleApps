#include <jni.h>
#include <stdbool.h>

#include "native_sort.h"

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_hank_myjnicpptest_MySort_createNativeSort(JNIEnv *env, jobject thiz) {
    jlong result;
    result = (jlong) new NativeSort();
    return result;
}

JNIEXPORT void JNICALL
Java_com_hank_myjnicpptest_MySort_nativeSelectionSort(JNIEnv *env, jobject thiz, jlong nativeInstance, jintArray array) {
    // Get array length
    jsize len = env->GetArrayLength(array);

    // Transform jintArray to jint*
    // GetIntArrayElements also pin down memory of jintArray to prevent from GC.
    jint *cArray = env->GetIntArrayElements(array, 0);

    NativeSort *instance = reinterpret_cast<NativeSort *>(nativeInstance);
    instance->selectionSort(cArray, len);

    // Must be called to copy data back to java array
    env->ReleaseIntArrayElements(array, cArray, 0);
}

JNIEXPORT void JNICALL
Java_com_hank_myjnicpptest_MySort_nativeBubbleSort(JNIEnv *env, jobject thiz, jlong nativeInstance, jintArray array) {
    // Get array length
    jsize len = env->GetArrayLength(array);

    // Transform jintArray to jint*
    // GetIntArrayElements also pin down memory of jintArray to prevent from GC.
    jint *cArray = env->GetIntArrayElements(array, 0);

    NativeSort *instance = reinterpret_cast<NativeSort *>(nativeInstance);
    instance->bubbleSort(cArray, len);

    // Must be called to copy data back to java array
    env->ReleaseIntArrayElements(array, cArray, 0);
}

JNIEXPORT void JNICALL
Java_com_hank_myjnicpptest_MySort_nativeInsertionSort(JNIEnv *env, jobject thiz, jlong nativeInstance, jintArray array) {
    // Get array length
    jsize len = env->GetArrayLength(array);

    // Transform jintArray to jint*
    // GetIntArrayElements also pin down memory of jintArray to prevent from GC.
    jint *cArray = env->GetIntArrayElements(array, 0);

    NativeSort *instance = reinterpret_cast<NativeSort *>(nativeInstance);
    instance->insertionSort(cArray, len);

    // Must be called to copy data back to java array
    env->ReleaseIntArrayElements(array, cArray, 0);
}

}