#!/usr/bin/env bash
set -e

echo "Stopping containers..."
docker compose stop

echo "All containers stopped (no resources removed)."