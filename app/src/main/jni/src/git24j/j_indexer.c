#include "j_indexer.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** int git_indexer_options_init(git_indexer_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_indexer_options_init((git_indexer_options *)optsPtr, version);
    return r;
}

/** int git_indexer_new(git_indexer **out, const char *path, unsigned int mode, git_odb *odb, git_indexer_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniNew)(JNIEnv *env, jclass obj, jobject out, jstring path, jint mode, jlong odbPtr, jlong optsPtr)
{
    git_indexer *c_out;
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_indexer_new(&c_out, c_path, mode, (git_odb *)odbPtr, (git_indexer_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_indexer_free(c_out); */
    free(c_path);
    return r;
}

/** int git_indexer_append(git_indexer *idx, const void *data, size_t size, git_indexer_progress *stats); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniAppend)(JNIEnv *env, jclass obj, jlong idxPtr, jbyteArray data, jint size, jlong statsPtr)
{
    int data_len;
    unsigned char *c_data = j_unsigned_chars_from_java(env, data, &data_len);
    int r = git_indexer_append((git_indexer *)idxPtr, (void *)c_data, size, (git_indexer_progress *)statsPtr);
    (*env)->ReleaseByteArrayElements(env, data, (jbyte *)c_data, 0);
    return r;
}

/** int git_indexer_commit(git_indexer *idx, git_indexer_progress *stats); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniCommit)(JNIEnv *env, jclass obj, jlong idxPtr, jlong statsPtr)
{
    int r = git_indexer_commit((git_indexer *)idxPtr, (git_indexer_progress *)statsPtr);
    return r;
}

/** const git_oid * git_indexer_hash(const git_indexer *idx); */
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Indexer_jniHash)(JNIEnv *env, jclass obj, jlong idxPtr)
{
    const git_oid *r = git_indexer_hash((git_indexer *)idxPtr);
    return j_git_oid_to_bytearray(env, r);
}

/** void git_indexer_free(git_indexer *idx); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Indexer_jniFree)(JNIEnv *env, jclass obj, jlong idxPtr)
{
    git_indexer_free((git_indexer *)idxPtr);
}

/** -------- Wrapper Body ---------- */
int j_git_indexer_progress_cb(const git_indexer_progress *stats, void *payload)
{
    if (!payload)
    {
        return 0;
    }
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = getEnv();
    int r = (*env)->CallIntMethod(env, j_payload->callback, j_payload->mid, (jlong)stats);
    return r;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version, jobject progressCb)
{
    git_indexer_options *opts = (git_indexer_options *)malloc(sizeof(git_indexer_options));
    j_cb_payload *payload = (j_cb_payload *)malloc(sizeof(j_cb_payload));
    j_cb_payload_init(env, payload, progressCb, "(J)I");
    int r = git_indexer_options_init(opts, version);
    opts->progress_cb = j_git_indexer_progress_cb;
    opts->progress_cb_payload = (void *)payload;
    (*env)->CallVoidMethod(env, outOpts, jniConstants->midAtomicLongSet, (jlong)opts);
    return r;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Indexer_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    git_indexer_options *opts = (git_indexer_options *)optsPtr;
    j_cb_payload *payload = (j_cb_payload *)opts->progress_cb_payload;
    j_cb_payload_release(env, payload);
    free(payload);
    opts->progress_cb = NULL;
    free(opts);
}

/** unsigned int version*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_indexer_options *)optionsPtr)->version;
}

/** unsigned char verify*/
JNIEXPORT jbyte JNICALL J_MAKE_METHOD(Indexer_jniOptionsGetVerify)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_indexer_options *)optionsPtr)->verify;
}

/** unsigned char verify*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Indexer_jniOptionsSetVerify)(JNIEnv *env, jclass obj, jlong optionsPtr, jbyte verify)
{
    ((git_indexer_options *)optionsPtr)->verify = (unsigned char)verify;
}

/** -------- Wrapper Body ---------- */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Indexer_jniProgressNew)(JNIEnv *env, jclass obj)
{
    git_indexer_progress *ptr = (git_indexer_progress *)malloc(sizeof(git_indexer_progress));
    return (jlong)ptr;
}
/** unsigned int total_objects*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetTotalObjects)(JNIEnv *env, jclass obj, jlong progressPtr)
{
    return ((git_indexer_progress *)progressPtr)->total_objects;
}

/** unsigned int indexed_objects*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetIndexedObjects)(JNIEnv *env, jclass obj, jlong progressPtr)
{
    return ((git_indexer_progress *)progressPtr)->indexed_objects;
}

/** unsigned int received_objects*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetReceivedObjects)(JNIEnv *env, jclass obj, jlong progressPtr)
{
    return ((git_indexer_progress *)progressPtr)->received_objects;
}

/** unsigned int local_objects*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetLocalObjects)(JNIEnv *env, jclass obj, jlong progressPtr)
{
    return ((git_indexer_progress *)progressPtr)->local_objects;
}

/** unsigned int total_deltas*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetTotalDeltas)(JNIEnv *env, jclass obj, jlong progressPtr)
{
    return ((git_indexer_progress *)progressPtr)->total_deltas;
}

/** unsigned int indexed_deltas*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetIndexedDeltas)(JNIEnv *env, jclass obj, jlong progressPtr)
{
    return ((git_indexer_progress *)progressPtr)->indexed_deltas;
}

/** size_t received_bytes*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetReceivedBytes)(JNIEnv *env, jclass obj, jlong progressPtr)
{
    return ((git_indexer_progress *)progressPtr)->received_bytes;
}

/** unsigned int total_objects*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Indexer_jniProgressSetTotalObjects)(JNIEnv *env, jclass obj, jlong progressPtr, jint totalObjects)
{
    ((git_indexer_progress *)progressPtr)->total_objects = (unsigned int)totalObjects;
}

/** unsigned int indexed_objects*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Indexer_jniProgressSetIndexedObjects)(JNIEnv *env, jclass obj, jlong progressPtr, jint indexedObjects)
{
    ((git_indexer_progress *)progressPtr)->indexed_objects = (unsigned int)indexedObjects;
}

/** unsigned int received_objects*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Indexer_jniProgressSetReceivedObjects)(JNIEnv *env, jclass obj, jlong progressPtr, jint receivedObjects)
{
    ((git_indexer_progress *)progressPtr)->received_objects = (unsigned int)receivedObjects;
}

/** unsigned int local_objects*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Indexer_jniProgressSetLocalObjects)(JNIEnv *env, jclass obj, jlong progressPtr, jint localObjects)
{
    ((git_indexer_progress *)progressPtr)->local_objects = (unsigned int)localObjects;
}

/** unsigned int total_deltas*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Indexer_jniProgressSetTotalDeltas)(JNIEnv *env, jclass obj, jlong progressPtr, jint totalDeltas)
{
    ((git_indexer_progress *)progressPtr)->total_deltas = (unsigned int)totalDeltas;
}

/** unsigned int indexed_deltas*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Indexer_jniProgressSetIndexedDeltas)(JNIEnv *env, jclass obj, jlong progressPtr, jint indexedDeltas)
{
    ((git_indexer_progress *)progressPtr)->indexed_deltas = (unsigned int)indexedDeltas;
}

/** size_t received_bytes*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Indexer_jniProgressSetReceivedBytes)(JNIEnv *env, jclass obj, jlong progressPtr, jint receivedBytes)
{
    ((git_indexer_progress *)progressPtr)->received_bytes = (size_t)receivedBytes;
}
