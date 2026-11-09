package com.github.git24j.core;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static com.github.git24j.core.GitException.ErrorCode;

public class Reference extends CAutoReleasable {
    static native int jniCmp(long ref1Ptr, long ref2Ptr);

    static native int jniCreate(
            AtomicLong outRef, long repoPtr, String name, Oid oid, int force, String logMessage);

    static native int jniCreateMatching(
            AtomicLong outRef,
            long repoPtr,
            String name,
            Oid oid,
            int force,
            Oid currentId,
            String logMessage);

    static native int jniDelete(long refPtr);

    static native int jniDup(AtomicLong outDest, long sourcePtr);

    static native int jniDwim(AtomicLong outRef, long repoPtr, String shorthand);

    static native int jniEnsureLog(long repoPtr, String refname);

    static native int jniForeach(long repoPtr, ForeachCb consumer);

    static native int jniForeachGlob(long repoPtr, String glob, ForeachNameCb callback);

    static native int jniForeachName(long repoPtr, ForeachNameCb consumer);

    static native void jniFree(long refPtr);

    static native int jniHasLog(long repoPtr, String refname);

    static native int jniIsBranch(long refPtr);

    static native int jniIsNote(long refPtr);

    static native int jniIsRemote(long refPtr);

    static native int jniIsTag(long refPtr);

    static native int jniIsValidName(String refname);

    static native void jniIteratorFree(long iterPtr);

    static native int jniIteratorGlobNew(AtomicLong outIter, long repoPtr, String glob);

    static native int jniIteratorNew(AtomicLong outIter, long repoPtr);

    static native int jniList(List<String> strList, long repoPtr);

    static native int jniLookup(AtomicLong outRef, long repoPtr, String name);

    static native String jniName(long refPtr);

    static native int jniNameToId(Oid oid, long repoPtr, String name);

    static native int jniNext(AtomicLong outRef, long iterPtr);

    static native int jniNextName(AtomicReference<String> outName, long iterPtr);

    static native int jniNormalizeName(AtomicReference<String> outName, String name, int flags);

    static native long jniOwner(long refPtr);

    static native int jniPeel(AtomicLong outObj, long refPtr, int objType);

    static native int jniRemove(long repoPtr, String name);

    static native int jniRename(
            AtomicLong outRef, long refPtr, String newName, int force, String logMessage);

    static native int jniResolve(AtomicLong outRef, long refPtr);

    static native int jniSetTarget(AtomicLong outRef, long refPtr, Oid oid, String logMessage);

    static native String jniShorthand(long refPtr);

    static native int jniSymbolicCreate(
            AtomicLong outRef,
            long repoPtr,
            String name,
            String target,
            int force,
            String logMessage);

    static native int jniSymbolicCreateMatching(
            AtomicLong outRef,
            long repoPtr,
            String name,
            String target,
            int force,
            String currentValue,
            String logMessage);

    static native int jniSymbolicSetTarget(
            AtomicLong outRef, long refPtr, String target, String logMessage);

    static native String jniSymbolicTarget(long refPtr);

    /** const git_oid * git_reference_target(const git_reference *ref); */
    static native byte[] jniTarget(long refPtr);

    static native byte[] jniTargetPeel(long refPtr);

    static native int jniType(long refPtr);

    protected Reference(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    /**
     * Lookup a reference by name in a repository.
     *
     * <p>The name will be checked for validity. See `git_reference_symbolic_create()` for rules
     * about valid names.
     *
     * @param repo the repository to look up the reference
     * @param name the long name for the reference (e.g. HEAD, refs/heads/master, refs/tags/v0.1.0,
     *     ...)
     * @return found reference, if not found, return null.
     * @throws GitException GIT_EINVALIDSPEC or other relevant git errors.
     */
    @CheckForNull
    public static Reference lookup(@Nonnull Repository repo, @Nonnull String name) {
        AtomicLong outRef = new AtomicLong();
        int e = jniLookup(outRef, repo.getRawPointer(), name);
        if (ErrorCode.of(e) == ErrorCode.ENOTFOUND) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new Reference(false, outRef.get());
    }

    /**
     * Lookup a reference by name and resolve immediately to OID.
     *
     * <p>This function provides a quick way to resolve a reference name straight through to the
     * object id that it refers to. This avoids having to allocate or free any `git_reference`
     * objects for simple situations.
     *
     * <p>The name will be checked for validity. See `git_reference_symbolic_create()` for rules
     * about valid names.
     *
     * @param repo The repository in which to look up the reference
     * @param name The long name for the reference (e.g. HEAD, refs/heads/master, refs/tags/v0.1.0,
     *     ...)
     * @return found Oid
     * @throws GitException GIT_ENOTFOUND, GIT_EINVALIDSPEC or an error code.
     */
    @CheckForNull
    public static Oid nameToId(@Nonnull Repository repo, @Nonnull String name) {
        Oid oid = new Oid();
        int e = jniNameToId(oid, repo.getRawPointer(), name);
        if (ErrorCode.of(e) == ErrorCode.ENOTFOUND) {
            return null;
        }
        Error.throwIfNeeded(e);
        return oid;
    }

    /**
     * Do What I Mean (dwim). Lookup a reference by DWIMing its short name. For example {@code dwim}
     * can find reference given name like "feature/dev", but look only understands
     * "refs/heads/feature/dev"
     *
     * <p>Apply the git precedences rules to the given shorthand to determine which reference the
     * user is referring to.
     *
     * @param repo the repository in which to look
     * @param shorthand the short name for the reference
     * @return found reference
     * @throws GitException
     */
    @CheckForNull
    public static Reference dwim(@Nonnull Repository repo, @Nonnull String shorthand) {
        AtomicLong outRef = new AtomicLong();
        int e = jniDwim(outRef, repo.getRawPointer(), shorthand);
        if (e == ErrorCode.ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new Reference(false, outRef.get());
    }

    /**
     * Create a symbolic reference that points to another reference (target reference) and matches
     * the {@code currentValue} when updating.
     *
     * <p>For example, creating a soft ref pointing to "refs/heads/master": {@code
     * symbolicCreateMatching(repo, "HEAD", "refs/heads/master", false, null, null); }
     *
     * <p>For atomicity concern, enxure the target ref is not moved: {@code
     * symbolicCreateMatching(repo, "HEAD", "refs/heads/master", false, "refs/heads/master", null);
     * }
     *
     * <p>To view/find symbolic refs:
     * <li>{@code ls -l .git/}
     * <li>{@code git rev-parse <symbolic refname>}
     * <li>{@code git symbolic ref ...}
     *
     * @param repo Repository where the target reference and the symbolic reference live
     * @param name name of the symbolic reference
     * @param target name of the target reference
     * @param force Overwrite if reference already exist
     * @param currentValue The expected name of the reference when updating
     * @param logMessage The one line long message to be appended to the reflog
     * @return 0 on success
     * @throws GitException GIT_EEXISTS, GIT_EINVALIDSPEC, GIT_EMODIFIED or an error code
     */
    @Nonnull
    public static Reference symbolicCreateMatching(
            @Nonnull Repository repo,
            @Nonnull String name,
            @Nonnull String target,
            boolean force,
            String currentValue,
            String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(
                jniSymbolicCreateMatching(
                        outRef,
                        repo.getRawPointer(),
                        name,
                        target,
                        force ? 1 : 0,
                        currentValue,
                        logMessage));
        return new Reference(false, outRef.get());
    }

    /**
     * Create a new symbolic reference.
     *
     * @param repo Repository where that reference will live
     * @param name The name of the reference
     * @param target The target of the reference
     * @param force Overwrite existing references
     * @param logMessage The one line long message to be appended to the reflog
     * @return created reference
     * @throws GitException GIT_EEXISTS, GIT_EINVALIDSPEC or an error code
     */
    @Nonnull
    public static Reference symbolicCreate(
            @Nonnull Repository repo,
            @Nonnull String name,
            @Nonnull String target,
            boolean force,
            String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(
                jniSymbolicCreate(
                        outRef, repo.getRawPointer(), name, target, force ? 1 : 0, logMessage));
        return new Reference(false, outRef.get());
    }

    /**
     * Create a new direct reference that refers directly to an Oid.
     *
     * @param repo Repository where that reference will live
     * @param name The name of the reference
     * @param oid The object id pointed to by the reference.
     * @param force Overwrite existing references
     * @param logMessage The one line long message to be appended to the reflog
     * @return created reference.
     * @throws GitException GIT_EEXISTS, GIT_EINVALIDSPEC or an error code
     */
    @Nonnull
    public static Reference create(
            @Nonnull Repository repo,
            @Nonnull String name,
            @Nonnull Oid oid,
            boolean force,
            String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(
                jniCreate(outRef, repo.getRawPointer(), name, oid, force ? 1 : 0, logMessage));
        return new Reference(false, outRef.get());
    }

    /**
     * Conditionally create new direct reference.
     *
     * @param repo Repository where that reference will live
     * @param name The name of the reference
     * @param oid The object id pointed to by the reference.
     * @param force Overwrite existing references
     * @param currentId The expected value of the reference at the time of update
     * @param logMessage The one line long message to be appended to the reflog
     * @return created reference.
     * @throws GitException GIT_EMODIFIED if the value of the reference has changed, GIT_EEXISTS,
     *     GIT_EINVALIDSPEC or an error code
     */
    @Nonnull
    public static Reference createMatching(
            @Nonnull Repository repo,
            @Nonnull String name,
            @Nonnull Oid oid,
            boolean force,
            Oid currentId,
            String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(
                jniCreateMatching(
                        outRef,
                        repo.getRawPointer(),
                        name,
                        oid,
                        force ? 1 : 0,
                        currentId,
                        logMessage));
        return new Reference(false, outRef.get());
    }

    /**
     * Delete an existing reference.
     *
     * <p>This method works for both direct and symbolic references. The reference will be
     * immediately removed on disk but the memory will not be freed. Callers must call
     * `git_reference_free`.
     *
     * <p>This function will return an error if the reference has changed from the time it was
     * looked up.
     *
     * @throws GitException GIT_EMODIFIED or an error code
     */
    public static void delete(Reference ref) {
        if (ref != null && ref.getRawPointer() != 0) {
            Error.throwIfNeeded(jniDelete(ref.getRawPointer()));
        }
    }

    /**
     * Delete an existing reference by name
     *
     * <p>This method removes the named reference from the repository without looking at its old
     * value.
     *
     * @param repo repository where the reference lives
     * @param name The reference to remove
     * @throws GitException git error
     */
    public static void remove(Repository repo, String name) {
        Error.throwIfNeeded(jniRemove(repo.getRawPointer(), name));
    }

    /**
     * Fill a list with all the references that can be found in a repository.
     *
     * <p>The string array will be filled with the names of all references; these values are owned
     * by the user and should be free'd manually when no longer needed, using `git_strarray_free()`.
     *
     * @param repo Repository where to find the refs
     * @return list of reference names
     * @throws GitException git error
     */
    @Nonnull
    public static List<String> list(@Nonnull Repository repo) {
        List<String> strList = new ArrayList<>();
        Error.throwIfNeeded(jniList(strList, repo.getRawPointer()));
        return strList;
    }

    /**
     * Perform a callback on each reference in the repository.
     *
     * <p>The `callback` function will be called for each reference in the repository, receiving the
     * reference object and the `payload` value passed to this method. Returning a non-zero value
     * from the callback will terminate the iteration.
     *
     * <p>Note that the callback function is responsible to call `git_reference_free` on each
     * reference passed to it.
     *
     * @param repo Repository where to find the refs
     * @param callback Function which will be called for every listed ref, return non-zero to
     *     terminate the iteration.
     * @throws GitException git errors
     */
    public static void foreach(
            @Nonnull Repository repo, @Nonnull Function<Reference, Integer> callback) {
        ForeachCb cb = ptr -> callback.apply(new Reference(true, ptr));
        int e = jniForeach(repo.getRawPointer(), cb);
        Error.throwIfNeeded(e);
    }

    /**
     * Perform a callback on the fully-qualified name of each reference.
     *
     * <p>The `callback` function will be called for each reference in the repository, receiving the
     * name of the reference and the `payload` value passed to this method. Returning a non-zero
     * value from the callback will terminate the iteration.
     *
     * @param repo Repository where to find the refs
     * @param callback Function which will be called for every listed ref name
     * @throws GitException git errors
     */
    public static void foreachName(
            @Nonnull Repository repo, @Nonnull Function<String, Integer> callback) {
        Error.throwIfNeeded(jniForeachName(repo.getRawPointer(), callback::apply));
    }

    public static int cmp(@Nullable Reference ref1, @Nullable Reference ref2) {
        return jniCmp(
                ref1 == null ? 0 : ref1.getRawPointer(), ref2 == null ? 0 : ref2.getRawPointer());
    }

    /**
     * Create an iterator for the repo's references that match the specified glob
     *
     * @param repo the repository
     * @param glob the glob to match against the reference names
     * @return newly created iterator
     * @throws GitException git errors
     */
    @Nonnull
    public static Iterator iteratorGlobNew(@Nonnull Repository repo, @Nonnull String glob) {
        AtomicLong outIter = new AtomicLong();
        Error.throwIfNeeded(jniIteratorGlobNew(outIter, repo.getRawPointer(), glob));
        return new Iterator(false, outIter.get());
    }

    /**
     * Get the next reference
     *
     * @param iter the iterator
     * @return next reference, null GIT_ITEROVER if there are no more;
     * @throws GitException or an error code
     */
    @CheckForNull
    public static Reference next(@Nonnull Iterator iter) {
        AtomicLong outRef = new AtomicLong();
        int e = jniNext(outRef, iter.getRawPointer());
        if (e == ErrorCode.ITEROVER.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new Reference(true, outRef.get());
    }

    /**
     * Perform a callback on each reference in the repository whose name matches the given pattern.
     *
     * <p>This function acts like `git_reference_foreach()` with an additional pattern match being
     * applied to the reference name before issuing the callback function. See that function for
     * more information.
     *
     * <p>The pattern is matched using fnmatch or "glob" style where a '*' matches any sequence of
     * letters, a '?' matches any letter, and square brackets can be used to define character ranges
     * (such as "[0-9]" for digits).
     *
     * @param repo Repository where to find the refs
     * @param glob Pattern to match (fnmatch-style) against reference name.
     * @param callback Function which will be called for every listed ref
     * @throws GitException GIT_EUSER on non-zero callback, or error code
     */
    public static void foreachGlob(
            @Nonnull Repository repo, @Nonnull String glob, @Nonnull ForeachNameCb callback) {
        Error.throwIfNeeded(jniForeachGlob(repo.getRawPointer(), glob, callback));
    }

    /**
     * Check if a reflog exists for the specified reference. Same as cli: {@code git reflog exists
     * refs/heads/master}
     *
     * @param repo the repository
     * @param refname the reference's name
     * @return true if reflog exists
     * @throws GitException git errors
     */
    public static boolean hasLog(@Nonnull Repository repo, @Nonnull String refname) {
        int e = jniHasLog((repo.getRawPointer()), refname);
        if (e == 0 || e == 1) {
            return e == 1;
        }
        Error.throwIfNeeded(e);
        return false;
    }

    /**
     * Ensure there is a reflog for a particular reference.
     *
     * <p>Make sure that successive updates to the reference will append to its log.
     *
     * @param repo the repository
     * @param refname the reference's name
     * @throws GitException git error
     */
    public static void ensureLog(Repository repo, String refname) {
        Error.throwIfNeeded(jniEnsureLog(repo.getRawPointer(), refname));
    }

    /**
     * Normalize reference name and check validity. See also {@code git check-ref-format
     * [--normalize]}
     *
     * <p>This will normalize the reference name by removing any leading slash '/' characters and
     * collapsing runs of adjacent slashes between name components into a single slash.
     *
     * <p>Once normalized, if the reference name is valid, it will be returned in the user allocated
     * buffer.
     *
     * <p>See `git_reference_symbolic_create()` for rules about valid names.
     *
     * @param name Reference name to be checked.
     * @param flags Flags to constrain name validation rules - see the GIT_REFERENCE_FORMAT
     *     constants above.
     * @return normalized name
     * @throws GitException GIT_EBUFS if buffer is too small, GIT_EINVALIDSPEC or an error code.
     */
    public static String normalizeName(String name, EnumSet<Format> flags) {
        AtomicReference<String> outName = new AtomicReference<>();
        Error.throwIfNeeded(jniNormalizeName(outName, name, IBitEnum.bitOrAll(flags)));
        return outName.get();
    }

    /**
     * Ensure the reference name is well-formed.
     *
     * <p>Valid reference names must follow one of two patterns:
     *
     * <p>1. Top-level names must contain only capital letters and underscores, and must begin and
     * end with a letter. (e.g. "HEAD", "ORIG_HEAD"). 2. Names prefixed with "refs/" can be almost
     * anything. You must avoid the characters '~', '^', ':', '\\', '?', '[', and '*', and the
     * sequences ".." and "@{" which have special meaning to revparse.
     *
     * @param refname name to be checked.
     * @return 1 if the reference name is acceptable; 0 if it isn't
     * @throws GitException git error
     */
    public static boolean isValidName(String refname) {
        return jniIsValidName(refname) == 1;
    }

    /**
     * Create an iterator for the repo's references
     *
     * @param repo the repository
     * @return newly constructed iterator
     * @throws GitException git error
     */
    @Nonnull
    public static Iterator iteratorNew(@Nonnull Repository repo) {
        AtomicLong outIter = new AtomicLong();
        Error.throwIfNeeded(jniIteratorNew(outIter, repo.getRawPointer()));
        return new Iterator(false, outIter.get());
    }

    /**
     * Get the next reference's name
     *
     * <p>This function is provided for convenience in case only the names are interesting as it
     * avoids the allocation of the `git_reference` object which `git_reference_next()` needs.
     *
     * @return name of the next reference, null there are no more (GIT_ITEROVER).
     * @throws GitException git error
     */
    @CheckForNull
    public static String nextName(@Nonnull Iterator iterator) {
        AtomicReference<String> outName = new AtomicReference<>();
        int e = jniNextName(outName, iterator.getRawPointer());
        if (ErrorCode.ITEROVER.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return outName.get();
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    /**
     * Return the peeled OID target of this reference.
     *
     * <p>This peeled OID only applies to direct references that point to a hard Tag object: it is
     * the result of peeling such Tag.
     *
     * @return peeled Oid or null
     */
    @CheckForNull
    public Oid targetPeel() {
        return Oid.ofNullable(jniTargetPeel(this.getRawPointer()));
    }

    /**
     * Get full name to the reference pointed to by a symbolic reference.
     *
     * <p>Only available if the reference is symbolic.
     *
     * @return name of the reference or null
     */
    @CheckForNull
    public String symbolicTarget() {
        return jniSymbolicTarget(this.getRawPointer());
    }

    /**
     * Get the type of a reference.
     *
     * <p>Either direct (GIT_REFERENCE_DIRECT) or symbolic (GIT_REFERENCE_SYMBOLIC)
     *
     * @return the type
     */
    @Nonnull
    public ReferenceType type() {
        return ReferenceType.valueOf(jniType(this.getRawPointer()));
    }

    /**
     * Get the full name of a reference.
     *
     * <p>See `git_reference_symbolic_create()` for rules about valid names.
     *
     * @return the full name for the ref
     */
    @Nonnull
    public String name() {
        return jniName(this.getRawPointer());
    }

    /**
     * Resolve a symbolic reference to a direct reference.
     *
     * <p>This method iteratively peels a symbolic reference until it resolves to a direct reference
     * to an OID.
     *
     * <p>The peeled reference is returned in the `resolved_ref` argument, and must be freed
     * manually once it's no longer needed.
     *
     * <p>If a direct reference is passed as an argument, a copy of that reference is returned. This
     * copy must be manually freed too.
     *
     * @return resolved reference
     * @throws GitException resolve attempt failed
     */
    @CheckForNull
    public Reference resolve() {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniResolve(outRef, getRawPointer()));
        return outRef.get() != 0 ? new Reference(false, outRef.get()) : null;
    }

    /**
     * Get the repository where a reference resides.
     *
     * @return owner repository
     */
    @Nonnull
    public Repository owner() {
        return new Repository(jniOwner(getRawPointer()));
    }

    /**
     * Create a new reference with the same name as the given reference but a different symbolic
     * target. The reference must be a symbolic reference, otherwise this will fail.
     *
     * <p>The new reference will be written to disk, overwriting the given reference.
     *
     * <p>The target name will be checked for validity. See `git_reference_symbolic_create()` for
     * rules about valid names.
     *
     * <p>The message for the reflog will be ignored if the reference does not belong in the
     * standard set (HEAD, branches and remote-tracking branches) and and it does not have a reflog.
     *
     * @param target The new target for the reference
     * @param logMessage The one line long message to be appended to the reflog
     * @return newly created Reference
     * @throws GitException GIT_EINVALIDSPEC or an error code
     */
    @CheckForNull
    public Reference symbolicSetTarget(@Nonnull String target, @Nonnull String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniSymbolicSetTarget(outRef, getRawPointer(), target, logMessage));
        return outRef.get() != 0 ? new Reference(false, outRef.get()) : null;
    }

    /**
     * Conditionally create a new reference with the same name as the given reference but a
     * different OID target. The reference must be a direct reference, otherwise this will fail.
     *
     * <p>The new reference will be written to disk, overwriting the given reference.
     *
     * @param oid The new target OID for the reference
     * @param logMessage The one line long message to be appended to the reflog
     * @return newly created Reference
     * @throws GitException GIT_EMODIFIED if the value of the reference has changed since it was
     *     read, or an error code
     */
    @Nonnull
    public Reference setTarget(@Nonnull Oid oid, @Nonnull String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniSetTarget(outRef, getRawPointer(), oid, logMessage));
        return new Reference(false, outRef.get());
    }

    /**
     * Rename an existing reference.
     *
     * <p>This method works for both direct and symbolic references.
     *
     * <p>The new name will be checked for validity. See `git_reference_symbolic_create()` for rules
     * about valid names.
     *
     * <p>If the `force` flag is not enabled, and there's already a reference with the given name,
     * the renaming will fail.
     *
     * <p>IMPORTANT: The user needs to write a proper reflog entry if the reflog is enabled for the
     * repository. We only rename the reflog if it exists.
     *
     * @param newName The new name for the reference
     * @param force Overwrite an existing reference
     * @param logMessage The one line long message to be appended to the reflog
     * @return new reference
     * @throws GitException GIT_EINVALIDSPEC, GIT_EEXISTS or an error code
     */
    @Nonnull
    public Reference rename(@Nonnull String newName, boolean force, String logMessage) {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniRename(outRef, getRawPointer(), newName, force ? 1 : 0, logMessage));
        return new Reference(false, outRef.get());
    }

    /**
     * Create a copy of an existing reference.
     *
     * <p>Call `git_reference_free` to free the data.
     *
     * @return 0 or an error code
     */
    @Nonnull
    public Reference dup() {
        AtomicLong outRef = new AtomicLong();
        Error.throwIfNeeded(jniDup(outRef, this.getRawPointer()));
        return new Reference(false, outRef.get());
    }

    /**
     * Check if a reference is a local branch.
     *
     * @return true if the reference lives in the refs/heads namespace;
     */
    public boolean isBranch() {
        return jniIsBranch(getRawPointer()) == 1;
    }

    /**
     * Check if a reference is a remote tracking branch
     *
     * @return true when the reference lives in the refs/remotes namespace;
     */
    public boolean isRemote() {
        return jniIsRemote(getRawPointer()) == 1;
    }

    /**
     * Check if a reference is a tag
     *
     * @return 1 when the reference lives in the refs/tags namespace;
     */
    public boolean isTag() {
        return jniIsTag(getRawPointer()) == 1;
    }

    /**
     * Check if a reference is a note
     *
     * @return 1 when the reference lives in the refs/notes namespace;
     */
    public boolean isNote() {
        return jniIsNote(getRawPointer()) == 1;
    }

    /**
     * Recursively peel reference until object of the specified type is found.
     *
     * <p>The retrieved `peeled` object is owned by the repository and should be closed with the
     * `git_object_free` method.
     *
     * <p>If you pass `GIT_OBJECT_ANY` as the target type, then the object will be peeled until a
     * non-tag object is met.
     *
     * @param objType The type of the requested object (GIT_OBJECT_COMMIT, GIT_OBJECT_TAG,
     *     GIT_OBJECT_TREE, GIT_OBJECT_BLOB or GIT_OBJECT_ANY).
     * @return GitObject on success, null if not found
     * @throws GitException GIT_EAMBIGUOUS or an error code
     */
    public GitObject peel(GitObject.Type objType) {
        GitObject out = new GitObject(false, 0);
        int e = jniPeel(out._rawPtr, this._rawPtr.get(), objType.getBit());
        Error.throwIfNeeded(e);
        return out;
//        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Get the reference's short name
     *
     * <p>This will transform the reference name into a name "human-readable" version. If no
     * shortname is appropriate, it will return the full name.
     *
     * <p>The memory is owned by the reference and must not be freed.
     *
     * @return the human-readable version of the name
     */
    @Nonnull
    public String shorthand() {
        return jniShorthand(getRawPointer());
    }

    /**
     * Get the OID pointed to by a direct reference.
     *
     * <p>Only available if the reference is direct (i.e. an object id reference, not a symbolic
     * one).
     *
     * <p>To find the OID of a symbolic ref, call `git_reference_resolve()` and then this function
     * (or maybe use `git_reference_name_to_id()` to directly resolve a reference name all the way
     * through to an OID).
     *
     * @return a pointer to the oid if available, NULL otherwise
     */
    @CheckForNull
    public Oid target() {
        return Oid.ofNullable(jniTarget(getRawPointer()));
    }

    /** @return target of this reference. */
    @CheckForNull
    public Oid id() {
        return target();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Reference reference = (Reference) o;
        return Reference.cmp(reference, this) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_rawPtr);
    }

    public enum Format implements IBitEnum {
        /** No particular normalization. */
        NORMAL(0),
        /**
         * Control whether one-level refnames are accepted (i.e., refnames that do not contain
         * multiple /-separated components). Those are expected to be written only using uppercase
         * letters and underscore (FETCH_HEAD, ...)
         */
        ALLOW_ONELEVEL(1 << 0),
        /**
         * Interpret the provided name as a reference pattern for a refspec (as used with remote
         * repositories). If this option is enabled, the name is allowed to contain a single *
         * (<star>) in place of a one full pathname component (e.g., foo/<star>/bar but not
         * foo/bar<star>).
         */
        REFSPEC_PATTERN(1 << 1),
        /**
         * Interpret the name as part of a refspec in shorthand form so the `ONELEVEL` naming rules
         * aren't enforced and 'master' becomes a valid name.
         */
        REFSPEC_SHORTHAND(1 << 2),
        ;

        private final int _bit;

        Format(int bit) {
            this._bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public enum ReferenceType implements IBitEnum {
        INVALID(0),
        DIRECT(1),
        SYMBOLIC(2),
        ALL(3);

        private final int _bit;

        ReferenceType(int bit) {
            _bit = bit;
        }

        static ReferenceType valueOf(int iVal) {
            return IBitEnum.valueOf(iVal, ReferenceType.class, INVALID);
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    @FunctionalInterface
    public interface ForeachNameCb {
        int accept(String name);
    }

    @FunctionalInterface
    public interface ForeachCb {
        int accept(long refPtr);
    }

    public static class Iterator extends CAutoReleasable {
        protected Iterator(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniIteratorFree(cPtr);
        }
    }
}
