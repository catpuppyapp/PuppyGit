package com.catpuppyapp.puppygit.jni;

import android.util.Log;
import com.bytedance.android.bytehook.ByteHook;
import com.catpuppyapp.puppygit.utils.saf.SafFile;


public class LibLoader {
    private static final String TAG="LibLoader";
    static boolean safHookJni = false;

    static {
        Log.d(TAG, "loading c libs...");

        System.loadLibrary("crypto");
        System.loadLibrary("ssl");
        System.loadLibrary("ssh2");
        System.loadLibrary("git2");
//        System.loadLibrary("git24j");
        System.loadLibrary("puppygit");

        Log.d(TAG, "c libs loaded");

        if(safHookJni) {
            Log.d(TAG, "safHookJni init");

            int initResult = ByteHook.init();
            Log.d(TAG, "`ByteHook.init()` initResult = "+initResult);
            SafFile.nativeHookFile("libgit2.so", null);
            SafFile.nativeInitSafJavaCallbacks();

            Log.d(TAG, "safHookJni finished");
        }

    }


    public static void load() {
        //象征性的空方法，没必要实现，加载这个类的class的时候就会执行静态代码块加载动态库了
        Log.d(TAG, "load() is a stub method");
    }
}
