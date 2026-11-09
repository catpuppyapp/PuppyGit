#include "j_filter_list.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** -------- Wrapper Body ---------- */
/** int git_filterList_load(git_filter_list **filters, git_repository *repo, git_blob *blob, const char *path, int mode, int flags); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniLoad)(JNIEnv *env, jclass obj, jobject filters, jlong repoPtr, jlong blobPtr, jstring path, jint mode, jint flags)
{
    git_filter_list *c_filters;
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_filter_list_load(&c_filters, (git_repository *)repoPtr, (git_blob *)blobPtr, c_path, mode, flags);
    (*env)->CallVoidMethod(env, filters, jniConstants->midAtomicLongSet, (jlong)c_filters);
    /* git_filter_list_free(c_filters); */
    free(c_path);
    return r;
}

/** int git_filterList_contains(git_filter_list *filters, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniContains)(JNIEnv *env, jclass obj, jlong filtersPtr, jstring name)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_filter_list_contains((git_filter_list *)filtersPtr, c_name);
    free(c_name);
    return r;
}

/** int git_filterList_apply_to_data(git_buf *out, git_filter_list *filters, git_buf *in); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniApplyToData)(JNIEnv *env, jclass obj, jobject out, jlong filtersPtr, jstring in)
{
    git_buf c_out = {0};
    git_buf c_in = {0};
    j_git_buf_of_jstring(env, &c_in, in);
    int r = git_filter_list_apply_to_data(&c_out, (git_filter_list *)filtersPtr, &c_in);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    git_buf_dispose(&c_in);
    return r;
}

/** int git_filterList_apply_to_file(git_buf *out, git_filter_list *filters, git_repository *repo, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniApplyToFile)(JNIEnv *env, jclass obj, jobject out, jlong filtersPtr, jlong repoPtr, jstring path)
{
    git_buf c_out = {0};
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_filter_list_apply_to_file(&c_out, (git_filter_list *)filtersPtr, (git_repository *)repoPtr, c_path);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    free(c_path);
    return r;
}

/** int git_filterList_apply_to_blob(git_buf *out, git_filter_list *filters, git_blob *blob); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniApplyToBlob)(JNIEnv *env, jclass obj, jobject out, jlong filtersPtr, jlong blobPtr)
{
    git_buf c_out = {0};
    int r = git_filter_list_apply_to_blob(&c_out, (git_filter_list *)filtersPtr, (git_blob *)blobPtr);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    return r;
}

/** int git_filterList_stream_data(git_filter_list *filters, git_buf *data, git_writestream *target); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniStreamData)(JNIEnv *env, jclass obj, jlong filtersPtr, jstring data, jlong targetPtr)
{
    git_buf c_data = {0};
    j_git_buf_of_jstring(env, &c_data, data);
    int r = git_filter_list_stream_data((git_filter_list *)filtersPtr, &c_data, (git_writestream *)targetPtr);
    git_buf_dispose(&c_data);
    return r;
}

/** int git_filterList_stream_file(git_filter_list *filters, git_repository *repo, const char *path, git_writestream *target); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniStreamFile)(JNIEnv *env, jclass obj, jlong filtersPtr, jlong repoPtr, jstring path, jlong targetPtr)
{
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_filter_list_stream_file((git_filter_list *)filtersPtr, (git_repository *)repoPtr, c_path, (git_writestream *)targetPtr);
    free(c_path);
    return r;
}

/** int git_filterList_stream_blob(git_filter_list *filters, git_blob *blob, git_writestream *target); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(FilterList_jniStreamBlob)(JNIEnv *env, jclass obj, jlong filtersPtr, jlong blobPtr, jlong targetPtr)
{
    int r = git_filter_list_stream_blob((git_filter_list *)filtersPtr, (git_blob *)blobPtr, (git_writestream *)targetPtr);
    return r;
}

/** void git_filterList_free(git_filter_list *filters); */
JNIEXPORT void JNICALL J_MAKE_METHOD(FilterList_jniFree)(JNIEnv *env, jclass obj, jlong filtersPtr)
{
    git_filter_list_free((git_filter_list *)filtersPtr);
}
