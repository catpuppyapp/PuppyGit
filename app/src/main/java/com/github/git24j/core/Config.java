package com.github.git24j.core;

import static com.github.git24j.core.GitException.ErrorCode.ENOTFOUND;
import static com.github.git24j.core.GitException.ErrorCode.ITEROVER;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Memory representation of a set of config files */
public class Config extends CAutoReleasable {
    /**
     * int git_config_add_file_ondisk(git_config *cfg, const char *path, git_config_level_t level,
     * const git_repository *repo, int force);
     */
    static native int jniAddFileOndisk(long cfg, String path, int level, long repoPtr, int force);

    /**
     * int git_config_backend_foreach_match(git_config_backend *backend, const char *regexp,
     * git_config_foreach_cb callback, void *payload);
     */
    static native int jniBackendForeachMatch(long backend, String regexp, CallbackJ callback);

    /** int git_config_delete_entry(git_config *cfg, const char *name); */
    static native int jniDeleteEntry(long cfg, String name);

    /** int git_config_delete_multivar(git_config *cfg, const char *name, const char *regexp); */
    static native int jniDeleteMultivar(long cfg, String name, String regexp);

    /** void git_config_entry_free(git_config_entry *entry); */
    static native void jniEntryFree(long entry);

    /** unsigned int include_depth */
    static native int jniEntryGetIncludeDepth(long entryPtr);

    /** git_config_level_t level */
    static native int jniEntryGetLevel(long entryPtr);

    /** const char *name */
    static native String jniEntryGetName(long entryPtr);

    /** const char *value */
    static native String jniEntryGetValue(long entryPtr);

    /**
     * The type of backend that this entry exists in (eg, "file")
     *
     *
     * @since libgit2 1.8.0
     * */
    static native String jniEntryGetBackendType(long entryPtr);
    /**
     * The path to the origin of this entry. For config files, this is
     * the path to the file.
     *
     *
     * @since libgit2 1.8.0
     */
    static native String jniEntryGetOriginPath(long entryPtr);

    /** int git_config_find_global(git_buf *out); */
    static native int jniFindGlobal(Buf out);

    /** int git_config_find_programdata(git_buf *out); */
    static native int jniFindProgramdata(Buf out);

    /** int git_config_find_system(git_buf *out); */
    static native int jniFindSystem(Buf out);

    /** int git_config_find_xdg(git_buf *out); */
    static native int jniFindXdg(Buf out);

    /**
     * int git_config_foreach(const git_config *cfg, git_config_foreach_cb callback, void *payload);
     */
    static native int jniForeach(long cfg, CallbackJ callback);

    /**
     * int git_config_foreach_match(const git_config *cfg, const char *regexp, git_config_foreach_cb
     * callback, void *payload);
     */
    static native int jniForeachMatch(long cfg, String regexp, CallbackJ callback);

    /** void git_config_free(git_config *cfg); */
    static native void jniFree(long cfg);

    /** int git_config_get_bool(int *out, const git_config *cfg, const char *name); */
    static native int jniGetBool(AtomicInteger out, long cfg, String name);

    /**
     * int git_config_get_entry(git_config_entry **out, const git_config *cfg, const char *name);
     */
    static native int jniGetEntry(AtomicLong out, long cfg, String name);

    /** int git_config_get_int32(int32_t *out, const git_config *cfg, const char *name); */
    static native int jniGetInt32(AtomicInteger out, long cfg, String name);

    /** int git_config_get_int64(int64_t *out, const git_config *cfg, const char *name); */
    static native int jniGetInt64(AtomicLong out, long cfg, String name);

    /**
     * int git_config_get_multivar_foreach(const git_config *cfg, const char *name, const char
     * *regexp, git_config_foreach_cb callback, void *payload);
     */
    static native int jniGetMultivarForeach(
            long cfg, String name, String regexp, CallbackJ callback);

    /** int git_config_get_path(git_buf *out, const git_config *cfg, const char *name); */
    static native int jniGetPath(Buf out, long cfg, String name);

    /** int git_config_get_string(const char **out, const git_config *cfg, const char *name); */
    static native int jniGetString(AtomicReference<String> out, long cfg, String name);

    /** int git_config_get_string_buf(git_buf *out, const git_config *cfg, const char *name); */
    static native int jniGetStringBuf(Buf out, long cfg, String name);

    /** void git_config_iterator_free(git_config_iterator *iter); */
    static native void jniIteratorFree(long iter);

    /**
     * int git_config_iterator_glob_new(git_config_iterator **out, const git_config *cfg, const char
     * *regexp);
     */
    static native int jniIteratorGlobNew(AtomicLong out, long cfg, String regexp);

    /** int git_config_iterator_new(git_config_iterator **out, const git_config *cfg); */
    static native int jniIteratorNew(AtomicLong out, long cfg);

    /** int git_config_lock(git_transaction **tx, git_config *cfg); */
    static native int jniLock(AtomicLong tx, long cfg);

    /**
     * int git_config_multivar_iterator_new(git_config_iterator **out, const git_config *cfg, const
     * char *name, const char *regexp);
     */
    static native int jniMultivarIteratorNew(AtomicLong out, long cfg, String name, String regexp);

    /** int git_config_new(git_config **out); */
    static native int jniNew(AtomicLong out);

    /** int git_config_next(git_config_entry **entry, git_config_iterator *iter); */
    static native int jniNext(AtomicLong entry, long iter);

    /** int git_config_open_default(git_config **out); */
    static native int jniOpenDefault(AtomicLong out);

    /** int git_config_open_global(git_config **out, git_config *config); */
    static native int jniOpenGlobal(AtomicLong out, long config);

    /**
     * int git_config_open_level(git_config **out, const git_config *parent, git_config_level_t
     * level);
     */
    static native int jniOpenLevel(AtomicLong out, long parent, int level);

    /** int git_config_open_ondisk(git_config **out, const char *path); */
    static native int jniOpenOndisk(AtomicLong out, String path);

    /** int git_config_parse_bool(int *out, const char *value); */
    static native int jniParseBool(AtomicInteger out, String value);

    /** int git_config_parse_int32(int32_t *out, const char *value); */
    static native int jniParseInt32(AtomicInteger out, String value);

    /** int git_config_parse_int64(int64_t *out, const char *value); */
    static native int jniParseInt64(AtomicLong out, String value);

    /** int git_config_parse_path(git_buf *out, const char *value); */
    static native int jniParsePath(Buf out, String value);

    /** int git_config_set_bool(git_config *cfg, const char *name, int value); */
    static native int jniSetBool(long cfg, String name, int value);

    /** int git_config_set_int32(git_config *cfg, const char *name, int32_t value); */
    static native int jniSetInt32(long cfg, String name, int value);

    /** int git_config_set_int64(git_config *cfg, const char *name, int64_t value); */
    static native int jniSetInt64(long cfg, String name, long value);

    /**
     * int git_config_set_multivar(git_config *cfg, const char *name, const char *regexp, const char
     * *value);
     */
    static native int jniSetMultivar(long cfg, String name, String regexp, String value);

    /** int git_config_set_string(git_config *cfg, const char *name, const char *value); */
    static native int jniSetString(long cfg, String name, String value);

    /** int git_config_snapshot(git_config **out, git_config *config); */
    static native int jniSnapshot(AtomicLong out, long config);

    protected Config(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    /**
     * Locate the path to the global configuration file
     *
     * @return path where global configuration is stored.
     */
    @Nullable
    public static String findGlobal() {
        Buf buf = new Buf();
        int e = jniFindGlobal(buf);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        if (buf.getSize() == 0 || buf.getPtr() == null) {
            return null;
        }
        return buf.getString().orElse(null);
    }

    /**
     * Locate the path to the global configuration file
     *
     * <p>The user or global configuration file is usually located in `$HOME/.gitconfig`.
     *
     * <p>This method will try to guess the full path to that file, if the file exists. The returned
     * path may be used on any `git_config` call to load the global configuration file.
     *
     * <p>This method will not guess the path to the xdg compatible config file
     * (.config/git/config).
     *
     * @return the path to the global configuration file or empty
     * @throws GitException git errors
     */
    @Nullable
    public static String findXdg() {
        Buf buf = new Buf();
        int e = jniFindXdg(buf);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        if (buf.getSize() == 0 || buf.getPtr() == null) {
            return null;
        }
        return buf.getString().orElse(null);
    }

    /**
     * Locate the path to the system configuration file
     *
     * <p>If /etc/gitconfig doesn't exist, it will look for %PROGRAMFILES%\Git\etc\gitconfig.
     *
     * @return found config file
     * @throws GitException git errors
     */
    @Nullable
    public static String findSystem() {
        Buf buf = new Buf();
        int e = jniFindSystem(buf);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        if (buf.getSize() == 0 || buf.getPtr() == null) {
            return null;
        }
        return buf.getString().orElse(null);
    }

    /**
     * Locate the path to the configuration file in ProgramData
     *
     * <p>Look for the file in %PROGRAMDATA%\Git\config used by portable git.
     *
     * @return the path to the configuration file in ProgramData
     * @throws GitException git errors
     */
    @CheckForNull
    public static String findProgramdata() {
        Buf buf = new Buf();
        int e = jniFindProgramdata(buf);
        if (e == ENOTFOUND.getCode()) {
            return null;
        }
        Error.throwIfNeeded(e);
        if (buf.getSize() == 0 || buf.getPtr() == null) {
            return null;
        }
        return buf.getString().orElse(null);
    }

    public static Config openDefault() {
        Config cfg = new Config(false, 0);
        Error.throwIfNeeded(jniOpenDefault(cfg._rawPtr));
        return cfg;
    }

    public static Config newConfig() {
        Config cfg = new Config(false, 0);
        Error.throwIfNeeded(jniNew(cfg._rawPtr));
        return cfg;
    }

    /**
     * Create a new config instance containing a single on-disk file
     *
     * <p>This method is a simple utility wrapper for the following sequence of calls: -
     * git_config_new - git_config_add_file_ondisk
     *
     * @param path Path to the on-disk file to open
     * @return the configuration instance opened
     * @throws GitException git errors
     */
    @Nonnull
    public static Config openOndisk(@Nonnull String path) {
        Config cfg = new Config(false, 0);
        Error.throwIfNeeded(jniOpenOndisk(cfg._rawPtr, path));
        return cfg;
    }

    /**
     * Build a single-level focused config object from a multi-level one.
     *
     * <p>The returned config object can be used to perform get/set/delete operations on a single
     * specific level.
     *
     * <p>Getting several times the same level from the same parent multi-level config will return
     * different config instances, but containing the same config_file instance.
     *
     * @param parent Multi-level config to search for the given level
     * @param level Configuration level to search for
     * @return The configuration instance opened or null if the passed level cannot be found in the
     *     multi-level parent config
     * @throws GitException git errors
     */
    public static Config openLevel(ConfigLevel level, Config parent) {
        Config cfg = new Config(false, 0);
        int e = jniOpenLevel(cfg._rawPtr, parent.getRawPointer(), level._code);
        if (ENOTFOUND.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return cfg;
    }

    @CheckForNull
    public static Config openGlobal(@Nonnull Config parent) {
        Config cfg = new Config(false, 0);
        int e = jniOpenGlobal(cfg._rawPtr, parent.getRawPointer());
        if (ENOTFOUND.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return cfg;
    }

    /**
     * Parse a string value as a bool.
     *
     * <p>Valid values for true are: 'true', 'yes', 'on', 1 or any number different from 0 Valid
     * values for false are: 'false', 'no', 'off', 0
     *
     * @return parsed boolean
     * @param value value to parse
     * @throws GitException parsing error
     */
    public static boolean parseBool(@Nonnull String value) {
        AtomicInteger out = new AtomicInteger();
        Error.throwIfNeeded(jniParseBool(out, value));
        return out.get() != 0;
    }

    /**
     * Parse a string value as an int32.
     *
     * <p>An optional value suffix of 'k', 'm', or 'g' will cause the value to be multiplied by
     * 1024, 1048576, or 1073741824 prior to output.
     *
     * @return the result of the parsing
     * @param value value to parse
     * @throws GitException parsing errors
     */
    public static int parseInt(@Nonnull String value) {
        AtomicInteger out = new AtomicInteger();
        Error.throwIfNeeded(jniParseInt32(out, value));
        return out.get();
    }

    /**
     * Parse a string value as an int64.
     *
     * <p>An optional value suffix of 'k', 'm', or 'g' will cause the value to be multiplied by
     * 1024, 1048576, or 1073741824 prior to output.
     *
     * @return the result of the parsing
     * @param value value to parse
     * @throws GitException parsing error
     */
    public static long parseLong(@Nonnull String value) {
        AtomicLong out = new AtomicLong();
        Error.throwIfNeeded(jniParseInt64(out, value));
        return out.get();
    }

    /**
     * Parse a string value as a path.
     *
     * <p>A leading '~' will be expanded to the global search path (which defaults to the user's
     * home directory but can be overridden via `git_libgit2_opts()`.
     *
     * <p>If the value does not begin with a tilde, the input will be returned. @@return the result
     * of parsing
     *
     * @param value the path to evaluate
     * @throws GitException parsing error
     */
    public static String parsePath(@Nonnull String value) {
        Buf out = new Buf();
        Error.throwIfNeeded(jniParsePath(out, value));
        return out.getString()
                .orElseThrow(
                        () ->
                                new GitException(
                                        GitException.ErrorClass.CONFIG,
                                        "could not parse: " + value));
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    public void foreachMatch(String regexp, ForeachCb foreachCb) {
        jniForeachMatch(
                getRawPointer(), regexp, entryPtr -> foreachCb.accept(new Entry(true, entryPtr)));
    }

    /**
     * Add an on-disk config file instance to an existing config
     *
     * <p>The on-disk file pointed at by `path` will be opened and parsed; it's expected to be a
     * native Git config file following the default Git config syntax (see man git-config).
     *
     * <p>If the file does not exist, the file will still be added and it will be created the first
     * time we write to it.
     *
     * <p>Note that the configuration object will free the file automatically.
     *
     * <p>Further queries on this config object will access each of the config file instances in
     * order (instances with a higher priority level will be accessed first).
     *
     * @param path path to the configuration file to add
     * @param level the priority level of the backend
     * @param force replace config file at the given priority level
     * @param repo optional repository to allow parsing of conditional includes
     * @throws GitException 0 on success, GIT_EEXISTS when adding more than one file for a given
     *     priority level (and force_replace set to 0), GIT_ENOTFOUND when the file doesn't exist or
     *     error code
     */
    public void addFileOndisk(String path, ConfigLevel level, Repository repo, boolean force) {
        Error.throwIfNeeded(
                jniAddFileOndisk(
                        getRawPointer(),
                        path,
                        level._code,
                        repo == null ? 0 : repo.getRawPointer(),
                        force ? 1 : 0));
    }

    /**
     * Create a snapshot of the configuration
     *
     * <p>Create a snapshot of the current state of a configuration, which allows you to look into a
     * consistent view of the configuration for looking up complex values (e.g. a remote,
     * submodule).
     *
     * <p>The string returned when querying such a config object is valid until it is freed.
     *
     * @return configuration to snapshot
     * @throws GitException git errors
     */
    public Config snapshot() {
        Config cfg = new Config(false, 0);
        Error.throwIfNeeded(jniSnapshot(cfg._rawPtr, getRawPointer()));
        return cfg;
    }

    @Nonnull
    public Optional<Entry> getEntry(@Nonnull String name) {
        Entry entry = new Entry(false, 0);
        int e = jniGetEntry(entry._rawPtr, getRawPointer(), name);
        if (e == ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.of(entry);
    }

    @Nonnull
    public Optional<Integer> getInt(@Nonnull String name) {
        AtomicInteger out = new AtomicInteger();
        int e = jniGetInt32(out, getRawPointer(), name);
        if (e == ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.of(out.get());
    }

    @Nonnull
    public Optional<Long> getLong(@Nonnull String name) {
        AtomicLong out = new AtomicLong();
        int e = jniGetInt64(out, getRawPointer(), name);
        if (e == ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.of(out.get());
    }

    /**
     * Get the value of a boolean config variable.
     *
     * <p>All config files will be looked into, in the order of their defined level. A higher level
     * means a higher priority. The first occurrence of the variable will be returned here.
     *
     * @param name the variable's name
     * @return bool value of name or empty if not found
     * @throws GitException git errors
     */
    public Optional<Boolean> getBool(String name) {
        AtomicInteger out = new AtomicInteger();
        int e = jniGetBool(out, getRawPointer(), name);
        if (e == ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.of(out.get() != 0);
    }

    /**
     * Get the value of a path config variable.
     *
     * <p>A leading '~' will be expanded to the global search path (which defaults to the user's
     * home directory but can be overridden via `git_libgit2_opts()`.
     *
     * <p>All config files will be looked into, in the order of their defined level. A higher level
     * means a higher priority. The first occurrence of the variable will be returned here.
     *
     * @param name the variable's name
     * @return path
     * @throws GitException git errors
     */
    @Nonnull
    public Optional<String> getPath(@Nonnull String name) {
        Buf out = new Buf();
        int e = jniGetPath(out, getRawPointer(), name);
        if (e == ENOTFOUND.getCode()) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return out.getString();
    }

    /**
     * Get the value of a string config variable.
     *
     * <p>This function can only be used on snapshot config objects. The string is owned by the
     * config and should not be freed by the user. The pointer will be valid until the config is
     * freed.
     *
     * <p>All config files will be looked into, in the order of their defined level. A higher level
     * means a higher priority. The first occurrence of the variable will be returned here.
     *
     * @param name the variable's name
     * @return value of a string config variable
     */
    public Optional<String> getString(String name) {
        AtomicReference<String> out = new AtomicReference<>();
        int e = jniGetString(out, getRawPointer(), name);
        if (ENOTFOUND.getCode() == e) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return Optional.ofNullable(out.get());
    }

    /**
     * Get the value of a string config variable.
     *
     * <p>The value of the config will be copied into the buffer.
     *
     * <p>All config files will be looked into, in the order of their defined level. A higher level
     * means a higher priority. The first occurrence of the variable will be returned here.
     *
     * @param name the variable's name
     * @return value of string config variable
     * @throws GitException git errors
     */
    public Optional<String> getStringBuf(String name) {
        Buf outBuf = new Buf();
        int e = jniGetStringBuf(outBuf, getRawPointer(), name);
        if (ENOTFOUND.getCode() == e) {
            return Optional.empty();
        }
        Error.throwIfNeeded(e);
        return outBuf.getString();
    }

    /**
     * Get each value of a multivar in a foreach callback
     *
     * <p>The callback will be called on each variable found
     *
     * <p>The regular expression is applied case-sensitively on the normalized form of the variable
     * name: the section and variable parts are lower-cased. The subsection is left unchanged.
     *
     * @param name the variable's name
     * @param regexp regular expression to filter which variables we're interested in. Use NULL to
     *     indicate all
     * @param cb the function to be called on each value of the variable
     * @throws GitException git errors
     * @apiNote libgit2 api throws ENOTFOUND if name does not exist, this api does not.
     */
    public void getMultivarForeach(
            @Nonnull String name, @Nullable String regexp, @Nonnull ForeachCb cb) {
        int e =
                jniGetMultivarForeach(
                        getRawPointer(),
                        name,
                        regexp,
                        entryPtr -> cb.accept(new Entry(true, entryPtr)));
        if (e == ENOTFOUND.getCode()) {
            return;
        }
        Error.throwIfNeeded(e);
    }

    /**
     * Get each value of a multivar
     *
     * <p>The regular expression is applied case-sensitively on the normalized form of the variable
     * name: the section and variable parts are lower-cased. The subsection is left unchanged.
     *
     * @return Iterator
     * @param name the variable's name
     * @param regexp regular expression to filter which variables we're interested in. Use null to
     *     indicate all
     * @throws GitException git errors
     */
    @Nonnull
    Iterator multivarIteratorNew(@Nonnull String name, @Nullable String regexp) {
        Iterator out = new Iterator(false, 0);
        int e = jniMultivarIteratorNew(out._rawPtr, getRawPointer(), name, regexp);
        Error.throwIfNeeded(e);
        return out;
    }

    public void setInt(@Nonnull String name, int value) {
        Error.throwIfNeeded(jniSetInt32(getRawPointer(), name, value));
    }

    public void setLong(@Nonnull String name, long value) {
        Error.throwIfNeeded(jniSetInt64(getRawPointer(), name, value));
    }

    public void setBool(@Nonnull String name, boolean value) {
        Error.throwIfNeeded(jniSetBool(getRawPointer(), name, value ? 1 : 0));
    }

    public void setString(@Nonnull String name, @Nonnull String value) {
        Error.throwIfNeeded(jniSetString(getRawPointer(), name, value));
    }

    public void setMultivar(@Nonnull String name, @Nonnull String regexp, @Nonnull String value) {
        Error.throwIfNeeded(jniSetMultivar(getRawPointer(), name, regexp, value));
    }

    /**
     * get all values as set for multi valued config name
     *
     * @param name entry name
     * @param regexp fiter that value should match
     * @return list of values
     */
    public List<String> getMultivar(@Nonnull String name, @Nonnull String regexp) {
        List<String> out = new ArrayList<>();
        getMultivarForeach(
                name,
                regexp,
                entry -> {
                    out.add(entry.getValue());
                    return 0;
                });
        return out;
    }

    public void deleteEntry(@Nonnull String name) {
        Error.throwIfNeeded(jniDeleteEntry(getRawPointer(), name));
    }

    public void deleteMultivar(@Nonnull String name, @Nonnull String regexp) {
        Error.throwIfNeeded(jniDeleteMultivar(getRawPointer(), name, regexp));
    }

    public void foreach(ForeachCb cb) {
        int e = jniForeach(getRawPointer(), entryPtr -> cb.accept(new Entry(true, entryPtr)));
        Error.throwIfNeeded(e);
    }

    @Nonnull
    public Iterator iteratorNew() {
        Iterator out = new Iterator(false, 0);
        int e = jniIteratorNew(out._rawPtr, getRawPointer());
        Error.throwIfNeeded(e);
        return out;
    }

    @Nonnull
    public Iterator iteratorGlobalNew(@Nonnull String regexp) {
        Iterator out = new Iterator(false, 0);
        int e = jniIteratorGlobNew(out._rawPtr, getRawPointer(), regexp);
        Error.throwIfNeeded(e);
        return out;
    }

    @Nonnull
    Transaction lock() {
        Transaction out = new Transaction(true, 0);
        Error.throwIfNeeded(jniLock(out._rawPtr, getRawPointer()));
        return out;
    }

    public enum ConfigLevel {
        /** System-wide on Windows, for compatibility with portable git */
        PROGRAMDATA(1),

        /** System-wide configuration file; /etc/gitconfig on Linux systems */
        SYSTEM(2),

        /** XDG compatible configuration file; typically ~/.config/git/config */
        XDG(3),

        /**
         * User-specific configuration file (also called Global configuration file); typically
         * ~/.gitconfig
         */
        GLOBAL(4),

        /** Repository specific configuration file; $WORK_DIR/.git/config on non-bare repos */
        LOCAL(5),

        /**
         * Worktree specific configuration file; $GIT_DIR/config.worktree

         @since libgit2 1.8.0
         */
        WORKTREE(6),


        /** Application specific configuration file; freely defined by applications
         *
         * @since libgit2 1.8 change to 7, before is 6
         * */
        APP(7),

        /**
         * Represents the highest level available config file (i.e. the most specific config file
         * available that actually is loaded)
         */
        HIGHEST(-1),
        ;
        private final int _code;

        ConfigLevel(int code) {
            _code = code;
        }

        public int getValue(){
            return _code;
        }
    }

    @FunctionalInterface
    public interface ForeachCb {
        int accept(Entry entry);
    }

    @FunctionalInterface
    private interface CallbackJ {
        int accept(long entryPtr);
    }

    public static class Entry extends CAutoReleasable {
        protected Entry(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {

            // libgit2 1.9.0 say: "end-users should not try to free that structure"
            // see: https://github.com/libgit2/libgit2/releases/tag/v1.9.0 , section "Breaking changes", sub section "Configuration entry member removal (ABI breaking change)"
            // jniEntryFree(cPtr);
        }

        public String getName() {
            return jniEntryGetName(getRawPointer());
        }

        public String getValue() {
            return jniEntryGetValue(getRawPointer());
        }

        public int getIncludeDepth() {
            return jniEntryGetIncludeDepth(getRawPointer());
        }

        public int getLevel() {
            return jniEntryGetLevel(getRawPointer());
        }

        public String getBackendType(){
            return jniEntryGetBackendType(getRawPointer());
        }

        public String getOriginPath(){
            return jniEntryGetOriginPath(getRawPointer());
        }
    }

    /**
     * An opaque structure for a configuration iterator
     *
     * @implNote Prefer loop with callbacks like {@code foreach} and {@code foreachMatch} than using
     *     iterator
     */
    public static class Iterator extends CAutoReleasable {
        protected Iterator(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Override
        protected void freeOnce(long cPtr) {
            jniIteratorFree(cPtr);
        }

        @CheckForNull
        Entry next() {
            Entry entry = new Entry(true, 0);
            int e = jniNext(entry._rawPtr, getRawPointer());
            if (e == ITEROVER.getCode()) {
                return null;
            }
            Error.throwIfNeeded(e);
            return entry;
        }
    }
}
