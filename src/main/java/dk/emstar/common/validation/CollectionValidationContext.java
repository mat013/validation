package dk.emstar.common.validation;

import java.util.Collection;

public class CollectionValidationContext<T> extends AbstractValidationContext<CollectionValidationContext<T>, Collection<T>> {

    public CollectionValidationContext(String context, Collection<T> currentItemToBeChecked) {
        super(context, currentItemToBeChecked);
    }

    public CollectionValidationContext(String context, String contextPath, String location, Collection<T> currentItemToBeChecked) {
        super(context, contextPath, location, currentItemToBeChecked);
    }

    public CollectionValidationContext<T> failWhenEmpty() {
        if (!isCurrentToBeCheckedItemNull()) {
            if (getCurrentItemToBeChecked().size() == 0) {
                registerAsFailure(IS_EMPTY, "is empty", getCurrentItemToBeChecked());
            }
        }

        registerWhenItemIsNullButNotOptional();

        return this;
    }

    public CollectionValidationContext<T> evaluateEachItem(String context, ValidateResultEvaluator<ValidationContext<T>> validator) {
        return validateEachItem(context, o -> validator.validate(o).result());
    }

    public CollectionValidationContext<T> validateEachItem(String context, Validator<ValidationContext<T>> validator) {
        if (!isCurrentToBeCheckedItemNull()) {
            int index = 0;
            for (T element : getCurrentItemToBeChecked()) {
                ValidationResult validationResult = validator.validate(new ValidationContext<T>(context, getCompletePath(), String.format("%s[%s]",
                        getCompletePath(), index++), element));
                register(validationResult);
            }
        }

        registerWhenItemIsNullButNotOptional();
        return this;
    }
}
