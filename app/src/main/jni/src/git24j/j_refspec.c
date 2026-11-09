#include "j_refspec.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** int git_refspec_parse(git_refspec **refspec, const char *input, int is_fetch); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniParse)(JNIEnv *env, jclass obj, jobject refspec, jstring input, jint is_fetch)
{
    git_refspec *c_refspec = 0;
    char *c_input = j_copy_of_jstring(env, input, true);
    int r = git_refspec_parse(&c_refspec, c_input, is_fetch);
    (*env)->CallVoidMethod(env, refspec, jniConstants->midAtomicLongSet, (jlong)c_refspec);
    /* git_refspec_free(c_refspec); */
    free(c_input);
    return r;
}

/** void git_refspec_free(git_refspec *refspec); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Refspec_jniFree)(JNIEnv *env, jclass obj, jlong refspecPtr)
{
    git_refspec_free((git_refspec *)refspecPtr);
}

/** const char * git_refspec_src(const git_refspec *refspec); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Refspec_jniSrc)(JNIEnv *env, jclass obj, jlong refspecPtr)
{
    const char *r = git_refspec_src((git_refspec *)refspecPtr);
    return (*env)->NewStringUTF(env, r);
}

/** const char * git_refspec_dst(const git_refspec *refspec); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Refspec_jniDst)(JNIEnv *env, jclass obj, jlong refspecPtr)
{
    const char *r = git_refspec_dst((git_refspec *)refspecPtr);
    return (*env)->NewStringUTF(env, r);
}

/** const char * git_refspec_string(const git_refspec *refspec); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Refspec_jniString)(JNIEnv *env, jclass obj, jlong refspecPtr)
{
    const char *r = git_refspec_string((git_refspec *)refspecPtr);
    return (*env)->NewStringUTF(env, r);
}

/** int git_refspec_force(const git_refspec *refspec); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniForce)(JNIEnv *env, jclass obj, jlong refspecPtr)
{
    int r = git_refspec_force((git_refspec *)refspecPtr);
    return r;
}

/** git_direction git_refspec_direction(const git_refspec *spec); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniDirection)(JNIEnv *env, jclass obj, jlong specPtr)
{
    git_direction r = git_refspec_direction((git_refspec *)specPtr);
    return r;
}

/** int git_refspec_src_matches(const git_refspec *refspec, const char *refname); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniSrcMatches)(JNIEnv *env, jclass obj, jlong refspecPtr, jstring refname)
{
    char *c_refname = j_copy_of_jstring(env, refname, true);
    int r = git_refspec_src_matches((git_refspec *)refspecPtr, c_refname);
    free(c_refname);
    return r;
}

/** int git_refspec_dst_matches(const git_refspec *refspec, const char *refname); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniDstMatches)(JNIEnv *env, jclass obj, jlong refspecPtr, jstring refname)
{
    char *c_refname = j_copy_of_jstring(env, refname, true);
    int r = git_refspec_dst_matches((git_refspec *)refspecPtr, c_refname);
    free(c_refname);
    return r;
}

/** int git_refspec_transform(git_buf *out, const git_refspec *spec, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniTransform)(JNIEnv *env, jclass obj, jobject out, jlong specPtr, jstring name)
{
    git_buf c_out = {0};
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_refspec_transform(&c_out, (git_refspec *)specPtr, c_name);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    free(c_name);
    return r;
}

/** int git_refspec_rtransform(git_buf *out, const git_refspec *spec, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Refspec_jniRtransform)(JNIEnv *env, jclass obj, jobject out, jlong specPtr, jstring name)
{
    git_buf c_out = {0};
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_refspec_rtransform(&c_out, (git_refspec *)specPtr, c_name);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    free(c_name);
    return r;
}
