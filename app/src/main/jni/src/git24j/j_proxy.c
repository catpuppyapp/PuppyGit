#include "j_proxy.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>

int j_proxy_git_credential_acquire_cb(git_credential **out, const char *url, const char *username_from_url, unsigned int allowed_types, void *payload)
{
    assert(payload && "git_credential_acquire_cb must be called with payload");
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = getEnv();
    jstring jUrl = (*env)->NewStringUTF(env, url);
    jstring jUsernameFromUrl = (*env)->NewStringUTF(env, username_from_url);
    /** 0 for success, < 0 to indicate an error, > 0 to indicate no credential was acquired and ptr returned */
    long r = (*env)->CallLongMethod(env, j_payload->callback, j_payload->mid, jUrl, jUsernameFromUrl, (jint)allowed_types);
    (*env)->DeleteLocalRef(env, jUrl);
    (*env)->DeleteLocalRef(env, jUsernameFromUrl);
    if (r > 0)
    {
        *out = (git_credential *)r;
        r = 0;
    }
    return r;
}

int j_proxy_git_transport_certificate_check_cb(struct git_cert *cert, int valid, const char *host, void *payload)
{
    assert(payload && "git_credential_acquire_cb must be called with payload");
    j_cb_payload *payloadArr = (j_cb_payload *)payload;
    j_cb_payload *j_payload = &(payloadArr[1]);
    JNIEnv *env = getEnv();
    jstring jHost = (*env)->NewStringUTF(env, host);
    int r = (*env)->CallIntMethod(env, j_payload->callback, j_payload->mid, (jlong)cert, valid, jHost);
    (*env)->DeleteLocalRef(env, jHost);
    return r;
}

void j_git_proxy_options_init_payload(git_proxy_options *opts)
{
    j_cb_payload *payloads = (j_cb_payload *)malloc(sizeof(j_cb_payload) * 2);
    payloads[0].callback = NULL;
    payloads[1].callback = NULL;
    opts->payload = payloads;
}
/** -------- proxy_options ---------- */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Proxy_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version)
{
    git_proxy_options *opts = (git_proxy_options *)malloc(sizeof(git_proxy_options));
    int r = git_proxy_init_options(opts, version);
    opts->url = NULL;
    opts->payload = NULL;
    j_git_proxy_options_init_payload(opts);
    (*env)->CallVoidMethod(env, outPtr, jniConstants->midAtomicLongSet, (jlong)opts);
    return r;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Proxy_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    git_proxy_options *opts = (git_proxy_options *)optsPtr;
    j_cb_payload *payloads = (j_cb_payload *)opts->payload;
    if (payloads)
    {
        j_cb_payload_release(env, &(payloads[0]));
        j_cb_payload_release(env, &(payloads[1]));
    }
    free((char *)opts->url);
    opts->url = NULL;
    opts->credentials = NULL;
    opts->certificate_check = NULL;
    free(opts->payload);
    opts->payload = NULL;
    free(opts);
}
/** int version*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Proxy_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_proxy_options *)optionsPtr)->version;
}

/** int type*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Proxy_jniOptionsGetType)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_proxy_options *)optionsPtr)->type;
}

/** const char *url*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Proxy_jniOptionsGetUrl)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return (*env)->NewStringUTF(env, ((git_proxy_options *)optionsPtr)->url);
}

/** SKIPPED (getter) void *payload*/

/** int version*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Proxy_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr, jint version)
{
    ((git_proxy_options *)optionsPtr)->version = (int)version;
}

/** int type*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Proxy_jniOptionsSetType)(JNIEnv *env, jclass obj, jlong optionsPtr, jint type)
{
    ((git_proxy_options *)optionsPtr)->type = (int)type;
}

/** const char *url*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Proxy_jniOptionsSetUrl)(JNIEnv *env, jclass obj, jlong optionsPtr, jstring url)
{
    ((git_proxy_options *)optionsPtr)->url = j_copy_of_jstring(env, url, true);
}

/** git_credential_acquire_cb credentials*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Proxy_jniOptionsSetCredentials)(JNIEnv *env, jclass obj, jlong optionsPtr, jobject credentials)
{
    git_proxy_options *opts = (git_proxy_options *)optionsPtr;
    assert(opts && "git_proxy_options was not initialized");
    if (opts->payload == NULL)
    {
        j_git_proxy_options_init_payload(opts);
    }
    j_cb_payload *payload = (j_cb_payload *)opts->payload;
    j_cb_payload_release(env, payload); /**first release old payload */
    j_cb_payload_init(env, payload, credentials, "(Ljava/lang/String;Ljava/lang/String;I)J");
    opts->credentials = j_proxy_git_credential_acquire_cb;
}

/** git_transport_certificate_check_cb certificate_check*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Proxy_jniOptionsSetCertificateCheck)(JNIEnv *env, jclass obj, jlong optionsPtr, jobject certificateCheck)
{
    git_proxy_options *opts = (git_proxy_options *)optionsPtr;
    assert(opts && "git_proxy_options was not initialized");
    if (opts->payload == NULL)
    {
        j_git_proxy_options_init_payload(opts);
    }
    j_cb_payload *payload_arr = (j_cb_payload *)opts->payload;
    j_cb_payload *payload = &(payload_arr[1]);
    j_cb_payload_release(env, payload);
    j_cb_payload_init(env, payload, certificateCheck, "(JILjava/lang/String;)I");
    opts->certificate_check = j_proxy_git_transport_certificate_check_cb;
}

/** run some callback tests. */
JNIEXPORT void JNICALL J_MAKE_METHOD(Proxy_jniOptionsTestCallbacks)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    git_proxy_options *opts = (git_proxy_options *)optsPtr;
    git_credential *out;
    opts->credentials(&out, "url-test", "username_from_url-test", 1, opts->payload);
    opts->certificate_check(NULL, 1, "host-test", opts->payload);
}
