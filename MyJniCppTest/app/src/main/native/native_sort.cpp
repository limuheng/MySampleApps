#include <jni.h>
#include "native_sort.h"

#include <android/log.h>

#define LOGI(TAG, __VA_ARGS__) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

NativeSort::NativeSort() {
    // Empty constructor
}

void NativeSort::selectionSort(int *array, int length) {
    LOGI("NativeSort", ">>> selectionSort");

    if (length <= 0) {
        return ;
    }

    // The start index of right-hand side at begging is 0, means there are no sorted numbers.
    for (int i = 0; i < length; i++) {
        int min = array[i], minIdx = i;
        // Find the min in right-hand side
        for (int j = i + 1; j < length; j++) {
            if (array[j] < min) {
                min = array[j];
                minIdx = j;
            }
        }
        // Place the min in last position of left-hand side
        if (minIdx != i) {
            int tmp = array[i];
            array[i] = min;
            array[minIdx] = tmp;
        }
    }

    LOGI("NativeSort", "<<< selectionSort");
}

void NativeSort::bubbleSort(int *array, int length) {
    LOGI("NativeSort", ">>> bubbleSort");

    if (length <= 0) {
        return ;
    }

    // Repeat length times
    for (int i = 0; i < length; i++) {
        bool hasChanged = false;
        // Start comparing from the first element
        // This loop will move larger elements to tail of the array.
        for (int j = 1; j < length; j++) {
            int k = j - 1;
            // if elements are not in ascending ordered, swap them.
            if (array[k] > array[j]) {
                int tmp = array[k];
                array[k] = array[j];
                array[j] = tmp;
                hasChanged = true;
            }
        }

        if (!hasChanged) {
            break;
        }
    }

    LOGI("NativeSort", "<<< bubbleSort");
}

void NativeSort::insertionSort(int *array, int length) {
    LOGI("NativeSort", ">>> insertionSort");

    if (length <= 0) {
        return ;
    }

    // The index 0 is the first sorted element.
    // The index 1 is the first unsorted element in right-hand side.
    for (int i = 1; i < length; i++) {
        // Insert the first unsorted element in right-hand side to correct position in left-hand side
        for (int j = i; j > 0; j--) {
            int k = j - 1;
            if (array[k] > array[j]) {
                int tmp = array[j];
                array[j] = array[k];
                array[k] = tmp;
            } else {
                break;
            }
        }
    }

    LOGI("NativeSort", "<<< insertionSort");
}
