#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_CREDENTIAL_H__
#define __GIT24J_CREDENTIAL_H__
#ifdef __cplusplus
extern "C"
{
#endif

    // no matching type found for 'git_credential_ssh_interactive_cb prompt_callback'
    /** int git_credential_ssh_interactive_new(git_credential **out, const char *username, git_credential_ssh_interactive_cb prompt_callback, void *payload); */
    // no matching type found for 'git_credential_sign_cb sign_callback'
    /** int git_credential_ssh_custom_new(git_credential **out, const char *username, const char *publickey, size_t publickey_len, git_credential_sign_cb sign_callback, void *payload); */
    /** -------- Signature of the header ---------- */
    /** void git_credential_free(git_credential *cred); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Credential_jniFree)(JNIEnv *env, jclass obj, jlong credPtr);

    /** int git_credential_has_username(git_credential *cred); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniHasUsername)(JNIEnv *env, jclass obj, jlong credPtr);

    /** const char * git_credential_get_username(git_credential *cred); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Credential_jniGetUsername)(JNIEnv *env, jclass obj, jlong credPtr);

    /** int git_credential_userpass_plaintext_new(git_credential **out, const char *username, const char *password); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniUserpassPlaintextNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring password);

    /** int git_credential_default_new(git_credential **out); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniDefaultNew)(JNIEnv *env, jclass obj, jobject out);

    /** int git_credential_username_new(git_credential **out, const char *username); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniUsernameNew)(JNIEnv *env, jclass obj, jobject out, jstring username);

    /** int git_credential_ssh_key_new(git_credential **out, const char *username, const char *publickey, const char *privatekey, const char *passphrase); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniSshKeyNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring publickey, jstring privatekey, jstring passphrase);

    /** int git_credential_ssh_key_memory_new(git_credential **out, const char *username, const char *publickey, const char *privatekey, const char *passphrase); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniSshKeyMemoryNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring publickey, jstring privatekey, jstring passphrase);

    /** int git_credential_ssh_key_from_agent(git_credential **out, const char *username); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniSshKeyFromAgent)(JNIEnv *env, jclass obj, jobject out, jstring username);

    /** int git_credential_userpass(git_credential **out, const char *url, const char *user_from_url, unsigned int allowed_types, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniUserpass)(JNIEnv *env, jclass obj, jobject out, jstring url, jstring user_from_url, jint allowedTypes, jlong payload);

#ifdef __cplusplus
}
#endif
#endif