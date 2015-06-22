package dk.emstar.common.validation;

public class DefaultValidationFactory implements ValidationFactory {

    @Override
    public <T> ValidationContext<T> validate(String context, T itemToBeValidated) {
        return new ValidationContext<T>(context, itemToBeValidated);
    }
}
