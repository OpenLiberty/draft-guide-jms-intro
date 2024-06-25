#!/bin/bash
set -euxo pipefail

mvn -pl models clean install

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

mvn -pl system liberty:stop
mvn -pl inventory liberty:stop

cp ../ibmmq/system/pom.xml system/pom.xml
cp ../ibmmq/inventory/pom.xml inventory/pom.xml
cp ../ibmmq/system/src/main/liberty/config/server.xml system/src/main/liberty/config/server.xml
cp ../ibmmq/inventory/src/main/liberty/config/server.xml inventory/src/main/liberty/config/server.xml

docker volume create qm1data

docker run --env LICENSE=accept --env MQ_QMGR_NAME=QM1 \
      --volume docker:/mnt/mqm --publish 1414:1414 \
      --publish 9443:9443 --detach \
      --env MQ_APP_PASSWORD=passw0rd \
      --env MQ_ADMIN_PASSWORD=passw0rd \
      --name QM1 ibm-mqadvanced-server-dev:9.4.0.0-arm64

mvn -ntp -pl inventory liberty:start
sleep 10
mvn -ntp -pl system liberty:start
sleep 15

mvn -ntp -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl inventory failsafe:integration-test

mvn -ntp -pl inventory failsafe:verify

mvn -pl system liberty:stop
mvn -pl inventory liberty:stop
