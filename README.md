# Keycloak

### Component Description
Keycloak with custom extension pack for the unbe1iev system (Custom Event Listener)
* Currently, we are using only a custom event listener as a provider, but we plan to expand this area in the future.
* We use external keycloak instance hosted on https://sso.unbe1iev.com/ so the local deployment needs to be set up by own 
* especially using `"start-dev"` (*development mode*) in Dockerfile build in ENTRYPOINT last statement and it ordinatory 
to generate temporary certification with:\
`RUN keytool -genkeypair -storepass password -storetype PKCS12 -keyalg RSA -keysize 2048 -dname "CN=server" -alias server -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -keystore conf/server.keystore`

### More settings:
* Realm: `unbe1iev`
* Use `custom-event-listener` in Events Tab
