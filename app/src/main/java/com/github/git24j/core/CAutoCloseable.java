package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Base bridge that manages raw c pointer, use this when resources carried in an object are shorter
 * than the object itself
 */
public abstract class CAutoCloseable implements AutoCloseable {
    /** C Pointer. */
    protected final AtomicLong _rawPtr = new AtomicLong();

    protected CAutoCloseable(long rawPointer) {
        _rawPtr.set(rawPointer);
    }

    /**
     * Get raw pointer of the repo.
     *
     * @return pointer value in long
     * @throws IllegalStateException if repository has already been closed.
     */
    long getRawPointer() {
        long ptr = _rawPtr.get();
        if (ptr == 0) {
            throw new IllegalStateException(
                    "Object has invalid memory address, likely it has been closed.");
        }
        return ptr;
    }
    /** Call this to actual release the resources */
    protected abstract void releaseOnce(long cPtr);

    protected boolean isNull() {
        return _rawPtr.get() == 0;
    }

    @Override
    public void close() {
        if (_rawPtr.get() != 0) {
            releaseOnce(_rawPtr.getAndSet(0));
        }
    }
}
