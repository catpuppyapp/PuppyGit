#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_PROXY_H__
#define __GIT24J_PROXY_H__
#ifdef __cplusplus
extern "C"
{
    #endif
    /** create new proxy option object, also create two j_cb_payloads which must be released through jniOptionsFree */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Proxy_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version);
    /** release payloads and free optsPtr. */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Proxy_jniOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    /** run some callback tests. */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Proxy_jniOptionsTestCallbacks)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** -------- Signature of the header ---------- */
    /** int version*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Proxy_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** int type*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Proxy_jniOptionsGetType)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** const char *url*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Proxy_jniOptionsGetUrl)(JNIEnv *env, jclass obj, jlong optionsPtr);

    /** SKIPPED (getter): void *payload*/

    /** int version*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Proxy_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr, jint version);
    /** int type*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Proxy_jniOptionsSetType)(JNIEnv *env, jclass obj, jlong optionsPtr, jint type);
    /** const char *url*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Proxy_jniOptionsSetUrl)(JNIEnv *env, jclass obj, jlong optionsPtr, jstring url);
    /** git_credential_acquire_cb credentials*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Proxy_jniOptionsSetCredentials)(JNIEnv *env, jclass obj, jlong optionsPtr, jobject credentials);
    /** git_transport_certificate_check_cb certificate_check*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Proxy_jniOptionsSetCertificateCheck)(JNIEnv *env, jclass obj, jlong optionsPtr, jobject certificateCheck);
    /** void *payload*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Proxy_jniOptionsSetPayload)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong payload);

    #ifdef __cplusplus
}
#endif
#endif