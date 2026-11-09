#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_CRED_H__
#define __GIT24J_CRED_H__
#ifdef __cplusplus
extern "C"
{
#endif
    // no matching type found for 'git_cred_ssh_interactive_callback prompt_callback'
    /** int git_cred_ssh_interactive_new(git_cred **out, const char *username, git_cred_ssh_interactive_callback prompt_callback, void *payload); */
    // no matching type found for 'git_cred_sign_callback sign_callback'
    /** int git_cred_ssh_custom_new(git_cred **out, const char *username, const char *publickey, size_t publickey_len, git_cred_sign_callback sign_callback, void *payload); */
    /** -------- Signature of the header ---------- */
    /** int git_cred_userpass(git_cred **cred, const char *url, const char *user_from_url, unsigned int allowed_types, void *payload); */
    /* JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniUserpass)(JNIEnv *env, jclass obj, jobject cred, jstring url, jstring user_from_url, jint allowedTypes); */

    /** int git_cred_has_username(git_cred *cred); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniHasUsername)(JNIEnv *env, jclass obj, jlong credPtr);

    /** int git_cred_userpass_plaintext_new(git_cred **out, const char *username, const char *password); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniUserpassPlaintextNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring password);

    /** int git_cred_ssh_key_new(git_cred **out, const char *username, const char *publickey, const char *privatekey, const char *passphrase); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniSshKeyNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring publickey, jstring privatekey, jstring passphrase);

    /** int git_cred_ssh_key_from_agent(git_cred **out, const char *username); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniSshKeyFromAgent)(JNIEnv *env, jclass obj, jobject out, jstring username);

    /** int git_cred_default_new(git_cred **out); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniDefaultNew)(JNIEnv *env, jclass obj, jobject out);

    /** int git_cred_username_new(git_cred **cred, const char *username); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniUsernameNew)(JNIEnv *env, jclass obj, jobject cred, jstring username);

    /** int git_cred_ssh_key_memory_new(git_cred **out, const char *username, const char *publickey, const char *privatekey, const char *passphrase); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniSshKeyMemoryNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring publickey, jstring privatekey, jstring passphrase);

    /** void git_cred_free(git_cred *cred); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Cred_jniFree)(JNIEnv *env, jclass obj, jlong credPtr);

    /** int git_cred_acquire_cb(git_cred **cred, const char *url, const char *username_from_url, unsigned int allowed_types, void *payload); */
    /* JNIEXPORT jint JNICALL J_MAKE_METHOD(Cred_jniAcquireCb)(JNIEnv *env, jclass obj, jobject cred, jstring url, jstring username_from_url, jint allowedTypes); */

#ifdef __cplusplus
}
#endif
#endif