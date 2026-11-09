#include "j_clone.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** -------- Wrapper Body ---------- */
/** int git_clone_clone(git_repository **out, const char *url, const char *local_path, const git_clone_options *options); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniClone)(JNIEnv *env, jclass obj, jobject out, jstring url, jstring local_path, jlong optionsPtr)
{
    git_repository *c_out = 0;
    char *c_url = j_copy_of_jstring(env, url, true);
    char *c_local_path = j_copy_of_jstring(env, local_path, true);
    git_clone_options *opts = (git_clone_options *)optionsPtr;
    int r = git_clone(&c_out, c_url, c_local_path, opts);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_url);
    free(c_local_path);
    return r;
}

// setter
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniSetVersion)(JNIEnv *env, jclass obj, jlong clonePtr, jint version)
{
    ((git_clone_options *)clonePtr)->version = version;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsNew)(JNIEnv *env, jclass obj, jint version, jobject outOpts)
{
    git_clone_options *opts = (git_clone_options *)malloc(sizeof(git_clone_options));
    int r = git_clone_init_options(opts, (unsigned int)version);
    (*env)->CallVoidMethod(env, outOpts, jniConstants->midAtomicLongSet, (jlong)opts);
    return r;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    git_clone_options *opts = (git_clone_options *)optionsPtr;
    opts->remote_cb = NULL;
    opts->repository_cb = NULL;
    j_cb_payload_release(env, opts->remote_cb_payload);
    j_cb_payload_release(env, opts->repository_cb_payload);
    free(opts->remote_cb_payload);
    free(opts->repository_cb_payload);
}

int j_git_repository_create_cb(git_repository **out, const char *path, int bare, void *payload)
{
    assert(payload && "jni callback cannot be null");
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    jobject callback = j_payload->callback;
    jmethodID mid = j_payload->mid;
    assert(callback && mid && "callback and payload must not be null");
    JNIEnv *env = getEnv();
    jobject outRepo = (*env)->NewObject(env, jniConstants->clzAtomicLong, jniConstants->midAtomicLongInit);
    jstring jPath = (*env)->NewStringUTF(env, path);
    int r = (*env)->CallIntMethod(env, callback, mid, outRepo, jPath, bare);
    jlong repoPtr = (*env)->CallLongMethod(env, outRepo, jniConstants->midAtomicLongGet);
    *out = (git_repository *)repoPtr;
    (*env)->DeleteLocalRef(env, jPath);
    (*env)->DeleteLocalRef(env, outRepo);
    return r;
}

int j_git_remote_create_cb(git_remote **out, git_repository *repo, const char *name, const char *url, void *payload)
{
    assert(payload && "jni callback cannot be null");
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    jobject callback = j_payload->callback;
    jmethodID mid = j_payload->mid;
    assert(callback && mid && "callback and payload must not be null");
    JNIEnv *env = getEnv();
    jobject outRemote = (*env)->NewObject(env, jniConstants->clzAtomicLong, jniConstants->midAtomicLongInit);
    jstring jName = (*env)->NewStringUTF(env, name);
    jstring jUrl = (*env)->NewStringUTF(env, url);
    int r = (*env)->CallIntMethod(env, callback, mid, outRemote, (jlong)repo, jName, jUrl);
    jlong remotePtr = (*env)->CallLongMethod(env, outRemote, jniConstants->midAtomicLongGet);
    *out = (git_remote *)remotePtr;
    (*env)->DeleteLocalRef(env, jUrl);
    (*env)->DeleteLocalRef(env, jName);
    (*env)->DeleteLocalRef(env, outRemote);
    return r;
}

/** -------- Wrapper Body ---------- */
/** unsigned int version*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_clone_options *)optionsPtr)->version;
}

/** git_checkout_options checkout_opts*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Clone_jniOptionsGetCheckoutOpts)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    git_checkout_options *r = &((git_clone_options *)optionsPtr)->checkout_opts;
    return (jlong)r;
}

/** git_fetch_options fetch_opts*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Clone_jniOptionsGetFetchOpts)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    git_fetch_options *r = &((git_clone_options *)optionsPtr)->fetch_opts;
    return (jlong)r;
}

/** int bare*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsGetBare)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_clone_options *)optionsPtr)->bare;
}

/** git_clone_local_t local*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsGetLocal)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_clone_options *)optionsPtr)->local;
}

/** const char* checkout_branch*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Clone_jniOptionsGetCheckoutBranch)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    const char *r = ((git_clone_options *)optionsPtr)->checkout_branch;
    return (*env)->NewStringUTF(env, r);
}

/** const char* checkout_branch*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetCheckoutBranch)(JNIEnv *env, jclass obj, jlong optionsPtr, jstring branch)
{
    const char *c_branch = j_copy_of_jstring(env, branch, true);

    ((git_clone_options *)optionsPtr)->checkout_branch = c_branch;

    //need not free c_branch, because checkout_branch point to it
}

/** unsigned int version*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr, jint version)
{
    ((git_clone_options *)optionsPtr)->version = (unsigned int)version;
}

/** SKIPPED: git_checkout_options checkout_opts*/
/** SKIPPED: git_fetch_options fetch_opts*/

/** int bare*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetBare)(JNIEnv *env, jclass obj, jlong optionsPtr, jint bare)
{
    ((git_clone_options *)optionsPtr)->bare = (int)bare;
}

/** git_clone_local_t local*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetLocal)(JNIEnv *env, jclass obj, jlong optionsPtr, jint local)
{
    ((git_clone_options *)optionsPtr)->local = (git_clone_local_t)local;
}

/** git_repository_create_cb repository_cb*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetRepositoryCb)(JNIEnv *env, jclass obj, jlong optionsPtr, jobject repositoryCb)
{
    git_clone_options *opts = (git_clone_options *)optionsPtr;
    j_cb_payload *payload = (j_cb_payload *)malloc(sizeof(j_cb_payload));
    j_cb_payload_init(env, payload, repositoryCb, "(Ljava/util/concurrent/atomic/AtomicLong;Ljava/lang/String;I)I");
    opts->repository_cb_payload = payload;
    opts->repository_cb = j_git_repository_create_cb;
}

/** git_remote_create_cb remote_cb*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetRemoteCb)(JNIEnv *env, jclass obj, jlong optionsPtr, jobject remoteCb)
{
    git_clone_options *opts = (git_clone_options *)optionsPtr;
    j_cb_payload *payload = (j_cb_payload *)malloc(sizeof(j_cb_payload));
    j_cb_payload_init(env, payload, remoteCb, "(Ljava/util/concurrent/atomic/AtomicLong;JLjava/lang/String;Ljava/lang/String;)I");
    opts->remote_cb_payload = payload;
    opts->remote_cb = j_git_remote_create_cb;
}
