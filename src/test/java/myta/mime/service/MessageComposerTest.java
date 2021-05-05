package myta.mime.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import myta.message.model.EmailAddress;
import myta.message.model.Header;
import myta.message.model.Recipient;
import myta.message.model.RecipientType;

@Testable
public class MessageComposerTest {

    @Test
    public void testComposeMimeMessageTextOnlyBody() throws MessagingException, UnsupportedEncodingException {

        MessageComposer messageComposer = new MessageComposer();

        myta.message.model.Message message = new myta.message.model.Message();

        message.setTextBody("TEXT BODY");

        Properties prop = new Properties();
        Session session = Session.getInstance(prop);

        javax.mail.Message mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertTrue(this.getHeaderValue(mimeMessage, "Content-Type").startsWith("text/plain"));
        assertEquals("TEXT BODY", this.getBodyValue(mimeMessage));

    }

    @Test
    public void testComposeMimeMessageTextOnlyEmptyBody() throws MessagingException, UnsupportedEncodingException {

        MessageComposer messageComposer = new MessageComposer();

        myta.message.model.Message message = new myta.message.model.Message();

        message.setTextBody(null);

        Properties prop = new Properties();
        Session session = Session.getInstance(prop);

        javax.mail.Message mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertTrue(this.getHeaderValue(mimeMessage, "Content-Type").startsWith("text/plain"));
        assertNotNull(this.getBodyValue(mimeMessage));
        assertEquals("", this.getBodyValue(mimeMessage));

    }

    @Test
    public void testComposeMimeMessageHtmlOnlyBody() throws MessagingException, UnsupportedEncodingException {

        MessageComposer messageComposer = new MessageComposer();

        myta.message.model.Message message = new myta.message.model.Message();

        message.setHtmlBody("HTML <b>BODY</b>");

        Properties prop = new Properties();
        Session session = Session.getInstance(prop);

        javax.mail.Message mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertTrue(this.getHeaderValue(mimeMessage, "Content-Type").startsWith("text/html"));
        assertEquals("HTML <b>BODY</b>", this.getBodyValue(mimeMessage));

    }

    @Test
    public void testComposeMimeMessageHtmlAndTextBody() throws MessagingException, UnsupportedEncodingException {

        MessageComposer messageComposer = new MessageComposer();

        myta.message.model.Message message = new myta.message.model.Message();

        message.setTextBody("TEXT BODY");
        message.setHtmlBody("HTML <b>BODY</b>");

        Properties prop = new Properties();
        Session session = Session.getInstance(prop);

        javax.mail.Message mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertTrue(this.getHeaderValue(mimeMessage, "Content-Type").startsWith("multipart/alternative"));

        List<BodyPart> bodyParts = this.getMultiPartBodyParts(mimeMessage);

        assertNotNull(bodyParts);
        assertEquals(2, bodyParts.size());

        assertTrue(this.getHeaderValue(bodyParts.get(0), "Content-Type").startsWith("text/plain"));
        assertEquals("TEXT BODY", this.getBodyValue(bodyParts.get(0)));

        assertTrue(this.getHeaderValue(bodyParts.get(1), "Content-Type").startsWith("text/html"));
        assertEquals("HTML <b>BODY</b>", this.getBodyValue(bodyParts.get(1)));

    }

    @Test
    public void testComposeMimeMessageSubject() throws MessagingException, UnsupportedEncodingException {

        MessageComposer messageComposer = new MessageComposer();

        myta.message.model.Message message = new myta.message.model.Message();

        message.setSubject(null);

        Properties prop = new Properties();
        Session session = Session.getInstance(prop);

        javax.mail.Message mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertFalse(this.containsHeader(mimeMessage, "Subject"));

        // now with actual subject

        message.setSubject("SOME SUBJECT");

        mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertTrue(this.containsHeader(mimeMessage, "Subject"));

        assertEquals("SOME SUBJECT", this.getHeaderValue(mimeMessage, "Subject"));

    }

    @Test
    public void testComposeMimeMessageRecipientTo() throws MessagingException, UnsupportedEncodingException {

        MessageComposer messageComposer = new MessageComposer();

        myta.message.model.Message message = new myta.message.model.Message();

        message.setSubject(null);

        Properties prop = new Properties();
        Session session = Session.getInstance(prop);

        javax.mail.Message mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertFalse(this.containsHeader(mimeMessage, "To"));
        assertFalse(this.containsHeader(mimeMessage, "Cc"));
        assertFalse(this.containsHeader(mimeMessage, "Bcc"));

        // now with actual recipient

        List<Recipient> recipients = new ArrayList<Recipient>();
        recipients.add(new Recipient(RecipientType.TO, "info@example.com"));

        message.setRecipients(recipients);

        mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertTrue(this.containsHeader(mimeMessage, "To"));
        assertFalse(this.containsHeader(mimeMessage, "Cc"));
        assertFalse(this.containsHeader(mimeMessage, "Bcc"));

        assertEquals("info@example.com", this.getHeaderValue(mimeMessage, "To"));

        // now with second recipient with name
        recipients.add(new Recipient(RecipientType.TO, new EmailAddress("other@example.com", "Firstname Lastname")));

        mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertTrue(this.containsHeader(mimeMessage, "To"));
        assertFalse(this.containsHeader(mimeMessage, "Cc"));
        assertFalse(this.containsHeader(mimeMessage, "Bcc"));

        assertEquals("info@example.com, Firstname Lastname <other@example.com>", this.getHeaderValue(mimeMessage, "To"));

    }

    @Test
    public void testComposeMimeMessageRecipientCc() throws MessagingException, UnsupportedEncodingException {

        MessageComposer messageComposer = new MessageComposer();

        myta.message.model.Message message = new myta.message.model.Message();

        message.setSubject(null);

        Properties prop = new Properties();
        Session session = Session.getInstance(prop);

        javax.mail.Message mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertFalse(this.containsHeader(mimeMessage, "To"));
        assertFalse(this.containsHeader(mimeMessage, "Cc"));
        assertFalse(this.containsHeader(mimeMessage, "Bcc"));

        // now with actual recipient

        List<Recipient> recipients = new ArrayList<Recipient>();
        recipients.add(new Recipient(RecipientType.CC, "info@example.com"));

        message.setRecipients(recipients);

        mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertFalse(this.containsHeader(mimeMessage, "To"));
        assertTrue(this.containsHeader(mimeMessage, "Cc"));
        assertFalse(this.containsHeader(mimeMessage, "Bcc"));

        assertEquals("info@example.com", this.getHeaderValue(mimeMessage, "Cc"));

        // now with second recipient with name
        recipients.add(new Recipient(RecipientType.CC, new EmailAddress("other@example.com", "Firstname Lastname")));

        mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertFalse(this.containsHeader(mimeMessage, "To"));
        assertTrue(this.containsHeader(mimeMessage, "Cc"));
        assertFalse(this.containsHeader(mimeMessage, "Bcc"));

        assertEquals("info@example.com, Firstname Lastname <other@example.com>", this.getHeaderValue(mimeMessage, "Cc"));

    }

    @Test
    public void testComposeMimeMessageRecipientBcc() throws MessagingException, UnsupportedEncodingException {

        MessageComposer messageComposer = new MessageComposer();

        myta.message.model.Message message = new myta.message.model.Message();

        message.setSubject(null);

        Properties prop = new Properties();
        Session session = Session.getInstance(prop);

        javax.mail.Message mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertFalse(this.containsHeader(mimeMessage, "To"));
        assertFalse(this.containsHeader(mimeMessage, "Cc"));
        assertFalse(this.containsHeader(mimeMessage, "Bcc"));

        // now with actual recipient

        List<Recipient> recipients = new ArrayList<Recipient>();
        recipients.add(new Recipient(RecipientType.BCC, "info@example.com"));

        message.setRecipients(recipients);

        mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertFalse(this.containsHeader(mimeMessage, "To"));
        assertFalse(this.containsHeader(mimeMessage, "Cc"));
        assertTrue(this.containsHeader(mimeMessage, "Bcc"));

        assertEquals("info@example.com", this.getHeaderValue(mimeMessage, "Bcc"));

        // now with second recipient with name
        recipients.add(new Recipient(RecipientType.BCC, new EmailAddress("other@example.com", "Firstname Lastname")));

        mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertFalse(this.containsHeader(mimeMessage, "To"));
        assertFalse(this.containsHeader(mimeMessage, "Cc"));
        assertTrue(this.containsHeader(mimeMessage, "Bcc"));

        assertEquals("info@example.com, Firstname Lastname <other@example.com>", this.getHeaderValue(mimeMessage, "Bcc"));

    }

    @Test
    public void testComposeMimeMessageReplyToAddresses() throws MessagingException, UnsupportedEncodingException {

        MessageComposer messageComposer = new MessageComposer();

        myta.message.model.Message message = new myta.message.model.Message();

        message.setReplyToAddresses(null);

        Properties prop = new Properties();
        Session session = Session.getInstance(prop);

        javax.mail.Message mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertFalse(this.containsHeader(mimeMessage, "Reply-To"));

        List<EmailAddress> replyToAddresses = new ArrayList<EmailAddress>();
        replyToAddresses.add(new EmailAddress("info@example.com"));

        // now with single reply-to
        message.setReplyToAddresses(replyToAddresses);

        mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertTrue(this.containsHeader(mimeMessage, "Reply-To"));
        assertEquals("info@example.com", this.getHeaderValue(mimeMessage, "Reply-To"));

        // now another with name
        replyToAddresses.add(new EmailAddress("other@example.com", "Firstname Lastname"));

        mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertTrue(this.containsHeader(mimeMessage, "Reply-To"));
        assertEquals("info@example.com, Firstname Lastname <other@example.com>", this.getHeaderValue(mimeMessage, "Reply-To"));

    }

    @Test
    public void testComposeMimeMessageFrom() throws MessagingException, UnsupportedEncodingException {

        MessageComposer messageComposer = new MessageComposer();

        myta.message.model.Message message = new myta.message.model.Message();

        message.setReplyToAddresses(null);

        Properties prop = new Properties();
        Session session = Session.getInstance(prop);

        javax.mail.Message mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertFalse(this.containsHeader(mimeMessage, "From"));

        // now with From address

        message.setFrom(new EmailAddress("info@example.com"));

        mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertTrue(this.containsHeader(mimeMessage, "From"));
        assertEquals("info@example.com", this.getHeaderValue(mimeMessage, "From"));

        // now with name

        message.setFrom(new EmailAddress("info@example.com", "Firstname Lastname"));

        mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertTrue(this.containsHeader(mimeMessage, "From"));
        assertEquals("Firstname Lastname <info@example.com>", this.getHeaderValue(mimeMessage, "From"));

    }

    @Test
    public void testComposeMimeMessageExtraHeaders() throws MessagingException, UnsupportedEncodingException {

        MessageComposer messageComposer = new MessageComposer();

        myta.message.model.Message message = new myta.message.model.Message();

        message.setReplyToAddresses(null);

        Properties prop = new Properties();
        Session session = Session.getInstance(prop);

        javax.mail.Message mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertFalse(this.containsHeader(mimeMessage, "Date"));
        assertTrue(this.containsHeader(mimeMessage, "Message-ID"));
        assertNotEquals("<SOMEID@example.com>", this.getHeaderValue(mimeMessage, "Message-ID"));

        List<Header> extraHeaders = new ArrayList<Header>();
        message.setExtraHeaders(extraHeaders);

        mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertFalse(this.containsHeader(mimeMessage, "Date"));
        assertTrue(this.containsHeader(mimeMessage, "Message-ID"));
        assertNotEquals("<SOMEID@example.com>", this.getHeaderValue(mimeMessage, "Message-ID"));

        // now with actual headers
        extraHeaders.add(new Header("Date", "Sat, 1 May 2021 16:55:27 +0200"));
        extraHeaders.add(new Header("Message-ID", "<SOMEID@example.com>"));

        mimeMessage = messageComposer.composeMimeMessage(session, message);

        assertNotNull(mimeMessage);

        assertTrue(this.containsHeader(mimeMessage, "Date"));
        assertEquals("Sat, 1 May 2021 16:55:27 +0200", this.getHeaderValue(mimeMessage, "Date"));
        assertEquals(1619880927000L, mimeMessage.getSentDate().getTime());
        assertTrue(this.containsHeader(mimeMessage, "Message-ID"));
        assertEquals("<SOMEID@example.com>", this.getHeaderValue(mimeMessage, "Message-ID"));

    }

    public String getHeaderValue(javax.mail.Message mimeMessage, String headerName) {

        String headerValue = null;

        String[] headerValues = null;

        try {
            headerValues = mimeMessage.getHeader(headerName);
        } catch (MessagingException e) {
        }

        if ((headerValues != null) && (headerValues.length > 0)) {

            headerValue = headerValues[0];

        }

        return headerValue;

    }

    public String getHeaderValue(javax.mail.BodyPart bodyPart, String headerName) {

        String headerValue = null;

        String[] headerValues = null;

        try {
            headerValues = bodyPart.getHeader(headerName);
        } catch (MessagingException e) {
        }

        if ((headerValues != null) && (headerValues.length > 0)) {

            headerValue = headerValues[0];

        }

        return headerValue;

    }

    public boolean containsHeader(javax.mail.Message mimeMessage, String headerName) {

        boolean containsHeader = false;

        String[] headerValues = null;

        try {
            headerValues = mimeMessage.getHeader(headerName);
        } catch (MessagingException e) {
        }

        if ((headerValues != null) && (headerValues.length > 0)) {

            containsHeader = true;

        }

        return containsHeader;

    }

    public String getBodyValue(javax.mail.Message mimeMessage) {

        String bodyValue = null;

        Object contentObject = null;

        try {

            contentObject = mimeMessage.getContent();

        } catch (Exception e) {
        }

        if (contentObject != null) {

            if (contentObject instanceof String) {

                bodyValue = contentObject.toString();

            }

        }

        return bodyValue;

    }

    public String getBodyValue(javax.mail.BodyPart bodyPart) {

        String bodyValue = null;

        Object contentObject = null;

        try {

            contentObject = bodyPart.getContent();

        } catch (Exception e) {
        }

        if (contentObject != null) {

            if (contentObject instanceof String) {

                bodyValue = contentObject.toString();

            }

        }

        return bodyValue;

    }

    public List<BodyPart> getMultiPartBodyParts(javax.mail.Message mimeMessage) {

        List<BodyPart> bodyParts = null;

        Object contentObject = null;

        try {

            contentObject = mimeMessage.getContent();

        } catch (Exception e) {
        }

        if (contentObject != null) {

            if (contentObject instanceof javax.mail.internet.MimeMultipart) {

                javax.mail.internet.MimeMultipart multiPart = (javax.mail.internet.MimeMultipart) contentObject;
                int count = 0;

                try {
                    count = multiPart.getCount();
                } catch (MessagingException e) {
                }

                if (count > 0) {

                    bodyParts = new ArrayList<BodyPart>(count);

                    for (int i = 0; i < count; i++) {

                        BodyPart bodyPart = null;

                        try {
                            bodyPart = multiPart.getBodyPart(i);
                        } catch (MessagingException e) {
                        }

                        if (bodyPart != null) {

                            bodyParts.add(bodyPart);

                        }

                    }

                }

            }

        }

        return bodyParts;

    }

}
