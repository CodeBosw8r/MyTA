package myta.config.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (inputStream != null) {

                iniConfig = this.loadIniFile(inputStream);

                try {
                    inputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
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

    public Map<String, Map<String, String>> loadIniFile(InputStream inputStream) {

        Map<String, Map<String, String>> iniFile = null;

        Ini ini = null;

        try {
            ini = new Ini(inputStream);
        } catch (InvalidFileFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (ini != null) {

            for (Entry<String, Section> iniEntry : ini.entrySet()) {

                Section section = iniEntry.getValue();

                String sectionName = section.getName();

                if (iniFile == null) {

                    iniFile = new LinkedHashMap<String, Map<String, String>>(ini.size());

                }

                if (!iniFile.containsKey(sectionName)) {

                    iniFile.put(sectionName, new LinkedHashMap<String, String>(section.size()));

                }

                for (Entry<String, String> sectionEntry : section.entrySet()) {

                    iniFile.get(sectionName).put(sectionEntry.getKey(), sectionEntry.getValue());

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
