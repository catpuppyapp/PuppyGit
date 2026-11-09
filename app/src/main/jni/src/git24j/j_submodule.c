#include "j_submodule.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>

int j_git_submodule_cb(git_submodule *sm, const char *name, void *payload)
{
    assert(payload && "j_git_submodule_cb must be called with payload");
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = getEnv();
    jstring jName = (*env)->NewStringUTF(env, name);
    int r = (*env)->CallIntMethod(env, j_payload->callback, j_payload->mid, jName, (jlong)sm);
    (*env)->DeleteLocalRef(env, jName);
    return r;
}

// no matching type found for 'git_submodule_cb callback'
/** int git_submodule_foreach(git_repository *repo, git_submodule_cb callback, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jobject foreachCb)
{
    assert(foreachCb && "Call Submodule::foreach with empty callback does not make any sense");
    j_cb_payload payload ={ 0 };
    j_cb_payload_init(env, &payload, foreachCb, "(Ljava/lang/String;J)I");
    int r = git_submodule_foreach((git_repository *)repoPtr, j_git_submodule_cb, &payload);
    j_cb_payload_release(env, &payload);
    return r;
}

/** -------- Wrapper Body ---------- */
/** int git_submodule_add_finalize(git_submodule *submodule); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniAddFinalize)(JNIEnv *env, jclass obj, jlong submodulePtr)
{
    int r = git_submodule_add_finalize((git_submodule *)submodulePtr);
    return r;
}

/** int git_submodule_add_setup(git_submodule **out, git_repository *repo, const char *url, const char *path, int use_gitlink); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniAddSetup)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring url, jstring path, jint use_gitlink)
{
    git_submodule *c_out = 0;
    char *c_url = j_copy_of_jstring(env, url, true);
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_submodule_add_setup(&c_out, (git_repository *)repoPtr, c_url, c_path, use_gitlink);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_url);
    free(c_path);
    return r;
}

/** int git_submodule_add_to_index(git_submodule *submodule, int write_index); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniAddToIndex)(JNIEnv *env, jclass obj, jlong submodulePtr, jint write_index)
{
    int r = git_submodule_add_to_index((git_submodule *)submodulePtr, write_index);
    return r;
}

/** const char * git_submodule_branch(git_submodule *submodule); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Submodule_jniBranch)(JNIEnv *env, jclass obj, jlong submodulePtr)
{
    const char *r = git_submodule_branch((git_submodule *)submodulePtr);
    return (*env)->NewStringUTF(env, r);
}

/** git_submodule_recurse_t git_submodule_fetch_recurse_submodules(git_submodule *submodule); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniFetchRecurseSubmodules)(JNIEnv *env, jclass obj, jlong submodulePtr)
{
    git_submodule_recurse_t r = git_submodule_fetch_recurse_submodules((git_submodule *)submodulePtr);
    return r;
}

/** void git_submodule_free(git_submodule *submodule); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Submodule_jniFree)(JNIEnv *env, jclass obj, jlong submodulePtr)
{
    git_submodule_free((git_submodule *)submodulePtr);
}

/** const git_oid * git_submodule_head_id(git_submodule *submodule); */
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Submodule_jniHeadId)(JNIEnv *env, jclass obj, jlong submodulePtr)
{
    const git_oid *c_oid = git_submodule_head_id((git_submodule *)submodulePtr);
    return j_git_oid_to_bytearray(env, c_oid);
}

/** git_submodule_ignore_t git_submodule_ignore(git_submodule *submodule); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniIgnore)(JNIEnv *env, jclass obj, jlong submodulePtr)
{
    git_submodule_ignore_t r = git_submodule_ignore((git_submodule *)submodulePtr);
    return r;
}

/** const git_oid * git_submodule_index_id(git_submodule *submodule); */
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Submodule_jniIndexId)(JNIEnv *env, jclass obj, jlong submodulePtr)
{
    const git_oid *c_oid = git_submodule_index_id((git_submodule *)submodulePtr);
    return j_git_oid_to_bytearray(env, c_oid);
}

/** int git_submodule_init(git_submodule *submodule, int overwrite); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniInit)(JNIEnv *env, jclass obj, jlong submodulePtr, jint overwrite)
{
    int r = git_submodule_init((git_submodule *)submodulePtr, overwrite);
    return r;
}

/** int git_submodule_location(unsigned int *location_status, git_submodule *submodule); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniLocation)(JNIEnv *env, jclass obj, jobject locationStatus, jlong submodulePtr)
{
    unsigned int c_location_status;
    int r = git_submodule_location(&c_location_status, (git_submodule *)submodulePtr);
    (*env)->CallVoidMethod(env, locationStatus, jniConstants->midAtomicIntSet, c_location_status);
    return r;
}

/** int git_submodule_lookup(git_submodule **out, git_repository *repo, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniLookup)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name)
{
    git_submodule *c_out = 0;
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_submodule_lookup(&c_out, (git_repository *)repoPtr, c_name);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_name);
    return r;
}

/** const char * git_submodule_name(git_submodule *submodule); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Submodule_jniName)(JNIEnv *env, jclass obj, jlong submodulePtr)
{
    git_submodule *sm = (git_submodule *)submodulePtr;
    const char *r = git_submodule_name(sm);
    return (*env)->NewStringUTF(env, r);
}

/** int git_submodule_open(git_repository **repo, git_submodule *submodule); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniOpen)(JNIEnv *env, jclass obj, jobject repo, jlong submodulePtr)
{
    git_repository *c_repo = 0;
    int r = git_submodule_open(&c_repo, (git_submodule *)submodulePtr);
    (*env)->CallVoidMethod(env, repo, jniConstants->midAtomicLongSet, (jlong)c_repo);
    return r;
}

/** git_repository * git_submodule_owner(git_submodule *submodule); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Submodule_jniOwner)(JNIEnv *env, jclass obj, jlong submodulePtr)
{
    git_repository *r = git_submodule_owner((git_submodule *)submodulePtr);
    return (jlong)r;
}

/** const char * git_submodule_path(git_submodule *submodule); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Submodule_jniPath)(JNIEnv *env, jclass obj, jlong submodulePtr)
{
    const char *r = git_submodule_path((git_submodule *)submodulePtr);
    return (*env)->NewStringUTF(env, r);
}

/** int git_submodule_reload(git_submodule *submodule, int force); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniReload)(JNIEnv *env, jclass obj, jlong submodulePtr, jint force)
{
    int r = git_submodule_reload((git_submodule *)submodulePtr, force);
    return r;
}

/** int git_submodule_repo_init(git_repository **out, const git_submodule *sm, int use_gitlink); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniRepoInit)(JNIEnv *env, jclass obj, jobject out, jlong smPtr, jint use_gitlink)
{
    git_repository *c_out = 0;
    int r = git_submodule_repo_init(&c_out, (git_submodule *)smPtr, use_gitlink);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** int git_submodule_resolve_url(git_buf *out, git_repository *repo, const char *url); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniResolveUrl)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring url)
{
    git_buf c_out ={ 0 };
    char *c_url = j_copy_of_jstring(env, url, true);
    int r = git_submodule_resolve_url(&c_out, (git_repository *)repoPtr, c_url);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    free(c_url);
    return r;
}

/** int git_submodule_set_branch(git_repository *repo, const char *name, const char *branch); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSetBranch)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jstring branch)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_branch = j_copy_of_jstring(env, branch, true);
    int r = git_submodule_set_branch((git_repository *)repoPtr, c_name, c_branch);
    free(c_name);
    free(c_branch);
    return r;
}

/** int git_submodule_set_fetch_recurse_submodules(git_repository *repo, const char *name, git_submodule_recurse_t fetch_recurse_submodules); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSetFetchRecurseSubmodules)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jint fetchRecurseSubmodules)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_submodule_set_fetch_recurse_submodules((git_repository *)repoPtr, c_name, fetchRecurseSubmodules);
    free(c_name);
    return r;
}

/** int git_submodule_set_ignore(git_repository *repo, const char *name, git_submodule_ignore_t ignore); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSetIgnore)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jint ignore)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_submodule_set_ignore((git_repository *)repoPtr, c_name, ignore);
    free(c_name);
    return r;
}

/** int git_submodule_set_update(git_repository *repo, const char *name, git_submodule_update_t update); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSetUpdate)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jint update)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_submodule_set_update((git_repository *)repoPtr, c_name, update);
    free(c_name);
    return r;
}

/** int git_submodule_set_url(git_repository *repo, const char *name, const char *url); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSetUrl)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jstring url)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_url = j_copy_of_jstring(env, url, true);
    int r = git_submodule_set_url((git_repository *)repoPtr, c_name, c_url);
    free(c_name);
    free(c_url);
    return r;
}

/** int git_submodule_status(unsigned int *status, git_repository *repo, const char *name, git_submodule_ignore_t ignore); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniStatus)(JNIEnv *env, jclass obj, jobject status, jlong repoPtr, jstring name, jint ignore)
{
    unsigned int c_status;
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_submodule_status(&c_status, (git_repository *)repoPtr, c_name, ignore);
    (*env)->CallVoidMethod(env, status, jniConstants->midAtomicIntSet, c_status);
    free(c_name);
    return r;
}

/** int git_submodule_sync(git_submodule *submodule); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniSync)(JNIEnv *env, jclass obj, jlong submodulePtr)
{
    int r = git_submodule_sync((git_submodule *)submodulePtr);
    return r;
}

/** int git_submodule_update(git_submodule *submodule, int init, git_submodule_update_options *options); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniUpdate)(JNIEnv *env, jclass obj, jlong submodulePtr, jint init, jlong optionsPtr)
{
    int r = git_submodule_update((git_submodule *)submodulePtr, init, (git_submodule_update_options *)optionsPtr);
    return r;
}

/** int git_submodule_update_init_options(git_submodule_update_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniUpdateInitOptions)(JNIEnv *env, jclass obj, jlong updateOptionsPtr, jint version)
{
    int r = git_submodule_update_init_options((git_submodule_update_options *)updateOptionsPtr, version);
    return r;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniUpdateOptionsNew)(JNIEnv *env, jclass obj, jobject outOpt, jint version)
{
    git_submodule_update_options *opts = (git_submodule_update_options *)malloc(sizeof(git_submodule_update_options));
    int r = git_submodule_update_init_options(opts, version);
    (*env)->CallVoidMethod(env, outOpt, jniConstants->midAtomicLongSet, (jlong)opts);
    return r;
}
/** unsigned int version*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniUpdateOptionsGetVersion)(JNIEnv *env, jclass obj, jlong updateOptionsPtr)
{
    return ((git_submodule_update_options *)updateOptionsPtr)->version;
}

/** git_checkout_options *checkout_opts*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Submodule_jniUpdateOptionsGetCheckoutOpts)(JNIEnv *env, jclass obj, jlong updateOptionsPtr)
{
    return (jlong) & (((git_submodule_update_options *)updateOptionsPtr)->checkout_opts);
}

/** git_fetch_options *fetch_opts*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Submodule_jniUpdateOptionsGetFetchOpts)(JNIEnv *env, jclass obj, jlong updateOptionsPtr)
{
    return (jlong) & (((git_submodule_update_options *)updateOptionsPtr)->fetch_opts);
}

/** int allow_fetch*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniUpdateOptionsGetAllowFetch)(JNIEnv *env, jclass obj, jlong updateOptionsPtr)
{
    return ((git_submodule_update_options *)updateOptionsPtr)->allow_fetch;
}

/** int allow_fetch*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Submodule_jniUpdateOptionsSetAllowFetch)(JNIEnv *env, jclass obj, jlong updateOptionsPtr, jint allowFetch)
{
    ((git_submodule_update_options *)updateOptionsPtr)->allow_fetch = (int)allowFetch;
}


/** git_submodule_update_t git_submodule_update_strategy(git_submodule *submodule); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniUpdateStrategy)(JNIEnv *env, jclass obj, jlong submodulePtr)
{
    git_submodule_update_t r = git_submodule_update_strategy((git_submodule *)submodulePtr);
    return r;
}

/** const char * git_submodule_url(git_submodule *submodule); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Submodule_jniUrl)(JNIEnv *env, jclass obj, jlong submodulePtr)
{
    const char *r = git_submodule_url((git_submodule *)submodulePtr);
    return r ? (*env)->NewStringUTF(env, r) : NULL;
}

/** const git_oid * git_submodule_wd_id(git_submodule *submodule); */
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Submodule_jniWdId)(JNIEnv *env, jclass obj, jlong submodulePtr)
{
    const git_oid *c_oid = git_submodule_wd_id((git_submodule *)submodulePtr);
    return j_git_oid_to_bytearray(env, c_oid);
}

/** int git_submodule_clone(git_repository **out, git_submodule *submodule, const git_submodule_update_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Submodule_jniClone)(JNIEnv *env, jclass obj, jobject out, jlong submodulePtr, jlong optsPtr)
{
    git_repository *c_out;
    int r = git_submodule_clone(&c_out, (git_submodule *)submodulePtr, (git_submodule_update_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}
