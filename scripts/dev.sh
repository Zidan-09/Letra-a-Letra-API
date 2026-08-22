#!/usr/bin/env bash

set -e

cd "$(dirname "$0")/.."

echo "=========================="
echo "Restarting Docker Ambient"
echo "=========================="

docker compose down -v

echo
echo "=========================="
echo "Loading Containers"
echo "=========================="

docker compose --env-file .env.dev up --build