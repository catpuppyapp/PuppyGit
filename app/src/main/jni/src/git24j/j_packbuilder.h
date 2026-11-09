#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_PACKBUILDER_H__
#define __GIT24J_PACKBUILDER_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** int git_packbuilder_new(git_packbuilder **out, git_repository *repo); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniNew)(JNIEnv *env, jclass obj, jobject out, jlong repoPtr);

    /** unsigned int git_packbuilder_set_threads(git_packbuilder *pb, unsigned int n); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniSetThreads)(JNIEnv *env, jclass obj, jlong pbPtr, jint n);

    /** int git_packbuilder_insert(git_packbuilder *pb, const git_oid *id, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniInsert)(JNIEnv *env, jclass obj, jlong pbPtr, jobject id, jstring name);

    /** int git_packbuilder_insert_tree(git_packbuilder *pb, const git_oid *id); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniInsertTree)(JNIEnv *env, jclass obj, jlong pbPtr, jobject id);

    /** int git_packbuilder_insert_commit(git_packbuilder *pb, const git_oid *id); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniInsertCommit)(JNIEnv *env, jclass obj, jlong pbPtr, jobject id);

    /** int git_packbuilder_insert_walk(git_packbuilder *pb, git_revwalk *walk); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniInsertWalk)(JNIEnv *env, jclass obj, jlong pbPtr, jlong walkPtr);

    /** int git_packbuilder_insert_recur(git_packbuilder *pb, const git_oid *id, const char *name); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniInsertRecur)(JNIEnv *env, jclass obj, jlong pbPtr, jobject id, jstring name);

    /** int git_packbuilder_write_buf(git_buf *buf, git_packbuilder *pb); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniWriteBuf)(JNIEnv *env, jclass obj, jobject buf, jlong pbPtr);

    /** int git_packbuilder_write(git_packbuilder *pb, const char *path, unsigned int mode, git_indexer_progress_cb * progress_cb); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniWrite)(JNIEnv *env, jclass obj, jlong pbPtr, jstring path, jint mode, jobject progressCb);

    /** const git_oid * git_packbuilder_hash(git_packbuilder *pb); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(PackBuilder_jniHash)(JNIEnv *env, jclass obj, jlong pbPtr);

    /** int git_packbuilder_foreach(git_packbuilder *pb, git_packbuilder_foreach_cb * cb); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniForeach)(JNIEnv *env, jclass obj, jlong pbPtr, jobject foreachCb);

    /** size_t git_packbuilder_object_count(git_packbuilder *pb); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniObjectCount)(JNIEnv *env, jclass obj, jlong pbPtr);

    /** size_t git_packbuilder_written(git_packbuilder *pb); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniWritten)(JNIEnv *env, jclass obj, jlong pbPtr);

    /** int git_packbuilder_set_callbacks(git_packbuilder *pb, git_packbuilder_progress * progress_cb); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(PackBuilder_jniSetCallbacks)(JNIEnv *env, jclass obj, jlong pbPtr, jobject progressCb);

    /** void git_packbuilder_free(git_packbuilder *pb); */
    JNIEXPORT void JNICALL J_MAKE_METHOD(PackBuilder_jniFree)(JNIEnv *env, jclass obj, jlong pbPtr);

#ifdef __cplusplus
}
#endif
#endif