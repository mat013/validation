package dk.emstar.common.validation;

import java.util.function.Predicate;

import com.google.common.base.Joiner;

public class AbstractValidationContext<T extends AbstractValidationContext<T, U>, U> implements ValidationResultProvider {
    private final U currentItemToBeChecked;
    private final ValidationResult validationResult;
    private boolean isOptional = false;

    public AbstractValidationContext(String context, U currentItemToBeChecked) {
        this(context, "", context, currentItemToBeChecked);
    }

    public AbstractValidationContext(String context, String contextPath, String location, U currentItemToBeChecked) {
        this.currentItemToBeChecked = currentItemToBeChecked;
        this.validationResult = new ValidationResult(context, contextPath, location);
    }

    @SuppressWarnings("unchecked")
    public T failWhen(Predicate<U> predicate, String failureCode, String message, Object... objects) {
        if (!isCurrentToBeCheckedItemNull() && predicate.test(currentItemToBeChecked)) {
            validationResult.registerValidationFailure(failureCode, message, objects);
        }
        registerWhenItemIsNullButNotOptional();
        return (T) this;
    }

    // validateMap // has hasNot
    // validateInt // between greaterThan greatherOrEqual lessThan or lessOrEqualThan
    // validateLong  // between greaterThan greatherOrEqual lessThan or lessOrEqualThan
    // validateLocalDate      // between later before
    // validateLocalDateTime  // between later before
    // validateLocalTime      // between later before
    // validateZonedDate      // between later before
    // validateZonedDateTime  // between later before
    // validateZonedTime      // between later before
    
    
    @SuppressWarnings("unchecked")
    public T asOptional() {
        isOptional = true;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T failWhenMissing() {
        if (isCurrentToBeCheckedItemNull()) {
            validationResult.registerNullValidation(MISSING, "is null");
        }

        return (T) this;
    }

    public ValidationResult result() {
        return validationResult;
    }

    public U getCurrentItemToBeChecked() {
        return currentItemToBeChecked;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public String getContext() {
        return validationResult.getContext();
    }

    public String getCompletePath() {
        return validationResult.getCompletePath();
    }

    public boolean isCurrentToBeCheckedItemNull() {
        return currentItemToBeChecked == null;
    }

    protected void registerWhenItemIsNullButNotOptional() {
        if (isCurrentToBeCheckedItemNull() && !isOptional()) {
            validationResult.registerValidationWarning(ValidationResultProvider.NOT_MARKED_AS_OPTIONAL, "not marked as optional but is null");
        }
    }

    protected void registerAsFailure(String failureCode, String message, U currentItemToBeChecked) {
        validationResult.registerValidationFailure(failureCode, message, currentItemToBeChecked);
    }

    protected void register(ValidationResult validationResult) {
        this.validationResult.register(validationResult);
    }

    protected String buildCompleteLocation(String location, String context) {
        return Joiner.on(".").join(location, context);
    }
}
