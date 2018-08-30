#include <jni.h>
#include "com_example_heizepalvin_streetlive_mainFragment_LiveFragment_CreateLiveRoomActivity.h"

#include <opencv2/opencv.hpp>

using namespace cv;

extern "C" {
/*
 * Class:     com_example_heizepalvin_streetlive_mainFragment_LiveFragment_CreateLiveRoomActivity
 * Method:    ConvertRGBtoGray
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_com_example_heizepalvin_streetlive_mainFragment_LiveFragment_CreateLiveRoomActivity_ConvertRGBtoGray
  (JNIEnv *env,
   jobject instance,
    jlong matAddrInput,
     jlong matAddrResult){

     Mat &matInput = *(Mat *)matAddrInput;
     Mat &matResult = *(Mat *)matAddrResult;

     cvtColor(matInput, matResult, CV_RGBA2GRAY);

     }

}