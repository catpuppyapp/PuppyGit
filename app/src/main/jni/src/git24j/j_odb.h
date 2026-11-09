#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_ODB_H__
#define __GIT24J_ODB_H__
#ifdef __cplusplus
extern "C"
{
#endif

    // no matching type found for 'git_odb_foreach_cb cb'
    /** int git_odb_foreach(git_odb *db, git_odb_foreach_cb cb, void *payload); */
    // no matching type found for 'char *buffer'
    /** int git_odb_stream_read(git_odb_stream *stream, char *buffer, size_t len); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniStreamRead)(JNIEnv *env, jclass obj, jlong streamPtr, jbyteArray buffer, jint len);
    // no matching type found for 'git_transfer_progress_cb progress_cb'
    /** int git_odb_write_pack(git_odb_writepack **out, git_odb *db, git_transfer_progress_cb progress_cb, void *progress_payload); */
    /** -------- Signature of the header ---------- */
    /** int git_odb_new(git_odb **out); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniNew)(JNIEnv *env, jclass obj, jobject out);

    /** int git_odb_open(git_odb **out, const char *objects_dir); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniOpen)(JNIEnv *env, jclass obj, jobject out, jstring objects_dir);

    /** int git_odb_add_disk_alternate(git_odb *odb, const char *path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniAddDiskAlternate)(JNIEnv *env, jclass obj, jlong odbPtr, jstring path);

    /** void git_odb_free(git_odb *db); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Odb_jniFree)(JNIEnv *env, jclass obj, jlong dbPtr);

    /** int git_odb_read(git_odb_object **out, git_odb *db, const git_oid *id); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniRead)(JNIEnv *env, jclass obj, jobject out, jlong dbPtr, jobject id);

    /** int git_odb_read_prefix(git_odb_object **out, git_odb *db, const git_oid *short_id, size_t len); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniReadPrefix)(JNIEnv *env, jclass obj, jobject out, jlong dbPtr, jstring shortId);

    /** int git_odb_read_header(size_t *len_out, git_object_t *type_out, git_odb *db, const git_oid *id); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniReadHeader)(JNIEnv *env, jclass obj, jobject lenOut, jobject typeOut, jlong dbPtr, jobject id);

    /** int git_odb_exists(git_odb *db, const git_oid *id); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniExists)(JNIEnv *env, jclass obj, jlong dbPtr, jobject id);

    /** int git_odb_exists_prefix(git_oid *out, git_odb *db, const git_oid *short_id, size_t len); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniExistsPrefix)(JNIEnv *env, jclass obj, jobject out, jlong dbPtr, jstring shortId);

    /** int git_odb_expand_ids(git_odb *db, git_odb_expand_id *ids, size_t count); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniExpandIds)(JNIEnv *env, jclass obj, jlong dbPtr, jlong idsPtr, jint count);
    /** create an array of git_odb_expand_id objects from java oid array (Oid []). */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Odb_jniExpandIdsNew)(JNIEnv *env, jclass obj, jobjectArray shortIds, jint type);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Odb_jniExpandIdsGetId)(JNIEnv *env, jclass obj, jlong expandIdsPtr, jint idx);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniExpandIdsGetType)(JNIEnv *env, jclass obj, jlong expandIdsPtr, jint idx);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniExpandIdsGetLength)(JNIEnv *env, jclass obj, jlong expandIdsPtr);

    /** int git_odb_refresh(const git_odb *db); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniRefresh)(JNIEnv *env, jclass obj, jlong dbPtr);

    /** int git_odb_write(git_oid *out, git_odb *odb, const void *data, size_t len, git_object_t type); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniWrite)(JNIEnv *env, jclass obj, jobject out, jlong odbPtr, jbyteArray data, jint len, jint type);

    /** int git_odb_open_wstream(git_odb_stream **out, git_odb *db, git_off_t size, git_object_t type); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniOpenWstream)(JNIEnv *env, jclass obj, jobject out, jlong dbPtr, jint size, jint type);

    /** int git_odb_stream_write(git_odb_stream *stream, const char *buffer, size_t len); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniStreamWrite)(JNIEnv *env, jclass obj, jlong streamPtr, jstring buffer, jint len);

    /** int git_odb_stream_finalize_write(git_oid *out, git_odb_stream *stream); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniStreamFinalizeWrite)(JNIEnv *env, jclass obj, jobject out, jlong streamPtr);

    /** void git_odb_stream_free(git_odb_stream *stream); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Odb_jniStreamFree)(JNIEnv *env, jclass obj, jlong streamPtr);

    /** int git_odb_open_rstream(git_odb_stream **out, size_t *len, git_object_t *type, git_odb *db, const git_oid *oid); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniOpenRstream)(JNIEnv *env, jclass obj, jobject out, jobject len, jobject outType, jlong dbPtr, jobject oid);

    /** int git_odb_hash(git_oid *out, const void *data, size_t len, git_object_t type); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniHash)(JNIEnv *env, jclass obj, jobject out, jbyteArray data, jint len, jint type);

    /** int git_odb_hashfile(git_oid *out, const char *path, git_object_t type); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniHashfile)(JNIEnv *env, jclass obj, jobject out, jstring path, jint type);

    /** int git_odb_object_dup(git_odb_object **dest, git_odb_object *source); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniObjectDup)(JNIEnv *env, jclass obj, jobject dest, jlong sourcePtr);

    /** void git_odb_object_free(git_odb_object *object); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Odb_jniObjectFree)(JNIEnv *env, jclass obj, jlong objectPtr);

    /** const git_oid * git_odb_object_id(git_odb_object *object); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Odb_jniObjectId)(JNIEnv *env, jclass obj, jlong objectPtr);

    /** const void * git_odb_object_data(git_odb_object *object); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Odb_jniObjectData)(JNIEnv *env, jclass obj, jlong objectPtr);

    /** size_t git_odb_object_size(git_odb_object *object); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniObjectSize)(JNIEnv *env, jclass obj, jlong objectPtr);

    /** git_object_t git_odb_object_type(git_odb_object *object); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniObjectType)(JNIEnv *env, jclass obj, jlong objectPtr);

    /** int git_odb_add_backend(git_odb *odb, git_odb_backend *backend, int priority); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniAddBackend)(JNIEnv *env, jclass obj, jlong odbPtr, jlong backendPtr, jint priority);

    /** int git_odb_add_alternate(git_odb *odb, git_odb_backend *backend, int priority); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniAddAlternate)(JNIEnv *env, jclass obj, jlong odbPtr, jlong backendPtr, jint priority);

    /** size_t git_odb_num_backends(git_odb *odb); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniNumBackends)(JNIEnv *env, jclass obj, jlong odbPtr);

    /** int git_odb_get_backend(git_odb_backend **out, git_odb *odb, size_t pos); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniGetBackend)(JNIEnv *env, jclass obj, jobject out, jlong odbPtr, jint pos);

    /** int git_odb_backend_pack(git_odb_backend **out, const char *objects_dir); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniBackendPack)(JNIEnv *env, jclass obj, jobject out, jstring objects_dir);

    /** int git_odb_backend_loose(git_odb_backend **out, const char *objects_dir, int compression_level, int do_fsync, unsigned int dir_mode, unsigned int file_mode); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniBackendLoose)(JNIEnv *env, jclass obj, jobject out, jstring objects_dir, jint compression_level, jint do_fsync, jint dirMode, jint fileMode);

    /** int git_odb_backend_one_pack(git_odb_backend **out, const char *index_file); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniBackendOnePack)(JNIEnv *env, jclass obj, jobject out, jstring index_file);

#ifdef __cplusplus
}
#endif
#endif