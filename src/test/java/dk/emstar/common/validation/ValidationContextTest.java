package dk.emstar.common.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import dk.emstar.common.validation.domain.Address;
import dk.emstar.common.validation.domain.Order;
import dk.emstar.common.validation.domain.OrderLine;
import dk.emstar.common.validation.domain.Person;

public class ValidationContextTest {

    @Test
    public void result_NoValidation_NoFailure() throws Exception {
        Order order = new Order();

        ValidationResult actual = new ValidationContext<Order>("order", order)
                .result();
        
        assertThat(actual.hasFailure(), is(false));
    }

    @Test
    public void result_Null_HasNull() throws Exception {
        String IS_NULL = "a1";
        
        ValidationResult actual = new ValidationContext<Order>("order", null)
                .failWhenMissingAs(IS_NULL)
                .result();
        
        assertThat(actual.hasFailure(), is(true));
        assertThat(actual.hasValidationCode(IS_NULL), is(true));
    }
    

    @Test
    public void asOptional_Null_WillNotFail() throws Exception {
        ValidationResult actual = new ValidationContext<Order>("order", null)
                .asOptional()
                .validate("customer", Order::getCustomer, o -> o.result())
                .result();
        
        assertThat(actual.hasFailure(), is(false));
    }
    
    @Test
    public void validate_NoOrder_RegisterWarningItemIsNotMarkedAsOptional() throws Exception {
        ValidationResult actual = new ValidationContext<Order>("order", null)
                .validate("customer", Order::getCustomer, o -> o.result())
                .result();
        
        assertThat(actual.hasWarning(), is(true));
        assertThat(actual.hasValidationCode(ValidationContext.NOT_MARKED_AS_OPTIONAL), is(true));
    }
    
    
    @Test
    public void validate_OrderNoCustomer_RegisterAsA2() throws Exception {
        Order order = new Order();

        ValidationResult actual = new ValidationContext<Order>("order", order)
                .failWhenMissingAs("a1")
                .validate("customer", Order::getCustomer, customer -> customer
                        .failWhenMissingAs("a2")
                        .result())
                .result();
        
        assertThat(actual.hasFailure(), is(true));
        assertThat(actual.hasValidationCode("a2"), is(true));
    }
    
    @Test
    public void validate_CustomerNoFirstName_RegisterAsA3() throws Exception {
        Order order = new Order();
        Person person = new Person();
        order.setCustomer(person);

        ValidationResult actual = new ValidationContext<Order>("order", order)
                .failWhenMissingAs("a1")
                .validate("customer", Order::getCustomer, customer -> customer
                        .failWhenMissingAs("a2")
                        .validateString("customernumber", Person::getFirstname, 
                                customernumber -> customernumber
                                    .failWhenMissingAs("a3")
                                    .result())
                        .result())
                .result();
        
        assertThat(actual.hasFailure(), is(true));
        assertThat(actual.hasValidationCode("a3"), is(true));
    }

    @Test
    public void validate_OrderLinesMissing_RegisterAsA4() throws Exception {
        Order order = new Order();
        Person person = new Person();
        person.setFirstname("abc");
        order.setCustomer(person);

        ValidationResult actual = new ValidationContext<Order>("order", order)
                .failWhenMissingAs("a1")
                .validate("customer", Order::getCustomer, customer -> customer
                        .failWhenMissingAs("a2")
                        .validateString("customernumber", Person::getFirstname, 
                                customernumber -> customernumber
                                    .failWhenMissingAs("a3")
                                    .result())
                        .result())
                .validate("orderlines", Order::getOrderLine,
                        orderLines -> orderLines
                            .failWhenMissingAs("a4")
                        .result())
                .result();
        
        assertThat(actual.hasFailure(), is(true));
        assertThat(actual.hasValidationCode("a4"), is(true));
    }
    

    @Test
    public void validate_NoOrderLines_RegisterAsA5() throws Exception {
        Person person = new Person();
        person.setFirstname("abc");
        
        Order order = new Order();
        order.setCustomer(person);
        order.setOrderLine(Lists.newArrayList());

        ValidationResult actual = new ValidationContext<Order>("order", order)
                .failWhenMissingAs("a1")
                .validate("customer", Order::getCustomer, customer -> customer
                        .failWhenMissingAs("a2")
                        .validateString("customernumber", Person::getFirstname, 
                                customernumber -> customernumber
                                    .failWhenMissingAs("a3")
                                    .result())
                        .result())
                .validateCollection("orderlines", Order::getOrderLine,
                        orderLines -> orderLines
                            .failWhenMissingAs("a4")
                            .failWhenEmpty("a5")
                            .result())
                .result();
        
        assertThat(actual.hasFailure(), is(true));
        assertThat(actual.hasValidationCode("a5"), is(true));
    }
    
    @Test
    public void validate_NullOrderLineInOrderlines_RegisterAsA6() throws Exception {
        Order order = new Order();
        Person person = new Person();
        person.setFirstname("abc");
        order.setCustomer(person);
        
        List<OrderLine> orderLines = Lists.newArrayList();
        orderLines.add(null);
        order.setOrderLine(orderLines);

        ValidationResult actual = new ValidationContext<Order>("order", order)
                .failWhenMissingAs("a1")
                .validate("customer", Order::getCustomer, customer -> customer
                        .failWhenMissingAs("a2")
                        .validateString("firstname", Person::getFirstname, 
                                firstname -> firstname
                                    .failWhenMissingAs("a3")
                                    .result())
                        .result())
                .validateCollection("orderlines", Order::getOrderLine,
                        $orderLines -> $orderLines
                            .failWhenMissingAs("a4")
                            .failWhenEmpty("a5")
                            .validateEachItem(orderLine -> orderLine
                                .failWhenMissingAs("a6")
                                .result())
                            .result())
                .result();
        
        assertThat(actual.hasFailure(), is(true));
        assertThat(actual.hasValidationCode("a6"), is(true));
    }
    
    
    @Test
    public void validate_NoItemCodeOnOrderLine_RegisterAsA7() throws Exception {
        Order order = new Order();
        Person person = new Person();
        order.setCustomer(person);
        List<OrderLine> orderLines = Lists.newArrayList();
        OrderLine orderLine = new OrderLine();
        orderLines.add(orderLine);
        order.setOrderLine(orderLines);

        ValidationResult actual = new ValidationContext<Order>("order", order)
                .failWhenMissingAs("a1")
                .validate("customer", Order::getCustomer, customer -> customer
                        .failWhenMissingAs("a2")
                        .validateString("customernumber", Person::getFirstname, 
                                customernumber -> customernumber
                                    .failWhenMissingAs("a3")
                                    .result())
                        .result())
                .validateCollection("orderlines", Order::getOrderLine,
                        $orderLines -> $orderLines
                            .failWhenMissingAs("a4")
                            .failWhenEmpty("a5")
                            .validateEachItem($orderLine -> $orderLine.failWhenMissingAs("a6")
                                    .validateString("itemCode", OrderLine::getItemCode, 
                                            itemCode -> itemCode.failWhenMissingAs("a7")
                                                .result())
                                .result())
                            .result())
                .result();
        
        assertThat(actual.hasFailure(), is(true));
        assertThat(actual.hasValidationCode("a7"), is(true));
    }
    
    
    @Test
    public void validate_NoBillingAddress_NoFailure() throws Exception {
        Order order = new Order();
        Person person = new Person();
        order.setCustomer(person);

        ValidationResult actual = new ValidationContext<Order>("order", order)
                .failWhenMissingAs("a1")
                .validate("customer", Order::getCustomer, customer -> customer
                        .failWhenMissingAs("a2")
                        .validate("billingAddress", Person::getBillingAddress, 
                                billingAddress -> billingAddress
                                    .asOptional()
                                    .validateString("addressline1", Address::getAddressline1,
                                            addressline1 -> addressline1
                                                .failWhenMissingAs("a7")
                                                .result())
                                    .result())
                        .result())
                .result();
        
        assertThat(actual.hasFailure(), is(false));
    }

    @Test
    public void validate_AdressLine1MissingInConditionalShippingAddress_RegisterAsA7() throws Exception {
        Order order = new Order();
        Person person = new Person();
        person.setBillingAddress(new Address());
        order.setCustomer(person);

        ValidationResult actual = new ValidationContext<Order>("order", order)
                .failWhenMissingAs("a1")
                .validate("customer", Order::getCustomer, customer -> customer
                        .failWhenMissingAs("a2")
                        .validate("billingAddress", Person::getBillingAddress, 
                                billingAddress -> billingAddress
                                    .asOptional()
                                    .validateString("addressline1", Address::getAddressline1,
                                            addressline1 -> addressline1
                                                .failWhenMissingAs("a7")
                                                .result())
                                    .result())
                        .result())
                .result();
        
        assertThat(actual.hasFailure(), is(true));
        assertThat(actual.hasValidationCode("a7"), is(true));
    }

    @Test
    public void validate_AdressLine1IsLongerThan10_RegisterAsA8() throws Exception {
        Order order = new Order();
        Person person = new Person();
        Address billingAddress = new Address();
        billingAddress.setAddressline1("Holmbladsgade 133");
        person.setBillingAddress(billingAddress);
        
        order.setCustomer(person);

        ValidationResult actual = new ValidationContext<Order>("order", order)
                .failWhenMissingAs("a1")
                .validate("customer", Order::getCustomer, customer -> customer
                        .failWhenMissingAs("a2")
                        .validate("billingAddress", Person::getBillingAddress, 
                                $billingAddress -> $billingAddress
                                    .asOptional()
                                    .validateString("addressline1", Address::getAddressline1,
                                            addressline1 -> addressline1
                                                .failWhenMissingAs("a7")
                                                .failWhenLongerThan(10, "a8")
                                                .result())
                                    .result())
                        .result())
                .result();
        
        assertThat(actual.hasFailure(), is(true));
        assertThat(actual.hasValidationCode("a8"), is(true));
    }

    @Test
    public void validate_AdressLine1IsOk_NoFailure() throws Exception {
        Order order = new Order();
        Person person = new Person();
        Address billingAddress = new Address();
        billingAddress.setAddressline1("åvej 3");
        person.setBillingAddress(billingAddress);
        order.setCustomer(person);

        ValidationResult actual = new ValidationContext<Order>("order", order)
                .failWhenMissingAs("a1")
                .validate("customer", Order::getCustomer, customer -> customer
                        .failWhenMissingAs("a2")
                        .validate("billingAddress", Person::getBillingAddress, 
                                $billingAddress -> $billingAddress
                                    .asOptional()
                                    .validateString("addressline1", Address::getAddressline1,
                                            addressline1 -> addressline1
                                                .failWhenMissingAs("a7")
                                                .failWhenLongerThan(30, "a8")
                                                .result())
                                    .result())
                        .result())
                .result();
        
        assertThat(actual.hasFailure(), is(false));
    }
    

    @Test
    public void validat1e_AdressLine1IsOk_NoFailure() throws Exception {
        Order order = new Order();
        Person person = new Person();
        person.setFirstname("me");
        Address billingAddress = new Address();
        billingAddress.setAddressline1("åvej 3");
        person.setBillingAddress(billingAddress);
        order.setCustomer(person);

        ValidationResult actual = new ValidationContext<Order>("order", order)
                .failWhenMissingAs("a1")
                .validate("customer", Order::getCustomer, customer -> customer
                        .failWhenMissingAs("a2")
                        .validate("billingAddress", Person::getBillingAddress, 
                                $billingAddress -> $billingAddress
                                    .asOptional()
                                    .validate("addressline1", Address::getAddressline1,
                                            addressline1 -> {
                                                if(!customer.isCurrentToBeCheckedItemNull()) {
                                                    if(customer.getCurrentItemToBeChecked().getFirstname().length() > 3) {
                                                        System.out.println("a");
                                                    }
                                                }
                                                
                                                return addressline1.result();
                                            })
                                    .result())
                        .result())
                .result();
        assertThat(actual.hasFailure(), is(false));
        
    }
    
}
