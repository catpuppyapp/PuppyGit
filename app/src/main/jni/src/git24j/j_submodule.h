#include "j_common.h"
#include "j_mappers.h"
#include <assert.h>
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_SUBMODULE_H__
#define __GIT24J_SUBMODULE_H__
#ifdef __cplusplus
extern "C"
{
    #endif

    int j_git_submodule_cb(git_submodule *sm, const char *name, void *payload);
    // no matching type found for 'git_submodule_cb callback'
    /** int git_submodule_foreach(git_repository *repo, git_submodule_cb callback, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jobject foreachCb);

    /** -------- Signature of the header ---------- */
    /** int git_submodule_add_finalize(git_submodule *submodule); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniAddFinalize)(JNIEnv *env, jclass obj, jlong submodulePtr);

    /** int git_submodule_add_setup(git_submodule **out, git_repository *repo, const char *url, const char *path, int use_gitlink); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniAddSetup)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring url, jstring path, jint use_gitlink);

    /** int git_submodule_add_to_index(git_submodule *submodule, int write_index); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniAddToIndex)(JNIEnv *env, jclass obj, jlong submodulePtr, jint write_index);

    /** const char * git_submodule_branch(git_submodule *submodule); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Submodule_jniBranch)(JNIEnv *env, jclass obj, jlong submodulePtr);

    /** git_submodule_recurse_t git_submodule_fetch_recurse_submodules(git_submodule *submodule); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniFetchRecurseSubmodules)(JNIEnv *env, jclass obj, jlong submodulePtr);

    /** void git_submodule_free(git_submodule *submodule); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Submodule_jniFree)(JNIEnv *env, jclass obj, jlong submodulePtr);

    /** const git_oid * git_submodule_head_id(git_submodule *submodule); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Submodule_jniHeadId)(JNIEnv *env, jclass obj, jlong submodulePtr);

    /** git_submodule_ignore_t git_submodule_ignore(git_submodule *submodule); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniIgnore)(JNIEnv *env, jclass obj, jlong submodulePtr);

    /** const git_oid * git_submodule_index_id(git_submodule *submodule); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Submodule_jniIndexId)(JNIEnv *env, jclass obj, jlong submodulePtr);

    /** int git_submodule_init(git_submodule *submodule, int overwrite); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniInit)(JNIEnv *env, jclass obj, jlong submodulePtr, jint overwrite);

    /** int git_submodule_location(unsigned int *location_status, git_submodule *submodule); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniLocation)(JNIEnv *env, jclass obj, jobject locationStatus, jlong submodulePtr);

    /** int git_submodule_lookup(git_submodule **out, git_repository *repo, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniLookup)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name);

    /** const char * git_submodule_name(git_submodule *submodule); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Submodule_jniName)(JNIEnv *env, jclass obj, jlong submodulePtr);

    /** int git_submodule_open(git_repository **repo, git_submodule *submodule); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniOpen)(JNIEnv *env, jclass obj, jobject repo, jlong submodulePtr);

    /** git_repository * git_submodule_owner(git_submodule *submodule); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Submodule_jniOwner)(JNIEnv *env, jclass obj, jlong submodulePtr);

    /** const char * git_submodule_path(git_submodule *submodule); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Submodule_jniPath)(JNIEnv *env, jclass obj, jlong submodulePtr);

    /** int git_submodule_reload(git_submodule *submodule, int force); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniReload)(JNIEnv *env, jclass obj, jlong submodulePtr, jint force);

    /** int git_submodule_repo_init(git_repository **out, const git_submodule *sm, int use_gitlink); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniRepoInit)(JNIEnv *env, jclass obj, jobject out, jlong smPtr, jint use_gitlink);

    /** int git_submodule_resolve_url(git_buf *out, git_repository *repo, const char *url); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniResolveUrl)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring url);

    /** int git_submodule_set_branch(git_repository *repo, const char *name, const char *branch); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSetBranch)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jstring branch);

    /** int git_submodule_set_fetch_recurse_submodules(git_repository *repo, const char *name, git_submodule_recurse_t fetch_recurse_submodules); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSetFetchRecurseSubmodules)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jint fetchRecurseSubmodules);

    /** int git_submodule_set_ignore(git_repository *repo, const char *name, git_submodule_ignore_t ignore); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSetIgnore)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jint ignore);

    /** int git_submodule_set_update(git_repository *repo, const char *name, git_submodule_update_t update); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSetUpdate)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jint update);

    /** int git_submodule_set_url(git_repository *repo, const char *name, const char *url); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSetUrl)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jstring url);

    /** int git_submodule_status(unsigned int *status, git_repository *repo, const char *name, git_submodule_ignore_t ignore); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniStatus)(JNIEnv *env, jclass obj, jobject status, jlong repoPtr, jstring name, jint ignore);

    /** int git_submodule_sync(git_submodule *submodule); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSync)(JNIEnv *env, jclass obj, jlong submodulePtr);

    /** int git_submodule_update(git_submodule *submodule, int init, git_submodule_update_options *options); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniUpdate)(JNIEnv *env, jclass obj, jlong submodulePtr, jint init, jlong optionsPtr);

    /** int git_submodule_update_init_options(git_submodule_update_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniUpdateInitOptions)(JNIEnv *env, jclass obj, jlong updateOptionsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniUpdateOptionsNew)(JNIEnv *env, jclass obj, jobject outOpt, jint version);
    /** git_checkout_options *checkout_opts*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Submodule_jniUpdateOptionsGetCheckoutOpts)(JNIEnv *env, jclass obj, jlong updateOptionsPtr);
    /** git_fetch_options *fetch_opts*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Submodule_jniUpdateOptionsGetFetchOpts)(JNIEnv *env, jclass obj, jlong updateOptionsPtr);
    /** int allow_fetch*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniUpdateOptionsGetAllowFetch)(JNIEnv *env, jclass obj, jlong updateOptionsPtr);
    /** int allow_fetch*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Submodule_jniUpdateOptionsSetAllowFetch)(JNIEnv *env, jclass obj, jlong updateOptionsPtr, jint allowFetch);

    /** git_submodule_update_t git_submodule_update_strategy(git_submodule *submodule); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniUpdateStrategy)(JNIEnv *env, jclass obj, jlong submodulePtr);

    /** const char * git_submodule_url(git_submodule *submodule); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Submodule_jniUrl)(JNIEnv *env, jclass obj, jlong submodulePtr);

    /** const git_oid * git_submodule_wd_id(git_submodule *submodule); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Submodule_jniWdId)(JNIEnv *env, jclass obj, jlong submodulePtr);

    /** int git_submodule_clone(git_repository **out, git_submodule *submodule, const git_submodule_update_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniClone)(JNIEnv *env, jclass obj, jobject out, jlong submodulePtr, jlong optsPtr);


    #ifdef __cplusplus
}
#endif
#endif