#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_DIFF_H__
#define __GIT24J_DIFF_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /**Pack jni objects to pass to update callback. */
    typedef struct
    {
        JNIEnv *env;
        jobject fileCb;
        jobject binaryCb;
        jobject hunkCb;
        jobject lineCb;
    } j_diff_callback_payload;

    int j_git_diff_line_cb(const git_diff_delta *delta, const git_diff_hunk *hunk, const git_diff_line *line, void *payload);

    /** -------- Signature of the header ---------- */
    /** int git_diff_init_options(git_diff_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniInitOptions)(JNIEnv *env, jclass obj, jobject outOpts, jint version);

    /** free git_diff_options */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniFreeOptions)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** int git_diff_find_init_options(git_diff_find_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFindInitOptions)(JNIEnv *env, jclass obj, jobject outFindOpts, jint version);

    JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniFreeFindOptions)(JNIEnv *env, jclass obj, jlong findOptsPtr);

    /** void git_diff_free(git_diff *diff); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniFree)(JNIEnv *env, jclass obj, jlong diffPtr);

    /** int git_diff_tree_to_tree(git_diff **diff, git_repository *repo, git_tree *old_tree, git_tree *new_tree, const git_diff_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniTreeToTree)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong oldTreePtr, jlong newTreePtr, jlong optsPtr);

    /** int git_diff_tree_to_index(git_diff **diff, git_repository *repo, git_tree *old_tree, git_index *index, const git_diff_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniTreeToIndex)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong oldTreePtr, jlong indexPtr, jlong optsPtr);

    /** int git_diff_index_to_workdir(git_diff **diff, git_repository *repo, git_index *index, const git_diff_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniIndexToWorkdir)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong indexPtr, jlong optsPtr);

    /** int git_diff_tree_to_workdir(git_diff **diff, git_repository *repo, git_tree *old_tree, const git_diff_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniTreeToWorkdir)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong oldTreePtr, jlong optsPtr);

    /** int git_diff_tree_to_workdir_with_index(git_diff **diff, git_repository *repo, git_tree *old_tree, const git_diff_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniTreeToWorkdirWithIndex)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong oldTreePtr, jlong optsPtr);

    /** int git_diff_index_to_index(git_diff **diff, git_repository *repo, git_index *old_index, git_index *new_index, const git_diff_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniIndexToIndex)(JNIEnv *env, jclass obj, jobject diff, jlong repoPtr, jlong oldIndexPtr, jlong newIndexPtr, jlong optsPtr);

    /** int git_diff_merge(git_diff *onto, const git_diff *from); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniMerge)(JNIEnv *env, jclass obj, jlong ontoPtr, jlong fromPtr);

    /** int git_diff_find_similar(git_diff *diff, const git_diff_find_options *options); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFindSimilar)(JNIEnv *env, jclass obj, jlong diffPtr, jlong optionsPtr);

    /** size_t git_diff_num_deltas(const git_diff *diff); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniNumDeltas)(JNIEnv *env, jclass obj, jlong diffPtr);

    /** size_t git_diff_num_deltas_of_type(const git_diff *diff, git_delta_t type); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniNumDeltasOfType)(JNIEnv *env, jclass obj, jlong diffPtr, jint type);

    /** const git_diff_delta * git_diff_get_delta(const git_diff *diff, size_t idx); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Diff_jniGetDelta)(JNIEnv *env, jclass obj, jlong diffPtr, jint idx);

    /** int git_diff_is_sorted_icase(const git_diff *diff); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniIsSortedIcase)(JNIEnv *env, jclass obj, jlong diffPtr);

    /** int git_diff_foreach(git_diff *diff, git_diff_file_cb file_cb, git_diff_binary_cb binary_cb, git_diff_hunk_cb hunk_cb, git_diff_line_cb line_cb, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniForeach)(JNIEnv *env, jclass obj, jlong diffPtr, jobject fileCb, jobject binaryCb, jobject hunkCb, jobject lineCb);

    /** char git_diff_status_char(git_delta_t status); */
    JNIEXPORT jchar JNICALL J_MAKE_METHOD(Diff_jniStatusChar)(JNIEnv *env, jclass obj, jint status);

    /** int git_diff_print(git_diff *diff, git_diff_format_t format, git_diff_line_cb print_cb, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniPrint)(JNIEnv *env, jclass obj, jlong diffPtr, jint format, jobject printCb);

    /** int git_diff_to_buf(git_buf *out, git_diff *diff, git_diff_format_t format); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniToBuf)(JNIEnv *env, jclass obj, jobject out, jlong diffPtr, jint format);

    /** int git_diff_blobs(const git_blob *old_blob, const char *old_as_path, const git_blob *new_blob, const char *new_as_path, const git_diff_options *options, git_diff_file_cb file_cb, git_diff_binary_cb binary_cb, git_diff_hunk_cb hunk_cb, git_diff_line_cb line_cb, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniBlobs)(JNIEnv *env, jclass obj, jlong oldBlobPtr, jstring old_as_path, jlong newBlobPtr, jstring new_as_path, jlong optionsPtr, jobject fileCb, jobject binaryCb, jobject hunkCb, jobject lineCb);

    /** int git_diff_blob_to_buffer(const git_blob *old_blob, const char *old_as_path, const char *buffer, size_t buffer_len, const char *buffer_as_path, const git_diff_options *options, git_diff_file_cb file_cb, git_diff_binary_cb binary_cb, git_diff_hunk_cb hunk_cb, git_diff_line_cb line_cb, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniBlobToBuffer)(JNIEnv *env, jclass obj, jlong oldBlobPtr, jstring old_as_path, jstring buffer, jint bufferLen, jstring buffer_as_path, jlong optionsPtr, jobject fileCb, jobject binaryCb, jobject hunkCb, jobject lineCb);

    /** int git_diff_buffers(const void *old_buffer, size_t old_len, const char *old_as_path, const void *new_buffer, size_t new_len, const char *new_as_path, const git_diff_options *options, git_diff_file_cb file_cb, git_diff_binary_cb binary_cb, git_diff_hunk_cb hunk_cb, git_diff_line_cb line_cb, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniBuffers)(JNIEnv *env, jclass obj, jbyteArray oldBuffer, jint oldLen, jstring old_as_path, jbyteArray newBuffer, jint newLen, jstring new_as_path, jlong optionsPtr, jobject fileCb, jobject binaryCb, jobject hunkCb, jobject lineCb);

    /** int git_diff_from_buffer(git_diff **out, const char *content, size_t content_len); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFromBuffer)(JNIEnv *env, jclass obj, jobject out, jstring content, jint contentLen);

    /** int git_diff_get_stats(git_diff_stats **out, git_diff *diff); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniGetStats)(JNIEnv *env, jclass obj, jobject out, jlong diffPtr);

    /** size_t git_diff_stats_files_changed(const git_diff_stats *stats); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniStatsFilesChanged)(JNIEnv *env, jclass obj, jlong statsPtr);

    /** size_t git_diff_stats_insertions(const git_diff_stats *stats); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniStatsInsertions)(JNIEnv *env, jclass obj, jlong statsPtr);

    /** size_t git_diff_stats_deletions(const git_diff_stats *stats); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniStatsDeletions)(JNIEnv *env, jclass obj, jlong statsPtr);

    /** int git_diff_stats_to_buf(git_buf *out, const git_diff_stats *stats, git_diff_stats_format_t format, size_t width); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniStatsToBuf)(JNIEnv *env, jclass obj, jobject out, jlong statsPtr, jint format, jint width);

    /** void git_diff_stats_free(git_diff_stats *stats); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniStatsFree)(JNIEnv *env, jclass obj, jlong statsPtr);

    /** int git_diff_format_email(git_buf *out, git_diff *diff, const git_diff_format_email_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFormatEmail)(JNIEnv *env, jclass obj, jobject out, jlong diffPtr, jlong optsPtr);

    /** int git_diff_commit_as_email(git_buf *out, git_repository *repo, git_commit *commit, size_t patch_no, size_t total_patches, git_diff_format_email_flags_t flags, const git_diff_options *diff_opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniCommitAsEmail)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong commitPtr, jint patchNo, jint totalPatches, jint flags, jlong diffOptsPtr);

    /** int git_diff_format_email_init_options(git_diff_format_email_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFormatEmailInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);

    /** new and init git_diff_format_email_options */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFormatEmailNewOptions)(JNIEnv *env, jclass obj, jobject outPtr, jint version);

    /** free git_diff_format_email_options *opts. */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniFormatEmailOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** int git_diff_patchid_init_options(git_diff_patchid_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniPatchidInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);

    /** new git_diff_patchid_options*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniPatchidOptionsNew)(JNIEnv *env, jclass obj, jobject outPtr, jint version);
    /** free git_diff_patchid_options */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniPatchidOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);

    /** int git_diff_patchid(git_oid *out, git_diff *diff, git_diff_patchid_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniPatchid)(JNIEnv *env, jclass obj, jobject out, jlong diffPtr, jlong optsPtr);

    /** git_delta_t   status*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniDeltaGetStatus)(JNIEnv *env, jclass obj, jlong deltaPtr);
    /** int      flags*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniDeltaGetFlags)(JNIEnv *env, jclass obj, jlong deltaPtr);
    /** int      similarity*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniDeltaGetSimilarity)(JNIEnv *env, jclass obj, jlong deltaPtr);
    /** int      nfiles*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniDeltaGetNfiles)(JNIEnv *env, jclass obj, jlong deltaPtr);
    /** git_diff_file old_file*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Diff_jniDeltaGetOldFile)(JNIEnv *env, jclass obj, jlong deltaPtr);
    /** git_diff_file new_file*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Diff_jniDeltaGetNewFile)(JNIEnv *env, jclass obj, jlong deltaPtr);

    /** git_oid            id*/
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Diff_jniFileGetId)(JNIEnv *env, jclass obj, jlong filePtr);
    /** const char        *path*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Diff_jniFileGetPath)(JNIEnv *env, jclass obj, jlong filePtr);
    /** git_object_size_t  size*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFileGetSize)(JNIEnv *env, jclass obj, jlong filePtr);
    /** uint32_t           flags*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFileGetFlags)(JNIEnv *env, jclass obj, jlong filePtr);
    /** uint16_t           mode*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFileGetMode)(JNIEnv *env, jclass obj, jlong filePtr);
    /** uint16_t           id_abbrev*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniFileGetIdAbbrev)(JNIEnv *env, jclass obj, jlong filePtr);

    /****** diff hunk *********/
    /** int    old_start*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniHunkGetOldStart)(JNIEnv *env, jclass obj, jlong hunkPtr);
    /** int    old_lines*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniHunkGetOldLines)(JNIEnv *env, jclass obj, jlong hunkPtr);
    /** int    new_start*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniHunkGetNewStart)(JNIEnv *env, jclass obj, jlong hunkPtr);
    /** int    new_lines*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniHunkGetNewLines)(JNIEnv *env, jclass obj, jlong hunkPtr);
    /** size_t header_len*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniHunkGetHeaderLen)(JNIEnv *env, jclass obj, jlong hunkPtr);
    /** const char*   header*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Diff_jniHunkGetHeader)(JNIEnv *env, jclass obj, jlong hunkPtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Diff_jniHunkGetHeaderBytes)(JNIEnv *env, jclass obj, jlong hunkPtr);
    /************ diff line *************/
    /** char   origin*/
    JNIEXPORT jchar JNICALL J_MAKE_METHOD(Diff_jniLineGetOrigin)(JNIEnv *env, jclass obj, jlong linePtr);
    /** int    old_lineno*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniLineGetOldLineno)(JNIEnv *env, jclass obj, jlong linePtr);
    /** int    new_lineno*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniLineGetNewLineno)(JNIEnv *env, jclass obj, jlong linePtr);
    /** int    num_lines*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniLineGetNumLines)(JNIEnv *env, jclass obj, jlong linePtr);
    /** size_t content_len*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniLineGetContentLen)(JNIEnv *env, jclass obj, jlong linePtr);
    /** size_t content_offset*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniLineGetContentOffset)(JNIEnv *env, jclass obj, jlong linePtr);
    /** const char *content*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Diff_jniLineGetContent)(JNIEnv *env, jclass obj, jlong linePtr);
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Diff_jniLineGetContentBytes)(JNIEnv *env, jclass obj, jlong linePtr);

    /************ diff binary *************/
    /** unsigned int contains_data*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniBinaryGetContainsData)(JNIEnv *env, jclass obj, jlong binaryPtr);
    /** git_diff_binary_file old_file*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Diff_jniBinaryGetOldFile)(JNIEnv *env, jclass obj, jlong binaryPtr);
    /** git_diff_binary_file new_file*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Diff_jniBinaryGetNewFile)(JNIEnv *env, jclass obj, jlong binaryPtr);

    /************ diff binary file *************/
    /** size_t type*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniBinaryFileGetType)(JNIEnv *env, jclass obj, jlong binaryFilePtr);
    /** const char *data*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Diff_jniBinaryFileGetData)(JNIEnv *env, jclass obj, jlong binaryFilePtr);
    /** size_t datalen*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniBinaryFileGetDatalen)(JNIEnv *env, jclass obj, jlong binaryFilePtr);
    /** size_t inflatedlen*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniBinaryFileGetInflatedlen)(JNIEnv *env, jclass obj, jlong binaryFilePtr);

    JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniDiffOptionsSetPathSpec)(JNIEnv *env, jclass obj, jlong diffOptionsPtr, jobjectArray pathSpecJArr);
    JNIEXPORT jobjectArray JNICALL J_MAKE_METHOD(Diff_jniDiffOptionsGetPathSpec)(JNIEnv *env, jclass obj, jlong diffOptionsPtr);

    JNIEXPORT void JNICALL J_MAKE_METHOD(Diff_jniDiffOptionsSetFlags)(JNIEnv *env, jclass obj, jlong diffOptionsPtr, jint flags);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Diff_jniDiffOptionsGetFlags)(JNIEnv *env, jclass obj, jlong diffOptionsPtr);

#ifdef __cplusplus
}
#endif
#endif
