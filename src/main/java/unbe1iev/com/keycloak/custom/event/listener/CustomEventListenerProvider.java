package unbe1iev.com.keycloak.custom.event.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import java.io.IOException;

import static org.keycloak.events.EventType.LOGIN;

public class CustomEventListenerProvider implements EventListenerProvider {

    private static final Logger logger = Logger.getLogger(CustomEventListenerProvider.class);

    private final String baseUrl;
    private final String sharedSecretValue;
    private final KeycloakSession session;
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    private final ObjectMapper mapper = new ObjectMapper();

    CustomEventListenerProvider(KeycloakSession session, String baseUrl, String sharedSecretValue) {
        this.session = session;
        this.baseUrl = baseUrl;
        this.sharedSecretValue = sharedSecretValue;
    }

    private HttpResponse sendEvent(Object event) throws IOException {
        HttpPost request = new HttpPost(baseUrl);

        String requestBody = mapper.writeValueAsString(event);
        StringEntity entity = new StringEntity(requestBody);
        entity.setContentType("application/json");
        request.setHeader("X-Shared-Secret", sharedSecretValue);
        request.setEntity(entity);

        return httpClient.execute(request);
    }

    @Override
    public void onEvent(Event event) {
        removeOtherSessions(event);

        try {
            HttpResponse response;
            switch (event.getType()) {
                case LOGIN, LOGOUT, REFRESH_TOKEN, REGISTER:
                    response = sendEvent(event);
                    logger.info("Sent '" + event.getType() + "' event to callback backend service with response: " + response);
                    break;
                default:
            }
        } catch (IOException e) {
            logger.error("An exception occurred during sending [Event] to backend service: ", e);
        }
    }

    private void removeOtherSessions(Event event) {
        if (event.getType().equals(LOGIN)) {
            try {
                RealmModel realmModel = session.getContext().getRealm();
                UserModel userModel = session.users().getUserById(realmModel, event.getUserId());
                logger.info("Removing existing sessions for user " + userModel.getUsername() + " within realm " + realmModel.getName());
                session.sessions().getUserSessionsStream(realmModel, userModel).forEach(userSession -> {
                    if (!userSession.getId().equals(event.getSessionId())) {
                        logger.info("removing session " + userSession.getId());
                        session.sessions().removeUserSession(realmModel, userSession);
                    }
                });
            } catch (Exception e) {
                logger.error("An exception occurred during cancelling other sessions", e);
            }
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        // nothing to do
    }

    @Override
    public void close() {
        try {
            httpClient.close();
        } catch (IOException e) {
            logger.error("Error closing HTTP client: ", e);
        }
    }
}
