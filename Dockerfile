FROM maven:3.9.1-eclipse-temurin-17-alpine as compiler

ENV MAVEN_CLI_OPTS '-B -DskipTests -Dmaven.repo.local=/opt/.m2/repository'

WORKDIR /tmp/maven

ADD pom.xml /tmp/maven
RUN mvn $MAVEN_CLI_OPTS verify --fail-never

ADD ./src /tmp/maven/src
RUN mvn $MAVEN_CLI_OPTS package

FROM quay.io/keycloak/keycloak:24.0.4 as builder

ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true
ENV KC_FEATURES=token-exchange
ENV KC_DB=mariadb

COPY --from=compiler /tmp/maven/target/unbe1iev-keycloak-extentions-1.0.0.jar /opt/keycloak/providers/
RUN /opt/keycloak/bin/kc.sh --verbose build --db=mariadb --transaction-xa-enabled=false

FROM quay.io/keycloak/keycloak:24.0.4
COPY --from=builder /opt/keycloak/ /opt/keycloak/

WORKDIR /opt/keycloak

# demonstration purposes only, use proper certificates in production instead
# RUN keytool -genkeypair -storepass password -storetype PKCS12 -keyalg RSA -keysize 2048 -dname "CN=server" -alias server -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -keystore conf/server.keystore

ENV KEYCLOAK_ADMIN=admin
ENV KEYCLOAK_ADMIN_PASSWORD=password

ENV KC_DB_URL=jdbc:mariadb://localhost:3306/keycloak
ENV KC_DB_USERNAME=keycloak
ENV KC_DB_PASSWORD=password

ENV KC_HOSTNAME=localhost

# use "start-dev" for development mode
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "--verbose", "start"]