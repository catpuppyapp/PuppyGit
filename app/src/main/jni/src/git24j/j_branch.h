#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_BRANCH_H__
#define __GIT24J_BRANCH_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_branch_create(git_reference **out, git_repository *repo, const char *branch_name, const git_commit *target, int force); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniCreate)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring branchName, jlong targetPtr, jint force);
    /**int git_branch_create_from_annotated(git_reference **ref_out, git_repository *repository, const char *branch_name, const git_annotated_commit *commit, int force); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniCreateFromAnnotated)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring branchName, jlong annoCommitPtr, jint force);
    /**int git_branch_delete(git_reference *branch); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniDelete)(JNIEnv *env, jclass obj, jlong refPtr);
    /**int git_branch_iterator_new(git_branch_iterator **out, git_repository *repo, git_branch_t list_flags); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniIteratorNew)(JNIEnv *env, jclass obj, jobject outBranchIter, jlong repoPtr, jint listFlags);
    /**int git_branch_next(git_reference **out, git_branch_t *out_type, git_branch_iterator *iter); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniNext)(JNIEnv *env, jclass obj, jobject outRef, jobject outType, jlong branchIterPtr);
    /**void git_branch_iterator_free(git_branch_iterator *iter); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Branch_jniIteratorFree)(JNIEnv *env, jclass obj, jlong branchIterPtr);
    /**int git_branch_move(git_reference **out, git_reference *branch, const char *new_branch_name, int force); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniMove)(JNIEnv *env, jclass obj, jobject outRef, jlong branchPtr, jstring branchName, jint force);
    /**int git_branch_lookup(git_reference **out, git_repository *repo, const char *branch_name, git_branch_t branch_type); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniLookup)(JNIEnv *env, jclass obj, jobject outRef, jlong repoPtr, jstring branchName, jint branchType);
    /**int git_branch_name(const char **out, const git_reference *ref); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniName)(JNIEnv *env, jclass obj, jobject outStr, jlong refPtr);
    /**int git_branch_upstream(git_reference **out, const git_reference *branch); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniUpstream)(JNIEnv *env, jclass obj, jobject outRef, jlong branchPtr);
    /**int git_branch_set_upstream(git_reference *branch, const char *upstream_name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniSetUpstream)(JNIEnv *env, jclass obj, jlong refPtr, jstring upstreamName);
    /**int git_branch_upstream_name(git_buf *out, git_repository *repo, const char *refname); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniUpstreamName)(JNIEnv *env, jclass obj, jobject outBuf, jlong repoPtr, jstring refName);
    /**int git_branch_is_head(const git_reference *branch); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniIsHead)(JNIEnv *env, jclass obj, jlong refPtr);
    /**int git_branch_is_checked_out(const git_reference *branch); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniIsCheckedOut)(JNIEnv *env, jclass obj, jlong rePftr);
    /**int git_branch_remote_name(git_buf *out, git_repository *repo, const char *canonical_branch_name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniRemoteName)(JNIEnv *env, jclass obj, jobject outBuf, jlong repoPtr, jstring canonicalBranchName);
    /**int git_branch_upstream_remote(git_buf *buf, git_repository *repo, const char *refname); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Branch_jniUpstreamRemote)(JNIEnv *env, jclass obj, jobject outBuf, jlong repoPtr, jstring refName);

#ifdef __cplusplus
}
#endif
#endif