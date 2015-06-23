package dk.emstar.common.validation;

public class StringValidationContext extends AbstractValidationContext<StringValidationContext, String> {
    public StringValidationContext(String context, String contextPath, String location, String currentItemToBeChecked) {
        super(context, contextPath, location, currentItemToBeChecked);
    }

    public StringValidationContext failWhenLongerThan(int length) {
        if (!isCurrentToBeCheckedItemNull()) {
            if (getCurrentItemToBeChecked().length() > length) {
                registerAsFailure(TOO_LONG, "too long", getCurrentItemToBeChecked());
            }
        }

        registerWhenItemIsNullButNotOptional();

        return this;
    }
}
