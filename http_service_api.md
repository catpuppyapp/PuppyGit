## Video tutorials
<a href="https://www.patreon.com/posts/puppygit-tasker-122757862">Enable http service and request pull/push by Tasker when entering/leaving an app</a><br>


**Note: You can use other automation tools that support sending HTTP requests instead of Tasker.**<br>
**Note: If has conflicts, you must go to PuppyGit resolve and stage them, else you will not be able pull/push by the http api.**

---

## Rules of token and ip whitelist:
1. Every request must with the token and the ip of client must in the ip white list.
2. if token or ip list empty, all requests will be rejected
3. if you want to allow all requests ip, use '*' to match all
4. if the host set to 0.0.0.0 will allow any ip of your device to access the service, else will only allow request to specified ip


### You must know if you allow external network access the http service, your traffics are plain text, everyone on your route link can see what you transferred if they want.

### You can go to `PuppyGit -> Service` to manage your tokens and ip whitelist  

---

## Quick Start:
pull a repo: http://127.0.0.1:52520/pull?token=replace_to_your_token&repoNameOrId=yourRepoName

push a repo: http://127.0.0.1:52520/push?token=replace_to_your_token&repoNameOrId=yourRepoName

sync a repo(pull then push): http://127.0.0.1:52520/sync?token=replace_to_your_token&repoNameOrId=yourRepoName

pull multi repos: http://127.0.0.1:52520/pull?token=replace_to_your_token&repoNameOrId=Repo1&repoNameOrId=Repo2&&repoNameOrId=Repo3

push multi repos: http://127.0.0.1:52520/push?token=replace_to_your_token&repoNameOrId=Repo1&repoNameOrId=Repo2&&repoNameOrId=Repo3

sync multi repos: http://127.0.0.1:52520/sync?token=replace_to_your_token&repoNameOrId=Repo1&repoNameOrId=Repo2&&repoNameOrId=Repo3

pull all repos: http://127.0.0.1:52520/pullAll?token=replace_to_your_token

push all repos: http://127.0.0.1:52520/pushAll?token=replace_to_your_token

sync all repos: http://127.0.0.1:52520/syncAll?token=replace_to_your_token

---

## Tasker Integration:
You can start the Service in PuppyGit, and use automation tools like Tasker to send http request to do some task, e.g. schedule a sync for the repos and/or auto pull/push when enter/exit specified apps.

btw: if you expect to auto pull/push when enter/exit specified apps, no more 3rd apps require, PuppyGit already included this feature, just go to the 'Automation' Screen in PuppyGit, and select apps and link repos, then PuppyGit will do pull when enter selected app, and do push after leave it.

---

## Apis:
### path: /pull

protocol: http

Http method: GET

params:
- repoNameOrId: repo name or ids, match by name first, if none, will match by id. can have multi repo name or ids, will do action to the list which matched with the repo name or ids and ignore invalids
- gitUsername: using for create commit, if not pass this param, will use PuppyGit settings
- gitEmail: using for create commit, if not pass this param, will use PuppyGit settings
- forceUseIdMatchRepo: 1 enable or 0 disable, default 0, if enable, will force match repo by repo id, else will match by name first, if no match, then match by id
- token: a valid token in your token list
- pullWithRebase: 1 or 0, if 1, will use rebase when pulling, else merge, if omitted, will follow settings
- async: 1 enable or 0 disable, default 1 (for backward-compatible), if enable, will start task then return response immediately, else will waiting for task finished

example:<br>
http://127.0.0.1/pull?repoNameOrId=your_repo_name&token=your_token

http://127.0.0.1/pull?repoNameOrId=your_repo_name&repoNameOrId=another_repoName&repoNameOrId=another_repoId&token=your_token

http://127.0.0.1/pull?repoNameOrId=your_repo_name&token=your_token&gitUsername=your_git_username&gitEmail=your_email@example.com



### path: /push

protocol: http

Http method: GET

params:
- repoNameOrId: repo name or ids, match by name first, if none, will match by id. can have multi repo name or ids, will do action to the list which matched with the repo name or ids and ignore invalids
- gitUsername: using for create commit, if not pass this param, will use PuppyGit settings
- gitEmail: using for create commit, if not pass this param, will use PuppyGit settings
- force: force push, 1 enable , 0 disable, default 0
- forceUseIdMatchRepo: 1 enable or 0 disable, default 0, if enable, will force match repo by repo id, else will match by name first, if no match, then match by id
- token: a valid token in your token list
- autoCommit: 1 enable or 0 disable, default 1: if enable and no conflict items exists, will auto commit all changes, and will check index, if index empty, will not pushing; if disable, will only do push, no commit changes, no index empty check, no conflict items check.
- async: 1 enable or 0 disable, default 1 (for backward-compatible), if enable, will start task then return response immediately, else will waiting for task finished
- resetIfErr: one of values: soft/mixed/hard, any other values will disable this feature, default is null(disable reset if err)
- cmtMsgPrefix: prefix will add to auto generated commit msg, default is null(no prefix will add)

example:<br>
http://127.0.0.1/push?repoNameOrId=your_repo_name&token=your_token

http://127.0.0.1/push?repoNameOrId=your_repo_name&repoNameOrId=another_repoName&repoNameOrId=another_repoId&token=your_token

http://127.0.0.1/push?repoNameOrId=your_repo_name&token=your_token&gitUsername=your_git_user_name&gitEmail=your_email@example.com



### path: /sync

protocol: http

Http method: GET

params:
- repoNameOrId: repo name or ids, match by name first, if none, will match by id. can have multi repo name or ids, will do action to the list which matched with the repo name or ids and ignore invalids
- gitUsername: using for create commit, if not pass this param, will use PuppyGit settings
- gitEmail: using for create commit, if not pass this param, will use PuppyGit settings
- force: force push, 1 enable , 0 disable, default 0
- forceUseIdMatchRepo: 1 enable or 0 disable, default 0, if enable, will force match repo by repo id, else will match by name first, if no match, then match by id
- token: a valid token in your token list
- autoCommit: 1 enable or 0 disable, default 1: if enable and no conflict items exists, will auto commit all changes, and will check index, if index empty, will not pushing; if disable, will only do push, no commit changes, no index empty check, no conflict items check.
- pullWithRebase: 1 or 0, if 1, will use rebase when pulling, else merge, if omitted, will follow settings
- async: 1 enable or 0 disable, default 1 (for backward-compatible), if enable, will start task then return response immediately, else will waiting for task finished
- resetIfErr: one of values: soft/mixed/hard, any other values will disable this feature, default is null(disable reset if err)
- cmtMsgPrefix: prefix will add to auto generated commit msg, default is null(no prefix will add)

example:<br>
http://127.0.0.1/sync?repoNameOrId=your_repo_name&token=your_token

http://127.0.0.1/sync?repoNameOrId=your_repo_name&repoNameOrId=another_repoName&repoNameOrId=another_repoId&token=your_token

http://127.0.0.1/sync?repoNameOrId=your_repo_name&token=your_token&gitUsername=your_git_user_name&gitEmail=your_email@example.com




### path: /pullAll

protocol: http

Http method: GET

params:
- gitUsername: using for create commit, if not pass this param, will use PuppyGit settings
- gitEmail: using for create commit, if not pass this param, will use PuppyGit settings
- token: a valid token in your token list
- pullWithRebase: 1 or 0, if 1, will use rebase when pulling, else merge, if omitted, will follow settings
- async: 1 enable or 0 disable, default 1 (for backward-compatible), if enable, will start task then return response immediately, else will waiting for task finished


example:<br>
http://127.0.0.1/pullAll?token=your_token

http://127.0.0.1/pullAll?token=your_token&gitUsername=your_git_username&gitEmail=your_email@example.com



### path: /pushAll

protocol: http

Http method: GET

params:
- gitUsername: using for create commit, if not pass this param, will use PuppyGit settings
- gitEmail: using for create commit, if not pass this param, will use PuppyGit settings
- force: force push, 1 enable , 0 disable, default 0
- autoCommit: 1 enable or 0 disable, default 1: if enable and no conflict items exists, will auto commit all changes, and will check index, if index empty, will not pushing; if disable, will only do push, no commit changes, no index empty check, no conflict items check.
- token: a valid token in your token list
- async: 1 enable or 0 disable, default 1 (for backward-compatible), if enable, will start task then return response immediately, else will waiting for task finished
- resetIfErr: one of values: soft/mixed/hard, any other values will disable this feature, default is null(disable reset if err)
- cmtMsgPrefix: prefix will add to auto generated commit msg, default is null(no prefix will add)


example:<br>
http://127.0.0.1/pushAll?token=your_token

http://127.0.0.1/pushAll?token=your_token&gitUsername=your_git_username&gitEmail=your_email@example.com



### path: /syncAll

protocol: http

Http method: GET

params:
- gitUsername: using for create commit, if not pass this param, will use PuppyGit settings
- gitEmail: using for create commit, if not pass this param, will use PuppyGit settings
- force: force push, 1 enable , 0 disable, default 0
- autoCommit: 1 enable or 0 disable, default 1: if enable and no conflict items exists, will auto commit all changes, and will check index, if index empty, will not pushing; if disable, will only do push, no commit changes, no index empty check, no conflict items check.
- token: a valid token in your token list
- pullWithRebase: 1 or 0, if 1, will use rebase when pulling, else merge, if omitted, will follow settings
- async: 1 enable or 0 disable, default 1 (for backward-compatible), if enable, will start task then return response immediately, else will waiting for task finished
- resetIfErr: one of values: soft/mixed/hard, any other values will disable this feature, default is null(disable reset if err)
- cmtMsgPrefix: prefix will add to auto generated commit msg, default is null(no prefix will add)


example:<br>
http://127.0.0.1/syncAll?token=your_token

http://127.0.0.1/syncAll?token=your_token&gitUsername=your_git_username&gitEmail=your_email@example.com

