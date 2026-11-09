#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_PATHSPEC_H__
#define __GIT24J_PATHSPEC_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_pathspec_new(git_pathspec **out, const git_strarray *pathspec); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniNew)(JNIEnv *env, jclass obj, jobject out, jobjectArray pathspec);

    /** void git_pathspec_free(git_pathspec *ps); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Pathspec_jniFree)(JNIEnv *env, jclass obj, jlong psPtr);

    /** int git_pathspec_matches_path(const git_pathspec *ps, uint32_t flags, const char *path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchesPath)(JNIEnv *env, jclass obj, jlong psPtr, jint flags, jstring path);

    /** int git_pathspec_match_workdir(git_pathspec_match_list **out, git_repository *repo, uint32_t flags, git_pathspec *ps); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchWorkdir)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jint flags, jlong psPtr);

    /** int git_pathspec_match_index(git_pathspec_match_list **out, git_index *index, uint32_t flags, git_pathspec *ps); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchIndex)(JNIEnv *env, jclass obj, jobject out, jlong indexPtr, jint flags, jlong psPtr);

    /** int git_pathspec_match_tree(git_pathspec_match_list **out, git_tree *tree, uint32_t flags, git_pathspec *ps); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchTree)(JNIEnv *env, jclass obj, jobject out, jlong treePtr, jint flags, jlong psPtr);

    /** int git_pathspec_match_diff(git_pathspec_match_list **out, git_diff *diff, uint32_t flags, git_pathspec *ps); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchDiff)(JNIEnv *env, jclass obj, jobject out, jlong diffPtr, jint flags, jlong psPtr);

    /** void git_pathspec_match_list_free(git_pathspec_match_list *m); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Pathspec_jniMatchListFree)(JNIEnv *env, jclass obj, jlong mPtr);

    /** size_t git_pathspec_match_list_entrycount(const git_pathspec_match_list *m); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchListEntrycount)(JNIEnv *env, jclass obj, jlong mPtr);

    /** const char * git_pathspec_match_list_entry(const git_pathspec_match_list *m, size_t pos); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Pathspec_jniMatchListEntry)(JNIEnv *env, jclass obj, jlong mPtr, jint pos);

    /** const git_diff_delta * git_pathspec_match_list_diff_entry(const git_pathspec_match_list *m, size_t pos); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Pathspec_jniMatchListDiffEntry)(JNIEnv *env, jclass obj, jlong mPtr, jint pos);

    /** size_t git_pathspec_match_list_failed_entrycount(const git_pathspec_match_list *m); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Pathspec_jniMatchListFailedEntrycount)(JNIEnv *env, jclass obj, jlong mPtr);

    /** const char * git_pathspec_match_list_failed_entry(const git_pathspec_match_list *m, size_t pos); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Pathspec_jniMatchListFailedEntry)(JNIEnv *env, jclass obj, jlong mPtr, jint pos);

#ifdef __cplusplus
}
#endif
#endif