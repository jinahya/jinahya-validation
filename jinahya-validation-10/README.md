# jinahya-validation-10

The Jakarta EE 10 generation of [jinahya-validation](../README.md): Jakarta Validation 3.0, on Java SE 11 or
higher.
Limited support upstream: Hibernate Validator lists its 8.0 series, the Jakarta EE 10 one, as limited-support.

| Artifact | |
| --- | --- |
| `io.github.jinahya:jinahya-validation-utils-10` | Utilities for Jakarta Validation 3.0 |
| `io.github.jinahya:jinahya-validation-test-utils-10` | Test utilities for Jakarta Validation 3.0 |

Every version below is set in this directory's `pom.xml`, and nowhere else; keep the tables in step with it.
What is the same for every generation (the build JDK, the test dependencies, the plugins) is the root's; see
[Java versions](../README.md#java-versions).

## Environment

| | Version | Set by | Enforced by |
| --- | --- | --- | --- |
| Java SE, minimum | 11 | `maven.compiler.source` | `enforce-require-jakartaee-java-version` |
| Main classes | `--release 11` | `maven.compiler.source` | |
| Runtime dependencies | run on Java SE 11 | | `enforce-jakartaee-bytecode-version` (`enforceBytecodeVersion`) |
| Build JDK | 25 or newer | the root's `maven.compiler.testSource` | the root's `enforce-require-test-java-version` |
| Test classes | `--release 25` | the root's `maven.compiler.testSource` | |

## Jakarta EE 10 alignment

| | Version from the EE 10 BOM | Implementation | In this build | Latest of its series | Status |
| --- | --- | --- | --- | --- | --- |
| Jakarta EE Platform | `jakarta.jakartaee-bom` 10.0.0 | | | 10.0.0 | current |
| Jakarta Validation 3.0 | `jakarta.validation-api` 3.0.2 (`provided`) | Hibernate Validator 8.0 | 8.0.6.Final | 8.0.6.Final | current; the 8.0 series is in limited support |
| Jakarta Validation 3.0 | `jakarta.validation-api` 3.0.2 (`provided`) | Apache BVal 3.0 (`bval-jsr`) | 3.0.2 | 3.0.2 | current |
| Jakarta Expression Language 5.0 | `jakarta.el-api` 5.0.1 | Expressly 5.0 (for Hibernate Validator) | 5.0.0 | 5.0.0 | current |

The providers and Expressly are `test`-scoped: the tests run against one provider at a time, chosen with this
generation's `hibernate-validator` (default) or `apache-bval` profile.

## Runtime dependencies of the artifacts

| Artifact | Dependency | Scope | Version | Runs on |
| --- | --- | --- | --- | --- |
| `jinahya-validation-utils-10` | `jakarta.validation:jakarta.validation-api` | `provided` | 3.0.2, from the BOM | Java SE 11 |
| `jinahya-validation-test-utils-10` | `io.github.jinahya:jinahya-validation-utils-10` | `compile` | this project's | Java SE 11 |
| `jinahya-validation-test-utils-10` | `org.assertj:assertj-core` | `compile` | 3.27.7 | Java SE 8 |

AssertJ 4 requires Java 17, so this module is on 3.27, the latest series which runs on Java 11; its tests, and every other module's, are on AssertJ 4.

*Surveyed 2026-11-14 against Maven Central metadata and the upstream release pages
([Jakarta EE Platform 10](https://jakarta.ee/specifications/platform/10/),
[Hibernate Validator](https://hibernate.org/validator/releases/),
[Apache BVal](https://bval.apache.org/)).*

## Building

From the root, which builds every module on one JDK:

```sh
./_mvn.sh -pl jinahya-validation-10 -amd verify                      # against each provider
./_mvn_hibernate_validator.sh -pl jinahya-validation-10 -amd verify  # against Hibernate Validator only
```
