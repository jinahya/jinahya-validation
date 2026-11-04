#!/bin/sh
# Runs a Maven build against Hibernate Validator on Jakarta EE 10 (Validation 3.0), with the jakarta-ee-10 and hibernate-validator profiles.
#
# `clean` is prepended to whatever you pass, and is not optional; classes compiled against one
# platform's API must not be reused by another.
#
# Usage: ./_mvn_jakarta_ee10_hibernate_validator.sh [maven args...]   e.g. ./_mvn_jakarta_ee10_hibernate_validator.sh test
cd "$(dirname "$0")" || exit 1

echo
echo "=== jakarta-ee-10,hibernate-validator"
if mvn -Pjakarta-ee-10,hibernate-validator clean "$@"; then
  echo "=== OK   jakarta-ee-10,hibernate-validator"
else
  echo "=== FAIL jakarta-ee-10,hibernate-validator"
  exit 1
fi
