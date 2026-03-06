#!/usr/bin/env bash
# dev.sh - load .env and run app (for dev not prod!!!)
set -e
if [ -f .env ]; then
  set -a
  . ./.env
  set +a
else
  echo "Warning: .env not found. Proceeding without loading env vars."
fi
./gradlew bootRun
