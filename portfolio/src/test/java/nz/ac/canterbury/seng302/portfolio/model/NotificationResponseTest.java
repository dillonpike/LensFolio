package nz.ac.canterbury.seng302.portfolio.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationResponseTest {

    @Test
    void notificationResponseFromMessageTest() {
        String action = "add";
        NotificationMessage message = new NotificationMessage("artefactName", 1, 1, "userFirstName", "userLastName", "username", "artefactType");
        NotificationResponse response = NotificationResponse.fromMessage(message, action);
        assertEquals(message.getArtefactName(), response.getArtefactName());
        assertEquals(message.getArtefactId(), response.getArtefactId());
        assertEquals(message.getUserFirstName(), response.getUserFirstName());
        assertEquals(message.getUserLastName(), response.getUserLastName());
        assertEquals(message.getUsername(), response.getUsername());
        assertEquals(message.getArtefactType(), response.getArtefactType());
        assertEquals(action, response.getAction());
        assertTrue(Date.from(Instant.ofEpochSecond(response.getDateOfCreation())).before(Date.from(Instant.now())));
    }
}
