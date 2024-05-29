package unbe1iev.com.keycloak.custom.event.listener;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class CustomEventListenerProviderFactory implements EventListenerProviderFactory {

    private static final Logger logger = Logger.getLogger(CustomEventListenerProviderFactory.class);
    private static final String CALLBACK_ENDPOINT_CONFIG_PARAMETER = "callbackEndpoint";
    private static final String PROVIDER_ID = "custom-event-listener";

    private String callbackUrl;

    @Override
    public EventListenerProvider create(KeycloakSession keycloakSession) {
        return new CustomEventListenerProvider(keycloakSession, callbackUrl);
    }

    @Override
    public void init(Config.Scope scope) {
        this.callbackUrl = scope.get(CALLBACK_ENDPOINT_CONFIG_PARAMETER);
        logger.info("CustomEventListenerProviderFactory init callback-endpoint to " + this.callbackUrl);
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
