#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_ANNOTATED_COMMIT_H__
#define __GIT24J_ANNOTATED_COMMIT_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_annotated_commit_from_ref(git_annotated_commit **out, git_repository *repo, const git_reference *ref); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(AnnotatedCommit_jniFromRef)(JNIEnv *env, jclass obj, jobject outAc, jlong repoPtr, jlong refPtr);
    /** int git_annotated_commit_from_fetchhead(git_annotated_commit **out, git_repository *repo, const char *branch_name, const char *remote_url, const git_oid *id); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(AnnotatedCommit_jniFromFetchHead)(JNIEnv *env, jclass obj, jobject outAc, jlong repoPtr, jstring branchName, jstring remoteUrl, jobject oid);
    /** int git_annotated_commit_lookup(git_annotated_commit **out, git_repository *repo, const git_oid *id); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(AnnotatedCommit_jniLookup)(JNIEnv *env, jclass obj, jobject outAc, jlong repoPtr, jobject oid);
    /** int git_annotated_commit_from_revspec(git_annotated_commit **out, git_repository *repo, const char *revspec); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(AnnotatedCommit_jniFromRevspec)(JNIEnv *env, jclass obj, jobject outAc, jlong repoPtr, jstring revspec);
    /** const git_oid * git_annotated_commit_id(const git_annotated_commit *commit); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(AnnotatedCommit_jniId)(JNIEnv *env, jclass obj, jlong acPtr);
    /** const char * git_annotated_commit_ref(const git_annotated_commit *commit); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(AnnotatedCommit_jniRef)(JNIEnv *env, jclass obj, jlong acPtr);
    /** void git_annotated_commit_free(git_annotated_commit *commit); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(AnnotatedCommit_jniFree)(JNIEnv *env, jclass obj, jlong acPtr);

#ifdef __cplusplus
}
#endif
#endif
