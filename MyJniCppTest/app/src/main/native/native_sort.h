#ifndef TESTINGAPP_NATIVE_SORT_H
#define TESTINGAPP_NATIVE_SORT_H

#include "jni.h"

using namespace std;

class NativeSort {
public:
    NativeSort();
    void selectionSort(int *array, int length);
    void bubbleSort(int *array, int length);
    void insertionSort(int *array, int length);
private:
};

#endif //TESTINGAPP_NATIVE_SORT_H
