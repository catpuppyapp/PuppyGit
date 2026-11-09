#include "j_signature.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** -------- Wrapper Body ---------- */
/** int git_signature_new(git_signature **out, const char *name, const char *email, git_time_t time, int offset); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Signature_jniNew)(JNIEnv *env, jclass obj, jobject out, jstring name, jstring email, jlong time, jint offset)
{
    git_signature *c_out = 0;
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_email = j_copy_of_jstring(env, email, true);
    int r = git_signature_new(&c_out, c_name, c_email, time, offset);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_name);
    free(c_email);
    return r;
}

/** int git_signature_now(git_signature **out, const char *name, const char *email); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Signature_jniNow)(JNIEnv *env, jclass obj, jobject out, jstring name, jstring email)
{
    git_signature *c_out = 0;
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_email = j_copy_of_jstring(env, email, true);

    int r = git_signature_now(&c_out, c_name, c_email);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_name);
    free(c_email);
    return r;
}

/** int git_signature_default(git_signature **out, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Signature_jniDefault)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr)
{
    git_signature *c_out = 0;
    int r = git_signature_default(&c_out, (git_repository *)repoPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** int git_signature_from_buffer(git_signature **out, const char *buf); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Signature_jniFromBuffer)(JNIEnv *env, jclass obj, jobject out, jstring buf)
{
    git_signature *c_out = 0;
    char *c_buf = j_copy_of_jstring(env, buf, true);
    int r = git_signature_from_buffer(&c_out, c_buf);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_buf);
    return r;
}

/** int git_signature_dup(git_signature **dest, const git_signature *sig); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Signature_jniDup)(JNIEnv *env, jclass obj, jobject dest, jlong sigPtr)
{
    git_signature *c_dest = 0;
    int r = git_signature_dup(&c_dest, (git_signature *)sigPtr);
    (*env)->CallVoidMethod(env, dest, jniConstants->midAtomicLongSet, (jlong)c_dest);
    return r;
}

/** void git_signature_free(git_signature *sig); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Signature_jniFree)(JNIEnv *env, jclass obj, jlong sigPtr)
{
    git_signature_free((git_signature *)sigPtr);
}

JNIEXPORT jstring JNICALL J_MAKE_METHOD(Signature_jniGetName)(JNIEnv *env, jclass obj, jlong isgPtr)
{
    return (*env)->NewStringUTF(env, ((git_signature *)isgPtr)->name);
}

JNIEXPORT jstring JNICALL J_MAKE_METHOD(Signature_jniGetEmail)(JNIEnv *env, jclass obj, jlong isgPtr)
{
    return (*env)->NewStringUTF(env, ((git_signature *)isgPtr)->email);
}

JNIEXPORT jlong JNICALL J_MAKE_METHOD(Signature_jniGetEpocSeconds)(JNIEnv *env, jclass obj, jlong isgPtr)
{
    return (jlong)((git_signature *)isgPtr)->when.time;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Signature_jniGetOffsetMinutes)(JNIEnv *env, jclass obj, jlong isgPtr)
{
    return (jlong)((git_signature *)isgPtr)->when.offset;
}
