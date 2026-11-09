package com.github.git24j.core;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Tree extends GitObject {

    /** int git_tree_builder_clear(git_tree_builder *bld); */
    static native int jniBuilderClear(long bld);

    /** size_t git_tree_builder_entrycount(git_tree_builder *bld); */
    static native int jniBuilderEntrycount(long bld);

    /**
     * int git_tree_builder_filter(git_tree_builder *bld, git_tree_builder_filter_cb filter, void
     * *payload);
     */
    static native void jniBuilderFilter(long bldPtr, Internals.JCallback callback);

    /** void git_tree_builder_free(git_tree_builder *bld); */
    static native void jniBuilderFree(long bld);

    /** const git_tree_entry * git_tree_builder_get(git_tree_builder *bld, const char *filename); */
    static native long jniBuilderGet(long bld, String filename);

    /**
     * int git_tree_builder_insert(const git_tree_entry **out, git_tree_builder *bld, const char
     * *filename, const git_oid *id, git_filemode_t filemode);
     */
    static native int jniBuilderInsert(
            AtomicLong out, long bld, String filename, Oid id, int filemode);

    /**
     * int git_tree_builder_new(git_tree_builder **out, git_repository *repo, const git_tree
     * *source);
     */
    static native int jniBuilderNew(AtomicLong out, long repoPtr, long source);

    /** int git_tree_builder_remove(git_tree_builder *bld, const char *filename); */
    static native int jniBuilderRemove(long bld, String filename);

    /** int git_tree_builder_write(git_oid *id, git_tree_builder *bld); */
    static native int jniBuilderWrite(Oid id, long bld);

    /**
     * int git_tree_builder_write_with_buffer(git_oid *oid, git_tree_builder *bld, git_buf *tree);
     */
    static native int jniBuilderWriteWithBuffer(Oid oid, long bld, Buf tree);

    /**
     * int git_tree_create_updated(git_oid *out, git_repository *repo, git_tree *baseline, size_t
     * nupdates, const git_tree_update *updates);
     */
    static native int jniCreateUpdated(Oid out, long repoPtr, long baseline, long[] updates);

    /** int git_tree_dup(git_tree **out, git_tree *source); */
    static native int jniDup(AtomicLong out, long source);

    /** const git_tree_entry * git_tree_entry_byid(const git_tree *tree, const git_oid *id); */
    static native long jniEntryByid(long tree, Oid id);

    /** const git_tree_entry * git_tree_entry_byindex(const git_tree *tree, size_t idx); */
    static native long jniEntryByindex(long tree, int idx);

    /** const git_tree_entry * git_tree_entry_byname(const git_tree *tree, const char *filename); */
    static native long jniEntryByname(long tree, String filename);

    /** int git_tree_entry_bypath(git_tree_entry **out, const git_tree *root, const char *path); */
    static native int jniEntryBypath(AtomicLong out, long root, String path);

    /** int git_tree_entry_cmp(const git_tree_entry *e1, const git_tree_entry *e2); */
    static native int jniEntryCmp(long e1, long e2);

    /** int git_tree_entry_dup(git_tree_entry **dest, const git_tree_entry *source); */
    static native int jniEntryDup(AtomicLong dest, long source);

    /** git_filemode_t git_tree_entry_filemode(const git_tree_entry *entry); */
    static native int jniEntryFilemode(long entry);

    /** git_filemode_t git_tree_entry_filemode_raw(const git_tree_entry *entry); */
    static native int jniEntryFilemodeRaw(long entry);

    /** void git_tree_entry_free(git_tree_entry *entry); */
    static native void jniEntryFree(long entry);

    /** const git_oid * git_tree_entry_id(const git_tree_entry *entry); */
    static native byte[] jniEntryId(long entry);

    /** const char * git_tree_entry_name(const git_tree_entry *entry); */
    static native String jniEntryName(long entry);

    /**
     * int git_tree_entry_to_object(git_object **object_out, git_repository *repo, const
     * git_tree_entry *entry);
     */
    static native int jniEntryToObject(AtomicLong objectOut, long repoPtr, long entry);

    /** git_object_t git_tree_entry_type(const git_tree_entry *entry); */
    static native int jniEntryType(long entry);

    /** size_t git_tree_entrycount(const git_tree *tree); */
    static native int jniEntrycount(long tree);

    static native void jniUpdateFree(long updatePtr);

    static native long jniUpdateNew(int updateType, Oid oid, int filemodeType, String path);

    /**
     * int git_tree_walk(const git_tree *tree, git_treewalk_mode mode, git_treewalk_cb callback,
     * void *payload);
     */
    static native int jniWalk(long treePtr, int mode, Internals.SJCallback callback);

    Tree(boolean weak, long rawPointer) {
        super(weak, rawPointer);
    }

    /**
     * Look up tree object given its oid
     *
     * @param repo repository to search
     * @param oid id of the tree object
     * @return found tree object
     * @throws GitException object not found or other git errors
     */
    @Nonnull
    public static Tree lookup(@Nonnull Repository repo, @Nonnull Oid oid) {
        return (Tree) GitObject.lookup(repo, oid, Type.TREE);
    }

    @Nonnull
    public static Tree lookupPrefix(@Nonnull Repository repo, @Nonnull String shortId) {
        return (Tree) GitObject.lookupPrefix(repo, shortId, Type.TREE);
    }

    /**
     * Create a new tree builder.
     *
     * <p>The tree builder can be used to create or modify trees in memory and write them as tree
     * objects to the database.
     *
     * <p>If the `source` parameter is not NULL, the tree builder will be initialized with the
     * entries of the given tree.
     *
     * <p>If the `source` parameter is NULL, the tree builder will start with no entries and will
     * have to be filled manually.
     *
     * @param repo Repository in which to store the object
     * @param source Source tree to initialize the builder (optional)
     * @return tree builder
     * @throws GitException git errors
     */
    @Nonnull
    public static Builder newBuilder(@Nonnull Repository repo, @Nullable Tree source) {
        Builder bld = new Builder(false, 0);
        int e =
                jniBuilderNew(
                        bld._rawPtr,
                        repo.getRawPointer(),
                        source == null ? 0 : source.getRawPointer());
        Error.throwIfNeeded(e);
        return bld;
    }

    public int walk(WalkMode mode, WalkCb cb) {
        return jniWalk(
                getRawPointer(), mode.getBit(), ((s, ptr) -> cb.accept(s, new Entry(true, ptr))));
    }

    /** @return Get the number of entries listed in a tree */
    public int entryCount() {
        return jniEntrycount(getRawPointer());
    }

    /**
     * Lookup a tree entry by its filename
     *
     * <p>This returns a git_tree_entry that is owned by the git_tree. You don't have to free it,
     * but you must not use it after the git_tree is released.
     *
     * @param filename the filename of the desired entry
     * @return the tree entry; empty if not found
     */
    @Nullable
    public Entry entryByName(@Nonnull String filename) {
        long ptr = jniEntryByname(getRawPointer(), filename);
        return ptr == 0 ? null : new Entry(true, ptr);
    }

    /**
     * Lookup a tree entry by its position in the tree
     *
     * <p>This returns a git_tree_entry that is owned by the git_tree. You don't have to free it,
     * but you must not use it after the git_tree is released.
     *
     * @param idx the position in the entry list
     * @return the tree entry; empty if not found
     */
    @Nullable
    public Entry entryByIndex(int idx) {
        long ptr = jniEntryByindex(getRawPointer(), idx);
        return ptr == 0 ? null : new Entry(true, ptr);
    }

    /**
     * Lookup a tree entry by SHA value.
     *
     * <p>This returns a git_tree_entry that is owned by the git_tree. You don't have to free it,
     * but you must not use it after the git_tree is released.
     *
     * <p>Warning: this must examine every entry in the tree, so it is not fast.
     *
     * @param id the sha being looked for
     * @return the tree entry; empty if not found
     */
    @Nullable
    public Entry entryById(@Nonnull Oid id) {
        long ptr = jniEntryByid(getRawPointer(), id);
        return ptr == 0 ? null : new Entry(true, ptr);
    }

    /**
     * Retrieve a tree entry contained in a tree or in any of its subtrees, given its relative path.
     *
     * @param path Path to the contained entry
     * @return found tree entry; empty if not found
     * @throws GitException 0 on success; GIT_ENOTFOUND if the path does not exist
     */
    @Nullable
    public Entry entryByPath(@Nonnull String path) {
        Entry out = new Entry(false, 0);
        int e = jniEntryBypath(out._rawPtr, getRawPointer(), path);
        if (e == GitException.ErrorCode.ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return out;
    }

    @Override
    public Tree dup() {
        return (Tree) super.dup();
    }

    /**
     * Create a tree based on another one with the specified modifications
     *
     * <p>Given the `baseline` perform the changes described in the list of `updates` and create a
     * new tree.
     *
     * <p>This function is optimized for common file/directory addition, removal and replacement in
     * trees. It is much more efficient than reading the tree into a `git_index` and modifying that,
     * but in exchange it is not as flexible.
     *
     * <p>Deleting and adding the same entry is undefined behaviour, changing a tree to a blob or
     * viceversa is not supported.
     *
     * @param repo the repository in which to create the tree, must be the same as for `baseline`
     * @param baseline the tree to base these changes on
     * @param updates the list of updates to perform
     * @return id of the new tree
     */
    @Nonnull
    public Oid createUpdated(
            @Nonnull Repository repo, @Nonnull Tree baseline, @Nonnull List<Update> updates) {
        Oid outOid = new Oid();
        int e =
                jniCreateUpdated(
                        outOid,
                        repo.getRawPointer(),
                        baseline.getRawPointer(),
                        updates.stream().mapToLong(Tree.Update::getRawPointer).toArray());
        Error.throwIfNeeded(e);
        return outOid;
    }

    public enum UpdateT {
        /** Update or insert an entry at the specified path */
        UPSERT,
        /** Remove an entry from the specified path */
        REMOVE;
    }

    public enum WalkMode implements IBitEnum {
        PRE(0),
        POST(1);
        private final int _bit;

        WalkMode(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    /** Callback for Tree.walk */
    public interface WalkCb {
        int accept(String root, Entry entry);
    }

    /** Representation of each one of the entries in a tree object. */
    public static class Entry extends CAutoReleasable {
        protected Entry(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniEntryFree(cPtr);
        }

        /**
         * Duplicate a tree entry
         *
         * @return duplicated entry
         * @throws GitException git errors
         */
        @Nonnull
        public Entry dup() {
            Entry out = new Entry(false, 0);
            Error.throwIfNeeded(jniEntryDup(out._rawPtr, this.getRawPointer()));
            return out;
        }

        /** @return the name of the file */
        @CheckForNull
        public String name() {
            return jniEntryName(this.getRawPointer());
        }

        /** the id of the object pointed by the entry */
        @Nonnull
        public Oid id() {
            return Oid.of(jniEntryId(this.getRawPointer()));
        }

        /** @return type of the object the entry is pointing to */
        @Nonnull
        public GitObject.Type type() {
            return GitObject.Type.valueOf(jniEntryType(this.getRawPointer()));
        }

        /** @return the type of the object pointed by the entry */
        @Nonnull
        public FileMode filemode() {
            return IBitEnum.valueOf(
                    jniEntryFilemode(this.getRawPointer()), FileMode.class, FileMode.UNREADABLE);
        }

        /**
         * Get the raw UNIX file attributes of a tree entry
         *
         * <p>This function does not perform any normalization and is only useful if you need to be
         * able to recreate the original tree object.
         *
         * @return filemode as an integer
         */
        @Nonnull
        public FileMode filemodeRaw() {
            return IBitEnum.valueOf(
                    jniEntryFilemodeRaw(this.getRawPointer()), FileMode.class, FileMode.UNREADABLE);
        }

        /**
         * Compare this entry to {@code that}
         *
         * @param that entry to compare
         * @return < 0 if this is before that, 0 if equals, >0 if this is after that
         */
        public int cmp(@Nonnull Entry that) {
            return jniEntryCmp(getRawPointer(), that.getRawPointer());
        }

        /**
         * Convert a tree entry to the git_object it points to.
         *
         * @param repo repository where to lookup the pointed object
         * @return converted object
         * @throws GitException git errors
         */
        @Nonnull
        public GitObject toObject(@Nonnull Repository repo) {
            GitObject out = new GitObject(false, 0);
            int e = jniEntryToObject(out._rawPtr, repo.getRawPointer(), this.getRawPointer());
            Error.throwIfNeeded(e);
            return out;
        }
    }

    public static class Update extends CAutoReleasable {
        protected Update(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        /** Create an Update object. In case of removal, only Path will looked at */
        @Nonnull
        public static Update create(
                @Nonnull UpdateT updateType,
                @Nullable Oid oid,
                @Nonnull FileMode fileModeType,
                @Nonnull String path) {
            long ptr = jniUpdateNew(updateType.ordinal(), oid, fileModeType.getBit(), path);
            return new Update(false, ptr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniUpdateFree(cPtr);
        }
    }

    public static class Builder extends CAutoReleasable {

        protected Builder(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniBuilderFree(cPtr);
        }

        /**
         * Selectively remove entries in the tree
         *
         * <p>The `filter` callback will be called for each entry in the tree with a pointer to the
         * entry and the provided `payload`; if the callback returns non-zero, the entry will be
         * filtered (removed from the builder).
         *
         * @param builder Tree builder
         * @param callback Callback to filter entries
         */
        public void filter(Builder builder, FilterCb callback) {
            jniBuilderFilter(builder.getRawPointer(), ptr -> callback.accept(new Entry(true, ptr)));
        }

        /**
         * Clear all the entires in the builder
         *
         * @throws GitException git error if clear failed
         */
        public void clear() {
            Error.throwIfNeeded(jniBuilderClear(this.getRawPointer()));
        }

        /** @return the number of entries in the treebuilder */
        public int entryCount() {
            return jniBuilderEntrycount(getRawPointer());
        }

        /**
         * Get an entry from the builder from its filename
         *
         * @param filename Name of the entry
         * @return entry if found
         */
        @Nullable
        public Entry get(@Nonnull String filename) {
            long ptr = jniBuilderGet(this.getRawPointer(), filename);
            if (ptr == 0) {
                return null;
            }
            return new Entry(true, ptr);
        }

        /**
         * Add or update an entry to the builder
         *
         * <p>Insert a new entry for `filename` in the builder with the given attributes.
         *
         * <p>If an entry named `filename` already exists, its attributes will be updated with the
         * given ones.
         *
         * <p>The optional pointer `out` can be used to retrieve a pointer to the newly
         * created/updated entry. Pass NULL if you do not need it. The pointer may not be valid past
         * the next operation in this builder. Duplicate the entry if you want to keep it.
         *
         * <p>By default the entry that you are inserting will be checked for validity; that it
         * exists in the object database and is of the correct type. If you do not want this
         * behavior, set the `GIT_OPT_ENABLE_STRICT_OBJECT_CREATION` library option to false.
         *
         * @param filename Filename of the entry
         * @param oid oid of the entry
         * @param filemode Folder attributes of the entry.
         * @return added entry
         * @throws GitException git errors
         */
        @Nonnull
        public Entry insert(
                @Nonnull String filename, @Nonnull Oid oid, @Nonnull FileMode filemode) {
            Entry out = new Entry(true, 0);
            Error.throwIfNeeded(
                    jniBuilderInsert(
                            out._rawPtr, this.getRawPointer(), filename, oid, filemode.getBit()));
            return out;
        }

        /**
         * Remove an entry from the builder by its filename
         *
         * @param filename Filename of the entry to remove
         */
        public void remove(@Nonnull String filename) {
            Error.throwIfNeeded(jniBuilderRemove(this.getRawPointer(), filename));
        }

        /**
         * Write the contents of the tree builder as a tree object
         *
         * <p>The tree builder will be written to the given `repo`, and its identifying SHA1 hash
         * will be stored in the `id` pointer.
         *
         * @return OID of the newly written tree
         * @throws GitException git errors
         */
        @Nonnull
        public Oid write() {
            Oid out = new Oid();
            Error.throwIfNeeded(jniBuilderWrite(out, getRawPointer()));
            return out;
        }

        /**
         * Write the contents of the tree builder as a tree object using a shared git_buf.
         *
         * @param buf Shared buffer for writing the tree. Will be grown as necessary.
         * @return OID of the newly written tree
         * @see {Tree.write}
         */
        public Oid writeWithBuf(@Nullable Buf buf) {
            if (buf == null) {
                buf = new Buf();
            }
            Oid out = new Oid();
            Error.throwIfNeeded(jniBuilderWriteWithBuffer(out, this.getRawPointer(), buf));
            return out;
        }

        @FunctionalInterface
        public interface FilterCb {
            int accept(Entry entry);
        }
    }
}
