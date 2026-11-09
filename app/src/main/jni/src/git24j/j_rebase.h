#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_REBASE_H__
#define __GIT24J_REBASE_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** -------- Signature of the header ---------- */
    /** int git_rebase_init(git_rebase **out, git_repository *repo, const git_annotated_commit *branch, const git_annotated_commit *upstream, const git_annotated_commit *onto, const git_rebase_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniInit)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong branchPtr, jlong upstreamPtr, jlong ontoPtr, jlong optsPtr);

    /** int git_rebase_open(git_rebase **out, git_repository *repo, const git_rebase_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOpen)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong optsPtr);

    /** size_t git_rebase_operation_entrycount(git_rebase *rebase); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOperationEntrycount)(JNIEnv *env, jclass obj, jlong rebasePtr);

    /** size_t git_rebase_operation_current(git_rebase *rebase); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOperationCurrent)(JNIEnv *env, jclass obj, jlong rebasePtr);

    /** git_rebase_operation * git_rebase_operation_byindex(git_rebase *rebase, size_t idx); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Rebase_jniOperationByindex)(JNIEnv *env, jclass obj, jlong rebasePtr, jint idx);

    /** int git_rebase_next(git_rebase_operation **operation, git_rebase *rebase); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniNext)(JNIEnv *env, jclass obj, jobject operation, jlong rebasePtr);

    /** int git_rebase_inmemory_index(git_index **index, git_rebase *rebase); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniInmemoryIndex)(JNIEnv *env, jclass obj, jobject index, jlong rebasePtr);

    /** int git_rebase_commit(git_oid *id, git_rebase *rebase, const git_signature *author, const git_signature *committer, const char *message_encoding, const char *message); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniCommit)(JNIEnv *env, jclass obj, jobject id, jlong rebasePtr, jlong authorPtr, jlong committerPtr, jstring message_encoding, jstring message);

    /** int git_rebase_abort(git_rebase *rebase); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniAbort)(JNIEnv *env, jclass obj, jlong rebasePtr);

    /** int git_rebase_finish(git_rebase *rebase, const git_signature *signature); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniFinish)(JNIEnv *env, jclass obj, jlong rebasePtr, jlong signaturePtr);

    /** void git_rebase_free(git_rebase *rebase); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniFree)(JNIEnv *env, jclass obj, jlong rebasePtr);

    /** const git_oid * git_rebase_onto_id(git_rebase *rebase); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Rebase_jniOntoId)(JNIEnv *env, jclass obj, jlong rebasePtr);

    /** const char * git_rebase_onto_name(git_rebase *rebase); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Rebase_jniOntoName)(JNIEnv *env, jclass obj, jlong rebasePtr);

    /** int git_rebase_options_init(git_rebase_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);

    /** const git_oid * git_rebase_orig_head_id(git_rebase *rebase); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Rebase_jniOrigHeadId)(JNIEnv *env, jclass obj, jlong rebasePtr);

    /** const char * git_rebase_orig_head_name(git_rebase *rebase); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Rebase_jniOrigHeadName)(JNIEnv *env, jclass obj, jlong rebasePtr);

    /** -------- git_rebase_operation ---------- */
    /** int type*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOperationGetType)(JNIEnv *env, jclass obj, jlong operationPtr);
    /** const git_oid id*/
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Rebase_jniOperationGetId)(JNIEnv *env, jclass obj, jlong operationPtr);
    /** const char *exec*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Rebase_jniOperationGetExec)(JNIEnv *env, jclass obj, jlong operationPtr);

    /** -------- git_rebase_options ---------- */
    /** int git_rebase_init_options(git_rebase_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    /** unsigned int version*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** int quiet*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetQuiet)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** int inmemory*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetInmemory)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** const char *rewrite_notes_ref*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetRewriteNotesRef)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** git_merge_options merge_options*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetMergeOptions)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** git_checkout_options checkout_options*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetCheckoutOptions)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** unsigned int version*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr, jint version);
    /** int quiet*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetQuiet)(JNIEnv *env, jclass obj, jlong optionsPtr, jint quiet);
    /** int inmemory*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetInmemory)(JNIEnv *env, jclass obj, jlong optionsPtr, jint inmemory);
    /** const char *rewrite_notes_ref*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetRewriteNotesRef)(JNIEnv *env, jclass obj, jlong optionsPtr, jstring rewriteNotesRef);
    /** git_commit_signing_cb signing_cb*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetSigningCb)(JNIEnv *env, jclass obj, jlong optionsPtr, jobject signingCb);

    JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetPayload)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong payload);

#ifdef __cplusplus
}
#endif
#endif