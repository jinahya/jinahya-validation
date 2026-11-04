#!/bin/sh
# Runs a Maven build against every platform x validation provider combination, by running each of
#   _mvn_jakarta_ee10.sh
#   _mvn_jakarta_ee11.sh
# and continuing past a failure, so every combination is reported.
#
# Usage: ./_mvn_jakarta_ee.sh [maven args...]   e.g. ./_mvn_jakarta_ee.sh test
cd "$(dirname "$0")" || exit 1

failed=''
for script in _mvn_jakarta_ee10.sh _mvn_jakarta_ee11.sh; do
  ./"$script" "$@" || failed="$failed $script"
done

echo
if [ -n "$failed" ]; then
  echo "FAILED (_mvn_jakarta_ee.sh):"
  for f in $failed; do echo "  $f"; done
  exit 1
fi
echo "all succeeded (_mvn_jakarta_ee.sh)"
