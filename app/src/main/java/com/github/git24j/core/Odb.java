package com.github.git24j.core;

import static com.github.git24j.core.GitException.ErrorCode.ENOTFOUND;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Odb extends CAutoReleasable {
    /** int git_odb_add_alternate(git_odb *odb, git_odb_backend *backend, int priority); */
    static native int jniAddAlternate(long odb, long backend, int priority);

    /** int git_odb_add_backend(git_odb *odb, git_odb_backend *backend, int priority); */
    static native int jniAddBackend(long odb, long backend, int priority);

    /** int git_odb_add_disk_alternate(git_odb *odb, const char *path); */
    static native int jniAddDiskAlternate(long odb, String path);

    /**
     * int git_odb_backend_loose(git_odb_backend **out, const char *objects_dir, int
     * compression_level, int do_fsync, unsigned int dir_mode, unsigned int file_mode);
     */
    static native int jniBackendLoose(
            AtomicLong out,
            String objectsDir,
            int compressionLevel,
            int doFsync,
            int dirMode,
            int fileMode);

    /** int git_odb_backend_one_pack(git_odb_backend **out, const char *index_file); */
    static native int jniBackendOnePack(AtomicLong out, String objectsDir);

    /** int git_odb_exists(git_odb *db, const git_oid *id); */
    static native int jniExists(long db, Oid id);

    /**
     * int git_odb_exists_prefix(git_oid *out, git_odb *db, const git_oid *short_id, size_t len);
     */
    static native int jniExistsPrefix(Oid out, long db, String shortId);

    /** int git_odb_expand_ids(git_odb *db, git_odb_expand_id *ids, size_t count); */
    static native int jniExpandIds(long db, long ids, int count);

    static native byte[] jniExpandIdsGetId(long expandIdsPtr, int idx);

    static native int jniExpandIdsGetLength(long expandIdsPtr);

    static native int jniExpandIdsGetType(long expandIdsPtr, int idx);

    static native long jniExpandIdsNew(String[] shortIds, int type);

    /** void git_odb_free(git_odb *db); */
    static native void jniFree(long db);

    /** int git_odb_get_backend(git_odb_backend **out, git_odb *odb, size_t pos); */
    static native int jniGetBackend(AtomicLong out, long odb, int pos);

    /** int git_odb_hash(git_oid *out, const void *data, size_t len, git_object_t type); */
    static native int jniHash(Oid out, byte[] data, int len, int type);

    /** int git_odb_hashfile(git_oid *out, const char *path, git_object_t type); */
    static native int jniHashfile(Oid out, String path, int type);

    /** int git_odb_new(git_odb **out); */
    static native int jniNew(AtomicLong out);

    /** size_t git_odb_num_backends(git_odb *odb); */
    static native int jniNumBackends(long odb);

    /** const void * git_odb_object_data(git_odb_object *object); */
    static native long jniObjectData(long object);

    /** int git_odb_object_dup(git_odb_object **dest, git_odb_object *source); */
    static native int jniObjectDup(AtomicLong dest, long source);

    /** void git_odb_object_free(git_odb_object *object); */
    static native void jniObjectFree(long object);

    /** const git_oid * git_odb_object_id(git_odb_object *object); */
    static native byte[] jniObjectId(long object);

    /** size_t git_odb_object_size(git_odb_object *object); */
    static native int jniObjectSize(long object);

    /** git_object_t git_odb_object_type(git_odb_object *object); */
    static native int jniObjectType(long object);

    /** int git_odb_open(git_odb **out, const char *objects_dir); */
    static native int jniOpen(AtomicLong out, String objectsDir);

    /**
     * int git_odb_open_rstream(git_odb_stream **out, size_t *len, git_object_t *type, git_odb *db,
     * const git_oid *oid);
     */
    static native int jniOpenRstream(
            AtomicLong out, AtomicInteger len, AtomicInteger type, long db, Oid oid);

    /**
     * int git_odb_open_wstream(git_odb_stream **out, git_odb *db, git_off_t size, git_object_t
     * type);
     */
    static native int jniOpenWstream(AtomicLong out, long db, int size, int type);

    /** int git_odb_read(git_odb_object **out, git_odb *db, const git_oid *id); */
    static native int jniRead(AtomicLong out, long db, Oid id);

    /**
     * int git_odb_read_header(size_t *len_out, git_object_t *type_out, git_odb *db, const git_oid
     * *id);
     */
    static native int jniReadHeader(AtomicInteger lenOut, AtomicInteger typeOut, long db, Oid id);

    /**
     * int git_odb_read_prefix(git_odb_object **out, git_odb *db, const git_oid *short_id, size_t
     * len);
     */
    static native int jniReadPrefix(AtomicLong out, long db, String shortId);

    /** int git_odb_refresh(const git_odb *db); */
    static native int jniRefresh(long db);

    /** int git_odb_stream_finalize_write(git_oid *out, git_odb_stream *stream); */
    static native int jniStreamFinalizeWrite(Oid out, long stream);

    /** void git_odb_stream_free(git_odb_stream *stream); */
    static native void jniStreamFree(long stream);

    /** int git_odb_stream_read(git_odb_stream *stream, char *buffer, size_t len); */
    static native int jniStreamRead(long streamPtr, byte[] buffer, int len);

    /** int git_odb_stream_write(git_odb_stream *stream, const char *buffer, size_t len); */
    static native int jniStreamWrite(long stream, String buffer, int len);

    /**
     * int git_odb_write(git_oid *out, git_odb *odb, const void *data, size_t len, git_object_t
     * type);
     */
    static native int jniWrite(Oid out, long odb, byte[] data, int len, int type);

    protected Odb(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    /**
     * Create a new object database with no backends.
     *
     * <p>Before the ODB can be used for read/writing, a custom database backend must be manually
     * added using `git_odb_add_backend()`
     *
     * @return object data base
     * @throws GitException git errors
     */
    @Nonnull
    public static Odb create() {
        Odb out = new Odb(false, 0);
        Error.throwIfNeeded(jniNew(out._rawPtr));
        if (out.isNull()) {
            throw new GitException(GitException.ErrorClass.ODB, "Failed to create object database");
        }
        return out;
    }

    /**
     * Create a new object database and automatically add the two default backends:
     *
     * <p>- git_odb_backend_loose: read and write loose object files from disk, assuming
     * `objects_dir` as the Objects folder
     *
     * <p>- git_odb_backend_pack: read objects from packfiles, assuming `objects_dir` as the Objects
     * folder which contains a 'pack/' folder with the corresponding data
     *
     * @param objectsDir path of the backends' "objects" directory.
     * @return object data base
     * @throws GitException fail to open odb error
     */
    @Nonnull
    public static Odb create(@Nonnull String objectsDir) {
        Odb out = new Odb(false, 0);
        Error.throwIfNeeded(jniOpen(out._rawPtr, objectsDir));
        if (out.isNull()) {
            throw new GitException(GitException.ErrorClass.ODB, "Failed to create object database");
        }
        return out;
    }

    /**
     * Determine the object-ID (sha1 hash) of a data buffer
     *
     * <p>The resulting SHA-1 OID will be the identifier for the data buffer as if the data buffer
     * it were to written to the ODB.
     *
     * @return the resulting object-ID.
     * @param data data to hash
     * @param type of the data to hash
     * @throws GitException git errors
     */
    @Nonnull
    public static Oid hash(@Nonnull byte[] data, @Nonnull GitObject.Type type) {
        Oid oid = new Oid();
        Error.throwIfNeeded(jniHash(oid, data, data.length, type.getBit()));
        return oid;
    }

    /**
     * Read a file from disk and fill a git_oid with the object id that the file would have if it
     * were written to the Object Database as an object of the given type (w/o applying filters).
     * Similar functionality to git.git's `git hash-object` without the `-w` flag, however, with the
     * --no-filters flag. If you need filters, see git_repository_hashfile.
     *
     * @return resulting oid
     * @param path file to read and determine object id for
     * @param type the type of the object that will be hashed
     * @throws GitException git errors
     */
    public static Oid hashfile(@Nonnull String path, @Nonnull GitObject.Type type) {
        Oid out = new Oid();
        Error.throwIfNeeded(jniHashfile(out, path, type.getBit()));
        return out;
    }

    /**
     * Create a backend for loose objects
     *
     * @return the odb backend
     * @param objectsDir the Git repository's objects directory
     * @param compressionLevel zlib compression level to use
     * @param doFsync whether to do an fsync() after writing
     * @param dirMode permissions to use creating a directory or 0 for defaults
     * @param fileMode permissions to use creating a file or 0 for defaults
     * @throws GitException git errors
     */
    @Nonnull
    public static OdbBackend backendLoose(
            @Nonnull String objectsDir,
            int compressionLevel,
            boolean doFsync,
            int dirMode,
            int fileMode) {
        OdbBackend out = new OdbBackend(false, 0);
        Error.throwIfNeeded(
                jniBackendLoose(
                        out._rawPtr,
                        objectsDir.toString(),
                        compressionLevel,
                        doFsync ? 1 : 0,
                        dirMode,
                        fileMode));
        return out;
    }

    /**
     * Create a backend out of a single packfile
     *
     * <p>This can be useful for inspecting the contents of a single packfile.
     *
     * @return odb backend
     * @param indexFile path to the packfile's .idx file
     * @throws GitException git errors
     */
    @Nonnull
    public static OdbBackend backendOnePack(@Nonnull String indexFile) {
        OdbBackend out = new OdbBackend(false, 0);
        Error.throwIfNeeded(jniBackendOnePack(out._rawPtr, indexFile));
        return out;
    }

    /**
     * Add an on-disk alternate to an existing Object DB.
     *
     * <p>Note that the added path must point to an `objects`, not to a full repository, to use it
     * as an alternate store.
     *
     * <p>Alternate backends are always checked for objects *after* all the main backends have been
     * exhausted.
     *
     * <p>Writing is disabled on alternate backends.
     *
     * @param path path to the objects folder for the alternate
     * @throws GitException git errors
     */
    public void addDiskAlternate(@Nonnull String path) {
        Error.throwIfNeeded(jniAddDiskAlternate(getRawPointer(), path));
    }

    /**
     * Read an object from the database.
     *
     * <p>This method queries all available ODB backends trying to read the given OID.
     *
     * <p>The returned object is reference counted and internally cached, so it should be closed by
     * the user once it's no longer in use.
     *
     * @return the read object
     * @param id identity of the object to read.
     * @throws GitException git errors
     */
    @Nullable
    public OdbObject read(@Nonnull Oid id) {
        OdbObject out = new OdbObject(false, 0);
        int e = jniRead(out._rawPtr, getRawPointer(), id);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return out;
    }

    /**
     * Read an object from the database, given a prefix of its identifier.
     *
     * <p>This method queries all available ODB backends trying to match the 'len' first hexadecimal
     * characters of the 'short_id'. The remaining (GIT_OID_HEXSZ-len)*4 bits of 'short_id' must be
     * 0s. 'len' must be at least GIT_OID_MINPREFIXLEN, and the prefix must be long enough to
     * identify a unique object in all the backends; the method will fail otherwise.
     *
     * <p>The returned object is reference counted and internally cached, so it should be closed by
     * the user once it's no longer in use.
     *
     * @param shortId a prefix of the id of the object to read.
     * @return - 0 if the object was read; - GIT_ENOTFOUND if the object is not in the database. -
     *     GIT_EAMBIGUOUS if the prefix is ambiguous (several objects match the prefix)
     */
    @Nullable
    public OdbObject readPrefix(String shortId) {
        OdbObject out = new OdbObject(false, 0);
        int e = jniReadPrefix(out._rawPtr, getRawPointer(), shortId);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return out;
    }

    /**
     * Read the header of an object from the database, without reading its full contents.
     *
     * <p>The header includes the length and the type of an object.
     *
     * <p>Note that most backends do not support reading only the header of an object, so the whole
     * object will be read and then the header will be returned.
     *
     * @param id identity of the object to read.
     * @return OdbObject.Type object
     * @throws GitException git errors
     */
    @Nullable
    public OdbObject.Header readHeader(@Nonnull Oid id) {
        AtomicInteger lenOut = new AtomicInteger();
        AtomicInteger typeOut = new AtomicInteger();
        int e = jniReadHeader(lenOut, typeOut, getRawPointer(), id);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return new OdbObject.Header(
                lenOut.get(), IBitEnum.valueOf(typeOut.get(), GitObject.Type.class));
    }

    /**
     * Determine if the given object can be found in the object database.
     *
     * @param id the object to search for.
     * @return true if the object was found
     */
    public boolean exists(@Nonnull Oid id) {
        return jniExists(getRawPointer(), id) == 1;
    }

    /**
     * Determine if an object can be found in the object database by an abbreviated object ID.
     *
     * @return The full OID of the found object if just one is found or empty if search failed.
     * @param shortId A prefix of the id of the object to read.
     * @throws GitException GIT_EAMBIGUOUS if multiple or other read errors
     */
    @Nullable
    public Oid existsPrefix(@Nonnull String shortId) {
        Oid fullId = new Oid();
        int e = jniExistsPrefix(fullId, getRawPointer(), shortId);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return fullId;
    }

    /**
     * Determine if one or more objects can be found in the object database by their abbreviated
     * object ID and type. The given array will be updated in place: for each abbreviated ID that is
     * unique in the database, and of the given type (if specified), the full object ID, object ID
     * length (`GIT_OID_HEXSZ`) and type will be written back to the array. For IDs that are not
     * found (or are ambiguous), the array entry will be zeroed.
     *
     * <p>Note that since this function operates on multiple objects, the underlying database will
     * not be asked to be reloaded if an object is not found (which is unlike other object database
     * operations.)
     *
     * @param shortIds An array of short object IDs to search for
     * @throws GitException git errors
     * @apiNote Use with caution, {@code git_odb_expand_ids} failed to expand all ids, see unit
     *     tests for more details.
     */
    @Nonnull
    public List<Oid> expandIds(@Nonnull List<String> shortIds, @Nonnull GitObject.Type type) {
        int idsCount = shortIds.size();
        long cIdArr = jniExpandIdsNew(shortIds.toArray(new String[0]), type.getBit());
        Error.throwIfNeeded(jniExpandIds(getRawPointer(), cIdArr, idsCount));
        List<Oid> expandIds = new ArrayList<>(idsCount);
        for (int i = 0; i < idsCount; i++) {
            Oid oid = Oid.of(jniExpandIdsGetId(cIdArr, i));
            expandIds.add(oid);
        }
        // FIXME: free expand IDs
        return expandIds;
    }

    /**
     * Refresh the object database to load newly added files.
     *
     * <p>If the object databases have changed on disk while the library is running, this function
     * will force a reload of the underlying indexes.
     *
     * <p>Use this function when you're confident that an external application has tampered with the
     * ODB.
     *
     * <p>NOTE that it is not necessary to call this function at all. The library will automatically
     * attempt to refresh the ODB when a lookup fails, to see if the looked up object exists on disk
     * but hasn't been loaded yet.
     *
     * @throws GitException git errors
     */
    public void refresh() {
        Error.throwIfNeeded(jniRefresh(getRawPointer()));
    }

    /**
     * Write an object directly into the ODB
     *
     * <p>This method writes a full object straight into the ODB. For most cases, it is preferred to
     * write objects through a write stream, which is both faster and less memory intensive,
     * specially for big objects.
     *
     * <p>This method is provided for compatibility with custom backends which are not able to
     * support streaming writes
     *
     * @param data buffer with the data to store
     * @param type type of the data to store
     * @return out oid
     * @throws GitException git errors
     */
    @Nonnull
    public Oid write(byte[] data, GitObject.Type type) {
        Oid out = new Oid();
        Error.throwIfNeeded(jniWrite(out, getRawPointer(), data, data.length, type.getBit()));
        return out;
    }

    /**
     * Open a stream to write an object into the ODB
     *
     * <p>The type and final length of the object must be specified when opening the stream.
     *
     * <p>The returned stream will be of type `GIT_STREAM_WRONLY`, and it won't be effective until
     * `git_odb_stream_finalize_write` is called and returns without an error
     *
     * <p>The stream must always be freed when done with `git_odb_stream_free` or will leak memory.
     *
     * @see Odb.Stream
     * @return opened write stream
     * @param size final size of the object that will be written
     * @param type type of the object that will be written
     * @throws GitException git errors
     */
    @Nonnull
    public Stream openWstream(int size, @Nonnull GitObject.Type type) {
        Stream ws = new Stream(0);
        Error.throwIfNeeded(jniOpenWstream(ws._rawPtr, getRawPointer(), size, type.getBit()));
        return ws;
    }

    /**
     * Open a stream to read an object from the ODB
     *
     * <p>Note that most backends do *not* support streaming reads because they store their objects
     * as compressed/delta'ed blobs.
     *
     * <p>It's recommended to use `git_odb_read` instead, which is assured to work on all backends.
     *
     * <p>The returned stream will be of type `GIT_STREAM_RDONLY` and will have the following
     * methods:
     *
     * <p>- stream->read: read `n` bytes from the stream - stream->free: free the stream
     *
     * <p>The stream must always be free'd or will leak memory.
     *
     * @see Odb.RStream
     * @param oid oid of the object the stream will read from
     * @return read stream
     * @throws GitException git errors
     */
    @Nonnull
    public RStream openRstream(@Nonnull Oid oid) {
        AtomicLong out = new AtomicLong();
        AtomicInteger outLen = new AtomicInteger();
        AtomicInteger outType = new AtomicInteger();
        Error.throwIfNeeded(jniOpenRstream(out, outLen, outType, getRawPointer(), oid));
        return new RStream(
                out.get(), outLen.get(), IBitEnum.valueOf(outType.get(), GitObject.Type.class));
    }

    /**
     * Add a custom backend to an existing Object DB
     *
     * <p>The backends are checked in relative ordering, based on the value of the `priority`
     * parameter.
     *
     * <p>Read <sys/odb_backend.h> for more information.
     *
     * @param backend pointer to a git_odb_backend instance
     * @param priority Value for ordering the backends queue
     * @throws GitException 0 on success; error code otherwise
     */
    public void addBackend(@Nonnull OdbBackend backend, int priority) {
        Error.throwIfNeeded(jniAddBackend(getRawPointer(), backend.getRawPointer(), priority));
    }

    /**
     * Add a custom backend to an existing Object DB; this backend will work as an alternate.
     *
     * <p>Alternate backends are always checked for objects *after* all the main backends have been
     * exhausted.
     *
     * <p>The backends are checked in relative ordering, based on the value of the `priority`
     * parameter.
     *
     * <p>Writing is disabled on alternate backends.
     *
     * <p>Read <sys/odb_backend.h> for more information.
     *
     * @param backend pointer to a git_odb_backend instance
     * @param priority Value for ordering the backends queue
     * @throws GitException 0 on success; error code otherwise
     */
    public void addAlternate(@Nonnull OdbBackend backend, int priority) {
        Error.throwIfNeeded(jniAddAlternate(getRawPointer(), backend.getRawPointer(), priority));
    }

    /** @return the number of ODB backend objects */
    public int numBackends() {
        return jniNumBackends(getRawPointer());
    }

    /**
     * Lookup an ODB backend object by index
     *
     * @return ODB backend at pos or empty if pos is invalid.
     * @param pos index into object database backend list
     * @throws GitException git errors
     */
    @Nullable
    public OdbBackend getBackend(int pos) {
        OdbBackend backend = new OdbBackend(false, 0);
        int e = jniGetBackend(backend._rawPtr, getRawPointer(), pos);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        return backend;
    }

    public static class ExpandId {
        private final Oid _oid;
        private final GitObject.Type _type;

        public ExpandId(@Nullable Oid oid, @Nullable GitObject.Type type) {
            _oid = oid;
            _type = type;
        }

        public Oid getOid() {
            return _oid;
        }

        public GitObject.Type getType() {
            return _type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ExpandId expandId = (ExpandId) o;
            return Objects.equals(_oid, expandId._oid) && _type == expandId._type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_oid, _type);
        }
    }

    public static class Stream extends CAutoCloseable {
        protected Stream(long rawPointer) {
            super(rawPointer);
        }

        @Override
        protected void releaseOnce(long cPtr) {
            jniStreamFree(cPtr);
        }

        /**
         * Write to an odb stream
         *
         * <p>This method will fail if the total number of received bytes exceeds the size declared
         * with `git_odb_open_wstream()`
         *
         * @param buffer the data to write
         * @throws GitException git errors
         */
        public void write(@Nonnull String buffer) {
            Error.throwIfNeeded(jniStreamWrite(getRawPointer(), buffer, buffer.length()));
        }

        /**
         * Finish writing to an odb stream
         *
         * <p>The object will take its final name and will be available to the odb.
         *
         * <p>This method will fail if the total number of received bytes differs from the size
         * declared with `git_odb_open_wstream()`
         *
         * @return the resulting object's id
         * @throws GitException git errors
         */
        public Oid finalizeWrite() {
            Oid out = new Oid();
            Error.throwIfNeeded(jniStreamFinalizeWrite(out, getRawPointer()));
            return out;
        }

        /**
         * Read from an odb stream
         *
         * <p>Most backends don't implement streaming reads
         */
        public void read(byte[] buffer) {
            Error.throwIfNeeded(jniStreamRead(getRawPointer(), buffer, buffer.length));
        }
    }

    public static class RStream extends Stream {
        private final int _size;
        private final GitObject.Type _type;

        protected RStream(long rawPointer, int size, GitObject.Type type) {
            super(rawPointer);
            _size = size;
            _type = type;
        }

        public int getSize() {
            return _size;
        }

        public GitObject.Type getType() {
            return _type;
        }
    }
}
