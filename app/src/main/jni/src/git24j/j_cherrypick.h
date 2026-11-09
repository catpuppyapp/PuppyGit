#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_CHERRYPICK_H__
#define __GIT24J_CHERRYPICK_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** -------- Signature of the header ---------- */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    /** int git_cherrypick_options_init(git_cherrypick_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    /** unsigned int mainline*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsGetMainline)(JNIEnv *env, jclass obj, jlong optionsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsSetMainline)(JNIEnv *env, jclass obj, jlong optionsPtr, jint mainline);

    /** git_merge_options merge_opts*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsGetMergeOpts)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** git_checkout_options checkout_opts*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Cherrypick_jniOptionsGetCheckoutOpts)(JNIEnv *env, jclass obj, jlong optionsPtr);

    /** int git_cherrypick_commit(git_index **out, git_repository *repo, git_commit *cherrypick_commit, git_commit *our_commit, unsigned int mainline, const git_merge_options *merge_options); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cherrypick_jniCommit)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong cherrypickCommitPtr, jlong ourCommitPtr, jint mainline, jlong mergeOptionsPtr);

    /** int git_cherrypick_cherrypick(git_repository *repo, git_commit *commit, const git_cherrypick_options *cherrypick_options); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cherrypick_jniCherrypick)(JNIEnv *env, jclass obj, jlong repoPtr, jlong commitPtr, jlong cherrypickOptionsPtr);

#ifdef __cplusplus
}
#endif
#endif
