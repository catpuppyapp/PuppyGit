#include "j_message.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** -------- Wrapper Body ---------- */
/** int git_message_prettify(git_buf *out, const char *message, int strip_comments, char comment_char); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Message_jniPrettify)(JNIEnv *env, jclass obj, jobject out, jstring message, jint strip_comments, jchar comment_char)
{
    git_buf c_out = {0};
    char *c_message = j_copy_of_jstring(env, message, true);
    int r = git_message_prettify(&c_out, c_message, strip_comments, comment_char);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    free(c_message);
    return r;
}

/** int git_message_trailers(git_message_trailer_array *arr, const char *message); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Message_jniTrailers)(JNIEnv *env, jclass obj, jobject outArrPtr, jstring message)
{
    char *c_message = j_copy_of_jstring(env, message, true);
    git_message_trailer_array *ptr = (git_message_trailer_array *)malloc(sizeof(git_message_trailer_array));
    int r = git_message_trailers(ptr, c_message);
    (*env)->CallVoidMethod(env, outArrPtr, jniConstants->midAtomicLongSet, (jlong)ptr);
    free(c_message);
    return r;
}

/** void git_message_trailer_array_free(git_message_trailer_array *arr); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Message_jniTrailerArrayFree)(JNIEnv *env, jclass obj, jlong arrPtr)
{
    git_message_trailer_array_free((git_message_trailer_array *)arrPtr);
}

/** const char *key*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Message_jniTrailerGetKey)(JNIEnv *env, jclass obj, jlong trailerPtr)
{
    return (*env)->NewStringUTF(env, ((git_message_trailer *)trailerPtr)->key);
}

/** const char *value*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Message_jniTrailerGetValue)(JNIEnv *env, jclass obj, jlong trailerPtr)
{
    return (*env)->NewStringUTF(env, ((git_message_trailer *)trailerPtr)->value);
}

/** git_message_trailer *trailers*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Message_jniTrailerArrayGetTrailer)(JNIEnv *env, jclass obj, jlong trailerArrayPtr, jint idx)
{
    git_message_trailer_array *ptr = (git_message_trailer_array *)trailerArrayPtr;
    assert((idx < (int)ptr->count) && "out of boundary access of git_message_trailer_array");
    git_message_trailer *trailerPtr = &(ptr->trailers[idx]);
    return (jlong)trailerPtr;
}

/** size_t count*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Message_jniTrailerArrayGetCount)(JNIEnv *env, jclass obj, jlong trailerArrayPtr)
{
    return ((git_message_trailer_array *)trailerArrayPtr)->count;
}

/** char * _trailer_block*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Message_jniTrailerArrayGetTrailerBlock)(JNIEnv *env, jclass obj, jlong trailerArrayPtr)
{
    return (*env)->NewStringUTF(env, ((git_message_trailer_array *)trailerArrayPtr)->_trailer_block);
}
