package myta.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

@Testable
class JsonMapParserTest {

    @Test
    public void testLoadMessageRequestEmpty() throws IOException {

        String messageFile = "message-invalid-empty.json";

        File file = new File("src/test/resources/request/" + messageFile);

        FileInputStream inputStream = new FileInputStream(file);

        JsonMapParser parser = new JsonMapParser();

        Map<String, Object> parsedMap = parser.readJsonMap(inputStream);

        assertNotNull(parsedMap);

        assertEquals(0, parsedMap.size());

    }

    @Test
    public void testLoadMessageRequestSimple() throws IOException {

        String messageFile = "message-valid-simple.json";

        File file = new File("src/test/resources/request/" + messageFile);

        FileInputStream inputStream = new FileInputStream(file);

        JsonMapParser parser = new JsonMapParser();

        Map<String, Object> parsedMap = parser.readJsonMap(inputStream);

        assertNotNull(parsedMap);

        assertEquals(4, parsedMap.size());
        assertTrue(parsedMap.containsKey("from"));
        assertTrue(parsedMap.containsKey("to"));
        assertTrue(parsedMap.containsKey("subject"));
        assertTrue(parsedMap.containsKey("textBody"));

    }    

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadMessageRequestFull() throws IOException {

        String messageFile = "message-valid-full.json";

        File file = new File("src/test/resources/request/" + messageFile);

        FileInputStream inputStream = new FileInputStream(file);

        JsonMapParser parser = new JsonMapParser();

        Map<String, Object> parsedMap = parser.readJsonMap(inputStream);

        assertNotNull(parsedMap);

        assertEquals(8, parsedMap.size());
        assertTrue(parsedMap.containsKey("from"));
        assertTrue(parsedMap.containsKey("recipients"));
        assertTrue(parsedMap.containsKey("subject"));
        assertTrue(parsedMap.containsKey("htmlBody"));
        assertTrue(parsedMap.containsKey("textBody"));
        assertTrue(parsedMap.containsKey("returnPath"));
        assertTrue(parsedMap.containsKey("replyTo"));
        assertTrue(parsedMap.containsKey("extraHeaders"));

        Object recipients = parsedMap.get("recipients");

        assertTrue(recipients instanceof List);

        List<Object> recipientsList = (List<Object>)recipients;

        assertEquals(3, recipientsList.size());

        Object recipient = recipientsList.get(0);

        assertTrue(recipient instanceof Map);

        Map<String,Object> recipientMap = (Map<String,Object>)recipient;

        assertEquals(2, recipientMap.size());

        assertTrue(recipientMap.containsKey("type"));
        assertTrue(recipientMap.containsKey("email"));

        assertEquals("to", recipientMap.get("type"));
        assertEquals("Firstname Lastname <to@example.com>", recipientMap.get("email"));

        recipient = recipientsList.get(1);

        assertTrue(recipient instanceof Map);

        recipientMap = (Map<String,Object>)recipient;

        assertEquals(2, recipientMap.size());

        assertTrue(recipientMap.containsKey("type"));
        assertTrue(recipientMap.containsKey("email"));

        assertEquals("cc", recipientMap.get("type"));
        assertEquals("cc@example.com", recipientMap.get("email"));  

        recipient = recipientsList.get(2);

        assertTrue(recipient instanceof Map);

        recipientMap = (Map<String,Object>)recipient;

        assertEquals(2, recipientMap.size());

        assertTrue(recipientMap.containsKey("type"));
        assertTrue(recipientMap.containsKey("email"));

        assertEquals("bcc", recipientMap.get("type"));
        assertEquals("bcc@example.com", recipientMap.get("email"));  

        assertEquals("Test subject", parsedMap.get("subject"));
        assertEquals("This is the <b>html</b> body", parsedMap.get("htmlBody"));
        assertEquals("This is the text body", parsedMap.get("textBody"));
        assertEquals("bounces@example.com", parsedMap.get("returnPath"));

        Object replyToObject = parsedMap.get("replyTo");

        assertTrue(replyToObject instanceof List);

        List<Object> replyToList = (List<Object>) replyToObject;

        assertEquals(2, replyToList.size());
        assertEquals("Firstname Lastname <replyto@example.com>", replyToList.get(0));
        assertEquals("reply2@example.com", replyToList.get(1));

        Object extraHeadersObject = parsedMap.get("extraHeaders");

        assertTrue(extraHeadersObject instanceof List);

        List<Object> extraHeadersList = (List<Object>)extraHeadersObject;

        assertEquals(2, extraHeadersList.size());

        Object extraHeader = extraHeadersList.get(0);

        assertTrue(extraHeader instanceof Map);

        Map<String,Object> extraHeaderMap = (Map<String,Object>)extraHeader;

        assertEquals(2, extraHeaderMap.size());

        assertTrue(extraHeaderMap.containsKey("name"));
        assertTrue(extraHeaderMap.containsKey("value"));

        assertEquals("Date", extraHeaderMap.get("name"));
        assertEquals("Sun, 02 May 2021 17:09:48 GMT", extraHeaderMap.get("value"));        

        extraHeader = extraHeadersList.get(1);

        assertTrue(extraHeader instanceof Map);

        extraHeaderMap = (Map<String,Object>)extraHeader;

        assertEquals(2, extraHeaderMap.size());

        assertTrue(extraHeaderMap.containsKey("name"));
        assertTrue(extraHeaderMap.containsKey("value"));

        assertEquals("Message-ID", extraHeaderMap.get("name"));
        assertEquals("<somemessageid@example.com>", extraHeaderMap.get("value"));              

    }       

}
