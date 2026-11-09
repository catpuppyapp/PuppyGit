package com.github.git24j.core;

import static com.github.git24j.core.GitException.ErrorCode.ENOTFOUND;

import java.util.EnumSet;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Submodule extends CAutoReleasable {
    /** int git_submodule_add_finalize(git_submodule *submodule); */
    static native int jniAddFinalize(long submodule);

    /**
     * int git_submodule_add_setup(git_submodule **out, git_repository *repo, const char *url, const
     * char *path, int use_gitlink);
     */
    static native int jniAddSetup(
            AtomicLong out, long repoPtr, String url, String path, int useGitlink);

    /** int git_submodule_add_to_index(git_submodule *submodule, int write_index); */
    static native int jniAddToIndex(long submodule, int writeIndex);

    /** const char * git_submodule_branch(git_submodule *submodule); */
    static native String jniBranch(long submodule);

    /**
     * int git_submodule_clone(git_repository **out, git_submodule *submodule, const
     * git_submodule_update_options *opts);
     */
    static native int jniClone(AtomicLong out, long submodule, long opts);

    /** git_submodule_recurse_t git_submodule_fetch_recurse_submodules(git_submodule *submodule); */
    static native int jniFetchRecurseSubmodules(long submodule);

    /**
     * int git_submodule_foreach(git_repository *repo, git_submodule_cb callback, void *payload);
     */
    static native int jniForeach(long repoPtr, Internals.SJCallback foreachCb);

    /** void git_submodule_free(git_submodule *submodule); */
    static native void jniFree(long submodule);

    /** const git_oid * git_submodule_head_id(git_submodule *submodule); */
    static native byte[] jniHeadId(long submodule);

    /** git_submodule_ignore_t git_submodule_ignore(git_submodule *submodule); */
    static native int jniIgnore(long submodule);

    /** const git_oid * git_submodule_index_id(git_submodule *submodule); */
    static native byte[] jniIndexId(long submodule);

    /** int git_submodule_init(git_submodule *submodule, int overwrite); */
    static native int jniInit(long submodule, int overwrite);

    /** int git_submodule_location(unsigned int *location_status, git_submodule *submodule); */
    static native int jniLocation(AtomicInteger locationStatus, long submodule);

    /** int git_submodule_lookup(git_submodule **out, git_repository *repo, const char *name); */
    static native int jniLookup(AtomicLong out, long repoPtr, String name);

    /** const char * git_submodule_name(git_submodule *submodule); */
    static native String jniName(long submodule);

    /** int git_submodule_open(git_repository **repo, git_submodule *submodule); */
    static native int jniOpen(AtomicLong repo, long submodule);

    /** git_repository * git_submodule_owner(git_submodule *submodule); */
    static native long jniOwner(long submodule);

    /** const char * git_submodule_path(git_submodule *submodule); */
    static native String jniPath(long submodule);

    /** int git_submodule_reload(git_submodule *submodule, int force); */
    static native int jniReload(long submodule, int force);

    /**
     * int git_submodule_repo_init(git_repository **out, const git_submodule *sm, int use_gitlink);
     */
    static native int jniRepoInit(AtomicLong out, long sm, int useGitlink);

    /** int git_submodule_resolve_url(git_buf *out, git_repository *repo, const char *url); */
    static native int jniResolveUrl(Buf out, long repoPtr, String url);

    /** int git_submodule_set_branch(git_repository *repo, const char *name, const char *branch); */
    static native int jniSetBranch(long repoPtr, String name, String branch);

    /**
     * int git_submodule_set_fetch_recurse_submodules(git_repository *repo, const char *name,
     * git_submodule_recurse_t fetch_recurse_submodules);
     */
    static native int jniSetFetchRecurseSubmodules(
            long repoPtr, String name, int fetchRecurseSubmodules);

    /**
     * int git_submodule_set_ignore(git_repository *repo, const char *name, git_submodule_ignore_t
     * ignore);
     */
    static native int jniSetIgnore(long repoPtr, String name, int ignore);

    /**
     * int git_submodule_set_update(git_repository *repo, const char *name, git_submodule_update_t
     * update);
     */
    static native int jniSetUpdate(long repoPtr, String name, int update);

    /** int git_submodule_set_url(git_repository *repo, const char *name, const char *url); */
    static native int jniSetUrl(long repoPtr, String name, String url);

    /**
     * int git_submodule_status(unsigned int *status, git_repository *repo, const char *name,
     * git_submodule_ignore_t ignore);
     */
    static native int jniStatus(AtomicInteger status, long repoPtr, String name, int ignore);

    /** int git_submodule_sync(git_submodule *submodule); */
    static native int jniSync(long submodule);

    /**
     * int git_submodule_update(git_submodule *submodule, int init, git_submodule_update_options
     * *options);
     */
    static native int jniUpdate(long submodule, int init, long options);

    /** int allow_fetch */
    static native int jniUpdateOptionsGetAllowFetch(long update_optionsPtr);

    /** git_checkout_options *checkout_opts */
    static native long jniUpdateOptionsGetCheckoutOpts(long update_optionsPtr);

    /** git_fetch_options *fetch_opts */
    static native long jniUpdateOptionsGetFetchOpts(long update_optionsPtr);

    /** unsigned int version */
    static native int jniUpdateOptionsGetVersion(long update_optionsPtr);

    static native int jniUpdateOptionsNew(AtomicLong outPtr, int version);

    /** int allow_fetch */
    static native void jniUpdateOptionsSetAllowFetch(long update_optionsPtr, int allowFetch);

    /** git_checkout_options *checkout_opts */
//    static native void jniUpdateOptionsSetCheckoutOpts(long update_optionsPtr, long checkoutOpts);

    /** git_submodule_update_init_options */
    static native int jniUpdateInitOptions(long updateOptionsPtr, int version);

    /** git_submodule_update_t git_submodule_update_strategy(git_submodule *submodule); */
    static native int jniUpdateStrategy(long submodule);

    /** const char * git_submodule_url(git_submodule *submodule); */
    static native String jniUrl(long submodule);

    /** const git_oid * git_submodule_wd_id(git_submodule *submodule); */
    static native byte[] jniWdId(long submodule);

    protected Submodule(boolean isWeak, long rawPtr) {
        super(isWeak, rawPtr);
    }

    /**
     * Iterate over all tracked submodules of a repository.
     *
     * <p>See the note on `git_submodule` above. This iterates over the tracked submodules as
     * described therein.
     *
     * <p>If you are concerned about items in the working directory that look like submodules but
     * are not tracked, the diff API will generate a diff record for workdir items that look like
     * submodules but are not tracked, showing them as added in the workdir. Also, the status API
     * will treat the entire subdirectory of a contained git repo as a single GIT_STATUS_WT_NEW
     * item.
     *
     * @param repo The repository
     * @param callback Function to be called with the name of each submodule. Return a non-zero
     *     value to terminate the iteration.
     * @throws GitException git errors
     * @apiNote {@code Submodule} captured in the callback is a weak reference and become unusable
     *     outside of the callback closure.
     */
    public static void foreach(@Nonnull Repository repo, @Nonnull Callback callback) {
        Error.throwIfNeeded(
                jniForeach(
                        repo.getRawPointer(),
                        (name, ptr) -> callback.accept(new Submodule(true, ptr), name)));
    }

    /**
     * Set up a new git submodule for checkout.
     *
     * <p>This does "git submodule add" up to the fetch and checkout of the submodule contents. It
     * preps a new submodule, creates an entry in .gitmodules and creates an empty initialized
     * repository either at the given path in the working directory or in .git/modules with a
     * gitlink from the working directory to the new repo.
     *
     * <p>To fully emulate "git submodule add" call this function, then open the submodule repo and
     * perform the clone step as needed. Lastly, call `git_submodule_add_finalize()` to wrap up
     * adding the new submodule and .gitmodules to the index to be ready to commit.
     *
     * <p>You must call `git_submodule_free` on the submodule object when done.
     *
     * @param repo The repository in which you want to create the submodule
     * @param url URL for the submodule's remote
     * @param path Path at which the submodule should be created
     * @param useGitlink Should workdir contain a gitlink to the repo in .git/modules vs. repo
     *     directly in workdir.
     * @return The newly created submodule ready to open for clone
     * @throws GitException GIT_EEXISTS if submodule already exists, -1 on other errors.
     */
    @Nonnull
    public static Submodule addSetup(
            @Nonnull Repository repo, @Nonnull String url, @Nonnull String path, boolean useGitlink) {
        Submodule out = new Submodule(false, 0);
        Error.throwIfNeeded(
                jniAddSetup(
                        out._rawPtr,
                        repo.getRawPointer(),
                        url,
                        path,
                        useGitlink ? 1 : 0));
        return out;
    }

    /**
     * Lookup submodule information by name or path.
     *
     * <p>Given either the submodule name or path (they are usually the same), this returns a
     * structure describing the submodule.
     *
     * <p>There are two expected error scenarios:
     *
     * <p>- The submodule is not mentioned in the HEAD, the index, and the config, but does "exist"
     * in the working directory (i.e. there is a subdirectory that appears to be a Git repository).
     * In this case, this function returns GIT_EEXISTS to indicate a sub-repository exists but not
     * in a state where a git_submodule can be instantiated. - The submodule is not mentioned in the
     * HEAD, index, or config and the working directory doesn't contain a value git repo at that
     * path. There may or may not be anything else at that path, but nothing that looks like a
     * submodule. In this case, this returns GIT_ENOTFOUND.
     *
     * <p>You must call `git_submodule_free` when done with the submodule.
     *
     * @param repo The parent repository
     * @param name The name of or path to the submodule; trailing slashes okay
     * @return submodule or empty if submodule does not exist.
     * @throws GitException GIT_EEXISTS if a repository is found in working directory only, -1 on
     *     other errors.
     */
    @Nullable
    public static Submodule lookup(@Nonnull Repository repo, @Nonnull String name) {
        Submodule out = new Submodule(false, 0);
        int e = jniLookup(out._rawPtr, repo.getRawPointer(), name);
        if (ENOTFOUND.getCode() == e) {
            return null;
        }
        Error.throwIfNeeded(e);
        return out;
    }

    /**
     * Resolve a submodule url relative to the given repository.
     *
     * @param repo Pointer to repository object
     * @param url Relative url
     * @return absolute submodule url
     * @throws GitException git errors
     * @throws IllegalArgumentException {@code url} cannot be resolved to an absolute path or the
     *     resolve string is nto a valid path.
     */
    @Nonnull
    public static String resolveUrl(@Nonnull Repository repo, @Nonnull String url) {
        Buf out = new Buf();
        Error.throwIfNeeded(jniResolveUrl(out, repo.getRawPointer(), url));
        return out.getString().orElseThrow(
                () -> new IllegalArgumentException(
                        String.format("'%s' cannot be resolved to an absolute url, check path please", url)
                )
        );
    }

    /**
     * Set the branch for the submodule in the configuration
     *
     * <p>After calling this, you may wish to call `git_submodule_sync()` to write the changes to
     * the checked out submodule repository.
     *
     * @param repo the repository to affect
     * @param name the name of the submodule to configure
     * @param branch Branch that should be used for the submodule
     * @throws GitException git errors
     */
    public static void setBranch(
            @Nonnull Repository repo, @Nonnull String name, @Nonnull String branch) {
        Error.throwIfNeeded(jniSetBranch(repo.getRawPointer(), name, branch));
    }

    /**
     * Set the fetchRecurseSubmodules rule for a submodule in the configuration
     *
     * <p>This setting won't affect any existing instances.
     *
     * @param repo the repository to affect
     * @param name the submodule to configure
     * @param fetchRecurseSubmodules recurse rule to set
     * @return old value for fetchRecurseSubmodules
     */
    @Nonnull
    public static RecurseT setFetchRecurseSubmodules(
            @Nonnull Repository repo,
            @Nonnull String name,
            @Nonnull RecurseT fetchRecurseSubmodules) {
        int e =
                jniSetFetchRecurseSubmodules(
                        repo.getRawPointer(), name, fetchRecurseSubmodules.getBit());
        return RecurseT.valueOf(e);
    }

    /**
     * Set the ignore rule for the submodule in the configuration
     *
     * <p>This does not affect any currently-loaded instances.
     *
     * @param repo the repository to affect
     * @param name the name of the submdule
     * @param ignore The new value for the ignore rule
     * @throws GitException git errors
     */
    public static void setIgnore(@Nonnull Repository repo, @Nonnull String name, boolean ignore) {
        Error.throwIfNeeded(jniSetIgnore(repo.getRawPointer(), name, ignore ? 1 : 0));
    }

    /**
     * Set the update rule for the submodule in the configuration
     *
     * <p>This setting won't affect any existing instances.
     *
     * @param repo the repository to affect
     * @param name the name of the submodule to configure
     * @param update The new value to use
     * @throws GitException git errors
     */
    public static void setUpdate(
            @Nonnull Repository repo, @Nonnull String name, @Nonnull UpdateT update) {
        Error.throwIfNeeded(jniSetUpdate(repo.getRawPointer(), name, update.getBit()));
    }

    /**
     * Set the URL for the submodule in the configuration
     *
     * <p>After calling this, you may wish to call `git_submodule_sync()` to write the changes to
     * the checked out submodule repository.
     *
     * @param repo the repository to affect
     * @param name the name of the submodule to configure
     * @param url URL that should be used for the submodule
     * @throws GitException git errors
     */
    public static void setUrl(@Nonnull Repository repo, @Nonnull String name, @Nonnull String url) {
        Error.throwIfNeeded(jniSetUrl(repo.getRawPointer(), name, url));
    }

    /**
     * Get the status for a submodule.
     *
     * <p>This looks at a submodule and tries to determine the status. It will return a combination
     * of the `GIT_SUBMODULE_STATUS` values above. How deeply it examines the working directory to
     * do this will depend on the `git_submodule_ignore_t` value for the submodule.
     *
     * @param repo the repository in which to look
     * @param name name of the submodule
     * @param ignore the ignore rules to follow
     * @return Combination of `GIT_SUBMODULE_STATUS` flags
     * @throws GitException git errors
     */
    public static EnumSet<StatusT> status(
            @Nonnull Repository repo, @Nonnull String name, @Nullable IgnoreT ignore) {
        AtomicInteger out = new AtomicInteger();
        Error.throwIfNeeded(
                jniStatus(
                        out,
                        repo.getRawPointer(),
                        name,
                        ignore == null ? IgnoreT.UNSPECIFIED.getBit() : ignore.getBit()));
        return IBitEnum.parse(out.get(), StatusT.class);
    }

    @Override
    protected void freeOnce(long cPtr) {
        jniFree(cPtr);
    }

    /**
     * Resolve the setup of a new git submodule.
     *
     * <p>This should be called on a submodule once you have called add setup and done the clone of
     * the submodule. This adds the .gitmodules file and the newly cloned submodule to the index to
     * be ready to be committed (but doesn't actually do the commit).
     *
     * @throws GitException git errors
     */
    public void addFinalize() {
        Error.throwIfNeeded(jniAddFinalize(getRawPointer()));
    }

    /**
     * Add current submodule HEAD commit to index of superproject.
     *
     * @param writeIndex Boolean if this should immediately write the index file. If you pass this
     *     as false, you will have to get the git_index and explicitly call `git_index_write()` on
     *     it to save the change.
     * @throws GitException git errors
     */
    public void addToIndex(boolean writeIndex) {
        Error.throwIfNeeded(jniAddToIndex(getRawPointer(), writeIndex ? 1 : 0));
    }

    /**
     * Get the branch for the submodule.
     *
     * @return Pointer to the submodule branch
     */
    @CheckForNull
    public String branch() {
        return jniBranch(getRawPointer());
    }

    /**
     * Read the fetchRecurseSubmodules rule for a submodule.
     *
     * <p>This accesses the submodule.<name>.fetchRecurseSubmodules value for the submodule that
     * controls fetching behavior for the submodule.
     *
     * <p>Note that at this time, libgit2 does not honor this setting and the fetch functionality
     * current ignores submodules.
     *
     * @return 0 if fetchRecurseSubmodules is false, 1 if true
     * @throws IllegalStateException if {@code fetchRecurseSubmodules} rule is not recognizable
     */
    @Nonnull
    public RecurseT fetchRecurseSubmodules() {
        int r = jniFetchRecurseSubmodules(getRawPointer());
        switch (r) {
            case 0:
                return RecurseT.NO;
            case 1:
                return RecurseT.YES;
            case 2:
                return RecurseT.ONDEMAN;
            default:
                throw new IllegalStateException(
                        "Illegal submodule.<submodule>.fetchRecurseSubmodules settings");
        }
    }

    /**
     * Get the OID for the submodule in the current HEAD tree.
     *
     * @return Pointer to git_oid or empty if submodule is not in the HEAD.
     */
    @CheckForNull
    public Oid headId() {
        return Oid.ofNullable(jniHeadId(getRawPointer()));
    }

    /**
     * Submodule ignore values
     *
     * <p>These values represent settings for the `submodule.$name.ignore` configuration value which
     * says how deeply to look at the working directory when getting submodule status.
     *
     * <p>You can override this value in memory on a per-submodule basis with
     * `git_submodule_set_ignore()` and can write the changed value to disk with
     * `git_submodule_save()`. If you have overwritten the value, you can revert to the on disk
     * value by using `GIT_SUBMODULE_IGNORE_RESET`.
     *
     * <p>The values are:
     *
     * <pre>
     * - GIT_SUBMODULE_IGNORE_UNSPECIFIED: use the submodule's configuration
     * - GIT_SUBMODULE_IGNORE_NONE: don't ignore any change - i.e. even an
     *   untracked file, will mark the submodule as dirty.  Ignored files are
     *   still ignored, of course.
     * - GIT_SUBMODULE_IGNORE_UNTRACKED: ignore untracked files; only changes
     *   to tracked files, or the index or the HEAD commit will matter.
     * - GIT_SUBMODULE_IGNORE_DIRTY: ignore changes in the working directory,
     *   only considering changes if the HEAD of submodule has moved from the
     *   value in the superproject.
     * - GIT_SUBMODULE_IGNORE_ALL: never check if the submodule is dirty
     * - GIT_SUBMODULE_IGNORE_DEFAULT: not used except as static initializer
     *   when we don't want any particular ignore rule to be specified.
     * </pre>
     */
    public IgnoreT ignore() {
        return IBitEnum.valueOf(jniIgnore(getRawPointer()), IgnoreT.class);
    }

    /**
     * Get the OID for the submodule in the index.
     *
     * @return Pointer to git_oid or NULL if submodule is not in index.
     */
    @CheckForNull
    public Oid indexId() {
        return Oid.ofNullable(jniIndexId(getRawPointer()));
    }

    /**
     * Copy submodule info into ".git/config" file.
     *
     * <p>Just like "git submodule init", this copies information about the submodule into
     * ".git/config". You can use the accessor functions above to alter the in-memory git_submodule
     * object and control what is written to the config, overriding what is in .gitmodules.
     *
     * @param overwrite By default, existing entries will not be overwritten, but setting this to
     *     true forces them to be updated.
     * @throws GitException git errors
     */
    public void init(boolean overwrite) {
        Error.throwIfNeeded(jniInit(getRawPointer(), overwrite ? 1 : 0));
    }

    /**
     * Get the locations of submodule information.
     *
     * <p>This is a bit like a very lightweight version of `git_submodule_status`. It just returns a
     * made of the first four submodule status values (i.e. the ones like
     * GIT_SUBMODULE_STATUS_IN_HEAD, etc) that tell you where the submodule data comes from (i.e.
     * the HEAD commit, gitmodules file, etc.). This can be useful if you want to know if the
     * submodule is present in the working directory at this point in time, etc.
     *
     * @return set of submodule status enums
     * @throws GitException git errors
     */
    @Nonnull
    public EnumSet<StatusT> location() {
        AtomicInteger out = new AtomicInteger();
        Error.throwIfNeeded(jniLocation(out, getRawPointer()));
        return IBitEnum.parse(out.get(), StatusT.class);
    }

    /** @return Get the name of submodule. */
    @Nonnull
    public String name() {
        return jniName(getRawPointer());
    }

    /**
     * Open the repository for a submodule.
     *
     * <p>This is a newly opened repository object. The caller is responsible for calling
     * `git_repository_free()` on it when done. Multiple calls to this function will return distinct
     * `git_repository` objects. This will only work if the submodule is checked out into the
     * working directory.
     *
     * @return the submodule repo which was opened
     * @throws GitException git errors
     */
    @Nonnull
    public Repository open() {
        Repository repo = new Repository(0);
        Error.throwIfNeeded(jniOpen(repo._rawPtr, getRawPointer()));
        return repo;
    }

    /**
     * Get the containing repository for a submodule.
     *
     * <p>This returns a pointer to the repository that contains the submodule. This is a just a
     * reference to the repository that was passed to the original `git_submodule_lookup()` call, so
     * if that repository has been freed, then this may be a dangling reference.
     *
     * @return submodule repository
     */
    @Nonnull
    public Repository owner() {
        return new Repository(jniOwner(getRawPointer()));
    }

    /**
     * Get the path to the submodule.
     *
     * <p>The path is almost always the same as the submodule name, but the two are actually not
     * required to match.
     *
     * @return submodule path
     */
    @Nonnull
    public String path() {
        return jniPath(getRawPointer());
    }

    /**
     * Reread submodule info from config, index, and HEAD.
     *
     * <p>Call this to reread cached submodule information for this submodule if you have reason to
     * believe that it has changed.
     *
     * @param force Force reload even if the data doesn't seem out of date
     * @throws GitException git errors
     */
    public void reload(boolean force) {
        Error.throwIfNeeded(jniReload(getRawPointer(), force ? 1 : 0));
    }

    /**
     * Set up the subrepository for a submodule in preparation for clone.
     *
     * <p>This function can be called to init and set up a submodule repository from a submodule in
     * preparation to clone it from its remote.
     *
     * @param useGitlink Should the workdir contain a gitlink to the repo in .git/modules vs. repo
     *     directly in workdir.
     * @return submodule repository
     * @throws GitException git errors
     */
    @Nonnull
    public Repository repoInit(boolean useGitlink) {
        Repository out = new Repository(0);
        Error.throwIfNeeded(jniRepoInit(out._rawPtr, getRawPointer(), useGitlink ? 1 : 0));
        return out;
    }

    /**
     * Copy submodule remote info into submodule repo.
     *
     * <p>This copies the information about the submodules URL into the checked out submodule
     * config, acting like "git submodule sync". This is useful if you have altered the URL for the
     * submodule (or it has been altered by a fetch of upstream changes) and you need to update your
     * local repo.
     *
     * @throws GitException git errors
     */
    public void sync() {
        Error.throwIfNeeded(jniSync(getRawPointer()));
    }

    /**
     * Update a submodule. This will clone a missing submodule and checkout the subrepository to the
     * commit specified in the index of the containing repository. If the submodule repository
     * doesn't contain the target commit (e.g. because fetchRecurseSubmodules isn't set), then the
     * submodule is fetched using the fetch options supplied in options.
     *
     * @param init If the submodule is not initialized, setting this flag to true will initialize
     *     the submodule before updating. Otherwise, this will return an error if attempting to
     *     update an uninitialzed repository. but setting this to true forces them to be updated.
     * @param options configuration options for the update. If NULL, the function works as though
     *     GIT_SUBMODULE_UPDATE_OPTIONS_INIT was passed.
     * @throws GitException git errors
     */
    public void update(boolean init, @Nullable UpdateOptions options) {
        Error.throwIfNeeded(
                jniUpdate(
                        getRawPointer(),
                        init ? 1 : 0,
                        options == null ? 0 : options.getRawPointer()));
    }

    /**
     * Get the update rule that will be used for the submodule.
     *
     * <p>This value controls the behavior of the `git submodule update` command. There are four
     * useful values documented with `git_submodule_update_t`.
     *
     * @return The current git_submodule_update_t value that will be used for this submodule.
     */
    @Nonnull
    public UpdateT updateStrategy() {
        int r = jniUpdateStrategy(getRawPointer());
        return IBitEnum.valueOf(r, UpdateT.class, UpdateT.DEFAULT);
    }

    /**
     * Get the URL for the submodule.
     *
     * @return the submodule url
     */
    @Nullable
    public String url() {
        return jniUrl(getRawPointer());
    }

    /**
     * Get the OID for the submodule in the current working directory.
     *
     * <p>This returns the OID that corresponds to looking up 'HEAD' in the checked out submodule.
     * If there are pending changes in the index or anything else, this won't notice that. You
     * should call `git_submodule_status()` for a more complete picture about the state of the
     * working directory.
     *
     * @return submodule oid or empty if submodule is not checked out.
     */
    @Nullable
    public Oid wdId() {
        return Optional.ofNullable(jniWdId(getRawPointer())).map(Oid::of).orElse(null);
    }

    /**
     * Perform the clone step for a newly created submodule.
     *
     * <p>This performs the necessary `git_clone` to setup a newly-created submodule.
     *
     * @param updateOptions The options to use.
     * @return The newly created repository object. Optional.
     */
    @Nonnull
    public Repository clone(@Nullable UpdateOptions updateOptions) {
        Repository out = new Repository(0);
        int e =
                jniClone(
                        out._rawPtr,
                        getRawPointer(),
                        updateOptions == null ? 0 : updateOptions.getRawPointer());
        Error.throwIfNeeded(e);
        return out;
    }

    /**
     * Return codes for submodule status.
     *
     * <p>A combination of these flags will be returned to describe the status of a submodule.
     * Depending on the "ignore" property of the submodule, some of the flags may never be returned
     * because they indicate changes that are supposed to be ignored.
     *
     * <p>Submodule info is contained in 4 places: the HEAD tree, the index, config files (both
     * .git/config and .gitmodules), and the working directory. Any or all of those places might be
     * missing information about the submodule depending on what state the repo is in. We consider
     * all four places to build the combination of status flags.
     *
     * <p>There are four values that are not really status, but give basic info about what sources
     * of submodule data are available. These will be returned even if ignore is set to "ALL".
     *
     * <pre>
     * * IN_HEAD   - superproject head contains submodule
     * * IN_INDEX  - superproject index contains submodule
     * * IN_CONFIG - superproject gitmodules has submodule
     * * IN_WD     - superproject workdir has submodule
     *
     * The following values will be returned so long as ignore is not "ALL".
     *
     * * INDEX_ADDED       - in index, not in head
     * * INDEX_DELETED     - in head, not in index
     * * INDEX_MODIFIED    - index and head don't match
     * * WD_UNINITIALIZED  - workdir contains empty directory
     * * WD_ADDED          - in workdir, not index
     * * WD_DELETED        - in index, not workdir
     * * WD_MODIFIED       - index and workdir head don't match
     *
     * The following can only be returned if ignore is "NONE" or "UNTRACKED".
     *
     * * WD_INDEX_MODIFIED - submodule workdir index is dirty
     * * WD_WD_MODIFIED    - submodule workdir has modified files
     *
     * Lastly, the following will only be returned for ignore "NONE".
     *
     * * WD_UNTRACKED      - wd contains untracked files
     * </pre>
     */
    public enum StatusT implements IBitEnum {
        IN_HEAD(1 << 0),
        IN_INDEX(1 << 1),
        IN_CONFIG(1 << 2),
        IN_WD(1 << 3),
        INDEX_ADDED(1 << 4),
        INDEX_DELETED(1 << 5),
        INDEX_MODIFIED(1 << 6),
        WD_UNINITIALIZED(1 << 7),
        WD_ADDED(1 << 8),
        WD_DELETED(1 << 9),
        WD_MODIFIED(1 << 10),
        WD_INDEX_MODIFIED(1 << 11),
        WD_WD_MODIFIED(1 << 12),
        WD_UNTRACKED(1 << 13);
        private final int _bit;

        StatusT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    /**
     * Options for submodule recurse.
     *
     * <p>Represent the value of `submodule.$name.fetchRecurseSubmodules`
     *
     * <pre>
     * * GIT_SUBMODULE_RECURSE_NO    - do no recurse into submodules
     * * GIT_SUBMODULE_RECURSE_YES   - recurse into submodules
     * * GIT_SUBMODULE_RECURSE_ONDEMAND - recurse into submodules only when
     *                                    commit not already in local clone
     * </pre>
     */
    public enum RecurseT implements IBitEnum {
        NO(0),
        YES(1),
        ONDEMAN(2);
        private final int _bit;

        RecurseT(int bit) {
            _bit = bit;
        }

        static RecurseT valueOf(int val) {
            switch (val) {
                case 1:
                    return YES;
                case 0:
                    return NO;
                default:
                    return ONDEMAN;
            }
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    /**
     * Submodule ignore values
     *
     * <p>These values represent settings for the `submodule.$name.ignore` configuration value which
     * says how deeply to look at the working directory when getting submodule status.
     *
     * <p>You can override this value in memory on a per-submodule basis with
     * `git_submodule_set_ignore()` and can write the changed value to disk with
     * `git_submodule_save()`. If you have overwritten the value, you can revert to the on disk
     * value by using `GIT_SUBMODULE_IGNORE_RESET`.
     *
     * <p>The values are:
     *
     * <pre>
     * - GIT_SUBMODULE_IGNORE_UNSPECIFIED: use the submodule's configuration
     * - GIT_SUBMODULE_IGNORE_NONE: don't ignore any change - i.e. even an
     *   untracked file, will mark the submodule as dirty.  Ignored files are
     *   still ignored, of course.
     * - GIT_SUBMODULE_IGNORE_UNTRACKED: ignore untracked files; only changes
     *   to tracked files, or the index or the HEAD commit will matter.
     * - GIT_SUBMODULE_IGNORE_DIRTY: ignore changes in the working directory,
     *   only considering changes if the HEAD of submodule has moved from the
     *   value in the superproject.
     * - GIT_SUBMODULE_IGNORE_ALL: never check if the submodule is dirty
     * - GIT_SUBMODULE_IGNORE_DEFAULT: not used except as static initializer
     *   when we don't want any particular ignore rule to be specified.
     * </pre>
     */
    public enum IgnoreT implements IBitEnum {
        /** < use the submodule's configuration */
        UNSPECIFIED(-1),
        /** < any change or untracked == dirty */
        NONE(1),
        /** < dirty if tracked files change */
        UNTRACKED(2),
        /** < only dirty if HEAD moved */
        DIRTY(3),
        /** < never dirty */
        ALL(4),
        ;
        private final int _bit;

        IgnoreT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    /**
     * Submodule update values
     *
     * <p>These values represent settings for the `submodule.$name.update` configuration value which
     * says how to handle `git submodule update` for this submodule. The value is usually set in the
     * ".gitmodules" file and copied to ".git/config" when the submodule is initialized.
     *
     * <p>You can override this setting on a per-submodule basis with `git_submodule_set_update()`
     * and write the changed value to disk using `git_submodule_save()`. If you have overwritten the
     * value, you can revert it by passing `GIT_SUBMODULE_UPDATE_RESET` to the set function.
     *
     * <p>The values are:
     *
     * <pre>
     *     - GIT_SUBMODULE_UPDATE_CHECKOUT: the default; when a submodule is updated, checkout
     *       the new detached HEAD to the submodule directory.
     *     - GIT_SUBMODULE_UPDATE_REBASE: update by rebasing the current checked out branch onto
     *       the commit from the superproject.
     *     - GIT_SUBMODULE_UPDATE_MERGE: update by merging the commit in the superproject into
     *       the current checkout out branch of the submodule.
     *     - GIT_SUBMODULE_UPDATE_NONE: do not update this submodule even when the commit in the
     *       superproject is updated.
     *     - GIT_SUBMODULE_UPDATE_DEFAULT: not used except as static initializer when we don't want
     *        any particular update rule to be specified.
     * </pre>
     */
    public enum UpdateT implements IBitEnum {
        CHECKOUT(1),
        REBASE(2),
        MERGE(3),
        NONE(4),
        DEFAULT(0);
        private final int _bit;

        UpdateT(int bit) {
            _bit = bit;
        }

        @Override
        public int getBit() {
            return _bit;
        }
    }

    @FunctionalInterface
    public interface Callback {
        /**
         * Function pointer to receive each submodule
         *
         * @param sm git_submodule currently being visited
         * @param name name of the submodule
         * @return 0 on success or error code
         */
        int accept(Submodule sm, String name);
    }

    public static class UpdateOptions extends CAutoReleasable {
        public static final int VERSION = 1;

        protected UpdateOptions(boolean isWeak, long rawPtr) {
            super(isWeak, rawPtr);
        }

        @Nonnull
        public static UpdateOptions create(int version) {
            UpdateOptions opts = new UpdateOptions(false, 0);
            Error.throwIfNeeded(jniUpdateOptionsNew(opts._rawPtr, version));
            return opts;
        }

        public static UpdateOptions createDefault() {
            return create(VERSION);
        }

        @Override
        protected void freeOnce(long cPtr) {
            Libgit2.jniShadowFree(cPtr);
        }

        /** unsigned int version */
        public int getVersion() {
            return jniUpdateOptionsGetVersion(getRawPointer());
        }

        /** git_checkout_options *checkout_opts */
        @Nonnull
        public Checkout.Options getCheckoutOpts() {
            long ptr = jniUpdateOptionsGetCheckoutOpts(getRawPointer());
            return new Checkout.Options(true, ptr);
        }

        /** git_fetch_options *fetch_opts */
        @Nonnull
        public FetchOptions getFetchOpts() {
            long ptr = jniUpdateOptionsGetFetchOpts(getRawPointer());
            return new FetchOptions(true, ptr);
        }

        /** int allow_fetch */
        public boolean getAllowFetch() {
            return jniUpdateOptionsGetAllowFetch(getRawPointer()) != 0;
        }

        /** int allow_fetch */
        public void setAllowFetch(boolean allowFetch) {
            jniUpdateOptionsSetAllowFetch(getRawPointer(), allowFetch ? 1 : 0);
        }
    }
}
