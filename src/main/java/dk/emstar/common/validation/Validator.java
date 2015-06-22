package dk.emstar.common.validation;

public interface Validator<T> {
    ValidationResult validate(T input);
}
