# Git Worktree

A free tool-window plugin for IntelliJ Platform IDEs to manage **git worktrees** without leaving the
editor. Works in IntelliJ IDEA and Android Studio.

## Features

A dedicated **Worktrees** tool window with a toolbar:

- **List** — shows all worktrees of the project's git repository (`git worktree list --porcelain`),
  with tags for `current` / `bare` / `detached` / `locked` and the path.
- **Add** — create a worktree from an existing branch or a new branch (from a chosen start point).
- **Open** — open a worktree as a project: the toolbar button lets the IDE ask *this / new window*;
  double-click opens it directly in a new window.
- **Remove** — with a confirmation, and a *force* fallback when the worktree has local changes.

Operations run in the background and surface git errors as IDE notifications. Worktree commands go
through the bundled Git integration (`git4idea`).

## Compatibility

- IntelliJ Platform **2025.2+** (`since-build 252`). Verified targets: Android Studio (253) and
  IntelliJ IDEA (261).
- Requires the bundled **Git** plugin (enabled by default in all IntelliJ-based IDEs).

> `git worktree` support in `git4idea` (`GitCommand.WORKTREE`) is available only since platform 252,
> which is why the minimum is 2025.2.

## Install (from disk)

```bash
./gradlew buildPlugin
```

The plugin zip is produced at `build/distributions/git-worktree-plugin-0.1.0.zip`. In the IDE:
**Settings → Plugins → ⚙ → Install Plugin from Disk…** and pick that zip, then restart.

## Build & develop

```bash
./gradlew buildPlugin     # build the distributable zip
./gradlew test            # run unit tests (porcelain parser + git args builder)
./gradlew runIde          # launch a sandbox IDE with the plugin for manual testing
```

Tech stack: Kotlin, IntelliJ Platform Gradle Plugin 2.x, `git4idea`, JUnit.

## Status

MVP: list / add / open / remove. Deliberately out of scope for now: `lock`/`unlock`, `move`,
`prune`, repository picker when several git roots are present, and JetBrains Marketplace publishing.
