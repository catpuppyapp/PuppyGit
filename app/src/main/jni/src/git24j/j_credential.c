#include "j_credential.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/**
 * java callback should have signature:
 *    int accept(AtomicLong outCredential, String url, String usernameFromUrl, int allowedTypes);
 * */
int j_git_credential_acquire_cb(git_credential **out, const char *url, const char *username_from_url, unsigned int allowed_types, void *payload)
{
    assert(payload && "jni callback cannot be null");
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    jobject callback = j_payload->callback;
    jmethodID mid = j_payload->mid;
    assert(callback && mid && "callback and payload must not be null");
    JNIEnv *env = getEnv();
    jobject outCredential = (*env)->NewObject(env, jniConstants->clzAtomicLong, jniConstants->midAtomicLongInit);
    jstring jName = (*env)->NewStringUTF(env, username_from_url);
    jstring jUrl = (*env)->NewStringUTF(env, url);
    /******* Call Java Callback ********/
    int r = (*env)->CallIntMethod(env, callback, mid, outCredential, jUrl, jName, (jint)allowed_types);
    /***********************************/
    jlong credentialPtr = (*env)->CallLongMethod(env, outCredential, jniConstants->midAtomicLongGet);
    *out = (git_credential *)credentialPtr;
    (*env)->DeleteLocalRef(env, jUrl);
    (*env)->DeleteLocalRef(env, jName);
    (*env)->DeleteLocalRef(env, outCredential);
    return r;
}

// no matching type found for 'git_credential_ssh_interactive_cb prompt_callback'
/** int git_credential_ssh_interactive_new(git_credential **out, const char *username, git_credential_ssh_interactive_cb prompt_callback, void *payload); */
// no matching type found for 'git_credential_sign_cb sign_callback'
/** int git_credential_ssh_custom_new(git_credential **out, const char *username, const char *publickey, size_t publickey_len, git_credential_sign_cb sign_callback, void *payload); */
/** -------- Wrapper Body ---------- */
/** void git_credential_free(git_credential *cred); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Credential_jniFree)(JNIEnv *env, jclass obj, jlong credPtr)
{
    git_credential_free((git_credential *)credPtr);
}

/** int git_credential_has_username(git_credential *cred); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniHasUsername)(JNIEnv *env, jclass obj, jlong credPtr)
{
    int r = git_credential_has_username((git_credential *)credPtr);
    return r;
}

/** const char * git_credential_get_username(git_credential *cred); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Credential_jniGetUsername)(JNIEnv *env, jclass obj, jlong credPtr)
{
    const char *r = git_credential_get_username((git_credential *)credPtr);
    return (*env)->NewStringUTF(env, r);
}

/** int git_credential_userpass_plaintext_new(git_credential **out, const char *username, const char *password); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniUserpassPlaintextNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring password)
{
    git_credential *c_out = 0;
    char *c_username = j_copy_of_jstring(env, username, true);
    char *c_password = j_copy_of_jstring(env, password, true);
    int r = git_credential_userpass_plaintext_new(&c_out, c_username, c_password);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_credential_free(c_out); */
    free(c_username);
    free(c_password);
    return r;
}

/** int git_credential_default_new(git_credential **out); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniDefaultNew)(JNIEnv *env, jclass obj, jobject out)
{
    git_credential *c_out = 0;
    int r = git_credential_default_new(&c_out);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_credential_free(c_out); */
    return r;
}

/** int git_credential_username_new(git_credential **out, const char *username); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniUsernameNew)(JNIEnv *env, jclass obj, jobject out, jstring username)
{
    git_credential *c_out = 0;
    char *c_username = j_copy_of_jstring(env, username, true);
    int r = git_credential_username_new(&c_out, c_username);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_credential_free(c_out); */
    free(c_username);
    return r;
}

/** int git_credential_ssh_key_new(git_credential **out, const char *username, const char *publickey, const char *privatekey, const char *passphrase); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniSshKeyNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring publickey, jstring privatekey, jstring passphrase)
{
    git_credential *c_out = 0;
    char *c_username = j_copy_of_jstring(env, username, true);
    char *c_publickey = j_copy_of_jstring(env, publickey, true);
    char *c_privatekey = j_copy_of_jstring(env, privatekey, true);
    char *c_passphrase = j_copy_of_jstring(env, passphrase, true);
    int r = git_credential_ssh_key_new(&c_out, c_username, c_publickey, c_privatekey, c_passphrase);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_credential_free(c_out); */
    free(c_username);
    free(c_publickey);
    free(c_privatekey);
    free(c_passphrase);
    return r;
}

/** int git_credential_ssh_key_memory_new(git_credential **out, const char *username, const char *publickey, const char *privatekey, const char *passphrase); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniSshKeyMemoryNew)(JNIEnv *env, jclass obj, jobject out, jstring username, jstring publickey, jstring privatekey, jstring passphrase)
{
    git_credential *c_out = 0;
    char *c_username = j_copy_of_jstring(env, username, true);
    char *c_publickey = j_copy_of_jstring(env, publickey, true);
    char *c_privatekey = j_copy_of_jstring(env, privatekey, true);
    char *c_passphrase = j_copy_of_jstring(env, passphrase, true);
    int r = git_credential_ssh_key_memory_new(&c_out, c_username, c_publickey, c_privatekey, c_passphrase);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_credential_free(c_out); */
    free(c_username);
    free(c_publickey);
    free(c_privatekey);
    free(c_passphrase);
    return r;
}

/** int git_credential_ssh_key_from_agent(git_credential **out, const char *username); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniSshKeyFromAgent)(JNIEnv *env, jclass obj, jobject out, jstring username)
{
    git_credential *c_out = 0;
    char *c_username = j_copy_of_jstring(env, username, true);
    int r = git_credential_ssh_key_from_agent(&c_out, c_username);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_credential_free(c_out); */
    free(c_username);
    return r;
}

/** int git_credential_userpass(git_credential **out, const char *url, const char *user_from_url, unsigned int allowed_types, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Credential_jniUserpass)(JNIEnv *env, jclass obj, jobject out, jstring url, jstring user_from_url, jint allowedTypes, jlong payload)
{
    git_credential *c_out = 0;
    char *c_url = j_copy_of_jstring(env, url, true);
    char *c_user_from_url = j_copy_of_jstring(env, user_from_url, true);
    int r = git_credential_userpass(&c_out, c_url, c_user_from_url, allowedTypes, (void *)payload);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_credential_free(c_out); */
    free(c_url);
    free(c_user_from_url);
    return r;
}
