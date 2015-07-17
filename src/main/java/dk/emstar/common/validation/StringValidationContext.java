package dk.emstar.common.validation;

import java.util.Collection;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

public class StringValidationContext extends ValidationContext<StringValidationContext, String> {
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

    public StringValidationContext failWhenNotMatching(Pattern pattern) {
        if(pattern == null) {
            throw new IllegalArgumentException("pattern is missing");
        }
        
        if(!(isOptional() && isCurrentToBeCheckedItemNull()) && 
                (isCurrentToBeCheckedItemNull() || !pattern.matcher(getCurrentItemToBeChecked()).matches())) {
            registerAsFailure(MISMATCH, "not matching", getCurrentItemToBeChecked());
        } 

        return this;
    }
    
    public StringValidationContext failWhenNotIn(Collection<String> items) {
        if(items == null) {
            throw new IllegalArgumentException("items are missing");
        }

        if(!(isOptional() && isCurrentToBeCheckedItemNull()) && 
                (isCurrentToBeCheckedItemNull() || !items.contains(getCurrentItemToBeChecked()))) {
            registerAsFailure(MISMATCH, "not matching", getCurrentItemToBeChecked());
        } 

        return this;
    }
    
    public StringValidationContext failWhenIn(Collection<String> items) {
        if(items == null) {
            throw new IllegalArgumentException("items are missing");
        }

        if(!(isOptional() && isCurrentToBeCheckedItemNull()) && 
                (isCurrentToBeCheckedItemNull() || items.contains(getCurrentItemToBeChecked()))) {
            registerAsFailure(MISMATCH, "not matching", getCurrentItemToBeChecked());
        } 

        return this;
    }
    
    public StringValidationContext failWhenMissingOrEmpty() {
        if(Strings.isNullOrEmpty(getCurrentItemToBeChecked())) {
            registerAsFailure(MISMATCH, "not matching", getCurrentItemToBeChecked());
        } 

        return this;
    }
}
