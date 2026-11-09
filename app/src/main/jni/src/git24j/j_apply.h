#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_APPLY_H__
#define __GIT24J_APPLY_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** -------- Signature of the header ---------- */
    /** int git_apply_to_tree(git_index **out, git_repository *repo, git_tree *preimage, git_diff *diff, const git_apply_options *options); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Apply_jniToTree)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong preimagePtr, jlong diffPtr, jlong optionsPtr);

    /** int git_apply_apply(git_repository *repo, git_diff *diff, int location, const git_apply_options *options); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Apply_jniApply)(JNIEnv *env, jclass obj, jlong repoPtr, jlong diffPtr, jint location, jlong optionsPtr);

    /** -------- Signature of the header ---------- */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Apply_jniOptionsNew)(JNIEnv *env, jclass obj, jint version, jobject out, jobject deltaCb, jobject hunkCb);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Apply_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    /** unsigned int flags*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Apply_jniOptionsGetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** unsigned int flags*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Apply_jniOptionsSetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr, jint flags);

#ifdef __cplusplus
}
#endif
#endif