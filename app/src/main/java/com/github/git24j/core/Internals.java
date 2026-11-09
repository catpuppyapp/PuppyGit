package com.github.git24j.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

class Internals {
    @FunctionalInterface
    interface JFCallback {
        int accept(long ptr, float f);
    }

    @FunctionalInterface
    interface JJCallback {
        int accept(long ptr1, long ptr2);
    }

    @FunctionalInterface
    interface JCallback {
        int accept(long ptr);
    }

    @FunctionalInterface
    interface ICallback {
        int accept(int ptr);
    }

    @FunctionalInterface
    interface JJJCallback {
        int accept(long ptr1, long ptr2, long ptr3);
    }

    @FunctionalInterface
    interface BArrCallback {
        int accept(byte[] rawid);
    }

    @FunctionalInterface
    interface BArrBarrCallback {
        int accept(byte[] id1, byte[] id2);
    }

    @FunctionalInterface
    interface ISJJJCallback {
        int accept(int flag, String str, long ptr1, long ptr2, long ptr3);
    }

    @FunctionalInterface
    interface SIICallback {
        int accept(String s, int i1, int i2);
    }

    @FunctionalInterface
    interface IIICallback {
        int accept(int i0, int i1, int i2);
    }

    @FunctionalInterface
    interface SJCallback {
        int accept(String s, long ptr);
    }

    @FunctionalInterface
    interface ASICallback {
        int accept(AtomicLong out, String str, int i);
    }

    @FunctionalInterface
    interface ALSSCallback {
        int accept(AtomicLong outRemote, long repoPtr, String name, String url);
    }

    @FunctionalInterface
    interface SSIJCallback {
        long accept(String s1, String s2, int i);
    }

    @FunctionalInterface
    interface JISCallback {
        int accept(long ptr, int i, String s);
    }

    @FunctionalInterface
    interface SSSCallback {
        int accept(String s1, String s2, String s3);
    }

    @FunctionalInterface
    interface SSCallback {
        int accept(String s1, String s2);
    }

    @FunctionalInterface
    interface ISBarrCalback {
        int accept(int i1, String s, byte[] bytes);
    }

    /** Class to hold */
    static class OidArray {
        private final List<Oid> _oids = new ArrayList<>();

        void add(byte[] oidRaw) {
            _oids.add(Oid.of(oidRaw));
        }

        public List<Oid> getOids() {
            return _oids;
        }
    }
}
