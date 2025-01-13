#!/bin/bash
# Get the current working directory
CURRENT_DIR=$(pwd)

# Define the desired directory path
HOST_DIR="${CURRENT_DIR}/result"
HOST_DIR_SIZE_IMPACT="${CURRENT_DIR}/result_size_impact"

# Check if the directory exists, if not create it
if [ ! -d "$HOST_DIR" ]; then
  mkdir -p "$HOST_DIR"
fi
chmod 755 "$HOST_DIR"
export HOST_DIR

if [ ! -d "$HOST_DIR_SIZE_IMPACT" ]; then
  mkdir -p "$HOST_DIR_SIZE_IMPACT"
fi
chmod 755 "$HOST_DIR_SIZE_IMPACT"
export HOST_DIR_SIZE_IMPACT



docker compose up --build -d

#docker compose up -d
docker exec -it nosdaq bash

docker compose down
