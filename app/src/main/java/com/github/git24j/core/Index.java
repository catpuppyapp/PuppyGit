package com.github.git24j.core;

import static com.github.git24j.core.GitException.ErrorCode.ITEROVER;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public class Index extends CAutoReleasable {
    static native int jniAdd(long idxPtr, long entryPtr);

    static native int jniAddAll(long idxPtr, String[] pathSpec, int flags, Callback callback);

    static native int jniAddByPath(long idxPtr, String path);

    static native int jniAddFromBuffer(long indexPtr, long entryPtr, byte[] buffer);

    static native int jniCaps(long idxPtr);

    static native byte[] jniChecksum(long indexPtr);

    static native int jniClear(long indexPtr);

    static native int jniConflictAdd(
            long indexPtr, long ancestorEntryPtr, long ourEntryPtr, long theirEntryPtr);

    static native int jniConflictCleanup(long indexPtr);

    static native int jniConflictGet(
            AtomicLong ancestorOut,
            AtomicLong ourOut,
            AtomicLong theirOut,
            long indexPtr,
            String path);

    /** void git_index_conflict_iterator_free(git_index_conflict_iterator *iterator); */
    static native void jniConflictIteratorFree(long iterPtr);

    /**
     * int git_index_conflict_iterator_new(git_index_conflict_iterator **iterator_out, git_index
     * *index);
     */
    static native int jniConflictIteratorNew(AtomicLong outIter, long indexPtr);

    /**
     * int git_index_conflict_next(const git_index_entry **ancestor_out, const git_index_entry
     * **our_out, const git_index_entry **their_out, git_index_conflict_iterator *iterator);
     */
    static native int jniConflictNext(
            AtomicLong ancestorOut, AtomicLong ourOut, AtomicLong theirOut, long iterPtr);

    static native int jniConflictRemove(long indexPtr, String path);

    static native int jniEntryCount(long indexPtr);

    static native void jniEntryFree(long entryPtr);

    /** int ctime_nanoseconds */
    static native long jniEntryGetCtimeNanoseconds(long entryPtr);

    /** int ctime_seconds */
    static native long jniEntryGetCtimeSeconds(long entryPtr);

    /** uint32_t dev */
    static native int jniEntryGetDev(long entryPtr);

    /** uint32_t file_size */
    static native int jniEntryGetFileSize(long entryPtr);

    /** uint16_t flags */
    static native int jniEntryGetFlags(long entryPtr);

    /** uint16_t flags_extended */
    static native int jniEntryGetFlagsExtended(long entryPtr);

    /** uint32_t gid */
    static native int jniEntryGetGid(long entryPtr);

    /** git_oid id */
    static native byte[] jniEntryGetId(long entryPtr);

    /** uint32_t ino */
    static native int jniEntryGetIno(long entryPtr);
    /** uint32_t mode */
    static native int jniEntryGetMode(long entryPtr);

    /** int mtime_nanoseconds */
    static native long jniEntryGetMtimeNanoseconds(long entryPtr);

    /** int mtime_seconds */
    static native long jniEntryGetMtimeSeconds(long entryPtr);

    /** const char *path */
    static native String jniEntryGetPath(long entryPtr);

    /** uint32_t uid */
    static native int jniEntryGetUid(long entryPtr);

    static native int jniEntryIsConflict(long entryPtr);

    static native long jniEntryNew();

    /** int ctime_nanoseconds */
    static native void jniEntrySetCtimeNanoseconds(long entryPtr, long ctimeNanoseconds);

    /** int ctime_seconds */
    static native void jniEntrySetCtimeSeconds(long entryPtr, long ctimeSeconds);

    /** uint32_t dev */
    static native void jniEntrySetDev(long entryPtr, int dev);

    /** uint32_t file_size */
    static native void jniEntrySetFileSize(long entryPtr, int fileSize);

    /** uint16_t flags */
    static native void jniEntrySetFlags(long entryPtr, int flags);

    /** uint16_t flags_extended */
    static native void jniEntrySetFlagsExtended(long entryPtr, int flagsExtended);

    /** uint32_t gid */
    static native void jniEntrySetGid(long entryPtr, int gid);

    /** git_oid id */
    static native void jniEntrySetId(long entryPtr, Oid id);

    /** uint32_t ino */
    static native void jniEntrySetIno(long entryPtr, int ino);

    /** uint32_t mode */
    static native void jniEntrySetMode(long entryPtr, int mode);

    /** int mtime_nanoseconds */
    static native void jniEntrySetMtimeNanoseconds(long entryPtr, long mtimeNanoseconds);

    /** int mtime_seconds */
    static native void jniEntrySetMtimeSeconds(long entryPtr, long mtimeSeconds);

    /** const char *path */
    static native void jniEntrySetPath(long entryPtr, String path);

    /** uint32_t uid */
    static native void jniEntrySetUid(long entryPtr, int uid);

    static native int jniEntryStage(long entryPtr);

    static native int jniFind(AtomicInteger outPos, long indexPtr, String path);

    static native int jniFindPrefix(AtomicInteger outPos, long indexPtr, String prefix);

    static native void jniFree(long idxPtr);

    static native long jniGetByIndex(long indexPtr, int n);

    static native long jniGetByPath(long indexPtr, String path, int stage);

    static native int jniHasConflicts(long indexPtr);

    static native void jniIteratorFree(long iterPtr);

    static native int jniIteratorNew(AtomicLong outIterPtr, long indexPtr);

    static native int jniIteratorNext(AtomicLong outEntryPtr, long iterPtr);

    static native int jniOpen(AtomicLong outIndexPtr, String indexPath);

    static native long jniOwner(long idxPtr);

    static native String jniPath(long indexPtr);

    static native int jniRead(long indexPtr, int force);

    static native int jniReadTree(long indexPtr, long treePtr);

    static native int jniRemove(long indexPtr, String path, int stage);

    /** int git_index_remove_bypath(git_index *index, const char *path); */
    static native int jniRemoveByPath(long indexPtr, String path);

    static native int jniRemoveDirectory(long indexPtr, String dir, int stage);

    static native int jniSetCaps(long idxPtr, int caps);

    static native int jniSetVersion(long indexPtr, int version);

    static native int jniUpdateAll(long idxPtr, String[] pathSpec, Callback callback);

    static native int jniVersion(long indexPtr);

    static native int jniWrite(long idxPtr);

    static native int jniWriteTree(Oid outOid, long indexPtr);

    static native int jniWriteTreeTo(Oid outOid, long indexPtr, long repoPtr);

    protected Index(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    /**
     * Create a new bare Git index object as a memory representation of the Git index file in
     * 'indexPath', without a repository to back it.
     *
     * <p>Since there is no ODB or working directory behind this index, any Index methods which rely
     * on these (e.g. index_add_bypath) will fail with the GIT_ERROR error code.
     *
     * <p>If you need to access the index of an actual repository, use the `git_repository_index`
     * wrapper.
     *
     * <p>The index must be freed once it's no longer in use.
     *
     * @param indexPath the path to the index file in disk
     * @return the pointer for the new index
     * @throws GitException git errors
     */
    public static Index open(String indexPath) {
        Index outIdx = new Index(false, 0);
        Error.throwIfNeeded(jniOpen(outIdx._rawPtr, indexPath));
        return outIdx;
    }

    /**
     * Get the repository this index relates to
     *
     * @return the repository that owns the index
     */
    public Repository owner() {
        long ptr = jniOwner(getRawPointer());
        if (ptr == 0) {
            throw new GitException(GitException.ErrorClass.INDEX, "Index owner not found");
        }
        return new Repository(ptr);
    }

    /**
     * Read index capabilities flags.
     *
     * @return A combination of Capability enums
     */
    public EnumSet<Capability> caps() {
        int c = jniCaps(getRawPointer());
        if (c < 0) {
            return EnumSet.of(Capability.FROM_OWNER);
        }
        return IBitEnum.parse(c, Capability.class);
    }

    /**
     * Set index capabilities flags.
     *
     * <p>If you pass `GIT_INDEX_CAPABILITY_FROM_OWNER` for the caps, then capabilities will be read
     * from the config of the owner object, looking at `core.ignorecase`, `core.filemode`,
     * `core.symlinks`.
     *
     * @param caps A combination of Index.Capability values
     * @throws GitException git errors
     */
    public void setCaps(EnumSet<Capability> caps) {
        if (caps.contains(Capability.FROM_OWNER)) {
            Error.throwIfNeeded(jniSetCaps(getRawPointer(), Capability.FROM_OWNER.getBit()));
            return;
        }
        Error.throwIfNeeded(jniSetCaps(getRawPointer(), IBitEnum.bitOrAll(caps)));
    }

    /**
     * Get index on-disk version.
     *
     * <p>Valid return values are 2, 3, or 4. If 3 is returned, an index with version 2 may be
     * written instead, if the extension data in version 3 is not necessary.
     *
     * @return the index version
     */
    public int version() {
        return jniVersion(getRawPointer());
    }

    /**
     * Set index on-disk version.
     *
     * <p>Valid values are 2, 3, or 4. If 2 is given, git_index_write may write an index with
     * version 3 instead, if necessary to accurately represent the index.
     *
     * @param version The new version number
     * @throws GitException git errors
     */
    public void setVersion(int version) {
        Error.throwIfNeeded(jniSetVersion(getRawPointer(), version));
    }

    /**
     * Update the contents of an existing index object in memory by reading from the hard disk.
     *
     * <p>If `force` is true, this performs a "hard" read that discards in-memory changes and always
     * reloads the on-disk index data. If there is no on-disk version, the index will be cleared.
     *
     * <p>If `force` is false, this does a "soft" read that reloads the index data from disk only if
     * it has changed since the last time it was loaded. Purely in-memory index data will be
     * untouched. Be aware: if there are changes on disk, unwritten in-memory changes are discarded.
     *
     * @param force if true, always reload, vs. only read if file has changed
     * @throws GitException git errors
     */
    public void read(boolean force) {
        Error.throwIfNeeded(jniRead(getRawPointer(), force ? 0 : 1));
    }

    /**
     * Write an existing index object from memory back to disk using an atomic file lock.
     *
     * @throws GitException git error.
     */
    public void write() {
        Error.throwIfNeeded(jniWrite(getRawPointer()));
    }

    public String path() {
        return jniPath(getRawPointer());
    }

    /**
     * Get the checksum of the index
     *
     * <p>This checksum is the SHA-1 hash over the index file (except the last 20 bytes which are
     * the checksum itself). In cases where the index does not exist on-disk, it will be zeroed out.
     *
     * @return the checksum of the index
     */
    @Nonnull
    public Oid checksum() {
        byte[] bytes = jniChecksum(getRawPointer());
        if (bytes == null) {
            throw new GitException(
                    GitException.ErrorClass.INDEX,
                    "git_index_checksum returned NULL unexpectedly.");
        }
        return Oid.of(bytes);
    }

    /**
     * Read a tree into the index file with stats, roughly equals to:
     *
     * <pre>
     *     git read-tree [<tree-ish>]
     * </pre>
     *
     * The current index contents will be replaced by the specified tree.
     *
     * @param tree tree to read
     * @throws GitException git errors
     */
    public void readTree(Tree tree) {
        Error.throwIfNeeded(jniReadTree(getRawPointer(), tree.getRawPointer()));
    }

    /**
     * Write the index as a tree, roughly equals to:
     *
     * <pre>
     *     git write-tree
     * </pre>
     *
     * <p>This method will scan the index and write a representation of its current state back to
     * disk; it recursively creates tree objects for each of the subtrees stored in the index, but
     * only returns the OID of the root tree. This is the OID that can be used e.g. to create a
     * commit.
     *
     * <p>The index instance cannot be bare, and needs to be associated to an existing repository.
     *
     * <p>The index must not contain any file in conflict.
     *
     * @return Oid of the tree
     * @throws GitException git errors, for example GIT_EUNMERGED when the index is not clean or an
     *     error code
     */
    public Oid writeTree() {
        Oid outOid = new Oid();
        Error.throwIfNeeded(jniWriteTree(outOid, getRawPointer()));
        return outOid;
    }

    /**
     * Write the index as a tree to the given repository
     *
     * <p>This method will do the same as `git_index_write_tree`, but letting the user choose the
     * repository where the tree will be written.
     *
     * <p>The index must not contain any file in conflict.
     *
     * @param repo Repository where to write the tree
     * @return oid of the tree written
     * @throws GitException git error like GIT_EUNMERGED when the index is not clean
     */
    public Oid writeTreeTo(Repository repo) {
        Oid outOid = new Oid();
        Error.throwIfNeeded(jniWriteTreeTo(outOid, getRawPointer(), repo.getRawPointer()));
        return outOid;
    }

    /**
     * Get the count of entries currently in the index, roughly equals to:
     *
     * <pre>
     *     $ git ls-files | wc -l
     * </pre>
     *
     * @return integer of count of current entries
     */
    public int entryCount() {
        return jniEntryCount(getRawPointer());
    }

    /**
     * Clear the contents (all the entries) of an index object.
     *
     * <p>This clears the index object in memory; changes must be explicitly written to disk for
     * them to take effect persistently.
     *
     * @throws GitException git errors
     */
    public void clear() {
        Error.throwIfNeeded(jniClear(getRawPointer()));
    }

    public Entry getEntryByIndex(int n) {
        return Entry.getByIndex(this, n);
    }

    @CheckForNull
    public Entry getEntryByPath(@Nonnull String path, @Nonnull Stage stage) {
        return Entry.getByPath(this, path, stage);
    }

    /**
     * Remove an entry from the index
     *
     * @param path path to search
     * @param stage stage to search
     * @throws GitException git errors
     */
    public void remove(String path, int stage) {
        Error.throwIfNeeded(jniRemove(getRawPointer(), path, stage));
    }

    /**
     * Remove all entries from the index under a given directory
     *
     * @param dir container directory path
     * @param stage stage to search
     * @throws GitException git errors
     */
    public void removeDirectory(String dir, int stage) {
        Error.throwIfNeeded(jniRemoveDirectory(getRawPointer(), dir, stage));
    }

    /**
     * Add or update an index entry from an in-memory struct.
     *
     * @param sourceEntry new entry object
     * @throws GitException git error
     */
    public void add(Entry sourceEntry) {
        if (sourceEntry == null) {
            return;
        }
        Error.throwIfNeeded(jniAdd(getRawPointer(), sourceEntry.getRawPointer()));
    }

    /**
     * Add or update an index entry from a buffer in memory
     *
     * <p>This method will create a blob in the repository that owns the index and then add the
     * index entry to the index. The `path` of the entry represents the position of the blob
     * relative to the repository's root folder.
     *
     * <p>If a previous index entry exists that has the same path as the given 'entry', it will be
     * replaced. Otherwise, the 'entry' will be added. The `id` and the `file_size` of the 'entry'
     * are updated with the real value of the blob.
     *
     * <p>This forces the file to be added to the index, not looking at gitignore rules. Those rules
     * can be evaluated through the git_status APIs (in status.h) before calling this.
     *
     * <p>If this file currently is the result of a merge conflict, this file will no longer be
     * marked as conflicting. The data about the conflict will be moved to the "resolve undo" (REUC)
     * section.
     *
     * @param entry filename to add
     * @param buffer data to be written into the blob
     * @throws GitException git errors
     */
    public void addFromBuffer(Entry entry, byte[] buffer) {
        Error.throwIfNeeded(jniAddFromBuffer(getRawPointer(), entry.getRawPointer(), buffer));
    }

    /**
     * Remove an index entry corresponding to a file on disk
     *
     * <p>The file `path` must be relative to the repository's working folder. It may exist.
     *
     * <p>If this file currently is the result of a merge conflict, this file will no longer be
     * marked as conflicting. The data about the conflict will be moved to the "resolve undo" (REUC)
     * section.
     *
     * @param path filename to remove
     * @throws GitException git erros
     */
    public void removeByPath(String path) {
        Error.throwIfNeeded(jniRemoveByPath(getRawPointer(), path));
    }

    /**
     * Add or update an index entry from a file on disk
     *
     * @param path filename to add
     * @throws GitException git error
     */
    public void add(String path) {
        Error.throwIfNeeded(jniAddByPath(getRawPointer(), path));
    }

    /**
     * Add or update index entries matching files in the working directory.
     *
     * @param pathSpec array of path patterns
     * @param flags combination of git_index_add_option_t flags
     * @param callback notification callback for each added/updated path (also gets index of
     *     matching pathspec entry); can be NULL; return 0 to add, >0 to skip, < 0 to abort scan.
     * @throws GitException git error
     */
    public void addAll(
            String[] pathSpec, EnumSet<AddOption> flags, BiConsumer<String, String> callback) {
        Error.throwIfNeeded(
                jniAddAll(getRawPointer(), pathSpec, IBitEnum.bitOrAll(flags), callback::accept));
    }

    /**
     * Update all index entries, which have already been included and match the {@code pathSpec} pattern),
     * to line up with the working directory.
     *
     * <pre>
     *  # show which files are included in existing index,
     *  $ git ls-files --cached
     *  # make a trivial change to one of the file, say "README.md"
     *  $ echo "" >> README.md
     *  # update the index: `updateAll(List.of("README.md", (path, pathSpec)-> {})`
     *  $ git update-index README.md
     * <pre/>
     *
     * @param pathSpec array of path patterns
     * @param callback notification callback for each updated path (also gets index of matching
     *     pathspec entry); can be NULL; return 0 to add, >0 to skip, < 0 to abort scan.
     * @throws GitException git error.
     */
    public void updateAll(List<String> pathSpec, Callback callback) {
        Error.throwIfNeeded(
                jniUpdateAll(getRawPointer(), pathSpec.toArray(new String[0]), callback));
    }

    /**
     * Find the first position of any entries which point to given path in the Git index.
     *
     * @param path path to search
     * @return a zero-based position in the index if found; GIT_ENOTFOUND otherwise
     */
    public int find(String path) {
        AtomicInteger outPos = new AtomicInteger();
        int e = jniFind(outPos, getRawPointer(), path);
        Error.throwIfNeeded(e);
        return outPos.get();
    }

    /**
     * Find the first position of any entries matching a prefix. To find the first position of a
     * path inside a given folder, suffix the prefix with a '/'.
     *
     * @param prefix the prefix to search for
     * @return 0 with valid value in at_pos;
     * @throws GitException git errors like GIT_ENOTFOUND
     */
    public int findPrefix(String prefix) {
        AtomicInteger outPos = new AtomicInteger();
        int e = jniFindPrefix(outPos, getRawPointer(), prefix);
        Error.throwIfNeeded(e);
        return outPos.get();
    }

    /**
     * Add or update index entries to represent a conflict. Any staged entries that exist at the
     * given paths will be removed.
     *
     * <p>The entries are the entries from the tree included in the merge. Any entry may be null to
     * indicate that that file was not present in the trees during the merge. For example,
     * ancestor_entry may be NULL to indicate that a file was added in both branches and must be
     * resolved.
     *
     * @param conflict a set of entry that represents a conflict
     * @throws GitException git errors
     */
    public void conflictAdd(Conflict conflict) {
        Error.throwIfNeeded(
                jniConflictAdd(
                        getRawPointer(),
                        conflict.ancestor.getRawPointer(),
                        conflict.our.getRawPointer(),
                        conflict.their.getRawPointer()));
    }

    /**
     * Roughly equals to {@code git ls-files -u}
     *
     * @see <a href=https://libgit2.org/libgit2/#HEAD/group/index/git_index_conflict_get></a>
     * @param path path to get the conflict
     * @throws GitException git errors
     */
    Conflict conflictGet(String path) {
        Conflict conflict = new Conflict();
        jniConflictGet(
                conflict.ancestor._rawPtr,
                conflict.our._rawPtr,
                conflict.their._rawPtr,
                getRawPointer(),
                path);
        return conflict;
    }

    /**
     * Removes the index entries that represent a conflict of a single file.
     *
     * @param path path to remove conflicts for
     * @throws GitException git errors
     */
    public void conflictRemove(String path) {
        Error.throwIfNeeded(jniConflictRemove(getRawPointer(), path));
    }

    /**
     * Remove all conflicts in the index (entries with a stage greater than 0).
     *
     * @throws GitException git errors
     */
    public void conflictCleanup() {
        Error.throwIfNeeded(jniConflictCleanup(getRawPointer()));
    }

    /**
     * Determine if the index contains entries representing file conflicts.
     *
     * @return 1 if at least one conflict is found, 0 otherwise.
     */
    public boolean hasConflicts() {
        return jniHasConflicts(getRawPointer()) == 1;
    }

    /** @return an iterator that can loops over iterator */
    public ConflictIterator conflictIteratorNew() {
        ConflictIterator iterator = new ConflictIterator();
        Error.throwIfNeeded(jniConflictIteratorNew(iterator._ptr, getRawPointer()));
        return iterator;
    }

    public enum Capability implements IBitEnum {
        IGNORE_CASE(1),
        NO_FILEMODE(1 << 1),
        NO_SYMLINKS(1 << 2),
        FROM_OWNER(-1),
        ;

        private final int _bit;

        Capability(int bit) {
            this._bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public enum AddOption implements IBitEnum {
        DEFAULT(0),
        FORCE(1 << 0),
        DISABLE_PATHSPEC_MATCH(1 << 1),
        CHECK_PATHSPEC(1 << 2),
        ;

        private final int _bit;

        AddOption(int bit) {
            this._bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public enum Stage implements IBitEnum {
        /**
         * Match any index stage.
         *
         * <p>Some index APIs take a stage to match; pass this value to match any entry matching the
         * path regardless of stage.
         */
        ANY(-1),

        /** A normal staged file in the index. */
        NORMAL(0),

        /** The ancestor side of a conflict. */
        ANCESTOR(1),

        /** The "ours" side of a conflict. */
        OURS(2),

        /** The "theirs" side of a conflict. */
        THEIRS(3);
        private final int _bit;

        Stage(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    public interface Callback {
        void accept(String path, String pathSpec);
    }

    public static class Entry extends CAutoReleasable {
        protected Entry(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Nonnull
        public static Entry create() {
            return new Entry(false, jniEntryNew());
        }

        /**
         * Get an entry to one of the entries in the index
         *
         * <p>The entry is not modifiable and should not be freed. Because the `git_index_entry`
         * struct is a publicly defined struct, you should be able to make your own permanent copy
         * of the data if necessary.
         *
         * @param index an existing index object
         * @param n the position of the entry
         * @return the entry or null if out of bounds
         */
        public static Entry getByIndex(Index index, int n) {
            long ptr = Index.jniGetByIndex(index.getRawPointer(), n);
            if (ptr == 0) {
                return null;
            }
            return new Entry(true, ptr);
        }

        /**
         * Get one of the entries in the index that matches the given path
         *
         * <p>The entry is not modifiable and should not be freed. Because the `git_index_entry`
         * struct is a publicly defined struct, you should be able to make your own permanent copy
         * of the data if necessary.
         *
         * @param index an existing index object
         * @param path path to search
         * @param stage stage to search
         * @return the entry or null if it was not found
         */
        @CheckForNull
        public static Entry getByPath(
                @Nonnull Index index, @Nonnull String path, @Nonnull Stage stage) {
            long ptr = Index.jniGetByPath(index.getRawPointer(), path, stage.getBit());
            if (ptr == 0) {
                return null;
            }
            return new Entry(true, ptr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniEntryFree(cPtr);
        }

        /**
         * Return the stage number from a git index entry
         *
         * <p>This entry is calculated from the entry's flag attribute like this:
         *
         * <p>(entry->flags & GIT_INDEX_ENTRY_STAGEMASK) >> GIT_INDEX_ENTRY_STAGESHIFT
         *
         * @return the stage number
         */
        public int state() {
            return Index.jniEntryStage(getRawPointer());
        }

        /**
         * Return whether the given index entry is a conflict (has a high stage entry). This is
         * simply shorthand for `git_index_entry_stage > 0`.
         *
         * @return if the entry is a conflict entry
         */
        public boolean isConflict() {
            return Index.jniEntryIsConflict(getRawPointer()) == 1;
        }

        @Nonnull
        public Instant getCtime() {
            return Instant.ofEpochSecond(
                    jniEntryGetCtimeSeconds(getRawPointer()),
                    Index.jniEntryGetCtimeNanoseconds(getRawPointer()));
        }

        public void setCtime(@Nonnull Instant ctime) {
            jniEntrySetCtimeSeconds(getRawPointer(), ctime.getEpochSecond());
            jniEntrySetCtimeNanoseconds(getRawPointer(), ctime.getNano());
        }

        @Nonnull
        public Instant getMtime() {
            return Instant.ofEpochSecond(
                    jniEntryGetMtimeSeconds(getRawPointer()),
                    Index.jniEntryGetMtimeNanoseconds(getRawPointer()));
        }

        public void setMtime(@Nonnull Instant mtime) {
            jniEntrySetMtimeSeconds(getRawPointer(), mtime.getEpochSecond());
            jniEntrySetMtimeNanoseconds(getRawPointer(), mtime.getNano());
        }

        public int getDev() {
            return jniEntryGetDev(getRawPointer());
        }

        public void setDev(int dev) {
            jniEntrySetDev(getRawPointer(), dev);
        }

        public int getIno() {
            return jniEntryGetIno(getRawPointer());
        }

        public void setIno(int ino) {
            jniEntrySetIno(getRawPointer(), ino);
        }

        public int getMode() {
            return jniEntryGetMode(getRawPointer());
        }

        public void setMode(int mode) {
            jniEntrySetMode(getRawPointer(), mode);
        }

        public int getUid() {
            return jniEntryGetUid(getRawPointer());
        }

        public void setUid(int uid) {
            jniEntrySetUid(getRawPointer(), uid);
        }

        public int getGid() {
            return jniEntryGetGid(getRawPointer());
        }

        public void setGid(int gid) {
            jniEntrySetGid(getRawPointer(), gid);
        }

        public int getFileSize() {
            return jniEntryGetFileSize(getRawPointer());
        }

        public void setFileSize(int fileSize) {
            jniEntrySetFileSize(getRawPointer(), fileSize);
        }

        @Nonnull
        public Oid getId() {
            byte[] raw = jniEntryGetId(getRawPointer());
            return Oid.of(raw);
        }

        public void setId(@Nonnull Oid id) {
            jniEntrySetId(getRawPointer(), id);
        }

        public int getFlags() {
            return jniEntryGetFlags(getRawPointer());
        }

        public void setFlags(int flags) {
            jniEntrySetFlags(getRawPointer(), flags);
        }

        public int getFlagsExtended() {
            return jniEntryGetFlagsExtended(getRawPointer());
        }

        public void setFlagsExtended(int flagsExtended) {
            jniEntrySetFlagsExtended(getRawPointer(), flagsExtended);
        }

        public String getPath() {
            return jniEntryGetPath(getRawPointer());
        }

        public void setPath(String path) {
            jniEntrySetPath(getRawPointer(), path);
        }
    }

    public static class Iterator extends CAutoReleasable {
        private final AtomicLong _ptr = new AtomicLong();

        protected Iterator(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Nonnull
        public static Iterator of(@Nonnull Index index) {
            Iterator iterator = new Iterator(false, 0);
            Error.throwIfNeeded(Index.jniIteratorNew(iterator._ptr, index.getRawPointer()));
            return iterator;
        }

        @Override
        protected void freeOnce(long cPtr) {
            Index.jniIteratorFree(cPtr);
        }

        @CheckForNull
        public Entry next() {
            Entry nextEntry = new Entry(true, 0);
            int e = Index.jniIteratorNext(nextEntry._rawPtr, _ptr.get());
            if (e == ITEROVER.getCode()) {
                return null;
            }
            Error.throwIfNeeded(e);
            return nextEntry;
        }
    }

    /** A set of Entry that represents a conflict. */
    public static class Conflict {
        public final Entry ancestor;
        public final Entry our;
        public final Entry their;

        Conflict() {
            ancestor = new Entry(true, 0);
            our = new Entry(true, 0);
            their = new Entry(true, 0);
        }
    }

    public static class ConflictIterator {
        AtomicLong _ptr = new AtomicLong();

        @Override
        protected void finalize() throws Throwable {
            if (_ptr.get() != 0) {
                jniConflictIteratorFree(_ptr.get());
            }
            super.finalize();
        }

        public Conflict next() {
            Conflict conflict = new Conflict();
            int e =
                    jniConflictNext(
                            conflict.ancestor._rawPtr,
                            conflict.our._rawPtr,
                            conflict.their._rawPtr,
                            _ptr.get());
            if (e == ITEROVER.getCode()) {
                return null;
            }
            Error.throwIfNeeded(e);
            return conflict;
        }
    }
}
