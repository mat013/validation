package dk.emstar.common.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import dk.emstar.common.validation.ValidationException;
import dk.emstar.common.validation.ValidationRegistration;
import dk.emstar.common.validation.ValidationResult;

@RunWith(MockitoJUnitRunner.class)
public class ValidationResultTest {

    private static final Logger logger = LoggerFactory.getLogger(ValidationResultTest.class);

    private ValidationResult validationResult;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        validationResult = new ValidationResult();
    }

    @Test
    public void throwIncludeAllMessagesWhenAnyFailures_SomeRegistrations_ExceptionThrownWithValidationCode10() throws Exception {
        thrown.expect(ValidationException.class);

        validationResult.registerNullValidation("11", "input is null");
        validationResult.registerValidationFailure("12", "input contains test", "test");
        validationResult.registerValidationFailure("13", "input contains test and test2", "test", "test2");

        validationResult.throwIncludeAllMessagesWhenAnyFailures("10", logger);
    }

    @Test
    public void throwIncludeAllMessagesWhenAnyFailures_NoErrorcodeSpecified_ExceptionThrownWithValidationCode10() throws Exception {
        thrown.expect(ValidationException.class);

        validationResult.registerNullValidation("11", "input is null");
        validationResult.registerValidationFailure("12", "input contains test", "test");
        validationResult.registerValidationFailure("13", "input contains test and test2", "test", "test2");

        try {
            validationResult.throwForFirstFoundWhenAnyFailures(logger);
        } catch (ValidationException e) {
            assertThat(e.getValidationCode(), is("11"));
            throw e;
        }
    }

    @Test
    public void throwIncludeAllMessagesWhenAnyFailures_NoRegistrations_ExceptionIsNotThrown() throws Exception {
        validationResult.throwIncludeAllMessagesWhenAnyFailures("10", logger);
    }

    @Test
    public void iterator_3RegistrationInTheResult_HasThreeRegistration() throws Exception {

        validationResult.register(new ValidationRegistration("a", "first", ValidationLevel.Failure));
        validationResult.register(new ValidationRegistration("b", "first", ValidationLevel.Failure));
        validationResult.register(new ValidationRegistration("c", "first", ValidationLevel.Failure));

        List<ValidationRegistration> actual = Lists.newArrayList(validationResult.iterator());
        List<String> actualFailureCodes = actual.stream().map(o -> o.getValidationCode()).collect(Collectors.toList());
        assertThat(actualFailureCodes, is(Lists.newArrayList("a", "b", "c")));
    }

    @Test
    public void iterator_3RegistrationOneSubRegsultInTheResult_HasFourRegistration() throws Exception {

        ValidationResult subValidationResult = new ValidationResult();
        subValidationResult.register(new ValidationRegistration("b1", "first", ValidationLevel.Failure));
        subValidationResult.register(new ValidationRegistration("b2", "first", ValidationLevel.Failure));

        validationResult.register(new ValidationRegistration("a", "first", ValidationLevel.Failure));
        validationResult.registerAll(subValidationResult);
        validationResult.register(new ValidationRegistration("c", "first", ValidationLevel.Failure));

        List<ValidationRegistration> actual = Lists.newArrayList(validationResult.iterator());
        List<String> actualFailureCodes = actual.stream().map(o -> o.getValidationCode()).collect(Collectors.toList());
        assertThat(actualFailureCodes, is(Lists.newArrayList("a", "b1", "b2", "c")));
        assertThat(validationResult.hasFailure(), is(equalTo(true)));
    }

    @Test
    public void iterator_FailureInSubResult_HasFailure() throws Exception {
        ValidationResult subValidationResult = new ValidationResult();
        subValidationResult.register(new ValidationRegistration("b1", "first", ValidationLevel.Failure));
        subValidationResult.register(new ValidationRegistration("b2", "first", ValidationLevel.Warning));

        validationResult.register(new ValidationRegistration("a", "first", ValidationLevel.Warning));
        validationResult.registerAll(subValidationResult);
        validationResult.register(new ValidationRegistration("c", "first", ValidationLevel.Warning));

        List<ValidationRegistration> actual = Lists.newArrayList(validationResult.iterator());
        List<String> actualFailureCodes = actual.stream().map(o -> o.getValidationCode()).collect(Collectors.toList());
        assertThat(actualFailureCodes, is(Lists.newArrayList("a", "b1", "b2", "c")));
        assertThat(validationResult.hasFailure(), is(equalTo(true)));
    }

    @Test
    public void iterator_RegisterInTreeNode_InTheRegistrationOrder() throws Exception {
        ValidationResult subValidationResult = new ValidationResult();
        subValidationResult.register(new ValidationRegistration("b1", "first", ValidationLevel.Failure));
        subValidationResult.register(new ValidationRegistration("b2", "first", ValidationLevel.Warning));

        validationResult.register(new ValidationRegistration("a", "first", ValidationLevel.Warning));
        validationResult.register(subValidationResult);
        validationResult.register(new ValidationRegistration("c", "first", ValidationLevel.Warning));

        List<ValidationRegistration> actual = Lists.newArrayList(validationResult.iterator());
        List<String> actualFailureCodes = actual.stream().map(o -> o.getValidationCode()).collect(Collectors.toList());
        assertThat(actualFailureCodes, is(Lists.newArrayList("a", "b1", "b2", "c")));
        assertThat(validationResult.hasFailure(), is(equalTo(true)));
    }

    @Test
    public void iterator_FailureInSubResult_FirstFailurIsB1() throws Exception {
        ValidationResult subValidationResult = new ValidationResult();
        subValidationResult.register(new ValidationRegistration("b1", "first", ValidationLevel.Failure));
        subValidationResult.register(new ValidationRegistration("b2", "first", ValidationLevel.Warning));

        validationResult.register(new ValidationRegistration("a", "first", ValidationLevel.Warning));
        validationResult.register(subValidationResult);
        validationResult.register(new ValidationRegistration("c", "first", ValidationLevel.Warning));

        Optional<ValidationRegistration> actual = validationResult
                .stream()
                .filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel()))
                .findFirst();
        assertThat(actual.isPresent(), is(true));
        assertThat(actual.get().getValidationCode(), is("b1"));
    }

    @Test
    public void iterator_NoFailure_HasNoFailure() throws Exception {
        ValidationResult subValidationResult = new ValidationResult();
        subValidationResult.register(new ValidationRegistration("b1", "first", ValidationLevel.Warning));
        subValidationResult.register(new ValidationRegistration("b2", "first", ValidationLevel.Warning));

        validationResult.register(new ValidationRegistration("a", "first", ValidationLevel.Warning));
        validationResult.register(subValidationResult);
        validationResult.register(new ValidationRegistration("c", "first", ValidationLevel.Warning));

        Optional<ValidationRegistration> actual = validationResult
                .stream()
                .filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel()))
                .findFirst();
        assertThat(actual.isPresent(), is(false));
    }
}
