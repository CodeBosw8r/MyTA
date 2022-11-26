package myta.servlet;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.Test;
import org.junit.platform.commons.annotation.Testable;

@Testable
public class PostMessageFilterTest {

    @Test
    public void testParseJsonRequestBodyMessageValidSimple() throws FileNotFoundException, IOException {

        PostMessageFilter filter = new PostMessageFilter();

        InputStream inputStream = this.createInputStream("message-valid-simple.json");

        assertNotNull(inputStream);

        Object parsedRequestBody = filter.parseJsonRequestBody(inputStream);

        assertNotNull(parsedRequestBody);

        assertTrue(parsedRequestBody instanceof Map);

        @SuppressWarnings("unchecked")
        Map<Object,Object> parsedRequestBodyMap = (Map<Object,Object>)parsedRequestBody;

        assertTrue(parsedRequestBodyMap.containsKey("from"));
        assertTrue(parsedRequestBodyMap.containsKey("to"));
        assertTrue(parsedRequestBodyMap.containsKey("subject"));
        assertTrue(parsedRequestBodyMap.containsKey("textBody"));

    }

    @Test
    public void testParseJsonRequestBodyMessageInvalidEmpty() throws FileNotFoundException, IOException {

        PostMessageFilter filter = new PostMessageFilter();

        InputStream inputStream = this.createInputStream("message-invalid-empty.json");

        assertNotNull(inputStream);

        Object parsedRequestBody = filter.parseJsonRequestBody(inputStream);

        assertNotNull(parsedRequestBody);

        assertTrue(parsedRequestBody instanceof Map);

        @SuppressWarnings("unchecked")
        Map<Object,Object> parsedRequestBodyMap = (Map<Object,Object>)parsedRequestBody;

        assertEquals(0, parsedRequestBodyMap.size());

    }

    public InputStream createInputStream(String messageFile) throws FileNotFoundException {

        File file = new File("src/test/resources/request/" + messageFile);

        FileInputStream inputStream = new FileInputStream(file);

        return inputStream;

    }

}
