#include <jni.h>
#include <stdbool.h>

/**
 * Reference: Accessing Java Arrays
 * http://web.mit.edu/javadev/doc/tutorial/native1.1/implementing/array.html
 */

JNIEXPORT void JNICALL
Java_com_hank_myjnitest_NativeSort_nativeSelectionSort(
        JNIEnv *env, jobject thiz, jintArray array) {
    // Get array length
    /**
     * C++
     * jsize len = env->GetArrayLength(array);
     */
    jsize len = (*env)->GetArrayLength(env, array);

    if (len <= 0) {
        return ;
    }

    /**
     * C++
     * jint *cArray = env->GetIntArrayElements(array, 0);
     */
    // Transform jintArray to jint*
    // GetIntArrayElements also pin down memory of jintArray to prevent from GC.
    jint *cArray = (*env)->GetIntArrayElements(env, array, 0);

// Ascending sort
    for (int i = len - 1; i >= 0; i--) {
        int max = cArray[i], maxIdx = i;
        for (int j = i - 1; j >= 0; j--) {
            if (cArray[j] > max) {
                max = cArray[j];
                maxIdx = j;
            }
        }
        if (maxIdx != i) {
            int tmp = cArray[i];
            cArray[i] = max;
            cArray[maxIdx] = tmp;
        }
    }

// Descending sort
//    for (int i = 0; i < len; i++) {
//        int min = cArray[i], minIdx = i;
//        for (int j = i + 1; j < len; j++) {
//            if (cArray[j] > min) {
//                min = cArray[j];
//                minIdx = j;
//            }
//        }
//        if (minIdx != i) {
//            int tmp = cArray[i];
//            cArray[i] = min;
//            cArray[minIdx] = tmp;
//        }
//    }

    // Must be called to copy data back to java array
    (*env)->ReleaseIntArrayElements(env, array, cArray, 0);
}

JNIEXPORT void JNICALL
Java_com_hank_myjnitest_NativeSort_nativeBubbleSort(
        JNIEnv *env, jobject thiz, jintArray array) {
    // Get array length
    jsize len = (*env)->GetArrayLength(env, array);

    if (len <= 0) {
        return ;
    }

    // Transform jintArray to jint*
    // GetIntArrayElements also pin down memory of jintArray to prevent from GC.
    jint *cArray = (*env)->GetIntArrayElements(env, array, 0);

    for (int i = 0; i < len; i++) {
        bool hasChanged = false;
        for (int j = 1; j < len; j++) {
            int k = j - 1;
            // Change to "cArray[j] > cArray[k]" will be descending sort
            if (cArray[j] < cArray[k]) {
                int tmp = cArray[k];
                cArray[k] = cArray[j];
                cArray[j] = tmp;
                hasChanged = true;
            }
        }
        if (!hasChanged) {
            break;
        }
    }

    // Must be called to copy data back to java array
    (*env)->ReleaseIntArrayElements(env, array, cArray, 0);
}

JNIEXPORT void JNICALL
Java_com_hank_myjnitest_NativeSort_nativeInsertionSort(
        JNIEnv *env, jobject thiz, jintArray array) {
    // Get array length
    jsize len = (*env)->GetArrayLength(env, array);

    if (len <= 0) {
        return ;
    }

    // Transform jintArray to jint*
    // GetIntArrayElements also pin down memory of jintArray to prevent from GC.
    jint *cArray = (*env)->GetIntArrayElements(env, array, 0);

    for (int i = 1; i < len; i++) {
        for (int j = i; j > 0; j--) {
            if (cArray[j] < cArray[j-1]) {
                int tmp = cArray[j];
                cArray[j] = cArray[j-1];
                cArray[j-1] = tmp;
            }
        }
    }

    // Must be called to copy data back to java array
    (*env)->ReleaseIntArrayElements(env, array, cArray, 0);
}