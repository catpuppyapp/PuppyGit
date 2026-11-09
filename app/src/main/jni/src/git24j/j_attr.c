#include "j_attr.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>
extern j_constants_t *jniConstants;

int j_git_attr_foreach_cb(const char *name, const char *value, void *payload)
{
    if (!payload)
    {
        return 0;
    }
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    jobject callback = j_payload->callback;
    jmethodID mid = j_payload->mid;
    if (!callback || !mid)
    {
        return 0;
    }
    JNIEnv *env = getEnv();
    jstring jName = (*env)->NewStringUTF(env, name);
    jstring jValue = (*env)->NewStringUTF(env, value);
    int r = (*env)->CallIntMethod(env, callback, mid, jName, jValue);
    (*env)->DeleteLocalRef(env, jName);
    (*env)->DeleteLocalRef(env, jValue);
    return r;
}

/** git_attr_value_t git_attr_value(const char *attr); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniValue)(JNIEnv *env, jclass obj, jstring attr)
{
    char *c_attr = j_copy_of_jstring(env, attr, true);
    git_attr_value_t r = git_attr_value(c_attr);
    free(c_attr);
    return r;
}

/** int git_attr_get(const char **value_out, git_repository *repo, int flags, const char *path, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniGet)(JNIEnv *env, jclass obj, jobject valueOut, jlong repoPtr, jint flags, jstring path, jstring name)
{
    const char *c_value_out;
    char *c_path = j_copy_of_jstring(env, path, true);
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_attr_get(&c_value_out, (git_repository *)repoPtr, flags, c_path, c_name);
    jstring j_value_out = (*env)->NewStringUTF(env, c_value_out);
    (*env)->CallVoidMethod(env, valueOut, jniConstants->midAtomicReferenceSet, j_value_out);
    (*env)->DeleteLocalRef(env, j_value_out);
    free(c_path);
    free(c_name);
    return r;
}

/** int git_attr_get_many(const char **values_out, git_repository *repo, int flags, const char *path, size_t num_attr, const char **names); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniGetMany)(JNIEnv *env, jclass obj, jobject valuesOut, jlong repoPtr, jint flags, jstring path, jobjectArray names)
{

    git_strarray names_arr = {0};
    j_strarray_from_java(env, &names_arr, names);
    git_strarray out = {0};
    git_strarray_copy(&out, &names_arr);
    char *c_path = j_copy_of_jstring(env, path, true);

    int r = git_attr_get_many((const char **)out.strings, (git_repository *)repoPtr, flags, c_path, names_arr.count, (const char **)names_arr.strings);
    j_strarray_to_java_list(env, &out, valuesOut);

    free(c_path);
    git_strarray_free(&out);
    git_strarray_free(&names_arr);

    return r;
}

/** int git_attr_foreach(git_repository *repo, int flags, const char *path, git_attr_foreach_cb callback, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jint flags, jstring path, jobject callback)
{
    char *c_path = j_copy_of_jstring(env, path, true);
    j_cb_payload payload = {0};
    j_cb_payload_init(env, &payload, callback, "(Ljava/lang/String;Ljava/lang/String;)I");
    int r = git_attr_foreach((git_repository *)repoPtr, flags, c_path, j_git_attr_foreach_cb, &payload);
    j_cb_payload_release(env, &payload);
    free(c_path);
    return r;
}

/** int git_attr_cache_flush(git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniCacheFlush)(JNIEnv *env, jclass obj, jlong repoPtr)
{
    int r = git_attr_cache_flush((git_repository *)repoPtr);
    return r;
}

/** int git_attr_add_macro(git_repository *repo, const char *name, const char *values); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Attr_jniAddMacro)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jstring values)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_values = j_copy_of_jstring(env, values, true);
    int r = git_attr_add_macro((git_repository *)repoPtr, c_name, c_values);
    free(c_name);
    free(c_values);
    return r;
}
