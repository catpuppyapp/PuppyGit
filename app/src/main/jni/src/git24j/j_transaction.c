#include "j_transaction.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** -------- Wrapper Body ---------- */
/** int git_transaction_new(git_transaction **out, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr)
{
    git_transaction *c_out = 0;
    int r = git_transaction_new(&c_out, (git_repository *)repoPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_transaction_free(c_out); */
    return r;
}

/** int git_transaction_lock_ref(git_transaction *tx, const char *refname); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniLockRef)(JNIEnv *env, jclass obj, jlong txPtr, jstring refname)
{
    char *c_refname = j_copy_of_jstring(env, refname, true);
    int r = git_transaction_lock_ref((git_transaction *)txPtr, c_refname);
    free(c_refname);
    return r;
}

/** int git_transaction_set_target(git_transaction *tx, const char *refname, const git_oid *target, const git_signature *sig, const char *msg); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniSetTarget)(JNIEnv *env, jclass obj, jlong txPtr, jstring refname, jobject target, jlong sigPtr, jstring msg)
{
    char *c_refname = j_copy_of_jstring(env, refname, true);
    git_oid c_target;
    j_git_oid_from_java(env, target, &c_target);
    char *c_msg = j_copy_of_jstring(env, msg, true);
    int r = git_transaction_set_target((git_transaction *)txPtr, c_refname, &c_target, (git_signature *)sigPtr, c_msg);
    free(c_refname);
    free(c_msg);
    return r;
}

/** int git_transaction_set_symbolic_target(git_transaction *tx, const char *refname, const char *target, const git_signature *sig, const char *msg); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniSetSymbolicTarget)(JNIEnv *env, jclass obj, jlong txPtr, jstring refname, jstring target, jlong sigPtr, jstring msg)
{
    char *c_refname = j_copy_of_jstring(env, refname, true);
    char *c_target = j_copy_of_jstring(env, target, true);
    char *c_msg = j_copy_of_jstring(env, msg, true);
    int r = git_transaction_set_symbolic_target((git_transaction *)txPtr, c_refname, c_target, (git_signature *)sigPtr, c_msg);
    free(c_refname);
    free(c_target);
    free(c_msg);
    return r;
}

/** int git_transaction_set_reflog(git_transaction *tx, const char *refname, const git_reflog *reflog); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniSetReflog)(JNIEnv *env, jclass obj, jlong txPtr, jstring refname, jlong reflogPtr)
{
    char *c_refname = j_copy_of_jstring(env, refname, true);
    int r = git_transaction_set_reflog((git_transaction *)txPtr, c_refname, (git_reflog *)reflogPtr);
    free(c_refname);
    return r;
}

/** int git_transaction_remove(git_transaction *tx, const char *refname); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniRemove)(JNIEnv *env, jclass obj, jlong txPtr, jstring refname)
{
    char *c_refname = j_copy_of_jstring(env, refname, true);
    int r = git_transaction_remove((git_transaction *)txPtr, c_refname);
    free(c_refname);
    return r;
}

/** int git_transaction_commit(git_transaction *tx); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Transaction_jniCommit)(JNIEnv *env, jclass obj, jlong txPtr)
{
    int r = git_transaction_commit((git_transaction *)txPtr);
    return r;
}

/** void git_transaction_free(git_transaction *tx); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Transaction_jniFree)(JNIEnv *env, jclass obj, jlong txPtr)
{
    git_transaction_free((git_transaction *)txPtr);
}
