#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_CONFIG_H__
#define __GIT24J_CONFIG_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** const char *name*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Config_jniEntryGetName)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** const char *value*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Config_jniEntryGetValue)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** const char *value*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Config_jniEntryGetBackendType)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** const char *value*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Config_jniEntryGetOriginPath)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** unsigned int include_depth*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniEntryGetIncludeDepth)(JNIEnv *env, jclass obj, jlong entryPtr);
    /** git_config_level_t level*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniEntryGetLevel)(JNIEnv *env, jclass obj, jlong entryPtr);

    /** void git_config_entry_free(git_config_entry *entry); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Config_jniEntryFree)(JNIEnv *env, jclass obj, jlong entryPtr);

    /** int git_config_find_global(git_buf *out); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniFindGlobal)(JNIEnv *env, jclass obj, jobject out);

    /** int git_config_find_xdg(git_buf *out); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniFindXdg)(JNIEnv *env, jclass obj, jobject out);

    /** int git_config_find_system(git_buf *out); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniFindSystem)(JNIEnv *env, jclass obj, jobject out);

    /** int git_config_find_programdata(git_buf *out); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniFindProgramdata)(JNIEnv *env, jclass obj, jobject out);

    /** int git_config_open_default(git_config **out); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniOpenDefault)(JNIEnv *env, jclass obj, jobject out);

    /** int git_config_new(git_config **out); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniNew)(JNIEnv *env, jclass obj, jobject out);

    /** int git_config_add_file_ondisk(git_config *cfg, const char *path, git_config_level_t level, const git_repository *repo, int force); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniAddFileOndisk)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring path, jint level, jlong repoPtr, jint force);

    /** int git_config_open_ondisk(git_config **out, const char *path); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniOpenOndisk)(JNIEnv *env, jclass obj, jobject out, jstring path);

    /** int git_config_open_level(git_config **out, const git_config *parent, git_config_level_t level); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniOpenLevel)(JNIEnv *env, jclass obj, jobject out, jlong parentPtr, jint level);

    /** int git_config_open_global(git_config **out, git_config *config); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniOpenGlobal)(JNIEnv *env, jclass obj, jobject out, jlong configPtr);

    /** int git_config_snapshot(git_config **out, git_config *config); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSnapshot)(JNIEnv *env, jclass obj, jobject out, jlong configPtr);

    /** void git_config_free(git_config *cfg); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Config_jniFree)(JNIEnv *env, jclass obj, jlong cfgPtr);

    /** int git_config_get_entry(git_config_entry **out, const git_config *cfg, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetEntry)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name);

    /** int git_config_get_int32(int32_t *out, const git_config *cfg, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetInt32)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name);

    /** int git_config_get_int64(int64_t *out, const git_config *cfg, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetInt64)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name);

    /** int git_config_get_bool(int *out, const git_config *cfg, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetBool)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name);

    /** int git_config_get_path(git_buf *out, const git_config *cfg, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetPath)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name);

    /** int git_config_get_string(const char **out, const git_config *cfg, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetString)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name);

    /** int git_config_get_string_buf(git_buf *out, const git_config *cfg, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetStringBuf)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name);

    /** int git_config_get_multivar_foreach(const git_config *cfg, const char *name, const char *regexp, git_config_foreach_cb callback, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetMultivarForeach)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jstring regexp, jobject callback);

    /** int git_config_multivar_iterator_new(git_config_iterator **out, const git_config *cfg, const char *name, const char *regexp); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniMultivarIteratorNew)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name, jstring regexp);

    /** int git_config_next(git_config_entry **entry, git_config_iterator *iter); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniNext)(JNIEnv *env, jclass obj, jobject entry, jlong iterPtr);

    /** void git_config_iterator_free(git_config_iterator *iter); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Config_jniIteratorFree)(JNIEnv *env, jclass obj, jlong iterPtr);

    /** int git_config_set_int32(git_config *cfg, const char *name, int32_t value); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSetInt32)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jint value);

    /** int git_config_set_int64(git_config *cfg, const char *name, int64_t value); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSetInt64)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jlong value);

    /** int git_config_set_bool(git_config *cfg, const char *name, int value); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSetBool)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jint value);

    /** int git_config_set_string(git_config *cfg, const char *name, const char *value); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSetString)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jstring value);

    /** int git_config_set_multivar(git_config *cfg, const char *name, const char *regexp, const char *value); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSetMultivar)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jstring regexp, jstring value);

    /** int git_config_delete_entry(git_config *cfg, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniDeleteEntry)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name);

    /** int git_config_delete_multivar(git_config *cfg, const char *name, const char *regexp); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniDeleteMultivar)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jstring regexp);

    /** int git_config_foreach(const git_config *cfg, git_config_foreach_cb callback, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniForeach)(JNIEnv *env, jclass obj, jlong cfgPtr, jobject callback);

    /** int git_config_iterator_new(git_config_iterator **out, const git_config *cfg); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniIteratorNew)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr);

    /** int git_config_iterator_glob_new(git_config_iterator **out, const git_config *cfg, const char *regexp); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniIteratorGlobNew)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring regexp);

    /** int git_config_foreach_match(const git_config *cfg, const char *regexp, git_config_foreach_cb callback, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniForeachMatch)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring regexp, jobject callback);

    /** int git_config_parse_bool(int *out, const char *value); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniParseBool)(JNIEnv *env, jclass obj, jobject out, jstring value);

    /** int git_config_parse_int32(int32_t *out, const char *value); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniParseInt32)(JNIEnv *env, jclass obj, jobject out, jstring value);

    /** int git_config_parse_int64(int64_t *out, const char *value); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniParseInt64)(JNIEnv *env, jclass obj, jobject out, jstring value);

    /** int git_config_parse_path(git_buf *out, const char *value); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniParsePath)(JNIEnv *env, jclass obj, jobject out, jstring value);

    /** int git_config_backend_foreach_match(git_config_backend *backend, const char *regexp, git_config_foreach_cb callback, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniBackendForeachMatch)(JNIEnv *env, jclass obj, jlong backendPtr, jstring regexp, jobject callback);

    /** int git_config_lock(git_transaction **tx, git_config *cfg); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniLock)(JNIEnv *env, jclass obj, jobject tx, jlong cfgPtr);

#ifdef __cplusplus
}
#endif
#endif
