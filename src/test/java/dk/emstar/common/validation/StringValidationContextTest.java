package dk.emstar.common.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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
        
        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("myVar")));
        assertThat(actual.getContextPath(), is(equalTo("myVar")));
        assertThat(actual.getLocation(), is(equalTo("myVar")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.MISMATCH));
    }

    @Test
    public void failNotMatching_HasInvalidValue_MismatchFailureRegistered() throws Exception {
        
        String myVar = "myvar";
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        Pattern pattern = Pattern.compile("\\d{2}-\\d{2}");
        ValidationResult result = stringValidationContext.failWhenNotMatching(pattern).result();
        
        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("myVar")));
        assertThat(actual.getContextPath(), is(equalTo("myVar")));
        assertThat(actual.getLocation(), is(equalTo("myVar")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.MISMATCH));
    }

    @Test
    public void failNotMatching_HasValidValue_NoMismatchFailureRegistered() throws Exception {
        
        String myVar = "11-22";
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        Pattern pattern = Pattern.compile("\\d{2}-\\d{2}");
        ValidationResult result = stringValidationContext.failWhenNotMatching(pattern).result();
        
        assertThat(result.hasFailure(), is(false));
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
        
        assertThat(result.hasFailure(), is(false));
    }
    
    @Test
    public void failWhenNotIn_HasNull_MismatchFailureRegistered() throws Exception {
        
        String myVar = null;
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        

        ValidationResult result = stringValidationContext.failWhenNotIn(Sets.newHashSet("a", "b")).result();
        
        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("myVar")));
        assertThat(actual.getContextPath(), is(equalTo("myVar")));
        assertThat(actual.getLocation(), is(equalTo("myVar")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.MISMATCH));
    }

    @Test
    public void failWhenNotIn_HasInvalidValue_MismatchFailureRegistered() throws Exception {
        
        String myVar = "myvar";
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext.failWhenNotIn(Sets.newHashSet("a", "b")).result();
        
        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("myVar")));
        assertThat(actual.getContextPath(), is(equalTo("myVar")));
        assertThat(actual.getLocation(), is(equalTo("myVar")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.MISMATCH));
    }

    @Test
    public void failWhenNotIn_HasValidValue_NoMismatchFailureRegistered() throws Exception {
        
        String myVar = "11-22";
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext.failWhenNotIn(Sets.newHashSet("11-22", "b")).result();
        
        assertThat(result.hasFailure(), is(false));
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
        
        assertThat(result.hasFailure(), is(false));
    }

    @Test
    public void failWhenIn_HasNull_MismatchFailureRegistered2() throws Exception {
        
        String myVar = null;
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext.failWhenIn(Sets.newHashSet("a", "b")).result();
        
        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("myVar")));
        assertThat(actual.getContextPath(), is(equalTo("myVar")));
        assertThat(actual.getLocation(), is(equalTo("myVar")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.MISMATCH));
    }

    @Test
    public void failWhenIn_HasInvalidValue_MismatchFailureRegistered() throws Exception {
        
        String myVar = "a";
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext.failWhenIn(Sets.newHashSet("a", "b")).result();
        
        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("myVar")));
        assertThat(actual.getContextPath(), is(equalTo("myVar")));
        assertThat(actual.getLocation(), is(equalTo("myVar")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.MISMATCH));
    }

    @Test
    public void failWhenIn_HasValidValue_NoMismatchFailureRegistered() throws Exception {
        
        String myVar = "11-22";
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext.failWhenIn(Sets.newHashSet("a", "b")).result();
        
        assertThat(result.hasFailure(), is(false));
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
        
        assertThat(result.hasFailure(), is(false));
    }

    @Test
    public void failWhenMissingOrEmpty_IsNull_MismatchFailureRegistered() throws Exception {
        
        String myVar = null;
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext.failWhenMissingOrEmpty().result();
        
        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("myVar")));
        assertThat(actual.getContextPath(), is(equalTo("myVar")));
        assertThat(actual.getLocation(), is(equalTo("myVar")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.MISMATCH));
    }

    @Test
    public void failWhenMissingOrEmpty_IsEmpty_MismatchFailureRegistered3() throws Exception {
        
        String myVar = "";
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext.failWhenMissingOrEmpty().result();
        
        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("myVar")));
        assertThat(actual.getContextPath(), is(equalTo("myVar")));
        assertThat(actual.getLocation(), is(equalTo("myVar")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.MISMATCH));
    }

    @Test
    public void failWhenMissingOrEmpty_HasValidValue_NoMismatchFailureRegistered() throws Exception {
        
        String myVar = "11-22";
        StringValidationContext stringValidationContext 
            = new StringValidationContext("myVar", "", "myVar", myVar);
        
        ValidationResult result = stringValidationContext.failWhenMissingOrEmpty().result();
        
        assertThat(result.hasFailure(), is(false));
    }
}
