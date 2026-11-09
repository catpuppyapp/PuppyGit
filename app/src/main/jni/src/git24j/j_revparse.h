#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_REFPARSE_H__
#define __GIT24J_REFPARSE_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /**int git_revparse(git_revspec *revspec, git_repository *repo, const char *spec); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revparse_jniLookup)(JNIEnv *env, jclass obj, jobject revspec, jlong repoPtr, jstring spec);

    /** int git_revparse_single(git_object **out, git_repository *repo, const char *spec); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revparse_jniSingle)(JNIEnv *env, jclass obj, jobject outObj, jlong repoPtr, jstring spec);

    /** int git_revparse_ext(git_object **object_out, git_reference **reference_out, git_repository *repo, const char *spec); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revparse_jniExt)(JNIEnv *env, jclass obj, jobject outObj, jobject outRef, jlong repoPtr, jstring spec);

#ifdef __cplusplus
}
#endif
#endif