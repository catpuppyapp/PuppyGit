package com.github.git24j.core;

import java.util.EnumSet;

public class Libgit2 {
    static native void jniShadowFree(long ptr);

    public static native void init();

    public static native void shutdown();

    public static native Version version();

    public static native int features();

    /** Wapper of features that returns an enum set. */
    public static EnumSet<GitFeature> featuresSet() {
        return GitFeature.valuesOf(features());
    }

    // git_libgit2_opts() start
    /* note:
        all these methods may throws GitException,
        but I saw in this project other methods, almost no one wrote throws part
        on method signature, so I don't wrote throws on method signature too.
        better use try catch when calling below methods.
    * */
    public static native void optsGitOptSetMwindowSize(long mWindowSize);
    public static native long optsGitOptGetMwindowSize();
    public static native void optsGitOptSetMWindowMappedLimit(long mWindowMappedLimit);
    public static native long optsGitOptGetMWindowMappedLimit();
    public static native void optsGitOptSetMWindowFileLimit(long mWindowFileLimit);
    public static native long optsGitOptGetMWindowFileLimit();

    private static native String optsGitOptGetSearchPath(int configLevel);
    private static native void optsGitOptSetSearchPath(int configLevel, String path);
    public static String optsGitOptGetSearchPath(Config.ConfigLevel configLevel) {
        return optsGitOptGetSearchPath(configLevel.getValue());
    }
    public static void optsGitOptSetSearchPath(Config.ConfigLevel configLevel, String path){
        optsGitOptSetSearchPath(configLevel.getValue(), path);
    }

    private static native void optsGitOptSetCacheObjectLimit(int type, long size);
    public static void optsGitOptSetCacheObjectLimit(GitObject.Type type, long size){
        optsGitOptSetCacheObjectLimit(type.getBit(),size);
    }

    public static native void optsGitOptSetCacheMaxSize(long size);
    public static native void optsGitOptEnableCaching(boolean enable);
    public static native GitCachedMemorySaver optsGitOptGetCachedMemory();
    public static native String optsGitOptGetTemplatePath();
    public static native void optsGitOptSetTemplatePath(String path);
    public static native void optsGitOptSetSslCertLocations(String file, String path);
    public static native void optsGitOptSetUserAgent(String useragent);
    public static native void optsGitOptEnableStrictObjectCreation(boolean enable);
    public static native void optsGitOptEnableStrictSymbolicRefCreation(boolean enable);
    public static native void optsGitOptSetSslCiphers(String sslCiphers);
    public static native String optsGitOptGetUserAgent();
    public static native void optsGitOptEnableOfsDelta(boolean enable);
    public static native void optsGitOptEnableFsyncGitdir(boolean enable);
    public static native long optsGitOptGetWindowsSharemode();
    public static native void optsGitOptSetWindowsSharemode(long createFileShareMode);
    public static native void optsGitOptEnableStrictHashVerification(boolean enable);
    public static native void optsGitOptEnableUnsavedIndexSafety(boolean enable);
    public static native void optsGitOptSetPackMaxObjects(long maxObjects);
    public static native long optsGitOptGetPackMaxObjects();
    public static native void optsGitOptDisablePackKeepFileChecks(boolean disable);
    public static native void optsGitOptEnableHttpExpectContinue(boolean expect);
    public static native void optsGitOptSetOdbPackedPriority(long priority);
    public static native void optsGitOptSetOdbLoosePriority(long priority);
    public static native void optsGitOptSetExtensions(String[] extensionsArray);
    public static native String[] optsGitOptGetExtensions();
    public static native boolean optsGitOptGetOwnerValidation();
    public static native void optsGitOptSetOwnerValidation(boolean validateOwnership);
    public static native String optsGitOptGetHomedir();
    public static native void optsGitOptSetHomedir(String homedir);
    public static native long optsGitOptGetServerConnectTimeout();
    public static native void optsGitOptSetServerConnectTimeout(long timeout);
    public static native long optsGitOptGetServerTimeout();
    public static native void optsGitOptSetServerTimeout(long timeout);

    // git_libgit2_opts() end
}
