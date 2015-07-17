package dk.emstar.common.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

public class ValidationResult implements Iterable<ValidationRegistration>, AutoCloseable {

    private static Object[] NULL_ARRAY = new Object[] {};

    private final List<Object> validationResults;

    public static final String UNKNOWN_ERROR = "9999";

    private final String context;

    private final String contextPath;

    private final String location;

    public ValidationResult() {
        this("N/A", "N/A", "N/A");
    }

    public ValidationResult(String context, String contextPath, String location) {
        this.context = context;
        this.contextPath = contextPath;
        this.location = location;
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

    public String getCompletePath() {
        return Joiner.on(".").skipNulls().join(Strings.isNullOrEmpty(contextPath) ? null : contextPath, context);
    }

    public String getLocation() {
        return location;
    }

    public String getAllDetailsAsString() {
        return Joiner.on(", ").join(getAllDetails());
    }

    public ValidationRegistration findValidationCode(String validationCode) {
        return stream().filter(o -> validationCode.equals(o.getValidationCode())).findFirst().orElse(null);
    }

    public ValidationResult registerNullValidation(String validationCode, String validationMessage) {
        register(new ValidationRegistration(validationCode, validationMessage, context, getLocation(), getCompletePath(), ValidationLevel.Failure, NULL_ARRAY));
        return this;
    }

    public ValidationResult registerValidationFailure(String validationCode, String validationMessage, Object... input) {
        register(new ValidationRegistration(validationCode, validationMessage, context, getLocation(), getCompletePath(), ValidationLevel.Failure, input));
        return this;
    }

    public ValidationResult registerValidationWarning(String validationCode, String validationMessage, Object... input) {
        register(new ValidationRegistration(validationCode, validationMessage, context, getLocation(), getCompletePath(), ValidationLevel.Warning, input));
        return this;
    }

    @Override
    public void close() {
        throwIncludeAllMessagesWhenAnyFailures(UNKNOWN_ERROR, "generic");
    }

    @Override
    public String toString() {
        return String.format("{%s:%s}", context, validationResults.stream().map(Object::toString).collect(Collectors.joining(", ")));
    }

    public String getContext() {
        return context;
    }

    // TODO provide better interface
    public ValidationResult throwWhenHasFailureCode(String validationCode, String errorMessage) {
        stream().filter(o -> validationCode.equals(o.getValidationCode())).findFirst().ifPresent(o -> {
            if (hasFailure()) {
                throw new ValidationException(validationCode, errorMessage, this, o);
            }
        });
        return this;
    }

    // TODO provide better interface
    public ValidationResult throwWhenHasFailureCode(String validationCode) {
        stream().filter(o -> validationCode.equals(o.getValidationCode())).findFirst().ifPresent(o -> {
            if (hasFailure()) {
                throw new ValidationException(validationCode, o.getValidationMessage(), this, o);
            }
        });
        return this;
    }

    // TODO provide better interface
    public ValidationResult throwMessageWhenHasFailureCode(String validationCode) {
        return throwWhenHasFailureCode(validationCode);
    }

    // TODO provide better interface
    public ValidationResult throwForFirstFoundWhenAnyFailures() {
        if (hasFailure()) {
            String allDetails = getAllDetailsAsString();
            ValidationRegistration firstValidationFailure = stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).findFirst().get();
            throw new ValidationException(firstValidationFailure.getValidationCode(), allDetails, this, firstValidationFailure);
        }

        return this;
    }

    // TODO provide better interface
    public ValidationResult throwIncludeAllMessagesWhenAnyFailures(String validationCode, String mainValidationMessage) {
        if (hasFailure()) {
            StringBuilder errorMessagesStringBuilder = new StringBuilder(Strings.isNullOrEmpty(mainValidationMessage) ? "" : MessageFormat.format("{0}: ",
                    mainValidationMessage));

            stream().forEach(o -> errorMessagesStringBuilder.append(o.getDetails()).append(", "));
            String errorMessages = errorMessagesStringBuilder.toString();
            throw new ValidationException(validationCode, errorMessages, this, stream().findFirst().get());
        }

        return this;
    }

    // TODO provide better interface
    public ValidationResult throwIncludeAllMessagesWhenAnyFailures(String validationCode) {
        return throwIncludeAllMessagesWhenAnyFailures(validationCode, "");
    }

    // TODO provide better interface
    public ValidationResult throwIncludeAllMessagesWhenAnyFailures() {
        return throwIncludeAllMessagesWhenAnyFailures(UNKNOWN_ERROR, "");
    }

}
