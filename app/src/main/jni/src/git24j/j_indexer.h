#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_INDEXER_H__
#define __GIT24J_INDEXER_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_indexer_new(git_indexer **out, const char *path, unsigned int mode, git_odb *odb, git_indexer_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniNew)(JNIEnv *env, jclass obj, jobject out, jstring path, jint mode, jlong odbPtr, jlong optsPtr);

    /** int git_indexer_append(git_indexer *idx, const void *data, size_t size, git_indexer_progress *stats); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniAppend)(JNIEnv *env, jclass obj, jlong idxPtr, jbyteArray data, jint size, jlong statsPtr);

    /** int git_indexer_commit(git_indexer *idx, git_indexer_progress *stats); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniCommit)(JNIEnv *env, jclass obj, jlong idxPtr, jlong statsPtr);

    /** const git_oid * git_indexer_hash(const git_indexer *idx); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Indexer_jniHash)(JNIEnv *env, jclass obj, jlong idxPtr);

    /** void git_indexer_free(git_indexer *idx); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Indexer_jniFree)(JNIEnv *env, jclass obj, jlong idxPtr);

    /** -------- Signature of the header ---------- */
    /** int git_indexer_options_init(git_indexer_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version, jobject progressCb);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Indexer_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    /** unsigned char verify*/
    JNIEXPORT jbyte JNICALL J_MAKE_METHOD(Indexer_jniOptionsGetVerify)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** unsigned char verify*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Indexer_jniOptionsSetVerify)(JNIEnv *env, jclass obj, jlong optionsPtr, jbyte verify);

    /** -------- Signature of the header ---------- */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Indexer_jniProgressNew)(JNIEnv *env, jclass obj);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetTotalObjects)(JNIEnv *env, jclass obj, jlong progressPtr);
    /** unsigned int indexed_objects*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetIndexedObjects)(JNIEnv *env, jclass obj, jlong progressPtr);
    /** unsigned int received_objects*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetReceivedObjects)(JNIEnv *env, jclass obj, jlong progressPtr);
    /** unsigned int local_objects*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetLocalObjects)(JNIEnv *env, jclass obj, jlong progressPtr);
    /** unsigned int total_deltas*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetTotalDeltas)(JNIEnv *env, jclass obj, jlong progressPtr);
    /** unsigned int indexed_deltas*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetIndexedDeltas)(JNIEnv *env, jclass obj, jlong progressPtr);
    /** size_t received_bytes*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Indexer_jniProgressGetReceivedBytes)(JNIEnv *env, jclass obj, jlong progressPtr);

#ifdef __cplusplus
}
#endif
#endif