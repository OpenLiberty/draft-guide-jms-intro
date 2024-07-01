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

#cp system/pom.xml system/pom.xml.bak
#cp inventory/pom.xml inventory/pom.xml.bak
#cp system/src/main/liberty/config/server.xml system/src/main/liberty/config/server.xml.bak
#cp inventory/src/main/liberty/config/server.xml inventory/src/main/liberty/config/server.xml.bak

cp ../ibmmq/system/pom.xml system/pom.xml
cp ../ibmmq/inventory/pom.xml inventory/pom.xml
cp ../ibmmq/system/src/main/liberty/config/server.xml system/src/main/liberty/config/server.xml
cp ../ibmmq/inventory/src/main/liberty/config/server.xml inventory/src/main/liberty/config/server.xml

docker pull --platform linux/amd64 icr.io/ibm-messaging/mq:latest

docker volume create qm1data

#if [ "$(docker ps -aq -f name=QM1)" ]; then
#        echo "Stopping and removing existing QM1 container..."
#        docker stop QM1 || true
#        docker rm QM1 || true
#fi

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
sleep 15

mvn -ntp -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl inventory failsafe:integration-test

mvn -ntp -pl inventory failsafe:verify

mvn -ntp -pl system liberty:stop
mvn -ntp -pl inventory liberty:stop

#mv system/pom.xml.bak system/pom.xml
#mv inventory/pom.xml.bak inventory/pom.xml
#mv system/src/main/liberty/config/server.xml.bak system/src/main/liberty/config/server.xml
#mv inventory/src/main/liberty/config/server.xml.bak inventory/src/main/liberty/config/server.xml
#
#rm system/pom.xml.bak
#rm inventory/pom.xml.bak
#rm system/src/main/liberty/config/server.xml.bak
#rm inventory/src/main/liberty/config/server.xml.bak

