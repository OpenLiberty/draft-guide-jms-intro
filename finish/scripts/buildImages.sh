#!/bin/bash

echo Building images

docker build -t command:1.0-SNAPSHOT command/. &
docker build -t query:1.0-SNAPSHOT query/. &
docker build -t inventory:1.0-SNAPSHOT inventory/. &
wait

echo Images building completed
