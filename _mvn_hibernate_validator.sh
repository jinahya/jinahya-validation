#!/bin/sh
# Runs a Maven build of every Jakarta EE generation (jinahya-validation-NN) against Hibernate Validator, with the
# hibernate-validator profile.
#
# `clean` is prepended to whatever you pass, and is not optional; test classes compiled for one provider must not be
# reused by another.
#
# Usage: ./_mvn_hibernate_validator.sh [maven args...]   e.g. ./_mvn_hibernate_validator.sh test
#        ./_mvn_hibernate_validator.sh -pl jinahya-validation-11 -amd test   (one generation only)
cd "$(dirname "$0")" || exit 1

echo
echo "=== hibernate-validator"
if ./mvnw -Phibernate-validator clean "$@"; then
  echo "=== OK   hibernate-validator"
else
  echo "=== FAIL hibernate-validator"
  exit 1
fi
