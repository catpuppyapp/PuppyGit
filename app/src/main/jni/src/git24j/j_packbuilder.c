#include "j_packbuilder.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

int j_packbuilder_git_indexer_progress_cb(const git_indexer_progress *stats, void *payload)
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

/** int git_packbuilder_new(git_packbuilder **out, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr)
{
    git_packbuilder *c_out;
    int r = git_packbuilder_new(&c_out, (git_repository *)repoPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_packbuilder_free(c_out); */
    return r;
}

/** unsigned int git_packbuilder_set_threads(git_packbuilder *pb, unsigned int n); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniSetThreads)(JNIEnv *env, jclass obj, jlong pbPtr, jint n)
{
    unsigned int r = git_packbuilder_set_threads((git_packbuilder *)pbPtr, n);
    return r;
}

/** int git_packbuilder_insert(git_packbuilder *pb, const git_oid *id, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniInsert)(JNIEnv *env, jclass obj, jlong pbPtr, jobject id, jstring name)
{
    git_oid c_id;
    j_git_oid_from_java(env, id, &c_id);
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_packbuilder_insert((git_packbuilder *)pbPtr, &c_id, c_name);
    free(c_name);
    return r;
}

/** int git_packbuilder_insert_tree(git_packbuilder *pb, const git_oid *id); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniInsertTree)(JNIEnv *env, jclass obj, jlong pbPtr, jobject id)
{
    git_oid c_id;
    j_git_oid_from_java(env, id, &c_id);
    int r = git_packbuilder_insert_tree((git_packbuilder *)pbPtr, &c_id);
    return r;
}

/** int git_packbuilder_insert_commit(git_packbuilder *pb, const git_oid *id); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniInsertCommit)(JNIEnv *env, jclass obj, jlong pbPtr, jobject id)
{
    git_oid c_id;
    j_git_oid_from_java(env, id, &c_id);
    int r = git_packbuilder_insert_commit((git_packbuilder *)pbPtr, &c_id);
    return r;
}

/** int git_packbuilder_insert_walk(git_packbuilder *pb, git_revwalk *walk); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniInsertWalk)(JNIEnv *env, jclass obj, jlong pbPtr, jlong walkPtr)
{
    int r = git_packbuilder_insert_walk((git_packbuilder *)pbPtr, (git_revwalk *)walkPtr);
    return r;
}

/** int git_packbuilder_insert_recur(git_packbuilder *pb, const git_oid *id, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniInsertRecur)(JNIEnv *env, jclass obj, jlong pbPtr, jobject id, jstring name)
{
    git_oid c_id;
    j_git_oid_from_java(env, id, &c_id);
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_packbuilder_insert_recur((git_packbuilder *)pbPtr, &c_id, c_name);
    free(c_name);
    return r;
}

/** int git_packbuilder_write_buf(git_buf *buf, git_packbuilder *pb); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniWriteBuf)(JNIEnv *env, jclass obj, jobject buf, jlong pbPtr)
{
    git_buf c_buf = {0};
    int r = git_packbuilder_write_buf(&c_buf, (git_packbuilder *)pbPtr);
    j_git_buf_to_java(env, &c_buf, buf);
    git_buf_dispose(&c_buf);
    return r;
}

/** int git_packbuilder_write(git_packbuilder *pb, const char *path, unsigned int mode, git_indexer_progress_cb * progress_cb); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniWrite)(JNIEnv *env, jclass obj, jlong pbPtr, jstring path, jint mode, jobject progressCb)
{
    int r;
    char *c_path = j_copy_of_jstring(env, path, true);
    if (progressCb == NULL)
    {
        r = git_packbuilder_write((git_packbuilder *)pbPtr, c_path, mode, NULL, NULL);
    }
    else
    {
        j_cb_payload payload = {0};
        j_cb_payload_init(env, &payload, progressCb, "(J)I");
        r = git_packbuilder_write((git_packbuilder *)pbPtr, c_path, mode, j_packbuilder_git_indexer_progress_cb, &payload);
        j_cb_payload_release(env, &payload);
    }
    free(c_path);
    return r;
}

/** const git_oid * git_packbuilder_hash(git_packbuilder *pb); */
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(PackBuilder_jniHash)(JNIEnv *env, jclass obj, jlong pbPtr)
{
    const git_oid *r = git_packbuilder_hash((git_packbuilder *)pbPtr);
    return j_git_oid_to_bytearray(env, r);
}

int j_git_packbuilder_foreach_cb(void *buf, size_t size, void *payload)
{
    if (!payload)
    {
        return 0;
    }
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = getEnv();
    jbyteArray jBytes = j_byte_array_from_c(env, (const unsigned char *)buf, size);
    int r = (*env)->CallIntMethod(env, j_payload->callback, j_payload->mid, jBytes);
    (*env)->DeleteLocalRef(env, jBytes);
    return r;
}

/** int git_packbuilder_foreach(git_packbuilder *pb, git_packbuilder_foreach_cb * cb); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniForeach)(JNIEnv *env, jclass obj, jlong pbPtr, jobject foreachCb)
{
    j_cb_payload payload = {0};
    j_cb_payload_init(env, &payload, foreachCb, "([B)I");
    int r = git_packbuilder_foreach((git_packbuilder *)pbPtr, j_git_packbuilder_foreach_cb, &payload);
    j_cb_payload_release(env, &payload);
    return r;
}

/** size_t git_packbuilder_object_count(git_packbuilder *pb); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniObjectCount)(JNIEnv *env, jclass obj, jlong pbPtr)
{
    size_t r = git_packbuilder_object_count((git_packbuilder *)pbPtr);
    return r;
}

/** size_t git_packbuilder_written(git_packbuilder *pb); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniWritten)(JNIEnv *env, jclass obj, jlong pbPtr)
{
    size_t r = git_packbuilder_written((git_packbuilder *)pbPtr);
    return r;
}

int j_git_packbuilder_progress(int stage, uint32_t current, uint32_t total, void *payload)
{
    if (!payload)
    {
        return 0;
    }
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = getEnv();
    int r = (*env)->CallIntMethod(env, j_payload->callback, j_payload->mid, stage, current, total);
    return r;
}

/** int git_packbuilder_set_callbacks(git_packbuilder *pb, git_packbuilder_progress * progress_cb); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniSetCallbacks)(JNIEnv *env, jclass obj, jlong pbPtr, jobject progressCb)
{
    j_cb_payload *payload = (j_cb_payload *)malloc(sizeof(j_cb_payload));
    j_cb_payload_init(env, payload, progressCb, "(III)I");
    int r = git_packbuilder_set_callbacks((git_packbuilder *)pbPtr, j_git_packbuilder_progress, (void *)payload);
    return r;
}

/** void git_packbuilder_free(git_packbuilder *pb); */
JNIEXPORT void JNICALL J_MAKE_METHOD(PackBuilder_jniFree)(JNIEnv *env, jclass obj, jlong pbPtr)
{
    git_packbuilder_free((git_packbuilder *)pbPtr);
}
