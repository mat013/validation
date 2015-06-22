package dk.emstar.common.validation;


public class ValidationContext<T> extends AbstractValidationContext<ValidationContext<T>, T> {

    public ValidationContext(String context, T currentItemToBeChecked) {
        super(context, currentItemToBeChecked);
    }

}
