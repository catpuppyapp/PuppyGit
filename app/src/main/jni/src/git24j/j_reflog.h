#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_REFLOG_H__
#define __GIT24J_REFLOG_H__
#ifdef __cplusplus
extern "C"
{
#endif
    /** -------- Signature of the header ---------- */
    /** int git_reflog_read(git_reflog **out, git_repository *repo, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniRead)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr, jstring name);

    /** int git_reflog_write(git_reflog *reflog); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniWrite)(JNIEnv *env, jclass obj, jlong reflogPtr);

    /** int git_reflog_append(git_reflog *reflog, const git_oid *id, const git_signature *committer, const char *msg); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniAppend)(JNIEnv *env, jclass obj, jlong reflogPtr, jobject id, jlong committerPtr, jstring msg);

    /** int git_reflog_rename(git_repository *repo, const char *old_name, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniRename)(JNIEnv *env, jclass obj, jlong repoPtr, jstring old_name, jstring name);

    /** int git_reflog_delete(git_repository *repo, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniDelete)(JNIEnv *env, jclass obj, jlong repoPtr, jstring name);

    /** size_t git_reflog_entrycount(git_reflog *reflog); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniEntrycount)(JNIEnv *env, jclass obj, jlong reflogPtr);

    /** const git_reflog_entry * git_reflog_entry_byindex(const git_reflog *reflog, size_t idx); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Reflog_jniEntryByindex)(JNIEnv *env, jclass obj, jlong reflogPtr, jint idx);

    /** int git_reflog_drop(git_reflog *reflog, size_t idx, int rewrite_previous_entry); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Reflog_jniDrop)(JNIEnv *env, jclass obj, jlong reflogPtr, jint idx, jint rewrite_previous_entry);

    /** const git_oid * git_reflog_entry_id_old(const git_reflog_entry *entry); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Reflog_jniEntryIdOld)(JNIEnv *env, jclass obj, jlong entryPtr);

    /** const git_oid * git_reflog_entry_id_new(const git_reflog_entry *entry); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Reflog_jniEntryIdNew)(JNIEnv *env, jclass obj, jlong entryPtr);

    /** const git_signature * git_reflog_entry_committer(const git_reflog_entry *entry); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Reflog_jniEntryCommitter)(JNIEnv *env, jclass obj, jlong entryPtr);

    /** const char * git_reflog_entry_message(const git_reflog_entry *entry); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Reflog_jniEntryMessage)(JNIEnv *env, jclass obj, jlong entryPtr);

    /** void git_reflog_free(git_reflog *reflog); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Reflog_jniFree)(JNIEnv *env, jclass obj, jlong reflogPtr);

#ifdef __cplusplus
}
#endif
#endif