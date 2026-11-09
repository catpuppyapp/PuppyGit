package com.github.git24j.core;

public class TransferProgress extends CAutoReleasable {
    protected TransferProgress(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {}
}
