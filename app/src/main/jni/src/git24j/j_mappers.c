#include "j_mappers.h"
#include "j_common.h"
#include "j_exception.h"
#include <assert.h>
#include <stdbool.h>
#include <stdio.h>
#include <string.h>

extern JavaVM *globalJvm;

j_constants_t *jniConstants;

/** Retrieve env attached to the current thread. */
JNIEnv *getEnv(void)
{
    JNIEnv *env;
    int st = (*globalJvm)->GetEnv(globalJvm, (void **)&env, JNI_VERSION_1_6);
    if (st == JNI_EDETACHED)
    {
        /* retry after attaching */
        (*globalJvm)->AttachCurrentThread(globalJvm, (void **)&env, NULL);
    }
    assert(env && "Could not obtain attached jni env");
    return env;
}

/** initialize payload object: create global ref. */
void j_cb_payload_init(JNIEnv *env, j_cb_payload *payload, jobject callback, const char *methodSig)
{
    assert(payload && callback && "cannot initialize empty payload or with empty callback");
    jclass clz = (*env)->GetObjectClass(env, callback);
    assert(clz && "failed to set callback: calss not found");
    jmethodID mid = (*env)->GetMethodID(env, clz, "accept", methodSig);
    assert(mid && "failed to set callback: method 'accept' not found");
    payload->callback = (*env)->NewGlobalRef(env, callback);
    payload->mid = mid;
    (*env)->DeleteLocalRef(env, clz);
}

/** release payload object: delete global ref. This does not free payload itself. */
void j_cb_payload_release(JNIEnv *env, j_cb_payload *payload)
{
    if (payload && payload->callback)
    {
        (*env)->DeleteGlobalRef(env, payload->callback);
        payload->callback = NULL;
    }
}

char *j_strdup(const char *src)
{
    char *copy = NULL;
    size_t len = strlen(src) + 1;
    if (src)
    {
        copy = malloc(len);
        if (copy)
        {
            strncpy(copy, src, len);
        }
    }
    return copy;
}

void j_git_buf_to_java(JNIEnv *env, git_buf *c_buf, jobject buf)
{
    assert(buf && "receiving object must not be null");
    jclass jclz = (*env)->GetObjectClass(env, buf);
    assert(jclz && "Could not find Buf class from given buf object");
//    j_call_setter_string_c(env, jclz, buf, "setPtr", c_buf->ptr);
    j_call_setter_byte_array(env, jclz, buf, "setPtr", j_byte_array_from_c(env, c_buf->ptr, c_buf->size));
    j_call_setter_int(env, jclz, buf, "setSize", c_buf->size);
    j_call_setter_int(env, jclz, buf, "setReserved", c_buf->reserved);
    (*env)->DeleteLocalRef(env, jclz);
}

jstring j_git_buf_to_jstring(JNIEnv *env, const git_buf *c_buf)
{
    if (c_buf == NULL)
    {
        return NULL;
    }
    size_t n = c_buf->size;
    char *copy = (char *)malloc(sizeof(char) * (n + 1));
    strncpy(copy, c_buf->ptr, n);
    copy[n] = '\0';
    jstring res = (*env)->NewStringUTF(env, copy);
    free(copy);
    return res;
}

char *j_call_getter_string(JNIEnv *env, jclass clz, jobject obj, const char *methodName)
{
    jmethodID method = (*env)->GetMethodID(env, clz, methodName, "()Ljava/lang/String;");
    if (method == NULL)
    {
        return NULL;
    }
    jstring jstr = (*env)->CallObjectMethod(env, obj, method);
    return j_copy_of_jstring(env, jstr, true);
}

int j_call_getter_int(JNIEnv *env, jclass clz, jobject obj, const char *methodName)
{
    jmethodID method = (*env)->GetMethodID(env, clz, methodName, "()I");
    assert(method && "j_call_getter_int getter method not found");
    return (*env)->CallIntMethod(env, obj, method);
}

long j_call_getter_long(JNIEnv *env, jclass clz, jobject obj, const char *methodName)
{
    jmethodID method = (*env)->GetMethodID(env, clz, methodName, "()J");
    assert(method && "j_call_getter_long getter method not found");
    return (*env)->CallLongMethod(env, obj, method);
}

char *j_copy_of_jstring(JNIEnv *env, jstring jstr, bool nullable)
{
    if (!nullable)
    {
        assert(jstr && "cannot cast null to c string");
    }
    if (!jstr)
    {
        return NULL;
    }

    const char *c_str = (*env)->GetStringUTFChars(env, jstr, NULL);
    char *copy = j_strdup(c_str);
    (*env)->ReleaseStringUTFChars(env, jstr, c_str);
    return copy;
}

void j_git_buf_of_jstring(JNIEnv *env, git_buf *out_buf, jstring jstr)
{
    if (jstr == NULL)
    {
        return;
    }
    assert(out_buf && "out_buf must not be null");
    git_buf_dispose(out_buf);
//    int size = (*env)->GetStringLength(env, jstr); // this size is java char length, not byte length
    const char *c_str = (*env)->GetStringUTFChars(env, jstr, NULL);

    // maybe null when no more memory
    if(c_str != NULL) {
        int size = strlen(c_str);  // bytes length of string
        strncpy(out_buf->ptr, c_str, size);
        out_buf->reserved = size + 1;
        out_buf->size = size;
    }
}

int j_get_int_field(JNIEnv *env, jclass jclz, jobject obj, const char *fieldName)
{
    jfieldID fid = (*env)->GetFieldID(env, jclz, fieldName, "I");
    assert(fid && "getter I() not found");
    jint value = (*env)->GetIntField(env, obj, fid);
    return value;
}

/* NOTE: Returned string needs to be free-ed. */
char *j_get_string_field(JNIEnv *env, jclass jclz, jobject obj, const char *fieldName)
{
    jfieldID fid = (*env)->GetFieldID(env, jclz, fieldName, "Ljava/lang/String;");
    assert(fid && "getter Ljava/lang/String() not found");
    jstring value = (*env)->GetObjectField(env, obj, fid);
    char *cstr = j_copy_of_jstring(env, value, true);
    (*env)->DeleteLocalRef(env, value);
    return cstr;
}

/** copy values of java Index.Entry object to git_index_entry struct. */
void index_entry_from_java(JNIEnv *env, git_index_entry *c_entry, jobject entry)
{
    if (entry == NULL)
    {
        return;
    }

    assert(c_entry);
    jclass jclz = (*env)->GetObjectClass(env, entry);
    assert(jclz && "Could not find Entry class from given entry object");
    c_entry->ctime.seconds = j_get_int_field(env, jclz, entry, "ctimeSec");
    c_entry->ctime.nanoseconds = j_get_int_field(env, jclz, entry, "ctimeNanoSec");
    c_entry->mtime.seconds = j_get_int_field(env, jclz, entry, "mtimeSec");
    c_entry->mtime.nanoseconds = j_get_int_field(env, jclz, entry, "mtimeNanoSec");
    c_entry->dev = j_get_int_field(env, jclz, entry, "dev");
    c_entry->mode = j_get_int_field(env, jclz, entry, "mode");
    c_entry->uid = j_get_int_field(env, jclz, entry, "uid");
    c_entry->gid = j_get_int_field(env, jclz, entry, "gid");
    char *oid = j_get_string_field(env, jclz, entry, "oid");
    git_oid_fromstr(&(c_entry->id), oid);
    free(oid);
    c_entry->file_size = j_get_int_field(env, jclz, entry, "fileSize");
    c_entry->flags = j_get_int_field(env, jclz, entry, "flags");
    c_entry->flags_extended = j_get_int_field(env, jclz, entry, "flagsExtended");
    free((void *)c_entry->path);
    c_entry->path = j_get_string_field(env, jclz, entry, "path");
    (*env)->DeleteLocalRef(env, jclz);
}

/** create jni jbyteArray from c unsigned char array. */
jbyteArray j_byte_array_from_c(JNIEnv *env, const unsigned char *buf, int len)
{
    if (buf == NULL || len < 1)
    {
        return NULL;
    }
    jbyteArray array = (*env)->NewByteArray(env, len);
    (*env)->SetByteArrayRegion(env, array, 0, len, (jbyte *)buf);
    return array;
}

/** create a long[] to hold raw pointers. */
jlongArray j_long_array_from_pointers(JNIEnv *env, const void **ptrs, size_t n)
{
    if (ptrs == NULL || n == 0)
    {
        return NULL;
    }
    jlongArray array = (*env)->NewLongArray(env, n);
    (*env)->SetLongArrayRegion(env, array, 0, n, (const jlong *)ptrs);
    return array;
}

/** get array of pointers from long[] */
void **j_long_array_to_pointers(JNIEnv *env, jlongArray pointers, size_t *out_len, int freeJavaArr)
{
    jsize np = (*env)->GetArrayLength(env, pointers);
    void **c_pointer = (void **)malloc(sizeof(void *) * np);

    jlong *elements = (*env)->GetLongArrayElements(env, pointers, 0);
    for (jsize i = 0; i < np; i++)
    {
        c_pointer[i] = (void *)elements[i];
    }
    *out_len = np;

    if(freeJavaArr != 0) {
       (*env)->ReleaseLongArrayElements(env, pointers, (jlong*)elements, 0);
    }

    return c_pointer;
}

/** create c unsigned char array from jni jbyteArray. Caller must FREE the return pointer. */
unsigned char *j_unsigned_chars_from_java(JNIEnv *env, jbyteArray array, int *out_len)
{
    if (array == NULL)
    {
        *out_len = 0;
        return NULL;
    }

    int len = (*env)->GetArrayLength(env, array);
    unsigned char *buf = (unsigned char *)malloc(sizeof(unsigned char) * len);
    (*env)->GetByteArrayRegion(env, array, 0, len, (jbyte *)buf);
    *out_len = len;
    return buf;
}

/** Copy git_oid value to java Oid via setter */
void j_git_oid_to_java(JNIEnv *env, const git_oid *c_oid, jobject oid)
{
    assert(oid && "receiving object must not be null");
    jbyteArray raw = j_byte_array_from_c(env, c_oid->id, GIT_OID_RAWSZ);
    (*env)->CallVoidMethod(env, oid, jniConstants->oid.midSetId, raw);
    (*env)->DeleteLocalRef(env, raw);
}

/** Copy git_oid bytes (all 20 bytes) to java and set effective size*/
void j_git_short_id_to_java(JNIEnv *env, const git_oid *c_oid, jobject oid, int effectiveSize)
{
    assert(oid && "receiving object must not be null");
    jbyteArray raw = j_byte_array_from_c(env, c_oid->id, GIT_OID_RAWSZ);
    (*env)->CallVoidMethod(env, oid, jniConstants->oid.midSetId, raw);
    (*env)->DeleteLocalRef(env, raw);
}

/** convert short id (java string) to git_oid and get effective oid len*/
int j_git_short_id_from_java(JNIEnv *env, jstring oidStr, git_oid *c_oid, int *out_len)
{
    assert(c_oid && "receiving oid must not be null");
    assert(out_len && "receiving oid length must not be null");
    if (oidStr == NULL)
    {
        *out_len = 0;
        return 0;
    }
    jsize oidLen = (*env)->GetStringLength(env, oidStr);
    const char *oid_str = (*env)->GetStringUTFChars(env, oidStr, 0);
    int e = git_oid_fromstrn(c_oid, oid_str, (size_t)oidLen);
    *out_len = oidLen;
    return e;
}

/** create byte[] that can be accessed directly by java. */
jbyteArray j_git_oid_to_bytearray(JNIEnv *env, const git_oid *c_oid)
{
    if (c_oid == NULL)
    {
        return NULL;
    }

    return j_byte_array_from_c(env, c_oid->id, GIT_OID_RAWSZ);
}

/** Copy value of java Oid to git_oid struct in c. */
void j_git_oid_from_java(JNIEnv *env, jobject oid, git_oid *c_oid)
{
    if (oid == NULL)
    {
        git_oid_fromstr(c_oid, "");
        return;
    }
    assert(c_oid && "receiving oid must not be null");
    jobjectArray jBytes = (*env)->CallObjectMethod(env, oid, jniConstants->oid.midGetId);
    int out_len;
    unsigned char *c_bytes = j_unsigned_chars_from_java(env, jBytes, &out_len);
    git_oid_fromraw(c_oid, c_bytes);
    free(c_bytes);
    (*env)->DeleteLocalRef(env, jBytes);
}

void j_git_oidarray_to_java(JNIEnv *env, jobject outOidArr, const git_oidarray *c_arr)
{
    assert(c_arr && outOidArr && "null input or receiver is not allowed in j_git_oidarray_to_java ");
    jclass clz = (*env)->GetObjectClass(env, outOidArr);
    assert(clz && "OidArray class not found");
    jmethodID setter = (*env)->GetMethodID(env, clz, "add", "([B)V");
    for (size_t i = 0; i < c_arr->count; i++)
    {
        jbyteArray raw = j_byte_array_from_c(env, c_arr->ids[i].id, GIT_OID_RAWSZ);
        (*env)->CallVoidMethod(env, outOidArr, setter, raw);
        (*env)->DeleteLocalRef(env, raw);
    }
    (*env)->DeleteLocalRef(env, clz);
}

/** Call `obj.method(val)` to set a java object value. */
void j_call_setter_long(JNIEnv *env, jclass clz, jobject obj, const char *method, jlong val)
{
    jmethodID setter = (*env)->GetMethodID(env, clz, method, "(J)V");
    (*env)->CallVoidMethod(env, obj, setter, val);
}

/** Call `obj.method(val)` to set a java object value. */
void j_call_setter_int(JNIEnv *env, jclass clz, jobject obj, const char *method, jint val)
{
    if (!obj)
    {
        return;
    }

    jmethodID setter = (*env)->GetMethodID(env, clz, method, "(I)V");
    assert(setter && "setter not found");
    (*env)->CallVoidMethod(env, obj, setter, val);
}

/** Call receiver.method(val) to set a java object value. */
void j_call_setter_object(JNIEnv *env, jclass clz, jobject receiver, const char *method, jobject jval)
{
    jmethodID setter = (*env)->GetMethodID(env, clz, method, "(Ljava/lang/Object;)V");
    assert(setter && "could not find setter method.");
    (*env)->CallVoidMethod(env, receiver, setter, jval);
}

void j_call_setter_string(JNIEnv *env, jclass clz, jobject obj, const char *method, jstring val)
{
    jmethodID setter = (*env)->GetMethodID(env, clz, method, "(Ljava/lang/String;)V");
    assert(setter && "could not find setter method.");
    (*env)->CallVoidMethod(env, obj, setter, val);
}

// the param `val` should end with '\0'
void j_call_setter_string_c(JNIEnv *env, jclass clz, jobject obj, const char *method, const char *val)
{
    //NewStringUTF get length of c chars by '\0'
    jstring jVal = (*env)->NewStringUTF(env, val);
    j_call_setter_string(env, clz, obj, method, jVal);
    (*env)->DeleteLocalRef(env, jVal);
}

void j_call_setter_byte_array(JNIEnv *env, jclass clz, jobject obj, const char *method, jbyteArray val)
{
    jmethodID setter = (*env)->GetMethodID(env, clz, method, "([B)V");
    (*env)->CallVoidMethod(env, obj, setter, val);
}

jbyteArray j_call_getter_byte_array(JNIEnv *env, jclass clz, jobject obj, const char *method)
{
    jmethodID getter = (*env)->GetMethodID(env, clz, method, "()[B");
    if (getter == NULL)
    {
        return NULL;
    }
    return (*env)->CallObjectMethod(env, obj, getter);
}

void j_set_object_field(JNIEnv *env, jclass clz, jobject obj, jobject val, const char *field, const char *sig)
{
    jfieldID fid = (*env)->GetFieldID(env, clz, field, sig);
    (*env)->SetObjectField(env, obj, fid, val);
}

void j_set_string_field_c(JNIEnv *env, jclass clz, jobject obj, const char *val, const char *field)
{
    jstring jVal = (*env)->NewStringUTF(env, val);
    j_set_object_field(env, clz, obj, jVal, field, "Ljava/lang/String;");
    (*env)->DeleteLocalRef(env, jVal);
}

/** Copy values from git_strarray to java::List<String> */
void j_strarray_to_java_list(JNIEnv *env, git_strarray *src, jobject strList)
{
    assert(strList && "receiving object must not be null");
    jclass clz = (*env)->GetObjectClass(env, strList);
    assert(clz && "Could not find the class of the accepting object");
    jmethodID midAdd = (*env)->GetMethodID(env, clz, "add", "(Ljava/lang/Object;)Z");
    assert(midAdd && "Could not get List.add method");
    for (size_t i = 0; i < src->count; i++)
    {
        jstring jVal = (*env)->NewStringUTF(env, src->strings[i]);
        (*env)->CallBooleanMethod(env, strList, midAdd, jVal);
        (*env)->DeleteLocalRef(env, jVal);
    }
    (*env)->DeleteLocalRef(env, clz);
}

/**Copy values from `String[] strArr` to `git_strarray *out`*/
void j_strarray_from_java(JNIEnv *env, git_strarray *out, jobjectArray strArr)
{
    if (strArr == NULL || out == NULL)
    {
        return;
    }

//    assert(out && "receiving git_strarray must not be null");
    jsize len = (*env)->GetArrayLength(env, strArr);

    // free `out` memory and set fields value to NULL and 0
    //btw: `git_strarray_free(out)` free more than 1time may cause program crash, so better don't use it
    j_clear_git_strarray(out);

    // now `out->strings` and `out->count` value is NULL and 0

    //len<1 , src is an empty array, and before already make `out` array to empty, so needn't do further copy
    if(len < 1) {
        return;
    }

    //do copy
    out->strings = (char **)malloc(sizeof(char *) * len);
    for (jsize i = 0; i < len; i++)
    {
        jobject si = (*env)->GetObjectArrayElement(env, strArr, i);
        out->strings[i] = j_copy_of_jstring(env, (jstring)si, true);
        (*env)->DeleteLocalRef(env, si);
    }
    out->count = len;
}

/** Copy values from `git_strarray *src` to `String[] out` */
void j_strarray_to_java_array(JNIEnv *env, jobjectArray out, git_strarray *src)
{
    if (out == NULL || src==NULL)
    {
        return;
    }

    size_t len = src->count;

    for (size_t i = 0; i < len; i++)
    {
        jstring jVal = (*env)->NewStringUTF(env, src->strings[i]);
        (*env)->SetObjectArrayElement(env, out, i, jVal);
        // fix issue #146: https://github.com/catpuppyapp/PuppyGit/issues/146
        (*env)->DeleteLocalRef(env, jVal);
    }
}

/** Copy values from git_signature to git24j.Signature. */
void deprecated_signature_to_java(JNIEnv *env, const git_signature *c_sig, jobject sig)
{
    assert(sig && "receiving object must not be null");
    jclass clz = (*env)->GetObjectClass(env, sig);
    assert(clz && "Signature class not found");
    j_call_setter_string_c(env, clz, sig, "setName", c_sig->name);
    j_call_setter_string_c(env, clz, sig, "setEmail", c_sig->email);
    jmethodID midSetWhen = (*env)->GetMethodID(env, clz, "setWhen", "(JIC)V");
    assert(midSetWhen && "Signature::setWhen method not found");
    (*env)->CallVoidMethod(env, sig, midSetWhen, c_sig->when.time, c_sig->when.offset, c_sig->when.sign);
}

/** Copy values from it24j.Signature to git_signature, `out_sig` should be free-ed later by `git_signature_free` */
int deprecated_signature_from_java(JNIEnv *env, jobject sig, git_signature **out_sig)
{
    if (sig == NULL)
    {
        return 0;
    }

    jclass clz = (*env)->GetObjectClass(env, sig);
    assert(clz && "Signature class not found");
    char *name = j_call_getter_string(env, clz, sig, "getName");
    char *email = j_call_getter_string(env, clz, sig, "getEmail");
    git_time_t gt = j_call_getter_long(env, clz, sig, "getWhenEpocSecond");
    int offset = j_call_getter_int(env, clz, sig, "getWhenOffsetMinutes");
    int e = git_signature_new(out_sig, name, email, gt, offset);
    free(name);
    free(email);
    (*env)->DeleteLocalRef(env, clz);
    return e;
}

void j_atomic_long_set(JNIEnv *env, long val, jobject outAL)
{
    (*env)->CallVoidMethod(env, outAL, jniConstants->midAtomicLongSet, val);
}

/** FOR DEBUG: inspect object class */
void __debug_inspect(JNIEnv *env, jobject obj)
{
    __debug_inspect2(env, obj, "obj", NULL);
}

void __debug_inspect2(JNIEnv *env, jobject obj, const char *message, const char *fname)
{
    FILE *fout = NULL;
    if (fname != NULL)
    {
        fout = fopen(fname, "a");
    }
    if (fout == NULL)
    {
        fout = stderr;
    }

    fprintf(fout, "------------------ INSPECT %s(%p) ----------------- \n", message, obj);
    jclass clz = (*env)->GetObjectClass(env, obj);
    // First get the class object
    jmethodID midGetClass = (*env)->GetMethodID(env, clz, "getClass", "()Ljava/lang/Class;");
    jobject clsObj = (*env)->CallObjectMethod(env, obj, midGetClass);

    // Now get the class object's class descriptor
    jclass clzClz = (*env)->GetObjectClass(env, clsObj);
    // Find the getName() method on the class object
    jmethodID midGetName = (*env)->GetMethodID(env, clzClz, "getName", "()Ljava/lang/String;");

    // Call the getName() to get a jstring object back
    jstring objClassName = (jstring)(*env)->CallObjectMethod(env, clsObj, midGetName);
    fprintf(fout, "qqqqq class of the object[%p]: %s \n", obj, j_copy_of_jstring(env, objClassName, true));
    (*env)->CallObjectMethod(env, clz, midGetName);
    fprintf(fout, "------------------ INSPECTION (%s) end ----------------- \n", message);
    (*env)->DeleteLocalRef(env, objClassName);
    (*env)->DeleteLocalRef(env, clzClz);
    (*env)->DeleteLocalRef(env, clsObj);
    (*env)->DeleteLocalRef(env, clz);
}

void j_clear_git_strarray(git_strarray* sarr){
    int cnt = sarr->count;
    if(cnt > 0) {
        char ** s = sarr->strings;
        for(size_t i=0; i<cnt; i++){
            free(s[i]);
        }
        free(s);

        sarr->strings=NULL;
        sarr->count=0;
    }
}
