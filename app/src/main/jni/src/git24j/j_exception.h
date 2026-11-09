
#ifndef __GIT24J_EXCEPTION_H__
#define __GIT24J_EXCEPTION_H__
#ifdef __cplusplus
extern "C"
{
#endif

#include <jni.h>

    jint j_throw_jni_error(JNIEnv *env, const char *message);

    /** call git_error_last and throw error if needed. */
    jint j_throw_last_error(JNIEnv *env);

    /** throw a java exception given its full name. */
    jint j_throw_java_error(JNIEnv *env, const char *exceptionName, const char *message);

#ifdef __cplusplus
}
#endif
#endif
