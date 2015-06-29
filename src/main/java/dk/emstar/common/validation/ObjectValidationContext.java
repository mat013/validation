package dk.emstar.common.validation;

import java.util.Collection;
import java.util.function.Function;

public class ObjectValidationContext<T> extends ValidationContext<ObjectValidationContext<T>, T> {

    public ObjectValidationContext(String context, T currentItemToBeChecked) {
        super(context, currentItemToBeChecked);
    }

    public ObjectValidationContext(String context, String contextPath, String location, T currentItemToBeChecked) {
        super(context, contextPath, location, currentItemToBeChecked);
    }

    public <V> ObjectValidationContext<T> evaluate(Function<T, V> getter, ValidateResultEvaluator<V> validator) {

        if (!isCurrentToBeCheckedItemNull()) {
            ValidationResult result = validator.validate(getter.apply(getCurrentItemToBeChecked())).result();
            result().register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return this;
    }

    public <V> ObjectValidationContext<T> evaluate(String context, Function<T, V> getter, ValidateResultEvaluator<ObjectValidationContext<V>> validator) {
        return validate(context, getter, o -> validator.validate(o).result());
    }

    public <V> ObjectValidationContext<T> evaluateCollection(String context, Function<T, Collection<V>> getter,
            ValidateResultEvaluator<CollectionValidationContext<V>> validator) {
        return validateCollection(context, getter, o -> validator.validate(o).result());
    }

    public <V> ObjectValidationContext<T> validateCollection(String context, Function<T, Collection<V>> getter, Validator<CollectionValidationContext<V>> validator) {
        if (!isCurrentToBeCheckedItemNull()) {
            CollectionValidationContext<V> validationContext = new CollectionValidationContext<V>(context, result().getCompletePath(),
                    buildCompleteLocation(result().getLocation(), context), getter.apply(getCurrentItemToBeChecked()));
            ValidationResult result = validator.validate(validationContext);
            result().register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return this;
    }

    public ObjectValidationContext<T> validateString(String context, Function<T, String> getter, ValidateResultEvaluator<StringValidationContext> validator) {

        if (!isCurrentToBeCheckedItemNull()) {
            StringValidationContext validationContext = new StringValidationContext(context, result().getCompletePath(),
                    buildCompleteLocation(result().getLocation(), context), getter.apply(getCurrentItemToBeChecked()));
            ValidationResult result = validator.validate(validationContext).result();
            result().register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return this;
    }

    public ObjectValidationContext<T> validateString(String context, Function<T, String> getter, int length, Required required) {

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
    
    public <V> ObjectValidationContext<T> validate(String context, Function<T, V> getter, Validator<ObjectValidationContext<V>> validator) {

        if (!isCurrentToBeCheckedItemNull()) {
            ObjectValidationContext<V> validationContext = new ObjectValidationContext<V>(context, result().getCompletePath(), buildCompleteLocation(
                    result().getLocation(), context), getter.apply(getCurrentItemToBeChecked()));
            ValidationResult result = validator.validate(validationContext);
            result().register(result);
        }
        registerWhenItemIsNullButNotOptional();
        return this;
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

}
