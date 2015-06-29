package dk.emstar.common.validation;

public class DefaultValidationFactory implements ValidationFactory {

    @Override
    public <T> ObjectValidationContext<T> validate(String context, T itemToBeValidated) {
        return new ObjectValidationContext<T>(context, itemToBeValidated);
    }
}
