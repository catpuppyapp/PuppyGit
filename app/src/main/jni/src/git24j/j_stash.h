#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_STASH_H__
#define __GIT24J_STASH_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_stash_save(git_oid *out, git_repository *repo, const git_signature *stasher, const char *message, uint32_t flags); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniSave)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong stasherPtr, jstring message, jint flags);

    /** int git_stash_apply_options_init(git_stash_apply_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);

    /** int git_stash_apply(git_repository *repo, size_t index, const git_stash_apply_options *options); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniApply)(JNIEnv *env, jclass obj, jlong repoPtr, jint index, jlong optionsPtr);

    /** int git_stash_foreach(git_repository *repo, git_stash_cb callback, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jobject callback);

    /** int git_stash_drop(git_repository *repo, size_t index); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniDrop)(JNIEnv *env, jclass obj, jlong repoPtr, jint index);

    /** int git_stash_pop(git_repository *repo, size_t index, const git_stash_apply_options *options); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniPop)(JNIEnv *env, jclass obj, jlong repoPtr, jint index, jlong optionsPtr);

    /** -------- git_stash_apply_options ---------- */
    /** git_stash_apply_options */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsFree)(JNIEnv *env, jclass obj, jlong applyOptionsPtr);
    /** uint32_t flags*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsGetFlags)(JNIEnv *env, jclass obj, jlong applyOptionsPtr);
    /** git_checkout_options checkout_options*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsGetCheckoutOptions)(JNIEnv *env, jclass obj, jlong applyOptionsPtr);
    /** uint32_t flags*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsSetFlags)(JNIEnv *env, jclass obj, jlong applyOptionsPtr, jint flags);
    /** git_stash_apply_progress_cb progress_cb*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsSetProgressCb)(JNIEnv *env, jclass obj, jlong applyOptionsPtr, jobject progressCb);

#ifdef __cplusplus
}
#endif
#endif