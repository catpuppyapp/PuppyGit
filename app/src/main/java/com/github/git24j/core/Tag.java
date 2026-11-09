package com.github.git24j.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Tag extends GitObject {
    static native int jniAnnotationCreate(
            Oid oid, long repoPtr, String tagName, long target, long tagger, String message);

    static native int jniCreate(
            Oid oid,
            long repoPtr,
            String tagName,
            long target,
            long tagger,
            String message,
            int force);

    static native int jniCreateFromBuffer(Oid oid, long repoPtr, String buffer, int force);

    static native int jniCreateLightWeight(
            Oid oid, long repoPtr, String tagName, long targetPtr, int force);

    static native int jniDelete(long repoPtr, String tagName);

    static native int jniForeach(long repoPtr, ForeachCb callback);

    static native int jniList(List<String> tagNames, long repoPtr);

    static native int jniListMatch(List<String> tagNames, String pattern, long repoPtr);

    static native String jniMessage(long tagPtr);

    static native String jniName(long tagPtr);

    static native int jniPeel(AtomicLong outTarget, long tagPtr);

    static native long jniTagger(long tagPtr);

    static native int jniTarget(AtomicLong outTargetPtr, long tagPtr);

    static native byte[] jniTargetId(long tagPtr);

    static native int jniTargetType(long tagPtr);

    Tag(boolean weak, long rawPointer) {
        super(weak, rawPointer);
    }

    public static Tag lookup(Repository repo, Oid oid) {
        return (Tag) GitObject.lookup(repo, oid, Type.TAG);
    }

    public static Tag lookupPrefix(Repository repo, String shortId) {
        return (Tag) GitObject.lookupPrefix(repo, shortId, Type.TAG);
    }

    /**
     * Create a new tag in the repository from an object
     *
     * <p>A new reference will also be created pointing to this tag object. If `force` is true and a
     * reference already exists with the given name, it'll be replaced.
     *
     * <p>The message will not be cleaned up. This can be achieved through `git_message_prettify()`.
     *
     * <p>The tag name will be checked for validity. You must avoid the characters '~', '^', ':',
     * '\\', '?', '[', and '*', and the sequences ".." and "@{" which have special meaning to
     * revparse.
     *
     * @param repo Repository where to store the tag
     * @param tagName Name for the tag; this name is validated for consistency. It should also not
     *     conflict with an already existing tag name
     * @param target Object to which this tag points. This object must belong to the given `repo`.
     * @param tagger Signature of the tagger for this tag, and of the tagging time
     * @param message Full message for this tag
     * @param force Overwrite existing references
     * @return OID of the * newly created tag. If the tag already exists, this parameter * will be
     *     the oid of the existing tag, and the function will * return a GIT_EEXISTS error code. 0
     *     on success, GIT_EINVALIDSPEC or an error code A tag object is written to the ODB, an
     * @throws GitException GIT_EEXISTS if tag already exists, GIT_EINVALIDSPEC or error writing to
     *     ODB
     */
    public static Oid create(
            @Nonnull Repository repo,
            @Nonnull String tagName,
            @Nonnull GitObject target,
            @Nonnull Signature tagger,
            @Nonnull String message,
            boolean force) {
        Oid outOid = new Oid();
        int e =
                jniCreate(
                        outOid,
                        repo.getRawPointer(),
                        tagName,
                        target.getRawPointer(),
                        tagger.getRawPointer(),
                        message,
                        force ? 1 : 0);
        Error.throwIfNeeded(e);
        return outOid;
    }

    /**
     * Create a new tag in the object database pointing to a git_object
     *
     * <p>The message will not be cleaned up. This can be achieved through `git_message_prettify()`.
     *
     * @param repo Repository where to store the tag
     * @param tagName Name for the tag
     * @param target Object to which this tag points. This object must belong to the given `repo`.
     * @param tagger Signature of the tagger for this tag, and of the tagging time
     * @param message Full message for this tag
     * @return oid to the newly created tag
     * @throws GitException 0 on success or an error code
     */
    @Nonnull
    public static Oid annotationCreate(
            @Nonnull Repository repo,
            @Nonnull String tagName,
            @Nonnull GitObject target,
            @Nonnull Signature tagger,
            @Nonnull String message) {
        Oid outOid = new Oid();
        int e =
                jniAnnotationCreate(
                        outOid,
                        repo.getRawPointer(),
                        tagName,
                        target.getRawPointer(),
                        tagger.getRawPointer(),
                        message);
        Error.throwIfNeeded(e);
        return outOid;
    }

    /**
     * Delete an existing tag reference.
     *
     * <p>The tag name will be checked for validity. See `git_tag_create()` for rules about valid
     * names.
     *
     * @param repo Repository where lives the tag
     * @param tagName Name of the tag to be deleted; this name is validated for consistency.
     * @throws GitException GIT_EINVALIDSPEC or other git errors
     */
    public static void delete(Repository repo, String tagName) {
        Error.throwIfNeeded(jniDelete(repo.getRawPointer(), tagName));
    }

    /**
     * Fill a list with all the tags in the Repository
     *
     * <p>The string array will be filled with the names of the matching tags; these values are
     * owned by the user and should be free'd manually when no longer needed, using
     * `git_strarray_free`.
     *
     * @param repo Repository where to find the tags
     * @return List of tag names
     * @throws GitException git errors
     */
    public static List<String> list(Repository repo) {
        List<String> tagNames = new ArrayList<>();
        Error.throwIfNeeded(jniList(tagNames, repo.getRawPointer()));
        return tagNames;
    }

    /**
     * Fill a list with all the tags in the Repository which name match a defined pattern
     *
     * <p>If an empty pattern is provided, all the tags will be returned.
     *
     * <p>The string array will be filled with the names of the matching tags; these values are
     * owned by the user and should be free'd manually when no longer needed, using
     * `git_strarray_free`.
     *
     * @param pattern Standard fnmatch pattern
     * @param repo Repository where to find the tags
     * @return a list of tag names that matches the given pattern
     */
    public static List<String> listMatch(String pattern, Repository repo) {
        List<String> tagNames = new ArrayList<>();
        Error.throwIfNeeded(jniListMatch(tagNames, pattern, repo.getRawPointer()));
        return tagNames;
    }

    /**
     * Call callback `cb' for each tag in the repository
     *
     * @param repo Repository
     * @param callback Callback function
     */
    public static void foreach(Repository repo, ForeachCb callback) {
        Error.throwIfNeeded(jniForeach(repo.getRawPointer(), callback));
    }

    /**
     * Create a new tag in the repository from a buffer. To see what the "buffer" should look like:
     * {@code git cat-file -p v0.1}
     *
     * @param repo Repository where to store the tag
     * @param buffer Raw tag data
     * @param force Overwrite existing tags
     * @return newly created tag
     * @throws GitException git error
     */
    public static Oid createFromBuffer(Repository repo, String buffer, boolean force) {
        Oid outOid = new Oid();
        Error.throwIfNeeded(
                jniCreateFromBuffer(outOid, repo.getRawPointer(), buffer, force ? 1 : 0));
        return outOid;
    }

    /**
     * Create a new lightweight tag pointing at a target object
     *
     * <p>A new direct reference will be created pointing to this target object. If `force` is true
     * and a reference already exists with the given name, it'll be replaced.
     *
     * <p>The tag name will be checked for validity. See `git_tag_create()` for rules about valid
     * names.
     *
     * @param repo Repository where to store the lightweight tag
     * @param tagName Name for the tag; this name is validated for consistency. It should also not
     *     conflict with an already existing tag name
     * @param target Object to which this tag points. This object must belong to the given `repo`.
     * @param force Overwrite existing references
     * @return newly created tag
     * @throws GitException e.g GIT_EINVALIDSPEC and GIT_EEXISTS
     */
    public static Oid createLightWeight(
            Repository repo, String tagName, GitObject target, boolean force) {
        Oid oid = new Oid();
        Error.throwIfNeeded(
                jniCreateLightWeight(
                        oid, repo.getRawPointer(), tagName, target.getRawPointer(), force ? 1 : 0));
        return oid;
    }

    @Override
    public Tag dup() {
        Tag tag = new Tag(false, 0);
        GitObject.jniDup(tag._rawPtr, getRawPointer());
        return tag;
    }

    /**
     *
     *
     * <pre>
     *     git rev-parse 'tag_name^{commit}'
     * </pre>
     *
     * @return Target object
     * @throws GitException git errors
     */
    public GitObject target() {
        GitObject out = new GitObject(true, 0);
        Error.throwIfNeeded(jniTarget(out._rawPtr, getRawPointer()));
        return out;
    }

    /**
     * Get the OID of the tagged object of a tag
     *
     * @return target OID
     */
    @CheckForNull
    public Oid targetId() {
        return Oid.ofNullable(jniTargetId(getRawPointer()));
    }

    /**
     * @return target type, this can be {@code Commit}, {@code Tag}, {@code Tree} or {@code Blob}
     */
    public Type targetType() {
        return Type.valueOf(jniTargetType(getRawPointer()));
    }

    /** @return Get tag name */
    public String name() {
        return jniName(getRawPointer());
    }

    /** @return Signature of the tagger */
    @Nullable
    public Signature tagger() {
        long ptr = jniTagger(getRawPointer());
        return ptr == 0 ? null : new Signature(true, ptr);
    }

    /**
     *
     *
     * <pre>
     *     git show tag $tag_name
     * </pre>
     *
     * @return message associated with the tag.
     */
    public String message() {
        return jniMessage(getRawPointer());
    }

    /**
     * Recursively peel a tag until a non tag git_object is found
     *
     * <p>The retrieved `tag_target` object is owned by the repository and should be closed with the
     * `git_object_free` method.
     *
     * @return peeled GitObject
     * @throws GitException git errors
     */
    public GitObject peel() {
        GitObject target = new GitObject(true, 0);
        Error.throwIfNeeded(jniPeel(target._rawPtr, getRawPointer()));
        return target;
    }

    /**
     * Callback used to iterate over tag names, return non-zero to terminate iteration.
     *
     * @see Tag::foreach
     */
    @FunctionalInterface
    public interface ForeachCb {
        int accept(String name, String oid);
    }
}
