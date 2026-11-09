#include "j_tree.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** int git_treewalk_cb(const char *root, const git_tree_entry *entry, void *payload); */
int j_git_treewalk_cb(const char *root, const git_tree_entry *entry, void *payload)
{
    assert(payload && "git_treewalk_cb must be called with payload");
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = getEnv();
    jstring jRoot = (*env)->NewStringUTF(env, root);
    int r = (*env)->CallIntMethod(env, j_payload->callback, j_payload->mid, jRoot, (jlong)entry);
    (*env)->DeleteLocalRef(env, jRoot);
    return r;
}

/** int git_treebuilder_filter_cb(const git_tree_entry *entry, void *payload); */
int j_git_treebuilder_filter_cb(const git_tree_entry *entry, void *payload)
{
    assert(payload && "j_git_treebuilder_filter_cb must be called with payload");
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = getEnv();
    return (*env)->CallIntMethod(env, j_payload->callback, j_payload->mid, (jlong)entry);
}

// no matching type found for 'git_filemode_t filemode'
/** int git_treebuilder_insert(const git_tree_entry **out, git_treebuilder *bld, const char *filename, const git_oid *id, git_filemode_t filemode); */
// no matching type found for 'git_treewalk_cb callback'
/** int git_tree_walk(const git_tree *tree, git_treewalk_mode mode, git_treewalk_cb callback, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniWalk)(JNIEnv *env, jclass obj, jlong treePtr, jint mode, jobject callback)
{
    assert(callback && "walk without callback does not make any sense");
    j_cb_payload payload = {0};
    j_cb_payload_init(env, &payload, callback, "(Ljava/lang/String;J)I");
    int r = git_tree_walk((git_tree *)treePtr, (git_treewalk_mode)mode, j_git_treewalk_cb, &payload);
    j_cb_payload_release(env, &payload);
    return r;
}

/** -------- Wrapper Body ---------- */
/** int git_tree_lookup(git_tree **out, git_repository *repo, const git_oid *id); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniLookup)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jobject id)
{
    git_tree *c_out = 0;
    git_oid c_id;
    j_git_oid_from_java(env, id, &c_id);
    int r = git_tree_lookup(&c_out, (git_repository *)repoPtr, &c_id);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    /* git_tree_free(c_out); */
    return r;
}

/** void git_tree_free(git_tree *tree); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Tree_jniFree)(JNIEnv *env, jclass obj, jlong treePtr)
{
    git_tree_free((git_tree *)treePtr);
}

/** size_t git_tree_entrycount(const git_tree *tree); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntrycount)(JNIEnv *env, jclass obj, jlong treePtr)
{
    size_t r = git_tree_entrycount((git_tree *)treePtr);
    return r;
}

/** const git_tree_entry * git_tree_entry_byname(const git_tree *tree, const char *filename); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Tree_jniEntryByname)(JNIEnv *env, jclass obj, jlong treePtr, jstring filename)
{
    char *c_filename = j_copy_of_jstring(env, filename, true);
    const git_tree_entry *r = git_tree_entry_byname((git_tree *)treePtr, c_filename);
    free(c_filename);
    return (jlong)r;
}

/** const git_tree_entry * git_tree_entry_byindex(const git_tree *tree, size_t idx); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Tree_jniEntryByindex)(JNIEnv *env, jclass obj, jlong treePtr, jint idx)
{
    const git_tree_entry *r = git_tree_entry_byindex((git_tree *)treePtr, idx);
    return (jlong)r;
}

/** const git_tree_entry * git_tree_entry_byid(const git_tree *tree, const git_oid *id); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Tree_jniEntryByid)(JNIEnv *env, jclass obj, jlong treePtr, jobject id)
{
    git_oid c_id;
    j_git_oid_from_java(env, id, &c_id);
    const git_tree_entry *r = git_tree_entry_byid((git_tree *)treePtr, &c_id);
    return (jlong)r;
}

/** int git_tree_entry_bypath(git_tree_entry **out, const git_tree *root, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryBypath)(JNIEnv *env, jclass obj, jobject out, jlong rootPtr, jstring path)
{
    git_tree_entry *c_out = 0;
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_tree_entry_bypath(&c_out, (git_tree *)rootPtr, c_path);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_tree_entry_free(c_out); */
    free(c_path);
    return r;
}

/** int git_tree_entry_dup(git_tree_entry **dest, const git_tree_entry *source); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryDup)(JNIEnv *env, jclass obj, jobject dest, jlong sourcePtr)
{
    git_tree_entry *c_dest = 0;
    int r = git_tree_entry_dup(&c_dest, (git_tree_entry *)sourcePtr);
    (*env)->CallVoidMethod(env, dest, jniConstants->midAtomicLongSet, (jlong)c_dest);
    /* git_tree_entry_free(c_dest); */
    return r;
}

/** void git_tree_entry_free(git_tree_entry *entry); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Tree_jniEntryFree)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    git_tree_entry_free((git_tree_entry *)entryPtr);
}

/** const char * git_tree_entry_name(const git_tree_entry *entry); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Tree_jniEntryName)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    const char *r = git_tree_entry_name((git_tree_entry *)entryPtr);
    return (*env)->NewStringUTF(env, r);
}

/** const git_oid * git_tree_entry_id(const git_tree_entry *entry); */
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Tree_jniEntryId)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    const git_oid *r = git_tree_entry_id((git_tree_entry *)entryPtr);
    return j_git_oid_to_bytearray(env, r);
}

/** git_object_t git_tree_entry_type(const git_tree_entry *entry); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryType)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    git_object_t r = git_tree_entry_type((git_tree_entry *)entryPtr);
    return r;
}

/** git_filemode_t git_tree_entry_filemode(const git_tree_entry *entry); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryFilemode)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    git_filemode_t r = git_tree_entry_filemode((git_tree_entry *)entryPtr);
    return r;
}

/** git_filemode_t git_tree_entry_filemode_raw(const git_tree_entry *entry); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryFilemodeRaw)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    git_filemode_t r = git_tree_entry_filemode_raw((git_tree_entry *)entryPtr);
    return r;
}

/** int git_tree_entry_cmp(const git_tree_entry *e1, const git_tree_entry *e2); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryCmp)(JNIEnv *env, jclass obj, jlong e1Ptr, jlong e2Ptr)
{
    int r = git_tree_entry_cmp((git_tree_entry *)e1Ptr, (git_tree_entry *)e2Ptr);
    return r;
}

/** int git_tree_entry_to_object(git_object **object_out, git_repository *repo, const git_tree_entry *entry); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniEntryToObject)(JNIEnv *env, jclass obj, jobject objectOut, jlong repoPtr, jlong entryPtr)
{
    git_object *c_object_out = 0;
    int r = git_tree_entry_to_object(&c_object_out, (git_repository *)repoPtr, (git_tree_entry *)entryPtr);
    (*env)->CallVoidMethod(env, objectOut, jniConstants->midAtomicLongSet, (jlong)c_object_out);
    /* git_object_free(c_object_out); */
    return r;
}

/** int git_tree_dup(git_tree **out, git_tree *source); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniDup)(JNIEnv *env, jclass obj, jobject out, jlong sourcePtr)
{
    git_tree *c_out = 0;
    int r = git_tree_dup(&c_out, (git_tree *)sourcePtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_tree_free(c_out); */
    return r;
}

/** int git_tree_create_updated(git_oid *out, git_repository *repo, git_tree *baseline, size_t nupdates, const git_tree_update *updates); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniCreateUpdated)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong baselinePtr, jlongArray updates)
{
    jsize nupdates = (*env)->GetArrayLength(env, updates);
    jlong *x = (*env)->GetLongArrayElements(env, updates, 0);
    git_tree_update *c_updates = (git_tree_update *)malloc(sizeof(git_tree_update) * nupdates);
    for (jsize i = 0; i < nupdates; i++)
    {
        //I am not sure this is a good copy or isn't
        c_updates[i] = *((git_tree_update *)x[i]);
        /* c_updates[i].action = x->action;
        c_updates[i].filemode = x->filemode;
        git_oid_cpy(&(c_updates[i].id), &(x->id));
        c_updates[i].path = x->path; */
    }
    git_oid c_out;
    int r = git_tree_create_updated(&c_out, (git_repository *)repoPtr, (git_tree *)baselinePtr, nupdates, c_updates);
    j_git_oid_to_java(env, &c_out, out);

    free(c_updates);
    (*env)->ReleaseLongArrayElements(env, updates, x, 0);

    return r;
}

/** create new git_tree_update object, return pointer as long, user must free the struct later. */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Tree_jniUpdateNew)(JNIEnv *env, jclass obj, jint updateType, jobject oid, jint filemodeType, jstring path)
{
    git_tree_update *update = (git_tree_update *)malloc(sizeof(git_tree_update));
    update->action = (git_tree_update_t)updateType;
    update->filemode = (git_filemode_t)filemodeType;
    j_git_oid_from_java(env, oid, &update->id);
    update->path = j_copy_of_jstring(env, path, true);
    return (jlong)update;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Tree_jniUpdateFree)(JNIEnv *env, jclass obj, jlong updatePtr)
{
    if (!updatePtr)
    {
        return;
    }
    git_tree_update *update = (git_tree_update *)updatePtr;
    free((void *)update->path);
    free(update);
}

// no matching type found for 'git_treebuilder_filter_cb filter'
/** int git_treebuilder_filter(git_tree_builder *bld, git_treebuilder_filter_cb filter, void *payload); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Tree_jniBuilderFilter)(JNIEnv *env, jclass obj, jlong bldPtr, jobject callback)
{
    j_cb_payload payload = {0};
    j_cb_payload_init(env, &payload, callback, "(J)I");
    git_treebuilder_filter((git_treebuilder *)bldPtr, j_git_treebuilder_filter_cb, &payload);
    j_cb_payload_release(env, &payload);
}

/** -------- Wrapper Body ---------- */
/** int git_treebuilder_new(git_tree_builder **out, git_repository *repo, const git_tree *source); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong sourcePtr)
{
    git_treebuilder *c_out = 0;
    int r = git_treebuilder_new(&c_out, (git_repository *)repoPtr, (git_tree *)sourcePtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_treebuilder_free(c_out); */
    return r;
}

/** int git_treebuilder_clear(git_tree_builder *bld); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderClear)(JNIEnv *env, jclass obj, jlong bldPtr)
{
    return git_treebuilder_clear((git_treebuilder *)bldPtr);
}

/** size_t git_treebuilder_entrycount(git_tree_builder *bld); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderEntrycount)(JNIEnv *env, jclass obj, jlong bldPtr)
{
    size_t r = git_treebuilder_entrycount((git_treebuilder *)bldPtr);
    return r;
}

/** void git_treebuilder_free(git_tree_builder *bld); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Tree_jniBuilderFree)(JNIEnv *env, jclass obj, jlong bldPtr)
{
    git_treebuilder_free((git_treebuilder *)bldPtr);
}

/** const git_tree_entry * git_treebuilder_get(git_tree_builder *bld, const char *filename); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Tree_jniBuilderGet)(JNIEnv *env, jclass obj, jlong bldPtr, jstring filename)
{
    char *c_filename = j_copy_of_jstring(env, filename, true);
    const git_tree_entry *r = git_treebuilder_get((git_treebuilder *)bldPtr, c_filename);
    free(c_filename);
    return (jlong)r;
}

/** int git_treebuilder_insert(const git_tree_entry **out, git_treebuilder *bld, const char *filename, const git_oid *id, git_filemode_t filemode); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderInsert)(JNIEnv *env, jclass obj, jobject out, jlong bldPtr, jstring filename, jobject id, jint filemode)
{
    const git_tree_entry *c_out = 0;
    char *c_filename = j_copy_of_jstring(env, filename, true);
    git_oid c_id;
    j_git_oid_from_java(env, id, &c_id);
    int r = git_treebuilder_insert(&c_out, (git_treebuilder *)bldPtr, c_filename, &c_id, filemode);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    free(c_filename);
    return r;
}

/** int git_treebuilder_remove(git_tree_builder *bld, const char *filename); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderRemove)(JNIEnv *env, jclass obj, jlong bldPtr, jstring filename)
{
    char *c_filename = j_copy_of_jstring(env, filename, true);
    int r = git_treebuilder_remove((git_treebuilder *)bldPtr, c_filename);
    free(c_filename);
    return r;
}

/** int git_treebuilder_write(git_oid *id, git_tree_builder *bld); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderWrite)(JNIEnv *env, jclass obj, jobject id, jlong bldPtr)
{
    git_oid c_id;
    int r = git_treebuilder_write(&c_id, (git_treebuilder *)bldPtr);
    j_git_oid_to_java(env, &c_id, id);
    return r;
}

/** int git_treebuilder_write_with_buffer(git_oid *oid, git_tree_builder *bld, git_buf *tree); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tree_jniBuilderWriteWithBuffer)(JNIEnv *env, jclass obj, jobject oid, jlong bldPtr, jobject tree)
{
    git_oid c_oid;
    git_buf c_tree = {0};
    int r = git_treebuilder_write_with_buffer(&c_oid, (git_treebuilder *)bldPtr, &c_tree);
    j_git_oid_to_java(env, &c_oid, oid);
    j_git_buf_to_java(env, &c_tree, tree);
    git_buf_dispose(&c_tree);
    return r;
}
