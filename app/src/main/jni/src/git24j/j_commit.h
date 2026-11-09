#include "j_common.h"
#include <git2.h>
#include <jni.h>
#ifndef __GIT24J_COMMIT_H__
#define __GIT24J_COMMIT_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /**int git_commit_lookup(git_commit **commit, git_repository *repo, const git_oid *id); */
    /**int git_commit_lookup_prefix(git_commit **commit, git_repository *repo, const git_oid *id, size_t len); */
    /**void git_commit_free(git_commit *commit); */
    /**const git_oid * git_commit_id(const git_commit *commit); */
    /**git_repository * git_commit_owner(const git_commit *commit); */
    /*<-- Above methods already implemented in j_gitobject.h -->*/

    /**const char * git_commit_message_encoding(const git_commit *commit); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniMessageEncoding)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**const char * git_commit_message(const git_commit *commit); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniMessage)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**const char * git_commit_message_raw(const git_commit *commit); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniMessageRaw)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**const char * git_commit_summary(git_commit *commit); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniSummary)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**const char * git_commit_body(git_commit *commit); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniBody)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**git_time_t git_commit_time(const git_commit *commit); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Commit_jniTime)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**int git_commit_time_offset(const git_commit *commit); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniTimeOffset)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**const git_signature * git_commit_committer(const git_commit *commit); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Commit_jniCommitter)(JNIEnv *env, jclass obj, jlong commitPtr);
    /**const git_signature * git_commit_author(const git_commit *commit); */
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Commit_jniAuthor)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**int git_commit_committer_with_mailmap(git_signature **out, const git_commit *commit, const git_mailmap *mailmap); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniCommitterWithMailmap)(JNIEnv *env, jclass obj, jobject out, jlong commitPtr, jlong mailmapPtr);

    /**int git_commit_author_with_mailmap(git_signature **out, const git_commit *commit, const git_mailmap *mailmap); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniAuthorWithMailmap)(JNIEnv *env, jclass obj, jobject out, jlong commitPtr, jlong mailmapPtr);

    /**const char * git_commit_raw_header(const git_commit *commit); */
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Commit_jniRawHeader)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**int git_commit_tree(git_tree **tree_out, const git_commit *commit); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniTree)(JNIEnv *env, jclass obj, jobject outTreePtr, jlong commitPtr);

    /**const git_oid * git_commit_tree_id(const git_commit *commit); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Commit_jniTreeId)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**unsigned int git_commit_parentcount(const git_commit *commit); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniParentCount)(JNIEnv *env, jclass obj, jlong commitPtr);

    /**int git_commit_parent(git_commit **out, const git_commit *commit, unsigned int n); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniParent)(JNIEnv *env, jclass obj, jobject outPtr, jlong commitPtr, jint n);

    /**const git_oid * git_commit_parent_id(const git_commit *commit, unsigned int n); */
    JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Commit_jniParentId)(JNIEnv *env, jclass obj, jlong commitPtr, jint n);

    /**int git_commit_nth_gen_ancestor(git_commit **ancestor, const git_commit *commit, unsigned int n); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniNthGenAncestor)(JNIEnv *env, jclass obj, jobject outPtr, jlong commitPtr, jint n);

    /**int git_commit_header_field(git_buf *out, const git_commit *commit, const char *field); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniHeaderField)(JNIEnv *env, jclass obj, jobject outBuf, jlong commitPtr, jstring field);

    /**int git_commit_extract_signature(git_buf *signature, git_buf *signed_data, git_repository *repo, git_oid *commit_id, const char *field); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniExtractSignature)(JNIEnv *env, jclass obj, jobject outBuf, jlong repoPtr, jobject commitId, jstring field);

    /**
     * int git_commit_create(git_oid *id,
     *                       git_repository *repo,
     *                       const char *update_ref,
     *                       const git_signature *author,
     *                       const git_signature *committer,
     *                       const char *message_encoding,
     *                       const char *message,
     *                       const git_tree *tree,
     *                       size_t parent_count,
     *                       const git_commit *[] parents);
     */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniCreate)(JNIEnv *env,
                                                           jclass obj,
                                                           jobject outOid,
                                                           jlong repoPtr,
                                                           jstring updateRef,
                                                           jlong author,
                                                           jlong committer,
                                                           jstring msgEncoding,
                                                           jstring message,
                                                           jlong treePtr,
                                                           jlongArray parents);
    /**int git_commit_create_v(git_oid *id,
     *                         git_repository *repo,
     *                         const char *update_ref,
     *                         const git_signature *author,
     *                         const git_signature *committer,
     *                         const char *message_encoding,
     *                         const char *message,
     *                         const git_tree *tree,
     *                         size_t parent_count, ...); */

    /**int git_commit_amend(git_oid *id,
     *                      const git_commit *commit_to_amend,
     *                      const char *update_ref,
     *                      const git_signature *author,
     *                      const git_signature *committer,
     *                      const char *message_encoding,
     *                      const char *message,
     *                      const git_tree *tree); */

    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniAmend)(JNIEnv *env, jclass obj,
                                                          jobject id,
                                                          jlong commitToAmendPtr,
                                                          jstring update_ref,
                                                          jlong authorPtr,
                                                          jlong committerPtr,
                                                          jstring message_encoding,
                                                          jstring message,
                                                          jlong treePtr);

    /* int git_commit_create_buffer(git_buf *out,
     *                            git_repository *repo,
     *                            const git_signature *author,
     *                            const git_signature *committer,
     *                            const char *message_encoding,
     *                            const char *message,
     *                            const git_tree *tree,
     *                            size_t parent_count,
     *                            const git_commit *[] parents); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniCreateBuffer)(JNIEnv *env, jclass obj,
                                                                 jobject out,
                                                                 jlong repoPtr,
                                                                 jlong authorPtr,
                                                                 jlong committerPtr,
                                                                 jstring message_encoding,
                                                                 jstring message,
                                                                 jlong treePtr,
                                                                 jint parentCount,
                                                                 jlongArray parents);

    /**int git_commit_create_with_signature(git_oid *out,
     *                                      git_repository *repo,
     *                                      const char *commit_content,
     *                                      const char *signature,
     *                                      const char *signature_field); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniCreateWithSignature)(JNIEnv *env, jclass obj,
                                                                        jobject outOid,
                                                                        jlong repoPtr,
                                                                        jstring commitContent,
                                                                        jstring signature,
                                                                        jstring signatureField);
    /**int git_commit_dup(git_commit **out, git_commit *source); */
    // JNIEXPORT jint JNICALL J_MAKE_METHOD(Commit_jniDup)(JNIEnv *env, jclass obj, jobject outPtr, jlong srcCommit);

    /**int git_commit_signing_cb(git_buf *signature, git_buf *signature_field, const char *commit_content, void *payload); */

#ifdef __cplusplus
}
#endif
#endif
