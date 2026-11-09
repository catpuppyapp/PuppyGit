#include "j_errors.h"
#include "j_ensure.h"
#include <assert.h>
#include <git2.h>

JNIEXPORT jthrowable JNICALL J_MAKE_METHOD(Error_jniLast)(JNIEnv *env, jclass obj)
{
    const git_error *error = git_error_last();
    if (error == NULL)
    {
        return NULL;
    }

    jclass cls = (*env)->FindClass(env, J_CLZ_PREFIX "GitException");
    assert(cls && "GitException.class not found");

    jmethodID ctor = (*env)->GetMethodID(env, cls, "<init>", "(ILjava/lang/String;)V");
    assert(cls && "GitException::new not found");

    jstring message = (*env)->NewStringUTF(env, error->message);
    return (*env)->NewObject(env, cls, ctor, error->klass, message);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Error_jniClear)(JNIEnv *env, jclass obj)
{
    // deprecated by libgit2 1.8.x
//    git_error_clear();
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Error_jniSetStr)(JNIEnv *env, jclass obj, jint klass, jstring message)
{
    // deprecated by libgit2 1.8.x
//    const char *c_msg = (*env)->GetStringUTFChars(env, message, NULL);
//    git_error_set_str((int)klass, c_msg);
//    (*env)->ReleaseStringUTFChars(env, message, c_msg);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Error_jniSetOom)(JNIEnv *env, jclass obj)
{
    // deprecated by libgit2 1.8.x

//    git_error_set_oom();
}
