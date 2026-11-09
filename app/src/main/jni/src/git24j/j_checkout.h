#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_CHECKOUT_H__
#define __GIT24J_CHECKOUT_H__
#ifdef __cplusplus
extern "C"
{
#endif
    // no matching type found for 'git_checkout_notify_t why'
    /** int git_checkout_notify_cb(git_checkout_notify_t why, const char *path, const git_diff_file *baseline, const git_diff_file *target, const git_diff_file *workdir, void *payload); */
    /** -------- Signature of the header ---------- */
    /** int git_checkout_init_options(git_checkout_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** int git_checkout_head(git_repository *repo, const git_checkout_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniHead)(JNIEnv *env, jclass obj, jlong repoPtr, jlong optsPtr);

    /** int git_checkout_index(git_repository *repo, git_index *index, const git_checkout_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniIndex)(JNIEnv *env, jclass obj, jlong repoPtr, jlong indexPtr, jlong optsPtr);

    /** int git_checkout_tree(git_repository *repo, const git_object *treeish, const git_checkout_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniTree)(JNIEnv *env, jclass obj, jlong repoPtr, jlong treeishPtr, jlong optsPtr);

    /** void git_checkout_progress_cb(const char *path, size_t completed_steps, size_t total_steps, void *payload); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniProgressCb)(JNIEnv *env, jclass obj, jstring path, jint completedSteps, jint totalSteps);

    /** void git_checkout_perfdata_cb(const git_checkout_perfdata *perfdata, void *payload); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniPerfdataCb)(JNIEnv *env, jclass obj, jlong perfdataPtr);

    // no matching type found for 'jobject notify_cb'
    /** void git_checkout_options_set_notify_cb(const git_checkout_options *opts, jobject notify_cb); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetNotifyCb)(JNIEnv *env, jclass obj, jlong optsPtr, jobject notifyCb);
    // no matching type found for 'jobject progress_cb'
    /** void git_checkout_options_set_progress_cb(const git_checkout_options *opts, jobject progress_cb); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetProgressCb)(JNIEnv *env, jclass obj, jlong optsPtr, jobject progressCb);
    // no matching type found for 'jobject perfdata_cb'
    /** void git_checkout_options_set_perfdata_cb(const git_checkout_options *opts, jobject perfdata_cb); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetPerfdataCb)(JNIEnv *env, jclass obj, jlong optsPtr, jobject perfdataCb);
    /** -------- Signature of the header ---------- */
    /** unsigned int git_checkout_options_get_version(const git_checkout_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** void git_checkout_options_set_version(const git_checkout_options *opts, unsigned int version); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);

    /** unsigned int git_checkout_options_get_strategy(const git_checkout_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetStrategy)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** void git_checkout_options_set_strategy(const git_checkout_options *opts, unsigned int strategy); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetStrategy)(JNIEnv *env, jclass obj, jlong optsPtr, jint strategy);

    /** int git_checkout_options_get_disable_filters(const git_checkout_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetDisableFilters)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** void git_checkout_options_set_disable_filters(const git_checkout_options *opts, int disalbe_filters); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetDisableFilters)(JNIEnv *env, jclass obj, jlong optsPtr, jint disalbe_filters);

    /** unsigned int git_checkout_options_get_dir_mode(const git_checkout_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetDirMode)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** void git_checkout_options_set_dir_mode(const git_checkout_options *opts, unsigned int mode); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetDirMode)(JNIEnv *env, jclass obj, jlong optsPtr, jint mode);

    /** unsigned int git_checkout_options_get_file_mode(const git_checkout_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetFileMode)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** void git_checkout_options_set_file_mode(const git_checkout_options *opts, unsigned int mode); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetFileMode)(JNIEnv *env, jclass obj, jlong optsPtr, jint mode);

    /** int git_checkout_options_get_file_open_flags(const git_checkout_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetFileOpenFlags)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** void git_checkout_options_set_file_open_flags(const git_checkout_options *opts, int flags); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetFileOpenFlags)(JNIEnv *env, jclass obj, jlong optsPtr, jint flags);

    /** unsigned int git_checkout_options_get_notify_flags(const git_checkout_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetNotifyFlags)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** void git_checkout_options_set_notify_flags(const git_checkout_options *opts, unsigned int flags); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetNotifyFlags)(JNIEnv *env, jclass obj, jlong optsPtr, jint flags);

    /** void git_checkout_options_set_paths(const git_checkout_options *opts, git_strarray paths); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetPaths)(JNIEnv *env, jclass obj, jlong optsPtr, jobjectArray paths);

    /** void git_checkout_options_get_paths(const git_checkout_options *opts, git_strarray paths); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetPaths)(JNIEnv *env, jclass obj, jlong optsPtr, jobject outPathsList);

    /** void git_checkout_options_get_baseline(const git_checkout_options *opts); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetBaseline)(JNIEnv *env, jclass obj, jlong optsPtr);
    /** void git_checkout_options_set_baseline(const git_checkout_options *opts, git_tree *baseline); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetBaseline)(JNIEnv *env, jclass obj, jlong optsPtr, jlong baselinePtr);

    /** void git_checkout_options_set_baseline_index(const git_checkout_options *opts); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetBaselineIndex)(JNIEnv *env, jclass obj, jlong optsPtr);
    /** void git_checkout_options_set_baseline_index(const git_checkout_options *opts, git_index *baseline_index); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetBaselineIndex)(JNIEnv *env, jclass obj, jlong optsPtr, jlong baselineIndexPtr);

    /** void git_checkout_options_set_target_directory(const git_checkout_options *opts, const char *target_directory); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetTargetDirectory)(JNIEnv *env, jclass obj, jlong optsPtr, jstring target_directory);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetTargetDirectory)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** void git_checkout_options_set_ancestor_label(const git_checkout_options *opts, const char *ancestor_label); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetAncestorLabel)(JNIEnv *env, jclass obj, jlong optsPtr, jstring ancestor_label);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetAncestorLabel)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** void git_checkout_options_set_our_label(const git_checkout_options *opts, const char *our_label); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetOurLabel)(JNIEnv *env, jclass obj, jlong optsPtr, jstring our_label);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetOurLabel)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** void git_checkout_options_set_their_lable(const git_checkout_options *opts, const char *their_label); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Checkout_jniOptionsSetTheirLable)(JNIEnv *env, jclass obj, jlong optsPtr, jstring their_label);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Checkout_jniOptionsGetTheirLable)(JNIEnv *env, jclass obj, jlong optsPtr);

#ifdef __cplusplus
}
#endif
#endif