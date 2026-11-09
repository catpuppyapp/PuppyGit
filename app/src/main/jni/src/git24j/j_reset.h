#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_RESET_H__
#define __GIT24J_RESET_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_reset_reset(git_repository *repo, const git_object *target, git_reset_t reset_type, const git_checkout_options *checkout_opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reset_jniReset)(JNIEnv *env, jclass obj, jlong repoPtr, jlong targetPtr, jint reset_type, jlong checkoutOptsPtr);

    /** int git_reset_from_annotated(git_repository *repo, const git_annotated_commit *commit, git_reset_t reset_type, const git_checkout_options *checkout_opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reset_jniFromAnnotated)(JNIEnv *env, jclass obj, jlong repoPtr, jlong commitPtr, jint reset_type, jlong checkoutOptsPtr);

    /** int git_reset_default(git_repository *repo, const git_object *target, const git_strarray *pathspecs); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reset_jniDefault)(JNIEnv *env, jclass obj, jlong repoPtr, jlong targetPtr, jobjectArray pathspecs);

#ifdef __cplusplus
}
#endif
#endif