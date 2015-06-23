package dk.emstar.common.validation;

import java.util.Collection;
import java.util.function.Function;
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
    public <V> T evaluate(Function<U, V> getter, ValidateResultEvaluator<V> validator) {

        if (!isCurrentToBeCheckedItemNull()) {
            ValidationResult result = validator.validate(getter.apply(currentItemToBeChecked)).result();
            validationResult.register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return (T) this;
    }

    public <V> T evaluate(String context, Function<U, V> getter, ValidateResultEvaluator<ValidationContext<V>> validator) {
        return validate(context, getter, o -> validator.validate(o).result());
    }

    public <V> T evaluateCollection(String context, Function<U, Collection<V>> getter,
            ValidateResultEvaluator<CollectionValidationContext<V>> validator) {
        return validateCollection(context, getter, o -> validator.validate(o).result());
    }

    @SuppressWarnings("unchecked")
    public <V> T validate(String context, Function<U, V> getter, Validator<ValidationContext<V>> validator) {

        if (!isCurrentToBeCheckedItemNull()) {
            ValidationContext<V> validationContext = new ValidationContext<V>(context, validationResult.getCompletePath(), buildCompleteLocation(
                    validationResult.getLocation(), context), getter.apply(currentItemToBeChecked));
            ValidationResult result = validator.validate(validationContext);
            validationResult.register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <V> T validateCollection(String context, Function<U, Collection<V>> getter, Validator<CollectionValidationContext<V>> validator) {
        if (!isCurrentToBeCheckedItemNull()) {
            CollectionValidationContext<V> validationContext = new CollectionValidationContext<V>(context, validationResult.getCompletePath(),
                    buildCompleteLocation(validationResult.getLocation(), context), getter.apply(currentItemToBeChecked));
            ValidationResult result = validator.validate(validationContext);
            validationResult.register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T validateString(String context, Function<U, String> getter, ValidateResultEvaluator<StringValidationContext> validator) {

        if (!isCurrentToBeCheckedItemNull()) {
            StringValidationContext validationContext = new StringValidationContext(context, validationResult.getCompletePath(),
                    buildCompleteLocation(validationResult.getLocation(), context), getter.apply(currentItemToBeChecked));
            ValidationResult result = validator.validate(validationContext).result();
            validationResult.register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T validateString(String context, Function<U, String> getter, int length, Required required) {

        if (!isCurrentToBeCheckedItemNull()) {
            StringValidationContext validationContext = new StringValidationContext(context, validationResult.getCompletePath(),
                    buildCompleteLocation(validationResult.getLocation(), context), getter.apply(currentItemToBeChecked));

            switch (required) {
            case Mandatory:
                validationContext.failWhenMissing();
                break;
            case Optional:
                validationContext.asOptional();
                break;
            }

            ValidationResult result = validationContext.failWhenLongerThan(length).result();
            validationResult.register(result);
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

    @SuppressWarnings("unchecked")
    public T failWhen(Predicate<U> predicate, String failureCode, String message, Object... objects) {
        if (!isCurrentToBeCheckedItemNull() && predicate.test(currentItemToBeChecked)) {
            validationResult.registerValidationFailure(failureCode, message, objects);
        }
        registerWhenItemIsNullButNotOptional();
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

    private String buildCompleteLocation(String location, String context) {
        return Joiner.on(".").join(location, context);
    }
}
