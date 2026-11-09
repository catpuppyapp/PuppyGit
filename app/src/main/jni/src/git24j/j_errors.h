#include "j_common.h"
#include <jni.h>

#ifndef __GIT24J_ERRORS_H__
#define __GIT24J_ERRORS_H__
#ifdef __cplusplus
extern "C"
{
#endif

    JNIEXPORT jthrowable JNICALL J_MAKE_METHOD(Error_jniLast)(JNIEnv *, jclass);

    JNIEXPORT void JNICALL J_MAKE_METHOD(Error_jniClear)(JNIEnv *, jclass);

    JNIEXPORT void JNICALL J_MAKE_METHOD(Error_jniSetStr)(JNIEnv *, jclass, jint, jstring);

    JNIEXPORT void JNICALL J_MAKE_METHOD(Error_jniSetOom)(JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif

#include <jni.h>
