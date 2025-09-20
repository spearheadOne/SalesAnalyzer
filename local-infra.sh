#!/usr/bin/env bash
set -e

COMMAND=$1

case "$COMMAND" in
  create)
    ./local-infra-create
    ;;
  remove)
    ./local-infra-remove
    ;;
  stop)
    ./local-infra-stop
    ;;
  start)
    ./local-infra-start
    ;;
  *)
    echo "Usage: $0 {create|remove|stop|start}"
    exit 1
    ;;
esac