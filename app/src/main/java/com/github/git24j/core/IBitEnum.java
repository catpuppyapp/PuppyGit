package com.github.git24j.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public interface IBitEnum {
    static <T extends Enum<T> & IBitEnum> int bitOrAll(EnumSet<T> enums) {
        if (enums == null) {
            return 0;
        }
        int res = 0;
        for (T x : enums) {
            res |= x.getBit();
        }
        return res;
    }

    static <T extends Enum<T> & IBitEnum> EnumSet<T> parse(int flags, Class<T> clz) {
        List<T> matched = new ArrayList<>();
        for (T x : clz.getEnumConstants()) {
            int b = x.getBit();
            if (b == 0 && flags == 0) {
                matched.add(x);
                break;
            }
            if ((b < 0 && flags > 0) || (b > 0 && flags < 0)) {
                continue;
            }
            if ((b & flags) != 0) {
                matched.add(x);
            }
        }
        return matched.isEmpty() ? EnumSet.noneOf(clz) : EnumSet.copyOf(matched);
    }

    /** Parse bit enums from candidates */
    @Nullable
    static <T extends Enum<T> & IBitEnum> EnumSet<T> parse(int flags, EnumSet<T> candidates) {
        List<T> matched = new ArrayList<>();

        for (T x : candidates) {
            int b = x.getBit();
            if (b == 0 && flags == 0) {
                matched.add(x);
                break;
            }
            if ((b < 0 && flags > 0) || (b > 0 && flags < 0)) {
                continue;
            }
            if ((b & flags) != 0) {
                matched.add(x);
            }
        }
        return matched.isEmpty() ? null : EnumSet.copyOf(matched);
    }

    static <T extends Enum<T> & IBitEnum> T valueOf(int bit, Class<T> clz, T defaultVal) {
        for (T x : clz.getEnumConstants()) {
            if (x.getBit() == bit) {
                return x;
            }
        }
        return defaultVal;
    }

    /**
     * Get enum item from int value
     *
     * @param bit bit value
     * @param clz class to parse
     * @param <T>
     * @return enum item
     */
    @CheckForNull
    static <T extends Enum<T> & IBitEnum> T valueOf(int bit, Class<T> clz) {
        return IBitEnum.valueOf(bit, clz, null);
    }

    int getBit();
}
