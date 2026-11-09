#include "j_repository.h"
#include "j_common.h"
#include "j_ensure.h"
#include "j_exception.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <jni.h>
#include <stdio.h>
#include <string.h>

extern j_constants_t *jniConstants;

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniOpen)(JNIEnv *env, jclass obj, jobject ptrReceiver, jstring path)
{
    git_repository *repo = NULL;

    char *c_path = j_copy_of_jstring(env, path, false);
    int error = git_repository_open(&repo, c_path);
    (*env)->CallVoidMethod(env, ptrReceiver, jniConstants->midAtomicLongSet, (jlong)repo);
    free(c_path);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniOpenFromWorkTree)(JNIEnv *env, jclass obj, jobject ptrReceiver, jlong wtPtr)
{
    git_repository *repo = NULL;
    git_worktree *c_wt = (git_worktree *)wtPtr;
    int error = git_repository_open_from_worktree(&repo, c_wt);
    (*env)->CallVoidMethod(env, ptrReceiver, jniConstants->midAtomicLongSet, (jlong)repo);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniWrapOdb)(JNIEnv *env, jclass obj, jobject ptrReceiver, jlong odbPtr)
{
    git_repository *repo = NULL;
    git_odb *c_odb = (git_odb *)odbPtr;
    int error = git_repository_wrap_odb(&repo, c_odb);
    (*env)->CallVoidMethod(env, ptrReceiver, jniConstants->midAtomicLongSet, (jlong)repo);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniDiscover)(JNIEnv *env, jclass obj, jobject buf, jstring startPath, jint acrossFs, jstring ceilingDirs)
{

    git_buf c_buf = {0};
    char *c_startPath = j_copy_of_jstring(env, startPath, false);
    char *c_ceilingDirs = j_copy_of_jstring(env, ceilingDirs, false);

    int error = git_repository_discover(&c_buf, c_startPath, acrossFs, c_ceilingDirs);
    if (error == 0)
    {
        j_git_buf_to_java(env, &c_buf, buf);
    }
    free(c_ceilingDirs);
    free(c_startPath);
    git_buf_dispose(&c_buf);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniOpenExt)(JNIEnv *env, jclass obj, jobject ptrReceiver, jstring path, jint flags, jstring ceilingDirs)
{
    git_repository *repo = 0;
    char *c_path = j_copy_of_jstring(env, path, false);
    char *c_ceilingDirs = j_copy_of_jstring(env, ceilingDirs, true);
    int error = git_repository_open_ext(&repo, c_path, flags, c_ceilingDirs);

    (*env)->CallVoidMethod(env, ptrReceiver, jniConstants->midAtomicLongSet, (jlong)repo);
    free(c_ceilingDirs);
    free(c_path);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniOpenBare)(JNIEnv *env, jclass obj, jobject ptrReceiver, jstring path)
{
    git_repository *repo = NULL;
    char *c_path = j_copy_of_jstring(env, path, false);

    int error = git_repository_open_bare(&repo, c_path);
    (*env)->CallVoidMethod(env, ptrReceiver, jniConstants->midAtomicLongSet, (jlong)repo);
    free(c_path);
    return error;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Repository_jniFree)(JNIEnv *env, jclass obj, jlong repo)
{
    git_repository *c_repo = (git_repository *)repo;
    git_repository_free(c_repo);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniInit)(JNIEnv *env, jclass obj, jobject outRepo, jstring path, jint isBare)
{
    git_repository *repo = NULL;
    char *c_path = j_copy_of_jstring(env, path, false);
    int error = git_repository_init(&repo, c_path, (unsigned int)isBare);
    (*env)->CallVoidMethod(env, outRepo, jniConstants->midAtomicLongSet, (jlong)repo);
    free(c_path);
    return error;
}

void init_options_copy_to_java(JNIEnv *env, git_repository_init_options *c_init_opts, jobject initOpts)
{
    assert(initOpts && "receiving object must not be null");
    jclass jclz = (*env)->GetObjectClass(env, initOpts);
    assert(jclz && "InitOptions.class not found");

    jmethodID setVersion = (*env)->GetMethodID(env, jclz, "setVersion", "(I)V");
    assert(setVersion && "setVersion (I)V is not found");

    jmethodID setFlags = (*env)->GetMethodID(env, jclz, "setFlags", "(I)V");
    assert(setFlags && "setFlags (I)V is not found");

    jmethodID setMode = (*env)->GetMethodID(env, jclz, "setMode", "(I)V");
    assert(setMode && "setMode (I)V is not found");

    jmethodID setWorkdirPath = (*env)->GetMethodID(env, jclz, "setWorkdirPath", "(Ljava/lang/String;)V");
    assert(setWorkdirPath && "setWorkdirPath (Ljava/lang/String;)V is not found");

    jmethodID setDescription = (*env)->GetMethodID(env, jclz, "setDescription", "(Ljava/lang/String;)V");
    assert(setDescription && "setDescription (Ljava/lang/String;)V is not found");

    jmethodID setTemplatePath = (*env)->GetMethodID(env, jclz, "setTemplatePath", "(Ljava/lang/String;)V");
    assert(setTemplatePath && "setTemplatePath (Ljava/lang/String;)V is not found");

    jmethodID setInitialHead = (*env)->GetMethodID(env, jclz, "setInitialHead", "(Ljava/lang/String;)V");
    assert(setInitialHead && "setInitialHead (Ljava/lang/String;)V");

    jmethodID setOriginUrl = (*env)->GetMethodID(env, jclz, "setOriginUrl", "(Ljava/lang/String;)V");
    assert(setOriginUrl && "setOriginUrl (Ljava/lang/String;)V");

    (*env)->CallVoidMethod(env, initOpts, setVersion, c_init_opts->version);
    (*env)->CallVoidMethod(env, initOpts, setFlags, c_init_opts->flags);
    (*env)->CallVoidMethod(env, initOpts, setMode, c_init_opts->mode);

    jstring workdirPath = (*env)->NewStringUTF(env, c_init_opts->workdir_path);
    (*env)->CallVoidMethod(env, initOpts, setWorkdirPath, workdirPath);
    jstring description = (*env)->NewStringUTF(env, c_init_opts->description);
    (*env)->CallVoidMethod(env, initOpts, setDescription, description);
    jstring templatePath = (*env)->NewStringUTF(env, c_init_opts->template_path);
    (*env)->CallVoidMethod(env, initOpts, setTemplatePath, templatePath);
    jstring initialHead = (*env)->NewStringUTF(env, c_init_opts->initial_head);
    (*env)->CallVoidMethod(env, initOpts, setInitialHead, initialHead);
    jstring originUrl = (*env)->NewStringUTF(env, c_init_opts->origin_url);
    (*env)->CallVoidMethod(env, initOpts, setOriginUrl, originUrl);

    (*env)->DeleteLocalRef(env, originUrl);
    (*env)->DeleteLocalRef(env, initialHead);
    (*env)->DeleteLocalRef(env, templatePath);
    (*env)->DeleteLocalRef(env, description);
    (*env)->DeleteLocalRef(env, workdirPath);
    (*env)->DeleteLocalRef(env, jclz);
}

typedef struct
{
    jstring workdirPath;
    jstring description;
    jstring templatePath;
    jstring initialHead;
    jstring originUrl;
} init_options_jobjects;

/**
 * populate git_repository_init_options with values from java.
 * NOTE: this may allocate char * data that needs to be free-ed separtely.
 */
void init_options_copy_from_java(JNIEnv *env, jobject initOpts, git_repository_init_options *c_init_opts)
{
    assert(initOpts && "receiving object must not be null");
    jclass jclz = (*env)->GetObjectClass(env, initOpts);
    assert(jclz && "InitOptions.class not found");

    jmethodID getVersion = (*env)->GetMethodID(env, jclz, "getVersion", "()I");
    assert(getVersion && "getVersion ()I is not found");

    jint version = (*env)->CallIntMethod(env, initOpts, getVersion);
    c_init_opts->version = version;

    jmethodID getFlags = (*env)->GetMethodID(env, jclz, "getFlags", "()I");
    assert(getFlags && "getFlags ()I is not found");
    jint flags = (*env)->CallIntMethod(env, initOpts, getFlags);
    c_init_opts->flags = flags;

    jmethodID getMode = (*env)->GetMethodID(env, jclz, "getMode", "()I");
    assert(getMode && "getMode ()I is not found");
    jint mode = (*env)->CallIntMethod(env, initOpts, getMode);
    c_init_opts->mode = mode;

    c_init_opts->workdir_path = j_call_getter_string(env, jclz, initOpts, "getWorkdirPath");
    c_init_opts->description = j_call_getter_string(env, jclz, initOpts, "getDescription");
    c_init_opts->template_path = j_call_getter_string(env, jclz, initOpts, "getTemplatePath");
    c_init_opts->initial_head = j_call_getter_string(env, jclz, initOpts, "getInitialHead");
    c_init_opts->origin_url = j_call_getter_string(env, jclz, initOpts, "getOriginUrl");
    (*env)->DeleteLocalRef(env, jclz);
}

/** free all fields of init_opts. Note: this does not free init_opts itself. */
void git_repository_init_options_clear(JNIEnv *env, git_repository_init_options *init_opts)
{
    free((void *)init_opts->workdir_path);
    free((void *)init_opts->description);
    free((void *)init_opts->template_path);
    free((void *)init_opts->initial_head);
    free((void *)init_opts->origin_url);
    init_opts->workdir_path = NULL;
    init_opts->description = NULL;
    init_opts->template_path = NULL;
    init_opts->initial_head = NULL;
    init_opts->origin_url = NULL;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniInitOptionsInit)(JNIEnv *env, jclass obj, jobject initOpts, jint version)
{
    git_repository_init_options init_opts;
    int error = git_repository_init_init_options(&init_opts, version);
    /* only set values when init succeeded. */
    if (error == 0)
    {
        init_options_copy_to_java(env, &init_opts, initOpts);
    }
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniInitOptionsNew)(JNIEnv *env, jclass obj, jobject outOpts, jint version)
{
    git_repository_init_options *c_opts = (git_repository_init_options *)malloc(sizeof(git_repository_init_options));
    int e = git_repository_init_init_options(c_opts, version);
    (*env)->CallVoidMethod(env, outOpts, jniConstants->midAtomicLongSet, (jlong)c_opts);
    return e;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniInitExt)(JNIEnv *env, jclass obj, jobject outRepo, jstring repoPath, jobject initOpts)
{
    git_repository *repo = 0;

    char *repo_path = j_copy_of_jstring(env, repoPath, false);

    git_repository_init_options init_opts;
    init_options_copy_from_java(env, initOpts, &init_opts);
    int error = git_repository_init_ext(&repo, repo_path, &init_opts);
    (*env)->CallVoidMethod(env, outRepo, jniConstants->midAtomicLongSet, (jlong)repo);
    git_repository_init_options_clear(env, &init_opts);
    free(repo_path);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniHead)(JNIEnv *env, jclass obj, jobject outRef, jlong repo)
{
    git_repository *c_repo = (git_repository *)repo;
    git_reference *c_ref;
    int error = git_repository_head(&c_ref, c_repo);
    (*env)->CallVoidMethod(env, outRef, jniConstants->midAtomicLongSet, c_ref);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniHeadForWorktree)(JNIEnv *env, jclass obj, jobject outRef, jlong repo, jstring name)
{
    git_repository *c_repo = (git_repository *)repo;
    char *c_name = j_copy_of_jstring(env, name, false);
    git_reference *c_ref;
    int error = git_repository_head_for_worktree(&c_ref, c_repo, c_name);
    (*env)->CallVoidMethod(env, outRef, jniConstants->midAtomicLongSet, (jlong)c_repo);
    free(c_name);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniHeadDetached)(JNIEnv *env, jclass obj, jlong repo)
{
    git_repository *c_repo = (git_repository *)repo;
    return git_repository_head_detached(c_repo);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniHeadUnborn)(JNIEnv *env, jclass obj, jlong repo)
{
    git_repository *c_repo = (git_repository *)repo;
    return git_repository_head_unborn(c_repo);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIsEmpty)(JNIEnv *env, jclass obj, jlong repo)
{
    git_repository *c_repo = (git_repository *)repo;
    return git_repository_is_empty(c_repo);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniItemPath)(JNIEnv *env, jclass obj, jobject outBuf, jlong repo, jint item)
{
    git_repository *c_repo = (git_repository *)repo;
    git_buf c_buf = {0};
    int error = git_repository_item_path(&c_buf, c_repo, item);
    j_git_buf_to_java(env, &c_buf, outBuf);
    git_buf_dispose(&c_buf);
    return error;
}

JNIEXPORT jstring JNICALL J_MAKE_METHOD(Repository_jniPath)(JNIEnv *env, jclass obj, jlong repoPtr)
{
    const char *path = git_repository_path((git_repository *)repoPtr);
    return (*env)->NewStringUTF(env, path);
}

JNIEXPORT jstring JNICALL J_MAKE_METHOD(Repository_jniWorkdir)(JNIEnv *env, jclass obj, jlong repoPtr)
{
    const char *c_workdir = git_repository_workdir((git_repository *)repoPtr);
    return (*env)->NewStringUTF(env, c_workdir);
}

JNIEXPORT jstring JNICALL J_MAKE_METHOD(Repository_jniCommondir)(JNIEnv *env, jclass obj, jlong repoPtr)
{
    const char *path = git_repository_commondir((git_repository *)repoPtr);
    return (*env)->NewStringUTF(env, path);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetWorkdir)(JNIEnv *env, jclass obj, jlong repoPtr, jstring workdir, jint updateGitlink)
{
    char *c_workdir = j_copy_of_jstring(env, workdir, false);
    int e = git_repository_set_workdir((git_repository *)repoPtr, c_workdir, updateGitlink);
    free(c_workdir);
    return e;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIsBare)(JNIEnv *env, jclass obj, jlong repoPtr)
{
    return git_repository_is_bare((git_repository *)repoPtr);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIsWorktree)(JNIEnv *env, jclass obj, jlong repoPtr)
{
    return git_repository_is_worktree((git_repository *)repoPtr);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniConfig)(JNIEnv *env, jclass obj, jobject outConfig, jlong repoPtr)
{
    git_config *c_config = 0;
    int e = git_repository_config(&c_config, (git_repository *)repoPtr);
    (*env)->CallVoidMethod(env, outConfig, jniConstants->midAtomicLongSet, (jlong)c_config);
    return e;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniConfigSnapshot)(JNIEnv *env, jclass obj, jobject outConfig, jlong repoPtr)
{
    git_config *c_config = 0;
    int e = git_repository_config_snapshot(&c_config, (git_repository *)repoPtr);
    (*env)->CallVoidMethod(env, outConfig, jniConstants->midAtomicLongSet, (jlong)c_config);
    return e;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniOdb)(JNIEnv *env, jclass obj, jobject outOdb, jlong repoPtr)
{
    git_odb *c_odb = 0;
    int e = git_repository_odb(&c_odb, (git_repository *)repoPtr);
    (*env)->CallVoidMethod(env, outOdb, jniConstants->midAtomicLongSet, (jlong)c_odb);
    return e;
}

/** int git_repository_refdb(git_refdb **out, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniRefdb)(JNIEnv *env, jclass obj, jobject outRefdb, jlong repoPtr)
{
    git_refdb *c_refdb = 0;
    int e = git_repository_refdb(&c_refdb, (git_repository *)repoPtr);
    (*env)->CallVoidMethod(env, outRefdb, jniConstants->midAtomicLongSet, (jlong)c_refdb);
    return e;
}

/** int git_repository_index(git_index **out, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIndex)(JNIEnv *env, jclass obj, jobject outIndex, jlong repoPtr)
{
    git_index *c_index = 0;
    int e = git_repository_index(&c_index, (git_repository *)repoPtr);
    (*env)->CallVoidMethod(env, outIndex, jniConstants->midAtomicLongSet, (jlong)c_index);
    return e;
}

/** int git_repository_message(git_buf *out, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniMessage)(JNIEnv *env, jclass obj, jobject outBuf, jlong repoPtr)
{
    git_buf c_buf = {0};
    int e = git_repository_message(&c_buf, (git_repository *)repoPtr);
    j_git_buf_to_java(env, &c_buf, outBuf);
    git_buf_dispose(&c_buf);
    return e;
}

/** int git_repository_message_remove(git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniMessageRemove)(JNIEnv *env, jclass obj, jlong repoPtr)
{
    return git_repository_message_remove((git_repository *)repoPtr);
}

/** int git_repository_state_cleanup(git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniStateCleanup)(JNIEnv *env, jclass obj, jlong repoPtr)
{
    return git_repository_state_cleanup((git_repository *)repoPtr);
}

/**int git_repository_fetchhead_foreach_cb(const char *ref_name, const char *remote_url, const git_oid *oid, unsigned int is_merge, void *payload);*/
int j_fetchhead_foreach_cb(const char *ref_name, const char *remote_url, const git_oid *oid, unsigned int is_merge, void *payload)
{
    assert(payload && "j_fetchhead_foreach_cb must be called with payload");
    JNIEnv *env = getEnv();
    /* assert(payload && "jni callback cannot be null"); */
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    jstring j_remoteUrl = (*env)->NewStringUTF(env, remote_url);
    jbyteArray j_oidBytes = j_byte_array_from_c(env, oid->id, GIT_OID_RAWSZ);
    int r = (*env)->CallIntMethod(env, j_payload->callback, j_payload->mid, j_remoteUrl, j_oidBytes, (jint)is_merge);
    (*env)->DeleteLocalRef(env, j_remoteUrl);
    (*env)->DeleteLocalRef(env, j_oidBytes);
    return r;
}

int j_mergehead_foreach_cb(const git_oid *oid, void *payload)
{
    assert(payload && "j_mergehead_foreach_cb must be called with paylaod");
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = getEnv();
    jbyteArray bytes = j_byte_array_from_c(env, oid->id, GIT_OID_RAWSZ);
    int r = (*env)->CallIntMethod(env, j_payload->callback, j_payload->mid, bytes);
    (*env)->DeleteLocalRef(env, bytes);
    return r;
}

/** int git_repository_fetchhead_foreach(git_repository *repo, git_repository_fetchhead_foreach_cb callback, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniFetchheadForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jobject callback)
{
    if (!callback)
    {
        return git_repository_fetchhead_foreach((git_repository *)repoPtr, NULL, NULL);
    }

    j_cb_payload payload = {0};
    j_cb_payload_init(env, &payload, callback, "(Ljava/lang/String;[BI)I");
    int r = git_repository_fetchhead_foreach((git_repository *)repoPtr, j_fetchhead_foreach_cb, &payload);
    j_cb_payload_release(env, &payload);
    return r;
}

/** int git_repository_mergehead_foreach(git_repository *repo, git_repository_mergehead_foreach_cb callback, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniMergeheadForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jobject callback)
{
    if (!callback)
    {
        return git_repository_mergehead_foreach((git_repository *)repoPtr, NULL, NULL);
    }

    j_cb_payload payload = {0};
    j_cb_payload_init(env, &payload, callback, "([B)I");
    int r = git_repository_mergehead_foreach((git_repository *)repoPtr, j_mergehead_foreach_cb, &payload);
    j_cb_payload_release(env, &payload);
    return r;
}

/** int git_repository_hashfile(git_oid *out, git_repository *repo, const char *path, git_object_t type, const char *as_path); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniHashfile)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring path, jint type, jstring asPath)
{
    char *c_path = j_copy_of_jstring(env, path, true);
    char *c_as_path = j_copy_of_jstring(env, asPath, true);
    git_oid c_oid;
    int e = git_repository_hashfile(&c_oid, (git_repository *)repoPtr, c_path, (git_object_t)type, c_as_path);
    j_git_oid_to_java(env, &c_oid, oid);
    free(c_as_path);
    free(c_path);
    return e;
}

/** int git_repository_set_head(git_repository *repo, const char *refname); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetHead)(JNIEnv *env, jclass obj, jlong repoPtr, jstring refName)
{
    char *c_refname = j_copy_of_jstring(env, refName, true);
    int e = git_repository_set_head((git_repository *)repoPtr, c_refname);
    free(c_refname);
    return e;
}

/** int git_repository_set_head_detached(git_repository *repo, const git_oid *commitish); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetHeadDetached)(JNIEnv *env, jclass obj, jlong repoPtr, jobject oid)
{
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    return git_repository_set_head_detached((git_repository *)repoPtr, &c_oid);
}

/** int git_repository_set_head_detached_from_annotated(git_repository *repo, const git_annotated_commit *commitish); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetHeadDetachedFromAnnotated)(JNIEnv *env, jclass obj, jlong repoPtr, jlong commitishPtr)
{
    return git_repository_set_head_detached_from_annotated((git_repository *)repoPtr, (const git_annotated_commit *)commitishPtr);
}

/** int git_repository_detach_head(git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniDetachHead)(JNIEnv *env, jclass obj, jlong repoPtr)
{
    return git_repository_detach_head((git_repository *)repoPtr);
}

/** int git_repository_state(git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniState)(JNIEnv *env, jclass obj, jlong repoPtr)
{
    return git_repository_state((git_repository *)repoPtr);
}

/** int git_repository_set_namespace(git_repository *repo, const char *nmspace); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetNamespace)(JNIEnv *env, jclass obj, jlong repoPtr, jstring nmspace)
{
    char *c_ns = j_copy_of_jstring(env, nmspace, true);
    int e = git_repository_set_namespace((git_repository *)repoPtr, c_ns);
    free(c_ns);
    return e;
}

/** const char * git_repository_get_namespace(git_repository *repo); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Repository_jniGetNamespace)(JNIEnv *env, jclass obj, jlong repoPtr)
{
    const char *c_ns = git_repository_get_namespace((git_repository *)repoPtr);
    return (*env)->NewStringUTF(env, c_ns);
}

/** int git_repository_is_shallow(git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIsShallow)(JNIEnv *env, jclass obj, jlong repoPtr)
{
    return git_repository_is_shallow((git_repository *)repoPtr);
}

/** int git_repository_ident(const char **name, const char **email, const git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniIdent)(JNIEnv *env, jclass obj, jobject identity, jlong repoPtr)
{
    assert(identity && "receiving object must not be null");
    jclass clz = (*env)->GetObjectClass(env, identity);
    const char *c_name;
    const char *c_email;
    int e = git_repository_ident(&c_name, &c_email, (git_repository *)repoPtr);
    j_set_string_field_c(env, clz, identity, c_name, "name");
    j_set_string_field_c(env, clz, identity, c_email, "email");
    return e;
}

/** int git_repository_set_ident(git_repository *repo, const char *name, const char *email); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Repository_jniSetIdent)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name, jstring email)
{
    char *c_name = j_copy_of_jstring(env, name, true);
    char *c_email = j_copy_of_jstring(env, email, true);
    int e = git_repository_set_ident((git_repository *)repoPtr, c_name, c_email);
    free(c_email);
    free(c_name);
    return e;
}
