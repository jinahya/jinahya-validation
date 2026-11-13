#!/bin/sh
# Runs a Maven build against Apache BVal on Jakarta EE 10 (Validation 3.0), with the jakartaee-10 and apache-bval profiles.
#
# `clean` is prepended to whatever you pass, and is not optional; classes compiled against one
# platform's API must not be reused by another.
#
# Usage: ./_mvn_jakarta_ee10_apache_bval.sh [maven args...]   e.g. ./_mvn_jakarta_ee10_apache_bval.sh test
cd "$(dirname "$0")" || exit 1

echo
echo "=== jakartaee-10,apache-bval"
if mvn -Pjakartaee-10,apache-bval clean "$@"; then
  echo "=== OK   jakartaee-10,apache-bval"
else
  echo "=== FAIL jakartaee-10,apache-bval"
  exit 1
fi
