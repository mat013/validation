package dk.emstar.common.validation;

public interface ValidationFactory {

    <T> ObjectValidationContext<T> validate(String context, T itemToBeValidated);
}
