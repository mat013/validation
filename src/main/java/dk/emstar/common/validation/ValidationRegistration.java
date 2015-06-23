package dk.emstar.common.validation;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ValidationRegistration {

    private final String validationCode;
    private final String validationMessage;
    private final Object[] input;
    private final ValidationLevel validationLevel;
    private final String location;
    private final String context;
    private final String contextPath;

    public ValidationRegistration(String validationCode, String validationMessage, ValidationLevel validationLevel, Object... input) {
        this(validationCode, validationMessage, null, null, null, validationLevel, input);
    }

    public ValidationRegistration(String validationCode, String validationMessage, String context, String location, String contextPath,
            ValidationLevel validationLevel, Object... input) {
        this.validationCode = validationCode;
        this.validationMessage = validationMessage;
        this.context = context;
        this.location = location;
        this.contextPath = contextPath;
        this.input = input;
        this.validationLevel = validationLevel;
    }

    public String getValidationCode() {
        return validationCode;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public ValidationLevel getValidationLevel() {
        return validationLevel;
    }

    public Object[] getInput() {
        return input;
    }

    public String getDetails() {
        String inputAsString = "";
        if (input != null && input.length > 0) {
            inputAsString = MessageFormat.format(": input: {0}",
                    Arrays.asList(input).stream().map(o -> o == null ? "" : o.toString()).collect(Collectors.joining(", ")));
        }

        return MessageFormat.format("[{0}#{1} - {2}{3}]", getContextPath(), getValidationCode(), getValidationMessage(), inputAsString);
    }

    @Override
    public String toString() {
        return MessageFormat.format("[{0} - {1}]", getValidationCode(), getValidationMessage());
    }

    public String getLocation() {
        return location;
    }

    public String getContext() {
        return context;
    }

    public String getContextPath() {
        return contextPath;
    }
}
