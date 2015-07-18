package dk.emstar.common.validation;

import java.text.MessageFormat;

import com.google.common.base.Strings;

public class ThrowableValidationResult implements AutoCloseable{
    
    private final ValidationResult validationResult;

    public ThrowableValidationResult(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }
    
    public static final String UNKNOWN_ERROR = "9999";

    // TODO provide better interface
    public ThrowableValidationResult throwWhenHasFailureCode(String validationCode, String errorMessage) {
        validationResult.stream().filter(o -> validationCode.equals(o.getValidationCode())).findFirst().ifPresent(o -> {
            if (validationResult.hasFailure()) {
                throw new ValidationException(validationCode, errorMessage, validationResult, o);
            }
        });
        return this;
    }

    // TODO provide better interface
    public ThrowableValidationResult throwWhenHasFailureCode(String validationCode) {
        validationResult.stream().filter(o -> validationCode.equals(o.getValidationCode())).findFirst().ifPresent(o -> {
            if (validationResult.hasFailure()) {
                throw new ValidationException(validationCode, o.getValidationMessage(), validationResult, o);
            }
        });
        return this;
    }

    // TODO provide better interface
    public ThrowableValidationResult throwMessageWhenHasFailureCode(String validationCode) {
        return throwWhenHasFailureCode(validationCode);
    }

    // TODO provide better interface
    public ThrowableValidationResult throwForFirstFoundWhenAnyFailures() {
        if (validationResult.hasFailure()) {
            String allDetails = validationResult.getAllDetailsAsString();
            ValidationRegistration firstValidationFailure = validationResult.stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).findFirst().get();
            throw new ValidationException(firstValidationFailure.getValidationCode(), allDetails, validationResult, firstValidationFailure);
        }

        return this;
    }

    // TODO provide better interface
    public ThrowableValidationResult throwIncludeAllMessagesWhenAnyFailures(String validationCode, String mainValidationMessage) {
        if (validationResult.hasFailure()) {
            StringBuilder errorMessagesStringBuilder = new StringBuilder(Strings.isNullOrEmpty(mainValidationMessage) ? "" : MessageFormat.format("{0}: ",
                    mainValidationMessage));

            validationResult.stream().forEach(o -> errorMessagesStringBuilder.append(o.getDetails()).append(", "));
            String errorMessages = errorMessagesStringBuilder.toString();
            throw new ValidationException(validationCode, errorMessages, validationResult, validationResult.stream().findFirst().get());
        }

        return this;
    }

    // TODO provide better interface
    public ThrowableValidationResult throwIncludeAllMessagesWhenAnyFailures(String validationCode) {
        return throwIncludeAllMessagesWhenAnyFailures(validationCode, "");
    }

    // TODO provide better interface
    public ThrowableValidationResult throwIncludeAllMessagesWhenAnyFailures() {
        return throwIncludeAllMessagesWhenAnyFailures(UNKNOWN_ERROR, "");
    }

    @Override
    public void close() {
        throwIncludeAllMessagesWhenAnyFailures(UNKNOWN_ERROR, "generic");
    }

}
