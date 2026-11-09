#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_TRANSACTION_H__
#define __GIT24J_TRANSACTION_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** -------- Signature of the header ---------- */
    /** int git_transaction_new(git_transaction **out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);

    /** int git_transaction_lock_ref(git_transaction *tx, const char *refname); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniLockRef)(JNIEnv *env, jclass obj, jlong txPtr, jstring refname);

    /** int git_transaction_set_target(git_transaction *tx, const char *refname, const git_oid *target, const git_signature *sig, const char *msg); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniSetTarget)(JNIEnv *env, jclass obj, jlong txPtr, jstring refname, jobject target, jlong sigPtr, jstring msg);

    /** int git_transaction_set_symbolic_target(git_transaction *tx, const char *refname, const char *target, const git_signature *sig, const char *msg); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniSetSymbolicTarget)(JNIEnv *env, jclass obj, jlong txPtr, jstring refname, jstring target, jlong sigPtr, jstring msg);

    /** int git_transaction_set_reflog(git_transaction *tx, const char *refname, const git_reflog *reflog); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniSetReflog)(JNIEnv *env, jclass obj, jlong txPtr, jstring refname, jlong reflogPtr);

    /** int git_transaction_remove(git_transaction *tx, const char *refname); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniRemove)(JNIEnv *env, jclass obj, jlong txPtr, jstring refname);

    /** int git_transaction_commit(git_transaction *tx); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniCommit)(JNIEnv *env, jclass obj, jlong txPtr);

    /** void git_transaction_free(git_transaction *tx); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Transaction_jniFree)(JNIEnv *env, jclass obj, jlong txPtr);

#ifdef __cplusplus
}
#endif
#endif