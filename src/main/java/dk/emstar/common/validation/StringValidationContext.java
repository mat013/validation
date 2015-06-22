package dk.emstar.common.validation;

public class StringValidationContext extends AbstractValidationContext<StringValidationContext, String> {
    public StringValidationContext(String context, String currentItemToBeChecked) {
        super(context, currentItemToBeChecked);
    }

    public StringValidationContext failWhenLongerThan(int length, String failureCode) {
        if (!isCurrentToBeCheckedItemNull()) {
            if (getCurrentItemToBeChecked().length() > length) {
                registerAsFailure(failureCode, "", getCurrentItemToBeChecked());
            }
        }

        registerWhenItemIsNullButNotOptional();

        return this;
    }
}
