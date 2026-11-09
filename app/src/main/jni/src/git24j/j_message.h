#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_MESSAGE_H__
#define __GIT24J_MESSAGE_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** -------- Signature of the header ---------- */
    /** int git_message_prettify(git_buf *out, const char *message, int strip_comments, char comment_char); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Message_jniPrettify)(JNIEnv *env, jclass obj, jobject out, jstring message, jint strip_comments, jchar comment_char);

    /** int git_message_trailers(git_message_trailer_array *arr, const char *message); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Message_jniTrailers)(JNIEnv *env, jclass obj, jobject outArrPtr, jstring message);
    /** void git_message_trailer_array_free(git_message_trailer_array *arr); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Message_jniTrailerArrayFree)(JNIEnv *env, jclass obj, jlong arrPtr);
    /** const char *key*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Message_jniTrailerGetKey)(JNIEnv *env, jclass obj, jlong trailerPtr);
    /** const char *value*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Message_jniTrailerGetValue)(JNIEnv *env, jclass obj, jlong trailerPtr);
    /** git_message_trailer *trailers*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Message_jniTrailerArrayGetTrailer)(JNIEnv *env, jclass obj, jlong trailerArrayPtr, jint idx);
    /** size_t count*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Message_jniTrailerArrayGetCount)(JNIEnv *env, jclass obj, jlong trailerArrayPtr);
    /** char * _trailer_block*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Message_jniTrailerArrayGetTrailerBlock)(JNIEnv *env, jclass obj, jlong trailerArrayPtr);

#ifdef __cplusplus
}
#endif
#endif