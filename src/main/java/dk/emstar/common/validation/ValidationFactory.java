package dk.emstar.common.validation;

public interface ValidationFactory {
    <T> ValidationContext<T> validate(String context, T itemToBeValidated);
}
