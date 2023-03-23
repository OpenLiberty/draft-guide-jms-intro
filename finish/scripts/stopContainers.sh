#!/bin/bash

docker stop command query inventory postgres-container

docker network rm cqrs-app

docker ps

docker container ls -a
