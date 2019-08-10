# jWorkspace - Desktop for Java
  
[![License](https://img.shields.io/badge/License-GPLv2%202.0-blue.svg)](libs/kiwi/src/main/resources/com/hyperrealm/kiwi/html/gpl.html)
[![Build Status](https://travis-ci.com/grauds/clematis.desktop.svg?token=TexcHfhzFr21pQNJbxcm&branch=master)](https://travis-ci.com/grauds/clematis.desktop)
[![codecov](https://codecov.io/gh/grauds/clematis.desktop/branch/master/graph/badge.svg?token=YdupUNe6dl)](https://codecov.io/gh/grauds/clematis.desktop)

## Introduction

jWorkspace is a pet project started back in 1999 and being developed at spare time. It includes 
and is build upon Kiwi and Springboard - the other great hobby projects of Mark Lindner, both gave
a significant contribution. Also it includes a number of other packages I've been experimenting with 
and some prototypes too.  
   
## License

jWorkspace is licensed under GNU GENERAL PUBLIC LICENSE, v2.

Inpependent modules and plugins of jWorkspace will be licensed under the
[Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0). 

## Build

This is a gradle project and is split on modules. The app module contains core classes and API, 
the libs directory holds Kiwi library and the modules directory contains all other different modules like 
Weather Archive scripts, Desktop UI and others

The following command builds the entire project

`./gradlew clean build`



