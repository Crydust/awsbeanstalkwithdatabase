package be.crydust.spike.presentation;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public interface Validateable {
    default List<ErrorMessage> validate(String prefix) {
        return validate().stream()
                .map(it -> it.withPrefix(prefix))
                .collect(collectingAndThen(toList(), Collections::unmodifiableList));
    }

    List<ErrorMessage> validate();
}
