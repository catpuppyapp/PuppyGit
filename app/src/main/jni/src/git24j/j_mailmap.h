#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_MAILMAP_H__
#define __GIT24J_MAILMAP_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** New function should stay in c only. */
    /** GIT_EXTERN(int) git_mailmap_new(git_mailmap **out); */

    /** GIT_EXTERN(void) git_mailmap_free(git_mailmap *mm); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Mailmap_jniFree)(JNIEnv *env, jclass obj, jlong mmPtr);

    /** GIT_EXTERN(int) git_mailmap_add_entry(git_mailmap *mm, const char *real_name, const char *real_email, const char *replace_name, const char *replace_email); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Mailmap_jniAddEntry)(JNIEnv *env, jclass obj, jlong mmPtr, jstring realName, jstring realEmail, jstring replaceName, jstring replaceEmail);

    /** GIT_EXTERN(int) git_mailmap_from_buffer(git_mailmap **out, const char *buf, size_t len); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Mailmap_jniFromBuffer)(JNIEnv *env, jclass obj, jobject outPtr, jstring buf);

    /** GIT_EXTERN(int) git_mailmap_from_repository(git_mailmap **out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Mailmap_jniFromRepository)(JNIEnv *env, jclass obj, jobject outPtr, jlong repoPtr);

    /** GIT_EXTERN(int) git_mailmap_resolve( const char **real_name, const char **real_email, const git_mailmap *mm, const char *name, const char *email); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Mailmap_jniResolve)(JNIEnv *env, jclass obj, jobject outRealName, jobject outRealEmail, jlong mmPtr, jstring name, jstring email);

    /** int git_mailmap_resolve_signature(git_signature **out, const git_mailmap *mm, const git_signature *sig); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Mailmap_jniResolveSignature)(JNIEnv *env, jclass obj, jobject out, jlong mmPtr, jlong sigPtr);

#ifdef __cplusplus
}
#endif
#endif