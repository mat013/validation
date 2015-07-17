package dk.emstar.common.validation;

@FunctionalInterface
public interface ValidationResultProvider {

    String MISSING = "VALRES-1";
    String TOO_LONG = "VALRES-2";
    String NOT_MARKED_AS_OPTIONAL = "VALRES-3";
    String IS_EMPTY = "VALRES-4";
    String MISMATCH = "VALRES-5";
    
    String AFTER_TEMPORAL = "VALRES-20";
    String BEFORE_TEMPORAL = "VALRES-21";
    String BETWEEN_TEMPORAL = "VALRES-22";
    String NOT_BETWEEN_TEMPORAL = "VALRES-23";


    ValidationResult result();
}
