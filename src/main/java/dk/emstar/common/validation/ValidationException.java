package dk.emstar.common.validation;


public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 7449469069625127238L;
    private final String validationCode;
    private final ValidationResult validationResult;

    public ValidationException(String validationCode, String validationMessage, ValidationResult validationResult) {
        super(validationMessage);
        this.validationCode = validationCode;
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }
    
    public String getValidationCode() {
        return validationCode;
    }
}
