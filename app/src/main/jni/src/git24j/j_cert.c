#include "j_cert.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** git_cert parent*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Cert_jniHostkeyGetParent)(JNIEnv *env, jclass obj, jlong hostkeyPtr)
{
    return (jlong)(&((git_cert_hostkey *)hostkeyPtr)->parent);
}

/** git_cert_ssh_t type*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Cert_jniHostkeyGetType)(JNIEnv *env, jclass obj, jlong hostkeyPtr)
{
    return ((git_cert_hostkey *)hostkeyPtr)->type;
}

/** unsigned char hash_md5[16]*/

JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Cert_jniHostkeyGetHashMd5)(JNIEnv *env, jclass obj, jlong hostkeyPtr)
{
    return j_byte_array_from_c(env, ((git_cert_hostkey *)hostkeyPtr)->hash_md5, 16);
}

/** unsigned char hash_sha1[20]*/
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Cert_jniHostkeyGetHashSha1)(JNIEnv *env, jclass obj, jlong hostkeyPtr)
{
    return j_byte_array_from_c(env, ((git_cert_hostkey *)hostkeyPtr)->hash_sha1, 20);
}

/** unsigned char hash_sha256[32]*/
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Cert_jniHostkeyGetHashSha256)(JNIEnv *env, jclass obj, jlong hostkeyPtr)
{
    return j_byte_array_from_c(env, ((git_cert_hostkey *)hostkeyPtr)->hash_sha256, 32);
}

/** create empty hostkey struct for testing*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Cert_jniHostkeyCreateEmptyForTesting)(JNIEnv *env, jclass obj)
{
    git_cert *cert = (git_cert *)malloc(sizeof(git_cert));
    return (jlong)cert;
}

/** git_cert parent*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Cert_jniX509GetParent)(JNIEnv *env, jclass obj, jlong x509Ptr)
{
    return (jlong) & (((git_cert_x509 *)x509Ptr)->parent);
}
