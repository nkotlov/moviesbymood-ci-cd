#!/usr/bin/env bash
set -euo pipefail

TAG="latest"

while getopts "t:" opt; do
  case "$opt" in
    t) TAG="$OPTARG" ;;
    *)
      echo "Использование: bash build.sh -t <tag>"
      exit 1
      ;;
  esac
done

IMAGE_NAME="moviesbymood-app:${TAG}"

echo "Сборка Docker-образа: ${IMAGE_NAME}"
docker build -t "${IMAGE_NAME}" .

echo "APP_IMAGE=${IMAGE_NAME}" > .env

echo "Сборка завершена."
echo "Тег образа: ${IMAGE_NAME}"