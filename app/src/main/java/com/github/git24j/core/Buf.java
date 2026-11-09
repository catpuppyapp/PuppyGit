package com.github.git24j.core;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Buf {
    private byte[] ptr;

    private int reserved;
    private int size;  // this size include c char NUL-byte '\0', should -1 when trans to java string by bytes

//    private String cachedString;  // if the Buf is immutable, cachedString is ok, but idk is immutable or isn't, if isn't, shouldn't cache it

    /** Get internal buffer, generally only the substr up to size is meaningful. */
    public byte[] getPtr() {
        return ptr;
    }

    public void setPtr(byte[] ptr) {
        this.ptr = ptr;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Optional<String> getString() {
        if (size < 1 || ptr == null || ptr.length<1) {
            return Optional.empty();  // value of empty Optional is null
        }

        // this should never happen
        if(size > ptr.length) {
            size = ptr.length;
        }

        return Optional.of(new String(ptr, 0, size, StandardCharsets.UTF_8));
    }

    @Override
    public String toString() {
//        if(cachedString == null) {  // even concurrence should be ok, just will executing getString more than 1 time
//            cachedString = getString().orElse("");
//        }
//
//        return cachedString;

        return getString().orElse("");
    }
}
