#!/usr/bin/env bash
set -e

echo "Stopping containers..."
docker compose down --volumes --remove-orphans

echo "Pruning unused Docker resources..."
docker system prune -f --volumes

echo "All infrastructure stopped and destroyed."