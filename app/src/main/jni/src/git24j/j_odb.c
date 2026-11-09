#include "j_odb.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

// no matching type found for 'git_odb_foreach_cb cb'
/** int git_odb_foreach(git_odb *db, git_odb_foreach_cb cb, void *payload); */
// no matching type found for 'char *buffer'
/** int git_odb_stream_read(git_odb_stream *stream, char *buffer, size_t len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniStreamRead)(JNIEnv *env, jclass obj, jlong streamPtr, jbyteArray buffer, jint len)
{
    char *c_buffer = (char *)malloc(sizeof(char) * len);
    int e = git_odb_stream_read((git_odb_stream *)streamPtr, c_buffer, len);
    (*env)->SetByteArrayRegion(env, buffer, 0, len, (jbyte *)c_buffer);
    free(c_buffer);
    return e;
}
// no matching type found for 'git_transfer_progress_cb progress_cb'
/** int git_odb_write_pack(git_odb_writepack **out, git_odb *db, git_transfer_progress_cb progress_cb, void *progress_payload); */
/** -------- Wrapper Body ---------- */
/** int git_odb_new(git_odb **out); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniNew)(JNIEnv *env, jclass obj, jobject out)
{
    git_odb *c_out = 0;
    int r = git_odb_new(&c_out);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_odb_free(c_out); */
    return r;
}

/** int git_odb_open(git_odb **out, const char *objects_dir); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniOpen)(JNIEnv *env, jclass obj, jobject out, jstring objects_dir)
{
    git_odb *c_out = 0;
    char *c_objects_dir = j_copy_of_jstring(env, objects_dir, true);
    int r = git_odb_open(&c_out, c_objects_dir);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_odb_free(c_out); */
    free(c_objects_dir);
    return r;
}

/** int git_odb_add_disk_alternate(git_odb *odb, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniAddDiskAlternate)(JNIEnv *env, jclass obj, jlong odbPtr, jstring path)
{
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_odb_add_disk_alternate((git_odb *)odbPtr, c_path);
    free(c_path);
    return r;
}

/** void git_odb_free(git_odb *db); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Odb_jniFree)(JNIEnv *env, jclass obj, jlong dbPtr)
{
    git_odb_free((git_odb *)dbPtr);
}

/** int git_odb_read(git_odb_object **out, git_odb *db, const git_oid *id); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniRead)(JNIEnv *env, jclass obj, jobject out, jlong dbPtr, jobject id)
{
    git_odb_object *c_out = 0;
    git_oid c_id;
    j_git_oid_from_java(env, id, &c_id);
    int r = git_odb_read(&c_out, (git_odb *)dbPtr, &c_id);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_odb_object_free(c_out); */
    return r;
}

/** int git_odb_read_prefix(git_odb_object **out, git_odb *db, const git_oid *short_id, size_t len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniReadPrefix)(JNIEnv *env, jclass obj, jobject out, jlong dbPtr, jstring shortId)
{
    git_odb_object *c_out = 0;
    git_oid c_short_id;
    int short_id_len;
    int e = j_git_short_id_from_java(env, shortId, &c_short_id, &short_id_len);
    if (e != 0)
    {
        return e;
    }
    int r = git_odb_read_prefix(&c_out, (git_odb *)dbPtr, &c_short_id, short_id_len);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** int git_odb_read_header(size_t *len_out, git_object_t *type_out, git_odb *db, const git_oid *id); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniReadHeader)(JNIEnv *env, jclass obj, jobject lenOut, jobject typeOut, jlong dbPtr, jobject id)
{
    size_t c_len_out;
    git_object_t c_type_out;
    git_oid c_id;
    j_git_oid_from_java(env, id, &c_id);
    int r = git_odb_read_header(&c_len_out, &c_type_out, (git_odb *)dbPtr, &c_id);
    (*env)->CallVoidMethod(env, lenOut, jniConstants->midAtomicIntSet, c_len_out);
    (*env)->CallVoidMethod(env, typeOut, jniConstants->midAtomicIntSet, c_type_out);
    return r;
}

/** int git_odb_exists(git_odb *db, const git_oid *id); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniExists)(JNIEnv *env, jclass obj, jlong dbPtr, jobject id)
{
    git_oid c_id;
    j_git_oid_from_java(env, id, &c_id);
    int r = git_odb_exists((git_odb *)dbPtr, &c_id);
    return r;
}

/** int git_odb_exists_prefix(git_oid *out, git_odb *db, const git_oid *short_id, size_t len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniExistsPrefix)(JNIEnv *env, jclass obj, jobject out, jlong dbPtr, jstring shortId)
{
    git_oid c_out;
    git_oid c_short_id;
    int short_id_len;
    int e = j_git_short_id_from_java(env, shortId, &c_short_id, &short_id_len);
    if (e != 0)
    {
        return e;
    }
    int r = git_odb_exists_prefix(&c_out, (git_odb *)dbPtr, &c_short_id, short_id_len);
    j_git_oid_to_java(env, &c_out, out);
    return r;
}

/** int git_odb_expand_ids(git_odb *db, git_odb_expand_id *ids, size_t count); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniExpandIds)(JNIEnv *env, jclass obj, jlong dbPtr, jlong idsPtr, jint count)
{

    /**FIXME: expandIds does not work properly*/
    /* git_odb_expand_id *ids = (git_odb_expand_id *)idsPtr;
    printf("qqqqq before expand_ids = %p; ids[0] = %s, len = %d, count = %d \n", ids, git_oid_tostr_s(&(ids[0].id)), ids->length, count);
    printf("qqqqq before expand_ids = %p; ids[1] = %s, len = %d \n", ids, git_oid_tostr_s(&(ids[1].id)), ids->length); */
    int r = git_odb_expand_ids((git_odb *)dbPtr, (git_odb_expand_id *)idsPtr, count);
    /* printf("qqqqq after expand_ids = %p; ids[0] = %s, len = %d \n", ids, git_oid_tostr_s(&(ids[0].id)), ids->length);
    printf("qqqqq after expand_ids = %p; ids[1] = %s, len = %d \n", ids, git_oid_tostr_s(&(ids[1].id)), ids->length); */
    return r;
}

/** create an array of git_odb_expand_id objects from java oid array (Oid []). */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Odb_jniExpandIdsNew)(JNIEnv *env, jclass obj, jobjectArray shortIds, jint type)
{
    jsize len = (*env)->GetArrayLength(env, shortIds);
    git_odb_expand_id *expand_ids = (git_odb_expand_id *)malloc(sizeof(git_odb_expand_id) * len);
    expand_ids->type = type;
    for (jsize i = 0; i < len; i++)
    {
        jstring oidStr = (jstring)(*env)->GetObjectArrayElement(env, shortIds, i);
        if (oidStr == NULL) {
            // 检测java side是否有异常（比如索引越界）
            if ((*env)->ExceptionCheck(env)) {
                (*env)->ExceptionDescribe(env); // 打印异常
                free(expand_ids);
                // 返回空指针
                // 注意：虽然一般出错都返回负数，但这里不行，
                // 因为c指针一般都是无符号类型（正数或0），
                // 但java里没有无符号类型，
                // 所以c的无符号long传给java时，
                // java读取到的可能会是负数，但底层存储的2进制数据不会变
                // 若在此返回负数代表错误，会和正常的指针混淆
                return (jlong)NULL;
            }

            // 数组元素为 null 但java没异常，继续循环
            continue;
        }

        int short_id_len;
        j_git_short_id_from_java(env, oidStr, &(expand_ids[i].id), &short_id_len);
        if (i == 0 || expand_ids->length > short_id_len)
        {
            expand_ids->length = short_id_len;
        }

        // fix issue #145: https://github.com/catpuppyapp/PuppyGit/issues/145
        (*env)->DeleteLocalRef(env, oidStr);
    }
    return (jlong)expand_ids;
}

JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Odb_jniExpandIdsGetId)(JNIEnv *env, jclass obj, jlong expandIdsPtr, jint idx)
{
    git_odb_expand_id *ids = (git_odb_expand_id *)expandIdsPtr;
    git_oid *c_oid = &(ids[idx].id);
    return j_git_oid_to_bytearray(env, c_oid);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniExpandIdsGetType)(JNIEnv *env, jclass obj, jlong expandIdsPtr, jint idx)
{
    git_odb_expand_id *ids = (git_odb_expand_id *)expandIdsPtr;
    return (jint)ids[idx].type;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniExpandIdsGetLength)(JNIEnv *env, jclass obj, jlong expandIdsPtr)
{
    git_odb_expand_id *ids = (git_odb_expand_id *)expandIdsPtr;
    return (jint)ids->length;
}

/** int git_odb_refresh(const git_odb *db); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniRefresh)(JNIEnv *env, jclass obj, jlong dbPtr)
{
    int r = git_odb_refresh((git_odb *)dbPtr);
    return r;
}

/** int git_odb_write(git_oid *out, git_odb *odb, const void *data, size_t len, git_object_t type); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniWrite)(JNIEnv *env, jclass obj, jobject out, jlong odbPtr, jbyteArray data, jint len, jint type)
{
    git_oid c_out;
    int data_len;
    unsigned char *c_data = j_unsigned_chars_from_java(env, data, &data_len);
    int r = git_odb_write(&c_out, (git_odb *)odbPtr, (void *)c_data, len, type);
    j_git_oid_to_java(env, &c_out, out);
    (*env)->ReleaseByteArrayElements(env, data, (jbyte *)c_data, 0);
    return r;
}

/** int git_odb_open_wstream(git_odb_stream **out, git_odb *db, git_off_t size, git_object_t type); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniOpenWstream)(JNIEnv *env, jclass obj, jobject out, jlong dbPtr, jint size, jint type)
{
    git_odb_stream *c_out = 0;
    int r = git_odb_open_wstream(&c_out, (git_odb *)dbPtr, size, type);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_odb_stream_free(c_out); */
    return r;
}

/** int git_odb_stream_write(git_odb_stream *stream, const char *buffer, size_t len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniStreamWrite)(JNIEnv *env, jclass obj, jlong streamPtr, jstring buffer, jint len)
{
    char *c_buffer = j_copy_of_jstring(env, buffer, true);
    int r = git_odb_stream_write((git_odb_stream *)streamPtr, c_buffer, len);
    free(c_buffer);
    return r;
}

/** int git_odb_stream_finalize_write(git_oid *out, git_odb_stream *stream); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniStreamFinalizeWrite)(JNIEnv *env, jclass obj, jobject out, jlong streamPtr)
{
    git_oid c_out;
    int r = git_odb_stream_finalize_write(&c_out, (git_odb_stream *)streamPtr);
    j_git_oid_to_java(env, &c_out, out);
    return r;
}

/** void git_odb_stream_free(git_odb_stream *stream); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Odb_jniStreamFree)(JNIEnv *env, jclass obj, jlong streamPtr)
{
    git_odb_stream_free((git_odb_stream *)streamPtr);
}

/** int git_odb_open_rstream(git_odb_stream **out, size_t *len, git_object_t *type, git_odb *db, const git_oid *oid); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniOpenRstream)(JNIEnv *env, jclass obj, jobject out, jobject len, jobject outType, jlong dbPtr, jobject oid)
{
    git_odb_stream *c_out = 0;
    size_t c_len;
    git_oid c_oid;
    git_object_t c_type;
    j_git_oid_from_java(env, oid, &c_oid);
    int r = git_odb_open_rstream(&c_out, &c_len, &c_type, (git_odb *)dbPtr, &c_oid);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_odb_stream_free(c_out); */
    (*env)->CallVoidMethod(env, outType, jniConstants->midAtomicIntSet, c_type);
    (*env)->CallVoidMethod(env, len, jniConstants->midAtomicIntSet, c_len);
    return r;
}

/** int git_odb_hash(git_oid *out, const void *data, size_t len, git_object_t type); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniHash)(JNIEnv *env, jclass obj, jobject out, jbyteArray data, jint len, jint type)
{
    git_oid c_out;
    int data_len;
    unsigned char *c_data = j_unsigned_chars_from_java(env, data, &data_len);
    int r = git_odb_hash(&c_out, (void *)c_data, len, type);
    j_git_oid_to_java(env, &c_out, out);
    (*env)->ReleaseByteArrayElements(env, data, (jbyte *)c_data, 0);
    return r;
}

/** int git_odb_hashfile(git_oid *out, const char *path, git_object_t type); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniHashfile)(JNIEnv *env, jclass obj, jobject out, jstring path, jint type)
{
    git_oid c_out;
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_odb_hashfile(&c_out, c_path, type);
    j_git_oid_to_java(env, &c_out, out);
    free(c_path);
    return r;
}

/** int git_odb_object_dup(git_odb_object **dest, git_odb_object *source); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniObjectDup)(JNIEnv *env, jclass obj, jobject dest, jlong sourcePtr)
{
    git_odb_object *c_dest = 0;
    int r = git_odb_object_dup(&c_dest, (git_odb_object *)sourcePtr);
    (*env)->CallVoidMethod(env, dest, jniConstants->midAtomicLongSet, (jlong)c_dest);
    /* git_odb_object_free(c_dest); */
    return r;
}

/** void git_odb_object_free(git_odb_object *object); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Odb_jniObjectFree)(JNIEnv *env, jclass obj, jlong objectPtr)
{
    git_odb_object_free((git_odb_object *)objectPtr);
}

/** const git_oid * git_odb_object_id(git_odb_object *object); */
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Odb_jniObjectId)(JNIEnv *env, jclass obj, jlong objectPtr)
{
    const git_oid *c_oid = git_odb_object_id((git_odb_object *)objectPtr);
    return j_git_oid_to_bytearray(env, c_oid);
}

/** const void * git_odb_object_data(git_odb_object *object); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Odb_jniObjectData)(JNIEnv *env, jclass obj, jlong objectPtr)
{
    const void *r = git_odb_object_data((git_odb_object *)objectPtr);
    return (jlong)r;
}

/** size_t git_odb_object_size(git_odb_object *object); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniObjectSize)(JNIEnv *env, jclass obj, jlong objectPtr)
{
    size_t r = git_odb_object_size((git_odb_object *)objectPtr);
    return r;
}

/** git_object_t git_odb_object_type(git_odb_object *object); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniObjectType)(JNIEnv *env, jclass obj, jlong objectPtr)
{
    git_object_t r = git_odb_object_type((git_odb_object *)objectPtr);
    return r;
}

/** int git_odb_add_backend(git_odb *odb, git_odb_backend *backend, int priority); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniAddBackend)(JNIEnv *env, jclass obj, jlong odbPtr, jlong backendPtr, jint priority)
{
    int r = git_odb_add_backend((git_odb *)odbPtr, (git_odb_backend *)backendPtr, priority);
    return r;
}

/** int git_odb_add_alternate(git_odb *odb, git_odb_backend *backend, int priority); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniAddAlternate)(JNIEnv *env, jclass obj, jlong odbPtr, jlong backendPtr, jint priority)
{
    int r = git_odb_add_alternate((git_odb *)odbPtr, (git_odb_backend *)backendPtr, priority);
    return r;
}

/** size_t git_odb_num_backends(git_odb *odb); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniNumBackends)(JNIEnv *env, jclass obj, jlong odbPtr)
{
    size_t r = git_odb_num_backends((git_odb *)odbPtr);
    return r;
}

/** int git_odb_get_backend(git_odb_backend **out, git_odb *odb, size_t pos); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniGetBackend)(JNIEnv *env, jclass obj, jobject out, jlong odbPtr, jint pos)
{
    git_odb_backend *c_out = 0;
    int r = git_odb_get_backend(&c_out, (git_odb *)odbPtr, pos);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_odb_backend_free(c_out); */
    return r;
}

/** int git_odb_backend_pack(git_odb_backend **out, const char *objects_dir); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniBackendPack)(JNIEnv *env, jclass obj, jobject out, jstring objects_dir)
{
    git_odb_backend *c_out = 0;
    char *c_objects_dir = j_copy_of_jstring(env, objects_dir, true);
    int r = git_odb_backend_pack(&c_out, c_objects_dir);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_odb_backend_free(c_out); */
    free(c_objects_dir);
    return r;
}

/** int git_odb_backend_loose(git_odb_backend **out, const char *objects_dir, int compression_level, int do_fsync, unsigned int dir_mode, unsigned int file_mode); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniBackendLoose)(JNIEnv *env, jclass obj, jobject out, jstring objects_dir, jint compression_level, jint do_fsync, jint dirMode, jint fileMode)
{
    git_odb_backend *c_out = 0;
    char *c_objects_dir = j_copy_of_jstring(env, objects_dir, true);
    int r = git_odb_backend_loose(&c_out, c_objects_dir, compression_level, do_fsync, dirMode, fileMode);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_odb_backend_free(c_out); */
    free(c_objects_dir);
    return r;
}

/** int git_odb_backend_one_pack(git_odb_backend **out, const char *index_file); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Odb_jniBackendOnePack)(JNIEnv *env, jclass obj, jobject out, jstring index_file)
{
    git_odb_backend *c_out = 0;
    char *c_index_file = j_copy_of_jstring(env, index_file, true);
    int r = git_odb_backend_one_pack(&c_out, c_index_file);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_odb_backend_free(c_out); */
    free(c_index_file);
    return r;
}
