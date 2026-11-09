#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_MERGE_H__
#define __GIT24J_MERGE_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_merge_file_init_input(git_merge_file_input *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInitInput)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    /** int git_merge_file_input_init(git_merge_file_input *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInputInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInputNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    /** unsigned int version*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInputGetVersion)(JNIEnv *env, jclass obj, jlong fileInputPtr);
    /** const char *ptr*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileInputGetPtr)(JNIEnv *env, jclass obj, jlong fileInputPtr);
    /** size_t size*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInputGetSize)(JNIEnv *env, jclass obj, jlong fileInputPtr);
    /** const char *path*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileInputGetPath)(JNIEnv *env, jclass obj, jlong fileInputPtr);
    /** int mode*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInputGetMode)(JNIEnv *env, jclass obj, jlong fileInputPtr);
    /** unsigned int version*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputSetVersion)(JNIEnv *env, jclass obj, jlong fileInputPtr, jint version);
    /** const char *ptr*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputSetPtr)(JNIEnv *env, jclass obj, jlong fileInputPtr, jstring ptr);
    /** size_t size*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputSetSize)(JNIEnv *env, jclass obj, jlong fileInputPtr, jint size);
    /** const char *path*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputSetPath)(JNIEnv *env, jclass obj, jlong fileInputPtr, jstring path);
    /** int mode*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputSetMode)(JNIEnv *env, jclass obj, jlong fileInputPtr, jint mode);

    /** int git_merge_file_init_options(git_merge_file_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    /** int git_merge_file_options_init(git_merge_file_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr);
    /** -------- Signature of the header ---------- */
    /** unsigned int version*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetVersion)(JNIEnv *env, jclass obj, jlong fileOptionsPtr);
    /** const char *ancestor_label*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetAncestorLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr);
    /** const char *our_label*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetOurLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr);
    /** const char *their_label*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetTheirLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr);
    /** git_merge_file_favor_t favor*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetFavor)(JNIEnv *env, jclass obj, jlong fileOptionsPtr);
    /** uint32_t flags*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetFlags)(JNIEnv *env, jclass obj, jlong fileOptionsPtr);
    /** unsigned short marker_size*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetMarkerSize)(JNIEnv *env, jclass obj, jlong fileOptionsPtr);
    /** unsigned int version*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetVersion)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jint version);
    /** const char *ancestor_label*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetAncestorLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jstring ancestorLabel);
    /** const char *our_label*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetOurLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jstring ourLabel);
    /** const char *their_label*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetTheirLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jstring theirLabel);
    /** git_merge_file_favor_t favor*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetFavor)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jint favor);
    /** uint32_t flags*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetFlags)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jint flags);
    /** unsigned short marker_size*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetMarkerSize)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jint markerSize);

    /** int git_merge_init_options(git_merge_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    /** int git_merge_options_init(git_merge_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version);
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsFree)(JNIEnv *env, jclass obj, jlong opts);
    /** unsigned int version*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** uint32_t flags*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** unsigned int rename_threshold*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetRenameThreshold)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** unsigned int target_limit*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetTargetLimit)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** git_diff_similarity_metric *metric*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Merge_jniOptionsGetMetric)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** unsigned int recursion_limit*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetRecursionLimit)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** const char *default_driver*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniOptionsGetDefaultDriver)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** git_merge_file_favor_t file_favor*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetFileFavor)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** uint32_t file_flags*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetFileFlags)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** unsigned int version*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr, jint version);
    /** uint32_t flags*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr, jint flags);
    /** unsigned int rename_threshold*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetRenameThreshold)(JNIEnv *env, jclass obj, jlong optionsPtr, jint renameThreshold);
    /** unsigned int target_limit*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetTargetLimit)(JNIEnv *env, jclass obj, jlong optionsPtr, jint targetLimit);
    /** git_diff_similarity_metric *metric*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetMetric)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong metric);
    /** unsigned int recursion_limit*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetRecursionLimit)(JNIEnv *env, jclass obj, jlong optionsPtr, jint recursionLimit);
    /** const char *default_driver*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetDefaultDriver)(JNIEnv *env, jclass obj, jlong optionsPtr, jstring defaultDriver);
    /** git_merge_file_favor_t file_favor*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetFileFavor)(JNIEnv *env, jclass obj, jlong optionsPtr, jint fileFavor);
    /** uint32_t file_flags*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetFileFlags)(JNIEnv *env, jclass obj, jlong optionsPtr, jint fileFlags);

    /** int git_merge_base(git_oid *out, git_repository *repo, const git_oid *one, const git_oid *two); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBase)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jobject one, jobject two);

    /** int git_merge_bases(git_oidarray *out, git_repository *repo, const git_oid *one, const git_oid *two); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBases)(JNIEnv *env, jclass obj, jobject outOids, jlong repoPtr, jobject one, jobject two);

    /** int git_merge_trees(git_index **out, git_repository *repo, const git_tree *ancestor_tree, const git_tree *our_tree, const git_tree *their_tree, const git_merge_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniTrees)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong ancestorTreePtr, jlong ourTreePtr, jlong theirTreePtr, jlong optsPtr);

    /** int git_merge_commits(git_index **out, git_repository *repo, const git_commit *our_commit, const git_commit *their_commit, const git_merge_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniCommits)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong ourCommitPtr, jlong theirCommitPtr, jlong optsPtr);

    /** int git_merge_analysis(git_merge_analysis_t *analysis_out, git_merge_preference_t *preference_out, git_repository *repo, const git_annotated_commit **their_heads, size_t their_heads_len); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniAnalysis)(JNIEnv *env, jclass obj, jobject analysisOut, jobject preferenceOut, jlong repoPtr, jlongArray theirHeads);

    /** int git_merge_analysis_for_ref(git_merge_analysis_t *analysis_out, git_merge_preference_t *preference_out, git_repository *repo, git_reference *our_ref, const git_annotated_commit **their_heads, size_t their_heads_len); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniAnalysisForRef)(JNIEnv *env, jclass obj, jobject analysisOut, jobject preferenceOut, jlong repoPtr, jlong ourRefPtr, jlongArray theirHeads);

    /** int git_merge_base_many(git_oid *out, git_repository *repo, size_t length, const git_oid [] input_array); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBaseMany)(JNIEnv *env, jclass obj, jobject outOid, jlong repoPtr, jobjectArray inputArray);

    /** int git_merge_bases_many(git_oidarray *out, git_repository *repo, size_t length, const git_oid [] input_array); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBasesMany)(JNIEnv *env, jclass obj, jobject outOids, jlong repoPtr, jobjectArray inputArray);

    /** int git_merge_base_octopus(git_oid *out, git_repository *repo, size_t length, const git_oid [] input_array); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBaseOctopus)(JNIEnv *env, jclass obj, jobject outOid, jlong repoPtr, jobjectArray inputArray);
    // no matching type found for 'git_merge_file_result *out'
    /** int git_merge_file(git_merge_file_result *out, const git_merge_file_input *ancestor, const git_merge_file_input *ours, const git_merge_file_input *theirs, const git_merge_file_options *opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFile)(JNIEnv *env, jclass obj, jobject out, jlong ancestorPtr, jlong oursPtr, jlong theirsPtr, jlong optsPtr);
    // no matching type found for 'git_merge_file_result *out'
    /** int git_merge_file_from_index(
     *      git_merge_file_result *out, 
     *      git_repository *repo,
     *      const git_index_entry *ancestor, 
     *      const git_index_entry *ours,
     *      const git_index_entry *theirs,
     *      const git_merge_file_options *opts); 
     * */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileFromIndex)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong ancestorPtr, jlong oursPtr, jlong theirsPtr, jlong optsPtr);

    /** -------- git_merge_file_result ---------- */
    /** void git_merge_file_result_free(git_merge_file_result *result); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileResultFree)(JNIEnv *env, jclass obj, jlong resultPtr);
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Merge_jniFileResultNew)(JNIEnv *env, jclass obj);
    /** unsigned int automergeable*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileResultGetAutomergeable)(JNIEnv *env, jclass obj, jlong fileResultPtr);
    /** const char *path*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileResultGetPath)(JNIEnv *env, jclass obj, jlong fileResultPtr);
    /** unsigned int mode*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileResultGetMode)(JNIEnv *env, jclass obj, jlong fileResultPtr);
    /** const char *ptr*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileResultGetPtr)(JNIEnv *env, jclass obj, jlong fileResultPtr);
    /** size_t len*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileResultGetLen)(JNIEnv *env, jclass obj, jlong fileResultPtr);

    /** int git_merge_create(git_repository *repo, const git_annotated_commit **their_heads, size_t their_heads_len, const git_merge_options *merge_opts, const git_checkout_options *checkout_opts); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniMerge)(JNIEnv *env, jclass obj, jlong repoPtr, jlongArray theirHeads, jlong mergeOptsPtr, jlong checkoutOpts);

#ifdef __cplusplus
}
#endif
#endif