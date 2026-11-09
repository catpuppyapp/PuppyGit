#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_REVERT_H__
#define __GIT24J_REVERT_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_revert_commit(git_index **out, git_repository *repo, git_commit *revert_commit, git_commit *our_commit, unsigned int mainline, const git_merge_options *merge_options); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revert_jniCommit)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong revertCommitPtr, jlong ourCommitPtr, jint mainline, jlong mergeOptionsPtr);

    /** int git_revert_revert(git_repository *repo, git_commit *commit, const git_revert_options *given_opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revert_jniRevert)(JNIEnv *env, jclass obj, jlong repoPtr, jlong commitPtr, jlong givenOptsPtr);

    /** -------- Signature of the header ---------- */

    /** unsigned int version*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revert_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Revert_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** unsigned int mainline*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revert_jniOptionsGetMainline)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** git_merge_options merge_opts*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Revert_jniOptionsGetMergeOpts)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** git_checkout_options checkout_opts*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Revert_jniOptionsGetCheckoutOpts)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** unsigned int mainline*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Revert_jniOptionsSetMainline)(JNIEnv *env, jclass obj, jlong optionsPtr, jint mainline);
    /** git_merge_options merge_opts*/

#ifdef __cplusplus
}
#endif
#endif