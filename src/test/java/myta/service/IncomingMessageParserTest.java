package myta.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import myta.exception.MessageParseException;
import myta.message.model.EmailAddress;
import myta.message.model.Header;
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

    public Map<String, Object> loadMessageRequest(String messageFile) throws IOException {

        Map<String, Object> message = null;

        File file = new File("src/test/resources/request/" + messageFile);

        FileInputStream inputStream = new FileInputStream(file);

        JsonMapParser parser = new JsonMapParser();

        message = parser.readJsonMap(inputStream);

        return message;

    }

    @Test
    public void testValidMessageSimple() throws IOException, MessageParseException {

        String messageFile = "message-valid-simple.json";

        Map<String, Object> messageRequest = this.loadMessageRequest(messageFile);

        assertNotNull(messageRequest);

        IncomingMessageParser parser = new IncomingMessageParser();

        Message message = parser.parseMessageRequest(messageRequest);

        assertNotNull(message);

        assertNotNull(message.getFrom());
        assertEquals("info@example.com", message.getFrom().getEmail());
        assertNull(message.getFrom().getName());

        assertNotNull(message.getRecipients());
        assertEquals(1, message.getRecipients().size());

        assertEquals(RecipientType.TO, message.getRecipients().get(0).getRecipientType());
        assertEquals("example@example.com", message.getRecipients().get(0).getEmailAddress().getEmail());
        assertNull(message.getRecipients().get(0).getEmailAddress().getName());

        assertEquals("Simple text body", message.getTextBody());
        assertNull(message.getHtmlBody());

    }

    @Test
    public void testValidMessageFull() throws IOException, MessageParseException {

        String messageFile = "message-valid-full.json";

        Map<String, Object> messageRequest = this.loadMessageRequest(messageFile);

        assertNotNull(messageRequest);

        IncomingMessageParser parser = new IncomingMessageParser();

        Message message = parser.parseMessageRequest(messageRequest);

        assertNotNull(message);

        assertNotNull(message.getFrom());
        assertEquals("from@example.com", message.getFrom().getEmail());
        assertNull(message.getFrom().getName());

        assertNotNull(message.getRecipients());
        assertEquals(3, message.getRecipients().size());

        assertEquals(RecipientType.TO, message.getRecipients().get(0).getRecipientType());
        assertEquals("to@example.com", message.getRecipients().get(0).getEmailAddress().getEmail());
        assertEquals("Firstname Lastname", message.getRecipients().get(0).getEmailAddress().getName());

        assertEquals(RecipientType.CC, message.getRecipients().get(1).getRecipientType());
        assertEquals("cc@example.com", message.getRecipients().get(1).getEmailAddress().getEmail());
        assertNull(message.getRecipients().get(1).getEmailAddress().getName());

        assertEquals(RecipientType.BCC, message.getRecipients().get(2).getRecipientType());
        assertEquals("bcc@example.com", message.getRecipients().get(2).getEmailAddress().getEmail());
        assertNull(message.getRecipients().get(2).getEmailAddress().getName());

        assertEquals("This is the text body", message.getTextBody());
        assertEquals("This is the <b>html</b> body", message.getHtmlBody());

        assertNotNull(message.getReplyToAddresses());
        assertEquals(2, message.getReplyToAddresses().size());
        assertEquals("replyto@example.com", message.getReplyToAddresses().get(0).getEmail());
        assertEquals("Firstname Lastname", message.getReplyToAddresses().get(0).getName());
        assertEquals("reply2@example.com", message.getReplyToAddresses().get(1).getEmail());
        assertEquals(null, message.getReplyToAddresses().get(1).getName());

        assertNotNull(message.getExtraHeaders());
        assertEquals(2, message.getExtraHeaders().size());

        assertEquals("Date", message.getExtraHeaders().get(0).getName());
        assertEquals("Sun, 02 May 2021 17:09:48 GMT", message.getExtraHeaders().get(0).getValue());
        assertEquals("Message-ID", message.getExtraHeaders().get(1).getName());
        assertEquals("<somemessageid@example.com>", message.getExtraHeaders().get(1).getValue());

    }

    @Test
    public void testInvalidEmptyMessage() throws IOException, MessageParseException {

        String messageFile = "message-invalid-empty.json";

        Map<String, Object> messageRequest = this.loadMessageRequest(messageFile);

        assertNotNull(messageRequest);

        IncomingMessageParser parser = new IncomingMessageParser();

        Message message = null;

        try {
            message = parser.parseMessageRequest(messageRequest);
            fail("Expected exception when parsing empty message");

        } catch (Exception e) {

            assertNull(message);
        }

    }

    @Test
    public void testParseRecipientsFlatSingleTo() throws MessageParseException {

        Map<String, Object> messageRequest = new LinkedHashMap<String, Object>();

        messageRequest.put("to", "info@example.com");

        IncomingMessageParser parser = new IncomingMessageParser();

        List<Recipient> recipients = parser.parseRecipients(messageRequest);

        assertNotNull(recipients);
        assertEquals(1, recipients.size());

        assertEquals(RecipientType.TO, recipients.get(0).getRecipientType());
        assertNotNull(recipients.get(0).getEmailAddress());
        assertEquals("info@example.com", recipients.get(0).getEmailAddress().getEmail());
        assertEquals(null, recipients.get(0).getEmailAddress().getName());

    }

    @Test
    public void testParseRecipientsFlatListTo() throws MessageParseException {

        Map<String, Object> messageRequest = new LinkedHashMap<String, Object>();

        List<String> toValue = new ArrayList<String>(2);
        toValue.add("Firstname Lastname <info@example.com>");
        toValue.add("other@example.com");

        messageRequest.put("to", toValue);

        IncomingMessageParser parser = new IncomingMessageParser();

        List<Recipient> recipients = parser.parseRecipients(messageRequest);

        assertNotNull(recipients);
        assertEquals(2, recipients.size());

        assertEquals(RecipientType.TO, recipients.get(0).getRecipientType());
        assertNotNull(recipients.get(0).getEmailAddress());
        assertEquals("info@example.com", recipients.get(0).getEmailAddress().getEmail());
        assertEquals("Firstname Lastname", recipients.get(0).getEmailAddress().getName());

        assertEquals(RecipientType.TO, recipients.get(1).getRecipientType());
        assertNotNull(recipients.get(1).getEmailAddress());
        assertEquals("other@example.com", recipients.get(1).getEmailAddress().getEmail());
        assertEquals(null, recipients.get(1).getEmailAddress().getName());

    }

    @Test
    public void testParseRecipientsMapList() throws MessageParseException {

        Map<String, Object> messageRequest = new LinkedHashMap<String, Object>();

        List<Object> recipientList = new ArrayList<Object>();

        Map<String, Object> recipient = new LinkedHashMap<String, Object>(2);
        recipient.put("type", "to");
        recipient.put("email", "Firstname Lastname <info@example.com>");

        recipientList.add(recipient);

        recipient = new LinkedHashMap<String, Object>(2);
        recipient.put("type", "to");
        recipient.put("email", "other@example.com");

        recipientList.add(recipient);

        recipient = new LinkedHashMap<String, Object>(2);
        recipient.put("type", "cc");
        recipient.put("email", "cc@example.com");

        recipientList.add(recipient);

        recipient = new LinkedHashMap<String, Object>(2);
        recipient.put("type", "bcc");
        recipient.put("email", "bcc@example.com");

        recipientList.add(recipient);

        messageRequest.put("recipients", recipientList);

        IncomingMessageParser parser = new IncomingMessageParser();

        List<Recipient> recipients = parser.parseRecipients(messageRequest);

        assertNotNull(recipients);
        assertEquals(4, recipients.size());

        assertEquals(RecipientType.TO, recipients.get(0).getRecipientType());
        assertNotNull(recipients.get(0).getEmailAddress());
        assertEquals("info@example.com", recipients.get(0).getEmailAddress().getEmail());
        assertEquals("Firstname Lastname", recipients.get(0).getEmailAddress().getName());

        assertEquals(RecipientType.TO, recipients.get(1).getRecipientType());
        assertNotNull(recipients.get(1).getEmailAddress());
        assertEquals("other@example.com", recipients.get(1).getEmailAddress().getEmail());
        assertEquals(null, recipients.get(1).getEmailAddress().getName());

        assertEquals(RecipientType.CC, recipients.get(2).getRecipientType());
        assertNotNull(recipients.get(2).getEmailAddress());
        assertEquals("cc@example.com", recipients.get(2).getEmailAddress().getEmail());
        assertEquals(null, recipients.get(2).getEmailAddress().getName());

        assertEquals(RecipientType.BCC, recipients.get(3).getRecipientType());
        assertNotNull(recipients.get(3).getEmailAddress());
        assertEquals("bcc@example.com", recipients.get(3).getEmailAddress().getEmail());
        assertEquals(null, recipients.get(3).getEmailAddress().getName());

    }

    @Test
    public void testParseRecipientsMap() throws MessageParseException {

        Map<String, Object> messageRequest = new LinkedHashMap<String, Object>();

        messageRequest.put("to", "info@example.com");

        IncomingMessageParser parser = new IncomingMessageParser();

        List<Recipient> recipients = parser.parseRecipients(messageRequest);

        assertNotNull(recipients);
        assertEquals(1, recipients.size());

        assertEquals(RecipientType.TO, recipients.get(0).getRecipientType());
        assertNotNull(recipients.get(0).getEmailAddress());
        assertEquals("info@example.com", recipients.get(0).getEmailAddress().getEmail());
        assertEquals(null, recipients.get(0).getEmailAddress().getName());

    }

    @Test
    public void testParseRecipientsList() throws MessageParseException {

        Map<String, Object> messageRequest = new LinkedHashMap<String, Object>();

        List<String> addresses = new ArrayList<String>(2);
        addresses.add("info@example.com");
        addresses.add("Firstname Lastname <other@example.com>");

        messageRequest.put("to", addresses);

        IncomingMessageParser parser = new IncomingMessageParser();

        List<Recipient> recipients = parser.parseRecipients(messageRequest);

        assertNotNull(recipients);
        assertEquals(2, recipients.size());

        assertEquals(RecipientType.TO, recipients.get(0).getRecipientType());
        assertNotNull(recipients.get(0).getEmailAddress());
        assertEquals("info@example.com", recipients.get(0).getEmailAddress().getEmail());
        assertEquals(null, recipients.get(0).getEmailAddress().getName());

        assertEquals(RecipientType.TO, recipients.get(1).getRecipientType());
        assertNotNull(recipients.get(1).getEmailAddress());
        assertEquals("other@example.com", recipients.get(1).getEmailAddress().getEmail());
        assertEquals("Firstname Lastname", recipients.get(1).getEmailAddress().getName());

    }

    @Test
    public void testParseRecipientEmailAddressesString() throws MessageParseException {

        IncomingMessageParser parser = new IncomingMessageParser();

        List<EmailAddress> emailAddresses = null;

        emailAddresses = parser.parseRecipientEmailAddresses("info@example.com");

        assertNotNull(emailAddresses);
        assertEquals(1, emailAddresses.size());
        assertEquals("info@example.com", emailAddresses.get(0).getEmail());
        assertEquals(null, emailAddresses.get(0).getName());

        emailAddresses = parser.parseRecipientEmailAddresses("Firstname Lastname <info@example.com>");

        assertNotNull(emailAddresses);
        assertEquals(1, emailAddresses.size());
        assertEquals("info@example.com", emailAddresses.get(0).getEmail());
        assertEquals("Firstname Lastname", emailAddresses.get(0).getName());

    }

    @Test
    public void testParseRecipientEmailAddressesList() throws MessageParseException {

        IncomingMessageParser parser = new IncomingMessageParser();

        List<String> recipientValue = new ArrayList<String>(2);
        recipientValue.add("info@example.com");
        recipientValue.add("Firstname Lastname <other@example.com>");

        List<EmailAddress> emailAddresses = null;

        emailAddresses = parser.parseRecipientEmailAddresses(recipientValue);

        assertNotNull(emailAddresses);
        assertEquals(2, emailAddresses.size());

        assertEquals("info@example.com", emailAddresses.get(0).getEmail());
        assertEquals(null, emailAddresses.get(0).getName());

        assertEquals("other@example.com", emailAddresses.get(1).getEmail());
        assertEquals("Firstname Lastname", emailAddresses.get(1).getName());

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

    @Test
    public void testParseReplyToAddressesString() throws MessageParseException {

        IncomingMessageParser parser = new IncomingMessageParser();

        Map<String, Object> requestMap = null;

        List<EmailAddress> replyToAddresses = parser.parseReplyToAddresses(requestMap);

        assertNotNull(replyToAddresses);
        assertEquals(0, replyToAddresses.size());

        requestMap = new LinkedHashMap<String, Object>();
        requestMap.put("replyTo", "info@example.com");

        replyToAddresses = parser.parseReplyToAddresses(requestMap);

        assertNotNull(replyToAddresses);
        assertEquals(1, replyToAddresses.size());
        assertEquals("info@example.com", replyToAddresses.get(0).getEmail());
        assertEquals(null, replyToAddresses.get(0).getName());

        requestMap.put("replyTo", "<info@example.com>");

        replyToAddresses = parser.parseReplyToAddresses(requestMap);

        assertNotNull(replyToAddresses);
        assertEquals(1, replyToAddresses.size());
        assertEquals("info@example.com", replyToAddresses.get(0).getEmail());
        assertEquals(null, replyToAddresses.get(0).getName());

        requestMap.put("replyTo", "Firstname Lastname <info@example.com>");

        replyToAddresses = parser.parseReplyToAddresses(requestMap);

        assertNotNull(replyToAddresses);
        assertEquals(1, replyToAddresses.size());
        assertEquals("info@example.com", replyToAddresses.get(0).getEmail());
        assertEquals("Firstname Lastname", replyToAddresses.get(0).getName());

    }

    @Test
    public void testParseReplyToAddressesList() throws MessageParseException {

        IncomingMessageParser parser = new IncomingMessageParser();

        Map<String, Object> requestMap = null;

        List<EmailAddress> replyToAddresses = parser.parseReplyToAddresses(requestMap);

        assertNotNull(replyToAddresses);
        assertEquals(0, replyToAddresses.size());

        List<String> replyToList = new ArrayList<String>();

        requestMap = new LinkedHashMap<String, Object>();
        requestMap.put("replyTo", replyToList);

        replyToAddresses = parser.parseReplyToAddresses(requestMap);

        assertNotNull(replyToAddresses);
        assertEquals(0, replyToAddresses.size());

        replyToList.add("info@example.com");

        replyToAddresses = parser.parseReplyToAddresses(requestMap);

        assertNotNull(replyToAddresses);
        assertEquals(1, replyToAddresses.size());
        assertEquals("info@example.com", replyToAddresses.get(0).getEmail());
        assertEquals(null, replyToAddresses.get(0).getName());

        replyToList.add("<other@example.com>");

        replyToAddresses = parser.parseReplyToAddresses(requestMap);

        assertNotNull(replyToAddresses);
        assertEquals(2, replyToAddresses.size());
        assertEquals("info@example.com", replyToAddresses.get(0).getEmail());
        assertEquals(null, replyToAddresses.get(0).getName());
        assertEquals("other@example.com", replyToAddresses.get(1).getEmail());
        assertEquals(null, replyToAddresses.get(1).getName());

        replyToList.add("Firstname Lastname <next@example.com>");

        replyToAddresses = parser.parseReplyToAddresses(requestMap);

        assertNotNull(replyToAddresses);
        assertEquals(3, replyToAddresses.size());
        assertEquals("info@example.com", replyToAddresses.get(0).getEmail());
        assertEquals(null, replyToAddresses.get(0).getName());
        assertEquals("other@example.com", replyToAddresses.get(1).getEmail());
        assertEquals(null, replyToAddresses.get(1).getName());
        assertEquals("next@example.com", replyToAddresses.get(2).getEmail());
        assertEquals("Firstname Lastname", replyToAddresses.get(2).getName());

    }

    @Test
    public void testParseExtraHeadersMapList() throws MessageParseException {

        Map<String, Object> messageRequest = new LinkedHashMap<String, Object>();

        IncomingMessageParser parser = new IncomingMessageParser();

        List<Header> headers = parser.parseExtraHeaders(messageRequest);

        assertNotNull(headers);
        assertEquals(0, headers.size());

        List<Object> headerList = new ArrayList<Object>();

        Map<String, Object> header = new LinkedHashMap<String, Object>(2);
        header.put("name", "Date");
        header.put("value", "Sun, 02 May 2021 17:09:48 GMT");

        headerList.add(header);

        header = new LinkedHashMap<String, Object>(2);
        header.put("name", "Message-ID");
        header.put("value", "<somemessageid@example.com>");

        headerList.add(header);

        messageRequest.put("extraHeaders", headerList);

        headers = parser.parseExtraHeaders(messageRequest);

        assertNotNull(headers);
        assertEquals(2, headers.size());

        assertEquals("Date", headers.get(0).getName());
        assertEquals("Sun, 02 May 2021 17:09:48 GMT", headers.get(0).getValue());
        assertEquals("Message-ID", headers.get(1).getName());
        assertEquals("<somemessageid@example.com>", headers.get(1).getValue());

    }

}
