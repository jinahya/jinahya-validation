#!/bin/sh
# Runs a Maven build against Hibernate Validator on Jakarta EE 11 (Validation 3.1), with the jakartaee-11 and hibernate-validator profiles.
#
# `clean` is prepended to whatever you pass, and is not optional; classes compiled against one
# platform's API must not be reused by another.
#
# Usage: ./_mvn_jakarta_ee11_hibernate_validator.sh [maven args...]   e.g. ./_mvn_jakarta_ee11_hibernate_validator.sh test
cd "$(dirname "$0")" || exit 1

echo
echo "=== jakartaee-11,hibernate-validator"
if mvn -Pjakartaee-11,hibernate-validator clean "$@"; then
  echo "=== OK   jakartaee-11,hibernate-validator"
else
  echo "=== FAIL jakartaee-11,hibernate-validator"
  exit 1
fi
