#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_NOTE_H__
#define __GIT24J_NOTE_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_note_foreach_cb(const git_oid *blob_id, const git_oid *annotated_object_id, void *payload); */
    // no matching type found for 'git_note_foreach_cb note_cb'
    /** int git_note_foreach(git_repository *repo, const char *notes_ref, git_note_foreach_cb note_cb, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jstring notesRef, jobject foreachCb);
    /** -------- Signature of the header ---------- */
    /** int git_note_iterator_new(git_note_iterator **out, git_repository *repo, const char *notes_ref); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniIteratorNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring notes_ref);

    /** int git_note_commit_iterator_new(git_note_iterator **out, git_commit *notes_commit); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniCommitIteratorNew)(JNIEnv *env, jclass obj, jobject out, jlong notesCommitPtr);

    /** void git_note_iterator_free(git_note_iterator *it); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Note_jniIteratorFree)(JNIEnv *env, jclass obj, jlong itPtr);

    /** int git_note_next(git_oid *note_id, git_oid *annotated_id, git_note_iterator *it); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniNext)(JNIEnv *env, jclass obj, jobject note_id, jobject annotated_id, jlong itPtr);

    /** int git_note_read(git_note **out, git_repository *repo, const char *notes_ref, const git_oid *oid); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniRead)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring notes_ref, jobject oid);

    /** int git_note_commit_read(git_note **out, git_repository *repo, git_commit *notes_commit, const git_oid *oid); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniCommitRead)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jlong notesCommitPtr, jobject oid);

    /** const git_signature * git_note_author(const git_note *note); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Note_jniAuthor)(JNIEnv *env, jclass obj, jlong notePtr);

    /** const git_signature * git_note_committer(const git_note *note); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Note_jniCommitter)(JNIEnv *env, jclass obj, jlong notePtr);

    /** const char * git_note_message(const git_note *note); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Note_jniMessage)(JNIEnv *env, jclass obj, jlong notePtr);

    /** const git_oid * git_note_id(const git_note *note); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Note_jniId)(JNIEnv *env, jclass obj, jlong notePtr);

    /** int git_note_create(git_oid *out, git_repository *repo, const char *notes_ref, const git_signature *author, const git_signature *committer, const git_oid *oid, const char *note, int force); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniCreate)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring notes_ref, jlong authorPtr, jlong committerPtr, jobject oid, jstring note, jint force);

    /** int git_note_commit_create(git_oid *notes_commit_out, git_oid *notes_blob_out, git_repository *repo, git_commit *parent, const git_signature *author, const git_signature *committer, const git_oid *oid, const char *note, int allow_note_overwrite); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniCommitCreate)(JNIEnv *env, jclass obj, jobject notes_commit_out, jobject notes_blob_out, jlong repoPtr, jlong parentPtr, jlong authorPtr, jlong committerPtr, jobject oid, jstring note, jint allow_note_overwrite);

    /** int git_note_remove(git_repository *repo, const char *notes_ref, const git_signature *author, const git_signature *committer, const git_oid *oid); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniRemove)(JNIEnv *env, jclass obj, jlong repoPtr, jstring notes_ref, jlong authorPtr, jlong committerPtr, jobject oid);

    /** int git_note_commit_remove(git_oid *notes_commit_out, git_repository *repo, git_commit *notes_commit, const git_signature *author, const git_signature *committer, const git_oid *oid); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniCommitRemove)(JNIEnv *env, jclass obj, jobject notes_commit_out, jlong repoPtr, jlong notesCommitPtr, jlong authorPtr, jlong committerPtr, jobject oid);

    /** void git_note_free(git_note *note); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Note_jniFree)(JNIEnv *env, jclass obj, jlong notePtr);

    /** int git_note_default_ref(git_buf *out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniDefaultRef)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);

    /** int git_note_foreach_cb(const git_oid *blob_id, const git_oid *annotated_object_id, void *payload); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Note_jniForeachCb)(JNIEnv *env, jclass obj, jobject blobId, jobject annotatedObjectId);

#ifdef __cplusplus
}
#endif
#endif