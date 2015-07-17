package dk.emstar.common.validation;

import org.slf4j.Logger;

public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 7449469069625127238L;
    private final String validationCode;
    private final ValidationResult validationResult;
    private final ValidationRegistration validationRegistration;

    public ValidationException(String validationCode, String validationMessage, ValidationResult validationResult, ValidationRegistration validationRegistrationsult) {
        super(validationMessage);
        this.validationCode = validationCode;
        this.validationResult = validationResult;
        this.validationRegistration = validationRegistrationsult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }
    
    public String getValidationCode() {
        return validationCode;
    }
    
    public void logCausingRegistration(Logger logger) {
        if (ValidationLevel.Failure.equals(validationRegistration.getValidationLevel())) {
            logger.error(validationRegistration.getDetails());
        } else {
            logger.warn(validationRegistration.getDetails());
        }
    }

}
