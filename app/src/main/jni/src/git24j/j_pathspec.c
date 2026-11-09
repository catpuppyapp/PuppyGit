#include "j_pathspec.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** -------- Wrapper Body ---------- */
/** int git_pathspec_new(git_pathspec **out, const git_strarray *pathspec); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniNew)(JNIEnv *env, jclass obj, jobject out, jobjectArray pathspec)
{
    git_pathspec *c_out;
    git_strarray c_pathspec;
    git_strarray_of_jobject_array(env, pathspec, &c_pathspec);
    int r = git_pathspec_new(&c_out, &c_pathspec);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_pathspec_free(c_out); */
    git_strarray_free(&c_pathspec);
    return r;
}

/** void git_pathspec_free(git_pathspec *ps); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Pathspec_jniFree)(JNIEnv *env, jclass obj, jlong psPtr)
{
    git_pathspec_free((git_pathspec *)psPtr);
}

/** int git_pathspec_matches_path(const git_pathspec *ps, uint32_t flags, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchesPath)(JNIEnv *env, jclass obj, jlong psPtr, jint flags, jstring path)
{
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_pathspec_matches_path((git_pathspec *)psPtr, flags, c_path);
    free(c_path);
    return r;
}

/** int git_pathspec_match_workdir(git_pathspec_match_list **out, git_repository *repo, uint32_t flags, git_pathspec *ps); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchWorkdir)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jint flags, jlong psPtr)
{
    git_pathspec_match_list *c_out;
    int r = git_pathspec_match_workdir(&c_out, (git_repository *)repoPtr, flags, (git_pathspec *)psPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_pathspec_match_list_free(c_out); */
    return r;
}

/** int git_pathspec_match_index(git_pathspec_match_list **out, git_index *index, uint32_t flags, git_pathspec *ps); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchIndex)(JNIEnv *env, jclass obj, jobject out, jlong indexPtr, jint flags, jlong psPtr)
{
    git_pathspec_match_list *c_out;
    int r = git_pathspec_match_index(&c_out, (git_index *)indexPtr, flags, (git_pathspec *)psPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_pathspec_match_list_free(c_out); */
    return r;
}

/** int git_pathspec_match_tree(git_pathspec_match_list **out, git_tree *tree, uint32_t flags, git_pathspec *ps); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchTree)(JNIEnv *env, jclass obj, jobject out, jlong treePtr, jint flags, jlong psPtr)
{
    git_pathspec_match_list *c_out;
    int r = git_pathspec_match_tree(&c_out, (git_tree *)treePtr, flags, (git_pathspec *)psPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_pathspec_match_list_free(c_out); */
    return r;
}

/** int git_pathspec_match_diff(git_pathspec_match_list **out, git_diff *diff, uint32_t flags, git_pathspec *ps); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchDiff)(JNIEnv *env, jclass obj, jobject out, jlong diffPtr, jint flags, jlong psPtr)
{
    git_pathspec_match_list *c_out;
    int r = git_pathspec_match_diff(&c_out, (git_diff *)diffPtr, flags, (git_pathspec *)psPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_pathspec_match_list_free(c_out); */
    return r;
}

/** void git_pathspec_match_list_free(git_pathspec_match_list *m); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Pathspec_jniMatchListFree)(JNIEnv *env, jclass obj, jlong mPtr)
{
    git_pathspec_match_list_free((git_pathspec_match_list *)mPtr);
}

/** size_t git_pathspec_match_list_entrycount(const git_pathspec_match_list *m); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchListEntrycount)(JNIEnv *env, jclass obj, jlong mPtr)
{
    size_t r = git_pathspec_match_list_entrycount((git_pathspec_match_list *)mPtr);
    return r;
}

/** const char * git_pathspec_match_list_entry(const git_pathspec_match_list *m, size_t pos); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Pathspec_jniMatchListEntry)(JNIEnv *env, jclass obj, jlong mPtr, jint pos)
{
    const char *r = git_pathspec_match_list_entry((git_pathspec_match_list *)mPtr, pos);
    return (*env)->NewStringUTF(env, r);
}

/** const git_diff_delta * git_pathspec_match_list_diff_entry(const git_pathspec_match_list *m, size_t pos); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Pathspec_jniMatchListDiffEntry)(JNIEnv *env, jclass obj, jlong mPtr, jint pos)
{
    const git_diff_delta *r = git_pathspec_match_list_diff_entry((git_pathspec_match_list *)mPtr, pos);
    return (jlong)r;
}

/** size_t git_pathspec_match_list_failed_entrycount(const git_pathspec_match_list *m); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchListFailedEntrycount)(JNIEnv *env, jclass obj, jlong mPtr)
{
    size_t r = git_pathspec_match_list_failed_entrycount((git_pathspec_match_list *)mPtr);
    return r;
}

/** const char * git_pathspec_match_list_failed_entry(const git_pathspec_match_list *m, size_t pos); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Pathspec_jniMatchListFailedEntry)(JNIEnv *env, jclass obj, jlong mPtr, jint pos)
{
    const char *r = git_pathspec_match_list_failed_entry((git_pathspec_match_list *)mPtr, pos);
    return (*env)->NewStringUTF(env, r);
}
