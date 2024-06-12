#!/bin/bash
set -euxo pipefail

cd finish
mvn -pl models clean install

mvn -ntp -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl system clean package liberty:create liberty:install-feature liberty:deploy

mvn -ntp -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl inventory clean package liberty:create liberty:install-feature liberty:deploy

mvn -ntp -pl system liberty:start
mvn -ntp -pl inventory liberty:start

mvn -ntp -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl system failsafe:integration-test
mvn -ntp -Dhttp.keepAlive=false \
    -Dmaven.wagon.http.pool=false \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -pl inventory failsafe:integration-test

mvn -ntp -pl system failsafe:verify
mvn -ntp -pl inventory failsafe:verify

mvn -pl system liberty:stop
mvn -pl inventory liberty:stop
