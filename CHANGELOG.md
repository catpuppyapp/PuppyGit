next version :
- maybe will support termux git cli, I have no any test for now, if possible, it will be able access git cli provide by termux in puppy git

1.0.6.0v36 - 20241101:
- fix Difference Screen crash

1.0.5.9v35 - 20241031:
- update git24j libs


1.0.5.8v34 - 20241030:
- support squash commits
- Improve view differences performance, now very fast


1.0.5.7v33 - 20241029:
- Optimize page scroll performance
- Editor, resolve conflict optimize: 
  - ours/theirs now has background color
  - add accept ours/theirs buttons
- can switch Prev/Next File in DiffScreen
- adjust go to top fab:
    - can permanent hide from settings
    - can temporary hide
    - support go to bottom
- fixed bug: commit history list omit commits
- able to copy clone err msg by long pressing the err msg on err repo card
- show last modified time in Files(selected single file) and Editor(clicked title, show file details)


1.0.5.6v32 - 20241017:
- fixed few minor bugs
- improved chinese translation


1.0.5.5v31 - 20241016:
- rename app name to PuppyGit, no more Pro suffix ever
- support chinese
- add Settings page


1.0.5.4v30 - 20241013:<br>
ignore file change:
- change comment start sign form "#" to "//"
- deprecated "ignores.txt", instead by "ignore_v2.txt", if users was use puppy git ignore feature, need re-ignore files


1.0.5.3v29 - 20241011:
- fixed Files Page loading very slow
- fixed Files Page open a unreadable dir when first launch app


1.0.5.2v28 - 20241011:
- Files page support go to ChangeList or Repos
- other minor bugs fixed


1.0.5.1v27 - 20241008:
- support submodules
- support init dir as git repo at Files page
- support ignore files at ChangeList page (git status for worktree to index)


1.0.5v26 - 20241005:
- settings file move to user-visible PuppyGit-Data folder
    * NOTICE: if you upgrade app from old version, below settings will lost, you can set it again, will save to new settings:
        * Editor Page: font size/show or hide line number/files last edited positions
        * Global Git username and email
        * Storage Paths
- dozen bugs fixed


1.0.4v25:
- important update, unlock all features


1.0.3.2v24:
- support clone repo to external storage (need grant manage storage permission, if don't grant, still can clone in to app internal storage like old versions)
- enable better compare method
- enable shallow clone (if make repo corrupted, please re-clone full repo)


1.0.3.1v23:
- open source

