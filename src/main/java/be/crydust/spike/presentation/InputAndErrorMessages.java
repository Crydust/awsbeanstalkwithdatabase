package be.crydust.spike.presentation;

import java.util.List;

public class InputAndErrorMessages<T> {
    private final T input;
    private final List<ErrorMessage> errorMessages;

    public InputAndErrorMessages(T input, List<ErrorMessage> errorMessages) {
        this.input = input;
        this.errorMessages = errorMessages;
    }

    public T getInput() {
        return input;
    }

    public List<ErrorMessage> getErrorMessages() {
        return errorMessages;
    }

    @Override
    public String toString() {
        return "InputAndErrorMessages{" +
                "input=" + input +
                ", errorMessages=" + errorMessages +
                '}';
    }
}
