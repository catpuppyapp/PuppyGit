#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_REFDB_H__
#define __GIT24J_REFDB_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** -------- Signature of the header ---------- */
    /** int git_refdb_new(git_refdb **out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refdb_jniNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);

    /** int git_refdb_open(git_refdb **out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refdb_jniOpen)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);

    /** int git_refdb_compress(git_refdb *refdb); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refdb_jniCompress)(JNIEnv *env, jclass obj, jlong refdbPtr);

    /** void git_refdb_free(git_refdb *refdb); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Refdb_jniFree)(JNIEnv *env, jclass obj, jlong refdbPtr);

#ifdef __cplusplus
}
#endif
#endif