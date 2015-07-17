package dk.emstar.common.validation;

import java.time.LocalDateTime;

public class LocalDateTimeValidationContext extends ValidationContext<LocalDateTimeValidationContext, LocalDateTime> {

    
    public LocalDateTimeValidationContext(String context, String contextPath, String location, LocalDateTime currentItemToBeChecked) {
        super(context, contextPath, location, currentItemToBeChecked);
    }

    public LocalDateTimeValidationContext failWhenBetween(LocalDateTime begin, LocalDateTime end) {
        if(!isCurrentToBeCheckedItemNull()) {
            if(begin == null) {
                throw new IllegalArgumentException("begin is missing");
            }
            
            if(end == null) {
                throw new IllegalArgumentException("end is missing");
            }
            
            LocalDateTime currentItemToBeChecked = getCurrentItemToBeChecked();
            boolean before = begin.isBefore(currentItemToBeChecked) || begin.isEqual(currentItemToBeChecked);
            boolean after = end.isAfter(currentItemToBeChecked) || end.isEqual(currentItemToBeChecked);
            if((before && after)) {
                registerAsFailure(BETWEEN_TEMPORAL, "between", currentItemToBeChecked);
            }
        }
        registerWhenItemIsNullButNotOptional();

        return this;
    }

    public LocalDateTimeValidationContext failWhenNotBetween(LocalDateTime begin, LocalDateTime end) {
        if(!isCurrentToBeCheckedItemNull()) {
            if(begin == null) {
                throw new IllegalArgumentException("begin is missing");
            }
            
            if(end == null) {
                throw new IllegalArgumentException("end is missing");
            }
            
            LocalDateTime currentItemToBeChecked = getCurrentItemToBeChecked();
            boolean before = begin.isBefore(currentItemToBeChecked) || begin.isEqual(currentItemToBeChecked);
            boolean after = end.isAfter(currentItemToBeChecked) || end.isEqual(currentItemToBeChecked);
            if(!(before && after)) {
                registerAsFailure(NOT_BETWEEN_TEMPORAL, "not in between", currentItemToBeChecked);
            }
        }
        registerWhenItemIsNullButNotOptional();

        return this;
    }

    public LocalDateTimeValidationContext failWhenAfter(LocalDateTime localDateTime) {
        if(!isCurrentToBeCheckedItemNull()) {
            if(localDateTime == null) {
                throw new IllegalArgumentException("validator is missing");
            }
            
            if(getCurrentItemToBeChecked().isAfter(localDateTime)) {
                registerAsFailure(AFTER_TEMPORAL, "after", getCurrentItemToBeChecked());
            }
        }
        registerWhenItemIsNullButNotOptional();

        return this;
    }

    public LocalDateTimeValidationContext failWhenBefore(LocalDateTime localDateTime) {
        if(!isCurrentToBeCheckedItemNull()) {
            if(localDateTime == null) {
                throw new IllegalArgumentException("validator is missing");
            }
            
            if(getCurrentItemToBeChecked().isBefore(localDateTime)) {
                registerAsFailure(BEFORE_TEMPORAL, "before", getCurrentItemToBeChecked());
            }
        }

        registerWhenItemIsNullButNotOptional();

        return this;
    }
}
