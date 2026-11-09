package com.github.git24j.core;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicLong;

public class Revparse {
    static native int jniExt(AtomicLong outObj, AtomicLong outRef, long repoPtr, String spec);

    static native int jniLookup(Revspec revspec, long repoPtr, String spec);

    static native int jniSingle(AtomicLong outObj, long repoPtr, String spec);

    public static Revspec lookup(Repository repository, String spec) {
        Revspec revspec = new Revspec(null, null, EnumSet.noneOf(Mode.class));
        Error.throwIfNeeded(jniLookup(revspec, repository.getRawPointer(), spec));
        return revspec;
    }

    /**
     * Find a single object, as specified by a revision string.
     *
     * @param repository the repository to search in
     * @param spec the textual specification for an object (must not be null)
     * @return output object
     * @throws GitException git error
     * @throws IllegalStateException if repository has already been closed.
     */
    public static GitObject single(Repository repository, String spec) {
        AtomicLong outObj = new AtomicLong();
        Error.throwIfNeeded(jniSingle(outObj, repository.getRawPointer(), spec));
        return GitObject.create(outObj.get());
    }

    /**
     * Find a single object and intermediate reference by a revision string.
     *
     * @param repository the repository to search in
     * @param spec the textual specification for an object (must not be null).
     * @return single object and intermediate reference found from rev parsing.
     * @throws GitException git error.
     * @throws IllegalStateException if repository already closed.
     */
    public static ExtReturn ext(Repository repository, String spec) {
        AtomicLong outObj = new AtomicLong();
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniExt(outObj, outRef, repository.getRawPointer(), spec));
        return new ExtReturn(
                outObj.get() != 0 ? new GitObject(false, outObj.get()) : null,
                outRef.get() != 0 ? new Reference(false, outRef.get()) : null);
    }

    public enum Mode implements IBitEnum {
        SINGLE(1 << 0),
        RANGE(1 << 1),
        MERGE_BASE(1 << 2);
        final int _bit;

        Mode(int bit) {
            this._bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public static class ExtReturn {
        private final GitObject obj;
        private final Reference ref;

        public ExtReturn(GitObject obj, Reference ref) {
            this.obj = obj;
            this.ref = ref;
        }

        /** single object found from rev parsing. */
        public GitObject getObj() {
            return obj;
        }

        /** reference found for rev parsing */
        public Reference getRef() {
            return ref;
        }
    }

    public static class Revspec {
        GitObject from;
        GitObject to;
        EnumSet<Mode> flags;

        Revspec(GitObject from, GitObject to, EnumSet<Mode> flags) {
            this.from = from;
            this.to = to;
            this.flags = flags;
        }

        public GitObject getFrom() {
            return from;
        }

        /** setter exposed to jni. */
        void setFrom(long fromPtr) {
            this.from = new GitObject(false, fromPtr);
        }

        public GitObject getTo() {
            return to;
        }

        /** setter exposed to jni. */
        void setTo(long toPtr) {
            this.to = new GitObject(false, toPtr);
        }

        public EnumSet<Mode> getFlags() {
            return flags;
        }

        /** setter exposed to jni. */
        void setFlags(int flags) {
            this.flags = IBitEnum.parse(flags, Mode.class);
        }
    }
}
