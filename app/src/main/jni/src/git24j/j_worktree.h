#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_WORKTREE_H__
#define __GIT24J_WORKTREE_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** -------- Signature of the header ---------- */
    /** int git_worktree_add(git_worktree **out, git_repository *repo, const char *name, const char *path, const git_worktree_add_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniAdd)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name, jstring path, jlong optsPtr);

    /** int git_worktree_add_init_options(git_worktree_add_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniAddInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);

    /** void git_worktree_free(git_worktree *wt); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Worktree_jniFree)(JNIEnv *env, jclass obj, jlong wtPtr);

    /** int git_worktree_is_locked(git_buf *reason, const git_worktree *wt); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniIsLocked)(JNIEnv *env, jclass obj, jobject reason, jlong wtPtr);

    /** int git_worktree_is_prunable(git_worktree *wt, git_worktree_prune_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniIsPrunable)(JNIEnv *env, jclass obj, jlong wtPtr, jlong optsPtr);

    /** int git_worktree_list(git_strarray *out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniList)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);

    /** int git_worktree_lock(git_worktree *wt, const char *reason); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniLock)(JNIEnv *env, jclass obj, jlong wtPtr, jstring reason);

    /** int git_worktree_lookup(git_worktree **out, git_repository *repo, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniLookup)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name);

    /** const char * git_worktree_name(const git_worktree *wt); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Worktree_jniName)(JNIEnv *env, jclass obj, jlong wtPtr);

    /** int git_worktree_open_from_repository(git_worktree **out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniOpenFromRepository)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);

    /** const char * git_worktree_path(const git_worktree *wt); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Worktree_jniPath)(JNIEnv *env, jclass obj, jlong wtPtr);

    /** int git_worktree_prune(git_worktree *wt, git_worktree_prune_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniPrune)(JNIEnv *env, jclass obj, jlong wtPtr, jlong optsPtr);

    /** int git_worktree_prune_init_options(git_worktree_prune_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniPruneInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);

    /** int git_worktree_unlock(git_worktree *wt); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniUnlock)(JNIEnv *env, jclass obj, jlong wtPtr);

    /** int git_worktree_validate(const git_worktree *wt); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniValidate)(JNIEnv *env, jclass obj, jlong wtPtr);

#ifdef __cplusplus
}
#endif
#endif