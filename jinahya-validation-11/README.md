# jinahya-validation-11

The Jakarta EE 11 generation of [jinahya-validation](../README.md): Jakarta Validation 3.1, on Java SE 17 or
higher.
The current generation.

| Artifact | |
| --- | --- |
| `io.github.jinahya:jinahya-validation-utils-11` | Utilities for Jakarta Validation 3.1 |
| `io.github.jinahya:jinahya-validation-test-utils-11` | Test utilities for Jakarta Validation 3.1 |

Every version below is set in this directory's `pom.xml`, and nowhere else; keep the tables in step with it.
What is the same for every generation (the build JDK, the test dependencies, the plugins) is the root's; see
[Java versions](../README.md#java-versions).

## Environment

| | Version | Set by | Enforced by |
| --- | --- | --- | --- |
| Java SE, minimum | 17 | `maven.compiler.source` | `enforce-require-jakartaee-java-version` |
| Main classes | `--release 17` | `maven.compiler.source` | |
| Runtime dependencies | run on Java SE 17 | | `enforce-jakartaee-bytecode-version` (`enforceBytecodeVersion`) |
| Build JDK | 25 or newer | the root's `maven.compiler.testSource` | the root's `enforce-require-test-java-version` |
| Test classes | `--release 25` | the root's `maven.compiler.testSource` | |

## Jakarta EE 11 alignment

| | Version from the EE 11 BOM | Implementation | In this build | Latest of its series | Status |
| --- | --- | --- | --- | --- | --- |
| Jakarta EE Platform | `jakarta.jakartaee-bom` 11.0.0 | | | 11.0.0 | current |
| Jakarta Validation 3.1 | `jakarta.validation-api` 3.1.1 (`provided`) | Hibernate Validator 9.1 | 9.1.6.Final | 9.1.6.Final | current; 9.2, also Jakarta Validation 3.1, is at 9.2.0.CR2 |
| Jakarta Validation 3.1 | `jakarta.validation-api` 3.1.1 (`provided`) | Apache BVal 3.1 (`bval-jsr`) | 3.1.0 | 3.1.0 | current |
| Jakarta Expression Language 6.0 | `jakarta.el-api` 6.0.1 | Expressly 6.0 (for Hibernate Validator) | 6.0.0 | 6.0.0 | current |

The providers and Expressly are `test`-scoped: the tests run against one provider at a time, chosen with this
generation's `hibernate-validator` (default) or `apache-bval` profile.

## Runtime dependencies of the artifacts

| Artifact | Dependency | Scope | Version | Runs on |
| --- | --- | --- | --- | --- |
| `jinahya-validation-utils-11` | `jakarta.validation:jakarta.validation-api` | `provided` | 3.1.1, from the BOM | Java SE 17 |
| `jinahya-validation-test-utils-11` | `io.github.jinahya:jinahya-validation-utils-11` | `compile` | this project's | Java SE 17 |
| `jinahya-validation-test-utils-11` | `org.assertj:assertj-core` | `compile` | 4.0.0-M1 | Java SE 17 |

The same AssertJ as the tests, since 4.x runs on Java 17.

*Surveyed 2026-11-14 against Maven Central metadata and the upstream release pages
([Jakarta EE Platform 11](https://jakarta.ee/specifications/platform/11/),
[Hibernate Validator](https://hibernate.org/validator/releases/),
[Apache BVal](https://bval.apache.org/)).*

## Building

From the root, which builds every module on one JDK:

```sh
./_mvn.sh -pl jinahya-validation-11 -amd verify                      # against each provider
./_mvn_hibernate_validator.sh -pl jinahya-validation-11 -amd verify  # against Hibernate Validator only
```
