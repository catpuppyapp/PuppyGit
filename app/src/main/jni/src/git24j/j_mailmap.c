#include "j_mailmap.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <stdio.h>
#include <string.h>
extern j_constants_t *jniConstants;

/** GIT_EXTERN(void) git_mailmap_free(git_mailmap *mm); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Mailmap_jniFree)(JNIEnv *env, jclass obj, jlong mmPtr)
{
    git_mailmap_free((git_mailmap *)mmPtr);
}

/** GIT_EXTERN(int) git_mailmap_add_entry(git_mailmap *mm, const char *real_name, const char *real_email, const char *replace_name, const char *replace_email); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Mailmap_jniAddEntry)(JNIEnv *env, jclass obj, jlong mmPtr, jstring realName, jstring realEmail, jstring replaceName, jstring replaceEmail)
{
    char *real_name = j_copy_of_jstring(env, realName, true);
    char *real_email = j_copy_of_jstring(env, realEmail, true);
    char *replace_name = j_copy_of_jstring(env, replaceName, true);
    char *replace_email = j_copy_of_jstring(env, replaceEmail, true);
    int e = git_mailmap_add_entry((git_mailmap *)mmPtr, real_name, real_email, replace_name, replace_email);
    free(replace_email);
    free(replace_name);
    free(real_email);
    free(real_name);
    return e;
}
/** GIT_EXTERN(int) git_mailmap_from_buffer(git_mailmap **out, const char *buf, size_t len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Mailmap_jniFromBuffer)(JNIEnv *env, jclass obj, jobject outPtr, jstring buf)
{
    char *c_buf = j_copy_of_jstring(env, buf, true);
    size_t len = strlen(c_buf);
    git_mailmap *c_out = 0;
    int e = git_mailmap_from_buffer(&c_out, c_buf, len);
    (*env)->CallVoidMethod(env, outPtr, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_buf);
    return e;
}

/** GIT_EXTERN(int) git_mailmap_from_repository(git_mailmap **out, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Mailmap_jniFromRepository)(JNIEnv *env, jclass obj, jobject outPtr, jlong repoPtr)
{
    git_mailmap *c_out = 0;
    int e = git_mailmap_from_repository(&c_out, (git_repository *)repoPtr);
    (*env)->CallVoidMethod(env, outPtr, jniConstants->midAtomicLongSet, (jlong)c_out);
    return e;
}

/** GIT_EXTERN(int) git_mailmap_resolve( const char **real_name, const char **real_email, const git_mailmap *mm, const char *name, const char *email); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Mailmap_jniResolve)(JNIEnv *env, jclass obj, jobject outRealName, jobject outRealEmail, jlong mmPtr, jstring name, jstring email)
{
    assert(outRealName && "receiving object must not be null");
    jclass clz = (*env)->GetObjectClass(env, outRealName);
    /* jclass clz = (*env)->FindClass(env, "Ljava/util/concurrent/atomic/AtomicReference;"); */
    assert(clz && "Mailmap::jniResolve: could not identify the class of receivers");
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_email = j_copy_of_jstring(env, email, true);
    const char *real_name;
    const char *real_email;
    int e = git_mailmap_resolve(&real_name, &real_email, (git_mailmap *)mmPtr, c_name, c_email);
    jstring realName = (*env)->NewStringUTF(env, real_name);
    jstring realEmail = (*env)->NewStringUTF(env, real_email);
    j_call_setter_object(env, clz, outRealName, "set", realName);
    j_call_setter_object(env, clz, outRealEmail, "set", realEmail);
    (*env)->DeleteLocalRef(env, realEmail);
    (*env)->DeleteLocalRef(env, realName);
    free(c_email);
    free(c_name);
    return e;
}

/** int git_mailmap_resolve_signature(git_signature **out, const git_mailmap *mm, const git_signature *sig); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Mailmap_jniResolveSignature)(JNIEnv *env, jclass obj, jobject out, jlong mmPtr, jlong sigPtr)
{
    git_signature *c_out = 0;
    int r = git_mailmap_resolve_signature(&c_out, (git_mailmap *)mmPtr, (git_signature *)sigPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}
