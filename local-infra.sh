#!/usr/bin/env bash
set -e

COMMAND=$1

case "$COMMAND" in
  create)
    ./local-infra-create.sh
    ;;
  remove)
    ./local-infra-remove.sh
    ;;
  stop)
    ./local-infra-stop.sh
    ;;
  start)
    ./local-infra-start.sh
    ;;
  *)
    echo "Usage: $0 {create|remove|stop|start}"
    exit 1
    ;;
esac