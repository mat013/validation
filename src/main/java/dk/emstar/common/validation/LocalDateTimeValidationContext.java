package dk.emstar.common.validation;

import java.time.LocalDateTime;

public class LocalDateTimeValidationContext extends ValidationContext<LocalDateTimeValidationContext, LocalDateTime> {
    public LocalDateTimeValidationContext(String context, String contextPath, String location, LocalDateTime currentItemToBeChecked) {
        super(context, contextPath, location, currentItemToBeChecked);
    }

    public LocalDateTimeValidationContext failWhenBetween(LocalDateTime begin, LocalDateTime end) {
        registerWhenItemIsNullButNotOptional();

        return this;
    }

    public LocalDateTimeValidationContext failWhenNotBetween(LocalDateTime begin, LocalDateTime end) {
        registerWhenItemIsNullButNotOptional();

        return this;
    }

    public LocalDateTimeValidationContext failWhenAfter(LocalDateTime localDateTime) {
        registerWhenItemIsNullButNotOptional();

        return this;
    }

    public LocalDateTimeValidationContext failWhenBefore(LocalDateTime localDateTime) {
        registerWhenItemIsNullButNotOptional();

        return this;
    }
}
