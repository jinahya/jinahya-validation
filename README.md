# jinahya-validation

Jakarta Validation extensions, built once per Jakarta EE generation.

## Artifacts

Every artifact carries its Jakarta EE generation in its artifactId; use the ones of the generation you run on, and
never two generations together, since they share their packages.

| Artifact | Module | Jakarta EE 10 | Jakarta EE 11 |
| --- | --- | --- | --- |
| Utilities | `io.github.jinahya.validation.util` | `io.github.jinahya:jinahya-validation-utils-10` | `io.github.jinahya:jinahya-validation-utils-11` |
| Test utilities | `io.github.jinahya.validation.test.util` | `io.github.jinahya:jinahya-validation-test-utils-10` | `io.github.jinahya:jinahya-validation-test-utils-11` |

Every generation of an artifact is the same module, named after its package.

The Jakarta Validation API is `provided`: your platform, or your own dependency on a validation provider, supplies it.

## Layout

```
pom.xml                                  plugins, and the versions not tied to a generation
shared/<module>/src/...                  the sources every generation shares
jinahya-validation-NN/pom.xml            Jakarta EE NN: the platform BOM, the providers, Java SE
  jinahya-validation-utils-NN/           the sources only Jakarta EE NN needs, if any
  jinahya-validation-test-utils-NN/
```

Each `jinahya-validation-NN/*` module compiles `shared/<module>/src/...` against its own generation's API, together
with its own `src/`, which holds only what differs.

## Jakarta EE alignment

Every version tied to a generation is set in that generation's parent, `jinahya-validation-NN/pom.xml`, and nowhere
else: the Jakarta EE BOM (and, through it, every `jakarta.*` API), the minimum Java SE, and every implementation of a
Jakarta specification.
Each implementation is kept on the newest release of the series aligned with its generation.
The root `pom.xml` holds only what is the same for every generation (JUnit, Mockito, AssertJ, Lombok, SLF4J, the
plugins).

Each generation documents its environment and its version alignment in its own `README.md`; update it whenever its
`pom.xml` changes.

| Jakarta EE | Module | Jakarta Validation | Java SE, minimum | Status |
| --- | --- | --- | --- | --- |
| 10 | [`jinahya-validation-10`](jinahya-validation-10/README.md) | 3.0 | 11 | limited support upstream |
| 11 | [`jinahya-validation-11`](jinahya-validation-11/README.md) | 3.1 | 17 | current |
| 12 | none yet | 4.0 | | not released; see [below](#jakarta-ee-12) |

### Java versions

* **Main classes:** each generation compiles them for its Jakarta EE generation's minimum Java SE
  (`maven.compiler.source` in `jinahya-validation-NN/pom.xml`), and the enforcer requires a JDK at least that new.
* **What the main classes need at runtime** (every dependency outside the `test` scope, `provided` included) must run
  on that minimum, too; each generation enforces it with `enforceBytecodeVersion`.
  That is why `jinahya-validation-test-utils-10`, which ships AssertJ at `compile` scope, is on AssertJ 3.27, while the
  rest of the build is on AssertJ 4 (see [its README](jinahya-validation-10/README.md)).
* **The build:** one JDK for the whole project, the latest LTS which can still compile for every generation's minimum
  (now JDK 25; e.g., `jenv local 25`).
  The test classes are compiled for it (`maven.compiler.testSource`), and the enforcer requires it.
* **Test dependencies** (JUnit, Mockito, AssertJ, Lombok, SLF4J) are at their latest releases which support that JDK,
  in every generation.

### Jakarta EE 12

Not released, so there is no `jinahya-validation-12` yet: no `jakarta.jakartaee-bom` 12.x is on Maven Central.

| Spec (EE 12 target) | Latest artifact on Central | Implementation | Latest artifact on Central |
| --- | --- | --- | --- |
| Jakarta Validation 4.0 | `jakarta.validation-api` 4.0.0-M1 | Hibernate Validator | no Jakarta Validation 4.0 series (9.2 is still 3.1) |
| Jakarta Validation 4.0 | `jakarta.validation-api` 4.0.0-M1 | Apache BVal | no Jakarta Validation 4.0 series (3.1.0 is the latest) |
| Jakarta Expression Language 6.1 | `jakarta.el-api` 6.1.0-M2 | Expressly | no 6.1 series (6.0.0 is the latest) |

*Surveyed 2026-11-14 against Maven Central metadata and the upstream release pages
([Jakarta EE Platform](https://jakarta.ee/specifications/platform/),
[Hibernate Validator](https://hibernate.org/validator/releases/),
[Apache BVal](https://bval.apache.org/)).*

### Adding a generation

1. Copy `jinahya-validation-NN/` to the new generation, renaming the directories and the artifactIds.
2. In its `pom.xml`, set `jakartaee`, `maven.compiler.source` (the generation's minimum Java SE), the BOM, and each
   provider's and Expressly's version; leave a provider out, with its profile, until it has a series for the
   generation.
3. Update its `README.md`, add the module to the root `pom.xml`, and add a row to the table above.
4. Move a shared source which the new API breaks into the generations' own `src/`.

## Validation providers

Tests run against one provider at a time, chosen with a profile of each generation's parent:

| Profile | Provider |
| --- | --- |
| `hibernate-validator` | Hibernate Validator, with Expressly; **active by default** |
| `apache-bval` | Apache BVal |

Tests specific to a provider go in `src/test/java-<profile>`, either under `shared/<module>/` or in a module.

## Building

A JDK 25 or newer is required, since the test classes are compiled for Java 25 (see [Java versions](#java-versions)).

```sh
./_mvn.sh verify                                          # every generation, against each provider
./_mvn_apache_bval.sh verify                              # every generation, against Apache BVal
./_mvn_hibernate_validator.sh -pl jinahya-validation-11 -amd verify   # one generation
```
