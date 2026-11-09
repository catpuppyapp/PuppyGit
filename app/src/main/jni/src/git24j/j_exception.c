
#include "j_exception.h"
#include "j_common.h"
#include "j_libgit2.h"
#include <assert.h>
#include <git2.h>
#include <jni.h>

jint j_throw_last_error(JNIEnv *env)
{
    const git_error *error = git_error_last();
    if (error == NULL)
    {
        return j_throw_java_error(env, J_NO_CLASS_ERROR, error->message);
    }
    return j_throw_jni_error(env, error->message);
}

jint j_throw_jni_error(JNIEnv *env, const char *message)
{
    assert(message);
    const char *classname = J_CLZ_PREFIX "GitException";
    const jclass clz = (*env)->FindClass(env, classname);
    if (clz == NULL)
    {
        return j_throw_java_error(env, J_NO_CLASS_ERROR, message);
    }

    jint ret = (*env)->ThrowNew(env, clz, message);
    (*env)->DeleteLocalRef(env, clz);
    return ret;
}

jint j_throw_java_error(JNIEnv *env, const char *exceptionName, const char *message)
{
    assert(exceptionName);
    jclass clz = (*env)->FindClass(env, exceptionName);
    if (clz == NULL)
    {
        clz = (*env)->FindClass(env, J_NO_CLASS_ERROR);
    }
    assert(clz);
    jint ret = (*env)->ThrowNew(env, clz, message);
    (*env)->DeleteLocalRef(env, clz);
    return ret;
}
