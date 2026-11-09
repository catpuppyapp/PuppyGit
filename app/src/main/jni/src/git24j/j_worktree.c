#include "j_worktree.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>

/** -------- Wrapper Body ---------- */
/** int git_worktree_add(git_worktree **out, git_repository *repo, const char *name, const char *path, const git_worktree_add_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniAdd)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name, jstring path, jlong optsPtr)
{
    git_worktree *c_out = 0;
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_worktree_add(&c_out, (git_repository *)repoPtr, c_name, c_path, (git_worktree_add_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_name);
    free(c_path);
    return r;
}

/** int git_worktree_add_init_options(git_worktree_add_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniAddInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_worktree_add_init_options((git_worktree_add_options *)optsPtr, version);
    return r;
}

/** void git_worktree_free(git_worktree *wt); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Worktree_jniFree)(JNIEnv *env, jclass obj, jlong wtPtr)
{
    git_worktree_free((git_worktree *)wtPtr);
}

/** int git_worktree_is_locked(git_buf *reason, const git_worktree *wt); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniIsLocked)(JNIEnv *env, jclass obj, jobject reason, jlong wtPtr)
{
    git_buf c_reason = {0};
    int r = git_worktree_is_locked(&c_reason, (git_worktree *)wtPtr);
    j_git_buf_to_java(env, &c_reason, reason);
    git_buf_dispose(&c_reason);
    return r;
}

/** int git_worktree_is_prunable(git_worktree *wt, git_worktree_prune_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniIsPrunable)(JNIEnv *env, jclass obj, jlong wtPtr, jlong optsPtr)
{
    int r = git_worktree_is_prunable((git_worktree *)wtPtr, (git_worktree_prune_options *)optsPtr);
    return r;
}

/** int git_worktree_list(git_strarray *out, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniList)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr)
{
    git_strarray *c_out = (git_strarray *)malloc(sizeof(git_strarray));
    int r = git_worktree_list(c_out, (git_repository *)repoPtr);
    j_strarray_to_java_list(env, c_out, out);
    git_strarray_free(c_out);
    free(c_out);
    return r;
}

/** int git_worktree_lock(git_worktree *wt, const char *reason); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniLock)(JNIEnv *env, jclass obj, jlong wtPtr, jstring reason)
{
    char *c_reason = j_copy_of_jstring(env, reason, true);
    int r = git_worktree_lock((git_worktree *)wtPtr, c_reason);
    free(c_reason);
    return r;
}

/** int git_worktree_lookup(git_worktree **out, git_repository *repo, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniLookup)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name)
{
    git_worktree *c_out = 0;
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_worktree_lookup(&c_out, (git_repository *)repoPtr, c_name);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_name);
    return r;
}

/** const char * git_worktree_name(const git_worktree *wt); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Worktree_jniName)(JNIEnv *env, jclass obj, jlong wtPtr)
{
    const char *r = git_worktree_name((git_worktree *)wtPtr);
    return (*env)->NewStringUTF(env, r);
}

/** int git_worktree_open_from_repository(git_worktree **out, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniOpenFromRepository)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr)
{
    git_worktree *c_out = 0;
    int r = git_worktree_open_from_repository(&c_out, (git_repository *)repoPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** const char * git_worktree_path(const git_worktree *wt); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Worktree_jniPath)(JNIEnv *env, jclass obj, jlong wtPtr)
{
    const char *r = git_worktree_path((git_worktree *)wtPtr);
    return (*env)->NewStringUTF(env, r);
}

/** int git_worktree_prune(git_worktree *wt, git_worktree_prune_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniPrune)(JNIEnv *env, jclass obj, jlong wtPtr, jlong optsPtr)
{
    int r = git_worktree_prune((git_worktree *)wtPtr, (git_worktree_prune_options *)optsPtr);
    return r;
}

/** int git_worktree_prune_init_options(git_worktree_prune_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniPruneInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_worktree_prune_options_init((git_worktree_prune_options *)optsPtr, version);
    // int r = git_worktree_prune_init_options((git_worktree_prune_options *)optsPtr, version);
    return r;
}

/** int git_worktree_unlock(git_worktree *wt); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniUnlock)(JNIEnv *env, jclass obj, jlong wtPtr)
{
    int r = git_worktree_unlock((git_worktree *)wtPtr);
    return r;
}

/** int git_worktree_validate(const git_worktree *wt); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Worktree_jniValidate)(JNIEnv *env, jclass obj, jlong wtPtr)
{
    int r = git_worktree_validate((git_worktree *)wtPtr);
    return r;
}
