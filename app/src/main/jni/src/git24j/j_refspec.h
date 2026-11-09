#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_REFSPEC_H__
#define __GIT24J_REFSPEC_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_refspec_parse(git_refspec **refspec, const char *input, int is_fetch); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniParse)(JNIEnv *env, jclass obj, jobject refspec, jstring input, jint is_fetch);

    /** void git_refspec_free(git_refspec *refspec); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Refspec_jniFree)(JNIEnv *env, jclass obj, jlong refspecPtr);

    /** const char * git_refspec_src(const git_refspec *refspec); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Refspec_jniSrc)(JNIEnv *env, jclass obj, jlong refspecPtr);

    /** const char * git_refspec_dst(const git_refspec *refspec); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Refspec_jniDst)(JNIEnv *env, jclass obj, jlong refspecPtr);

    /** const char * git_refspec_string(const git_refspec *refspec); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Refspec_jniString)(JNIEnv *env, jclass obj, jlong refspecPtr);

    /** int git_refspec_force(const git_refspec *refspec); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniForce)(JNIEnv *env, jclass obj, jlong refspecPtr);

    /** git_direction git_refspec_direction(const git_refspec *spec); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniDirection)(JNIEnv *env, jclass obj, jlong specPtr);

    /** int git_refspec_src_matches(const git_refspec *refspec, const char *refname); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniSrcMatches)(JNIEnv *env, jclass obj, jlong refspecPtr, jstring refname);

    /** int git_refspec_dst_matches(const git_refspec *refspec, const char *refname); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniDstMatches)(JNIEnv *env, jclass obj, jlong refspecPtr, jstring refname);

    /** int git_refspec_transform(git_buf *out, const git_refspec *spec, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniTransform)(JNIEnv *env, jclass obj, jobject out, jlong specPtr, jstring name);

    /** int git_refspec_rtransform(git_buf *out, const git_refspec *spec, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniRtransform)(JNIEnv *env, jclass obj, jobject out, jlong specPtr, jstring name);

#ifdef __cplusplus
}
#endif
#endif