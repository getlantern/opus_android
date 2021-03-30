#include "config.h"
#ifdef ANDROID_V
/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class top_oply_opuslib_OpusTool */

#ifndef _Included_top_oply_opuslib_OpusTool
#define _Included_top_oply_opuslib_OpusTool
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    nativeGetString
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_top_oply_opuslib_OpusTool_nativeGetString
  (JNIEnv *, jobject);

/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    encode
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_top_oply_opuslib_OpusTool_encode
  (JNIEnv *, jobject, jstring, jstring, jstring);

/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    decode
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_top_oply_opuslib_OpusTool_decode
  (JNIEnv *, jobject, jstring, jstring, jstring);

/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    startRecording
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_top_oply_opuslib_OpusTool_startRecording
  (JNIEnv *, jobject, jstring, jint, jint, jint);

/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    stopRecording
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_top_oply_opuslib_OpusTool_stopRecording
  (JNIEnv *, jobject);

/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    play
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_top_oply_opuslib_OpusTool_play
  (JNIEnv *, jobject, jstring);

/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    stopPlaying
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_top_oply_opuslib_OpusTool_stopPlaying
  (JNIEnv *, jobject);

/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    writeFrame
 * Signature: (Ljava/nio/ByteBuffer;I)I
 */
JNIEXPORT jint JNICALL Java_top_oply_opuslib_OpusTool_writeFrame
  (JNIEnv *, jobject, jobject, jint);

/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    isOpusFile
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_top_oply_opuslib_OpusTool_isOpusFile
  (JNIEnv *, jobject, jstring);

/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    openOpusFile
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_top_oply_opuslib_OpusTool_openOpusFile
  (JNIEnv *, jobject, jstring);

/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    seekOpusFile
 * Signature: (F)I
 */
JNIEXPORT jint JNICALL Java_top_oply_opuslib_OpusTool_seekOpusFile
  (JNIEnv *, jobject, jfloat);

/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    closeOpusFile
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_top_oply_opuslib_OpusTool_closeOpusFile
  (JNIEnv *, jobject);

/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    readOpusFile
 * Signature: (Ljava/nio/ByteBuffer;I)V
 */
JNIEXPORT void JNICALL Java_top_oply_opuslib_OpusTool_readOpusFile
  (JNIEnv *, jobject, jobject, jint);

/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    getFinished
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_top_oply_opuslib_OpusTool_getFinished
  (JNIEnv *, jobject);

/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    getSize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_top_oply_opuslib_OpusTool_getSize
  (JNIEnv *, jobject);

/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    getChannelCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_top_oply_opuslib_OpusTool_getChannelCount
  (JNIEnv *env, jobject obj) ;

/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    getPcmOffset
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_top_oply_opuslib_OpusTool_getPcmOffset
  (JNIEnv *, jobject);

/*
 * Class:     top_oply_opuslib_OpusTool
 * Method:    getTotalPcmDuration
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_top_oply_opuslib_OpusTool_getTotalPcmDuration
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif

#endif 
