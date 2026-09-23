# MyBigNumber — Core Library (Task 1)

Implements the addition of two very large natural numbers, represented as strings, using the
exact same "carry" addition algorithm taught in elementary school: walk both strings from right
to left, add each pair of digits together with the carry, and store the resulting digit.

The result is written from right to left into a preallocated `char[]`, then converted to a
`String` once, without reversing or repeatedly inserting at the beginning. Redundant leading
zeros are removed, with an all-zero result returned as `"0"`.

`sum` requires non-null, non-empty strings containing only ASCII digits (`0-9`). Characters
are validated while being added or copied, rather than in a separate pass. Invalid input throws
`IllegalArgumentException` and clears `getLastSteps()`; the console entry point reports the
error and exits with status 1.

When one operand runs out of digits, arithmetic continues only while a carry remains (including
chains of `9`s). The untouched prefix is then copied and validated, with one "bring down"
step in the log and console trace. This avoids allocating and formatting an addition step for
every untouched digit. Reading and copying the input still takes linear time.

The public JAR API is unchanged: `sum`, `getLastSteps`, and all `AdditionStep` constructors and
getters keep their signatures. `getLastSteps()` still exposes the complete, read-only per-column
trace for existing clients; copied columns are created lazily when accessed. Consumers that
iterate or serialize the entire list still pay the cost of producing those detailed steps.

This module has **no dependency on the Web module** (`MyBigNumber-MVC`) — it can be built, tested,
and run entirely on its own, which makes it suitable to push to its own branch/repository (e.g. a
`core` branch).

## What's inside

| File | Purpose |
|---|---|
| `MyBigNumber.java` | Core class. Method `String sum(String stn1, String stn2)` performs the addition and logs each step via SLF4J. `getLastSteps()` returns the step-by-step trace of the most recent call, consumed by the Web UI to render calculation progress. |
| `Main.java` | Console entry point so the program can run **standalone** (see "Run standalone" below). |
| `MyBigNumberTest.java` (in `src/test`) | JUnit 5 unit tests. |

## Requirements

- JDK 17+
- Maven 3.9+

## Install (build)

From this module's directory:

```bash
cd MyBigNumber
mvn clean package
```

This produces two jars under `target/`:
- `MyBigNumber-0.0.2.jar` — plain library jar (for other modules to depend on).
- `MyBigNumber-0.0.2-jar-with-dependencies.jar` — a fat jar bundling SLF4J API and its Simple provider, **runnable on its own**.

The standalone JAR prints INFO logs to standard error through `slf4j-simple`. This runtime
dependency is optional, so Maven consumers of the plain library JAR keep their own logging
provider (for example, Logback in a Spring Boot application). Use the plain JAR as a library;
the JAR with dependencies is intended for standalone execution. A consuming application with
no provider must supply one to enable logging; `slf4j-api` alone does not output logs.

To also install it into your local Maven repository (`~/.m2`) so `MyBigNumber-MVC` can pick it up
as a dependency:

```bash
mvn clean install
```

## Run standalone

With two numbers passed as command-line arguments:

```bash
java -jar target/MyBigNumber-0.0.2-jar-with-dependencies.jar 1234 897
```

Or with no arguments, to type the numbers interactively:

```bash
java -jar target/MyBigNumber-0.0.2-jar-with-dependencies.jar
```

Sample output:

```
[main] INFO com.example.add2num.core.MyBigNumber - Starting addition: "1234" + "897"
[main] INFO com.example.add2num.core.MyBigNumber - Step 1: 4 + 7 (carry-in 0) = 11 -> store 1 in the result, carry-out 1
[main] INFO com.example.add2num.core.MyBigNumber - Step 2: 3 + 9 (carry-in 1) = 13 -> store 3 in the result, carry-out 1
[main] INFO com.example.add2num.core.MyBigNumber - Step 3: 2 + 8 (carry-in 1) = 11 -> store 1 in the result, carry-out 1
[main] INFO com.example.add2num.core.MyBigNumber - Step 4: 1 + - (carry-in 1) = 2 -> store 2 in the result, carry-out 0
[main] INFO com.example.add2num.core.MyBigNumber - Result: "1234" + "897" = "2131"

Calculation progress (see the log lines above as well, via SLF4J):
  Step 1: 4 + 7 (carry-in 0) = 11 -> store 1 in the result, carry-out 1
  Step 2: 3 + 9 (carry-in 1) = 13 -> store 3 in the result, carry-out 1
  Step 3: 2 + 8 (carry-in 1) = 11 -> store 1 in the result, carry-out 1
  Step 4: 1 + - (carry-in 1) = 2 -> store 2 in the result, carry-out 0

1234 + 897 = 2131
```

## Run the unit tests

```bash
cd MyBigNumber
mvn test
```

Covers:
- The exact example from the requirement document (`sum("1234", "897") = "2131"`).
- Numbers of different lengths, with and without carrying, and very large numbers (tens of digits).
- Commutativity (`sum(a,b) == sum(b,a)`).
- Correctness of the recorded step-by-step trace.
- Leading zeros, a final carry column, and a 10,000-digit carry chain.
- Comparison with `BigInteger` on 200 reproducible random operand pairs (test code only).
- Rejection of null, empty, and non-digit operands, and clearing of partial calculation steps.
- Compact prefix-copy traces, carry chains, and unchanged per-column data for legacy JAR clients.

## Use as a library from another project

Add it as a Maven dependency (after running `mvn install` from this module, or from the parent
project):

```xml
<dependency>
    <groupId>com.example.add2num</groupId>
    <artifactId>MyBigNumber</artifactId>
    <version>0.0.2</version>
</dependency>
```

```java
MyBigNumber myBigNumber = new MyBigNumber();
String result = myBigNumber.sum("1234", "897"); // "2131"
List<MyBigNumber.AdditionStep> steps = myBigNumber.getLastSteps(); // step-by-step trace
```
