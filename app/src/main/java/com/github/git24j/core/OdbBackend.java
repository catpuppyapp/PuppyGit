package com.github.git24j.core;

public class OdbBackend extends CAutoReleasable {
    protected OdbBackend(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {
        Libgit2.jniShadowFree(cPtr);
    }
}
