#!/usr/bin/env bash

#
# A default startup script for the Kakadu Docker container.
#

function printStartupMessage {
  echo ""
  echo "Did not receive an argument, so just running the container indefinitely."
  echo ""
}

function printUsageMessage {
  echo "Usage:"
  echo "  docker run --rm --name kdutest -v \$(pwd):/home/kakadu kakadu test.tif"
  echo "  docker run --rm --name kdutest -v \$(pwd):/home/kakadu kakadu test.jp2"
  echo ""
}

function printCleanUpMessage {
  echo "To stop this container, before starting another, type in a new terminal:"
  echo "  docker rm -f \$(docker ps -l --format '{{.ID}}')"
  echo ""
}

if [ -z "$1" ]; then
  printStartupMessage
  printUsageMessage
  printCleanUpMessage

  # If we don't have a startup argument, just keep the container running by default
  tail -f /dev/null
elif [[ "$1" == *.tif ]]; then
  kdu_compress -i "$1" -o /tmp/test.jp2
elif [[ "$1" == *.jp2 || "$1" == "*.jpx" ]]; then
  kdu_jp2info -i "$1"
else
  printUsageMessage
fi
