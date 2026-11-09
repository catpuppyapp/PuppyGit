#include "j_blob.h"
#include "j_mappers.h"
#include "j_util.h"
extern j_constants_t *jniConstants;

/** int git_blob_lookup(git_blob **blob, git_repository *repo, const git_oid *id); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniLookup)(JNIEnv *env, jclass obj, jobject outBlob, jlong repoPtr, jobject oid)
{
    git_blob *c_blob;
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    int e = git_blob_lookup(&c_blob, (git_repository *)repoPtr, &c_oid);
    (*env)->CallVoidMethod(env, outBlob, jniConstants->midAtomicLongSet, (jlong)c_blob);
    return e;
}

/** int git_blob_lookup_prefix(git_blob **blob, git_repository *repo, const git_oid *id, size_t len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniLookupPrefix)(JNIEnv *env, jclass obj, jobject outBlob, jlong repoPtr, jstring shortId)
{
    git_blob *c_blob = 0;
    git_oid c_oid;
    int short_id_len;
    int e = j_git_short_id_from_java(env, shortId, &c_oid, &short_id_len);
    if (e != 0)
    {
        return e;
    }

    int r = git_blob_lookup_prefix(&c_blob, (git_repository *)repoPtr, &c_oid, short_id_len);
    (*env)->CallVoidMethod(env, outBlob, jniConstants->midAtomicLongSet, (jlong)c_blob);
    return r;
}

/** void git_blob_free(git_blob *blob); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Blob_jniFree)(JNIEnv *env, jclass obj, jlong blobPtr)
{
    git_blob_free((git_blob *)blobPtr);
}

/** const git_oid * git_blob_id(const git_blob *blob); */
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Blob_jniId)(JNIEnv *env, jclass obj, jlong blobPtr)
{
    const git_oid *c_oid = git_blob_id((git_blob *)blobPtr);
    return j_git_oid_to_bytearray(env, c_oid);
}

/** git_repository * git_blob_owner(const git_blob *blob); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blob_jniOwner)(JNIEnv *env, jclass obj, jlong blobPtr)
{
    return (jlong)git_blob_owner((git_blob *)blobPtr);
}

/** const void * git_blob_rawcontent(const git_blob *blob); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blob_jniRawContent)(JNIEnv *env, jclass obj, jlong blobPtr)
{
    return (jlong)git_blob_rawcontent((git_blob *)blobPtr);
}

/** git_off_t git_blob_rawsize(const git_blob *blob); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blob_jniRawSize)(JNIEnv *env, jclass obj, jlong blobPtr)
{
    return (jlong)git_blob_rawsize((git_blob *)blobPtr);
}

/** int git_blob_create_from_workdir(git_oid *id, git_repository *repo, const char *relative_path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromWorkdir)(JNIEnv *env, jclass obj, jobject outId, jlong repoPtr, jstring relativePath)
{
    git_oid c_oid;
    char *relative_path = j_copy_of_jstring(env, relativePath, true);
    int e = git_blob_create_fromworkdir(&c_oid, (git_repository *)repoPtr, relative_path);
    free(relative_path);
    j_git_oid_to_java(env, &c_oid, outId);
    return e;
}

/** int git_blob_create_from_disk(git_oid *id, git_repository *repo, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromDisk)(JNIEnv *env, jclass obj, jobject outId, jlong repoPtr, jstring path)
{
    git_oid c_oid;
    char *c_path = j_copy_of_jstring(env, path, true);
    int e = git_blob_create_fromdisk(&c_oid, (git_repository *)repoPtr, c_path);
    free(c_path);
    j_git_oid_to_java(env, &c_oid, outId);
    return e;
}

/** int git_blob_create_from_stream(git_writestream **out, git_repository *repo, const char *hintpath); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromStream)(JNIEnv *env, jclass obj, jobject outStream, jlong repoPtr, jstring hintPath)
{
    char *hint_path = j_copy_of_jstring(env, hintPath, true);
    git_writestream *out_stream = 0;
    int e = git_blob_create_fromstream(&out_stream, (git_repository *)repoPtr, hint_path);
    (*env)->CallVoidMethod(env, outStream, jniConstants->midAtomicLongSet, (jlong)out_stream);
    free(hint_path);
    return e;
}

/** int git_blob_create_from_stream_commit(git_oid *out, git_writestream *stream); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromStreamCommit)(JNIEnv *env, jclass obj, jobject outId, jlong streamPtr)
{
    git_oid c_oid;
    int e = git_blob_create_fromstream_commit(&c_oid, (git_writestream *)streamPtr);
    j_git_oid_to_java(env, &c_oid, outId);
    return e;
}

/** int git_blob_create_from_buffer(git_oid *id, git_repository *repo, const void *buffer, size_t len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniCreateFromBuffer)(JNIEnv *env, jclass obj, jobject outId, jlong repoPtr, jbyteArray buf)
{
    git_oid c_oid;
    int c_buf_len;
    unsigned char *c_buf = j_unsigned_chars_from_java(env, buf, &c_buf_len);
    int e = git_blob_create_frombuffer(&c_oid, (git_repository *)repoPtr, (const void *)c_buf, c_buf_len);
    free(c_buf);
    j_git_oid_to_java(env, &c_oid, outId);
    return e;
}

/** int git_blob_is_binary(const git_blob *blob); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniIsBinary)(JNIEnv *env, jclass obj, jlong blobPtr)
{
    return git_blob_is_binary((git_blob *)blobPtr);
}

/** int git_blob_dup(git_blob **out, git_blob *source); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniDup)(JNIEnv *env, jclass obj, jobject outDest, jlong srcPtr)
{
    git_blob *out = 0;
    int e = git_blob_dup(&out, (git_blob *)srcPtr);
    (*env)->CallVoidMethod(env, outDest, jniConstants->midAtomicLongSet, (jlong)out);
    return e;
}

/** int git_blob_filtered_content(git_buf *out, git_blob *blob, const char *as_path, int check_for_binary_data); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blob_jniFilteredContent)(JNIEnv *env, jclass obj, jobject out, jlong blobPtr, jstring as_path, jint check_for_binary_data)
{
    git_buf c_out = {0};
    char *c_as_path = j_copy_of_jstring(env, as_path, true);
    int r = git_blob_filtered_content(&c_out, (git_blob *)blobPtr, c_as_path, check_for_binary_data);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    free(c_as_path);
    return r;
}
