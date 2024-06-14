package unbe1iev.com.keycloak.custom.event.listener;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class CustomEventListenerProviderFactory implements EventListenerProviderFactory {

    private static final String PROVIDER_ID = "custom-event-listener";

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new CustomEventListenerProvider(keycloakSession);
    }

    @Override
    public void init(Config.Scope scope) {
        //logger.info("CustomEventListenerProviderFactory init callback-endpoint to " + this.callbackUrl);
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        // nothing to do
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
