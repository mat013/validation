package dk.emstar.common.validation;

@FunctionalInterface
public interface Validator<T> {
    ValidationResult validate(T input);
}
