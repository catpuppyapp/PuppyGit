#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_REMOTE_H__
#define __GIT24J_REMOTE_H__
#ifdef __cplusplus
extern "C"
{
#endif

    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniInitCallbackConstant)(JNIEnv *env, jclass obj, jobject callback);

    // no matching type found for 'const git_remote_head ***out'
    /** int git_remote_ls(const git_remote_head ***out, size_t *size, git_remote *remote); */
    /** -------- Signature of the header ---------- */
    /** int git_remote_add_fetch(git_repository *repo, const char *remote, const char *refspec); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniAddFetch)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jstring refspec);

    /** int git_remote_add_push(git_repository *repo, const char *remote, const char *refspec); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniAddPush)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jstring refspec);

    /** git_remote_autotag_option_t git_remote_autotag(const git_remote *remote); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniAutotag)(JNIEnv *env, jclass obj, jlong remotePtr);

    /** int git_remote_connect(git_remote *remote, git_direction direction, const git_remote_callbacks *callbacks, const git_proxy_options *proxy_opts, const git_strarray *custom_headers); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniConnect)(JNIEnv *env, jclass obj, jlong remotePtr, jint direction, jlong callbacksPtr, jlong proxyOptsPtr, jobjectArray customHeaders);

    /** int git_remote_connected(const git_remote *remote); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniConnected)(JNIEnv *env, jclass obj, jlong remotePtr);

    /** int git_remote_create(git_remote **out, git_repository *repo, const char *name, const char *url); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreate)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name, jstring url);

    /** int git_remote_create_anonymous(git_remote **out, git_repository *repo, const char *url); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateAnonymous)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring url);

    /** int git_remote_create_detached(git_remote **out, const char *url); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateDetached)(JNIEnv *env, jclass obj, jobject out, jstring url);

    /** int git_remote_create_init_options(git_remote_create_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** int git_remote_create_with_fetchspec(git_remote **out, git_repository *repo, const char *name, const char *url, const char *fetch); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateWithFetchspec)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name, jstring url, jstring fetch);

    /** int git_remote_create_with_opts(git_remote **out, const char *url, const git_remote_create_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateWithOpts)(JNIEnv *env, jclass obj, jobject out, jstring url, jlong optsPtr);

    /** int git_remote_default_branch(git_buf *out, git_remote *remote); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniDefaultBranch)(JNIEnv *env, jclass obj, jobject out, jlong remotePtr);

    /** int git_remote_delete(git_repository *repo, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniDelete)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name);

    /** void git_remote_disconnect(git_remote *remote); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniDisconnect)(JNIEnv *env, jclass obj, jlong remotePtr);

    /** int git_remote_download(git_remote *remote, const git_strarray *refspecs, const git_fetch_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniDownload)(JNIEnv *env, jclass obj, jlong remotePtr, jobjectArray refspecs, jlong optsPtr);

    /** int git_remote_dup(git_remote **dest, git_remote *source); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniDup)(JNIEnv *env, jclass obj, jobject dest, jlong sourcePtr);

    /** int git_remote_fetch(git_remote *remote, const git_strarray *refspecs, const git_fetch_options *opts, const char *reflog_message); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetch)(JNIEnv *env, jclass obj, jlong remotePtr, jobjectArray refspecs, jlong optsPtr, jstring reflog_message);

    /** void git_remote_free(git_remote *remote); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFree)(JNIEnv *env, jclass obj, jlong remotePtr);

    /** int git_remote_get_fetch_refspecs(git_strarray *array, const git_remote *remote); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniGetFetchRefspecs)(JNIEnv *env, jclass obj, jobject array, jlong remotePtr);

    /** int git_remote_get_push_refspecs(git_strarray *array, const git_remote *remote); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniGetPushRefspecs)(JNIEnv *env, jclass obj, jobject array, jlong remotePtr);

    /** const git_refspec * git_remote_get_refspec(const git_remote *remote, size_t n); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniGetRefspec)(JNIEnv *env, jclass obj, jlong remotePtr, jint n);

    /** int git_remote_init_callbacks(git_remote_callbacks *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniInitCallbacks)(JNIEnv *env, jclass obj, jlong remoteCallbacksPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCallbacksNew)(JNIEnv *env, jclass obj, jobject outCb, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCallbacksFree)(JNIEnv *env, jclass obj, jlong cbsPtr);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCallbacksTest)(JNIEnv *env, jclass obj, jlong cbsPtr, jobject cbsObject);
    typedef enum
    {
        J_REMOTE_CALLBACK_CRED = 0,
        J_REMOTE_CALLBACK_TRANSPORT_MSG,
        J_REMOTE_CALLBACK_COMPLETION,
        J_REMOTE_CALLBACK_CERTIFICATE_CHECK,
        J_REMOTE_CALLBACK_TRANSFER_PROGRESS,
        J_REMOTE_CALLBACK_UPDATE_TIP,
        J_REMOTE_CALLBACK_PACK_PROGRESS,
        J_REMOTE_CALLBACK_PUSH_TRANSFER_PROGRESS,
        J_REMOTE_CALLBACK_PUSH_UPDATE_REFERENCE,
        J_REMOTE_CALLBACK_PUSH_NEGOTIATION,
        J_REMOTE_CALLBACK_TRANSPORT,
        J_REMOTE_CALLBACK_URL_RESOLVE,
    } j_callback_type_t;

    /** set transport_message_cb */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCallbacksSetCallbackObject)(JNIEnv *env, jclass obj, jlong cbsPtr, jobject cbsObject, jint cbt);

    /** int git_remote_is_valid_name(const char *remote_name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniIsValidName)(JNIEnv *env, jclass obj, jstring remote_name);

    /** int git_remote_list(git_strarray *out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniList)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);

    /** int git_remote_lookup(git_remote **out, git_repository *repo, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniLookup)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name);

    /** const char * git_remote_name(const git_remote *remote); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniName)(JNIEnv *env, jclass obj, jlong remotePtr);

    /** git_repository * git_remote_owner(const git_remote *remote); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniOwner)(JNIEnv *env, jclass obj, jlong remotePtr);

    /** int git_remote_prune(git_remote *remote, const git_remote_callbacks *callbacks); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPrune)(JNIEnv *env, jclass obj, jlong remotePtr, jlong callbacksPtr);

    /** int git_remote_prune_refs(const git_remote *remote); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPruneRefs)(JNIEnv *env, jclass obj, jlong remotePtr);

    /** int git_remote_push(git_remote *remote, const git_strarray *refspecs, const git_push_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPush)(JNIEnv *env, jclass obj, jlong remotePtr, jobjectArray refspecs, jlong optsPtr);

    /** const char * git_remote_pushurl(const git_remote *remote); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniPushurl)(JNIEnv *env, jclass obj, jlong remotePtr);

    /** size_t git_remote_refspec_count(const git_remote *remote); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniRefspecCount)(JNIEnv *env, jclass obj, jlong remotePtr);

    /** int git_remote_rename(git_strarray *problems, git_repository *repo, const char *name, const char *new_name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniRename)(JNIEnv *env, jclass obj, jobject problems, jlong repoPtr, jstring name, jstring new_name);

    /** int git_remote_set_autotag(git_repository *repo, const char *remote, git_remote_autotag_option_t value); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniSetAutotag)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jint value);

    /** int git_remote_set_pushurl(git_repository *repo, const char *remote, const char *url); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniSetPushurl)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jstring url);

    /** int git_remote_set_url(git_repository *repo, const char *remote, const char *url); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniSetUrl)(JNIEnv *env, jclass obj, jlong repoPtr, jstring remote, jstring url);

    /** const git_transfer_progress * git_remote_stats(git_remote *remote); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniStats)(JNIEnv *env, jclass obj, jlong remotePtr);

    /** void git_remote_stop(git_remote *remote); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniStop)(JNIEnv *env, jclass obj, jlong remotePtr);

    /** int git_remote_update_tips(git_remote *remote, const git_remote_callbacks *callbacks, int update_fetchhead, git_remote_autotag_option_t download_tags, const char *reflog_message); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniUpdateTips)(JNIEnv *env, jclass obj, jlong remotePtr, jlong callbacksPtr, jint update_fetchhead, jint downloadTags, jstring reflog_message);

    /** int git_remote_upload(git_remote *remote, const git_strarray *refspecs, const git_push_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniUpload)(JNIEnv *env, jclass obj, jlong remotePtr, jobjectArray refspecs, jlong optsPtr);

    /** const char * git_remote_url(const git_remote *remote); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniUrl)(JNIEnv *env, jclass obj, jlong remotePtr);

    /** -------- structure_git_remote_create_options ---------- */
    /** unsigned int version*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsGetVersion)(JNIEnv *env, jclass obj, jlong createOptionsPtr);
    /** git_repository *repository*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsGetRepository)(JNIEnv *env, jclass obj, jlong createOptionsPtr);
    /** const char *name*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsGetName)(JNIEnv *env, jclass obj, jlong createOptionsPtr);
    /** const char *fetchspec*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsGetFetchspec)(JNIEnv *env, jclass obj, jlong createOptionsPtr);
    /** unsigned int flags*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsGetFlags)(JNIEnv *env, jclass obj, jlong createOptionsPtr);
    /** unsigned int version*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsSetVersion)(JNIEnv *env, jclass obj, jlong createOptionsPtr, jint version);
    /** git_repository *repository*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsSetRepository)(JNIEnv *env, jclass obj, jlong createOptionsPtr, jlong repository);
    /** const char *name*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsSetName)(JNIEnv *env, jclass obj, jlong createOptionsPtr, jstring name);
    /** const char *fetchspec*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsSetFetchspec)(JNIEnv *env, jclass obj, jlong createOptionsPtr, jstring fetchspec);
    /** unsigned int flags*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniCreateOptionsSetFlags)(JNIEnv *env, jclass obj, jlong createOptionsPtr, jint flags);

    /** -------- structure_git_remote_fetch_options ---------- */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    /** int version*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetVersion)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr);
    /** git_remote_callbacks callbacks*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetCallbacks)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr);
    /** git_fetch_prune_t prune*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetPrune)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr);
    /** int update_fetchhead*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetUpdateFetchhead)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr);
    /** git_remote_autotag_option_t download_tags*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetDownloadTags)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr);
    /** git_proxy_options proxy_opts*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetProxyOpts)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr);
    /** git_strarray custom_headers*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetCustomHeaders)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jobject outHeadersList);
    /** int version*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetVersion)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint version);
    /** depth (shallow clone) **/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetDepth)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint depth);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetDepth)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr);

    /** follow_redirects **/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetFollowRedirects)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint redirectT);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsGetFollowRedirects)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr);

    /** git_fetch_prune_t prune*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetPrune)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint prune);
    /** int update_fetchhead*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetUpdateFetchhead)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint updateFetchhead);
    /** git_remote_autotag_option_t download_tags*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetDownloadTags)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jint downloadTags);
    /** git_proxy_options proxy_opts*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetProxyOpts)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jlong proxyOptsPtr);
    /** git_strarray custom_headers*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniFetchOptionsSetCustomHeaders)(JNIEnv *env, jclass obj, jlong fetchOptionsPtr, jobjectArray customHeaders);

    /** -------- git_push_options ---------- */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPushOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    /** unsigned int version*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetVersion)(JNIEnv *env, jclass obj, jlong pushOptionsPtr);
    /** unsigned int pb_parallelism*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetPbParallelism)(JNIEnv *env, jclass obj, jlong pushOptionsPtr);
    /** git_remote_callbacks callbacks*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetCallbacks)(JNIEnv *env, jclass obj, jlong pushOptionsPtr);
    /** git_proxy_options proxy_opts*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetProxyOpts)(JNIEnv *env, jclass obj, jlong pushOptionsPtr);
    /** git_strarray custom_headers*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetCustomHeaders)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jobject outHeadersList);
    /** unsigned int version*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsSetVersion)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jint version);
    /** unsigned int pb_parallelism*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsSetPbParallelism)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jint pbParallelism);
    /** git_strarray custom_headers*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsSetCustomHeaders)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jobjectArray customHeaders);

    /** git_strarray remote_push_options*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsSetRemotePushOptions)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jobjectArray remotePushOptions);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushOptionsGetRemotePushOptions)(JNIEnv *env, jclass obj, jlong pushOptionsPtr, jobject outRemotePushOptions);

    /** -------- Signature of the header ---------- */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Remote_jniPushUpdateNew)(JNIEnv *env, jclass obj);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushUpdateFree)(JNIEnv *env, jclass obj, jlong pushUpdatePtr);
    /** char *src_refname*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniPushUpdateGetSrcRefname)(JNIEnv *env, jclass obj, jlong pushUpdatePtr);
    /** char *dst_refname*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Remote_jniPushUpdateGetDstRefname)(JNIEnv *env, jclass obj, jlong pushUpdatePtr);
    /** git_oid src*/
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Remote_jniPushUpdateGetSrc)(JNIEnv *env, jclass obj, jlong pushUpdatePtr);
    /** git_oid dst*/
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Remote_jniPushUpdateGetDst)(JNIEnv *env, jclass obj, jlong pushUpdatePtr);
    /** char *src_refname*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushUpdateSetSrcRefname)(JNIEnv *env, jclass obj, jlong pushUpdatePtr, jstring srcRefname);
    /** char *dst_refname*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushUpdateSetDstRefname)(JNIEnv *env, jclass obj, jlong pushUpdatePtr, jstring dstRefname);
    /** git_oid src*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushUpdateSetSrc)(JNIEnv *env, jclass obj, jlong pushUpdatePtr, jobject src);
    /** git_oid dst*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Remote_jniPushUpdateSetDst)(JNIEnv *env, jclass obj, jlong pushUpdatePtr, jobject dst);

#ifdef __cplusplus
}
#endif
#endif
