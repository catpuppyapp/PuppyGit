#include "j_common.h"
#include "j_mappers.h"
#include <jni.h>

#ifndef __GIT24J_GLOBAL_H__
#define __GIT24J_GLOBAL_H__
#ifdef __cplusplus
extern "C"
{
#endif
    jclass j_find_and_hold_clz(JNIEnv *env, const char *descriptor);

    extern JavaVM *globalJvm;

    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_init)(JNIEnv *, jclass);

    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_shutdown)(JNIEnv *, jclass);

    JNIEXPORT jobject JNICALL J_MAKE_METHOD(Libgit2_version)(JNIEnv *, jclass);

    JNIEXPORT jint JNICALL J_MAKE_METHOD(Libgit2_features)(JNIEnv *, jclass);

    /**call free(void* ptr) without recursively freeing fields. */
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_jniShadowFree)(JNIEnv *env, jclass obj, jlong ptr);

    // git_libgit2_opts() start
    //GIT_OPT_SET_MWINDOW_SIZE
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetMwindowSize)(JNIEnv *env, jclass obj, jlong mWindowSize);
    //GIT_OPT_GET_MWINDOW_SIZE
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetMwindowSize)(JNIEnv *env, jclass obj);

    //GIT_OPT_SET_MWINDOW_MAPPED_LIMIT
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetMWindowMappedLimit)(JNIEnv *env, jclass obj, jlong mWindowMappedLimit);
    //GIT_OPT_GET_MWINDOW_MAPPED_LIMIT
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetMWindowMappedLimit)(JNIEnv *env, jclass obj);

    //GIT_OPT_SET_MWINDOW_FILE_LIMIT
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetMWindowFileLimit)(JNIEnv *env, jclass obj, jlong mWindowFileLimit);
    //GIT_OPT_GET_MWINDOW_FILE_LIMIT
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetMWindowFileLimit)(JNIEnv *env, jclass obj);

    //GIT_OPT_GET_SEARCH_PATH
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetSearchPath)(JNIEnv *env, jclass obj, jint configLevel);
    //GIT_OPT_SET_SEARCH_PATH
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetSearchPath)(JNIEnv *env, jclass obj, jint configLevel, jstring path);

    //GIT_OPT_SET_CACHE_OBJECT_LIMIT
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetCacheObjectLimit)(JNIEnv *env, jclass obj, jint type, jlong size);

    //GIT_OPT_SET_CACHE_MAX_SIZE
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetCacheMaxSize)(JNIEnv *env, jclass obj, jlong size);

    //GIT_OPT_ENABLE_CACHING
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableCaching)(JNIEnv *env, jclass obj, jboolean enable);

    //GIT_OPT_GET_CACHED_MEMORY
    //return is :com.github.git24j.core.GitCachedMemorySaver
    JNIEXPORT jobject JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetCachedMemory)(JNIEnv *env, jclass obj);

    //GIT_OPT_GET_TEMPLATE_PATH
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetTemplatePath)(JNIEnv *env, jclass obj);
    //GIT_OPT_SET_TEMPLATE_PATH
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetTemplatePath)(JNIEnv *env, jclass obj, jstring path);

    //GIT_OPT_SET_SSL_CERT_LOCATIONS
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetSslCertLocations)(JNIEnv *env, jclass obj, jstring file, jstring path);

    //GIT_OPT_SET_USER_AGENT
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetUserAgent)(JNIEnv *env, jclass obj, jstring useragent);

    //GIT_OPT_ENABLE_STRICT_OBJECT_CREATION
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableStrictObjectCreation)(JNIEnv *env, jclass obj, jboolean enable);
    //GIT_OPT_ENABLE_STRICT_SYMBOLIC_REF_CREATION
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableStrictSymbolicRefCreation)(JNIEnv *env, jclass obj, jboolean enable);

    //GIT_OPT_SET_SSL_CIPHERS
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetSslCiphers)(JNIEnv *env, jclass obj, jstring sslCiphers);

    //GIT_OPT_GET_USER_AGENT
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetUserAgent)(JNIEnv *env, jclass obj);

    //GIT_OPT_ENABLE_OFS_DELTA
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableOfsDelta)(JNIEnv *env, jclass obj, jboolean enable);

    //GIT_OPT_ENABLE_FSYNC_GITDIR
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableFsyncGitdir)(JNIEnv *env, jclass obj, jboolean enable);

    //GIT_OPT_GET_WINDOWS_SHAREMODE
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetWindowsSharemode)(JNIEnv *env, jclass obj);
    //GIT_OPT_SET_WINDOWS_SHAREMODE
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetWindowsSharemode)(JNIEnv *env, jclass obj, jlong createFileShareMode);

    //GIT_OPT_ENABLE_STRICT_HASH_VERIFICATION
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableStrictHashVerification)(JNIEnv *env, jclass obj, jboolean enable);

    // TODO : `GIT_OPT_SET_ALLOCATOR` key isn't implement
    // git_libgit2_opts(GIT_OPT_SET_ALLOCATOR, git_allocator * allocator)

    //GIT_OPT_ENABLE_UNSAVED_INDEX_SAFETY
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableUnsavedIndexSafety)(JNIEnv *env, jclass obj, jboolean enable);

    //GIT_OPT_SET_PACK_MAX_OBJECTS
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetPackMaxObjects)(JNIEnv *env, jclass obj, jlong maxObjects);
    //GIT_OPT_GET_PACK_MAX_OBJECTS
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetPackMaxObjects)(JNIEnv *env, jclass obj);

    //GIT_OPT_DISABLE_PACK_KEEP_FILE_CHECKS
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptDisablePackKeepFileChecks)(JNIEnv *env, jclass obj, jboolean disable);

    //GIT_OPT_ENABLE_HTTP_EXPECT_CONTINUE
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptEnableHttpExpectContinue)(JNIEnv *env, jclass obj, jboolean expect);

    //GIT_OPT_SET_ODB_PACKED_PRIORITY
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetOdbPackedPriority)(JNIEnv *env, jclass obj, jlong priority);

    //GIT_OPT_SET_ODB_LOOSE_PRIORITY
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetOdbLoosePriority)(JNIEnv *env, jclass obj, jlong priority);

    //GIT_OPT_SET_EXTENSIONS
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetExtensions)(JNIEnv *env, jclass obj, jobjectArray extensionsArray);

    //GIT_OPT_GET_EXTENSIONS
    JNIEXPORT jobjectArray JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetExtensions)(JNIEnv *env, jclass obj);

    //GIT_OPT_GET_OWNER_VALIDATION
    JNIEXPORT jboolean JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetOwnerValidation)(JNIEnv *env, jclass obj);
    //GIT_OPT_SET_OWNER_VALIDATION
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetOwnerValidation)(JNIEnv *env, jclass obj, jboolean validateOwnership);

    //GIT_OPT_GET_HOMEDIR
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetHomedir)(JNIEnv *env, jclass obj);
    //GIT_OPT_SET_HOMEDIR
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetHomedir)(JNIEnv *env, jclass obj, jstring homedir);

    //GIT_OPT_GET_SERVER_CONNECT_TIMEOUT
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetServerConnectTimeout)(JNIEnv *env, jclass obj);
    //GIT_OPT_SET_SERVER_CONNECT_TIMEOUT
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetServerConnectTimeout)(JNIEnv *env, jclass obj, jlong timeout);

    //GIT_OPT_GET_SERVER_TIMEOUT
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Libgit2_optsGitOptGetServerTimeout)(JNIEnv *env, jclass obj);
    //GIT_OPT_SET_SERVER_TIMEOUT
    JNIEXPORT void JNICALL J_MAKE_METHOD(Libgit2_optsGitOptSetServerTimeout)(JNIEnv *env, jclass obj, jlong timeout);

    // git_libgit2_opts() end

#ifdef __cplusplus
}
#endif
#endif
