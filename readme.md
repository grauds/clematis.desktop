# Clematis Java Workspace

[![Installer Publish](https://github.com/grauds/clematis.desktop/actions/workflows/release.yml/badge.svg)](https://github.com/grauds/clematis.desktop/actions/workflows/release.yml)
[![Gradle Package](https://github.com/grauds/clematis.desktop/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/grauds/clematis.desktop/actions/workflows/gradle-publish.yml)
[![License](https://img.shields.io/badge/License-GPLv2%202.0-blue.svg)](libs/kiwi/src/main/resources/com/hyperrealm/kiwi/html/gpl.html)

<img src="./docs/logo.png" alt="logo" width="600"/>

## Introduction

Clematis Java Workspace is a project for Master Degree at MIPT (Moscow Institute of Physics and Technology) started back in September'98. It has a long history, the first version of it was awarded as a Java Konkurs 2006 finalist.  

<img src="./docs/overview.png" alt="workspace_component_life_cycle.png" width="700"/>

### Downloads & Installation

Get the latest version of the application for your operating system.

| Operating System | Installer / Architecture | Link                                                                                                                        |
|:-----------------|:-------------------------|:----------------------------------------------------------------------------------------------------------------------------|
| **🍏 macOS**     | Apple Silicon (`arm64`)  | [Download DMG](https://github.com/grauds/clematis.desktop/releases/download/latest/Clematis.Java.Workspace-2.0.0.pkg)       |
|                  | Intel (`x64`)            | [Download DMG](https://github.com/grauds/clematis.desktop/releases/download/latest/Clematis.Java.Workspace-2.0.0.pkg)       |
| **🐧 Linux**     | Ubuntu / Debian (`.deb`) | [Download DEB](https://github.com/grauds/clematis.desktop/releases/download/latest/clematis-java-workspace_2.0.0_amd64.deb) |
| **Sources**      |                          | [Download ZIP](https://github.com/grauds/clematis.desktop/archive/refs/tags/latest.zip) |

*Check out the [Full Releases Page](https://github.com/grauds/clematis.desktop/releases/tag/latest).*

---

#### 🍏 macOS Installation

1. Download the `.dmg` file matching your Mac architecture from the table above.
2. Double-click the downloaded `.dmg` file.
3. Drag the application icon into your **Applications** folder.

---

#### 🐧 Linux Installation

##### Debian / Ubuntu
```bash
sudo dpkg -i clematis-java-workspace_2.0.0_amd64.deb
```

## Modules

The architecture of the application is modular, its functionality is extendable via plugins.
The basis, however, is the following modules:

1. **Workspace** - the entry point and a controller of a lifecycle [docs](./libs/api/README.md)
2. **Profiles** - to manage user data and configuration  [docs](./libs/profiles/README.md)
3. **Runtime** - plugins installer, updater and launcher of third-party applications (with OS command line)  [docs](./libs/runtime/README.md)
4. The default implementation of UI is [Workspace Desktop](./modules/ui/README.md)

   
## License

Clematis Desktop is licensed under GNU GENERAL PUBLIC LICENSE, v2.

## Build

The following command builds the entire project

`./gradlew clean build`



