#include "j_common.h"

#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_INDEX_H__
#define __GIT24J_INDEX_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** standard callback that forwards c-callback to java.
     * c: standard_matched_cb(path, pathspec, ...)
     * java: class IndexMachedCallback extends BiConsumer<String, String> {
     *     @override
     *     public void accept(String path, String pathspec) {
     *     }
     * }
     *
     * jniAddAll(indexPtr.get(), new String[]{"foo/", "bar/"}, 0, (path, pathSpec) -> { ... })
     */
    int standard_matched_cb(const char *path, const char *matched_pathspec, void *payload);

    /***********************************/
    /** int git_index_open(git_index **out, const char *index_path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniOpen)(JNIEnv *env, jclass obj, jobject outIndexPtr, jstring indexPath);
    /** int git_index_new(git_index **out); */
    /** void git_index_free(git_index *index); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniFree)(JNIEnv *env, jclass obj, jlong index);

    /** git_repository * git_index_owner(const git_index *index); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniOwner)(JNIEnv *env, jclass obj, jlong indexPtr);
    /** int git_index_caps(const git_index *index); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniCaps)(JNIEnv *env, jclass obj, jlong idxPtr);
    /** int git_index_set_caps(git_index *index, int caps); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniSetCaps)(JNIEnv *env, jclass obj, jlong index, jint caps);
    /** unsigned int git_index_version(git_index *index); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniVersion)(JNIEnv *env, jclass obj, jlong idxPtr);
    /** int git_index_set_version(git_index *index, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniSetVersion)(JNIEnv *env, jclass obj, jlong idxPtr, jint version);

    /** int git_index_read(git_index *index, int force); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniRead)(JNIEnv *env, jclass obj, jlong indexPtr, jint force);
    /** int git_index_write(git_index *index); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniWrite)(JNIEnv *env, jclass obj, jlong index);

    /** const char * git_index_path(const git_index *index); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Index_jniPath)(JNIEnv *env, jclass obj, jlong idxPtr);
    /** const git_oid * git_index_checksum(git_index *index); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Index_jniChecksum)(JNIEnv *env, jclass obj, jlong indexPtr);

    /** int git_index_read_tree(git_index *index, const git_tree *tree); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniReadTree)(JNIEnv *env, jclass obj, jlong indexPtr, jlong treePtr);
    /** int git_index_write_tree(git_oid *out, git_index *index); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniWriteTree)(JNIEnv *env, jclass obj, jobject outOid, jlong indexPtr);
    /** int git_index_write_tree_to(git_oid *out, git_index *index, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniWriteTreeTo)(JNIEnv *env, jclass obj, jobject outOid, jlong indexPtr, jlong repoPtr);

    /** size_t git_index_entrycount(const git_index *index); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryCount)(JNIEnv *env, jclass obj, jlong indexPtr);
    /** int git_index_clear(git_index *index); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniClear)(JNIEnv *env, jclass obj, jlong indexPtr);

    /** const git_index_entry * git_index_get_byindex(git_index *index, size_t n); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniGetByIndex)(JNIEnv *env, jclass obj, jlong indexPtr, jint n);
    /** const git_index_entry * git_index_get_bypath(git_index *index, const char *path, int stage); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniGetByPath)(JNIEnv *env, jclass obj, jlong indexPtr, jstring path, jint stage);
    /** int git_index_remove(git_index *index, const char *path, int stage); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniRemove)(JNIEnv *env, jclass obj, jlong indexPtr, jstring path, jint stage);
    /** int git_index_remove_directory(git_index *index, const char *dir, int stage); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniRemoveDirectory)(JNIEnv *env, jclass obj, jlong indexPtr, jstring dir, jint stage);

    /** int git_index_add(git_index *index, const git_index_entry *source_entry); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAdd)(JNIEnv *env, jclass obj, jlong index, jlong entryPtr);
    /** int git_index_entry_stage(const git_index_entry *entry); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryStage)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** int git_index_entry_is_conflict(const git_index_entry *entry); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryIsConflict)(JNIEnv *env, jclass obj, jlong entryPtr);

    /** int git_index_iterator_new(git_index_iterator **iterator_out, git_index *index); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniIteratorNew)(JNIEnv *env, jclass obj, jobject outIterPtr, jlong indexptr);
    /** int git_index_iterator_next(const git_index_entry **out, git_index_iterator *iterator); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniIteratorNext)(JNIEnv *env, jclass obj, jobject outEntryPtr, jlong iterPtr);
    /** void git_index_iterator_free(git_index_iterator *iterator); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniIteratorFree)(JNIEnv *env, jclass obj, jlong iterPtr);

    /** int git_index_add_bypath(git_index *index, const char *path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAddByPath)(JNIEnv *env, jclass obj, jlong index, jstring path);

    /** int git_index_add_from_buffer(git_index *index, const git_index_entry *entry, const void *buffer, size_t len); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAddFromBuffer)(JNIEnv *env, jclass obj, jlong indexPtr, jlong entryPtr, jbyteArray buffer);
    /** int git_index_remove_bypath(git_index *index, const char *path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniRemoveByPath)(JNIEnv *env, jclass obj, jlong indexPtr, jstring path);
    /** int git_index_add_all(git_index *index, const git_strarray *pathspec, unsigned int flags, git_index_matched_path_cb callback, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAddAll)(JNIEnv *env, jclass obj, jlong index, jobjectArray pathspec, jint flags, jobject biConsumer);

    /** int git_index_remove_all(git_index *index, const git_strarray *pathspec, git_index_matched_path_cb callback, void *payload); */
    /** int git_index_update_all(git_index *index, const git_strarray *pathspec, git_index_matched_path_cb callback, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniUpdateAll)(JNIEnv *env, jclass obj, jlong index, jobjectArray pathspec, jobject biConsumer);

    /** int git_index_find(size_t *at_pos, git_index *index, const char *path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniFind)(JNIEnv *env, jclass obj, jobject outPos, jlong indexPtr, jstring path);

    /** int git_index_find_prefix(size_t *at_pos, git_index *index, const char *prefix); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniFindPrefix)(JNIEnv *env, jclass obj, jobject outPos, jlong indexPtr, jstring prefix);

    /** int git_index_conflict_add(git_index *index, const git_index_entry *ancestor_entry, const git_index_entry *our_entry, const git_index_entry *their_entry); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictAdd)(JNIEnv *env, jclass obj, jlong indexPtr, jlong ancestorEntryPtr, jlong outEntryPtr, jlong theirEntryPtr);

    /** int git_index_conflict_get(const git_index_entry **ancestor_out, const git_index_entry **our_out, const git_index_entry **their_out, git_index *index, const char *path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictGet)(JNIEnv *env, jclass obj, jobject ancestorOut, jobject ourOut, jobject theirOut, jlong indexPtr, jstring path);

    /** int git_index_conflict_remove(git_index *index, const char *path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictRemove)(JNIEnv *env, jclass obj, jlong indexPtr, jstring path);
    /** int git_index_conflict_cleanup(git_index *index); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictCleanup)(JNIEnv *env, jclass obj, jlong indexPtr);
    /** int git_index_has_conflicts(const git_index *index); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniHasConflicts)(JNIEnv *env, jclass obj, jlong indexPtr);

    /** int git_index_conflict_iterator_new(git_index_conflict_iterator **iterator_out, git_index *index); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictIteratorNew)(JNIEnv *env, jclass obj, jobject outIterPtr, jlong indexPtr);
    /** int git_index_conflict_next(const git_index_entry **ancestor_out, const git_index_entry **our_out, const git_index_entry **their_out, git_index_conflict_iterator *iterator); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniConflictNext)(JNIEnv *env, jclass obj, jobject ancestorOut, jobject ourOut, jobject theirOut, jlong iterPtr);
    /** void git_index_conflict_iterator_free(git_index_conflict_iterator *iterator); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniConflictIteratorFree)(JNIEnv *env, jclass obj, jlong iterPtr);

    /** -------- git_index_entry ---------- */
    /** int ctime_seconds*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniEntryNew)(JNIEnv *env, jclass obj);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntryFree)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** int ctime_seconds*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniEntryGetCtimeSeconds)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** int ctime_nanoseconds*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniEntryGetCtimeNanoseconds)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** int mtime_seconds*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniEntryGetMtimeSeconds)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** int mtime_nanoseconds*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Index_jniEntryGetMtimeNanoseconds)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** uint32_t dev*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetDev)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** uint32_t ino*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetIno)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** uint32_t mode*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetMode)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** uint32_t uid*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetUid)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** uint32_t gid*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetGid)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** uint32_t file_size*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetFileSize)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** git_oid id*/
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Index_jniEntryGetId)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** uint16_t flags*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetFlags)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** uint16_t flags_extended*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniEntryGetFlagsExtended)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** const char *path*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Index_jniEntryGetPath)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** int ctime_seconds*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetCtimeSeconds)(JNIEnv *env, jclass obj, jlong entryPtr, jlong ctimeSeconds);
    /** int ctime_nanoseconds*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetCtimeNanoseconds)(JNIEnv *env, jclass obj, jlong entryPtr, jlong ctimeNanoseconds);
    /** int mtime_seconds*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetMtimeSeconds)(JNIEnv *env, jclass obj, jlong entryPtr, jlong mtimeSeconds);
    /** int mtime_nanoseconds*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetMtimeNanoseconds)(JNIEnv *env, jclass obj, jlong entryPtr, jlong mtimeNanoseconds);
    /** uint32_t dev*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetDev)(JNIEnv *env, jclass obj, jlong entryPtr, jint dev);
    /** uint32_t ino*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetIno)(JNIEnv *env, jclass obj, jlong entryPtr, jint ino);
    /** uint32_t mode*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetMode)(JNIEnv *env, jclass obj, jlong entryPtr, jint mode);
    /** uint32_t uid*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetUid)(JNIEnv *env, jclass obj, jlong entryPtr, jint uid);
    /** uint32_t gid*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetGid)(JNIEnv *env, jclass obj, jlong entryPtr, jint gid);
    /** uint32_t file_size*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetFileSize)(JNIEnv *env, jclass obj, jlong entryPtr, jint fileSize);
    /** git_oid id*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetId)(JNIEnv *env, jclass obj, jlong entryPtr, jobject id);
    /** uint16_t flags*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetFlags)(JNIEnv *env, jclass obj, jlong entryPtr, jint flags);
    /** uint16_t flags_extended*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetFlagsExtended)(JNIEnv *env, jclass obj, jlong entryPtr, jint flagsExtended);
    /** const char *path*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniEntrySetPath)(JNIEnv *env, jclass obj, jlong entryPtr, jstring path);

#ifdef __cplusplus
}
#endif
#endif
