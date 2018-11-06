package be.crydust.spike.presentation.users;

import javax.validation.ConstraintViolation;
import java.util.Set;

class InputAndViolations<T> {
    private final T input;
    private final Set<ConstraintViolation<T>> violations;

    InputAndViolations(T input, Set<ConstraintViolation<T>> violations) {
        this.input = input;
        this.violations = violations;
    }

    public T getInput() {
        return input;
    }

    public Set<ConstraintViolation<T>> getViolations() {
        return violations;
    }

    @Override
    public String toString() {
        return "InputAndViolations{" +
                "input=" + input +
                ", violations=" + violations +
                '}';
    }
}
