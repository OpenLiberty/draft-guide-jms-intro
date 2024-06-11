#!/bin/bash
set -euxo pipefail

cd ../
mvn -pl models install
mvn -pl inventory liberty:start
mvn -pl system liberty:start

mvn -ntp -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    failsafe:integration-test
mvn -ntp failsafe:verify

mvn -pl inventory liberty:stop
mvn -pl system liberty:stop
