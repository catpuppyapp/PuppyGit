#include "j_config.h"
#include "j_common.h"
#include "j_mappers.h"
#include <assert.h>

extern j_constants_t *jniConstants;

/**
 * A config enumeration callback
 *
 * @param entry the entry currently being enumerated
 * @param payload a user-specified pointer
 */
int j_git_config_foreach_cb(const git_config_entry *entry, void *payload)
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
    return (*env)->CallIntMethod(env, callback, mid, (long)entry);
}

/** const char *name*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Config_jniEntryGetName)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return (*env)->NewStringUTF(env, ((git_config_entry *)entryPtr)->name);
}

/** const char *value*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Config_jniEntryGetValue)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return (*env)->NewStringUTF(env, ((git_config_entry *)entryPtr)->value);
}

/** const char *backend_type */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Config_jniEntryGetBackendType)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    const char* c_char = ((git_config_entry *)entryPtr)->backend_type;
    return (*env)->NewStringUTF(env, c_char?c_char:"");
}
/** const char *origin_path */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Config_jniEntryGetOriginPath)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    const char* c_char = ((git_config_entry *)entryPtr)->origin_path;
    return (*env)->NewStringUTF(env, c_char?c_char:"");
}

/** unsigned int include_depth*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniEntryGetIncludeDepth)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return ((git_config_entry *)entryPtr)->include_depth;
}

/** git_config_level_t level*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniEntryGetLevel)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    return ((git_config_entry *)entryPtr)->level;
}

/** void git_config_entry_free(git_config_entry *entry); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Config_jniEntryFree)(JNIEnv *env, jclass obj, jlong entryPtr)
{
    git_config_entry_free((git_config_entry *)entryPtr);
}

/** int git_config_find_global(git_buf *out); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniFindGlobal)(JNIEnv *env, jclass obj, jobject out)
{
    git_buf c_out = {0};
    int r = git_config_find_global(&c_out);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    return r;
}

/** int git_config_find_xdg(git_buf *out); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniFindXdg)(JNIEnv *env, jclass obj, jobject out)
{
    git_buf c_out = {0};
    int r = git_config_find_xdg(&c_out);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    return r;
}

/** int git_config_find_system(git_buf *out); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniFindSystem)(JNIEnv *env, jclass obj, jobject out)
{
    git_buf c_out = {0};
    int r = git_config_find_system(&c_out);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    return r;
}

/** int git_config_find_programdata(git_buf *out); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniFindProgramdata)(JNIEnv *env, jclass obj, jobject out)
{
    git_buf c_out = {0};
    int r = git_config_find_programdata(&c_out);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    return r;
}

/** int git_config_open_default(git_config **out); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniOpenDefault)(JNIEnv *env, jclass obj, jobject out)
{
    git_config *c_out = 0;
    int r = git_config_open_default(&c_out);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** int git_config_new(git_config **out); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniNew)(JNIEnv *env, jclass obj, jobject out)
{
    git_config *c_out = 0;
    int r = git_config_new(&c_out);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** int git_config_add_file_ondisk(git_config *cfg, const char *path, git_config_level_t level, const git_repository *repo, int force); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniAddFileOndisk)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring path, jint level, jlong repoPtr, jint force)
{
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_config_add_file_ondisk((git_config *)cfgPtr, c_path, (git_config_level_t)level, (git_repository *)repoPtr, force);
    free(c_path);
    return r;
}

/** int git_config_open_ondisk(git_config **out, const char *path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniOpenOndisk)(JNIEnv *env, jclass obj, jobject out, jstring path)
{
    git_config *c_out = 0;
    char *c_path = j_copy_of_jstring(env, path, true);
    int r = git_config_open_ondisk(&c_out, c_path);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_path);
    return r;
}

/** int git_config_open_level(git_config **out, const git_config *parent, git_config_level_t level); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniOpenLevel)(JNIEnv *env, jclass obj, jobject out, jlong parentPtr, jint level)
{
    git_config *c_out = 0;
    int r = git_config_open_level(&c_out, (git_config *)parentPtr, (git_config_level_t)level);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** int git_config_open_global(git_config **out, git_config *config); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniOpenGlobal)(JNIEnv *env, jclass obj, jobject out, jlong configPtr)
{
    git_config *c_out = 0;
    int r = git_config_open_global(&c_out, (git_config *)configPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** int git_config_snapshot(git_config **out, git_config *config); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSnapshot)(JNIEnv *env, jclass obj, jobject out, jlong configPtr)
{
    git_config *c_out = 0;
    int r = git_config_snapshot(&c_out, (git_config *)configPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** void git_config_free(git_config *cfg); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Config_jniFree)(JNIEnv *env, jclass obj, jlong cfgPtr)
{
    git_config_free((git_config *)cfgPtr);
}

/** int git_config_get_entry(git_config_entry **out, const git_config *cfg, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetEntry)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name)
{
    git_config_entry *c_out = 0;
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_config_get_entry(&c_out, (git_config *)cfgPtr, c_name);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_name);
    return r;
}

/** int git_config_get_int32(int32_t *out, const git_config *cfg, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetInt32)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name)
{
    int32_t c_out = 0;
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_config_get_int32(&c_out, (git_config *)cfgPtr, c_name);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicIntSet, c_out);
    free(c_name);
    return r;
}

/** int git_config_get_int64(int64_t *out, const git_config *cfg, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetInt64)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name)
{
    int64_t c_out = 0;
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_config_get_int64(&c_out, (git_config *)cfgPtr, c_name);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, c_out);
    free(c_name);
    return r;
}

/** int git_config_get_bool(int *out, const git_config *cfg, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetBool)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name)
{
    int c_out = 0;
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_config_get_bool(&c_out, (git_config *)cfgPtr, c_name);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicIntSet, c_out);
    free(c_name);
    return r;
}

/** int git_config_get_path(git_buf *out, const git_config *cfg, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetPath)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name)
{
    git_buf c_out = {0};
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_config_get_path(&c_out, (git_config *)cfgPtr, c_name);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    free(c_name);
    return r;
}

/** int git_config_get_string(const char **out, const git_config *cfg, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetString)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name)
{
    const char *c_out = 0;
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_config_get_string(&c_out, (git_config *)cfgPtr, c_name);
    jstring j_out = (*env)->NewStringUTF(env, c_out);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicReferenceSet, j_out);
    (*env)->DeleteLocalRef(env, j_out);
    free(c_name);
    return r;
}

/** int git_config_get_string_buf(git_buf *out, const git_config *cfg, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetStringBuf)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name)
{
    git_buf c_out = {0};
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_config_get_string_buf(&c_out, (git_config *)cfgPtr, c_name);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    free(c_name);
    return r;
}

/** int git_config_get_multivar_foreach(const git_config *cfg, const char *name, const char *regexp, git_config_foreach_cb callback, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniGetMultivarForeach)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jstring regexp, jobject callback)
{
    assert(callback && "multivar_foreach with null callback does not make any sense");
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_regexp = j_copy_of_jstring(env, regexp, true);
    j_cb_payload payload = {0};
    j_cb_payload_init(env, &payload, callback, "(J)I");
    int r = git_config_get_multivar_foreach((git_config *)cfgPtr, c_name, c_regexp, j_git_config_foreach_cb, &payload);
    j_cb_payload_release(env, &payload);
    free(c_name);
    free(c_regexp);
    return r;
}

/** int git_config_multivar_iterator_new(git_config_iterator **out, const git_config *cfg, const char *name, const char *regexp); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniMultivarIteratorNew)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring name, jstring regexp)
{
    git_config_iterator *c_out = 0;
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_regexp = j_copy_of_jstring(env, regexp, true);
    int r = git_config_multivar_iterator_new(&c_out, (git_config *)cfgPtr, c_name, c_regexp);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_name);
    free(c_regexp);
    return r;
}

/** int git_config_next(git_config_entry **entry, git_config_iterator *iter); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniNext)(JNIEnv *env, jclass obj, jobject entry, jlong iterPtr)
{
    git_config_entry *c_entry = 0;
    int r = git_config_next(&c_entry, (git_config_iterator *)iterPtr);
    (*env)->CallVoidMethod(env, entry, jniConstants->midAtomicLongSet, (jlong)c_entry);
    return r;
}

/** void git_config_iterator_free(git_config_iterator *iter); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Config_jniIteratorFree)(JNIEnv *env, jclass obj, jlong iterPtr)
{
    git_config_iterator_free((git_config_iterator *)iterPtr);
}

/** int git_config_set_int32(git_config *cfg, const char *name, int32_t value); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSetInt32)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jint value)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_config_set_int32((git_config *)cfgPtr, c_name, value);
    free(c_name);
    return r;
}

/** int git_config_set_int64(git_config *cfg, const char *name, int64_t value); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSetInt64)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jlong value)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_config_set_int64((git_config *)cfgPtr, c_name, value);
    free(c_name);
    return r;
}

/** int git_config_set_bool(git_config *cfg, const char *name, int value); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSetBool)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jint value)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_config_set_bool((git_config *)cfgPtr, c_name, value);
    free(c_name);
    return r;
}

/** int git_config_set_string(git_config *cfg, const char *name, const char *value); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSetString)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jstring value)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_value = j_copy_of_jstring(env, value, true);
    int r = git_config_set_string((git_config *)cfgPtr, c_name, c_value);
    free(c_name);
    free(c_value);
    return r;
}

/** int git_config_set_multivar(git_config *cfg, const char *name, const char *regexp, const char *value); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniSetMultivar)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jstring regexp, jstring value)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_regexp = j_copy_of_jstring(env, regexp, true);
    char *c_value = j_copy_of_jstring(env, value, true);
    int r = git_config_set_multivar((git_config *)cfgPtr, c_name, c_regexp, c_value);
    free(c_name);
    free(c_regexp);
    free(c_value);
    return r;
}

/** int git_config_delete_entry(git_config *cfg, const char *name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniDeleteEntry)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    int r = git_config_delete_entry((git_config *)cfgPtr, c_name);
    free(c_name);
    return r;
}

/** int git_config_delete_multivar(git_config *cfg, const char *name, const char *regexp); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniDeleteMultivar)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring name, jstring regexp)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_regexp = j_copy_of_jstring(env, regexp, true);
    int r = git_config_delete_multivar((git_config *)cfgPtr, c_name, c_regexp);
    free(c_name);
    free(c_regexp);
    return r;
}

/** int git_config_foreach(const git_config *cfg, git_config_foreach_cb callback, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniForeach)(JNIEnv *env, jclass obj, jlong cfgPtr, jobject callback)
{
    assert(callback && "foreach callback must not be NULL");
    j_cb_payload payload = {0};
    j_cb_payload_init(env, &payload, callback, "(J)I");
    int r = git_config_foreach((git_config *)cfgPtr, j_git_config_foreach_cb, &payload);
    j_cb_payload_release(env, &payload);
    return r;
}

/** int git_config_iterator_new(git_config_iterator **out, const git_config *cfg); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniIteratorNew)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr)
{
    git_config_iterator *c_out = 0;
    int r = git_config_iterator_new(&c_out, (git_config *)cfgPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** int git_config_iterator_glob_new(git_config_iterator **out, const git_config *cfg, const char *regexp); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniIteratorGlobNew)(JNIEnv *env, jclass obj, jobject out, jlong cfgPtr, jstring regexp)
{
    git_config_iterator *c_out = 0;
    char *c_regexp = j_copy_of_jstring(env, regexp, true);
    int r = git_config_iterator_glob_new(&c_out, (git_config *)cfgPtr, c_regexp);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    free(c_regexp);
    return r;
}

/** int git_config_foreach_match(const git_config *cfg, const char *regexp, git_config_foreach_cb callback, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniForeachMatch)(JNIEnv *env, jclass obj, jlong cfgPtr, jstring regexp, jobject callback)
{
    assert(callback && "foreach match callback must not be NULL");
    char *c_regexp = j_copy_of_jstring(env, regexp, true);
    j_cb_payload payload = {0};
    j_cb_payload_init(env, &payload, callback, "(J)I");
    int r = git_config_foreach_match((git_config *)cfgPtr, c_regexp, j_git_config_foreach_cb, &payload);
    j_cb_payload_release(env, &payload);
    free(c_regexp);
    return r;
}

/** int git_config_parse_bool(int *out, const char *value); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniParseBool)(JNIEnv *env, jclass obj, jobject out, jstring value)
{
    int c_out;
    char *c_value = j_copy_of_jstring(env, value, true);
    int r = git_config_parse_bool(&c_out, c_value);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicIntSet, c_out);
    free(c_value);
    return r;
}

/** int git_config_parse_int32(int32_t *out, const char *value); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniParseInt32)(JNIEnv *env, jclass obj, jobject out, jstring value)
{
    int32_t c_out;
    char *c_value = j_copy_of_jstring(env, value, true);
    int r = git_config_parse_int32(&c_out, c_value);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicIntSet, c_out);
    free(c_value);
    return r;
}

/** int git_config_parse_int64(int64_t *out, const char *value); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniParseInt64)(JNIEnv *env, jclass obj, jobject out, jstring value)
{
    int64_t c_out;
    char *c_value = j_copy_of_jstring(env, value, true);
    int r = git_config_parse_int64(&c_out, c_value);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, c_out);
    free(c_value);
    return r;
}

/** int git_config_parse_path(git_buf *out, const char *value); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniParsePath)(JNIEnv *env, jclass obj, jobject out, jstring value)
{
    git_buf c_out = {0};
    char *c_value = j_copy_of_jstring(env, value, true);
    int r = git_config_parse_path(&c_out, c_value);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    free(c_value);
    return r;
}

/** int git_config_backend_foreach_match(git_config_backend *backend, const char *regexp, git_config_foreach_cb callback, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniBackendForeachMatch)(JNIEnv *env, jclass obj, jlong backendPtr, jstring regexp, jobject callback)
{
    assert(callback && "foreach match backend callback must not be NULL");
    char *c_regexp = j_copy_of_jstring(env, regexp, true);
    j_cb_payload payload = {0};
    j_cb_payload_init(env, &payload, callback, "(J)I");
    int r = git_config_backend_foreach_match((git_config_backend *)backendPtr, c_regexp, j_git_config_foreach_cb, &payload);
    j_cb_payload_release(env, &payload);
    free(c_regexp);
    return r;
}

/** int git_config_lock(git_transaction **tx, git_config *cfg); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Config_jniLock)(JNIEnv *env, jclass obj, jobject tx, jlong cfgPtr)
{
    git_transaction *c_tx = 0;
    int r = git_config_lock(&c_tx, (git_config *)cfgPtr);
    (*env)->CallVoidMethod(env, tx, jniConstants->midAtomicLongSet, (jlong)c_tx);
    return r;
}
