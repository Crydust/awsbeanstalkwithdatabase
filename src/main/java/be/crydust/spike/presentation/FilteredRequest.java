package be.crydust.spike.presentation;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.SuppressPropertiesBeanIntrospector;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;

public class FilteredRequest {

    private static final Pattern UNESCAPED_COMMA = Pattern.compile("(?<!\\\\),");

    private final String button;
    private final Map<String, String[]> parameterMap;

    public FilteredRequest(String button, Map<String, String[]> parameterMap) {
        this.button = button;
        this.parameterMap = parameterMap;
    }

    private static Map<String, String[]> combineButtonArgumentsAndParameterMap(String button, Map<String, String[]> parameterMap) {
        final Map<String, String[]> combinedParameterMap;
        if (button == null || button.isEmpty() || !button.contains(":")) {
            combinedParameterMap = parameterMap;
        } else {
            final int colon = button.indexOf(':');
            final String prefix = button.substring(0, colon + 1);
            final String suffix = button.substring(colon + 1);
            final Map<String, String[]> buttonParameterMap = parseButtonArguments(suffix);
            final Map<String, String[]> filteredParameterMap = filterParameterMap(parameterMap, prefix);
            combinedParameterMap = combineMaps(buttonParameterMap, filteredParameterMap);
        }
        return combinedParameterMap;
    }

    private static Map<String, String[]> parseButtonArguments(String suffix) {
        if (suffix.isEmpty()) {
            return emptyMap();
        }
        final Map<String, String[]> buttonParameters = new LinkedHashMap<>();
        final String[] pairs = UNESCAPED_COMMA.split(suffix, 0);
        for (String pair : pairs) {
            final int eq = pair.indexOf("=");
            final String key = pair.substring(0, eq);
            final String[] value = {unescape(pair.substring(eq + 1))};
            buttonParameters.put(key, value);
        }
        return buttonParameters;
    }

    static String escape(String escapedValue) {
        return escapedValue
                .replace("\\", "\\\\")
                .replace(",", "\\,");
    }

    private static String unescape(String escapedValue) {
        return escapedValue
                .replace("\\,", ",")
                .replace("\\\\", "\\");
    }

    private static Map<String, String[]> filterParameterMap(Map<String, String[]> parameterMap, String prefix) {
        return parameterMap
                .entrySet()
                .stream()
                .filter(it -> it.getKey().startsWith(prefix))
                .collect(toMap(
                        entry -> entry.getKey().substring(prefix.length()),
                        entry -> entry.getValue()
                ));
    }

    private static Map<String, String[]> combineMaps(Map<String, String[]> mapA, Map<String, String[]> mapB) {
        if (mapA.isEmpty()) {
            return mapB;
        }
        if (mapB.isEmpty()) {
            return mapA;
        }
        final Map<String, String[]> combinedParameterMap = new LinkedHashMap<>(mapA);
        for (Map.Entry<String, String[]> entry : mapB.entrySet()) {
            final String key = entry.getKey();
            final String[] newValue = entry.getValue();
            final String[] oldValue = mapA.get(key);
            if (combinedParameterMap.containsKey(key) && oldValue != null) {
                final int lenA = oldValue.length;
                final int lenB = newValue.length;
                final String[] combinedValue = new String[lenA + lenB];
                System.arraycopy(oldValue, 0, combinedValue, 0, lenA);
                System.arraycopy(newValue, 0, combinedValue, lenA, lenB);
                combinedParameterMap.put(key, combinedValue);
            } else {
                combinedParameterMap.put(key, newValue);
            }
        }
        return combinedParameterMap;
    }

    private static Map<String, String[]> addIndexesToKeys(Map<String, String[]> parameterMap) {
        final List<String> keysToRemove = new ArrayList<>();
        final Map<String, String[]> entriesToAdd = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            final String key = entry.getKey();
            final String[] values = entry.getValue();
            if (values.length > 1) {
                keysToRemove.add(key);
                for (int i = 0; i < values.length; i++) {
                    entriesToAdd.put(key + "[" + i + "]", new String[]{values[i]});
                }
            }
        }
        if (keysToRemove.isEmpty() && entriesToAdd.isEmpty()) {
            return parameterMap;
        }
        final Map<String, String[]> result = new LinkedHashMap<>(parameterMap);
        result.keySet().removeAll(keysToRemove);
        result.putAll(entriesToAdd);
        return result;
    }

    private String getPrefix() {
        final int colon = button.indexOf(':');
        final String prefix = button.substring(0, colon + 1);
        return prefix;
    }

    public <T> InputAndErrorMessages<T> read(T input) {
        try {
            final Map<String, String[]> combinedParameterMap = addIndexesToKeys(
                    combineButtonArgumentsAndParameterMap(button, parameterMap)
            );

            final ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();
            BeanUtilsBean beanUtilsBean = new BeanUtilsBean(convertUtilsBean, new PropertyUtilsBean());
            beanUtilsBean.getPropertyUtils().addBeanIntrospector(SuppressPropertiesBeanIntrospector.SUPPRESS_CLASS);
            beanUtilsBean.populate(input, combinedParameterMap);

            final List<ErrorMessage> messages;
            if (input instanceof Validateable) {
                messages = ((Validateable) input).validate(getPrefix());
            } else {
                messages = emptyList();
            }
            return new InputAndErrorMessages<>(input, messages);
        } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

}
