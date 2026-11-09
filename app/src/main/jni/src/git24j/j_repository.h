#include "j_common.h"
#include <jni.h>

#ifndef __GIT24J_REPOSITORY_H__
#define __GIT24J_REPOSITORY_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_repository_open(git_repository **out, const char *path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniOpen)(JNIEnv *env, jclass obj, jobject ptrReceiver, jstring path);

    /** int git_repository_open_from_worktree(git_repository **out, git_worktree *wt); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniOpenFromWorkTree)(JNIEnv *env, jclass obj, jobject ptrReceiver, jlong wtPtr);

    /** int git_repository_wrap_odb(git_repository **out, git_odb *odb); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniWrapOdb)(JNIEnv *env, jclass obj, jobject ptrReceiver, jlong odbPtr);

    /** int git_repository_discover(git_buf *out, const char *start_path, int across_fs, const char *ceiling_dirs); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniDiscover)(JNIEnv *env, jclass obj, jobject buf, jstring startPath, jint acrossFs, jstring ceilingDirs);

    /** int git_repository_open_ext(git_repository **out, const char *path, unsigned int flags, const char *ceiling_dirs); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniOpenExt)(JNIEnv *env, jclass obj, jobject ptrReceiver, jstring path, jint flags, jstring ceilingDirs);

    /** int git_repository_open_bare(git_repository **out, const char *bare_path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniOpenBare)(JNIEnv *env, jclass obj, jobject ptrReceiver, jstring path);

    /** void git_repository_free(git_repository *repo); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Repository_jniFree)(JNIEnv *env, jclass obj, jlong repo);

    /** int git_repository_init(git_repository **out, const char *path, unsigned int is_bare); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniInit)(JNIEnv *env, jclass obj, jobject outRepo, jstring path, jint isBare);

    /** int git_repository_init_options_init(git_repository_init_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniInitOptionsInit)(JNIEnv *env, jclass obj, jobject initOpts, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniInitOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);

    /** int git_repository_init_ext(git_repository **out, const char *repo_path, git_repository_init_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniInitExt)(JNIEnv *env, jclass obj, jobject outRepo, jstring repoPath, jobject initOpts);

    /** int git_repository_head(git_reference **out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniHead)(JNIEnv *env, jclass obj, jobject outRef, jlong repo);

    /** int git_repository_head_for_worktree(git_reference **out, git_repository *repo, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniHeadForWorktree)(JNIEnv *env, jclass obj, jobject outRef, jlong repo, jstring name);

    /** int git_repository_head_detached(git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniHeadDetached)(JNIEnv *env, jclass obj, jlong repo);

    /** int git_repository_head_unborn(git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniHeadUnborn)(JNIEnv *env, jclass obj, jlong repo);

    /** int git_repository_is_empty(git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIsEmpty)(JNIEnv *env, jclass obj, jlong repo);

    /** int git_repository_item_path(git_buf *out, const git_repository *repo, git_repository_item_t item); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniItemPath)(JNIEnv *env, jclass obj, jobject outBuf, jlong repo, jint item);

    /** int git_repository_index(git_index **out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIndex)(JNIEnv *env, jclass obj, jobject outIndex, jlong repo);

    /** const char * git_repository_path(const git_repository *repo); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Repository_jniPath)(JNIEnv *env, jclass obj, jlong repoPtr);

    /** const char * git_repository_workdir(const git_repository *repo); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Repository_jniWorkdir)(JNIEnv *env, jclass obj, jlong repoPtr);

    /** const char * git_repository_commondir(const git_repository *repo); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Repository_jniCommondir)(JNIEnv *env, jclass obj, jlong repoPtr);

    /** int git_repository_set_workdir(git_repository *repo, const char *workdir, int update_gitlink); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetWorkdir)(JNIEnv *env, jclass obj, jlong repoPtr, jstring workdir, jint updateGitlink);

    /** int git_repository_is_bare(const git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIsBare)(JNIEnv *env, jclass obj, jlong repoPtr);

    /** int git_repository_is_worktree(const git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIsWorktree)(JNIEnv *env, jclass obj, jlong repoPtr);

    /** int git_repository_config(git_config **out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniConfig)(JNIEnv *env, jclass obj, jobject outConfig, jlong repoPtr);

    /** int git_repository_config_snapshot(git_config **out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniConfigSnapshot)(JNIEnv *env, jclass obj, jobject outConfig, jlong repoPtr);

    /** int git_repository_odb(git_odb **out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniOdb)(JNIEnv *env, jclass obj, jobject outOdb, jlong repoPtr);

    /** int git_repository_refdb(git_refdb **out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniRefdb)(JNIEnv *env, jclass obj, jobject outRefdb, jlong repoPtr);

    /** int git_repository_index(git_index **out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIndex)(JNIEnv *env, jclass obj, jobject outIndex, jlong repoPtr);

    /** int git_repository_message(git_buf *out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniMessage)(JNIEnv *env, jclass obj, jobject outBuf, jlong repoPtr);

    /** int git_repository_message_remove(git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniMessageRemove)(JNIEnv *env, jclass obj, jlong repoPtr);

    /** int git_repository_state_cleanup(git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniStateCleanup)(JNIEnv *env, jclass obj, jlong repoPtr);

    /** int git_repository_fetchhead_foreach(git_repository *repo, git_repository_fetchhead_foreach_cb callback, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniFetchheadForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jobject consumer);

    /** int git_repository_mergehead_foreach(git_repository *repo, git_repository_mergehead_foreach_cb callback, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniMergeheadForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jobject consumer);

    /** int git_repository_hashfile(git_oid *out, git_repository *repo, const char *path, git_object_t type, const char *as_path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniHashfile)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring path, jint type, jstring asPath);

    /** int git_repository_set_head(git_repository *repo, const char *refname); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetHead)(JNIEnv *env, jclass obj, jlong repoPtr, jstring refName);

    /** int git_repository_set_head_detached(git_repository *repo, const git_oid *commitish); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetHeadDetached)(JNIEnv *env, jclass obj, jlong repoPtr, jobject oid);

    /** int git_repository_set_head_detached_from_annotated(git_repository *repo, const git_annotated_commit *commitish); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetHeadDetachedFromAnnotated)(JNIEnv *env, jclass obj, jlong repoPtr, jlong commitishPtr);

    /** int git_repository_detach_head(git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniDetachHead)(JNIEnv *env, jclass obj, jlong repoPtr);

    /** int git_repository_state(git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniState)(JNIEnv *env, jclass obj, jlong repoPtr);

    /** int git_repository_set_namespace(git_repository *repo, const char *nmspace); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetNamespace)(JNIEnv *env, jclass obj, jlong repoPtr, jstring nmspace);

    /** const char * git_repository_get_namespace(git_repository *repo); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Repository_jniGetNamespace)(JNIEnv *env, jclass obj, jlong repoPtr);

    /** int git_repository_is_shallow(git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIsShallow)(JNIEnv *env, jclass obj, jlong repoPtr);

    /** int git_repository_ident(const char **name, const char **email, const git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIdent)(JNIEnv *env, jclass obj, jobject identity, jlong repoPtr);

    /** int git_repository_set_ident(git_repository *repo, const char *name, const char *email); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetIdent)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jstring email);

    /** int git_repository_create_cb(git_repository **out, const char *path, int bare, void *payload); */
    /** int git_repository_fetchhead_foreach_cb(const char *ref_name, const char *remote_url, const git_oid *oid, unsigned int is_merge, void *payload); */
    /** int git_repository_mergehead_foreach_cb(const git_oid *oid, void *payload); */

#ifdef __cplusplus
}
#endif
#endif
