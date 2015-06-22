package dk.emstar.common.validation;

import java.util.Collection;

public class CollectionValidationContext<T> extends AbstractValidationContext<CollectionValidationContext<T>, Collection<T>> {

    public CollectionValidationContext(String context, Collection<T> currentItemToBeChecked) {
        super(context, currentItemToBeChecked);
    }

    public CollectionValidationContext<T> failWhenEmpty(String failureCode) {
        if (!isCurrentToBeCheckedItemNull()) {
            if (getCurrentItemToBeChecked().size() == 0) {
                registerAsFailure(failureCode, "is empty", getCurrentItemToBeChecked());
            }
        }

        registerWhenItemIsNullButNotOptional();

        return this;
    }

    public CollectionValidationContext<T> validateEachItem(Validator<ValidationContext<T>> validator) {
        if (!isCurrentToBeCheckedItemNull()) {
            int index = 0;
            for (T element : getCurrentItemToBeChecked()) {
                ValidationResult validationResult = validator.validate(new ValidationContext<T>(String.format("%s", index++), element));
                register(validationResult);
            }
        }

        registerWhenItemIsNullButNotOptional();
        return this;
    }
}
