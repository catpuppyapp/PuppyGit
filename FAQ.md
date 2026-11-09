## Question 17917109:
#### Q: Why my github password not work!?
#### A: If you enabled the 2fa, you must create a personal access token instead of password, or you can use ssh instead of https.
---
## Question 16544986:
#### Q: How committing all changes?
#### A: Two ways for it:
#### way 1: You can click top bar 3-dots icon then click "Commit All"
#### way 2: Long pressing the change list item to enable selection mode at ChangeList (btw: tap icon of item can enable it as well), then you can select all and commit/push/stage/revert/etc..., check the video: https://github.com/user-attachments/assets/32647093-9ee9-4eff-97e5-ee8ccf825105
---
## Question 17588197:
#### Q: How remove a file from git? (git rm --cached)
#### A: You can remove a file from git at Files or ChangeList view, check this: https://github.com/user-attachments/assets/b8fdd6ca-56c2-410a-b95b-6671a5b71fce
---
## Question 16683267:
#### Q: How ignore a file for repo? (git ignore)
#### A: You can ignore a file from Files or ChangeList view, check this: 
#### <img src=https://github.com/user-attachments/assets/933069c2-31d0-4e59-9338-d3c6e6700d89 width=30% >
---
## Question 11805957:
#### Q: How link credential and repo?
#### A: Actually the credential not linked to the repo, it linked to the remote of repo, the video show how to do it: https://github.com/user-attachments/assets/d9cda88e-b040-4f19-93d6-273f37d9f151
---
## Question 17853895:
#### Q: How restore a removed file?
#### A: You only can restore a file if git was tracked it and create a commit include it: 
#### method 1. You can find the commit which include the file then diff to local, then filter change list by file name, then you can restore it.
#### method 2. You can create a file at same path with same name in Files view, then click the 3-dots icon, You will see "File History", click it, you will see all reversions of the file, then you can choose a version to restore
---
## Question 13996879:
#### Q: How import an existed git repo?
#### A: You can import repos from Files view, check the video: https://github.com/user-attachments/assets/411c36af-36da-4c76-b82a-8c2fb92c065d
---
## Question 16107165:
#### Q: How pull/push when I entering/leaving my note app?
#### A: You can plan a pull/push when entering/leaving any apps, even PuppyGit self, check the video: <a href=https://www.patreon.com/posts/puppygit-auto-122757321>Auto Sync Obsidian Vault</a><br>
---
## Question 18189085:
#### Q: Why don't use sync instead of pull/push when entering/leaving an app?
#### A: In my opinion, for automation action of entering/leaving app, "enter then pull" + "leave then push", better than "enter then sync" and "leave then sync", cause when you entering, a push is nonsense, when you leaving, a pull may make sense but it may make you ignore some changes from remote, so, a better way is throw a pushing err when remote branch changed, then notice users to pull the changes by hand, and check the changes then decide push right now or update some files before push.
---
## Question 18454299:
#### Q: I need auto pull/push, but when I short-time leave the app, the auto pushing bother me!
#### A: You can set a delay at Automation view of PuppyGit, then it will auto push after you leave the app over the delay time.
---
## Question 19995900:
#### Q: I need auto pull/push, but when I short-time leave the app, the return, I don't want a pull again!
#### A: You can set a pull interval at Automation view of PuppyGit, then it will and only will do a auto pull after you leave the app over the interval.
---
## Question 11847801:
#### Q: What difference about fetch/merge/pull/push/sync? 
#### A: fetch is download changes from remote repo; merge is merge two branches, it may cause files change; pull is fetch then merge it with local branch; push is upload local branch to remote repo; sync is pull then push.
#### A: in my opinion, most time a separate pull and push better than a sync, cause the files may changed after a pull, so, after did a pull, you should check the changes then decide push right now or update some files before push.
---
## Question 15184822:
#### Q: How let Tasker schedule a pull/push via PuppyGit?
#### A: Go to Service view of PuppyGit, then go to app info view to allow PuppyGit run in background with no limit(depend system, maybe it called ignore battery optimize), then return PuppyGit and enable the service, it will start a http service, then you can call pull push from external apps,  e.g. tasker or even a browser, check the video: https://www.patreon.com/posts/puppygit-tasker-122757862
---
## Question 17073178:
#### Q: what is the `PuppyGit-Data` folder?
#### A: it saved app settings and file snapshot/tls certs/app settings
---
## Question 15011877:
#### Q: How I find the `PuppyGit-Data` folder?
#### A: Go to Files Page, click top right 3 dot menu, click `Internal Storage`, then you can find the `PuppyGit-Data` under this folder.
---
## Question 10052951:
#### Q: Can I delete the `PuppyGit-Data` folder?
#### A: You can, but better don't do that, it will delete the app settings also, if you want to clear cache of app, go to Setting Screen, and try clear cache
---
## Question 18437671:
#### Q: I enabled snapshot feature for Editor and Diff, How I found the snapshot files?
#### A: go to the path `PuppyGit-Data/FileSnapshot`, then you can filter your files by name, and the file name include a timestamp indicate when the file created
---
## Question 10725795:
#### Q: If I disabled the snapshot feature for Diff, and app crashed when I edit lines on the Diff screen, and my origin file got broken, how I restore it?
#### A: If your origin file broken, it means app crashing when try overwrite your origin file, so don't worry, it should has a tmp file include the content you was edited in the Diff screen, you can go to the Files screen, then go to `Internal Storage`, press back twice, then you see a `cache` folder, open it, you should see a file name like "diff__RLTF-YourFileName-some_random_str.tmp", then open the file, check the content, if good, you can use it replace your broken file. if you can't found any file in cache dir, sry, your data lost. You can enable file snapshot for diff for avoid it happens again.
---
## Question 14116556:
#### Q: I saw the `EditCache` in Settings Screen, what is used for?
#### A: if enable, it will cache your input text into the `PuppyGit-Data/EditCache` folder.
---
## Question 11674829:
#### Q: How I use self-signed cert for https?
#### A: Method 1: Put your cert into `AppData://cert-user` and restart app(kill the process by slip it out from the task manager or 'force stop' app in the app information screen)
#### note: You can copy and paste the path `AppData://cert-user` to "Go To" dialog of PuppyGit's Files view to direct jump to the folder.
#### A: Method 2: You can disable the ssl verify in settings page, but I don't recommend this method, due to it's unsafe, but if you are sure you want to disable it, check this issue: https://github.com/catpuppyapp/PuppyGit/issues/85#issuecomment-3166758525 
---
## Question 10997123:
#### Q: What the `master password` used for?
#### A: It used to encrypt your credentials, if you forgot it, can reset it, but all password/passphrase of your credentials will be invalid, you must re-set the `password/passphrase` filed for your credentials, else, it still can't decrypt by new master password, so, ofc, the encrypted password can't used for connect remote git repository as well.
---
## Question 19453181:
#### Q: If I don't use the `master password`, is dangerous?
#### A: Nah, actually, is not really very dangerous, but I can't promise, even your `password/passphrase` still will encrypted by default password, but if a hacker steal database from your phone, then, he can easy get the default password if he is a good hacker, and when he got the default password, he ofc can decrypt your `password/passphrase`. so, better set a master password and make sure you can remember it.



*** ***

