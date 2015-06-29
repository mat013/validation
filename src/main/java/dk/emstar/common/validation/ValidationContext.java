package dk.emstar.common.validation;

import java.util.Collection;
import java.util.function.Function;

public class ValidationContext<T> extends AbstractValidationContext<ValidationContext<T>, T> {

    public ValidationContext(String context, T currentItemToBeChecked) {
        super(context, currentItemToBeChecked);
    }

    public ValidationContext(String context, String contextPath, String location, T currentItemToBeChecked) {
        super(context, contextPath, location, currentItemToBeChecked);
    }

    public <V> ValidationContext<T> evaluate(Function<T, V> getter, ValidateResultEvaluator<V> validator) {

        if (!isCurrentToBeCheckedItemNull()) {
            ValidationResult result = validator.validate(getter.apply(getCurrentItemToBeChecked())).result();
            result().register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return this;
    }

    public <V> ValidationContext<T> evaluate(String context, Function<T, V> getter, ValidateResultEvaluator<ValidationContext<V>> validator) {
        return validate(context, getter, o -> validator.validate(o).result());
    }

    public <V> ValidationContext<T> evaluateCollection(String context, Function<T, Collection<V>> getter,
            ValidateResultEvaluator<CollectionValidationContext<V>> validator) {
        return validateCollection(context, getter, o -> validator.validate(o).result());
    }

    public <V> ValidationContext<T> validateCollection(String context, Function<T, Collection<V>> getter, Validator<CollectionValidationContext<V>> validator) {
        if (!isCurrentToBeCheckedItemNull()) {
            CollectionValidationContext<V> validationContext = new CollectionValidationContext<V>(context, result().getCompletePath(),
                    buildCompleteLocation(result().getLocation(), context), getter.apply(getCurrentItemToBeChecked()));
            ValidationResult result = validator.validate(validationContext);
            result().register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return this;
    }

    public ValidationContext<T> validateString(String context, Function<T, String> getter, ValidateResultEvaluator<StringValidationContext> validator) {

        if (!isCurrentToBeCheckedItemNull()) {
            StringValidationContext validationContext = new StringValidationContext(context, result().getCompletePath(),
                    buildCompleteLocation(result().getLocation(), context), getter.apply(getCurrentItemToBeChecked()));
            ValidationResult result = validator.validate(validationContext).result();
            result().register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return this;
    }

    public ValidationContext<T> validateString(String context, Function<T, String> getter, int length, Required required) {

        if (!isCurrentToBeCheckedItemNull()) {
            StringValidationContext validationContext = new StringValidationContext(context, result().getCompletePath(),
                    buildCompleteLocation(result().getLocation(), context), getter.apply(getCurrentItemToBeChecked()));

            switch (required) {
            case Mandatory:
                validationContext.failWhenMissing();
                break;
            case Optional:
                validationContext.asOptional();
                break;
            }

            ValidationResult result = validationContext.failWhenLongerThan(length).result();
            result().register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return this;
    }
    
    public <V> ValidationContext<T> validate(String context, Function<T, V> getter, Validator<ValidationContext<V>> validator) {

        if (!isCurrentToBeCheckedItemNull()) {
            ValidationContext<V> validationContext = new ValidationContext<V>(context, result().getCompletePath(), buildCompleteLocation(
                    result().getLocation(), context), getter.apply(getCurrentItemToBeChecked()));
            ValidationResult result = validator.validate(validationContext);
            result().register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return this;
    }
    
}
