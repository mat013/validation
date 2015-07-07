package dk.emstar.common.validation;

import java.util.Collection;

public class CollectionValidationContext<T> extends ValidationContext<CollectionValidationContext<T>, Collection<T>> {

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

    public CollectionValidationContext<T> evaluateEachItem(String context, ValidateResultEvaluator<ObjectValidationContext<T>> validator) {
        return validateEachItem(context, o -> validator.validate(o).result());
    }

    public CollectionValidationContext<T> validateEachItem(String context, Validator<ObjectValidationContext<T>> validator) {
        if (!isCurrentToBeCheckedItemNull()) {
            int index = 0;
            for (T element : getCurrentItemToBeChecked()) {
                ValidationResult validationResult = validator.validate(new ObjectValidationContext<T>(context, getCompletePath(), String.format("%s[%s]",
                        getCompletePath(), index++), element));
                register(validationResult);
            }
        }

        registerWhenItemIsNullButNotOptional();
        return this;
    }
    
    // failWhenHas
    // failWhenHasNot
    // failWhenHasDuplicate

}
