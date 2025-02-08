# GitHub Actions
Pipelines follow trunk-based development, which is suitable for small teams and small projects.

The `main` branch (development branch) is where the latest version of the code is. When implementing a feature of a bug fix, branch off from main, made the implementation and then create a pull request back to the `main` branch. 

This action will trigger the pull request pipeline.

For releases, the `release` branch is used. When the code on the `main` branch is ready for a release version, we can merge `main` into `release`, which triggers the release pipeline.

## Pull Request Pipeline
This pipeline runs when a pull request is made to merge changes into the `main` branch.

Purpose:
- To verify the changes made in a feature branch (or bug fix), to catch bugs early in development process

Tasks:
- Run unit tests
- Ensure code integrates properly with `release` branch (e.g. no merge conflicts)
- Build the app and verify it works
- Build an artifact (but not publish it to GitHub releases)


## Release Pipeline
This pipeline is triggers 