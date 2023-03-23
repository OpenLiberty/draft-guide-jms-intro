#!/bin/bash

NETWORK=cqrs-app

docker network create $NETWORK

docker run -d \
  --network=$NETWORK \
  --name postgres-container \
  --rm \
  postgres-sample --max_prepared_transactions=100

sleep 10

docker run -d \
  -e POSTGRES_HOSTNAME=postgres-container \
  -e QUERY_JMS_ADDRESS=query:7277 \
  --network=$NETWORK \
  --name=command \
  --rm \
  command:1.0-SNAPSHOT &

docker run -d \
  -e POSTGRES_HOSTNAME=postgres-container \
  -e CACHE_JMS_ADDRESS=inventory:7278 \
  --network=$NETWORK \
  --name=query \
  --rm \
  query:1.0-SNAPSHOT &
 
docker run -d \
  -e COMMAND_JMS_ADDRESS=command:7276 \
  -p 9080:9080 \
  --network=$NETWORK \
  --name=inventory \
  --rm \
  inventory:1.0-SNAPSHOT &

wait

sleep 10
