
#include "j_libgit2.h"
#include <assert.h>
#include <git2.h>
#include <jni.h>
#include <stdio.h>
#include "j_exception.h"

extern j_constants_t *jniConstants;

JavaVM *globalJvm;

jclass j_find_and_hold_clz(JNIEnv *env, const char *descriptor)
{
    jclass clz = (*env)->FindClass(env, descriptor);
    assert(clz && "class not found");
    jclass gClz = (jclass)(*env)->NewGlobalRef(env, clz);
    (*env)->DeleteLocalRef(env, clz);
    return gClz;
}

jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
    globalJvm = vm;
    return JNI_VERSION_1_6;
}

void git24j_init(JNIEnv *env)
{
    assert(env && "cannot initiate git24j without jvm");
    jniConstants = (j_constants_t *)malloc(sizeof(j_constants_t));
    jniConstants->clzAtomicInt = j_find_and_hold_clz(env, "java/util/concurrent/atomic/AtomicInteger");
    jniConstants->clzAtomicLong = j_find_and_hold_clz(env, "java/util/concurrent/atomic/AtomicLong");
    jniConstants->clzAtomicReference = j_find_and_hold_clz(env, "java/util/concurrent/atomic/AtomicReference");
    jniConstants->clzList = j_find_and_hold_clz(env, "java/util/List");
    assert(jniConstants->clzAtomicInt && "AtomicInteger class not found");
    assert(jniConstants->clzAtomicLong && "AtomicLong class not found");
    assert(jniConstants->clzAtomicReference && "AtomicReference class not found");
    assert(jniConstants->clzList && "List class not found");
    jniConstants->midAtomicIntSet = (*env)->GetMethodID(env, jniConstants->clzAtomicInt, "set", "(I)V");
    jniConstants->midAtomicLongSet = (*env)->GetMethodID(env, jniConstants->clzAtomicLong, "set", "(J)V");
    jniConstants->midAtomicLongGet = (*env)->GetMethodID(env, jniConstants->clzAtomicLong, "get", "()J");
    jniConstants->midAtomicLongInit = (*env)->GetMethodID(env, jniConstants->clzAtomicLong, "<init>", "()V");
    jniConstants->midAtomicReferenceSet = (*env)->GetMethodID(env, jniConstants->clzAtomicReference, "set", "(Ljava/lang/Object;)V");
    jniConstants->midListGetI = (*env)->GetMethodID(env, jniConstants->clzList, "get", "(I)Ljava/lang/Object;");
    /* Remote constants */
    jclass clz = j_find_and_hold_clz(env, J_CLZ_PREFIX "Remote$Callbacks");
    assert(clz && "Remote.Callbacks class not found");
    jniConstants->remote.clzCallbacks = clz;
    jniConstants->remote.midAcquireCred = (*env)->GetMethodID(env, clz, "acquireCred", "(Ljava/lang/String;Ljava/lang/String;I)J");
    jniConstants->remote.midCompletion = (*env)->GetMethodID(env, clz, "complete", "(I)I");
    jniConstants->remote.midTransportMessage = (*env)->GetMethodID(env, clz, "transportMessage", "(Ljava/lang/String;)I");
    jniConstants->remote.midTransportCertificateCheck = (*env)->GetMethodID(env, clz, "transportMessageCheck", "(JILjava/lang/String;)I");
    jniConstants->remote.midTransferProgress = (*env)->GetMethodID(env, clz, "transferProgress", "(J)I");
    jniConstants->remote.midUpdateTips = (*env)->GetMethodID(env, clz, "updateTips", "(Ljava/lang/String;[B[B)I");
    jniConstants->remote.midPackProgress = (*env)->GetMethodID(env, clz, "packProgress", "(IJJ)I");
    jniConstants->remote.midPushTransferProgress = (*env)->GetMethodID(env, clz, "pushTransferProgress", "(JJI)I");
    jniConstants->remote.midPushUpdateReference = (*env)->GetMethodID(env, clz, "pushUpdateReference", "(Ljava/lang/String;Ljava/lang/String;)I");
    jniConstants->remote.midPushNegotiation = (*env)->GetMethodID(env, clz, "pushNegotiation", "([J)I");
    jniConstants->remote.midTransport = (*env)->GetMethodID(env, clz, "transport", "(J)J");
    jniConstants->remote.midResolveUrl = (*env)->GetMethodID(env, clz, "resolveUrl", "(Ljava/lang/String;Ljava/lang/String;I)I");
    /* java/util/List<?> */

    /* clz = j_find_and_hold_clz(env, "java/util/List");
    assert(clz && "List class not found");
    jniConstants->list.midAdd = (*env)->GetMethodID(env, clz, "add", "(Ljava/lang/Object;)z");
    jniConstants->list.midGet = (*env)->GetMethodID(env, clz, "get", "(I)Ljava/lang/Object;");
    jniConstants->list.midSize = (*env)->GetMethodID(env, clz, "size", "()I"); */

    /* oid class and methods */
    clz = j_find_and_hold_clz(env, J_CLZ_PREFIX "Oid");
    assert(clz && "Oid class not found");
    jniConstants->oid.clzOid = clz;
    jniConstants->oid.midSetId = (*env)->GetMethodID(env, clz, "setId", "([B)V");
    jniConstants->oid.midGetId = (*env)->GetMethodID(env, clz, "getId", "()[B");

    /* Buf class */
    clz = j_find_and_hold_clz(env, J_CLZ_PREFIX "Buf");
    assert(clz && "Buf class not found");
    jniConstants->buf.clzBuf = clz;
    jniConstants->buf.emptyConstructor = (*env)->GetMethodID(env, clz, "<init>", "()V");

    /* GitCacheMemorySaver class */
    clz = j_find_and_hold_clz(env, J_CLZ_PREFIX "GitCachedMemorySaver");
    assert(clz && "GitCacheMemorySaver class not found");
    jniConstants->gitCacheMemorySaver.clzGitCacheMemorySaver = clz;
    jniConstants->gitCacheMemorySaver.emptyConstructor = (*env)->GetMethodID(env, clz, "<init>", "()V");;
    jniConstants->gitCacheMemorySaver.midGetCurrentStorageValue = (*env)->GetMethodID(env, clz, "getCurrentStorageValue", "()J");
    jniConstants->gitCacheMemorySaver.midSetCurrentStorageValue =  (*env)->GetMethodID(env, clz, "setCurrentStorageValue", "(J)V");
    jniConstants->gitCacheMemorySaver.midGetMaxStorage = (*env)->GetMethodID(env, clz, "getMaxStorage", "()J");
    jniConstants->gitCacheMemorySaver.midSetMaxStorage = (*env)->GetMethodID(env, clz, "setMaxStorage", "(J)V");

}

void git24j_shutdown(JNIEnv *env)
{
    assert(jniConstants && env && "jvm was shutdown unexpected");
    (*env)->DeleteGlobalRef(env, jniConstants->clzAtomicInt);
    (*env)->DeleteGlobalRef(env, jniConstants->clzAtomicLong);
    (*env)->DeleteGlobalRef(env, jniConstants->clzAtomicReference);
    (*env)->DeleteGlobalRef(env, jniConstants->clzList);
    (*env)->DeleteGlobalRef(env, jniConstants->remote.clzCallbacks);
    (*env)->DeleteGlobalRef(env, jniConstants->oid.clzOid);
    (*env)->DeleteGlobalRef(env, jniConstants->buf.clzBuf);
    (*env)->DeleteGlobalRef(env, jniConstants->gitCacheMemorySaver.clzGitCacheMemorySaver);
    free(jniConstants);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_init)(JNIEnv *env, jclass obj)
{
    git_libgit2_init();
    git24j_init(env);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_shutdown)(JNIEnv *env, jclass obj)
{
    git24j_shutdown(env);
    git_libgit2_shutdown();
}

JNIEXPORT jobject JNICALL J_MAKE_METHOD(Libgit2_version)(JNIEnv *env, jclass obj)
{
    int major, minor, patch;
    git_libgit2_version(&major, &minor, &patch);

    jclass cls = (*env)->FindClass(env, J_CLZ_PREFIX "Version");
    if (cls == NULL)
    {
        return NULL;
    }

    jmethodID ctor = (*env)->GetMethodID(env, cls, "<init>", "(III)V");
    if (ctor == NULL)
    {
        return NULL;
    }

    jobject version = (*env)->NewObject(env, cls, ctor, major, minor, patch);
    return version;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Libgit2_features)(JNIEnv *env, jclass obj)
{
    return git_libgit2_features();
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_jniShadowFree)(JNIEnv *env, jclass obj, jlong ptr)
{
    free((void *)ptr);
}

// git_libgit2_opts() start
/*
    implement every case with depend java method, the method start with "opts",
    eg:
    java method: `optsGitOptSetMwindowSize(long mWindowSize)`
    corresponding
    libgit2's :`git_libgit2_opts(GIT_OPT_SET_MWINDOW_SIZE,mWindowSize)`

*/

void set_opts_jlong(JNIEnv *env, int key, jlong value)
{
    int error = git_libgit2_opts(key, value);
    if(error != 0) {
        j_throw_last_error(env);
    }
}

jlong get_opts_jlong(JNIEnv *env, int key)
{
    jlong out = 0;
    int error = git_libgit2_opts(key, &out);

    if(error != 0) {
        j_throw_last_error(env);
        return JNI_ERR;
    }

    return out;
}

void set_opts_jboolean(JNIEnv *env, int key, jboolean value)
{
    int error = git_libgit2_opts(key, value);
    if(error != 0) {
        j_throw_last_error(env);
    }
}
jboolean get_opts_jboolean(JNIEnv *env, int key)
{
    jlong out = 0;
    int error = git_libgit2_opts(key, &out);

    if(error != 0) {
        j_throw_last_error(env);
        return JNI_FALSE;
    }

    return out?JNI_TRUE:JNI_FALSE;
}

void set_opts_jstring(JNIEnv *env, int key, jstring value)
{
    if(!value) {
        return;
    }

    char *c_str= j_copy_of_jstring(env, value, true);  // last param `true` allow null value, else will assert
    int error = git_libgit2_opts(key, c_str);

    free(c_str);

    if(error != 0) {
        j_throw_last_error(env);
    }
}
jstring get_opts_jstring(JNIEnv *env, int key)
{
    git_buf out = {0};

    int error = git_libgit2_opts(key, &out);

    if(error != 0) {
        git_buf_dispose(&out);

        j_throw_last_error(env);
        return NULL;
    }

    jstring ret = j_git_buf_to_jstring(env, &out);

    git_buf_dispose(&out);

    return ret;
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetMwindowSize)(JNIEnv *env, jclass obj, jlong mWindowSize)
{
    set_opts_jlong(env, GIT_OPT_SET_MWINDOW_SIZE, mWindowSize);
}

JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetMwindowSize)(JNIEnv *env, jclass obj)
{
    return get_opts_jlong(env, GIT_OPT_GET_MWINDOW_SIZE);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetMWindowMappedLimit)(JNIEnv *env, jclass obj, jlong mWindowMappedLimit)
{
    set_opts_jlong(env, GIT_OPT_SET_MWINDOW_MAPPED_LIMIT, mWindowMappedLimit);
}

JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetMWindowMappedLimit)(JNIEnv *env, jclass obj)
{
    return get_opts_jlong(env, GIT_OPT_GET_MWINDOW_MAPPED_LIMIT);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetMWindowFileLimit)(JNIEnv *env, jclass obj, jlong mWindowFileLimit)
{
    set_opts_jlong(env, GIT_OPT_SET_MWINDOW_FILE_LIMIT, mWindowFileLimit);
}

JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetMWindowFileLimit)(JNIEnv *env, jclass obj)
{
    return get_opts_jlong(env, GIT_OPT_GET_MWINDOW_FILE_LIMIT);
}

JNIEXPORT jstring JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetSearchPath)(JNIEnv *env, jclass obj, jint configLevel)
{
    git_buf out = {0};

    int error = git_libgit2_opts(GIT_OPT_GET_SEARCH_PATH, configLevel, &out);

    if(error != 0) {
        git_buf_dispose(&out);

        j_throw_last_error(env);
        return NULL;
    }

    jstring ret = j_git_buf_to_jstring(env, &out);

    git_buf_dispose(&out);

    return ret;
}

// libgit2 use the param `configLevel` to find sysdir, then set the dir to param `path`
JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetSearchPath)(JNIEnv *env, jclass obj, jint configLevel, jstring path)
{
    // path is null
    if(!path) {
        return;
    }

    char *c_path = j_copy_of_jstring(env, path, true);
    int error = git_libgit2_opts(GIT_OPT_SET_SEARCH_PATH, configLevel, c_path);

    free(c_path);

    if(error != 0) {
        j_throw_last_error(env);
    }
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetCacheObjectLimit)(JNIEnv *env, jclass obj, jint type, jlong size)
{
    int error = git_libgit2_opts(GIT_OPT_SET_CACHE_OBJECT_LIMIT, type, size);
    if(error != 0) {
        j_throw_last_error(env);
    }
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetCacheMaxSize)(JNIEnv *env, jclass obj, jlong size)
{
    set_opts_jlong(env, GIT_OPT_SET_CACHE_MAX_SIZE, size);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableCaching)(JNIEnv *env, jclass obj, jboolean enable)
{
    set_opts_jboolean(env, GIT_OPT_ENABLE_CACHING, enable);
}
JNIEXPORT jobject JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetCachedMemory)(JNIEnv *env, jclass obj)
{
    jlong git_cache_current_storage_val = 0;
    jlong git_cache_max_storage = 0;

    int error = git_libgit2_opts(GIT_OPT_GET_CACHED_MEMORY, &git_cache_current_storage_val, &git_cache_max_storage);
    if(error != 0) {
        j_throw_last_error(env);
        return NULL;
    }

    // get GitCacheMemorySaver class and set field, then return the object
    jobject ret = (*env)->NewObject(env, jniConstants->gitCacheMemorySaver.clzGitCacheMemorySaver, jniConstants->gitCacheMemorySaver.emptyConstructor);

    (*env)->CallVoidMethod(env, ret, jniConstants->gitCacheMemorySaver.midSetCurrentStorageValue, git_cache_current_storage_val);
    (*env)->CallVoidMethod(env, ret, jniConstants->gitCacheMemorySaver.midSetMaxStorage, git_cache_max_storage);

    return ret;
}

JNIEXPORT jstring JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetTemplatePath)(JNIEnv *env, jclass obj)
{
    return get_opts_jstring(env, GIT_OPT_GET_TEMPLATE_PATH);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetTemplatePath)(JNIEnv *env, jclass obj, jstring path)
{
    set_opts_jstring(env, GIT_OPT_SET_TEMPLATE_PATH, path);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetSslCertLocations)(JNIEnv *env, jclass obj, jstring file, jstring path)
{
    if(!file && !path) {
        return;
    }

    char *c_file = j_copy_of_jstring(env, file, true);
    char *c_path = j_copy_of_jstring(env, path, true);
    int error = git_libgit2_opts(GIT_OPT_SET_SSL_CERT_LOCATIONS, c_file, c_path);

    free(c_file);
    free(c_path);

    if(error != 0) {
        j_throw_last_error(env);
    }
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetUserAgent)(JNIEnv *env, jclass obj, jstring useragent)
{
    set_opts_jstring(env, GIT_OPT_SET_USER_AGENT, useragent);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableStrictObjectCreation)(JNIEnv *env, jclass obj, jboolean enable)
{
    set_opts_jboolean(env, GIT_OPT_ENABLE_STRICT_OBJECT_CREATION, enable);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableStrictSymbolicRefCreation)(JNIEnv *env, jclass obj, jboolean enable)
{
    set_opts_jboolean(env, GIT_OPT_ENABLE_STRICT_SYMBOLIC_REF_CREATION, enable);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetSslCiphers)(JNIEnv *env, jclass obj, jstring sslCiphers)
{
    set_opts_jstring(env, GIT_OPT_SET_SSL_CIPHERS, sslCiphers);
}
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetUserAgent)(JNIEnv *env, jclass obj)
{
    return get_opts_jstring(env, GIT_OPT_GET_USER_AGENT);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableOfsDelta)(JNIEnv *env, jclass obj, jboolean enable)
{
    set_opts_jboolean(env, GIT_OPT_ENABLE_OFS_DELTA, enable);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableFsyncGitdir)(JNIEnv *env, jclass obj, jboolean enable)
{
    set_opts_jboolean(env, GIT_OPT_ENABLE_FSYNC_GITDIR, enable);
}

JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetWindowsSharemode)(JNIEnv *env, jclass obj)
{
    return get_opts_jlong(env, GIT_OPT_GET_WINDOWS_SHAREMODE);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetWindowsSharemode)(JNIEnv *env, jclass obj, jlong createFileShareMode)
{
    set_opts_jlong(env, GIT_OPT_SET_WINDOWS_SHAREMODE, createFileShareMode);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableStrictHashVerification)(JNIEnv *env, jclass obj, jboolean enable)
{
    set_opts_jboolean(env, GIT_OPT_ENABLE_STRICT_HASH_VERIFICATION, enable);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableUnsavedIndexSafety)(JNIEnv *env, jclass obj, jboolean enable)
{
    set_opts_jboolean(env, GIT_OPT_ENABLE_UNSAVED_INDEX_SAFETY, enable);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetPackMaxObjects)(JNIEnv *env, jclass obj, jlong maxObjects)
{
    set_opts_jlong(env, GIT_OPT_SET_PACK_MAX_OBJECTS, maxObjects);
}

JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetPackMaxObjects)(JNIEnv *env, jclass obj)
{
    return get_opts_jlong(env, GIT_OPT_GET_PACK_MAX_OBJECTS);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptDisablePackKeepFileChecks)(JNIEnv *env, jclass obj, jboolean disable)
{
    set_opts_jboolean(env, GIT_OPT_DISABLE_PACK_KEEP_FILE_CHECKS, disable);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableHttpExpectContinue)(JNIEnv *env, jclass obj, jboolean expect)
{
    set_opts_jboolean(env, GIT_OPT_ENABLE_HTTP_EXPECT_CONTINUE, expect);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetOdbPackedPriority)(JNIEnv *env, jclass obj, jlong priority)
{
    set_opts_jlong(env, GIT_OPT_SET_ODB_PACKED_PRIORITY, priority);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetOdbLoosePriority)(JNIEnv *env, jclass obj, jlong priority)
{
    set_opts_jlong(env, GIT_OPT_SET_ODB_LOOSE_PRIORITY, priority);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetExtensions)(JNIEnv *env, jclass obj, jobjectArray extensionsArray)
{
    git_strarray out = {0};
    j_strarray_from_java(env, &out, extensionsArray);

    int error = git_libgit2_opts(GIT_OPT_SET_EXTENSIONS, out.strings, out.count);

    git_strarray_free(&out);

    if(error != 0) {
        j_throw_last_error(env);
    }
}

JNIEXPORT jobjectArray JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetExtensions)(JNIEnv *env, jclass obj)
{
    git_strarray out = {0};

    int error = git_libgit2_opts(GIT_OPT_GET_EXTENSIONS, &out);

    if(error != 0) {
        git_strarray_free(&out);
        j_throw_last_error(env);
        return NULL;
    }

    jclass clzStr = (*env)->FindClass(env,"java/lang/String");
    jobjectArray ret = (*env)->NewObjectArray(env, out.count, clzStr, NULL);  // last param is initial value

    j_strarray_to_java_array(env, ret, &out);

    git_strarray_free(&out);
    (*env)->DeleteLocalRef(env, clzStr);

    return ret;
}


JNIEXPORT jboolean JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetOwnerValidation)(JNIEnv *env, jclass obj)
{
    return get_opts_jboolean(env, GIT_OPT_GET_OWNER_VALIDATION);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetOwnerValidation)(JNIEnv *env, jclass obj, jboolean validateOwnership)
{
    set_opts_jboolean(env, GIT_OPT_SET_OWNER_VALIDATION, validateOwnership);
}

JNIEXPORT jstring JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetHomedir)(JNIEnv *env, jclass obj)
{
    return get_opts_jstring(env, GIT_OPT_GET_HOMEDIR);
}
JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetHomedir)(JNIEnv *env, jclass obj, jstring homedir)
{
    set_opts_jstring(env, GIT_OPT_SET_HOMEDIR, homedir);
}


JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetServerConnectTimeout)(JNIEnv *env, jclass obj)
{
    return get_opts_jlong(env, GIT_OPT_GET_SERVER_CONNECT_TIMEOUT);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetServerConnectTimeout)(JNIEnv *env, jclass obj, jlong timeout)
{
    set_opts_jlong(env, GIT_OPT_SET_SERVER_CONNECT_TIMEOUT, timeout);
}
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetServerTimeout)(JNIEnv *env, jclass obj)
{
    return get_opts_jlong(env, GIT_OPT_GET_SERVER_TIMEOUT);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetServerTimeout)(JNIEnv *env, jclass obj, jlong timeout)
{
    set_opts_jlong(env, GIT_OPT_SET_SERVER_TIMEOUT, timeout);
}
// git_libgit2_opts() end
