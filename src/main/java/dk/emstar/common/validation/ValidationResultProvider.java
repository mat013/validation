package dk.emstar.common.validation;

@FunctionalInterface
public interface ValidationResultProvider {

    String MISSING = "VALRES-1";
    String TOO_LONG = "VALRES-2";
    String NOT_MARKED_AS_OPTIONAL = "VALRES-3";
    String IS_EMPTY = "VALRES-4";

    ValidationResult result();
}
