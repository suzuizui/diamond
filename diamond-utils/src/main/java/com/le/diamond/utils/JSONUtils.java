package com.le.diamond.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;


public class JSONUtils {

    static ObjectMapper mapper = new ObjectMapper();

    public static String serializeObject(Object o) throws Exception {
        return mapper.writeValueAsString(o);
    }

    public static Object deserializeObject(String s, Class<?> clazz) throws Exception {
        return mapper.readValue(s, clazz);
    }

    public static Object deserializeObject(String s, TypeReference<?> typeReference)
            throws Exception {
        return mapper.readValue(s, typeReference);
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static Object deserializeCollection(String s, JavaType type) throws Exception {
        return mapper.readValue(s, type);
    }

}
