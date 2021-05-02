package myta.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import myta.exception.MessageParseException;
import myta.message.model.EmailAddress;
import myta.message.model.Message;
import myta.message.model.Recipient;
import myta.message.model.RecipientType;

@Testable
class IncomingMessageParserTest {

    @Test
    public void testLoadMessageRequest() throws IOException {

        String messageFile = "message-invalid-empty.json";

        Map<String, Object> messageRequest = this.loadMessageRequest(messageFile);

        assertNotNull(messageRequest);

        assertEquals(0, messageRequest.size());

        messageFile = "message-valid-simple.json";

        messageRequest = this.loadMessageRequest(messageFile);

        assertNotNull(messageRequest);

        assertEquals(4, messageRequest.size());
        assertTrue(messageRequest.containsKey("from"));
        assertTrue(messageRequest.containsKey("to"));
        assertTrue(messageRequest.containsKey("subject"));
        assertTrue(messageRequest.containsKey("textBody"));

    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> loadMessageRequest(String messageFile) throws IOException {

        Map<String, Object> message = null;

        Object obj = null;

        File file = new File("src/test/resources/request/" + messageFile);

        FileInputStream inputStream = new FileInputStream(file);

        ObjectMapper mapper = new ObjectMapper();

        obj = mapper.readValue(inputStream, Object.class);

        if (obj != null) {

            message = (Map<String, Object>) obj;

        }

        return message;

    }

    @Test
    public void testSimpleValidMessage() throws IOException, MessageParseException {

        String messageFile = "message-valid-simple.json";

        Map<String, Object> messageRequest = this.loadMessageRequest(messageFile);

        assertNotNull(messageRequest);

        IncomingMessageParser parser = new IncomingMessageParser();

        Message message = parser.parseMessageRequest(messageRequest);

        assertNotNull(message);

        assertNotNull(message.getRecipients());
        assertEquals(99, message.getRecipients().size());

    }

    @Test
    public void testParseRecipients() throws MessageParseException {

        Map<String, Object> messageRequest = new LinkedHashMap<String, Object>();

        messageRequest.put("to", "info@example.com");

        IncomingMessageParser parser = new IncomingMessageParser();

        Collection<Recipient> recipients = parser.parseRecipients(messageRequest);

        assertNotNull(recipients);
        assertEquals(99, recipients.size());

    }

    @Test
    public void testParseRecipientNullPointer() {

        IncomingMessageParser parser = new IncomingMessageParser();

        Recipient recipient = null;

        try {

            recipient = parser.parseRecipient(null);
            fail("Expected exception when parsing empty recipient map");

        } catch (MessageParseException e) {

            assertNull(recipient);

        }

    }

    @Test
    public void testParseRecipientEmptyMap() {

        IncomingMessageParser parser = new IncomingMessageParser();

        Map<String, Object> recipientMap = new LinkedHashMap<String, Object>();

        Recipient recipient = null;

        try {

            recipient = parser.parseRecipient(recipientMap);
            fail("Expected exception when parsing empty recipient map");

        } catch (MessageParseException e) {

            assertNull(recipient);

        }

    }

    @Test
    public void testParseRecipientType() throws MessageParseException {

        IncomingMessageParser parser = new IncomingMessageParser();

        RecipientType recipientType = null;

        try {

            recipientType = parser.parseRecipientType(null);
            fail("Expected exception when parsing empty recipient type");

        } catch (MessageParseException e) {

            assertNull(recipientType);

        }

        try {

            recipientType = parser.parseRecipientType("");
            fail("Expected exception when parsing empty recipient type");

        } catch (MessageParseException e) {

            assertNull(recipientType);

        }

        // valid types
        recipientType = parser.parseRecipientType("to");
        assertNotNull(recipientType);
        assertEquals(RecipientType.TO, recipientType);

        recipientType = parser.parseRecipientType("cc");
        assertNotNull(recipientType);
        assertEquals(RecipientType.CC, recipientType);

        recipientType = parser.parseRecipientType("bcc");
        assertNotNull(recipientType);
        assertEquals(RecipientType.BCC, recipientType);

        recipientType = null;

        try {

            recipientType = parser.parseRecipientType("other");
            fail("Expected exception when parsing unknown recipient type");

        } catch (MessageParseException e) {

            assertNull(recipientType);

        }

    }

    @Test
    public void testParseEmailAddress() throws MessageParseException {

        IncomingMessageParser parser = new IncomingMessageParser();

        EmailAddress emailAddress = null;

        try {

            emailAddress = parser.parseEmailAddress(null);
            fail("Expected exception when parsing empty email address");

        } catch (MessageParseException e) {

            assertNull(emailAddress);

        }

        try {

            emailAddress = parser.parseEmailAddress("");
            fail("Expected exception when parsing empty email address");

        } catch (MessageParseException e) {

            assertNull(emailAddress);

        }

        emailAddress = parser.parseEmailAddress("info@example.com");
        assertNotNull(emailAddress);
        assertEquals("info@example.com", emailAddress.getEmail());
        assertNull(emailAddress.getName());

        emailAddress = parser.parseEmailAddress("<info@example.com>");
        assertNotNull(emailAddress);
        assertEquals("info@example.com", emailAddress.getEmail());
        assertNull(emailAddress.getName());

        emailAddress = parser.parseEmailAddress("Firstname Lastname <info@example.com>");
        assertNotNull(emailAddress);
        assertEquals("info@example.com", emailAddress.getEmail());
        assertNotNull(emailAddress.getName());
        assertEquals("Firstname Lastname", emailAddress.getName());

        // invalid values
        emailAddress = null;

        try {

            emailAddress = parser.parseEmailAddress("Firstname Lastname info@example.com");
            fail("Expected exception when parsing empty email address");

        } catch (MessageParseException e) {

            assertNull(emailAddress);

        }

    }

}
