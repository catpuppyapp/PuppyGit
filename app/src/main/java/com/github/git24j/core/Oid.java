package com.github.git24j.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;

/**
 * An Oid is a 20 bytes array (each byte coded 32bit), or a 40 hex characters string (16 bit coded)
 */
public class Oid {
    public static final int RAWSZ = 20;
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    private static final String ZERO_HEX = "0000000000000000000000000000000000000000";
    private static final byte[] ZERO_SHA = new byte[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};  //len is 20

    /** in case of short sha, only up to {@code eSize} bytes are effective */
    private byte[] id = new byte[RAWSZ];

    Oid() {}

    Oid(byte[] bytes) {
        if (bytes.length != RAWSZ) {
            throw new IllegalArgumentException("Invalid Oid data, length must be 20");
        }
        id = bytes;
    }

    @CheckForNull
    public static Oid ofNullable(@Nullable byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new Oid(bytes);
    }

    public static Oid of(@Nonnull byte[] bytes) {
        return new Oid(bytes);
    }

    public static Oid of(@Nonnull String hexSha) {
        // kotlin default `String.lowercase()` use `Local.ROOT`, maybe more compatible and less err? idk, just use it
        byte[] bytes = hexStringToByteArray(hexSha.toLowerCase(Locale.ROOT));
        return new Oid(bytes);
    }

    private static String bytesToHex(byte[] bytes, int len) {
        int cutoffLen = Math.min(len, bytes.length);
        char[] hexChars = new char[cutoffLen * 2];
        for (int j = 0; j < cutoffLen; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i + 1 < len; i += 2) {
            int front4 = Character.digit(s.charAt(i), 16);
            int end4 = Character.digit(s.charAt(i + 1), 16);
            if (front4 < 0 || end4 < 0) {
                throw new IllegalArgumentException("Invalid hex string: " + s);
            }
            data[i / 2] = (byte) ((front4 << 4) | end4);  // maybe faster bit_or than + ?
        }
        return data;
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public boolean isEmpty() {
        return id == null || id.length == 0;
    }

    public boolean isZero() {
        return Arrays.equals(id, ZERO_SHA);
    }

    public boolean isNullOrEmptyOrZero() {
        return isEmpty() || isZero();
    }

    @Override
    public String toString() {
        return bytesToHex(id, id.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Oid oid = (Oid) o;
        return Arrays.equals(this.id, oid.id);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(id);
    }
}
