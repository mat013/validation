package dk.emstar.common.validation;

@FunctionalInterface
public interface ValidateResultEvaluator<T> {
    ValidationResultProvider validate(T input);
}
