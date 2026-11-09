#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_BLAME_H__
#define __GIT24J_BLAME_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_blame_init_options(git_blame_options *opts, unsigned int version); */
//    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version);

    /** size_t git_blame_get_hunk_count(git_blame *blame); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniGetHunkCount)(JNIEnv *env, jclass obj, jlong blamePtr);

    /** const git_blame_hunk * git_blame_get_hunk_byindex(git_blame *blame, size_t index); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniGetHunkByindex)(JNIEnv *env, jclass obj, jlong blamePtr, jint index);

    /** const git_blame_hunk * git_blame_get_hunk_byline(git_blame *blame, size_t lineno); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniGetHunkByline)(JNIEnv *env, jclass obj, jlong blamePtr, jint lineno);

    /** int git_blame_file(git_blame **out, git_repository *repo, const char *path, git_blame_options *options); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniFile)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring path, jlong optionsPtr);

    /** int git_blame_buffer(git_blame **out, git_blame *reference, const char *buffer, size_t buffer_len); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniBuffer)(JNIEnv *env, jclass obj, jobject out, jlong referencePtr, jstring buffer, jint bufferLen);

    /** void git_blame_free(git_blame *blame); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Blame_jniFree)(JNIEnv *env, jclass obj, jlong blamePtr);

    /** -------- Signature of the header ---------- */
    /** size_t lines_in_hunk*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniHunkGetLinesInHunk)(JNIEnv *env, jclass obj, jlong hunkPtr);
    /** git_oid final_commit_id*/
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Blame_jniHunkGetFinalCommitId)(JNIEnv *env, jclass obj, jlong hunkPtr);
    /** size_t final_start_line_number*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniHunkGetFinalStartLineNumber)(JNIEnv *env, jclass obj, jlong hunkPtr);
    /** git_signature *final_signature*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniHunkGetFinalSignature)(JNIEnv *env, jclass obj, jlong hunkPtr);
    /** git_oid orig_commit_id*/
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Blame_jniHunkGetOrigCommitId)(JNIEnv *env, jclass obj, jlong hunkPtr);
    /** const char *orig_path*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Blame_jniHunkGetOrigPath)(JNIEnv *env, jclass obj, jlong hunkPtr);
    /** size_t orig_start_line_number*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Blame_jniHunkGetOrigStartLineNumber)(JNIEnv *env, jclass obj, jlong hunkPtr);
    /** git_signature *orig_signature*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Blame_jniHunkGetOrigSignature)(JNIEnv *env, jclass obj, jlong hunkPtr);
    /** char boundary*/
    JNIEXPORT jchar JNICALL J_MAKE_METHOD(Blame_jniHunkGetBoundary)(JNIEnv *env, jclass obj, jlong hunkPtr);

#ifdef __cplusplus
}
#endif
#endif