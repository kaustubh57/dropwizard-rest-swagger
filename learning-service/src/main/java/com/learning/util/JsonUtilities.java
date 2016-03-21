package com.learning.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.dropwizard.jackson.Jackson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kaustubh on 3/20/16.
 */

public class JsonUtilities {
    private final ObjectMapper mapper = Jackson.newObjectMapper();

    public JsonUtilities() {
        this.mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        this.mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    public JsonNode asJson(Object object) throws IOException {
        return this.mapper.valueToTree(object);
    }

    public JsonNode asJson(String string) throws IOException {
        return this.mapper.readTree(string);
    }

    public String asString(Object object) throws IOException {
        return this.mapper.writeValueAsString(object);
    }

    public <T> T pojoFromFile(String filename, Class<T> klass) throws IOException {
        JsonNode node = this.mapper.readTree(filename);
        return this.mapper.treeToValue(node, klass);
    }

    public <T> T pojoFromJson(JsonNode json, TypeReference<T> reference) throws IOException {
        return this.mapper.readValue(this.mapper.writeValueAsString(json), reference);
    }

    public <T> T pojoFromJson(String json, Class<T> klass) throws IOException {
        return this.mapper.readValue(json, klass);
    }

    public JsonNode jsonFromFile(String filename, Class<?> klass) throws IOException {
        return this.asJson(this.pojoFromFile(filename, klass));
    }

    public String toJson(Object object) throws IOException {
        return this.mapper.writeValueAsString(object);
    }

    private Object[] quoteStrings(Object... members) {
        if(null == members) {
            return new Object[0];
        } else {
            Object[] args = new Object[members.length];

            for(int i = 0; i < members.length; ++i) {
                Object member = members[i];
                if(member instanceof String) {
                    member = String.format("\"%s\"", new Object[]{member});
                }

                args[i] = member;
            }

            return args;
        }
    }

    public Map<String, Object> toObjectMap(String json) throws IOException {
        TypeReference typeRef = new TypeReference() {
        };
        HashMap map = (HashMap)this.mapper.readValue(json.getBytes("UTF-8"), typeRef);
        return map;
    }

    public String buildNaiveJsonString(String template, Object... members) {
        return String.format(template, this.quoteStrings(members));
    }
}
