#include "j_refdb.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** -------- Wrapper Body ---------- */
/** int git_refdb_new(git_refdb **out, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Refdb_jniNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr)
{
    git_refdb *c_out;
    int r = git_refdb_new(&c_out, (git_repository *)repoPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** int git_refdb_open(git_refdb **out, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Refdb_jniOpen)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr)
{
    git_refdb *c_out;
    int r = git_refdb_open(&c_out, (git_repository *)repoPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** int git_refdb_compress(git_refdb *refdb); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Refdb_jniCompress)(JNIEnv *env, jclass obj, jlong refdbPtr)
{
    int r = git_refdb_compress((git_refdb *)refdbPtr);
    return r;
}

/** void git_refdb_free(git_refdb *refdb); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Refdb_jniFree)(JNIEnv *env, jclass obj, jlong refdbPtr)
{
    git_refdb_free((git_refdb *)refdbPtr);
}
