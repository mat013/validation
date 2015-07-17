package dk.emstar.common.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
public class StringValidationContextTest {
    @Test
    public void failNotMatching_HasNull_MismatchFailureRegistered() throws Exception {
        
        String myVar = null;
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        Pattern pattern = Pattern.compile("\\d{2}-\\d{2}");
        ValidationResult result = stringValidationContext.failWhenNotMatching(pattern).result();
        
        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("myVar");
        assertThat(actual.getContextPath()).isEqualTo("myVar");
        assertThat(actual.getLocation()).isEqualTo("myVar");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISMATCH);
    }

    @Test
    public void failNotMatching_HasInvalidValue_MismatchFailureRegistered() throws Exception {
        
        String myVar = "myvar";
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        Pattern pattern = Pattern.compile("\\d{2}-\\d{2}");
        ValidationResult result = stringValidationContext.failWhenNotMatching(pattern).result();
        
        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().count()).isEqualTo(1l);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("myVar");
        assertThat(actual.getContextPath()).isEqualTo("myVar");
        assertThat(actual.getLocation()).isEqualTo("myVar");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISMATCH);
    }

    @Test
    public void failNotMatching_HasValidValue_NoMismatchFailureRegistered() throws Exception {
        
        String myVar = "11-22";
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        Pattern pattern = Pattern.compile("\\d{2}-\\d{2}");
        ValidationResult result = stringValidationContext.failWhenNotMatching(pattern).result();
        
        assertThat(result.hasFailure()).isEqualTo(false);
    }
    
    @Test
    public void failNotMatchingUnlessOptional_HasNullValue_NoMismatchFailureRegistered() throws Exception {
        
        String myVar = null;
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        Pattern pattern = Pattern.compile("\\d{2}-\\d{2}");
        ValidationResult result = stringValidationContext
                .asOptional()
                .failWhenNotMatching(pattern)
                .result();
        
        assertThat(result.hasFailure()).isEqualTo(false);
    }
    
    @Test
    public void failWhenNotIn_HasNull_MismatchFailureRegistered() throws Exception {
        
        String myVar = null;
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        

        ValidationResult result = stringValidationContext.failWhenNotIn(Sets.newHashSet("a", "b")).result();
        
        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("myVar");
        assertThat(actual.getContextPath()).isEqualTo("myVar");
        assertThat(actual.getLocation()).isEqualTo("myVar");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISMATCH);
    }

    @Test
    public void failWhenNotIn_HasInvalidValue_MismatchFailureRegistered() throws Exception {
        
        String myVar = "myvar";
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext.failWhenNotIn(Sets.newHashSet("a", "b")).result();
        
        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().count()).isEqualTo(1l);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("myVar");
        assertThat(actual.getContextPath()).isEqualTo("myVar");
        assertThat(actual.getLocation()).isEqualTo("myVar");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISMATCH);
    }

    @Test
    public void failWhenNotIn_HasValidValue_NoMismatchFailureRegistered() throws Exception {
        
        String myVar = "11-22";
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext.failWhenNotIn(Sets.newHashSet("11-22", "b")).result();
        
        assertThat(result.hasFailure()).isFalse();
    }
    
    @Test
    public void failWhenNotInUnlessOptional_HasNullValue_NoMismatchFailureRegistered() throws Exception {
        
        String myVar = null;
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext
                .asOptional()
                .failWhenNotIn(Sets.newHashSet("a", "b"))
                .result();
        
        assertThat(result.hasFailure()).isFalse();
    }

    @Test
    public void failWhenIn_HasNull_MismatchFailureRegistered2() throws Exception {
        
        String myVar = null;
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext.failWhenIn(Sets.newHashSet("a", "b")).result();
        
        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("myVar");
        assertThat(actual.getContextPath()).isEqualTo("myVar");
        assertThat(actual.getLocation()).isEqualTo("myVar");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISMATCH);
    }

    @Test
    public void failWhenIn_HasInvalidValue_MismatchFailureRegistered() throws Exception {
        
        String myVar = "a";
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext.failWhenIn(Sets.newHashSet("a", "b")).result();
        
        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("myVar");
        assertThat(actual.getContextPath()).isEqualTo("myVar");
        assertThat(actual.getLocation()).isEqualTo("myVar");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISMATCH);
    }

    @Test
    public void failWhenIn_HasValidValue_NoMismatchFailureRegistered() throws Exception {
        
        String myVar = "11-22";
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext.failWhenIn(Sets.newHashSet("a", "b")).result();
        
        assertThat(result.hasFailure()).isFalse();
    }
    
    @Test
    public void failWhenInUnlessOptional_HasNullValue_NoMismatchFailureRegistered() throws Exception {
        
        String myVar = null;
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext
                .asOptional()
                .failWhenIn(Sets.newHashSet("a", "b"))
                .result();
        
        assertThat(result.hasFailure()).isFalse();
    }

    @Test
    public void failWhenMissingOrEmpty_IsNull_MismatchFailureRegistered() throws Exception {
        
        String myVar = null;
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext.failWhenMissingOrEmpty().result();
        
        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().count()).isEqualTo(1l);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("myVar");
        assertThat(actual.getContextPath()).isEqualTo("myVar");
        assertThat(actual.getLocation()).isEqualTo("myVar");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISMATCH);
    }

    @Test
    public void failWhenMissingOrEmpty_IsEmpty_MismatchFailureRegistered3() throws Exception {
        
        String myVar = "";
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext.failWhenMissingOrEmpty().result();
        
        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("myVar");
        assertThat(actual.getContextPath()).isEqualTo("myVar");
        assertThat(actual.getLocation()).isEqualTo("myVar");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISMATCH);
    }

    @Test
    public void failWhenMissingOrEmpty_HasValidValue_NoMismatchFailureRegistered() throws Exception {
        
        String myVar = "11-22";
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext.failWhenMissingOrEmpty().result();
        
        assertThat(result.hasFailure()).isFalse();
    }
}
