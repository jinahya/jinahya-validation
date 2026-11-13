#!/bin/sh
# Runs a Maven build of every Jakarta EE generation (jinahya-validation-NN) against Apache BVal, with the
# apache-bval profile.
#
# `clean` is prepended to whatever you pass, and is not optional; test classes compiled for one provider must not be
# reused by another.
#
# Usage: ./_mvn_apache_bval.sh [maven args...]   e.g. ./_mvn_apache_bval.sh test
#        ./_mvn_apache_bval.sh -pl jinahya-validation-11 -amd test   (one generation only)
cd "$(dirname "$0")" || exit 1

echo
echo "=== apache-bval"
if ./mvnw -Papache-bval clean "$@"; then
  echo "=== OK   apache-bval"
else
  echo "=== FAIL apache-bval"
  exit 1
fi
