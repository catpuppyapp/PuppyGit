#include "j_rebase.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

/** -------- Wrapper Body ---------- */

/** int git_rebase_init(git_rebase **out, git_repository *repo, const git_annotated_commit *branch, const git_annotated_commit *upstream, const git_annotated_commit *onto, const git_rebase_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniInit)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong branchPtr, jlong upstreamPtr, jlong ontoPtr, jlong optsPtr)
{
    git_rebase *c_out = 0;
    int r = git_rebase_init(&c_out, (git_repository *)repoPtr, (git_annotated_commit *)branchPtr, (git_annotated_commit *)upstreamPtr, (git_annotated_commit *)ontoPtr, (git_rebase_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** int git_rebase_open(git_rebase **out, git_repository *repo, const git_rebase_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOpen)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong optsPtr)
{
    git_rebase *c_out = 0;
    int r = git_rebase_open(&c_out, (git_repository *)repoPtr, (git_rebase_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** size_t git_rebase_operation_entrycount(git_rebase *rebase); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOperationEntrycount)(JNIEnv *env, jclass obj, jlong rebasePtr)
{
    size_t r = git_rebase_operation_entrycount((git_rebase *)rebasePtr);
    return r;
}

/** size_t git_rebase_operation_current(git_rebase *rebase); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOperationCurrent)(JNIEnv *env, jclass obj, jlong rebasePtr)
{
    size_t r = git_rebase_operation_current((git_rebase *)rebasePtr);
    return r;
}

/** git_rebase_operation * git_rebase_operation_byindex(git_rebase *rebase, size_t idx); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Rebase_jniOperationByindex)(JNIEnv *env, jclass obj, jlong rebasePtr, jint idx)
{
    git_rebase_operation *r = git_rebase_operation_byindex((git_rebase *)rebasePtr, idx);
    return (jlong)r;
}

/** int git_rebase_next(git_rebase_operation **operation, git_rebase *rebase); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniNext)(JNIEnv *env, jclass obj, jobject operation, jlong rebasePtr)
{
    git_rebase_operation *c_operation = 0;
    int r = git_rebase_next(&c_operation, (git_rebase *)rebasePtr);
    (*env)->CallVoidMethod(env, operation, jniConstants->midAtomicLongSet, (jlong)c_operation);
    return r;
}

/** int git_rebase_inmemory_index(git_index **index, git_rebase *rebase); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniInmemoryIndex)(JNIEnv *env, jclass obj, jobject index, jlong rebasePtr)
{
    git_index *c_index = 0;
    int r = git_rebase_inmemory_index(&c_index, (git_rebase *)rebasePtr);
    (*env)->CallVoidMethod(env, index, jniConstants->midAtomicLongSet, (jlong)c_index);
    return r;
}

/** int git_rebase_commit(git_oid *id, git_rebase *rebase, const git_signature *author, const git_signature *committer, const char *message_encoding, const char *message); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniCommit)(JNIEnv *env, jclass obj, jobject id, jlong rebasePtr, jlong authorPtr, jlong committerPtr, jstring message_encoding, jstring message)
{
    git_oid c_id;
    char *c_message_encoding = j_copy_of_jstring(env, message_encoding, true);
    char *c_message = j_copy_of_jstring(env, message, true);
    int r = git_rebase_commit(&c_id, (git_rebase *)rebasePtr, (git_signature *)authorPtr, (git_signature *)committerPtr, c_message_encoding, c_message);
    j_git_oid_to_java(env, &c_id, id);
    free(c_message_encoding);
    free(c_message);
    return r;
}

/** int git_rebase_abort(git_rebase *rebase); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniAbort)(JNIEnv *env, jclass obj, jlong rebasePtr)
{
    int r = git_rebase_abort((git_rebase *)rebasePtr);
    return r;
}

/** int git_rebase_finish(git_rebase *rebase, const git_signature *signature); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniFinish)(JNIEnv *env, jclass obj, jlong rebasePtr, jlong signaturePtr)
{
    int r = git_rebase_finish((git_rebase *)rebasePtr, (git_signature *)signaturePtr);
    return r;
}

/** void git_rebase_free(git_rebase *rebase); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniFree)(JNIEnv *env, jclass obj, jlong rebasePtr)
{
    git_rebase_free((git_rebase *)rebasePtr);
}

/** const git_oid * git_rebase_onto_id(git_rebase *rebase); */
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Rebase_jniOntoId)(JNIEnv *env, jclass obj, jlong rebasePtr)
{
    const git_oid *r = git_rebase_onto_id((git_rebase *)rebasePtr);
    return j_git_oid_to_bytearray(env, r);
}

/** const char * git_rebase_onto_name(git_rebase *rebase); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Rebase_jniOntoName)(JNIEnv *env, jclass obj, jlong rebasePtr)
{
    const char *r = git_rebase_onto_name((git_rebase *)rebasePtr);
    return (*env)->NewStringUTF(env, r);
}

/** int git_rebase_options_init(git_rebase_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_rebase_options_init((git_rebase_options *)optsPtr, version);
    return r;
}

/** const git_oid * git_rebase_orig_head_id(git_rebase *rebase); */
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Rebase_jniOrigHeadId)(JNIEnv *env, jclass obj, jlong rebasePtr)
{
    const git_oid *r = git_rebase_orig_head_id((git_rebase *)rebasePtr);
    return j_git_oid_to_bytearray(env, r);
}

/** const char * git_rebase_orig_head_name(git_rebase *rebase); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Rebase_jniOrigHeadName)(JNIEnv *env, jclass obj, jlong rebasePtr)
{
    const char *r = git_rebase_orig_head_name((git_rebase *)rebasePtr);
    return (*env)->NewStringUTF(env, r);
}

/** -------- git_rebase_operation ---------- */
/** int type*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOperationGetType)(JNIEnv *env, jclass obj, jlong operationPtr)
{
    return ((git_rebase_operation *)operationPtr)->type;
}

/** const git_oid id*/
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Rebase_jniOperationGetId)(JNIEnv *env, jclass obj, jlong operationPtr)
{
    return j_git_oid_to_bytearray(env, &(((git_rebase_operation *)operationPtr)->id));
}

/** const char *exec*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Rebase_jniOperationGetExec)(JNIEnv *env, jclass obj, jlong operationPtr)
{
    const char *exec = ((git_rebase_operation *)operationPtr)->exec;
    return (*env)->NewStringUTF(env, exec);
}

/** -------- git_rebase_options ---------- */
/** int git_rebase_init_options(git_rebase_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_rebase_init_options((git_rebase_options *)optsPtr, version);
    return r;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version)
{
    git_rebase_options *opts = (git_rebase_options *)malloc(sizeof(git_rebase_options));
    int r = git_rebase_init_options(opts, version);
    opts->rewrite_notes_ref = NULL;
    (*env)->CallVoidMethod(env, outOpts, jniConstants->midAtomicLongSet, (jlong)opts);
    return r;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    git_rebase_options *opts = (git_rebase_options *)optsPtr;
    opts->signing_cb = NULL;
    j_cb_payload_release(env, opts->payload);
    free(opts->payload);
    free((void *)opts->rewrite_notes_ref);
    free(opts);
}

/** unsigned int version*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_rebase_options *)optionsPtr)->version;
}

/** int quiet*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetQuiet)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_rebase_options *)optionsPtr)->quiet;
}

/** int inmemory*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetInmemory)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_rebase_options *)optionsPtr)->inmemory;
}

/** const char *rewrite_notes_ref*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetRewriteNotesRef)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    const char *res = ((git_rebase_options *)optionsPtr)->rewrite_notes_ref;
    return (*env)->NewStringUTF(env, res);
}

/** git_merge_options merge_options*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetMergeOptions)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return (jlong) & (((git_rebase_options *)optionsPtr)->merge_options);
}

/** git_checkout_options checkout_options*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Rebase_jniOptionsGetCheckoutOptions)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    git_checkout_options *r = &((git_rebase_options *)optionsPtr)->checkout_options;
    return (jlong)r;
}

/** unsigned int version*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr, jint version)
{
    ((git_rebase_options *)optionsPtr)->version = (unsigned int)version;
}

/** int quiet*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetQuiet)(JNIEnv *env, jclass obj, jlong optionsPtr, jint quiet)
{
    ((git_rebase_options *)optionsPtr)->quiet = (int)quiet;
}

/** int inmemory*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetInmemory)(JNIEnv *env, jclass obj, jlong optionsPtr, jint inmemory)
{
    ((git_rebase_options *)optionsPtr)->inmemory = (int)inmemory;
}

/** const char *rewrite_notes_ref*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetRewriteNotesRef)(JNIEnv *env, jclass obj, jlong optionsPtr, jstring rewriteNotesRef)
{
    char *ref = j_copy_of_jstring(env, rewriteNotesRef, true);
    ((git_rebase_options *)optionsPtr)->rewrite_notes_ref = ref;
}

int j_rebase_git_commit_signing_cb(git_buf *signature, git_buf *signature_field, const char *commit_content, void *payload)
{
    assert(payload && "git_rebase_options.siging_cb must be called with payload");
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = getEnv();
    jstring jSig = j_git_buf_to_jstring(env, signature);
    jstring jSigField = j_git_buf_to_jstring(env, signature_field);
    jstring commitContent = (*env)->NewStringUTF(env, commit_content);
    int r = (*env)->CallIntMethod(env, j_payload->callback, j_payload->mid, jSig, jSigField, commitContent);
    if (jSig)
    {
        (*env)->DeleteLocalRef(env, jSig);
    }
    if (jSigField)
    {
        (*env)->DeleteLocalRef(env, jSigField);
    }
    if (commitContent)
    {
        (*env)->DeleteLocalRef(env, commitContent);
    }
    return r;
}

/** git_commit_signing_cb signing_cb*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetSigningCb)(JNIEnv *env, jclass obj, jlong optionsPtr, jobject signingCb)
{
    git_rebase_options *opts = (git_rebase_options *)optionsPtr;
    j_cb_payload *payload = (j_cb_payload *)malloc(sizeof(j_cb_payload));
    j_cb_payload_init(env, payload, signingCb, "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I");
    opts->payload = payload;
    opts->signing_cb = j_rebase_git_commit_signing_cb;
}

/** void *payload*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Rebase_jniOptionsSetPayload)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong payload)
{
    /**FIXME: callback and payload needs human review*/
    ((git_rebase_options *)optionsPtr)->payload = (void *)payload;
}
