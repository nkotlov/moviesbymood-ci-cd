#!/usr/bin/env bash
set -euo pipefail

TAG="latest"

while getopts "t:" opt; do
  case "$opt" in
    t) TAG="$OPTARG" ;;
    *)
      echo "Использование: bash run.sh -t <tag>"
      exit 1
      ;;
  esac
done

IMAGE_NAME="moviesbymood-app:${TAG}"

echo "APP_IMAGE=${IMAGE_NAME}" > .env

mkdir -p logs files

echo "Запуск проекта с образом: ${IMAGE_NAME}"
docker compose up -d

echo "Контейнеры запущены."
docker compose ps