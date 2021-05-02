package myta.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.Test;

class RequestMessageParserTest {

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

}
