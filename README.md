# LensFolio

LensFolio is an application for students in project courses. Currently, users can:
- Register an account and login
- View their account profile and edit their details
- View and edit details of the project they're apart of
- Add and remove sprints from the project
- View and edit sprints from the project


## Basic Project Structure

- `systemd/` - This folder includes the systemd service files that will be present on the VM, these can be safely ignored.
- `runner/` - These are the bash scripts used by the VM to execute the application.
- `shared/` - Contains (initially) some `.proto` contracts that are used to generate Java classes and stubs that the following modules will import and build on.
- `identityprovider/` - The Identity Provider (IdP) is built with Spring Boot, and uses gRPC to communicate with other modules. The IdP is where we will store user information (such as usernames, passwords, names, ids, etc.).
- `portfolio/` - The Portfolio module is another fully fledged Java application running Spring Boot. It also uses gRPC to communicate with other modules.


## How to run

### 1 - Connecting to the database
The project needs to be connected to the remote database to be able to run. 

To do this, insert your UC username into the following command where indicated, run it, enter your password when prompted, and leave the terminal open.
```
ssh [username]@linux.cosc.canterbury.ac.nz -L 3306:db2.csse.canterbury.ac.nz:3306
```

### 2 - Generating Java dependencies from the `shared` class library
The `shared` class library is a dependency of the two main applications, so before you will be able to build either `portfolio` or `identityprovider`, you must make sure the shared library files are available via the local maven repository.

Assuming we start in the project root, the steps are as follows...

On Linux: 
```
cd shared
./gradlew clean
./gradlew publishToMavenLocal
```

On Windows:
```
cd shared
gradlew clean
gradlew publishToMavenLocal
```

*Note: The `gradle clean` step is usually only necessary if there have been changes since the last publishToMavenLocal.*

### 3 - Identity Provider (IdP) Module
Assuming we are starting in the root directory...

On Linux:
```
cd identityprovider
./gradlew bootRun
```

On Windows:
```
cd identityprovider
gradlew bootRun
```

By default, the IdP will run on local port 9002 (`http://localhost:9002`).

### 4 - Portfolio Module
Now that the IdP is up and running, we will be able to use the Portfolio module (note: it is entirely possible to start it up without the IdP running, you just won't be able to get very far).

From the root directory (and likely in a second terminal tab / window)...

On Linux:
```
cd portfolio
./gradlew bootRun
```

On Windows:
```
cd portfolio
gradlew bootRun
```

By default, the Portfolio will run on local port 9000 (`http://localhost:9000`)

## How to run tests

### 1 - Run the application
For the automated GUI tests to work, tests must be run while the application is running.

### 2 - Running the tests
Tests for each module can be run using the following command in the module's directory...

On Linux:
```
./gradlew test
```

On Windows:
```
gradlew test
```

### (Optional) 3 - Changing browsers
The automated GUI tests are run on Chrome by default, but can be run on Firefox with the following command... 

On Linux:
```
./gradlew test -Dbrowser=firefox
```

On Windows:
```
gradlew test -Dbrowser=firefox
```

## Contributors
- Christopher Hamdajani
- John Elliott
- Wil Johnston
- Haipeng Liu
- Jamie Thomas
- Dillon Pike
- Rachel Hodgson
- SENG302 teaching team

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Thymeleaf Docs](https://www.thymeleaf.org/documentation.html)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=13269&section=9)
