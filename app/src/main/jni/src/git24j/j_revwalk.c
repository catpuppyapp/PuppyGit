#include "j_revwalk.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>

int j_git_revwalk_hide_cb(const git_oid *commit_id, void *payload)
{
    assert(payload && "j_git_revwalk_hide_cb must be called with payload");
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = getEnv();
    jbyteArray commitId = j_git_oid_to_bytearray(env, commit_id);
    int r = (*env)->CallIntMethod(env, j_payload->callback, j_payload->mid, commitId);
    (*env)->DeleteLocalRef(env, commitId);
    return r;
}

/** Release payload */
JNIEXPORT void JNICALL J_MAKE_METHOD(Revwalk_jniFreeHideCb)(JNIEnv *env, jclass obj, jlong payloadPtr)
{
    j_cb_payload *payload = (j_cb_payload *)payloadPtr;
    if (!payload)
    {
        JNIEnv *env = getEnv();
        j_cb_payload_release(env, payload);
        free(payload);
    }
}

/** int git_revwalk_add_hide_cb(git_revwalk *walk, git_revwalk_hide_cb hide_cb, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniAddHideCb)(JNIEnv *env, jclass obj, jlong walkPtr, jobject hideCb, jobject outPayload)
{

    /*  int e = _hide_cb_payload_free(env, (git_revwalk *)walkPtr); */
    git_revwalk *walk = (git_revwalk *)walkPtr;
    if (hideCb == NULL)
    {
        return git_revwalk_add_hide_cb(walk, NULL, NULL);
    }
    j_cb_payload *payload = (j_cb_payload *)malloc(sizeof(j_cb_payload));
    j_cb_payload_init(env, payload, hideCb, "([B)I");
    int e = git_revwalk_add_hide_cb((git_revwalk *)walkPtr, j_git_revwalk_hide_cb, payload);
    (*env)->CallVoidMethod(env, outPayload, jniConstants->midAtomicLongSet, (jlong)payload);
    return e;
}

// no matching type found for 'git_revwalk_hide_cb hide_cb'
/** int git_revwalk_add_hide_cb(git_revwalk *walk, git_revwalk_hide_cb hide_cb, void *payload); */
/** -------- Wrapper Body ---------- */
/** void git_revwalk_free(git_revwalk *walk); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Revwalk_jniFree)(JNIEnv *env, jclass obj, jlong walkPtr)
{
    git_revwalk *walk = (struct git_revwalk *)walkPtr;
    git_revwalk_free(walk);
}

/** int git_revwalk_hide(git_revwalk *walk, const git_oid *commit_id); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniHide)(JNIEnv *env, jclass obj, jlong walkPtr, jobject commitId)
{
    git_oid c_commit_id;
    j_git_oid_from_java(env, commitId, &c_commit_id);
    int r = git_revwalk_hide((git_revwalk *)walkPtr, &c_commit_id);
    return r;
}

/** int git_revwalk_hide_glob(git_revwalk *walk, const char *glob); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniHideGlob)(JNIEnv *env, jclass obj, jlong walkPtr, jstring glob)
{
    char *c_glob = j_copy_of_jstring(env, glob, true);
    int r = git_revwalk_hide_glob((git_revwalk *)walkPtr, c_glob);
    free(c_glob);
    return r;
}

/** int git_revwalk_hide_head(git_revwalk *walk); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniHideHead)(JNIEnv *env, jclass obj, jlong walkPtr)
{
    int r = git_revwalk_hide_head((git_revwalk *)walkPtr);
    return r;
}

/** int git_revwalk_hide_ref(git_revwalk *walk, const char *refname); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniHideRef)(JNIEnv *env, jclass obj, jlong walkPtr, jstring refname)
{
    char *c_refname = j_copy_of_jstring(env, refname, true);
    int r = git_revwalk_hide_ref((git_revwalk *)walkPtr, c_refname);
    free(c_refname);
    return r;
}

/** int git_revwalk_new(git_revwalk **out, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr)
{
    git_revwalk *c_out = 0;
    int r = git_revwalk_new(&c_out, (git_repository *)repoPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    return r;
}

/** int git_revwalk_next(git_oid *out, git_revwalk *walk); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniNext)(JNIEnv *env, jclass obj, jobject out, jlong walkPtr)
{
    git_oid c_out;
    int r = git_revwalk_next(&c_out, (git_revwalk *)walkPtr);
    j_git_oid_to_java(env, &c_out, out);
    return r;
}

/** int git_revwalk_push(git_revwalk *walk, const git_oid *id); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniPush)(JNIEnv *env, jclass obj, jlong walkPtr, jobject id)
{
    git_oid c_id;
    j_git_oid_from_java(env, id, &c_id);
    int r = git_revwalk_push((git_revwalk *)walkPtr, &c_id);
    return r;
}

/** int git_revwalk_push_glob(git_revwalk *walk, const char *glob); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniPushGlob)(JNIEnv *env, jclass obj, jlong walkPtr, jstring glob)
{
    char *c_glob = j_copy_of_jstring(env, glob, true);
    int r = git_revwalk_push_glob((git_revwalk *)walkPtr, c_glob);
    free(c_glob);
    return r;
}

/** int git_revwalk_push_head(git_revwalk *walk); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniPushHead)(JNIEnv *env, jclass obj, jlong walkPtr)
{
    int r = git_revwalk_push_head((git_revwalk *)walkPtr);
    return r;
}

/** int git_revwalk_push_range(git_revwalk *walk, const char *range); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniPushRange)(JNIEnv *env, jclass obj, jlong walkPtr, jstring range)
{
    char *c_range = j_copy_of_jstring(env, range, true);
    int r = git_revwalk_push_range((git_revwalk *)walkPtr, c_range);
    free(c_range);
    return r;
}

/** int git_revwalk_push_ref(git_revwalk *walk, const char *refname); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Revwalk_jniPushRef)(JNIEnv *env, jclass obj, jlong walkPtr, jstring refname)
{
    char *c_refname = j_copy_of_jstring(env, refname, true);
    int r = git_revwalk_push_ref((git_revwalk *)walkPtr, c_refname);
    free(c_refname);
    return r;
}

/** git_repository * git_revwalk_repository(git_revwalk *walk); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Revwalk_jniRepository)(JNIEnv *env, jclass obj, jlong walkPtr)
{
    git_repository *r = git_revwalk_repository((git_revwalk *)walkPtr);
    return (jlong)r;
}

/** void git_revwalk_reset(git_revwalk *walker); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Revwalk_jniReset)(JNIEnv *env, jclass obj, jlong walkerPtr)
{
    git_revwalk_reset((git_revwalk *)walkerPtr);
}

/** void git_revwalk_simplify_first_parent(git_revwalk *walk); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Revwalk_jniSimplifyFirstParent)(JNIEnv *env, jclass obj, jlong walkPtr)
{
    git_revwalk_simplify_first_parent((git_revwalk *)walkPtr);
}

/** void git_revwalk_sorting(git_revwalk *walk, unsigned int sort_mode); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Revwalk_jniSorting)(JNIEnv *env, jclass obj, jlong walkPtr, jint sortMode)
{
    git_revwalk_sorting((git_revwalk *)walkPtr, sortMode);
}
