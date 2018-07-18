#include <jni.h>
#include "admob.h"

string const AdMob::sAdMobAppId = "ca-app-pub-3940256099942544~3347511713";
string const AdMob::sTestAdId = "ca-app-pub-3940256099942544/6300978111";

AdMob::AdMob() {}

string AdMob::getAppId() {
    return sAdMobAppId;
}

string AdMob::getTestAdId() {
    return sTestAdId;
}

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_muheng_jni_JniAdMob_createNativeObject(
        JNIEnv *env, jobject obj) {
    jlong result;
    result = (jlong) new AdMob();
    return result;
}

JNIEXPORT jstring JNICALL
Java_com_muheng_jni_JniAdMob_getAdmobAppId(
        JNIEnv *env, jobject obj, jlong thiz) {
    return env->NewStringUTF(((AdMob *) thiz)->getAppId().c_str());
}

JNIEXPORT jstring JNICALL
Java_com_muheng_jni_JniAdMob_getTestAdId(
        JNIEnv *env, jobject obj, jlong thiz) {
    return env->NewStringUTF(((AdMob *) thiz)->getTestAdId().c_str());
}

}
