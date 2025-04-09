package com.github.git24j.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
* TODO 把这个类的函数改成调用libgit2的api以兼容sha256
* TODO 如果支持sha256还有个潜在的问题：如果一个仓库混合sha256和sha1怎么办？
*  若直接用sha1的全0 oid理论上有可能会出错，但概率不大。最好判断下，先取head，如果是256就用256的全0oid，否则用sha1的全0oid，若不确定就用sha1。
*  代码修改：需要在Cons里添加个sha256的全0oid。
* TODO Files 的 init repo也需要调整，让用户选择是创建sha1的还是sha256的仓库，最好是能选，不能选的话，默认应该是sha1？未测试。
* TODO 虽然sha1理论上会存在冲突，但ipv4理论上还会耗尽呢，不也一样没被淘汰，已经投入使用的东西，只要能用且没有硬伤，就很难被彻底替换，
*  而且对于普通用户来说，遇到sha1冲突的概率很低（md5比sha1更容易发生冲突，但实际上我连md5冲突都没遇到过），所以我觉得sha256至少5年内（2030年之前）不会全面替换sha1，换句话说这个功能可以先鸽了，
*  除非特别多用户要求这个功能或者我收到很多赞助然后充满动力去写各种偏门功能，否则预计一时半会不用做这个功能。
* 
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
        byte[] bytes = hexStringToByteArray(hexSha.toLowerCase());
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
