package myta.config.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import myta.config.model.EngineConfig;
import myta.dkim.model.DkimKey;

public class EngineConfigIniLoader {

    public EngineConfig loadEngineConfig(String iniFile) {

        File configFile = new File(iniFile);

        return this.loadEngineConfig(configFile);

    }

    public EngineConfig loadEngineConfig(File configFile) {

        EngineConfig engineConfig = new EngineConfig();

        Map<String, Map<String, String>> iniConfig = new HashMap<String, Map<String, String>>(0);

        if ((configFile != null) && configFile.exists()) {

            FileInputStream inputStream = null;

            try {
                inputStream = new FileInputStream(configFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (inputStream != null) {

                try {

                    iniConfig = this.loadIniFile(inputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

        if (iniConfig.containsKey("dkim-mapping")) {

            Map<String, String> dkimMappingConfig = iniConfig.get("dkim-mapping");

            Map<String, DkimKey> dkimMapping = this.loadDkimMapping(dkimMappingConfig);

            engineConfig.setDkimKeyMapping(dkimMapping);

        }

        return engineConfig;

    }

    public Map<String, Map<String, String>> loadIniFile(InputStream inputStream)
            throws IOException, ConfigurationException {

        Map<String, Map<String, String>> iniFile = null;

        Reader reader = new InputStreamReader(inputStream);

        INIConfiguration iniConfiguration = new INIConfiguration();
        iniConfiguration.read(reader);

        Set<String> sections = iniConfiguration.getSections();

        if (sections != null) {

            for (String sectionName : sections) {

                if (iniFile == null) {

                    iniFile = new LinkedHashMap<String, Map<String, String>>(sections.size());

                }

                SubnodeConfiguration section = iniConfiguration.getSection(sectionName);

                if (!iniFile.containsKey(sectionName)) {

                    iniFile.put(sectionName, new LinkedHashMap<String, String>(section.size()));

                }

                Iterator<String> keyIterator = section.getKeys();

                while (keyIterator.hasNext()) {
                    String key = keyIterator.next();
                    String value = section.getProperty(key).toString();
                    iniFile.get(sectionName).put(key.replace("..", "."), value);
                }

            }

        }

        return iniFile;

    }

    public Map<String, DkimKey> loadDkimMapping(Map<String, String> dkimMappingConfig) {

        Map<String, DkimKey> dkimKeyMapping = null;

        if ((dkimMappingConfig != null) && (dkimMappingConfig.size() > 0)) {

            dkimKeyMapping = new LinkedHashMap<String, DkimKey>(dkimMappingConfig.size());

            for (Entry<String, String> entry : dkimMappingConfig.entrySet()) {

                String name = entry.getKey();

                String value = entry.getValue();

                String[] parts = value.split(",", 3);

                if (parts.length == 3) {

                    String domain = parts[0];
                    String selector = parts[1];
                    String file = parts[2];

                    DkimKey dkimKey = null;

                    File privateKeyFile = new File(file);

                    if (privateKeyFile.exists()) {

                        RSAPrivateKey privateKey = null;

                        try {
                            privateKey = this.readPrivateKey(privateKeyFile);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        if (privateKey != null) {

                            dkimKey = new DkimKey(domain, selector, privateKey);

                        }

                    }

                    if (dkimKey != null) {

                        dkimKeyMapping.put(name, dkimKey);

                    }

                }

            }

        }

        return dkimKeyMapping;

    }

    public RSAPrivateKey readPrivateKey(File file) throws Exception {
        KeyFactory factory = KeyFactory.getInstance("RSA");

        try (FileReader keyReader = new FileReader(file); PemReader pemReader = new PemReader(keyReader)) {

            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(content);
            return (RSAPrivateKey) factory.generatePrivate(privateKeySpec);
        }
    }

}
