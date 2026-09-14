/*
 * Copyright 2026 GapplX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.gapplex.gxl.core.utils;

import java.lang.reflect.Array;
import java.util.*;

public class JSONUtil {
    private static final char[] HEX_TABLE = "0123456789abcdef".toCharArray();

    private JSONUtil(){}

    public static String objectToJson(Object value) {
        Set<Object> stack = Collections.newSetFromMap(new IdentityHashMap<>());
        return objectToJson(value, stack);
    }

    private static String objectToJson(Object value, Set<Object> stack) {
        if (value == null) {
            return "null";
        }

        Class<?> type = value.getClass();

        if (type == String.class || type == Character.class) {
            return "\"" + safeString(String.valueOf(value)) + "\"";
        }
        if (type == Boolean.class) {
            return String.valueOf(value);
        }
        if (Number.class.isAssignableFrom(type)) {
            if (type == Float.class && !Float.isFinite((Float) value)) {
                return "null";
            }
            if (type == Double.class && !Double.isFinite((Double) value)) {
                return "null";
            }
            return String.valueOf(value);
        }

        if (stack.contains(value)) {
            return "\"<circular>\"";
        }
        stack.add(value);

        String result;
        try {
            if (value instanceof Map) {
                result = mapToJson((Map<?, ?>) value, stack);
            } else if (value instanceof Collection) {
                result = collectionToJson((Collection<?>) value, stack);
            } else if (type.isArray()) {
                int length = Array.getLength(value);
                List<Object> array = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    array.add(Array.get(value, i));
                }
                result = collectionToJson(array, stack);
            } else {
                result = "\"" + safeString(String.valueOf(value)) + "\"";
            }
        } finally {
            stack.remove(value);
        }
        return result;
    }

    private static String mapToJson(Map<?, ?> map, Set<Object> stack) {
        StringBuilder result = new StringBuilder(map.size() * 32);
        result.append('{');
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            result.append("\"");
            result.append(safeString(String.valueOf(entry.getKey())));
            result.append("\":");
            result.append(objectToJson(entry.getValue(), stack)).append(',');
        }
        if (result.length() > 1) {
            result.deleteCharAt(result.length() - 1);
        }
        result.append('}');
        return result.toString();
    }

    private static String collectionToJson(Collection<?> collection, Set<Object> stack) {
        StringBuilder result = new StringBuilder(collection.size() * 32);
        result.append('[');
        for (Object o : collection) {
            result.append(objectToJson(o, stack)).append(",");
        }
        if (result.length() > 1) {
            result.deleteCharAt(result.length() - 1);
        }
        result.append(']');
        return result.toString();
    }

    public static String safeString(String s){
        StringBuilder result = new StringBuilder(s.length() + 64);

        for (int i = 0; i < s.length(); i++){
            char currentChar = s.charAt(i);
            if (currentChar == '"'){
                result.append("\\\"");
            } else if (currentChar == '\\'){
                result.append("\\\\");
            } else if (currentChar == '\n'){
                result.append("\\n");
            } else if (currentChar == '\t'){
                result.append("\\t");
            } else if (currentChar == '\r'){
                result.append("\\r");
            } else if (currentChar < 32) {
                result.append("\\u00")
                        .append(HEX_TABLE[(currentChar >> 4) & 0xF])
                        .append(HEX_TABLE[currentChar & 0xF]);
            } else {
                result.append(currentChar);
            }
        }

        return result.toString();
    }
}
