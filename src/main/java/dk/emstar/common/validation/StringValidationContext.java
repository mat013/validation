package dk.emstar.common.validation;

import java.util.regex.Pattern;

public class StringValidationContext extends AbstractValidationContext<StringValidationContext, String> {
    public StringValidationContext(String context, String contextPath, String location, String currentItemToBeChecked) {
        super(context, contextPath, location, currentItemToBeChecked);
    }

    public StringValidationContext failWhenLongerThan(int length) {
        if (!isCurrentToBeCheckedItemNull()) {
            if (getCurrentItemToBeChecked().length() > length) {
                registerAsFailure(TOO_LONG, "too long", getCurrentItemToBeChecked());
            }
        }

        registerWhenItemIsNullButNotOptional();

        return this;
    }

    public StringValidationContext failNotMatching(Pattern pattern) {
        if (!isCurrentToBeCheckedItemNull()) {
            if (!pattern.matcher(getCurrentItemToBeChecked()).matches()) {
                registerAsFailure(MISMATCH, "not matching", getCurrentItemToBeChecked());
            }
        }

        registerWhenItemIsNullButNotOptional();

        return this;
    }
    
    
    // failWhenMatching
    // TODO within a set
    // failWhenIn
    // failWhenNotIn
    // failWhenMissingOrEmpty()
    
    
    
}
