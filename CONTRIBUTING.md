# How to Contribute

First of all, thanks for considering to contribute. We appreciate the time and effort you want to
spend helping to improve things around here. And help we can use :-)

Here is a (non-exclusive, non-prioritized) list of things you might be able to help us with:

* bug reports
* bug fixes
* improvements regarding code quality, e.g. improving readability, performance, modularity etc.
* documentation
* features (both ideas and code are welcome)
* tests

### File Headers
A proper header must be in place for any file contributed to the project. For a new contribution, please add the below header:

```
/*******************************************************************************
 * Copyright (c) <year> <legal entity> and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 ******************************************************************************/
 ```

 Please ensure \<year\> is replaced with the current year or range (e.g. 2017 or 2015, 2017).
 Please ensure \<legal entity\> is replaced with the relevant information. If you are editing an existing contribution, feel free
 to create or add your legal entity to the contributors section as such:

 ```
 /*******************************************************************************
 * Copyright (c) <year> <legal entity> and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     <legal entity>
 *******************************************************************************/
 ```

### How to Contribute
The easiest way to contribute code/patches/whatever is by creating a GitHub pull request (PR). When you do make sure that you *Sign-off* your commits.

You do this by adding the `-s` flag when you make the commit(s), e.g.

    $> git commit -s -m "Let's do it."

## Making your Changes

* Fork the repository on GitHub
* Create a new branch for your changes
* Configure your IDE installing:
  * [SonarLint](http://www.sonarlint.org/eclipse/index.html)
* Make your changes
* Make sure copyright headers are included in (all) files including updated year(s)
* Make sure proper headers are in place for each file (see above Legal Requirements)
* Commit your changes to that branch
* Use descriptive and meaningful commit messages
* If you have a lot of commits squash them into a single commit
* Make sure you use the `-s` flag when committing as explained above
* Push your changes to your branch in your forked repository

## Submitting the Changes

Submit a pull request via the normal GitHub UI.

## After Submitting

* Do not use your branch for any other development, otherwise further changes that you make will be visible in the PR.

