#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_CERT_H__
#define __GIT24J_CERT_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** git_cert parent*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Cert_jniHostkeyGetParent)(JNIEnv *env, jclass obj, jlong hostkeyPtr);
    /** git_cert_ssh_t type*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cert_jniHostkeyGetType)(JNIEnv *env, jclass obj, jlong hostkeyPtr);
    /** unsigned char hash_md5[16]*/
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Cert_jniHostkeyGetHashMd5)(JNIEnv *env, jclass obj, jlong hostkeyPtr);
    /** unsigned char hash_sha1[20]*/
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Cert_jniHostkeyGetHashSha1)(JNIEnv *env, jclass obj, jlong hostkeyPtr);
    /** unsigned char hash_sha256[32]*/
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Cert_jniHostkeyGetHashSha256)(JNIEnv *env, jclass obj, jlong hostkeyPtr);

    /** create empty hostkey struct for testing*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Cert_jniHostkeyCreateEmptyForTesting)(JNIEnv *env, jclass obj);

    /** -------- X509 ---------- */
    /** git_cert parent*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Cert_jniX509GetParent)(JNIEnv *env, jclass obj, jlong x509Ptr);

#ifdef __cplusplus
}
#endif
#endif