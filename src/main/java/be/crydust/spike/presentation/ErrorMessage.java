package be.crydust.spike.presentation;

public class ErrorMessage {
    private final String fieldId;
    private final String message;

    public ErrorMessage(String fieldId, String message) {
        this.fieldId = fieldId;
        this.message = message;
    }

    public String getFieldId() {
        return fieldId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
                "fieldId='" + fieldId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
