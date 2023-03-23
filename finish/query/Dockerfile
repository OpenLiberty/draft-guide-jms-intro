FROM icr.io/appcafe/open-liberty:full-java11-openj9-ubi

ARG VERSION=1.0
ARG REVISION=SNAPSHOT

LABEL \
  org.opencontainers.image.authors="My Name" \
  org.opencontainers.image.vendor="Open Liberty" \
  org.opencontainers.image.url="local" \
  org.opencontainers.image.source="https://github.com/OpenLiberty/guide-jms-cqrs" \
  org.opencontainers.image.version="$VERSION" \
  org.opencontainers.image.revision="$REVISION" \
  vendor="Open Liberty" \
  name="query" \
  version="$VERSION-$REVISION" \
  summary="The query microservice from the Jakarta Messaging CQRS guide" \
  description="This image contains the query microservice running with the Open Liberty runtime."

COPY --chown=1001:0 src/main/liberty/config /config/
COPY --chown=1001:0 target/guide-jms-cqrs-query.war /config/apps
COPY --chown=1001:0 target/liberty/wlp/usr/shared/resources/*.* /opt/ol/wlp/usr/shared/resources

EXPOSE 9082 9445 7277 9101

RUN configure.sh
