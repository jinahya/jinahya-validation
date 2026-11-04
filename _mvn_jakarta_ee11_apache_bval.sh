#!/bin/sh
# Runs a Maven build against Apache BVal on Jakarta EE 11 (Validation 3.1), with the jakarta-ee-11-apache-bval profile.
#
# `clean` is prepended to whatever you pass, and is not optional; classes compiled against one
# platform's API must not be reused by another.
#
# Usage: ./_mvn_jakarta_ee11_apache_bval.sh [maven args...]   e.g. ./_mvn_jakarta_ee11_apache_bval.sh test
cd "$(dirname "$0")" || exit 1

echo
echo "=== jakarta-ee-11-apache-bval"
if mvn -Pjakarta-ee-11-apache-bval clean "$@"; then
  echo "=== OK   jakarta-ee-11-apache-bval"
else
  echo "=== FAIL jakarta-ee-11-apache-bval"
  exit 1
fi
