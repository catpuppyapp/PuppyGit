#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_WRITESTREAM_H__
#define __GIT24J_WRITESTREAM_H__
#ifdef __cplusplus
extern "C"
{
#endif

    JNIEXPORT jint JNICALL J_MAKE_METHOD(WriteStream_jniWrite)(JNIEnv *env, jclass obj, jlong wsPtr, jbyteArray content);
    JNIEXPORT void JNICALL J_MAKE_METHOD(WriteStream_jniFree)(JNIEnv *env, jclass obj, jlong wsPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(WriteStream_jniClose)(JNIEnv *env, jclass obj, jlong wsPtr);

#ifdef __cplusplus
}
#endif
#endif
