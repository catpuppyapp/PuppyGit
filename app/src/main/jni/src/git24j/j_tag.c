#include "j_tag.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/**
 * Callback used to iterate over tag names
 *
 * @see git_tag_foreach
 *
 * @param name The tag name
 * @param oid The tag's OID
 * @param payload {@link j_cb_payload}
 * @return non-zero to terminate the iteration
 */

int j_git_tag_foreach_cb(const char *name, git_oid *oid, void *payload)
{
    assert(payload && "j_git_tag_foreach_cb must be called with payload");
    j_cb_payload *j_payload = (j_cb_payload *)payload;
    JNIEnv *env = getEnv();
    jstring jniName = (*env)->NewStringUTF(env, name);
    char *oid_s = git_oid_tostr_s(oid);
    jstring jniOidStr = (*env)->NewStringUTF(env, oid_s);
    int r = (*env)->CallIntMethod(env, j_payload->callback, j_payload->mid, jniName, jniOidStr);
    (*env)->DeleteLocalRef(env, jniName);
    (*env)->DeleteLocalRef(env, jniOidStr);
    return r;
}

/** int git_tag_lookup_prefix(git_tag **out, git_repository *repo, const git_oid *id, size_t len); */
/** void git_tag_free(git_tag *tag); */
/** const git_oid * git_tag_id(const git_tag *tag); */
/** git_repository * git_tag_owner(const git_tag *tag); */

/** int git_tag_target(git_object **target_out, const git_tag *tag); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniTarget)(JNIEnv *env, jclass obj, jobject outTargetPtr, jlong tagPtr)
{
    git_object *target_out = 0;
    int e = git_tag_target(&target_out, (git_tag *)tagPtr);
    (*env)->CallVoidMethod(env, outTargetPtr, jniConstants->midAtomicLongSet, (jlong)target_out);
    return e;
}
/** const git_oid * git_tag_target_id(const git_tag *tag); */
JNIEXPORT jbyteArray JNICALL J_MAKE_METHOD(Tag_jniTargetId)(JNIEnv *env, jclass obj, jlong tagPtr)
{
    const git_oid *c_oid = git_tag_target_id((const git_tag *)tagPtr);
    return j_git_oid_to_bytearray(env, c_oid);
}

/** git_object_t git_tag_target_type(const git_tag *tag); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniTargetType)(JNIEnv *env, jclass obj, jlong tagPtr)
{
    return (jint)git_tag_target_type((const git_tag *)tagPtr);
}
/** const char * git_tag_name(const git_tag *tag); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Tag_jniName)(JNIEnv *env, jclass obj, jlong tagPtr)
{
    const char *c_name = git_tag_name((const git_tag *)tagPtr);
    return (*env)->NewStringUTF(env, c_name);
}

/** const git_signature * git_tag_tagger(const git_tag *tag); */
// JNIEXPORT void JNICALL J_MAKE_METHOD(Tag_jniTagger)(JNIEnv *env, jclass obj, jobject outSig, jlong tagPtr)
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Tag_jniTagger)(JNIEnv *env, jclass obj, jlong tagPtr)
{
    const git_signature *tagger = git_tag_tagger((const git_tag *)tagPtr);
    return (jlong)tagger;
}

/** const char * git_tag_message(const git_tag *tag); */
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Tag_jniMessage)(JNIEnv *env, jclass obj, jlong tagPtr)
{
    const char *message = git_tag_message((const git_tag *)tagPtr);
    return (*env)->NewStringUTF(env, message);
}

/** int git_tag_create(git_oid *oid, git_repository *repo, const char *tag_name, const git_object *target, const git_signature *tagger, const char *message, int force); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniCreate)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring tag_name, jlong targetPtr, jlong taggerPtr, jstring message, jint force)
{
    git_oid c_oid;
    char *c_tag_name = j_copy_of_jstring(env, tag_name, true);
    char *c_message = j_copy_of_jstring(env, message, true);
    int r = git_tag_create(&c_oid, (git_repository *)repoPtr, c_tag_name, (git_object *)targetPtr, (git_signature *)taggerPtr, c_message, force);
    j_git_oid_to_java(env, &c_oid, oid);
    free(c_tag_name);
    free(c_message);
    return r;
}

/** int git_tag_annotation_create(git_oid *oid, git_repository *repo, const char *tag_name, const git_object *target, const git_signature *tagger, const char *message); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniAnnotationCreate)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring tag_name, jlong targetPtr, jlong taggerPtr, jstring message)
{
    git_oid c_oid;
    char *c_tag_name = j_copy_of_jstring(env, tag_name, true);
    char *c_message = j_copy_of_jstring(env, message, true);
    int r = git_tag_annotation_create(&c_oid, (git_repository *)repoPtr, c_tag_name, (git_object *)targetPtr, (git_signature *)taggerPtr, c_message);
    j_git_oid_to_java(env, &c_oid, oid);
    free(c_tag_name);
    free(c_message);
    return r;
}

/** int git_tag_create_from_buffer(git_oid *oid, git_repository *repo, const char *buffer, int force); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniCreateFromBuffer)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring buffer, jint force)
{
    git_oid c_oid;
    char *c_buffer = j_copy_of_jstring(env, buffer, true);
    int e = git_tag_create_frombuffer(&c_oid, (git_repository *)repoPtr, c_buffer, force);
    j_git_oid_to_java(env, &c_oid, oid);
    free(c_buffer);
    return e;
}

/** int git_tag_create_lightweight(git_oid *oid, git_repository *repo, const char *tag_name, const git_object *target, int force); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniCreateLightWeight)(JNIEnv *env, jclass obj, jobject oid, jlong repoPtr, jstring tagName, jlong targetPtr, jint force)
{
    git_oid c_oid;
    char *tag_name = j_copy_of_jstring(env, tagName, true);
    int e = git_tag_create_lightweight(&c_oid, (git_repository *)repoPtr, tag_name, (const git_object *)targetPtr, force);
    j_git_oid_to_java(env, &c_oid, oid);
    free(tag_name);
    return e;
}

/** int git_tag_delete(git_repository *repo, const char *tag_name); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniDelete)(JNIEnv *env, jclass obj, jlong repoPtr, jstring tagName)
{
    char *tag_name = j_copy_of_jstring(env, tagName, true);
    int e = git_tag_delete((git_repository *)repoPtr, tag_name);

    free(tag_name);
    return e;
}

/** int git_tag_list(git_strarray *tag_names, git_repository *repo); */
/** int git_tag_list_match(git_strarray *tag_names, const char *pattern, git_repository *repo); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniList)(JNIEnv *env, jclass obj, jobject tagNames, jlong repoPtr)
{
    git_strarray *c_tag_names = (git_strarray *)malloc(sizeof(git_strarray));
    int r = git_tag_list(c_tag_names, (git_repository *)repoPtr);
    j_strarray_to_java_list(env, c_tag_names, tagNames);
    git_strarray_free(c_tag_names);
    free(c_tag_names);
    return r;
}
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniListMatch)(JNIEnv *env, jclass obj, jobject tagNames, jstring pattern, jlong repoPtr)
{
    git_strarray *c_tag_names = (git_strarray *)malloc(sizeof(git_strarray));
    char *c_pattern = j_copy_of_jstring(env, pattern, true);
    int r = git_tag_list_match(c_tag_names, c_pattern, (git_repository *)repoPtr);
    j_strarray_to_java_list(env, c_tag_names, tagNames);
    git_strarray_free(c_tag_names);
    free(c_tag_names);
    free(c_pattern);
    return r;
}

/** int git_tag_foreach(git_repository *repo, git_tag_foreach_cb callback, void *payload); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniForeach)(JNIEnv *env, jclass obj, jlong repoPtr, jobject foreachCb)
{
    j_cb_payload payload = {0};
    j_cb_payload_init(env, &payload, foreachCb, "(Ljava/lang/String;Ljava/lang/String;)I");
    int r = git_tag_foreach((git_repository *)repoPtr, j_git_tag_foreach_cb, &payload);
    j_cb_payload_release(env, &payload);
    return r;
}

/** int git_tag_peel(git_object **tag_target_out, const git_tag *tag); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniPeel)(JNIEnv *env, jclass obj, jobject outTarget, jlong tagPtr)
{
    git_object *tag_target_out = 0;
    int e = git_tag_peel(&tag_target_out, (const git_tag *)tagPtr);
    (*env)->CallVoidMethod(env, outTarget, jniConstants->midAtomicLongSet, (jlong)tag_target_out);
    return e;
}

/** int git_tag_dup(git_tag **out, git_tag *source); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Tag_jniDup)(JNIEnv *env, jclass obj, jobject outTag, jlong sourcePtr)
{
    git_tag *c_out = 0;
    int e = git_tag_dup(&c_out, (git_tag *)sourcePtr);
    (*env)->CallVoidMethod(env, outTag, jniConstants->midAtomicLongSet, (jlong)c_out);
    return e;
}
