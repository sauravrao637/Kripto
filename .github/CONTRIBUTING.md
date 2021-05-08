Kripto contribution guidelines
===

### Code practices

Following the best practice to make it easy for the reviewer as well as the contributor. 

 * Single commit per pull request.
 * Reference the issue numbers in the commit message. Follow the pattern ``` Fixes #<issue number> <commit message>```
 * Follow uniform design practices. The design language must be consistent throughout the app.
 * The pull request will not get merged until and unless the commits are squashed. In case there are multiple commits on the PR, the commit author needs to squash them.
 * If the PR is related to any front end change, please attach relevant screenshots in the pull request description.

#### How to `git squash`?

As a tip for new developers those who are new with squashing commits into one, multiple commits may appear in your pull request mostly due to following reasons.

 * Intentionally adding multiple commit messages after each change without just `git add`ing.
 * Updating the current branch with the remote so a merge commit takes place.

Despite any reason, follow the steps given below to squash all commits into one adhering to our best practices.

 * Setup remote to upstream branch if not set before

`$ git remote add upstream https://github.com/fossasia/pslab-android.git`

 * Check into the branch related to the pull request

`$ git checkout <branch-name>`

 * Perform a soft reset to retain the changes while removing all the commit details

`$ git reset --soft upstream/development`

 * Add files to the staging area

`$ git add <file paths or "." to add everything>`

 * Create a new commit with a proper message following commit message guidelines

`$ git commit -m "tag: commit message"`

 * If you have already made a pull request

`$ git push -f origin <branch-name>`

### Branch Policy

We have the following branches
* **development** All development goes on in this branch. If you're making a contribution, you are supposed to make a pull request to _development_. Make sure it passes a build check on Travis.
* **master** This contains the stable code. After significant features/bugfixes are accumulated on development, we move it to master.

### Code style

Please try to follow the mentioned guidelines while writing and submitting your code as it makes easier for the reviewer and other developers to understand.

 * While naming the layout files, ensure that the convention followed is (activity/fragment) _ (name).xml like ```activity_oscilloscope.xml``` , ```frag_control_main.xml``` .
 * Name the views and widgets defined in the layout files as (viewtype/widget) _ (fragment/activity name) _ (no. in the file) like ```spinner_channel_select_la1``` , ```button_activity_oscilloscope1``` .
 * The activity/fragment file name corresponding to the layout files should be named as                       (activity/fragment name)(activity/fragment).java like ```FragChannelsParameter.java``` corresponding to the layout file ```frag_channels_parameter.xml``` .


## Developers

### Maintainers
The project is maintained by
- Saurav Rao ([@sauravrao637](https://github.com/sauravrao637))

