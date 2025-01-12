import java.lang.reflect.Field;
import java.util.*;

public class JsonReader {

    public Map<String, Object> root = new HashMap<>();

    public JsonReader(String json) {
        Objects.requireNonNull(json, "json is null");
        if(json.isBlank()) {
            throw new IllegalArgumentException("json is blank");
        }
        boolean isObject = isObjectTree(json);
        if(!isObject) {
            throw new IllegalArgumentException("Invalid json=%s".formatted(json));
        }

        readObject(json, root);
    }


    @SuppressWarnings("unchecked")
    public <T> T getFirstValueInTree(String key, Class<T> clazz) {
        Object value = getFirstValueInObject(key, root);
        if(value instanceof String) {
            if(clazz.isInstance(value)) {
                return (T) value;
            } else {
                throw new IllegalArgumentException("Invalid clazz=%s Value is instance of %s"
                        .formatted(clazz.getName(), value.getClass().getName()));
            }
        } else if(value instanceof Map) {
            return mapToClass((Map<String, Object>) value, clazz);
        } else if(value instanceof List) {
            if(clazz.isAssignableFrom(List.class)) {
                return (T) value;
            }
        }
        throw new RuntimeException("Error occurred in getting value");
    }

    private <T> T mapToClass(Map<String, Object> jsonObject, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = jsonObject.get(fieldName);

                if (fieldValue != null) {
                    if (field.getType().isAssignableFrom(Map.class)) {
                        Object nestedObject = mapToClass((Map<String, Object>) fieldValue, field.getType());
                        field.set(instance, nestedObject);
                    } else {
                        field.set(instance, fieldValue);
                    }
                }
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map JSON to class: " + clazz.getName(), e);
        }
    }

    private Object getFirstValueInObject(String key, Map<String, Object> jsonObject) {
        for(Map.Entry<String, Object> entry: jsonObject.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            if(k.equals(key)) {
                return v;
            }
        }
        return null;
    }


    private void readObject(String json, Map<String, Object> root) {
        json = json.substring(1, json.length() - 1);

        int i = 0;
        while (i < json.length()) {
            int keyIndexStart = json.indexOf('"', i);
            int keyIndexEnd = json.indexOf('"', keyIndexStart + 1);
            if(keyIndexStart == -1 || keyIndexEnd == -1) break;
            String key = json.substring(keyIndexStart + 1, keyIndexEnd);
            i = keyIndexEnd + 1;

            int colonIndex = json.indexOf(':', i);
            if(colonIndex == -1) break;
            i = colonIndex + 1;

            i = skipWhitespaces(json, i);
            char firstChar = json.charAt(i);
            if (firstChar == '{') {
                int braceEndIndex = findMatchingBrace(json, i, '{', '}');
                String nestedObject = json.substring(i, braceEndIndex + 1);
                Map<String, Object> nestedRoot = new HashMap<>();
                readObject(nestedObject, nestedRoot);
                root.put(key, nestedRoot);
                i = braceEndIndex + 1;
            } else if (firstChar == '[') {
                int arrayEndIndex = findMatchingBrace(json, i, '[', ']');
                String arrayContent = json.substring(i + 1, arrayEndIndex);
                List<Object> arrayValues = readArray(arrayContent);
                root.put(key, arrayValues);
                i = arrayEndIndex + 1;
            } else if (firstChar == '"') {
                int valueStartIndex = i + 1;
                int valueEndIndex = json.indexOf('"', valueStartIndex);
                String value = json.substring(valueStartIndex, valueEndIndex);
                root.put(key, value);
                i = valueEndIndex + 1;
            } else {
                throw new RuntimeException("Json reader supports only objects and strings");
            }

            i = skipComma(json, i);
        }
    }

    private List<Object> readArray(String arrayContent) {
        List<Object> arrayValues = new ArrayList<>();
        int i = 0;
        while (i < arrayContent.length()) {
            i = skipWhitespaces(arrayContent, i);
            char firstChar = arrayContent.charAt(i);
            if (firstChar == '{') {
                int braceEndIndex = findMatchingBrace(arrayContent, i, '{', '}');
                String nestedObject = arrayContent.substring(i, braceEndIndex + 1);
                Map<String, Object> nestedRoot = new HashMap<>();
                readObject(nestedObject, nestedRoot);
                arrayValues.add(nestedRoot);
                i = braceEndIndex + 1;
            } else if (firstChar == '"') {
                int valueStartIndex = i + 1;
                int valueEndIndex = arrayContent.indexOf('"', valueStartIndex);
                String value = arrayContent.substring(valueStartIndex, valueEndIndex);
                arrayValues.add(value);
                i = valueEndIndex + 1;
            } else {
                throw new RuntimeException("Json reader supports only objects and strings in arrays");
            }
            i = skipComma(arrayContent, i);
        }
        return arrayValues;
    }

    private int findMatchingBrace(String json, int start, char braceTypeStart, char braceTypeEnd) {
        int depth = 0;
        for(int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if(c == braceTypeStart) depth++;
            else if(c == braceTypeEnd) depth--;
            if(depth == 0) return i;
        }
        throw new IllegalArgumentException("Unmatched braces in JSON. Invalid json=%s".formatted(json));
    }

    private int skipWhitespaces(String json, int start) {
        while(start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }
        return start;
    }

    private int skipComma(String json, int start) {
        while (start < json.length()) {
            char c = json.charAt(start);
            if(c == ',' || Character.isWhitespace(json.charAt(start))) {
                start++;
            } else {
                break;
            }
        }
        return start;
    }

    private boolean isObjectTree(String json) {
        return json.startsWith("{") && json.endsWith("}");
    }

}
