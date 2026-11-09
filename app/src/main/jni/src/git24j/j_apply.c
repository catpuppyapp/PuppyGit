#include "j_apply.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

int j_git_apply_delta_cb(const git_diff_delta *delta, void *payload)
{
    if (!payload)
    {
        return 0;
    }
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = getEnv();
    int r = (*env)->CallIntMethod(env, j_payload->callback, j_payload->mid, (jlong)delta);
    return r;
}

int j_git_apply_hunk_cb(const git_diff_hunk *hunk, void *payload)
{
    if (!payload)
    {
        return 0;
    }
    j_cb_payload *head = (j_cb_payload *)payload;
    j_cb_payload *j_payload = &(head[1]);
    JNIEnv *env = getEnv();
    int r = (*env)->CallIntMethod(env, j_payload->callback, j_payload->mid, (jlong)hunk);
    return r;
}

/** int git_apply_to_tree(git_index **out, git_repository *repo, git_tree *preimage, git_diff *diff, const git_apply_options *options); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Apply_jniToTree)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong preimagePtr, jlong diffPtr, jlong optionsPtr)
{
    git_index *c_out;
    int r = git_apply_to_tree(&c_out, (git_repository *)repoPtr, (git_tree *)preimagePtr, (git_diff *)diffPtr, (git_apply_options *)optionsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** int git_apply_apply(git_repository *repo, git_diff *diff, int location, const git_apply_options *options); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Apply_jniApply)(JNIEnv *env, jclass obj, jlong repoPtr, jlong diffPtr, jint location, jlong optionsPtr)
{
    int r = git_apply((git_repository *)repoPtr, (git_diff *)diffPtr, location, (git_apply_options *)optionsPtr);
    return r;
}

/** -------- apply options ---------- */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Apply_jniOptionsNew)(JNIEnv *env, jclass obj, jint version, jobject out, jobject deltaCb, jobject hunkCb)
{
    git_apply_options *opts = (git_apply_options *)malloc(sizeof(git_apply_options));
    int e = git_apply_options_init(opts, (unsigned int)version);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)opts);
    j_cb_payload *payload = (j_cb_payload *)malloc(sizeof(j_cb_payload) * 2);

    j_cb_payload_init(env, &payload[0], deltaCb, "(J)I");
    opts->delta_cb = j_git_apply_delta_cb;

    j_cb_payload_init(env, &payload[1], hunkCb, "(J)I");
    opts->hunk_cb = j_git_apply_hunk_cb;
    opts->payload = payload;
    return e;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Apply_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    git_apply_options *opts = (git_apply_options *)optsPtr;
    if (opts)
    {
        j_cb_payload *payload = opts->payload;
        j_cb_payload_release(env, &payload[0]);
        j_cb_payload_release(env, &payload[1]);
        free(opts->payload);
        opts->delta_cb = NULL;
        opts->hunk_cb = NULL;
    }
    free(opts);
}

/** unsigned int flags*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Apply_jniOptionsGetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_apply_options *)optionsPtr)->flags;
}

/** unsigned int flags*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Apply_jniOptionsSetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr, jint flags)
{
    ((git_apply_options *)optionsPtr)->flags = (unsigned int)flags;
}
