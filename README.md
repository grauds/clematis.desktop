# Getting Started

## Dependencies

The following must be installed on the development / build box:

* [Node.js](http://nodejs.org/) - used for build and run application
* Webpack:
 ```
 sudo npm i -g webpack 
 ```


### To verify the install:

```
node -v
```

```
npm -v
```
```
webpack --version
```

### Work configuration
We use `nvm` to manage multiple active node.js versions.

* node v7.10.1
* npm v4.2.0
* webpack v3.5.5

## Install project dependencies

To begin running the environment UI development (from a fresh clone) go to js/ folder and:

Install dependencies:

```sh
npm install
```

## Start the application

There are different possibilities to start this application

1. To start application without Java backend using Node Express server and mock responses from backend. From js/ folder run
```bash
npm run start:local
```
and go to localhost:3000

2. To start application with Java backend and responses from QA KS in dev mode from js/ folder run
```bash
npm run build:dev
```
Start Java class src/main/java/com/wiley/af/SpringApplication and go to localhost:8080

3. To start application with Java backend and responses from QA KS in prod mode from the project folder run
```bash
gradle clean build && java -jar build/libs/AF-0.0.1-SNAPSHOT.jar
```

## Additional info
All frontend build information is described in scripts section of js/package.json