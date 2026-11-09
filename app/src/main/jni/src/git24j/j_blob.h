#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_BLOB_H__
#define __GIT24J_BLOB_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_blob_lookup(git_blob **blob, git_repository *repo, const git_oid *id); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniLookup)(JNIEnv *env, jclass obj, jobject outBlob, jlong repoPtr, jobject oid);

    /** int git_blob_lookup_prefix(git_blob **blob, git_repository *repo, const git_oid *id, size_t len); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniLookupPrefix)(JNIEnv *env, jclass obj, jobject outBlob, jlong repoPtr, jstring shortId);
    /** void git_blob_free(git_blob *blob); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Blob_jniFree)(JNIEnv *env, jclass obj, jlong blobPtr);

    /** const git_oid * git_blob_id(const git_blob *blob); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Blob_jniId)(JNIEnv *env, jclass obj, jlong blobPtr);

    /** git_repository * git_blob_owner(const git_blob *blob); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blob_jniOwner)(JNIEnv *env, jclass obj, jlong blobPtr);

    /** const void * git_blob_rawcontent(const git_blob *blob); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blob_jniRawContent)(JNIEnv *env, jclass obj, jlong blobPtr);

    /** git_off_t git_blob_rawsize(const git_blob *blob); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blob_jniRawSize)(JNIEnv *env, jclass obj, jlong blobPtr);

    /** int git_blob_create_from_workdir(git_oid *id, git_repository *repo, const char *relative_path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromWorkdir)(JNIEnv *env, jclass obj, jobject outId, jlong repoPtr, jstring relativePath);

    /** int git_blob_create_from_disk(git_oid *id, git_repository *repo, const char *path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromDisk)(JNIEnv *env, jclass obj, jobject outId, jlong repoPtr, jstring path);

    /** int git_blob_create_from_stream(git_writestream **out, git_repository *repo, const char *hintpath); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromStream)(JNIEnv *env, jclass obj, jobject outStream, jlong repoPtr, jstring hintPath);

    /** int git_blob_create_from_stream_commit(git_oid *out, git_writestream *stream); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromStreamCommit)(JNIEnv *env, jclass obj, jobject outId, jlong streamPtr);

    /** int git_blob_create_from_buffer(git_oid *id, git_repository *repo, const void *buffer, size_t len); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromBuffer)(JNIEnv *env, jclass obj, jobject outId, jlong repoPtr, jbyteArray buf);

    /** int git_blob_is_binary(const git_blob *blob); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniIsBinary)(JNIEnv *env, jclass obj, jlong blobPtr);

    /** int git_blob_dup(git_blob **out, git_blob *source); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniDup)(JNIEnv *env, jclass obj, jobject outDest, jlong srcPtr);

    /** int git_blob_filtered_content(git_buf *out, git_blob *blob, const char *as_path, int check_for_binary_data); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniFilteredContent)(JNIEnv *env, jclass obj, jobject out, jlong blobPtr, jstring as_path, jint check_for_binary_data);

#ifdef __cplusplus
}
#endif
#endif
