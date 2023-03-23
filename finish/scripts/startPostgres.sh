#!/bin/bash

docker ps | grep -q "postgres-sample" && echo Postgres is running && exit 0

cd ./postgres || exit
docker build -t postgres-sample .
docker run -d \
  -p 5432:5432 \
  --name postgres-container \
  postgres-sample --max_prepared_transactions=100
