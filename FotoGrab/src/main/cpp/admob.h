//
// Created by limuh on 2018/7/17.
//

#ifndef PHOTOVIEWER_ADMOB_H
#define PHOTOVIEWER_ADMOB_H

#include "jni.h"
#include <string>

using namespace std;

class AdMob {
public:
    AdMob();
    string getAppId();
    string getTestAdId();
private:
    static const string sAdMobAppId;
    static const string sTestAdId;
};

#endif //PHOTOVIEWER_ADMOB_H
