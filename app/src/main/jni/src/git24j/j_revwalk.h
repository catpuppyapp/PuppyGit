#include "j_common.h"
#include <jni.h>

#ifndef __GIT24J_REVWALK_H__
#define __GIT24J_REVWALK_H__
#ifdef __cplusplus
extern "C"
{
#endif
    // no matching type found for 'git_revwalk_hide_cb hide_cb'
    /** int git_revwalk_add_hide_cb(git_revwalk *walk, git_revwalk_hide_cb hide_cb, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniAddHideCb)(JNIEnv *env, jclass obj, jlong walkPtr, jobject hideCb, jobject outPayload);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Revwalk_jniFreeHideCb)(JNIEnv *env, jclass obj, jlong payloadPtr);
    /** -------- Signature of the header ---------- */
    /** void git_revwalk_free(git_revwalk *walk); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Revwalk_jniFree)(JNIEnv *env, jclass obj, jlong walkPtr);

    /** int git_revwalk_hide(git_revwalk *walk, const git_oid *commit_id); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniHide)(JNIEnv *env, jclass obj, jlong walkPtr, jobject commitId);

    /** int git_revwalk_hide_glob(git_revwalk *walk, const char *glob); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniHideGlob)(JNIEnv *env, jclass obj, jlong walkPtr, jstring glob);

    /** int git_revwalk_hide_head(git_revwalk *walk); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniHideHead)(JNIEnv *env, jclass obj, jlong walkPtr);

    /** int git_revwalk_hide_ref(git_revwalk *walk, const char *refname); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniHideRef)(JNIEnv *env, jclass obj, jlong walkPtr, jstring refname);

    /** int git_revwalk_new(git_revwalk **out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);

    /** int git_revwalk_next(git_oid *out, git_revwalk *walk); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniNext)(JNIEnv *env, jclass obj, jobject out, jlong walkPtr);

    /** int git_revwalk_push(git_revwalk *walk, const git_oid *id); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniPush)(JNIEnv *env, jclass obj, jlong walkPtr, jobject id);

    /** int git_revwalk_push_glob(git_revwalk *walk, const char *glob); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniPushGlob)(JNIEnv *env, jclass obj, jlong walkPtr, jstring glob);

    /** int git_revwalk_push_head(git_revwalk *walk); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniPushHead)(JNIEnv *env, jclass obj, jlong walkPtr);

    /** int git_revwalk_push_range(git_revwalk *walk, const char *range); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniPushRange)(JNIEnv *env, jclass obj, jlong walkPtr, jstring range);

    /** int git_revwalk_push_ref(git_revwalk *walk, const char *refname); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniPushRef)(JNIEnv *env, jclass obj, jlong walkPtr, jstring refname);

    /** git_repository * git_revwalk_repository(git_revwalk *walk); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Revwalk_jniRepository)(JNIEnv *env, jclass obj, jlong walkPtr);

    /** void git_revwalk_reset(git_revwalk *walker); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Revwalk_jniReset)(JNIEnv *env, jclass obj, jlong walkerPtr);

    /** void git_revwalk_simplify_first_parent(git_revwalk *walk); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Revwalk_jniSimplifyFirstParent)(JNIEnv *env, jclass obj, jlong walkPtr);

    /** void git_revwalk_sorting(git_revwalk *walk, unsigned int sort_mode); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Revwalk_jniSorting)(JNIEnv *env, jclass obj, jlong walkPtr, jint sortMode);

#ifdef __cplusplus
}
#endif
#endif