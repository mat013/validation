package dk.emstar.common.validation;

import static org.assertj.core.api.StrictAssertions.assertThat;

import java.time.LocalDateTime;

import org.junit.Test;

public class LocalDateTimeValidationContextTest {

    @Test
    public void failWhenMissing_IsNull_MissingRegistered() throws Exception {
        LocalDateTime localDateTime = null;

        LocalDateTimeValidationContext localDateTimeValidationContext 
            = new LocalDateTimeValidationContext("myVar", "", "myVar", localDateTime);

        ValidationResult result = localDateTimeValidationContext
            .failWhenMissing()
            .result();
        
        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("myVar");
        assertThat(actual.getContextPath()).isEqualTo("myVar");
        assertThat(actual.getLocation()).isEqualTo("myVar");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISSING);
    }

    @Test
    public void asOptional_IsNull_NoFailureRegistered() throws Exception {
        LocalDateTime localDateTime = null;

        LocalDateTimeValidationContext localDateTimeValidationContext 
            = new LocalDateTimeValidationContext("myVar", "", "myVar", localDateTime);

        ValidationResult result = localDateTimeValidationContext
            .asOptional()
            .result();
        
        assertThat(result.hasFailure()).isFalse();
    }
    
    @Test
    public void failWhenAfter_DateProvidedIsLater_LaterThanRegistered() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2014, 11, 8, 3, 46);

        LocalDateTimeValidationContext localDateTimeValidationContext 
            = new LocalDateTimeValidationContext("myVar", "", "myVar", localDateTime);

        ValidationResult result = localDateTimeValidationContext
            .failWhenMissing()
            .failWhenAfter(LocalDateTime.of(2013, 04, 21, 8, 50))
            .result();
        
        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("myVar");
        assertThat(actual.getContextPath()).isEqualTo("myVar");
        assertThat(actual.getLocation()).isEqualTo("myVar");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.AFTER_TEMPORAL);
    }
    
    @Test
    public void failWhenBefore_DateProvidedIsBefore_BeforeThanRegistered() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2013, 04, 21, 8, 50);

        LocalDateTimeValidationContext localDateTimeValidationContext 
            = new LocalDateTimeValidationContext("myVar", "", "myVar", localDateTime);

        ValidationResult result = localDateTimeValidationContext
            .failWhenMissing()
            .failWhenBefore(LocalDateTime.of(2014, 11, 8, 3, 46))
            .result();
        
        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("myVar");
        assertThat(actual.getContextPath()).isEqualTo("myVar");
        assertThat(actual.getLocation()).isEqualTo("myVar");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.BEFORE_TEMPORAL);
    }

    @Test
    public void failWhenNotBetween_DateProvidedIsBetween_NoFailure() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2014, 01, 01, 0, 15);

        LocalDateTimeValidationContext localDateTimeValidationContext 
            = new LocalDateTimeValidationContext("myVar", "", "myVar", localDateTime);

        ValidationResult result = localDateTimeValidationContext
            .failWhenMissing()
            .failWhenNotBetween(LocalDateTime.of(2014, 01, 01, 0, 10), LocalDateTime.of(2014, 01, 01, 0, 20))
            .result();
        
        assertThat(result.hasFailure()).isFalse();
    }

    @Test
    public void fail1WhenNotBetween_DateProvidedIsSameAsBeginning_NoFailure() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2014, 01, 01, 0, 10);

        LocalDateTimeValidationContext localDateTimeValidationContext 
            = new LocalDateTimeValidationContext("myVar", "", "myVar", localDateTime);

        ValidationResult result = localDateTimeValidationContext
            .failWhenMissing()
            .failWhenNotBetween(LocalDateTime.of(2014, 01, 01, 0, 10), LocalDateTime.of(2014, 01, 01, 0, 20))
            .result();
        
        assertThat(result.hasFailure()).isFalse();
    }
    @Test
    public void fail1WhenNotBetween_DateProvidedIsSameAsEnd_NoFailure() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2014, 01, 01, 0, 20);

        LocalDateTimeValidationContext localDateTimeValidationContext 
            = new LocalDateTimeValidationContext("myVar", "", "myVar", localDateTime);

        ValidationResult result = localDateTimeValidationContext
            .failWhenMissing()
            .failWhenNotBetween(LocalDateTime.of(2014, 01, 01, 0, 10), LocalDateTime.of(2014, 01, 01, 0, 20))
            .result();
        
        assertThat(result.hasFailure()).isFalse();
    }
    
    @Test
    public void fail1WhenNotBetween_DateProvidedIsBeforeBeginning_FailureRegistered() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2014, 01, 01, 0, 9);

        LocalDateTimeValidationContext localDateTimeValidationContext 
            = new LocalDateTimeValidationContext("myVar", "", "myVar", localDateTime);

        ValidationResult result = localDateTimeValidationContext
            .failWhenMissing()
            .failWhenNotBetween(LocalDateTime.of(2014, 01, 01, 0, 10), LocalDateTime.of(2014, 01, 01, 0, 20))
            .result();
        
        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("myVar");
        assertThat(actual.getContextPath()).isEqualTo("myVar");
        assertThat(actual.getLocation()).isEqualTo("myVar");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.NOT_BETWEEN_TEMPORAL);
    }
    @Test
    public void fail1WhenNotBetween_DateProvidedIsAfterEnd_FailureRegistered() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2014, 01, 01, 0, 21);

        LocalDateTimeValidationContext localDateTimeValidationContext 
            = new LocalDateTimeValidationContext("myVar", "", "myVar", localDateTime);

        ValidationResult result = localDateTimeValidationContext
            .failWhenMissing()
            .failWhenNotBetween(LocalDateTime.of(2014, 01, 01, 0, 10), LocalDateTime.of(2014, 01, 01, 0, 20))
            .result();
        
        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("myVar");
        assertThat(actual.getContextPath()).isEqualTo("myVar");
        assertThat(actual.getLocation()).isEqualTo("myVar");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.NOT_BETWEEN_TEMPORAL);
    }

    @Test
    public void failWhenBetween_DateProvidedIsBetween_NoFailure() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2014, 01, 01, 0, 15);

        LocalDateTimeValidationContext localDateTimeValidationContext 
            = new LocalDateTimeValidationContext("myVar", "", "myVar", localDateTime);

        ValidationResult result = localDateTimeValidationContext
            .failWhenMissing()
            .failWhenBetween(LocalDateTime.of(2014, 01, 01, 0, 10), LocalDateTime.of(2014, 01, 01, 0, 20))
            .result();
        
        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("myVar");
        assertThat(actual.getContextPath()).isEqualTo("myVar");
        assertThat(actual.getLocation()).isEqualTo("myVar");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.BETWEEN_TEMPORAL);
    }

    @Test
    public void fail1WhenBetween_DateProvidedIsSameAsBeginning_NoFailure() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2014, 01, 01, 0, 10);

        LocalDateTimeValidationContext localDateTimeValidationContext 
            = new LocalDateTimeValidationContext("myVar", "", "myVar", localDateTime);

        ValidationResult result = localDateTimeValidationContext
            .failWhenMissing()
            .failWhenBetween(LocalDateTime.of(2014, 01, 01, 0, 10), LocalDateTime.of(2014, 01, 01, 0, 20))
            .result();
        
        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("myVar");
        assertThat(actual.getContextPath()).isEqualTo("myVar");
        assertThat(actual.getLocation()).isEqualTo("myVar");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.BETWEEN_TEMPORAL);
    }
    @Test
    public void fail1WhenBetween_DateProvidedIsSameAsEnd_NoFailure() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2014, 01, 01, 0, 20);

        LocalDateTimeValidationContext localDateTimeValidationContext 
            = new LocalDateTimeValidationContext("myVar", "", "myVar", localDateTime);

        ValidationResult result = localDateTimeValidationContext
            .failWhenMissing()
            .failWhenBetween(LocalDateTime.of(2014, 01, 01, 0, 10), LocalDateTime.of(2014, 01, 01, 0, 20))
            .result();
        
        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("myVar");
        assertThat(actual.getContextPath()).isEqualTo("myVar");
        assertThat(actual.getLocation()).isEqualTo("myVar");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.BETWEEN_TEMPORAL);
    }
    
    @Test
    public void fail1WhenBetween_DateProvidedIsBeforeBeginning_FailureRegistered() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2014, 01, 01, 0, 9);

        LocalDateTimeValidationContext localDateTimeValidationContext 
            = new LocalDateTimeValidationContext("myVar", "", "myVar", localDateTime);

        ValidationResult result = localDateTimeValidationContext
            .failWhenMissing()
            .failWhenBetween(LocalDateTime.of(2014, 01, 01, 0, 10), LocalDateTime.of(2014, 01, 01, 0, 20))
            .result();
        
        assertThat(result.hasFailure()).isFalse();
    }
    @Test
    public void fail1WhenBetween_DateProvidedIsAfterEnd_FailureRegistered() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2014, 01, 01, 0, 21);

        LocalDateTimeValidationContext localDateTimeValidationContext 
            = new LocalDateTimeValidationContext("myVar", "", "myVar", localDateTime);

        ValidationResult result = localDateTimeValidationContext
            .failWhenMissing()
            .failWhenBetween(LocalDateTime.of(2014, 01, 01, 0, 10), LocalDateTime.of(2014, 01, 01, 0, 20))
            .result();
        
        assertThat(result.hasFailure()).isFalse();
    }

}
