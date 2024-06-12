#!/bin/bash
set -euxo pipefail

mvn -ntp -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -q clean package liberty:create liberty:install-feature liberty:deploy -pl system -pl inventory

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
