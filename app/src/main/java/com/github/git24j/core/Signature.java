package com.github.git24j.core;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Signature extends CAutoReleasable {
    /** int git_signature_default(git_signature **out, git_repository *repo); */
    static native int jniDefault(AtomicLong out, long repoPtr);

    /** int git_signature_dup(git_signature **dest, const git_signature *sig); */
    static native int jniDup(AtomicLong dest, long sig);

    /** void git_signature_free(git_signature *sig); */
    static native void jniFree(long sig);

    /** int git_signature_from_buffer(git_signature **out, const char *buf); */
    static native int jniFromBuffer(AtomicLong out, String buf);

    static native String jniGetEmail(long sigPtr);

    static native long jniGetEpocSeconds(long sigPtr);

    static native String jniGetName(long sigPtr);

    static native int jniGetOffsetMinutes(long sigPtr);

    /**
     * int git_signature_new(git_signature **out, const char *name, const char *email, git_time_t
     * time, int offset);
     */
    static native int jniNew(AtomicLong out, String name, String email, long time, int offset);

    /** int git_signature_now(git_signature **out, const char *name, const char *email); */
    static native int jniNow(AtomicLong out, String name, String email);

    protected Signature(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    /**
     * Create a new action signature.
     *
     * <p>Note: angle brackets ('<' and '>') characters are not allowed to be used in either the
     * `name` or the `email` parameter.
     *
     * @param name name of the person
     * @param email email of the person
     * @param time time (in seconds from epoch) when the action happened
     * @throws GitException git errors
     */
    public Signature(@Nonnull String name, @Nonnull String email, @Nonnull OffsetDateTime time) {
        super(false, 0);
        Error.throwIfNeeded(
                jniNew(
                        _rawPtr,
                        name,
                        email,
                        time.toEpochSecond(),
                        time.getOffset().getTotalSeconds() / 60));
    }

    public Signature(
            @Nonnull String name, @Nonnull String email, long epocSecs, int offsetMinutes) {
        super(false, 0);
        Error.throwIfNeeded(jniNew(_rawPtr, name, email, epocSecs, offsetMinutes));
    }

    /**
     * Create a new action signature with a timestamp of 'now'.
     *
     * <p>Call `git_signature_free()` to free the data.
     *
     * @return out new signature
     * @param name name of the person
     * @param email email of the person
     * @throws GitException git errors
     */
    @Nonnull
    public static Signature create(@Nonnull String name, @Nonnull String email) {
        Signature out = new Signature(false, 0);
        Error.throwIfNeeded(jniNow(out._rawPtr, name, email));
        if (out.isNull()) {
            throw new NullPointerException("Failed to create Signature");
        }
        return out;
    }

    /**
     * Create a new action signature with default user and now timestamp.
     *
     * <p>This looks up the user.name and user.email from the configuration and uses the current
     * time as the timestamp, and creates a new signature based on that information. It will return
     * GIT_ENOTFOUND if either the user.name or user.email are not set.
     *
     * @return new signature or empty if config is missing
     * @param repo repository pointer
     * @throws GitException git errors
     */
    @Nullable
    public static Signature getDefault(@Nonnull Repository repo) {
        Signature sig = new Signature(false, 0);
        int e = jniDefault(sig._rawPtr, repo.getRawPointer());
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return sig.isNull() ? null : sig;
    }

    /**
     * Create a new signature by parsing the given buffer, which is expected to be in the format
     * "Real Name <email> timestamp tzoffset", where `timestamp` is the number of seconds since the
     * Unix epoch and `tzoffset` is the timezone offset in `hhmm` format (note the lack of a colon
     * separator).
     *
     * @return out new signature
     * @param buf signature string
     * @throws GitException git errors
     */
    @Nonnull
    public static Signature fromBuffer(@Nonnull String buf) {
        Signature out = new Signature(false, 0);
        Error.throwIfNeeded(jniFromBuffer(out._rawPtr, buf));
        return out;
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    /**
     * Create a copy of an existing signature. All internal strings are also duplicated.
     *
     * <p>Call `git_signature_free()` to free the data.
     *
     * @return copy of the signature
     * @throws GitException git error
     */
    public Signature dup() {
        Signature out = new Signature(false, 0);
        Error.throwIfNeeded(jniDup(out._rawPtr, getRawPointer()));
        return out;
    }

    @Nonnull
    public String getName() {
        return jniGetName(getRawPointer());
    }

    @Nonnull
    public String getEmail() {
        return jniGetEmail(getRawPointer());
    }

    @Nonnull
    public OffsetDateTime getWhen() {
        return getWhenWithOffset(null);
    }

    /**
     *
     * @param timezoneOffsetInMinutes if null, will use offset from signature
     * @return
    */
    @Nonnull
    public OffsetDateTime getWhenWithOffset(@Nullable Integer timezoneOffsetInMinutes) {
        int minutes = timezoneOffsetInMinutes==null ? timeOffsetInMinutes() : timezoneOffsetInMinutes;
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(minutes * 60);
        return Instant.ofEpochSecond(timeInUtcSeconds()).atOffset(offset);
    }

    public Integer timeOffsetInMinutes() {
        return jniGetOffsetMinutes(getRawPointer());
    }

    public Long timeInUtcSeconds() {
        return jniGetEpocSeconds(getRawPointer());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Signature signature = (Signature) o;
        return Objects.equals(getName(), signature.getName())
                && Objects.equals(getEmail(), signature.getEmail())
                && Objects.equals(timeOffsetInMinutes(), signature.timeOffsetInMinutes())
                && Objects.equals(timeInUtcSeconds(), signature.timeInUtcSeconds())
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getEmail(), timeOffsetInMinutes(),timeInUtcSeconds());
    }
}
