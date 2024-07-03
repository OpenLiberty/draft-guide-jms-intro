#!/bin/bash
set -euxo pipefail

mvn -ntp -pl models clean install

mvn -ntp -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl system -q clean package liberty:create liberty:install-feature liberty:deploy

mvn -ntp -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl inventory -q clean package liberty:create liberty:install-feature liberty:deploy

mvn -ntp -pl inventory liberty:start
sleep 10
mvn -ntp -pl system liberty:start
sleep 15

mvn -ntp -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl inventory failsafe:integration-test

mvn -ntp -pl inventory failsafe:verify

mvn -ntp -pl system liberty:stop
mvn -ntp -pl inventory liberty:stop

# IBM MQ test

docker pull --platform linux/amd64 icr.io/ibm-messaging/mq:latest

docker volume create qm1data

docker run \
--env LICENSE=accept --env MQ_QMGR_NAME=QM1 \
--volume qm1data:/mnt/mqm \
--publish 1414:1414 --publish 9443:9443 \
--detach \
--env MQ_APP_PASSWORD=passw0rd --env MQ_ADMIN_PASSWORD=passw0rd \
--rm \
--platform linux/amd64 \
--name QM1 \
icr.io/ibm-messaging/mq:latest

mvn -ntp -pl inventory liberty:start
sleep 10
mvn -ntp -pl system liberty:start
sleep 20

mvn -ntp -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl inventory failsafe:integration-test

mvn -ntp -pl inventory failsafe:verify

mvn -ntp -pl system liberty:stop
mvn -ntp -pl inventory liberty:stop

docker stop QM1
docker volume remove qm1data

