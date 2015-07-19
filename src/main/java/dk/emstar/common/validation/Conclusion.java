package dk.emstar.common.validation;

@FunctionalInterface
public interface Conclusion {

    ThrowableValidationResult conclude(ThrowableValidationResult throwableValidationResult);
}
