# Add2Num — Big Number Addition

The project has 2 Maven modules:

| Module | Content | Maps to |
|---|---|---|
| `MyBigNumber` | Core class `MyBigNumber`, method `String sum(String stn1, String stn2)`. Implements the grade-school carry-addition algorithm, packaged as a `.jar` to be reused as a library. See [`MyBigNumber/README.md`](MyBigNumber/README.md) for install/run instructions specific to this module. | Task 1 |
| `MyBigNumber-MVC` | Web application (Spring Boot + Thymeleaf + Bootstrap) that lets a user enter 2 large numbers, calls `MyBigNumber` to compute the sum, and displays the **step-by-step progress** of the addition plus the history of past calculations. | Task 2 |

`MyBigNumber-MVC` declares `MyBigNumber` as a **dependency** (`.jar`) in its `pom.xml`, matching the requirement to "reuse the Task 1 result as a sub-module or library".

## Environment requirements

- JDK 17+
- Maven 3.9+ (or use the Maven Wrapper if you add one)

## Building the whole project

From the root directory (containing the parent `pom.xml`):

```bash
mvn clean install
```

This will:
1. Build `MyBigNumber`, run its Unit Tests, and install the `.jar` into the local Maven repository (`~/.m2`).
2. Build `MyBigNumber-MVC`, automatically picking up `MyBigNumber` from step 1 as a dependency.

## Running the `MyBigNumber` core module by itself

See [`MyBigNumber/README.md`](MyBigNumber/README.md) — this module can be built, tested, and run
completely on its own, without the Web module.

## Running the Web application (Task 2)

After `mvn clean install` has been run once from the root directory:

```bash
cd MyBigNumber-MVC
mvn spring-boot:run
```

Or run the built jar directly:

```bash
cd MyBigNumber-MVC
mvn clean package
java -jar target/MyBigNumber-MVC-0.0.1.jar
```

Open your browser at: **http://localhost:8080**

On the page:
- Enter 2 large numbers (digits only accepted) and click "Thực hiện phép cộng" (Perform addition).
- The result and a **step-by-step progress table** (digit 1, digit 2, carry-in, column sum,
  result digit, carry-out) appear right below.
- At the bottom is the **history of additions performed** in the current run (keeps the last 50
  records, stored in memory).

## Running the Web module's tests

```bash
cd MyBigNumber-MVC
mvn test
```

Includes a form-page test and integration tests (MockMvc) for `POST /sum`.

## Logging the operation history

`MyBigNumber` uses SLF4J to log each step of every `sum(...)` call (e.g.
`Step 1: 4 + 7 (carry-in 0) = 11 -> store 1 in the result, carry-out 1`). When running the
`MyBigNumber-MVC` module, these logs show up on the console via Spring Boot's default logging
(Logback), which comes bundled with `spring-boot-starter-web`.

## Directory structure

```
add2num/
├── pom.xml                     # parent (aggregator) POM
├── README.md
├── .gitignore
├── MyBigNumber/                 # Task 1 - core library, also runs standalone
│   ├── README.md                # module-specific install/run guide
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/example/add2num/core/MyBigNumber.java
│       ├── main/java/com/example/add2num/core/Main.java   # standalone console entry point
│       └── test/java/com/example/add2num/core/MyBigNumberTest.java
└── MyBigNumber-MVC/              # Task 2 - Web application
    ├── pom.xml
    └── src/
        ├── main/java/com/example/add2num/web/
        │   ├── BignumberWebApplication.java
        │   ├── controller/BigNumberController.java
        │   ├── dto/BigNumberForm.java
        │   ├── model/OperationRecord.java
        │   └── service/HistoryService.java
        ├── main/resources/
        │   ├── application.properties
        │   └── templates/index.html
        └── test/java/com/example/add2num/web/BigNumberControllerTest.java
```

## Version submitted for evaluation

Version submitted for evaluation: **0.0.1** (tag or branch it accordingly on the Git server).

## Clone directory convention

After pushing the project to GitHub/GitLab at a URL like `https://<git-host>/<account>/<projectname>`,
clone it into:

- Windows: `D:\Projects\<git-host>\<account>\<projectname>`
- macOS/Linux: `~/Projects/<git-host>/<account>/<projectname>`

Example: a project at `https://github.com/youraccount/add2num` is cloned into
`D:\Projects\github.com\youraccount\add2num` (Windows) or
`~/Projects/github.com/youraccount/add2num` (macOS/Linux).
