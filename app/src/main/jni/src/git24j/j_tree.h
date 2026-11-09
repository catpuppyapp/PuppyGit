#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_TREE_H__
#define __GIT24J_TREE_H__
#ifdef __cplusplus
extern "C"
{
#endif

    // no matching type found for 'git_filemode_t filemode'
    /** int git_treebuilder_insert(const git_tree_entry **out, git_treebuilder *bld, const char *filename, const git_oid *id, git_filemode_t filemode); */
    // no matching type found for 'git_treewalk_cb callback'
    /** int git_tree_walk(const git_tree *tree, git_treewalk_mode mode, git_treewalk_cb callback, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniWalk)(JNIEnv *env, jclass obj, jlong treePtr, jint mode, jobject callback);
    /** -------- Signature of the header ---------- */
    /** int git_tree_lookup(git_tree **out, git_repository *repo, const git_oid *id); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniLookup)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jobject id);

    /** int git_tree_lookup_prefix(git_tree **out, git_repository *repo, const git_oid *id, size_t len); */

    /** void git_tree_free(git_tree *tree); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Tree_jniFree)(JNIEnv *env, jclass obj, jlong treePtr);

    /** size_t git_tree_entrycount(const git_tree *tree); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntrycount)(JNIEnv *env, jclass obj, jlong treePtr);

    /** const git_tree_entry * git_tree_entry_byname(const git_tree *tree, const char *filename); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Tree_jniEntryByname)(JNIEnv *env, jclass obj, jlong treePtr, jstring filename);

    /** const git_tree_entry * git_tree_entry_byindex(const git_tree *tree, size_t idx); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Tree_jniEntryByindex)(JNIEnv *env, jclass obj, jlong treePtr, jint idx);

    /** const git_tree_entry * git_tree_entry_byid(const git_tree *tree, const git_oid *id); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Tree_jniEntryByid)(JNIEnv *env, jclass obj, jlong treePtr, jobject id);

    /** int git_tree_entry_bypath(git_tree_entry **out, const git_tree *root, const char *path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryBypath)(JNIEnv *env, jclass obj, jobject out, jlong rootPtr, jstring path);

    /** int git_tree_entry_dup(git_tree_entry **dest, const git_tree_entry *source); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryDup)(JNIEnv *env, jclass obj, jobject dest, jlong sourcePtr);

    /** void git_tree_entry_free(git_tree_entry *entry); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Tree_jniEntryFree)(JNIEnv *env, jclass obj, jlong entryPtr);

    /** const char * git_tree_entry_name(const git_tree_entry *entry); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Tree_jniEntryName)(JNIEnv *env, jclass obj, jlong entryPtr);

    /** const git_oid * git_tree_entry_id(const git_tree_entry *entry); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Tree_jniEntryId)(JNIEnv *env, jclass obj, jlong entryPtr);

    /** git_object_t git_tree_entry_type(const git_tree_entry *entry); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryType)(JNIEnv *env, jclass obj, jlong entryPtr);

    /** git_filemode_t git_tree_entry_filemode(const git_tree_entry *entry); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryFilemode)(JNIEnv *env, jclass obj, jlong entryPtr);

    /** git_filemode_t git_tree_entry_filemode_raw(const git_tree_entry *entry); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryFilemodeRaw)(JNIEnv *env, jclass obj, jlong entryPtr);

    /** int git_tree_entry_cmp(const git_tree_entry *e1, const git_tree_entry *e2); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryCmp)(JNIEnv *env, jclass obj, jlong e1Ptr, jlong e2Ptr);

    /** int git_tree_entry_to_object(git_object **object_out, git_repository *repo, const git_tree_entry *entry); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryToObject)(JNIEnv *env, jclass obj, jobject objectOut, jlong repoPtr, jlong entryPtr);

    /** int git_tree_dup(git_tree **out, git_tree *source); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniDup)(JNIEnv *env, jclass obj, jobject out, jlong sourcePtr);

    /** int git_tree_create_updated(git_oid *out, git_repository *repo, git_tree *baseline, size_t nupdates, const git_tree_update *updates); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniCreateUpdated)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong baselinePtr, jlongArray updates);

    /** create new git_tree_update object, return pointer as long, user must free the struct later. */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Tree_jniUpdateNew)(JNIEnv *env, jclass obj, jint updateType, jobject oid, jint filemodeType, jstring path);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Tree_jniUpdateFree)(JNIEnv *env, jclass obj, jlong updatePtr);

    // no matching type found for 'git_treebuilder_filter_cb filter'
    /** void git_treebuilder_filter(git_tree_builder *bld, git_treebuilder_filter_cb filter, void *payload); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Tree_jniBuilderFilter)(JNIEnv *env, jclass obj, jlong bldPtr, jobject callback);
    /** -------- Signature of the header ---------- */
    /** int git_treebuilder_new(git_tree_builder **out, git_repository *repo, const git_tree *source); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong sourcePtr);

    /** int git_treebuilder_clear(git_tree_builder *bld); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderClear)(JNIEnv *env, jclass obj, jlong bldPtr);

    /** size_t git_treebuilder_entrycount(git_tree_builder *bld); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderEntrycount)(JNIEnv *env, jclass obj, jlong bldPtr);

    /** void git_treebuilder_free(git_tree_builder *bld); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Tree_jniBuilderFree)(JNIEnv *env, jclass obj, jlong bldPtr);

    /** const git_tree_entry * git_treebuilder_get(git_tree_builder *bld, const char *filename); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Tree_jniBuilderGet)(JNIEnv *env, jclass obj, jlong bldPtr, jstring filename);

    /** int git_treebuilder_insert(const git_tree_entry **out, git_tree_builder *bld, const char *filename, const git_oid *id, git_filemode_t filemode); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderInsert)(JNIEnv *env, jclass obj, jobject out, jlong bldPtr, jstring filename, jobject id, jint filemode);

    /** int git_treebuilder_remove(git_tree_builder *bld, const char *filename); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderRemove)(JNIEnv *env, jclass obj, jlong bldPtr, jstring filename);

    /** int git_treebuilder_write(git_oid *id, git_tree_builder *bld); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderWrite)(JNIEnv *env, jclass obj, jobject id, jlong bldPtr);

    /** int git_treebuilder_write_with_buffer(git_oid *oid, git_tree_builder *bld, git_buf *tree); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderWriteWithBuffer)(JNIEnv *env, jclass obj, jobject oid, jlong bldPtr, jobject tree);

    /** int git_treebuilder_filter_cb(const git_tree_entry *entry, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderFilterCb)(JNIEnv *env, jclass obj, jlong entryPtr);

#ifdef __cplusplus
}
#endif
#endif