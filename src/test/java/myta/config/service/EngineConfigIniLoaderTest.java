package myta.config.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

@Testable
public class EngineConfigIniLoaderTest {

    @Test
    public void testLoadIniFile() throws IOException {

        Map<String, Map<String, String>> loadedIniFile = this.loadIniFile("config-valid-dkim-mapping.ini");

        assertNotNull(loadedIniFile);

        assertEquals(3, loadedIniFile.size());

        assertTrue(loadedIniFile.containsKey("main"));
        assertTrue(loadedIniFile.containsKey("dkim"));
        assertTrue(loadedIniFile.containsKey("dkim-mapping"));

        assertEquals(0, loadedIniFile.get("main").size());
        assertEquals(0, loadedIniFile.get("dkim").size());
        assertEquals(2, loadedIniFile.get("dkim-mapping").size());

        assertTrue(loadedIniFile.get("dkim-mapping").containsKey("info@example.com"));
        assertTrue(loadedIniFile.get("dkim-mapping").containsKey("*@example.com"));

        assertEquals("example.com,someselector,/path/to/some/key.pem", loadedIniFile.get("dkim-mapping").get("info@example.com"));
        assertEquals("example.com,wildcard,/path/to/wildcard/key.pem", loadedIniFile.get("dkim-mapping").get("*@example.com"));

    }

    public Map<String, Map<String, String>> loadIniFile(String iniFile) throws IOException {

        Map<String, Map<String, String>> loadedIniFile = null;

        File file = new File("src/test/resources/config/" + iniFile);

        FileInputStream inputStream = new FileInputStream(file);

        EngineConfigIniLoader iniLoader = new EngineConfigIniLoader();

        loadedIniFile = iniLoader.loadIniFile(inputStream);

        return loadedIniFile;

    }

}
