#include "j_blame.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** -------- Wrapper Body ---------- */
/** int git_blame_init_options(git_blame_options *opts, unsigned int version); */
//JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
//{
//    int r = git_blame_init_options((git_blame_options *)optsPtr, version);
//    return r;
//}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version)
{
    git_blame_options *opts = (git_blame_options *)malloc(sizeof(git_blame_options));

    //value of `err`: Zero on success; -1 on failure.
    // I am not sure it can get this err msg from git_last_err(), if version bad, maybe can get, if mem alloc err, maybe can't
    int initErr = git_blame_init_options(opts, (unsigned int)version);

    // this set no check the initErr, even initErr, still set the ptr to java pointer, it make java be able free the mallocated memory
    (*env)->CallVoidMethod(env, outPtr, jniConstants->midAtomicLongSet, (jlong)opts);

    return initErr;
}

/** size_t git_blame_get_hunk_count(git_blame *blame); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniGetHunkCount)(JNIEnv *env, jclass obj, jlong blamePtr)
{
    size_t r = git_blame_get_hunk_count((git_blame *)blamePtr);
    return r;
}

/** const git_blame_hunk * git_blame_get_hunk_byindex(git_blame *blame, size_t index); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniGetHunkByindex)(JNIEnv *env, jclass obj, jlong blamePtr, jint index)
{
    const git_blame_hunk *r = git_blame_get_hunk_byindex((git_blame *)blamePtr, index);
    return (jlong)r;
}

/** const git_blame_hunk * git_blame_get_hunk_byline(git_blame *blame, size_t lineno); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniGetHunkByline)(JNIEnv *env, jclass obj, jlong blamePtr, jint lineno)
{
    const git_blame_hunk *r = git_blame_get_hunk_byline((git_blame *)blamePtr, lineno);
    return (jlong)r;
}

/** int git_blame_file(git_blame **out, git_repository *repo, const char *path, git_blame_options *options); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniFile)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring path, jlong optionsPtr)
{
    git_blame *c_out = NULL;
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_blame_file(&c_out, (git_repository *)repoPtr, c_path, (git_blame_options *)optionsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_blame_free(c_out); */
    free(c_path);
    return r;
}

/** int git_blame_buffer(git_blame **out, git_blame *reference, const char *buffer, size_t buffer_len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniBuffer)(JNIEnv *env, jclass obj, jobject out, jlong referencePtr, jstring buffer, jint bufferLen)
{
    git_blame *c_out = NULL;
    char *c_buffer = j_copy_of_jstring(env, buffer, true);
    int r = git_blame_buffer(&c_out, (git_blame *)referencePtr, c_buffer, bufferLen);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_blame_free(c_out); */
    free(c_buffer);
    return r;
}

/** void git_blame_free(git_blame *blame); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Blame_jniFree)(JNIEnv *env, jclass obj, jlong blamePtr)
{
    git_blame_free((git_blame *)blamePtr);
}

/** -------- Wrapper Body ---------- */
/** size_t lines_in_hunk*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniHunkGetLinesInHunk)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return ((git_blame_hunk *)hunkPtr)->lines_in_hunk;
}

/** git_oid final_commit_id*/
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Blame_jniHunkGetFinalCommitId)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return j_git_oid_to_bytearray(env, &((git_blame_hunk *)hunkPtr)->final_commit_id);
}

/** size_t final_start_line_number*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniHunkGetFinalStartLineNumber)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return ((git_blame_hunk *)hunkPtr)->final_start_line_number;
}

/** git_signature *final_signature*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniHunkGetFinalSignature)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return (jlong)((git_blame_hunk *)hunkPtr)->final_signature;
}

/** git_oid orig_commit_id*/
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Blame_jniHunkGetOrigCommitId)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return j_git_oid_to_bytearray(env, &((git_blame_hunk *)hunkPtr)->orig_commit_id);
}

/** const char *orig_path*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Blame_jniHunkGetOrigPath)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return (*env)->NewStringUTF(env, ((git_blame_hunk *)hunkPtr)->orig_path);
}

/** size_t orig_start_line_number*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniHunkGetOrigStartLineNumber)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return ((git_blame_hunk *)hunkPtr)->orig_start_line_number;
}

/** git_signature *orig_signature*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniHunkGetOrigSignature)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return (jlong)((git_blame_hunk *)hunkPtr)->orig_signature;
}

/** char boundary*/
JNIEXPORT jchar JNICALL J_MAKE_METHOD(Blame_jniHunkGetBoundary)(JNIEnv *env, jclass obj, jlong hunkPtr)
{
    return ((git_blame_hunk *)hunkPtr)->boundary;
}
