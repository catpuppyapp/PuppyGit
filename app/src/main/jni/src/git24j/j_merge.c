#include "j_merge.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;
/** int git_merge_file_init_input(git_merge_file_input *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInitInput)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_merge_file_init_input((git_merge_file_input *)optsPtr, version);
    return r;
}

/** int git_merge_file_input_init(git_merge_file_input *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInputInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_merge_file_input_init((git_merge_file_input *)optsPtr, version);
    return r;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInputNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version)
{
    git_merge_file_input *opts = (git_merge_file_input *)malloc(sizeof(git_merge_file_input));
    int r = git_merge_file_init_input(opts, version);
    (*env)->CallVoidMethod(env, outOpts, jniConstants->midAtomicLongSet, (jlong)opts);
    return r;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputFree)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    git_merge_file_input *ptr = (git_merge_file_input *)optsPtr;
    free((char *)ptr->path);
    free((char *)ptr->ptr);
    free(ptr);
}

/** unsigned int version*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInputGetVersion)(JNIEnv *env, jclass obj, jlong fileInputPtr)
{
    return ((git_merge_file_input *)fileInputPtr)->version;
}

/** const char *ptr*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileInputGetPtr)(JNIEnv *env, jclass obj, jlong fileInputPtr)
{
    return (*env)->NewStringUTF(env, ((git_merge_file_input *)fileInputPtr)->ptr);
}

/** size_t size*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInputGetSize)(JNIEnv *env, jclass obj, jlong fileInputPtr)
{
    return ((git_merge_file_input *)fileInputPtr)->size;
}

/** const char *path*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileInputGetPath)(JNIEnv *env, jclass obj, jlong fileInputPtr)
{
    return (*env)->NewStringUTF(env, ((git_merge_file_input *)fileInputPtr)->path);
}

/** int mode*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInputGetMode)(JNIEnv *env, jclass obj, jlong fileInputPtr)
{
    return ((git_merge_file_input *)fileInputPtr)->mode;
}

/** unsigned int version*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputSetVersion)(JNIEnv *env, jclass obj, jlong fileInputPtr, jint version)
{
    ((git_merge_file_input *)fileInputPtr)->version = (unsigned int)version;
}

/** const char *ptr*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputSetPtr)(JNIEnv *env, jclass obj, jlong fileInputPtr, jstring ptr)
{
    ((git_merge_file_input *)fileInputPtr)->ptr = j_copy_of_jstring(env, ptr, false);
}

/** size_t size*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputSetSize)(JNIEnv *env, jclass obj, jlong fileInputPtr, jint size)
{
    ((git_merge_file_input *)fileInputPtr)->size = (size_t)size;
}

/** const char *path*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputSetPath)(JNIEnv *env, jclass obj, jlong fileInputPtr, jstring path)
{
    ((git_merge_file_input *)fileInputPtr)->path = j_copy_of_jstring(env, path, false);
}

/** int mode*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileInputSetMode)(JNIEnv *env, jclass obj, jlong fileInputPtr, jint mode)
{
    ((git_merge_file_input *)fileInputPtr)->mode = (int)mode;
}

/** int git_merge_file_init_options(git_merge_file_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_merge_file_init_options((git_merge_file_options *)optsPtr, version);
    return r;
}

/** int git_merge_file_options_init(git_merge_file_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_merge_file_options_init((git_merge_file_options *)optsPtr, version);
    return r;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version)
{
    git_merge_file_options *opts = (git_merge_file_options *)malloc(sizeof(git_merge_file_options));
    opts->ancestor_label = NULL;
    opts->our_label = NULL;
    opts->their_label = NULL;
    int r = git_merge_file_init_options((git_merge_file_options *)opts, version);
    (*env)->CallVoidMethod(env, outOpts, jniConstants->midAtomicLongSet, (jlong)opts);
    return r;
}

/** unsigned int version*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetVersion)(JNIEnv *env, jclass obj, jlong fileOptionsPtr)
{
    return ((git_merge_file_options *)fileOptionsPtr)->version;
}

/** const char *ancestor_label*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetAncestorLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr)
{
    return (*env)->NewStringUTF(env, ((git_merge_file_options *)fileOptionsPtr)->ancestor_label);
}

/** const char *our_label*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetOurLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr)
{
    return (*env)->NewStringUTF(env, ((git_merge_file_options *)fileOptionsPtr)->our_label);
}

/** const char *their_label*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetTheirLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr)
{
    return (*env)->NewStringUTF(env, ((git_merge_file_options *)fileOptionsPtr)->their_label);
}

/** git_merge_file_favor_t favor*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetFavor)(JNIEnv *env, jclass obj, jlong fileOptionsPtr)
{
    return ((git_merge_file_options *)fileOptionsPtr)->favor;
}

/** uint32_t flags*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetFlags)(JNIEnv *env, jclass obj, jlong fileOptionsPtr)
{
    return ((git_merge_file_options *)fileOptionsPtr)->flags;
}

/** int marker_size*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileOptionsGetMarkerSize)(JNIEnv *env, jclass obj, jlong fileOptionsPtr)
{
    return ((git_merge_file_options *)fileOptionsPtr)->marker_size;
}

/** unsigned int version*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetVersion)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jint version)
{
    ((git_merge_file_options *)fileOptionsPtr)->version = (unsigned int)version;
}

/** const char *ancestor_label*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetAncestorLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jstring ancestorLabel)
{
    ((git_merge_file_options *)fileOptionsPtr)->ancestor_label = j_copy_of_jstring(env, ancestorLabel, true);
}

/** const char *our_label*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetOurLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jstring ourLabel)
{
    ((git_merge_file_options *)fileOptionsPtr)->our_label = j_copy_of_jstring(env, ourLabel, true);
}

/** const char *their_label*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetTheirLabel)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jstring theirLabel)
{
    ((git_merge_file_options *)fileOptionsPtr)->their_label = j_copy_of_jstring(env, theirLabel, true);
}

/** git_merge_file_favor_t favor*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetFavor)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jint favor)
{
    ((git_merge_file_options *)fileOptionsPtr)->favor = (git_merge_file_favor_t)favor;
}

/** uint32_t flags*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetFlags)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jint flags)
{
    ((git_merge_file_options *)fileOptionsPtr)->flags = (uint32_t)flags;
}

/** int marker_size*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsSetMarkerSize)(JNIEnv *env, jclass obj, jlong fileOptionsPtr, jint markerSize)
{
    ((git_merge_file_options *)fileOptionsPtr)->marker_size = (int)markerSize;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileOptionsFree)(JNIEnv *env, jclass obj, jlong optsPtr)
{
    git_merge_file_options *ptr = (git_merge_file_options *)optsPtr;
    free((char *)ptr->ancestor_label);
    free((char *)ptr->our_label);
    free((char *)ptr->their_label);
    free((git_merge_file_options *)optsPtr);
}

/** int git_merge_init_options(git_merge_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_merge_init_options((git_merge_options *)optsPtr, version);
    return r;
}

/** int git_merge_options_init(git_merge_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsInit)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_merge_options_init((git_merge_options *)optsPtr, version);
    return r;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version)
{
    git_merge_options *opts = (git_merge_options *)malloc(sizeof(git_merge_options));
    opts->default_driver = NULL;
    int r = git_merge_init_options((git_merge_options *)opts, version);
    (*env)->CallVoidMethod(env, outOpts, jniConstants->midAtomicLongSet, (jlong)opts);
    return r;
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsFree)(JNIEnv *env, jclass obj, jlong opts)
{
    git_merge_options *ptr = (git_merge_options *)opts;
    free((char *)ptr->default_driver);
    free(ptr);
}

/** unsigned int version*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_merge_options *)optionsPtr)->version;
}

/** uint32_t flags*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_merge_options *)optionsPtr)->flags;
}

/** unsigned int rename_threshold*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetRenameThreshold)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_merge_options *)optionsPtr)->rename_threshold;
}

/** unsigned int target_limit*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetTargetLimit)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_merge_options *)optionsPtr)->target_limit;
}

/** git_diff_similarity_metric *metric*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Merge_jniOptionsGetMetric)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return (jlong)((git_merge_options *)optionsPtr)->metric;
}

/** unsigned int recursion_limit*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetRecursionLimit)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_merge_options *)optionsPtr)->recursion_limit;
}

/** const char *default_driver*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniOptionsGetDefaultDriver)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return (*env)->NewStringUTF(env, ((git_merge_options *)optionsPtr)->default_driver);
}

/** git_merge_file_favor_t file_favor*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetFileFavor)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_merge_options *)optionsPtr)->file_favor;
}

/** uint32_t file_flags*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniOptionsGetFileFlags)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_merge_options *)optionsPtr)->file_flags;
}

/** unsigned int version*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr, jint version)
{
    ((git_merge_options *)optionsPtr)->version = (unsigned int)version;
}

/** uint32_t flags*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetFlags)(JNIEnv *env, jclass obj, jlong optionsPtr, jint flags)
{
    ((git_merge_options *)optionsPtr)->flags = (uint32_t)flags;
}

/** unsigned int rename_threshold*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetRenameThreshold)(JNIEnv *env, jclass obj, jlong optionsPtr, jint renameThreshold)
{
    ((git_merge_options *)optionsPtr)->rename_threshold = (unsigned int)renameThreshold;
}

/** unsigned int target_limit*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetTargetLimit)(JNIEnv *env, jclass obj, jlong optionsPtr, jint targetLimit)
{
    ((git_merge_options *)optionsPtr)->target_limit = (unsigned int)targetLimit;
}

/** git_diff_similarity_metric *metric*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetMetric)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong metric)
{
    ((git_merge_options *)optionsPtr)->metric = (git_diff_similarity_metric *)metric;
}

/** unsigned int recursion_limit*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetRecursionLimit)(JNIEnv *env, jclass obj, jlong optionsPtr, jint recursionLimit)
{
    ((git_merge_options *)optionsPtr)->recursion_limit = (unsigned int)recursionLimit;
}

/** const char *default_driver*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetDefaultDriver)(JNIEnv *env, jclass obj, jlong optionsPtr, jstring defaultDriver)
{
    if (defaultDriver == NULL)
    {
        return;
    }
    // (*env)->GetStringChars(env, )
    ((git_merge_options *)optionsPtr)->default_driver = j_copy_of_jstring(env, defaultDriver, false);
}

/** git_merge_file_favor_t file_favor*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetFileFavor)(JNIEnv *env, jclass obj, jlong optionsPtr, jint fileFavor)
{
    ((git_merge_options *)optionsPtr)->file_favor = (git_merge_file_favor_t)fileFavor;
}

/** uint32_t file_flags*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniOptionsSetFileFlags)(JNIEnv *env, jclass obj, jlong optionsPtr, jint fileFlags)
{
    ((git_merge_options *)optionsPtr)->file_flags = (uint32_t)fileFlags;
}

/** int git_merge_base(git_oid *out, git_repository *repo, const git_oid *one, const git_oid *two); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBase)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jobject one, jobject two)
{
    git_oid c_out;
    git_oid c_one;
    j_git_oid_from_java(env, one, &c_one);
    git_oid c_two;
    j_git_oid_from_java(env, two, &c_two);
    int r = git_merge_base(&c_out, (git_repository *)repoPtr, &c_one, &c_two);
    j_git_oid_to_java(env, &c_out, out);
    return r;
}

/** int git_merge_bases(git_oidarray *out, git_repository *repo, const git_oid *one, const git_oid *two); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBases)(JNIEnv *env, jclass obj, jobject outOids, jlong repoPtr, jobject one, jobject two)
{
    git_oid c_one;
    j_git_oid_from_java(env, one, &c_one);
    git_oid c_two;
    j_git_oid_from_java(env, two, &c_two);
    git_oidarray c_arr = {NULL, 0};
    int r = git_merge_bases(&c_arr, (git_repository *)repoPtr, &c_one, &c_two);
    j_git_oidarray_to_java(env, outOids, &c_arr);
    git_oidarray_free(&c_arr);
    return r;
}

/** int git_merge_trees(git_index **out, git_repository *repo, const git_tree *ancestor_tree, const git_tree *our_tree, const git_tree *their_tree, const git_merge_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniTrees)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong ancestorTreePtr, jlong ourTreePtr, jlong theirTreePtr, jlong optsPtr)
{
    git_index *c_out = 0;
    int r = git_merge_trees(&c_out, (git_repository *)repoPtr, (git_tree *)ancestorTreePtr, (git_tree *)ourTreePtr, (git_tree *)theirTreePtr, (git_merge_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** int git_merge_commits(git_index **out, git_repository *repo, const git_commit *our_commit, const git_commit *their_commit, const git_merge_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniCommits)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong ourCommitPtr, jlong theirCommitPtr, jlong optsPtr)
{
    git_index *c_out = 0;
    int r = git_merge_commits(&c_out, (git_repository *)repoPtr, (git_commit *)ourCommitPtr, (git_commit *)theirCommitPtr, (git_merge_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** int git_merge_analysis(git_merge_analysis_t *analysis_out, git_merge_preference_t *preference_out, git_repository *repo, const git_annotated_commit **their_heads, size_t their_heads_len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniAnalysis)(JNIEnv *env, jclass obj, jobject analysisOut, jobject preferenceOut, jlong repoPtr, jlongArray theirHeads)
{
    jsize theirHeadsLen = (*env)->GetArrayLength(env, theirHeads);
    jlong *elements = (*env)->GetLongArrayElements(env, theirHeads, 0);
//    const git_annotated_commit **their_heads = (const git_annotated_commit **)malloc(sizeof(const git_annotated_commit *) * theirHeadsLen);
//    for (jsize i = 0; i < theirHeadsLen; i++)
//    {
//        their_heads[i] = (const git_annotated_commit *)elements[i];
//    }

    git_merge_analysis_t analysis_out;
    git_merge_preference_t preference_out;
    int r = git_merge_analysis(&analysis_out, &preference_out, (git_repository *)repoPtr, (git_annotated_commit **)elements, theirHeadsLen);
    (*env)->CallVoidMethod(env, analysisOut, jniConstants->midAtomicIntSet, analysis_out);
    (*env)->CallVoidMethod(env, preferenceOut, jniConstants->midAtomicIntSet, preference_out);
//    free(their_heads);
    (*env)->ReleaseLongArrayElements(env, theirHeads, elements, 0);
    return r;
}

/** int git_merge_analysis_for_ref(git_merge_analysis_t *analysis_out, git_merge_preference_t *preference_out, git_repository *repo, git_reference *our_ref, const git_annotated_commit **their_heads, size_t their_heads_len); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniAnalysisForRef)(JNIEnv *env, jclass obj, jobject analysisOut, jobject preferenceOut, jlong repoPtr, jlong ourRefPtr, jlongArray theirHeads)
{
    jsize theirHeadsLen = (*env)->GetArrayLength(env, theirHeads);
    jlong *elements = (*env)->GetLongArrayElements(env, theirHeads, 0);
//    const git_annotated_commit **their_heads = (const git_annotated_commit **)malloc(sizeof(const git_annotated_commit *) * theirHeadsLen);
//    for (jsize i = 0; i < theirHeadsLen; i++)
//    {
//        their_heads[i] = (const git_annotated_commit *)elements[i];
//    }
    git_merge_analysis_t analysis_out;
    git_merge_preference_t preference_out;
    int r = git_merge_analysis_for_ref(&analysis_out, &preference_out, (git_repository *)repoPtr, (git_reference *)ourRefPtr, (git_annotated_commit **)elements, theirHeadsLen);
    (*env)->CallVoidMethod(env, analysisOut, jniConstants->midAtomicIntSet, analysis_out);
    (*env)->CallVoidMethod(env, preferenceOut, jniConstants->midAtomicIntSet, preference_out);
//    free(their_heads);
    (*env)->ReleaseLongArrayElements(env, theirHeads, elements, 0);
    return r;
}

/** int git_merge_base_many(git_oid *out, git_repository *repo, size_t length, const git_oid [] input_array); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBaseMany)(JNIEnv *env, jclass obj, jobject outOid, jlong repoPtr, jobjectArray inputArray)
{
    jsize arrayLen = (*env)->GetArrayLength(env, inputArray);
    git_oid *input_array = (git_oid *)malloc(sizeof(git_oid) * arrayLen);
    for (jsize i = 0; i < arrayLen; i++)
    {
        jobject xi = (*env)->GetObjectArrayElement(env, inputArray, i);
        j_git_oid_from_java(env, xi, &input_array[i]);
        (*env)->DeleteLocalRef(env, xi);
    }
    git_oid out_oid;
    int r = git_merge_base_many(&out_oid, (git_repository *)repoPtr, arrayLen, input_array);
    j_git_oid_to_java(env, &out_oid, outOid);
    free(input_array);
    return r;
}

/** int git_merge_bases_many(git_oidarray *out, git_repository *repo, size_t length, const git_oid [] input_array); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBasesMany)(JNIEnv *env, jclass obj, jobject outOids, jlong repoPtr, jobjectArray inputArray)
{
    jsize arrayLen = (*env)->GetArrayLength(env, inputArray);
    git_oid *input_array = (git_oid *)malloc(sizeof(git_oid) * arrayLen);
    for (jsize i = 0; i < arrayLen; i++)
    {
        jobject xi = (*env)->GetObjectArrayElement(env, inputArray, i);
        j_git_oid_from_java(env, xi, &input_array[i]);
        (*env)->DeleteLocalRef(env, xi);
    }
    git_oidarray c_out;
    int r = git_merge_bases_many(&c_out, (git_repository *)repoPtr, arrayLen, input_array);
    j_git_oidarray_to_java(env, outOids, &c_out);
    git_oidarray_free(&c_out);
    free(input_array);
    return r;
}

/** int git_merge_base_octopus(git_oid *out, git_repository *repo, size_t length, const git_oid [] input_array); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniBaseOctopus)(JNIEnv *env, jclass obj, jobject outOid, jlong repoPtr, jobjectArray inputArray)
{
    jsize arrayLen = (*env)->GetArrayLength(env, inputArray);
    git_oid *input_array = (git_oid *)malloc(sizeof(git_oid) * arrayLen);
    for (jsize i = 0; i < arrayLen; i++)
    {
        jobject xi = (*env)->GetObjectArrayElement(env, inputArray, i);
        j_git_oid_from_java(env, xi, &input_array[i]);
        (*env)->DeleteLocalRef(env, xi);
    }
    git_oid out_oid;
    int r = git_merge_base_octopus(&out_oid, (git_repository *)repoPtr, arrayLen, input_array);
    j_git_oid_to_java(env, &out_oid, outOid);
    free(input_array);
    return r;
}

/** int git_merge_file(git_merge_file_result *out, const git_merge_file_input *ancestor, const git_merge_file_input *ours, const git_merge_file_input *theirs, const git_merge_file_options *opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFile)(JNIEnv *env, jclass obj, jobject out, jlong ancestorPtr, jlong oursPtr, jlong theirsPtr, jlong optsPtr)
{
    git_merge_file_result *c_out = (git_merge_file_result *)malloc(sizeof(git_merge_file_result));
    int r = git_merge_file(
        c_out,
        (const git_merge_file_input *)ancestorPtr,
        (const git_merge_file_input *)oursPtr,
        (const git_merge_file_input *)theirsPtr,
        (const git_merge_file_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** int git_merge_file_from_index(
 *      git_merge_file_result *out,
 *      git_repository *repo,
 *      const git_index_entry *ancestor,
 *      const git_index_entry *ours,
 *      const git_index_entry *theirs,
 *      const git_merge_file_options *opts);
 * */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileFromIndex)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong ancestorPtr, jlong oursPtr, jlong theirsPtr, jlong optsPtr)
{
    git_merge_file_result *c_out = (git_merge_file_result *)malloc(sizeof(git_merge_file_result));
    int r = git_merge_file_from_index(
        c_out,
        (git_repository *)repoPtr,
        (const git_index_entry *)ancestorPtr,
        (const git_index_entry *)oursPtr,
        (const git_index_entry *)theirsPtr,
        (const git_merge_file_options *)optsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** -------- git_merge_file_result ---------- */
/** void git_merge_file_result_free(git_merge_file_result *result);
 * Note: this also frees the resultPtr itself.
 */
JNIEXPORT void JNICALL J_MAKE_METHOD(Merge_jniFileResultFree)(JNIEnv *env, jclass obj, jlong resultPtr)
{
    git_merge_file_result_free((git_merge_file_result *)resultPtr);
    free((git_merge_file_result *)resultPtr);
}

JNIEXPORT jlong JNICALL J_MAKE_METHOD(Merge_jniFileResultNew)(JNIEnv *env, jclass obj)
{
    git_merge_file_result *result = (git_merge_file_result *)malloc(sizeof(git_merge_file_result));
    return (jlong)result;
}

/** unsigned int automergeable*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileResultGetAutomergeable)(JNIEnv *env, jclass obj, jlong fileResultPtr)
{
    return ((git_merge_file_result *)fileResultPtr)->automergeable;
}

/** const char *path*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileResultGetPath)(JNIEnv *env, jclass obj, jlong fileResultPtr)
{
    return (*env)->NewStringUTF(env, ((git_merge_file_result *)fileResultPtr)->path);
}

/** unsigned int mode*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileResultGetMode)(JNIEnv *env, jclass obj, jlong fileResultPtr)
{
    return ((git_merge_file_result *)fileResultPtr)->mode;
}

/** const char *ptr*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Merge_jniFileResultGetPtr)(JNIEnv *env, jclass obj, jlong fileResultPtr)
{
    return (*env)->NewStringUTF(env, ((git_merge_file_result *)fileResultPtr)->ptr);
}

/** size_t len*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniFileResultGetLen)(JNIEnv *env, jclass obj, jlong fileResultPtr)
{
    return ((git_merge_file_result *)fileResultPtr)->len;
}

/** int git_merge_create(git_repository *repo, const git_annotated_commit **their_heads, size_t their_heads_len, const git_merge_options *merge_opts, const git_checkout_options *checkout_opts); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Merge_jniMerge)(JNIEnv *env, jclass obj, jlong repoPtr, jlongArray theirHeads, jlong mergeOptsPtr, jlong checkoutOpts)
{
    jsize arrayLen = (*env)->GetArrayLength(env, theirHeads);
    jlong *elements = (*env)->GetLongArrayElements(env, theirHeads, 0);
    int r = git_merge((git_repository *)repoPtr, (const git_annotated_commit **)elements, arrayLen, (const git_merge_options *)mergeOptsPtr, (const git_checkout_options *)checkoutOpts);
    (*env)->ReleaseLongArrayElements(env, theirHeads, elements, 0);
    return r;
}
