#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_PATCH_H__
#define __GIT24J_PATCH_H__
#ifdef __cplusplus
extern "C"
{
#endif
    /** -------- Signature of the header ---------- */
    /** int git_patch_from_diff(git_patch **out, git_diff *diff, size_t idx); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniFromDiff)(JNIEnv *env, jclass obj, jobject out, jlong diffPtr, jint idx);

    /** int git_patch_from_blobs(git_patch **out, const git_blob *old_blob, const char *old_as_path, const git_blob *new_blob, const char *new_as_path, const git_diff_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniFromBlobs)(JNIEnv *env, jclass obj, jobject out, jlong oldBlobPtr, jstring old_as_path, jlong newBlobPtr, jstring new_as_path, jlong optsPtr);

    /** int git_patch_from_blob_and_buffer(git_patch **out, const git_blob *old_blob, const char *old_as_path, const void *buffer, size_t buffer_len, const char *buffer_as_path, const git_diff_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniFromBlobAndBuffer)(JNIEnv *env, jclass obj, jobject out, jlong oldBlobPtr, jstring old_as_path, jbyteArray buffer, jint bufferLen, jstring buffer_as_path, jlong optsPtr);

    /** int git_patch_from_buffers(git_patch **out, const void *old_buffer, size_t old_len, const char *old_as_path, const void *new_buffer, size_t new_len, const char *new_as_path, const git_diff_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniFromBuffers)(JNIEnv *env, jclass obj, jobject out, jbyteArray oldBuffer, jint oldLen, jstring old_as_path, jbyteArray newBuffer, jint newLen, jstring new_as_path, jlong optsPtr);

    /** void git_patch_free(git_patch *patch); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Patch_jniFree)(JNIEnv *env, jclass obj, jlong patchPtr);

    /** const git_diff_delta * git_patch_get_delta(const git_patch *patch); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Patch_jniGetDelta)(JNIEnv *env, jclass obj, jlong patchPtr);

    /** size_t git_patch_num_hunks(const git_patch *patch); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniNumHunks)(JNIEnv *env, jclass obj, jlong patchPtr);

    /** int git_patch_line_stats(size_t *total_context, size_t *total_additions, size_t *total_deletions, const git_patch *patch); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniLineStats)(JNIEnv *env, jclass obj, jobject totalContext, jobject totalAdditions, jobject totalDeletions, jlong patchPtr);

    /** int git_patch_get_hunk(const git_diff_hunk **out, size_t *lines_in_hunk, git_patch *patch, size_t hunk_idx); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniGetHunk)(JNIEnv *env, jclass obj, jobject out, jobject linesInHunk, jlong patchPtr, jint hunkIdx);

    /** int git_patch_num_lines_in_hunk(const git_patch *patch, size_t hunk_idx); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniNumLinesInHunk)(JNIEnv *env, jclass obj, jlong patchPtr, jint hunkIdx);

    /** int git_patch_get_line_in_hunk(const git_diff_line **out, git_patch *patch, size_t hunk_idx, size_t line_of_hunk); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniGetLineInHunk)(JNIEnv *env, jclass obj, jobject out, jlong patchPtr, jint hunkIdx, jint lineOfHunk);

    /** size_t git_patch_size(git_patch *patch, int include_context, int include_hunk_headers, int include_file_headers); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniSize)(JNIEnv *env, jclass obj, jlong patchPtr, jint include_context, jint include_hunk_headers, jint include_file_headers);

    /** int git_patch_print(git_patch *patch, git_diff_line_cb print_cb, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniPrint)(JNIEnv *env, jclass obj, jlong patchPtr, jobject printCb);

    /** int git_patch_to_buf(git_buf *out, git_patch *patch); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Patch_jniToBuf)(JNIEnv *env, jclass obj, jobject out, jlong patchPtr);

#ifdef __cplusplus
}
#endif
#endif