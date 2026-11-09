#include <git2.h>
#include <jni.h>
#include <stdbool.h>
#ifndef __GIT24J_MAPPERS_H__
#define __GIT24J_MAPPERS_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** Save jni objects to be passed to async callbacks. */
    typedef struct
    {
        jobject callback;
        jmethodID mid;
    } j_cb_payload;

    typedef struct
    {
        jclass clzCallbacks;
        jmethodID midTransportMessage;
        jmethodID midCompletion;
        jmethodID midAcquireCred;
        jmethodID midTransportCertificateCheck;
        jmethodID midTransferProgress;
        jmethodID midUpdateTips;
        jmethodID midPackProgress;
        jmethodID midPushTransferProgress;
        jmethodID midPushUpdateReference;
        jmethodID midPushNegotiation;
        jmethodID midTransport;
        jmethodID midResolveUrl;
    } j_remote_constants;

    typedef struct
    {
        jclass clzList;
        jmethodID midGet;
        jmethodID midSize;
        jmethodID midAdd;
    } j_list;

    typedef struct
    {
        jclass clzOid;
        jmethodID midSetId;
        jmethodID midGetId;
    } j_oid_constants;

    typedef struct
    {
        jclass clzBuf;

        jmethodID emptyConstructor;

    } j_git_buf_constants_t;

    typedef struct
    {
        jclass clzGitCacheMemorySaver;

        jmethodID emptyConstructor;

        jmethodID midGetCurrentStorageValue;
        jmethodID midSetCurrentStorageValue;
        jmethodID midGetMaxStorage;
        jmethodID midSetMaxStorage;
    } j_git_cache_memory_saver_constants_t;

    typedef struct
    {
        jclass clzAtomicInt;
        jclass clzAtomicLong;
        jclass clzAtomicReference;
        jclass clzList;

        jmethodID midAtomicIntSet;
        jmethodID midAtomicLongSet;
        jmethodID midAtomicLongGet;
        jmethodID midAtomicLongInit;
        jmethodID midAtomicReferenceSet;
        jmethodID midListGetI;
        j_remote_constants remote;
        j_oid_constants oid;
        j_git_buf_constants_t buf;
        j_git_cache_memory_saver_constants_t gitCacheMemorySaver;
    } j_constants_t;

    /** commonly used constants. */
    extern j_constants_t *jniConstants;

    /** Retrieve env attached to the current thread. */
    JNIEnv *getEnv(void);

    /** initialize payload object: create global ref. */
    void j_cb_payload_init(JNIEnv *env, j_cb_payload *payload, jobject callback, const char *methodSig);
    /** release payload object: delete global ref. This does not free payload itself. */
    void j_cb_payload_release(JNIEnv *env, j_cb_payload *payload);

    /**
     * Duplicate c string, returned string needs to be free-ed separately.
     * */
    char *j_strdup(const char *src);

    /**
     * Cast jstring to const char*, throw java error if failed.
     * throw java NullPointer error if not nullable, but jstr is null.
     * returned pointer needs to be free-ed.
     */
    char *j_copy_of_jstring(JNIEnv *env, jstring jstr, bool nullable);

    /**
     * Copy content of `jstr` to `out_buf`. Result out_buf must be disposed by
     * `git_buf_dispose`.
     */
    void j_git_buf_of_jstring(JNIEnv *env, git_buf *out_buf, jstring jstr);

    /** Copy values of git_buf to java::Buf */
    void j_git_buf_to_java(JNIEnv *env, git_buf *c_buf, jobject buf);

    /* Create jstring object from git_buf. */
    jstring j_git_buf_to_jstring(JNIEnv *env, const git_buf *c_buf);

    /** copy values of java Index.Entry object to git_index_entry struct. */
    void index_entry_from_java(JNIEnv *env, git_index_entry *c_entry, jobject entry);

    /**
     * Create jni jbyteArray from c unsigned char array.
     * Returned jbyteArray should be copied and then released.
     * Don't try to retain jni objects unless you know what you are doing.
     * */
    jbyteArray j_byte_array_from_c(JNIEnv *env, const unsigned char *buf, int len);

    /** create a long[] to hold raw pointers. */
    jlongArray j_long_array_from_pointers(JNIEnv *env, const void **ptrs, size_t n);

    /** get array of pointers from long[] */
    void **j_long_array_to_pointers(JNIEnv *env, jlongArray pointers, size_t *out_len, int freeJavaArr);

    /**
     * Copy jni jbyteArray to unsigned char array.
     * @param array jni array from which content is copied.
     * @param out_len length of the output array.
     * @return pointer to the output array.
     * Returned array should be released by the caller via `ReleaseByteArrayElements`.
     * */
    unsigned char *j_unsigned_chars_from_java(JNIEnv *env, jbyteArray array, int *out_len);

    /** Copy git_oid value to java Oid vis setter. */
    void j_git_oid_to_java(JNIEnv *env, const git_oid *c_oid, jobject oid);

    /** Copy git_oid bytes (all 20 bytes) to java and set effective size*/
    void j_git_short_id_to_java(JNIEnv *env, const git_oid *c_oid, jobject oid, int effectiveSize);

    /** convert short id (java string) to git_oid and get effective oid len*/
    int j_git_short_id_from_java(JNIEnv *env, jstring oidStr, git_oid *c_oid, int *out_len);

    /** Copy value of java Oid to git_oid struct in c. */
    void j_git_oid_from_java(JNIEnv *env, jobject oid, git_oid *c_oid);

    /** copy gitarray values to java OidArray */
    void j_git_oidarray_to_java(JNIEnv *env, jobject outOidArr, const git_oidarray *c_arr);

    /** create byte[] that can be accessed directly by java. */
    jbyteArray j_git_oid_to_bytearray(JNIEnv *env, const git_oid *c_oid);

    /**
     * call `String obj.getXXX()` and get the result string.
     * Note: User must free the returned string.
     */
    char *j_call_getter_string(JNIEnv *env, jclass clz, jobject obj, const char *methodName);

    /** Call `int obj.methodName()` to get the int value. */
    int j_call_getter_int(JNIEnv *env, jclass clz, jobject obj, const char *methodName);

    /** Call `long obj.methodName()` to get the long value. */
    long j_call_getter_long(JNIEnv *env, jclass clz, jobject obj, const char *methodName);

    /** Call `obj.method(val)` to set a java object value. */
    void j_call_setter_long(JNIEnv *env, jclass clz, jobject obj, const char *method, jlong val);

    /** Call `obj.method(val)` to pass an integer to java. */
    void j_call_setter_int(JNIEnv *env, jclass clz, jobject obj, const char *method, jint val);

    /** Call receiver.method(val) to set a java object value. */
    void j_call_setter_object(JNIEnv *env, jclass clz, jobject receiver, const char *method, jobject jval);

    /** Call `obj.method(val)` to set a java object value. */
    void j_call_setter_string(JNIEnv *env, jclass clz, jobject obj, const char *method, jstring val);

    /** Call `obj.method(val)` to set a java object value. */
    void j_call_setter_string_c(JNIEnv *env, jclass clz, jobject obj, const char *method, const char *val);

    /** Call `obj.method(val)` to set byte array values to object. */
    void j_call_setter_byte_array(JNIEnv *env, jclass clz, jobject obj, const char *method, jbyteArray val);

    /**Call `obj.method()` to get */
    jbyteArray j_call_getter_byte_array(JNIEnv *env, jclass clz, jobject obj, const char *method);

    /** Call obj.field = val to set value. */
    void j_set_object_field(JNIEnv *env, jclass clz, jobject obj, jobject val, const char *field, const char *sig);

    /** Call obj.field = val to set string value. */
    void j_set_string_field_c(JNIEnv *env, jclass clz, jobject obj, const char *val, const char *field);

    /** Copy values from git_strarray to java::List<String> */
    void j_strarray_to_java_list(JNIEnv *env, git_strarray *src, jobject strList);

    /**Copy values from String[] to git_strarray*/
    void j_strarray_from_java(JNIEnv *env, git_strarray *out, jobjectArray strArr);

    /** Copy values from `git_strarray *src` to `String[] out` */
    void j_strarray_to_java_array(JNIEnv *env, jobjectArray out, git_strarray *src);

    /** Copy values from git_signature to git24j.Signature. */
    void deprecated_signature_to_java(JNIEnv *env, const git_signature *c_sig, jobject sig);

    /** Copy values from it24j.Signature to git_signature */
    int deprecated_signature_from_java(JNIEnv *env, jobject sig, git_signature **out_sig);

    /** pass a long value to java's AtomicLong */
    void j_atomic_long_set(JNIEnv *env, long val, jobject outAL);

    /** FOR DEBUG: inspect object class */
    void __debug_inspect(JNIEnv *env, jobject obj);
    void __debug_inspect2(JNIEnv *env, jobject obj, const char *message, const char *fname);

    void j_clear_git_strarray(git_strarray* sarr);

#ifdef __cplusplus
}
#endif
#endif
