#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_SIGNATURE_H__
#define __GIT24J_SIGNATURE_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** -------- Signature of the header ---------- */
    /** int git_signature_new(git_signature **out, const char *name, const char *email, git_time_t time, int offset); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Signature_jniNew)(JNIEnv *env, jclass obj, jobject out, jstring name, jstring email, jlong time, jint offset);

    /** int git_signature_now(git_signature **out, const char *name, const char *email); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Signature_jniNow)(JNIEnv *env, jclass obj, jobject out, jstring name, jstring email);

    /** int git_signature_default(git_signature **out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Signature_jniDefault)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);

    /** int git_signature_from_buffer(git_signature **out, const char *buf); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Signature_jniFromBuffer)(JNIEnv *env, jclass obj, jobject out, jstring buf);

    /** int git_signature_dup(git_signature **dest, const git_signature *sig); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Signature_jniDup)(JNIEnv *env, jclass obj, jobject dest, jlong sigPtr);

    /** void git_signature_free(git_signature *sig); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Signature_jniFree)(JNIEnv *env, jclass obj, jlong sigPtr);

    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Signature_jniGetName)(JNIEnv *env, jclass obj, jlong isgPtr);
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Signature_jniGetEmail)(JNIEnv *env, jclass obj, jlong isgPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Signature_jniGetEpocSeconds)(JNIEnv *env, jclass obj, jlong isgPtr);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Signature_jniGetOffsetMinutes)(JNIEnv *env, jclass obj, jlong isgPtr);

#ifdef __cplusplus
}
#endif
#endif