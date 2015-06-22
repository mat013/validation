package dk.emstar.common.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

public class ValidationResult implements Iterable<ValidationRegistration>, AutoCloseable {

    private static Object[] NULL_ARRAY = new Object[] {};

    private static final Logger logger = LoggerFactory.getLogger(ValidationResult.class);

    private final List<Object> validationResults;

    public static final String UNKNOWN_ERROR = "9999";

    private final String context;

    public ValidationResult() {
        this("N/A");
    }

    public ValidationResult(String context) {
        this.context = context;
        validationResults = new ArrayList<Object>();
    }

    public void register(ValidationRegistration validationRegistration) {
        validationResults.add(validationRegistration);
    }

    public void register(ValidationResult validationResult) {
        validationResults.add(validationResult);
    }

    public ValidationResult registerAll(ValidationResult validationResult) {
        validationResult.forEach(validationResults::add);
        return this;
    }

    @Override
    public Iterator<ValidationRegistration> iterator() {
        return new ValidationRegistrationIterator(validationResults.iterator());
    }

    public Stream<ValidationRegistration> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public boolean hasFailure() {
        return stream().anyMatch(o -> ValidationLevel.Failure.equals(o.getValidationLevel()));
    }

    public boolean hasWarning() {
        return stream().anyMatch(o -> ValidationLevel.Warning.equals(o.getValidationLevel()));
    }

    public boolean hasValidationCode(String validationCode) {
        return findValidationCode(validationCode) != null;
    }

    public List<String> getAllDetails() {
        return stream().map(o -> o.getDetails()).collect(Collectors.toList());
    }

    public String getAllDetailsAsString() {
        return Joiner.on(", ").join(getAllDetails());
    }

    public ValidationRegistration findValidationCode(String validationCode) {
        return stream().filter(o -> validationCode.equals(o.getValidationCode())).findFirst().orElse(null);
    }

    public ValidationResult registerNullValidation(String validationCode, String validationMessage) {
        register(new ValidationRegistration(validationCode, validationMessage, ValidationLevel.Failure, NULL_ARRAY));
        return this;
    }

    public ValidationResult registerValidationFailure(String validationCode, String validationMessage, Object... input) {
        register(new ValidationRegistration(validationCode, validationMessage, ValidationLevel.Failure, input));
        return this;
    }

    public ValidationResult registerValidationWarning(String validationCode, String validationMessage, Object... input) {
        register(new ValidationRegistration(validationCode, validationMessage, ValidationLevel.Warning, input));
        return this;
    }

    public ValidationResult throwWhenHasFailureCode(String validationCode, String errorMessage, Logger logger) {
        stream().filter(o -> validationCode.equals(o.getValidationCode())).findFirst().ifPresent(o -> {
            log(o, logger);
            if (hasFailure()) {
                throw new ValidationException(validationCode, errorMessage, this);
            }
        });
        return this;
    }

    public ValidationResult throwWhenHasFailureCode(String validationCode, Logger logger) {
        stream().filter(o -> validationCode.equals(o.getValidationCode())).findFirst().ifPresent(o -> {
            log(o, logger);
            if (hasFailure()) {
                throw new ValidationException(validationCode, o.getValidationMessage(), this);
            }
        });
        return this;
    }

    public ValidationResult throwMessageWhenHasFailureCode(String validationCode, Logger logger) {
        return throwWhenHasFailureCode(validationCode, logger);
    }

    public ValidationResult throwForFirstFoundWhenAnyFailures(Logger logger) {
        if (hasFailure()) {
            String allDetails = getAllDetailsAsString();
            logger.error(allDetails);

            ValidationRegistration firstValidationFailure = stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).findFirst().get();

            throw new ValidationException(firstValidationFailure.getValidationCode(), allDetails, this);
        }

        return this;
    }

    public ValidationResult throwIncludeAllMessagesWhenAnyFailures(String validationCode, String mainValidationMessage, Logger logger) {
        if (hasFailure()) {
            StringBuilder errorMessagesStringBuilder = new StringBuilder(Strings.isNullOrEmpty(mainValidationMessage) ? "" : MessageFormat.format("{0}: ",
                    mainValidationMessage));

            stream().forEach(o -> errorMessagesStringBuilder.append(o.getDetails()).append(", "));
            String errorMessages = errorMessagesStringBuilder.toString();
            logger.error(errorMessages);
            throw new ValidationException(validationCode, errorMessages, this);
        }

        return this;
    }

    public ValidationResult throwIncludeAllMessagesWhenAnyFailures(String ValidationCode, Logger logger) {
        return throwIncludeAllMessagesWhenAnyFailures(ValidationCode, "", logger);
    }

    public ValidationResult throwIncludeAllMessagesWhenAnyFailures(Logger logger) {
        return throwIncludeAllMessagesWhenAnyFailures(UNKNOWN_ERROR, "", logger);
    }

    @Override
    public void close() {
        throwIncludeAllMessagesWhenAnyFailures(UNKNOWN_ERROR, "generic", logger);
    }

    @Override
    public String toString() {
        return String.format("{%s:%s}", context, validationResults.stream().map(Object::toString).collect(Collectors.joining(", ")));
    }

    private void log(ValidationRegistration validationResult, Logger logger) {
        if (ValidationLevel.Failure.equals(validationResult.getValidationLevel())) {
            logger.error(validationResult.getDetails());
        } else {
            logger.warn(validationResult.getDetails());
        }
    }

    public String getContext() {
        return context;
    }
}
