package dk.emstar.common.validation;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public class AbstractValidationContext<T extends AbstractValidationContext<T, U>, U> {
    public static final String NOT_MARKED_AS_OPTIONAL = "VALCON-1";

    private U currentItemToBeChecked;
    private ValidationResult validationResult;
    private boolean isOptional = false;

    public AbstractValidationContext(String context, U currentItemToBeChecked) {
        this.currentItemToBeChecked = currentItemToBeChecked;
        this.validationResult = new ValidationResult(context);
    }

    @SuppressWarnings("unchecked")
    public <V> T validate(Function<U, V> getter, Validator<V> validator) {

        if (!isCurrentToBeCheckedItemNull()) {
            ValidationResult result = validator.validate(getter.apply(currentItemToBeChecked));
            validationResult.register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <V> T validate(String context, Function<U, V> getter, Validator<ValidationContext<V>> validator) {

        if (!isCurrentToBeCheckedItemNull()) {
            ValidationContext<V> validationContext = new ValidationContext<V>(context, getter.apply(currentItemToBeChecked));
            ValidationResult result = validator.validate(validationContext);
            validationResult.register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <V> T validateCollection(String context, Function<U, Collection<V>> getter, Validator<CollectionValidationContext<V>> validator) {
        if (!isCurrentToBeCheckedItemNull()) {
            CollectionValidationContext<V> validationContext = new CollectionValidationContext<V>(context, getter.apply(currentItemToBeChecked));
            ValidationResult result = validator.validate(validationContext);
            validationResult.register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T validateString(String context, Function<U, String> getter, Validator<StringValidationContext> validator) {

        if (!isCurrentToBeCheckedItemNull()) {
            StringValidationContext validationContext = new StringValidationContext(context, getter.apply(currentItemToBeChecked));
            ValidationResult result = validator.validate(validationContext);
            validationResult.register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T conditionallyValidateString(boolean evaluate, String context, Function<U, String> getter, Validator<StringValidationContext> validator) {

        if (!isCurrentToBeCheckedItemNull()) {
            StringValidationContext validationContext = new StringValidationContext(context, getter.apply(currentItemToBeChecked));
            ValidationResult result = validator.validate(validationContext);
            validationResult.register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T asOptional() {
        isOptional = true;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T failWhenMissingAs(String failureCode) {
        if (isCurrentToBeCheckedItemNull()) {
            validationResult.registerNullValidation(failureCode, "is null");
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

    public String context() {
        return validationResult.getContext();
    }

    public boolean isCurrentToBeCheckedItemNull() {
        return currentItemToBeChecked == null;
    }

    protected void registerWhenItemIsNullButNotOptional() {
        if (isCurrentToBeCheckedItemNull() && !isOptional()) {
            validationResult.registerValidationWarning(NOT_MARKED_AS_OPTIONAL, "not marked as optional but is null");
        }
    }

    protected void registerAsFailure(String failureCode, String message, U currentItemToBeChecked) {
        validationResult.registerValidationFailure(failureCode, message, currentItemToBeChecked);
    }

    protected void register(ValidationResult validationResult) {
        this.validationResult.register(validationResult);
    }

}
