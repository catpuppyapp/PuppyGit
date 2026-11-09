#include "j_stash.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

int j_git_stash_apply_progress_cb(git_stash_apply_progress_t progress, void *payload)
{
    assert(payload && " callback function must be called with payload");
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = getEnv();
    int r = (*env)->CallIntMethod(env, j_payload->callback, j_payload->mid, (jint)progress);
    return r;
}

int j_git_stash_cb(size_t index, const char *message, const git_oid *stash_id, void *payload)
{
    assert(payload && " callback function must be called with payload");
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = getEnv();
    jstring jMessage = (*env)->NewStringUTF(env, message);
    jbyteArray stashIdBytes = j_git_oid_to_bytearray(env, stash_id);
    int r = (*env)->CallIntMethod(env, j_payload->callback, j_payload->mid, (jint)index, jMessage, stashIdBytes);
    (*env)->DeleteLocalRef(env, jMessage);
    (*env)->DeleteLocalRef(env, stashIdBytes);
    return r;
}

/** int git_stash_save(git_oid *out, git_repository *repo, const git_signature *stasher, const char *message, uint32_t flags); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniSave)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong stasherPtr, jstring message, jint flags)
{
    git_oid c_out;
    char *c_message = j_copy_of_jstring(env, message, true);
    int r = git_stash_save(&c_out, (git_repository *)repoPtr, (git_signature *)stasherPtr, c_message, flags);
    j_git_oid_to_java(env, &c_out, out);
    free(c_message);
    return r;
}

/** int git_stash_apply_options_init(git_stash_apply_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_stash_apply_options_init((git_stash_apply_options *)optsPtr, version);
    return r;
}

/** int git_stash_apply(git_repository *repo, size_t index, const git_stash_apply_options *options); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniApply)(JNIEnv *env, jclass obj, jlong repoPtr, jint index, jlong optionsPtr)
{
    int r = git_stash_apply((git_repository *)repoPtr, index, (git_stash_apply_options *)optionsPtr);
    return r;
}

/** int git_stash_foreach(git_repository *repo, git_stash_cb callback, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jobject callback)
{
    assert(callback && "foreach with null callback does not make any sense");
    j_cb_payload payload = {0};
    j_cb_payload_init(env, &payload, callback, "(ILjava/lang/String;[B)I");
    int r = git_stash_foreach((git_repository *)repoPtr, j_git_stash_cb, &payload);
    j_cb_payload_release(env, &payload);
    return r;
}

/** int git_stash_drop(git_repository *repo, size_t index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniDrop)(JNIEnv *env, jclass obj, jlong repoPtr, jint index)
{
    int r = git_stash_drop((git_repository *)repoPtr, index);
    return r;
}

/** int git_stash_pop(git_repository *repo, size_t index, const git_stash_apply_options *options); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniPop)(JNIEnv *env, jclass obj, jlong repoPtr, jint index, jlong optionsPtr)
{
    int r = git_stash_pop((git_repository *)repoPtr, index, (git_stash_apply_options *)optionsPtr);
    return r;
}

/** -------- git_stash_apply_options ---------- */
/** git_stash_apply_options */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version)
{
    git_stash_apply_options *opts = (git_stash_apply_options *)malloc(sizeof(git_stash_apply_options));
    opts->progress_payload = NULL;
    int e = git_stash_apply_options_init(opts, version);
    (*env)->CallVoidMethod(env, outPtr, jniConstants->midAtomicLongSet, (jlong)opts);
    return e;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsFree)(JNIEnv *env, jclass obj, jlong applyOptionsPtr)
{
    git_stash_apply_options *opts = (git_stash_apply_options *)applyOptionsPtr;
    if (opts && opts->progress_payload)
    {
        j_cb_payload *payload = (j_cb_payload *)opts->progress_payload;
        j_cb_payload_release(env, payload);
    }
    opts->progress_cb = NULL;
    free(opts->progress_payload);
    free(opts);
}

/** uint32_t flags*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsGetFlags)(JNIEnv *env, jclass obj, jlong applyOptionsPtr)
{
    return ((git_stash_apply_options *)applyOptionsPtr)->flags;
}

/** git_checkout_options checkout_options*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsGetCheckoutOptions)(JNIEnv *env, jclass obj, jlong applyOptionsPtr)
{
    git_checkout_options *r = &((git_stash_apply_options *)applyOptionsPtr)->checkout_options;
    return (jlong)r;
}

/** uint32_t flags*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsSetFlags)(JNIEnv *env, jclass obj, jlong applyOptionsPtr, jint flags)
{
    ((git_stash_apply_options *)applyOptionsPtr)->flags = (uint32_t)flags;
}

/** git_stash_apply_progress_cb progress_cb*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Stash_jniApplyOptionsSetProgressCb)(JNIEnv *env, jclass obj, jlong applyOptionsPtr, jobject progressCb)
{
    git_stash_apply_options *opts = (git_stash_apply_options *)applyOptionsPtr;
    j_cb_payload *old_payload = (j_cb_payload *)opts->progress_payload;
    if (old_payload)
    {
        j_cb_payload_release(env, old_payload);
        free(old_payload);
    }
    j_cb_payload *payload = (j_cb_payload *)malloc(sizeof(j_cb_payload));
    j_cb_payload_init(env, payload, progressCb, "(I)I");
    opts->progress_cb = j_git_stash_apply_progress_cb;
    opts->progress_payload = payload;
}
