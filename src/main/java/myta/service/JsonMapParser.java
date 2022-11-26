package myta.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

public class JsonMapParser {
    
    public Map<String,Object> readJsonMap(InputStream inputStream) {

        Map<String,Object> jsonMap = null;

        JsonReader jsonReader = Json.createReader(inputStream);
        JsonObject jobj = jsonReader.readObject();      

        jsonReader.close();

        if (jobj != null) {

            jsonMap = this.parseJsonMap(jobj);

        }

        return jsonMap;

    }

    public Map<String,Object> parseJsonMap(JsonObject jsonObject) {

        Map<String,Object> jsonMap = null;

        if (jsonObject != null) {

            jsonMap = new LinkedHashMap<String,Object> (jsonObject.size());

            Set<String> keys = jsonObject.keySet();

            for (String key : keys) {

                JsonValue value = jsonObject.get(key);

                Object parsedValue = this.parseJsonValue(value);

                jsonMap.put(key, parsedValue);

            }

        }

        return jsonMap;

    }

    public Object parseJsonValue(JsonValue jsonValue) {

        Object value = null;

        if (jsonValue instanceof JsonString) {

            JsonString jsonString = (JsonString)jsonValue;

            value = jsonString.getString();

        } else if (jsonValue instanceof JsonNumber) {

            JsonNumber childNumber = (JsonNumber) jsonValue;

            if (childNumber.isIntegral()) {

                value = childNumber.bigIntegerValue();

            } else {

                value = childNumber.bigDecimalValue();

            }

        } else if (jsonValue instanceof JsonArray) {

            JsonArray childArray = (JsonArray) jsonValue;

            List<Object> parsedArray = new ArrayList<Object>(childArray.size());

            for (JsonValue childValue : childArray) {

                Object parsedValue = this.parseJsonValue(childValue);

                parsedArray.add(parsedValue);

            }

            value = parsedArray;

        } else if (jsonValue instanceof JsonObject) {

            JsonObject childObject = (JsonObject) jsonValue;

            value = this.parseJsonMap(childObject);

        }

        return value;

    }

}
