#!/bin/bash
# Runs once, the first time the mongo volume is empty.
# The scripts in /scripts use the "use <db>" shell helper, so we pipe them into
# mongosh (interactive mode) instead of passing them as files. The originals stay unchanged.
for f in /seed/persona_ddl_mongo.js /seed/persona_dml_mongo.js; do
  echo "[personapp] seeding MongoDB with $f"
  mongosh --quiet --host 127.0.0.1 \
    -u "$MONGO_INITDB_ROOT_USERNAME" -p "$MONGO_INITDB_ROOT_PASSWORD" \
    --authenticationDatabase admin < "$f"
done
