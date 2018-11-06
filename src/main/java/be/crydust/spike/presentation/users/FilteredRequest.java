package be.crydust.spike.presentation.users;

import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

class FilteredRequest {

    private final String prefix;
    private final Map<String, String[]> parameterMap;

    FilteredRequest(String button, Map<String, String[]> parameterMap) {
        if (button == null || button.isEmpty() || !button.contains(":")) {
            this.prefix = "";
            this.parameterMap = parameterMap;
        } else {
            this.prefix = button.substring(button.indexOf(':') + 1);
            this.parameterMap = parameterMap
                    .entrySet()
                    .stream()
                    .filter(it -> it.getKey().startsWith(prefix + ':'))
                    .collect(toMap(
                            entry -> entry.getKey().substring(prefix.length() + 1),
                            entry -> entry.getValue()
                    ));
        }
    }

//        String getParameter(String name) {
//            final String[] values = parameterMap.get(name);
//            if (values == null || values.length == 0) {
//                return null;
//            }
//            return values[0];
//        }
//
//        String[] getParameterValues(String name) {
//            return parameterMap.get(name);
//        }
//
//        Map<String, String[]> getParameterMap() {
//            return parameterMap;
//        }
//
//        Enumeration<String> getParameterNames() {
//            return Collections.enumeration(parameterMap.keySet());
//        }

    <T> InputAndViolations<T> read(T input) {
        try {
            BeanUtils.populate(input, parameterMap);
            final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            final Validator validator = factory.getValidator();
            Set<ConstraintViolation<T>> violations = validator.validate(input);
            return new InputAndViolations<>(input, violations);
        } catch (IllegalAccessException | InvocationTargetException | ValidationException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return new InputAndViolations<>(input, Collections.emptySet());
    }
}
