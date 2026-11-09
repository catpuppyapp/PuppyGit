#include "j_note.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
extern j_constants_t *jniConstants;

// no matching type found for 'git_note_foreach_cb note_cb'
/** int git_note_foreach(git_repository *repo, const char *notes_ref, git_note_foreach_cb note_cb, void *payload); */
int j_git_note_foreach_cb(const git_oid *blob_id, const git_oid *annotated_object_id, void *payload)
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
    jbyteArray blobId = j_git_oid_to_bytearray(env, blob_id);
    jbyteArray annoObjId = j_git_oid_to_bytearray(env, annotated_object_id);
    int r = (*env)->CallIntMethod(env, callback, mid, blobId, annoObjId);
    (*env)->DeleteLocalRef(env, annoObjId);
    (*env)->DeleteLocalRef(env, blobId);
    return r;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jstring notesRef, jobject foreachCb)
{
    assert(foreachCb && "foreach with null callback does not make any sense");

    char *notes_ref = j_copy_of_jstring(env, notesRef, true);
    j_cb_payload payload = {0};
    j_cb_payload_init(env, &payload, foreachCb, "([B[B)I");
    int r = git_note_foreach((git_repository *)repoPtr, notes_ref, j_git_note_foreach_cb, &payload);
    j_cb_payload_release(env, &payload);
    free(notes_ref);
    return r;
}
/** -------- Wrapper Body ---------- */
/** int git_note_iterator_new(git_note_iterator **out, git_repository *repo, const char *notes_ref); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniIteratorNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring notes_ref)
{
    git_note_iterator *c_out = 0;
    char *c_notes_ref = j_copy_of_jstring(env, notes_ref, true);
    int r = git_note_iterator_new(&c_out, (git_repository *)repoPtr, c_notes_ref);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_note_iterator_free(c_out); */
    free(c_notes_ref);
    return r;
}

/** int git_note_commit_iterator_new(git_note_iterator **out, git_commit *notes_commit); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniCommitIteratorNew)(JNIEnv *env, jclass obj, jobject out, jlong notesCommitPtr)
{
    git_note_iterator *c_out = 0;
    int r = git_note_commit_iterator_new(&c_out, (git_commit *)notesCommitPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_note_iterator_free(c_out); */
    return r;
}

/** void git_note_iterator_free(git_note_iterator *it); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Note_jniIteratorFree)(JNIEnv *env, jclass obj, jlong itPtr)
{
    git_note_iterator_free((git_note_iterator *)itPtr);
}

/** int git_note_next(git_oid *note_id, git_oid *annotated_id, git_note_iterator *it); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniNext)(JNIEnv *env, jclass obj, jobject note_id, jobject annotated_id, jlong itPtr)
{
    git_oid c_note_id;
    git_oid c_annotated_id;
    int r = git_note_next(&c_note_id, &c_annotated_id, (git_note_iterator *)itPtr);
    j_git_oid_to_java(env, &c_note_id, note_id);
    j_git_oid_to_java(env, &c_annotated_id, annotated_id);
    return r;
}

/** int git_note_read(git_note **out, git_repository *repo, const char *notes_ref, const git_oid *oid); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniRead)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring notes_ref, jobject oid)
{
    git_note *c_out = 0;
    char *c_notes_ref = j_copy_of_jstring(env, notes_ref, true);
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    int r = git_note_read(&c_out, (git_repository *)repoPtr, c_notes_ref, &c_oid);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_note_free(c_out); */
    free(c_notes_ref);
    return r;
}

/** int git_note_commit_read(git_note **out, git_repository *repo, git_commit *notes_commit, const git_oid *oid); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniCommitRead)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong notesCommitPtr, jobject oid)
{
    git_note *c_out = 0;
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    int r = git_note_commit_read(&c_out, (git_repository *)repoPtr, (git_commit *)notesCommitPtr, &c_oid);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (jlong)c_out);
    /* git_note_free(c_out); */
    return r;
}

/** const git_signature * git_note_author(const git_note *note); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Note_jniAuthor)(JNIEnv *env, jclass obj, jlong notePtr)
{
    const git_signature *r = git_note_author((git_note *)notePtr);
    return (jlong)r;
}

/** const git_signature * git_note_committer(const git_note *note); */
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Note_jniCommitter)(JNIEnv *env, jclass obj, jlong notePtr)
{
    const git_signature *r = git_note_committer((git_note *)notePtr);
    return (jlong)r;
}

/** const char * git_note_message(const git_note *note); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Note_jniMessage)(JNIEnv *env, jclass obj, jlong notePtr)
{
    const char *r = git_note_message((git_note *)notePtr);
    return (*env)->NewStringUTF(env, r);
}

/** const git_oid * git_note_id(const git_note *note); */
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Note_jniId)(JNIEnv *env, jclass obj, jlong notePtr)
{
    const git_oid *c_oid = git_note_id((git_note *)notePtr);
    return j_git_oid_to_bytearray(env, c_oid);
}

/** int git_note_create(git_oid *out, git_repository *repo, const char *notes_ref, const git_signature *author, const git_signature *committer, const git_oid *oid, const char *note, int force); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniCreate)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring notes_ref, jlong authorPtr, jlong committerPtr, jobject oid, jstring note, jint force)
{
    git_oid c_out;
    char *c_notes_ref = j_copy_of_jstring(env, notes_ref, true);
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    char *c_note = j_copy_of_jstring(env, note, true);
    int r = git_note_create(&c_out, (git_repository *)repoPtr, c_notes_ref, (git_signature *)authorPtr, (git_signature *)committerPtr, &c_oid, c_note, force);
    j_git_oid_to_java(env, &c_out, out);
    free(c_notes_ref);
    free(c_note);
    return r;
}

/** int git_note_commit_create(git_oid *notes_commit_out, git_oid *notes_blob_out, git_repository *repo, git_commit *parent, const git_signature *author, const git_signature *committer, const git_oid *oid, const char *note, int allow_note_overwrite); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniCommitCreate)(JNIEnv *env, jclass obj, jobject notes_commit_out, jobject notes_blob_out, jlong repoPtr, jlong parentPtr, jlong authorPtr, jlong committerPtr, jobject oid, jstring note, jint allow_note_overwrite)
{
    git_oid c_notes_commit_out;
    git_oid c_notes_blob_out;
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    char *c_note = j_copy_of_jstring(env, note, true);
    int r = git_note_commit_create(&c_notes_commit_out, &c_notes_blob_out, (git_repository *)repoPtr, (git_commit *)parentPtr, (git_signature *)authorPtr, (git_signature *)committerPtr, &c_oid, c_note, allow_note_overwrite);
    j_git_oid_to_java(env, &c_notes_commit_out, notes_commit_out);
    j_git_oid_to_java(env, &c_notes_blob_out, notes_blob_out);
    free(c_note);
    return r;
}

/** int git_note_remove(git_repository *repo, const char *notes_ref, const git_signature *author, const git_signature *committer, const git_oid *oid); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniRemove)(JNIEnv *env, jclass obj, jlong repoPtr, jstring notes_ref, jlong authorPtr, jlong committerPtr, jobject oid)
{
    char *c_notes_ref = j_copy_of_jstring(env, notes_ref, true);
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    int r = git_note_remove((git_repository *)repoPtr, c_notes_ref, (git_signature *)authorPtr, (git_signature *)committerPtr, &c_oid);
    free(c_notes_ref);
    return r;
}

/** int git_note_commit_remove(git_oid *notes_commit_out, git_repository *repo, git_commit *notes_commit, const git_signature *author, const git_signature *committer, const git_oid *oid); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniCommitRemove)(JNIEnv *env, jclass obj, jobject notes_commit_out, jlong repoPtr, jlong notesCommitPtr, jlong authorPtr, jlong committerPtr, jobject oid)
{
    git_oid c_notes_commit_out;
    git_oid c_oid;
    j_git_oid_from_java(env, oid, &c_oid);
    int r = git_note_commit_remove(&c_notes_commit_out, (git_repository *)repoPtr, (git_commit *)notesCommitPtr, (git_signature *)authorPtr, (git_signature *)committerPtr, &c_oid);
    j_git_oid_to_java(env, &c_notes_commit_out, notes_commit_out);
    return r;
}

/** void git_note_free(git_note *note); */
JNIEXPORT void JNICALL J_MAKE_METHOD(Note_jniFree)(JNIEnv *env, jclass obj, jlong notePtr)
{
    git_note_free((git_note *)notePtr);
}

/** int git_note_default_ref(git_buf *out, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniDefaultRef)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr)
{
    git_buf c_out = {0};
    int r = git_note_default_ref(&c_out, (git_repository *)repoPtr);
    j_git_buf_to_java(env, &c_out, out);
    git_buf_dispose(&c_out);
    return r;
}
