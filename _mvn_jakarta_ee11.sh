#!/bin/sh
# Runs a Maven build against both Jakarta EE 11 (Validation 3.1) providers, by running each of
#   _mvn_jakarta_ee11_hibernate_validator.sh
#   _mvn_jakarta_ee11_apache_bval.sh
# and continuing past a failure, so every combination is reported.
#
# Usage: ./_mvn_jakarta_ee11.sh [maven args...]   e.g. ./_mvn_jakarta_ee11.sh test
cd "$(dirname "$0")" || exit 1

failed=''
for script in _mvn_jakarta_ee11_hibernate_validator.sh _mvn_jakarta_ee11_apache_bval.sh; do
  ./"$script" "$@" || failed="$failed $script"
done

echo
if [ -n "$failed" ]; then
  echo "FAILED (_mvn_jakarta_ee11.sh):"
  for f in $failed; do echo "  $f"; done
  exit 1
fi
echo "all succeeded (_mvn_jakarta_ee11.sh)"
