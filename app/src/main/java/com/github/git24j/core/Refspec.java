package com.github.git24j.core;

import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Refspec extends CAutoReleasable {
    /** git_direction git_refspec_direction(const git_refspec *spec); */
    static native int jniDirection(long spec);

    /** const char * git_refspec_dst(const git_refspec *refspec); */
    static native String jniDst(long refspec);

    /** int git_refspec_dst_matches(const git_refspec *refspec, const char *refname); */
    static native int jniDstMatches(long refspec, String refname);

    /** int git_refspec_force(const git_refspec *refspec); */
    static native int jniForce(long refspec);

    /** void git_refspec_free(git_refspec *refspec); */
    static native void jniFree(long refspec);

    /** int git_refspec_parse(git_refspec **refspec, const char *input, int is_fetch); */
    static native int jniParse(AtomicLong refspec, String input, int isFetch);

    /** int git_refspec_rtransform(git_buf *out, const git_refspec *spec, const char *name); */
    static native int jniRtransform(Buf out, long spec, String name);

    /** const char * git_refspec_src(const git_refspec *refspec); */
    static native String jniSrc(long refspec);

    /** int git_refspec_src_matches(const git_refspec *refspec, const char *refname); */
    static native int jniSrcMatches(long refspec, String refname);

    /** const char * git_refspec_string(const git_refspec *refspec); */
    static native String jniString(long refspec);

    /** int git_refspec_transform(git_buf *out, const git_refspec *spec, const char *name); */
    static native int jniTransform(Buf out, long spec, String name);

    protected Refspec(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    /**
     * Parse a given refspec string
     *
     * @return the refspec handle
     * @param input the refspec string
     * @param isFetch is this a refspec for a fetch
     * @throws GitException git errors
     */
    @Nonnull
    public static Refspec parse(@Nonnull String input, boolean isFetch) {
        Refspec out = new Refspec(false, 0);
        Error.throwIfNeeded(jniParse(out._rawPtr, input, isFetch ? 1 : 0));
        return out;
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    /** @return the refspec's source specifier */
    public String src() {
        return jniSrc(getRawPointer());
    }

    /** @return the refspec's destination specifier */
    public String dst() {
        return jniDst(getRawPointer());
    }

    /** @returns the refspec's original string */
    public String getString() {
        return jniString(getRawPointer());
    }

    /** @return true if force update has been set, 0 otherwise */
    public boolean force() {
        return jniForce(getRawPointer()) == 1;
    }

    /**
     * Get the refspec's direction.
     *
     * @return GIT_DIRECTION_FETCH or GIT_DIRECTION_PUSH
     */
    @Nonnull
    public Remote.Direction direction() {
        return jniDirection(getRawPointer()) == 0 ? Remote.Direction.FETCH : Remote.Direction.PUSH;
    }

    /**
     * Check if a refspec's source descriptor matches a reference
     *
     * @param refname the name of the reference to check
     * @return true if the refspec matches
     */
    public boolean srcMatches(@Nonnull String refname) {
        return jniSrcMatches(getRawPointer(), refname) == 1;
    }

    /**
     * Check if a refspec's destination descriptor matches a reference
     *
     * @param refname the name of the reference to check
     * @return true if the refspec matches
     */
    public boolean dstMatches(@Nonnull String refname) {
        return jniDstMatches(getRawPointer(), refname) == 1;
    }

    /**
     * Transform a reference to its target following the refspec's rules
     *
     * @return the target name
     * @param name the name of the reference to transform
     * @throws GitException GIT_EBUFS or other errors
     */
    @Nullable
    public String transform(@Nonnull String name) {
        Buf out = new Buf();
        Error.throwIfNeeded(jniTransform(out, getRawPointer(), name));
        return out.getString().orElse(null);
    }

    /**
     * Transform a reference to its target following the refspec's rules
     *
     * @return the target name
     * @param name the name of the reference to transform
     * @throws GitException GIT_EBUFS or another error
     */
    @Nullable
    public String rtransform(@Nonnull String name) {
        Buf out = new Buf();
        Error.throwIfNeeded(jniRtransform(out, getRawPointer(), name));
        return out.getString().orElse(null);
    }
}
