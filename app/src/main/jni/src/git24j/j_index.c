#include "j_index.h"
#include "j_common.h"
#include "j_ensure.h"
#include "j_mappers.h"
#include "j_repository.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <jni.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/**Pack jni objects to pass to update callback. */
typedef struct
{
    JNIEnv *env;
    jobject biConsumer;
} j_mached_cb_paylocads;

int standard_matched_cb(const char *path, const char *matched_pathspec, void *payload)
{
    if (payload == NULL)
    {
        return 0;
    }
    j_mached_cb_paylocads *j_payload = (j_mached_cb_paylocads *)payload;
    JNIEnv *env = j_payload->env;
    jobject biConsumer = j_payload->biConsumer;
    assert(biConsumer && "consumer object must not be null");
    jclass jclz = (*env)->GetObjectClass(env, biConsumer);
    if (jclz == NULL)
    {
        return 0;
    }
    jmethodID accept = (*env)->GetMethodID(env, jclz, "accept", "(Ljava/lang/String;Ljava/lang/String;)V");
    if (accept == NULL)
    {
        return 0;
    }
    jstring j_path = (*env)->NewStringUTF(env, path);
    jstring j_pathspec = (*env)->NewStringUTF(env, matched_pathspec);
    (*env)->CallVoidMethod(env, biConsumer, accept, j_path, j_pathspec);
    (*env)->DeleteLocalRef(env, j_path);
    (*env)->DeleteLocalRef(env, j_pathspec);
    (*env)->DeleteLocalRef(env, jclz);
    return 0;
}

/** int git_index_open(git_index **out, const char *index_path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniOpen)(JNIEnv *env, jclass obj, jobject outIndexPtr, jstring indexPath)
{
    git_index *c_out = 0;
    char *index_path = j_copy_of_jstring(env, indexPath, false);
    int e = git_index_open(&c_out, index_path);
    (*env)->CallVoidMethod(env, outIndexPtr, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(index_path);
    return e;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniFree)(JNIEnv *env, jclass obj, jlong index)
{
    git_index_free((git_index *)index);
}

/** git_repository * git_index_owner(const git_index *index); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniOwner)(JNIEnv *env, jclass obj, jlong indexPtr)
{
    return (jlong)git_index_owner((git_index *)indexPtr);
}

/** int git_index_caps(const git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniCaps)(JNIEnv *env, jclass obj, jlong idxPtr)
{
    return (jint)git_index_caps((git_index *)idxPtr);
}
/** int git_index_set_caps(git_index *index, int caps); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniSetCaps)(JNIEnv *env, jclass obj, jlong idxPtr, jint caps)
{
    return git_index_set_caps((git_index *)idxPtr, caps);
}

/** unsigned int git_index_version(git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniVersion)(JNIEnv *env, jclass obj, jlong idxPtr)
{
    return (jint)git_index_version((git_index *)idxPtr);
    ;
}

/** int git_index_set_version(git_index *index, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniSetVersion)(JNIEnv *env, jclass obj, jlong idxPtr, jint version)
{
    return git_index_set_version((git_index *)idxPtr, (unsigned int)version);
}

/** int git_index_read(git_index *index, int force); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniRead)(JNIEnv *env, jclass obj, jlong indexPtr, jint force)
{
    return git_index_read((git_index *)indexPtr, force);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniWrite)(JNIEnv *env, jclass obj, jlong index)
{
    return git_index_write((git_index *)index);
}

/** const char * git_index_path(const git_index *index); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Index_jniPath)(JNIEnv *env, jclass obj, jlong idxPtr)
{
    const char *path = git_index_path((git_index *)idxPtr);
    return (*env)->NewStringUTF(env, path);
}

/** const git_oid * git_index_checksum(git_index *index); */
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Index_jniChecksum)(JNIEnv *env, jclass obj, jlong indexPtr)
{
    const git_oid *c_oid = git_index_checksum((git_index *)indexPtr);
    return j_git_oid_to_bytearray(env, c_oid);
}

/** int git_index_read_tree(git_index *index, const git_tree *tree); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniReadTree)(JNIEnv *env, jclass obj, jlong indexPtr, jlong treePtr)
{
    return git_index_read_tree((git_index *)indexPtr, (git_tree *)treePtr);
}

/** int git_index_write_tree(git_oid *out, git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniWriteTree)(JNIEnv *env, jclass obj, jobject outOid, jlong indexPtr)
{
    git_oid c_oid = {0};
    int e = git_index_write_tree(&c_oid, (git_index *)indexPtr);
    j_git_oid_to_java(env, &c_oid, outOid);
    return e;
}
/** int git_index_write_tree_to(git_oid *out, git_index *index, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniWriteTreeTo)(JNIEnv *env, jclass obj, jobject outOid, jlong indexPtr, jlong repoPtr)
{
    git_oid c_oid = {0};
    int e = git_index_write_tree_to(&c_oid, (git_index *)indexPtr, (git_repository *)repoPtr);
    j_git_oid_to_java(env, &c_oid, outOid);
    return e;
}

/** size_t git_index_entrycount(const git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryCount)(JNIEnv *env, jclass obj, jlong indexPtr)
{
    return (jint)git_index_entrycount((git_index *)indexPtr);
}

/** int git_index_clear(git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniClear)(JNIEnv *env, jclass obj, jlong indexPtr)
{
    return git_index_clear((git_index *)indexPtr);
}

/** const git_index_entry * git_index_get_byindex(git_index *index, size_t n); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniGetByIndex)(JNIEnv *env, jclass obj, jlong indexPtr, jint n)
{
    return (jlong)git_index_get_byindex((git_index *)indexPtr, (size_t)n);
}
/** const git_index_entry * git_index_get_bypath(git_index *index, const char *path, int stage); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniGetByPath)(JNIEnv *env, jclass obj, jlong indexPtr, jstring path, jint stage)
{
    char *c_path = j_copy_of_jstring(env, path, true);
    const git_index_entry *entry = git_index_get_bypath((git_index *)indexPtr, c_path, stage);
    free(c_path);
    return (jlong)entry;
}

/** int git_index_remove(git_index *index, const char *path, int stage); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniRemove)(JNIEnv *env, jclass obj, jlong indexPtr, jstring path, jint stage)
{
    char *c_path = j_copy_of_jstring(env, path, true);
    int e = git_index_remove((git_index *)indexPtr, c_path, stage);
    free(c_path);
    return e;
}

/** int git_index_remove_directory(git_index *index, const char *dir, int stage); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniRemoveDirectory)(JNIEnv *env, jclass obj, jlong indexPtr, jstring dir, jint stage)
{
    char *c_dir = j_copy_of_jstring(env, dir, true);
    int e = git_index_remove_directory((git_index *)indexPtr, c_dir, stage);
    free(c_dir);
    return e;
}

/** int git_index_add(git_index *index, const git_index_entry *source_entry); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAdd)(JNIEnv *env, jclass obj, jlong index, jlong entryPtr)
{
    return git_index_add((git_index *)index, (git_index_entry *)entryPtr);
}

/** int git_index_entry_stage(const git_index_entry *entry); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryStage)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return git_index_entry_stage((git_index_entry *)entryPtr);
}

/** int git_index_entry_is_conflict(const git_index_entry *entry); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryIsConflict)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return git_index_entry_is_conflict((git_index_entry *)entryPtr);
}

/** int git_index_iterator_new(git_index_iterator **iterator_out, git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniIteratorNew)(JNIEnv *env, jclass obj, jobject outIterPtr, jlong indexPtr)
{
    git_index_iterator *iterator_out = 0;
    int e = git_index_iterator_new(&iterator_out, (git_index *)indexPtr);
    (*env)->CallVoidMethod(env, outIterPtr, jniConstants->midAtomicLongSet, (jlong)iterator_out);
    return e;
}

/** int git_index_iterator_next(const git_index_entry **out, git_index_iterator *iterator); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniIteratorNext)(JNIEnv *env, jclass obj, jobject outEntryPtr, jlong iterPtr)
{
    const git_index_entry *c_out = 0;
    int e = git_index_iterator_next(&c_out, (git_index_iterator *)iterPtr);
    (*env)->CallVoidMethod(env, outEntryPtr, jniConstants->midAtomicLongSet, (jlong)c_out);
    return e;
}

/** void git_index_iterator_free(git_index_iterator *iterator); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniIteratorFree)(JNIEnv *env, jclass obj, jlong iterPtr)
{
    git_index_iterator_free((git_index_iterator *)iterPtr);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAddByPath)(JNIEnv *env, jclass obj, jlong index, jstring path)
{
    git_index *c_index = (git_index *)index;
    char *c_path = j_copy_of_jstring(env, path, false);
    int error = git_index_add_bypath(c_index, c_path);
    free(c_path);
    return error;
}

/** int git_index_add_from_buffer(git_index *index, const git_index_entry *entry, const void *buffer, size_t len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAddFromBuffer)(JNIEnv *env, jclass obj, jlong indexPtr, jlong entryPtr, jbyteArray buffer)
{
    int out_len;
    unsigned char *c_buffer = j_unsigned_chars_from_java(env, buffer, &out_len);
    int e = git_index_add_frombuffer((git_index *)indexPtr, (git_index_entry *)entryPtr, (void *)c_buffer, out_len);
    (*env)->ReleaseByteArrayElements(env, buffer, (jbyte *)c_buffer, 0);
    return e;
}

/** int git_index_remove_bypath(git_index *index, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniRemoveByPath)(JNIEnv *env, jclass obj, jlong indexPtr, jstring path)
{
    char *c_path = j_copy_of_jstring(env, path, true);
    int e = git_index_remove_bypath((git_index *)indexPtr, c_path);
    free(c_path);
    return e;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAddAll)(JNIEnv *env, jclass obj, jlong index, jobjectArray pathspec, jint flags, jobject biConsumer)
{
    j_mached_cb_paylocads j_payloads = {env, biConsumer};
    git_strarray c_pathspec = {0};
    git_index *c_index = (git_index *)index;
    git_strarray_of_jobject_array(env, pathspec, &c_pathspec);
    int error = git_index_add_all(c_index, &c_pathspec, (unsigned int)flags, standard_matched_cb, (void *)(&j_payloads));
    git_strarray_free(&c_pathspec);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniUpdateAll)(JNIEnv *env, jclass obj, jlong index, jobjectArray pathspec, jobject biConsumer)
{
    j_mached_cb_paylocads j_payloads = {env, biConsumer};
    git_strarray c_pathspec = {0};
    git_index *c_index = (git_index *)index;

    git_strarray_of_jobject_array(env, pathspec, &c_pathspec);
    int error = git_index_update_all(c_index, &c_pathspec, standard_matched_cb, (void *)(&j_payloads));
    git_strarray_free(&c_pathspec);
    return error;
}

/** int git_index_find(size_t *at_pos, git_index *index, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniFind)(JNIEnv *env, jclass obj, jobject outPos, jlong indexPtr, jstring path)
{
    char *c_path = j_copy_of_jstring(env, path, true);
    size_t at_pos;
    int e = git_index_find(&at_pos, (git_index *)indexPtr, c_path);
    (*env)->CallVoidMethod(env, outPos, jniConstants->midAtomicIntSet, (jint)at_pos);
    free(c_path);
    return e;
}

/** int git_index_find_prefix(size_t *at_pos, git_index *index, const char *prefix); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniFindPrefix)(JNIEnv *env, jclass obj, jobject outPos, jlong indexPtr, jstring prefix)
{
    char *c_prefix = j_copy_of_jstring(env, prefix, true);
    size_t at_pos;
    int e = git_index_find_prefix(&at_pos, (git_index *)indexPtr, c_prefix);
    (*env)->CallVoidMethod(env, outPos, jniConstants->midAtomicIntSet, (jint)at_pos);
    free(c_prefix);
    return e;
}

/** int git_index_conflict_add(git_index *index, const git_index_entry *ancestor_entry, const git_index_entry *our_entry, const git_index_entry *their_entry); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictAdd)(JNIEnv *env, jclass obj, jlong indexPtr, jlong ancestorEntryPtr, jlong outEntryPtr, jlong theirEntryPtr)
{
    return git_index_conflict_add((git_index *)indexPtr,
                                  (git_index_entry *)ancestorEntryPtr,
                                  (git_index_entry *)outEntryPtr,
                                  (git_index_entry *)theirEntryPtr);
}

/** int git_index_conflict_get(const git_index_entry **ancestor_out, const git_index_entry **our_out, const git_index_entry **their_out, git_index *index, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictGet)(JNIEnv *env, jclass obj, jobject ancestorOut, jobject ourOut, jobject theirOut, jlong indexPtr, jstring path)
{
    const git_index_entry *ancestor_out, *our_out, *their_out;
    char *c_path = j_copy_of_jstring(env, path, true);
    int e = git_index_conflict_get(&ancestor_out, &our_out, &their_out, (git_index *)indexPtr, c_path);
    free(c_path);
    j_atomic_long_set(env, (long)ancestor_out, ancestorOut);
    j_atomic_long_set(env, (long)our_out, ourOut);
    j_atomic_long_set(env, (long)their_out, theirOut);
    return e;
}

/** int git_index_conflict_remove(git_index *index, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictRemove)(JNIEnv *env, jclass obj, jlong indexPtr, jstring path)
{
    char *c_path = j_copy_of_jstring(env, path, true);
    int e = git_index_conflict_remove((git_index *)indexPtr, c_path);
    free(c_path);
    return e;
}

/** int git_index_conflict_cleanup(git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictCleanup)(JNIEnv *env, jclass obj, jlong indexPtr)
{
    return git_index_conflict_cleanup((git_index *)indexPtr);
}

/** int git_index_has_conflicts(const git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniHasConflicts)(JNIEnv *env, jclass obj, jlong indexPtr)
{
    return git_index_has_conflicts((git_index *)indexPtr);
}

/** int git_index_conflict_iterator_new(git_index_conflict_iterator **iterator_out, git_index *index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictIteratorNew)(JNIEnv *env, jclass obj, jobject outIterPtr, jlong indexPtr)
{
    git_index_conflict_iterator *iterator_out = 0;
    int e = git_index_conflict_iterator_new(&iterator_out, (git_index *)indexPtr);
    (*env)->CallVoidMethod(env, outIterPtr, jniConstants->midAtomicLongSet, (jlong)iterator_out);
    return e;
}

/** int git_index_conflict_next(const git_index_entry **ancestor_out, const git_index_entry **our_out, const git_index_entry **their_out, git_index_conflict_iterator *iterator); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictNext)(JNIEnv *env, jclass obj, jobject ancestorOut, jobject ourOut, jobject theirOut, jlong iterPtr)
{
    const git_index_entry *ancestor_out = 0;
    const git_index_entry *our_out = 0;
    const git_index_entry *their_out = 0;
    int e = git_index_conflict_next(&ancestor_out, &our_out, &their_out, (git_index_conflict_iterator *)iterPtr);
    (*env)->CallVoidMethod(env, ancestorOut, jniConstants->midAtomicLongSet, (jlong)ancestor_out);
    (*env)->CallVoidMethod(env, ourOut, jniConstants->midAtomicLongSet, (jlong)our_out);
    (*env)->CallVoidMethod(env, theirOut, jniConstants->midAtomicLongSet, (jlong)their_out);
    return e;
}
/** void git_index_conflict_iterator_free(git_index_conflict_iterator *iterator); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniConflictIteratorFree)(JNIEnv *env, jclass obj, jlong iterPtr)
{
    git_index_conflict_iterator_free((git_index_conflict_iterator *)iterPtr);
}

/** -------- git_index_entry ---------- */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniEntryNew)(JNIEnv *env, jclass obj)
{
    git_index_entry *entry = (git_index_entry *)malloc(sizeof(git_index_entry));
    entry->path = NULL;
    return (jlong)entry;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntryFree)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    git_index_entry *entry = (git_index_entry *)entryPtr;
    free((char *)entry->path);
    free(entry);
}

/** int ctime_seconds*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniEntryGetCtimeSeconds)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return ((git_index_entry *)entryPtr)->ctime.seconds;
}

/** int ctime_nanoseconds*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniEntryGetCtimeNanoseconds)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return ((git_index_entry *)entryPtr)->ctime.nanoseconds;
}

/** int mtime_seconds*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniEntryGetMtimeSeconds)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return ((git_index_entry *)entryPtr)->mtime.seconds;
}

/** int mtime_nanoseconds*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniEntryGetMtimeNanoseconds)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return ((git_index_entry *)entryPtr)->mtime.nanoseconds;
}

/** uint32_t dev*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetDev)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return ((git_index_entry *)entryPtr)->dev;
}

/** uint32_t ino*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetIno)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return ((git_index_entry *)entryPtr)->ino;
}

/** uint32_t mode*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetMode)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return ((git_index_entry *)entryPtr)->mode;
}

/** uint32_t uid*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetUid)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return ((git_index_entry *)entryPtr)->uid;
}

/** uint32_t gid*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetGid)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return ((git_index_entry *)entryPtr)->gid;
}

/** uint32_t file_size*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetFileSize)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return ((git_index_entry *)entryPtr)->file_size;
}

/** git_oid id*/
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Index_jniEntryGetId)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return j_git_oid_to_bytearray(env, &(((git_index_entry *)entryPtr)->id));
}

/** uint16_t flags*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetFlags)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return ((git_index_entry *)entryPtr)->flags;
}

/** uint16_t flags_extended*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetFlagsExtended)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return ((git_index_entry *)entryPtr)->flags_extended;
}

/** const char *path*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Index_jniEntryGetPath)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return (*env)->NewStringUTF(env, ((git_index_entry *)entryPtr)->path);
}

/** int ctime_seconds*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetCtimeSeconds)(JNIEnv *env, jclass obj, jlong entryPtr, jlong ctimeSeconds)
{
    ((git_index_entry *)entryPtr)->ctime.seconds = (int32_t)ctimeSeconds;
}

/** int ctime_nanoseconds*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetCtimeNanoseconds)(JNIEnv *env, jclass obj, jlong entryPtr, jlong ctimeNanoseconds)
{
    ((git_index_entry *)entryPtr)->ctime.nanoseconds = (uint32_t)ctimeNanoseconds;
}

/** int mtime_seconds*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetMtimeSeconds)(JNIEnv *env, jclass obj, jlong entryPtr, jlong mtimeSeconds)
{
    ((git_index_entry *)entryPtr)->mtime.seconds = (int32_t)mtimeSeconds;
}

/** int mtime_nanoseconds*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetMtimeNanoseconds)(JNIEnv *env, jclass obj, jlong entryPtr, jlong mtimeNanoseconds)
{
    ((git_index_entry *)entryPtr)->mtime.nanoseconds = (uint32_t)mtimeNanoseconds;
}

/** uint32_t dev*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetDev)(JNIEnv *env, jclass obj, jlong entryPtr, jint dev)
{
    ((git_index_entry *)entryPtr)->dev = (uint32_t)dev;
}

/** uint32_t ino*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetIno)(JNIEnv *env, jclass obj, jlong entryPtr, jint ino)
{
    ((git_index_entry *)entryPtr)->ino = (uint32_t)ino;
}

/** uint32_t mode*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetMode)(JNIEnv *env, jclass obj, jlong entryPtr, jint mode)
{
    ((git_index_entry *)entryPtr)->mode = (uint32_t)mode;
}

/** uint32_t uid*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetUid)(JNIEnv *env, jclass obj, jlong entryPtr, jint uid)
{
    ((git_index_entry *)entryPtr)->uid = (uint32_t)uid;
}

/** uint32_t gid*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetGid)(JNIEnv *env, jclass obj, jlong entryPtr, jint gid)
{
    ((git_index_entry *)entryPtr)->gid = (uint32_t)gid;
}

/** uint32_t file_size*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetFileSize)(JNIEnv *env, jclass obj, jlong entryPtr, jint fileSize)
{
    ((git_index_entry *)entryPtr)->file_size = (uint32_t)fileSize;
}

/** git_oid id*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetId)(JNIEnv *env, jclass obj, jlong entryPtr, jobject id)
{
    j_git_oid_from_java(env, id, &(((git_index_entry *)entryPtr)->id));
}

/** uint16_t flags*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetFlags)(JNIEnv *env, jclass obj, jlong entryPtr, jint flags)
{
    ((git_index_entry *)entryPtr)->flags = (uint16_t)flags;
}

/** uint16_t flags_extended*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetFlagsExtended)(JNIEnv *env, jclass obj, jlong entryPtr, jint flagsExtended)
{
    ((git_index_entry *)entryPtr)->flags_extended = (uint16_t)flagsExtended;
}

/** const char *path*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetPath)(JNIEnv *env, jclass obj, jlong entryPtr, jstring path)
{
    ((git_index_entry *)entryPtr)->path = j_copy_of_jstring(env, path, false);
}
