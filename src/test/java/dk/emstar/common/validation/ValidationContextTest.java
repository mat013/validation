package dk.emstar.common.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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

        ValidationResult actual = new ValidationContext<Order>("order", order).result();

        assertThat(actual.hasFailure(), is(false));
    }

    @Test
    public void result_Null_HasMissingOrder() throws Exception {
        ValidationResult result = new ValidationContext<Order>("order", null)
            .failWhenMissing()
            .result();

        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("order")));
        assertThat(actual.getContextPath(), is(equalTo("order")));
        assertThat(actual.getLocation(), is(equalTo("order")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.MISSING));
    }

    @Test
    public void asOptional_Null_WillNotFail() throws Exception {
        ValidationResult actual = new ValidationContext<Order>("order", null)
            .asOptional()
            .evaluate("customer", Order::getCustomer, o -> o)
            .result();

        assertThat(actual.hasFailure(), is(false));
    }

    @Test
    public void validate_NoOrder_RegisterWarningItemIsNotMarkedAsOptional() throws Exception {
        ValidationResult result = new ValidationContext<Order>("order", null)
            .evaluate("customer", Order::getCustomer,
                    o -> o)
            .result();

        assertThat(result.hasWarning(), is(true));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("order")));
        assertThat(actual.getContextPath(), is(equalTo("order")));
        assertThat(actual.getLocation(), is(equalTo("order")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.NOT_MARKED_AS_OPTIONAL));
    }

    @Test
    public void validate_OrderNoCustomer_RegisterAsMissingCustomer() throws Exception {
        Order order = new Order();

        ValidationResult result = new ValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer, 
                customer -> customer
                    .failWhenMissing())
                    .result();

        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("customer")));
        assertThat(actual.getContextPath(), is(equalTo("order.customer")));
        assertThat(actual.getLocation(), is(equalTo("order.customer")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.MISSING));
    }

    @Test
    public void validate_CustomerNoFirstName_RegisterAsMissingFirstName() throws Exception {
        Order order = new Order();
        Person person = new Person();
        order.setCustomer(person);

        ValidationResult result = new ValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                    customer -> customer
                        .failWhenMissing()
                        .validateString("firstname", Person::getFirstname,
                            firstname -> firstname
                                .failWhenMissing()))
            .result();

        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("firstname")));
        assertThat(actual.getContextPath(), is(equalTo("order.customer.firstname")));
        assertThat(actual.getLocation(), is(equalTo("order.customer.firstname")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.MISSING));
    }

    @Test
    public void validate_OrderLinesMissing_RegisterAsOrderLineMissing() throws Exception {
        Order order = new Order();
        Person person = new Person();
        person.setFirstname("abc");
        order.setCustomer(person);

        ValidationResult result = new ValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                customer -> customer
                    .failWhenMissing()
                    .validateString("customernumber", Person::getFirstname,
                        customernumber -> customernumber
                            .failWhenMissing()))
            .evaluate("orderlines", Order::getOrderLine, 
                orderLines -> orderLines
                    .failWhenMissing())
            .result();

        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("orderlines")));
        assertThat(actual.getContextPath(), is(equalTo("order.orderlines")));
        assertThat(actual.getLocation(), is(equalTo("order.orderlines")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.MISSING));
    }

    @Test
    public void validate_NoOrderLines_RegisterAsIsEmpty() throws Exception {
        Person person = new Person();
        person.setFirstname("abc");

        Order order = new Order();
        order.setCustomer(person);
        order.setOrderLine(Lists.newArrayList());

        ValidationResult result = new ValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                customer -> customer
                    .failWhenMissing()
                    .validateString("customernumber", Person::getFirstname,
                        customernumber -> customernumber
                            .failWhenMissing()))
            .evaluateCollection("orderlines", 
                Order::getOrderLine, 
                    orderLines -> orderLines
                        .failWhenMissing()
                        .failWhenEmpty())
            .result();

        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("orderlines")));
        assertThat(actual.getContextPath(), is(equalTo("order.orderlines")));
        assertThat(actual.getLocation(), is(equalTo("order.orderlines")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.IS_EMPTY));
    }

    @Test
    public void validate_NullOrderLineInOrderlines_RegisterAsMissingOrderline() throws Exception {
        Order order = new Order();
        Person person = new Person();
        person.setFirstname("abc");
        order.setCustomer(person);

        List<OrderLine> orderLines = Lists.newArrayList();
        orderLines.add(null);
        order.setOrderLine(orderLines);

        ValidationResult result = new ValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                customer -> customer
                    .failWhenMissing()
                    .validateString("firstname", Person::getFirstname,
                        firstname -> firstname
                            .failWhenMissing()))
                .evaluateCollection("orderlines", Order::getOrderLine,
                    $orderLines -> $orderLines
                        .failWhenMissing()
                        .failWhenEmpty()
                        .evaluateEachItem("orderline", 
                            orderLine -> orderLine
                                .failWhenMissing()))
            .result();

        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("orderline")));
        assertThat(actual.getContextPath(), is(equalTo("order.orderlines.orderline")));
        assertThat(actual.getLocation(), is(equalTo("order.orderlines[0]")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.MISSING));
    }

    @Test
    public void validate_NoItemCodeOnOrderLine_RegisterAsA7() throws Exception {
        Order order = new Order();
        Person person = new Person();
        person.setFirstname("abc");
        order.setCustomer(person);
        List<OrderLine> orderLines = Lists.newArrayList();
        OrderLine orderLine = new OrderLine();
        orderLines.add(orderLine);
        order.setOrderLine(orderLines);

        ValidationResult result = new ValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                customer -> customer
                    .failWhenMissing()
                    .validateString("firstname", Person::getFirstname,
                        firstname -> firstname
                            .failWhenMissing()))
            .evaluateCollection("orderlines", Order::getOrderLine,
                orderLines1 -> orderLines1
                    .failWhenMissing()
                    .failWhenEmpty()
                    .evaluateEachItem("orderline", 
                        orderLine1 -> orderLine1
                            .failWhenMissing()
                            .validateString("itemCode", OrderLine::getItemCode,
                                itemCode -> itemCode
                                    .failWhenMissing())))
            .result();

        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("itemCode")));
        assertThat(actual.getContextPath(), is(equalTo("order.orderlines.orderline.itemCode")));
        assertThat(actual.getLocation(), is(equalTo("order.orderlines[0].itemCode")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.MISSING));
    }

    @Test
    public void validate_NoBillingAddress_NoFailure() throws Exception {
        Order order = new Order();
        Person person = new Person();
        order.setCustomer(person);

        ValidationResult result = new ValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                customer -> customer
                    .failWhenMissing()
                    .evaluate("billingAddress", Person::getBillingAddress,
                        billingAddress -> billingAddress
                            .asOptional()
                            .validateString("addressline1", Address::getAddressline1,
                                addressline1 -> addressline1
                                    .failWhenMissing())))
            .result();

        assertThat(result.hasFailure(), is(false));
    }

    @Test
    public void validate_AdressLine1MissingInConditionalShippingAddress_RegisterAsMissingAddressLine1() throws Exception {
        Order order = new Order();
        Person person = new Person();
        person.setBillingAddress(new Address());
        order.setCustomer(person);

        ValidationResult result = new ValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                customer -> customer
                    .failWhenMissing()
                        .evaluate("billingAddress", Person::getBillingAddress,
                            billingAddress -> billingAddress
                                .asOptional()
                                .validateString("addressline1", Address::getAddressline1,
                                    addressline1 -> addressline1
                                        .failWhenMissing())))
            .result();

        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("addressline1")));
        assertThat(actual.getContextPath(), is(equalTo("order.customer.billingAddress.addressline1")));
        assertThat(actual.getLocation(), is(equalTo("order.customer.billingAddress.addressline1")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.MISSING));
    }

    @Test
    public void validate_AdressLine1IsLongerThan10_RegisterAsTooLong() throws Exception {
        Order order = new Order();
        Person person = new Person();
        Address billingAddress = new Address();
        billingAddress.setAddressline1("Holmbladsgade 133");
        person.setBillingAddress(billingAddress);

        order.setCustomer(person);

        ValidationResult result = new ValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                customer -> customer
                    .failWhenMissing()
                    .evaluate("billingAddress", Person::getBillingAddress,
                        $billingAddress -> $billingAddress
                            .asOptional()
                            .validateString("addressline1", Address::getAddressline1,
                                addressline1 -> addressline1
                                    .failWhenMissing()
                                    .failWhenLongerThan(10))))
            .result();

        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("addressline1")));
        assertThat(actual.getContextPath(), is(equalTo("order.customer.billingAddress.addressline1")));
        assertThat(actual.getLocation(), is(equalTo("order.customer.billingAddress.addressline1")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.TOO_LONG));
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
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                customer -> customer
                    .failWhenMissing()
                    .evaluate("billingAddress", Person::getBillingAddress,
                        $billingAddress -> $billingAddress
                            .asOptional()
                            .validateString("addressline1", Address::getAddressline1,
                                addressline1 -> addressline1
                                    .failWhenMissing()
                                    .failWhenLongerThan(30))))
            .result();

        assertThat(actual.hasFailure(), is(false));
    }

    @Test
    public void validate_ConditionalAdressLine1IsOk_NoFailure() throws Exception {
        Order order = new Order();
        Person person = new Person();
        person.setFirstname("me");
        Address billingAddress = new Address();
        billingAddress.setAddressline1("åvej 3");
        person.setBillingAddress(billingAddress);
        order.setCustomer(person);

        ValidationResult actual = new ValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                customer -> customer
                    .failWhenMissing()
                    .evaluate("billingAddress", Person::getBillingAddress,
                                $billingAddress -> $billingAddress
                                    .asOptional()
                                    .evaluate("addressline1", Address::getAddressline1, addressline1 -> {
                                        if (!customer.isCurrentToBeCheckedItemNull()) {
                                            if (customer.getCurrentItemToBeChecked().getFirstname().length() > 3) {
                                                System.out.println("a");
                                            }
                                        }

                                        return addressline1;
                                })))
            .result();

        assertThat(actual.hasFailure(), is(false));
    }

    @Test
    public void validateString_ConditionalAdressLine1IsOk_Failure() throws Exception {
        Order order = new Order();
        Person person = new Person();
        person.setFirstname("meang");
        Address billingAddress = new Address();
        person.setBillingAddress(billingAddress);
        order.setCustomer(person);

        ValidationResult result = new ValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                customer -> customer
                    .failWhenMissing()
                    .evaluate("billingAddress", Person::getBillingAddress,
                            $billingAddress -> $billingAddress
                                .asOptional()
                                .evaluate("addressline1", Address::getAddressline1, 
                                    addressline1 -> {
                                        if (!customer.isCurrentToBeCheckedItemNull()) {
                                            if (customer.getCurrentItemToBeChecked().getFirstname().length() > 3) {
                                                addressline1.failWhenMissing();
                                            }
                                        }

                                        return addressline1;
                            })))
            .result();

        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("addressline1")));
        assertThat(actual.getContextPath(), is(equalTo("order.customer.billingAddress.addressline1")));
        assertThat(actual.getLocation(), is(equalTo("order.customer.billingAddress.addressline1")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.MISSING));
    }

    @Test
    public void validateString_HasNoFirstName_MissingFirstName() throws Exception {
        Order order = new Order();
        Person person = new Person();
        order.setCustomer(person);

        ValidationResult result = new ValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                $customer -> $customer
                    .validateString("firstname", Person::getFirstname, 5, Required.Mandatory))
            .result();

        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("firstname")));
        assertThat(actual.getContextPath(), is(equalTo("order.customer.firstname")));
        assertThat(actual.getLocation(), is(equalTo("order.customer.firstname")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.MISSING));

    }

    @Test
    public void validateString_HasLongFirstName_TooLongFirstName() throws Exception {
        Order order = new Order();
        Person person = new Person();
        person.setFirstname("funny bunny");
        order.setCustomer(person);

        ValidationResult result = new ValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                $customer -> $customer
                    .validateString("firstname", Person::getFirstname, 5, Required.Mandatory))
            .result();

        assertThat(result.hasFailure(), is(true));
        assertThat(result.stream().count(), is(1l));
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext(), is(equalTo("firstname")));
        assertThat(actual.getContextPath(), is(equalTo("order.customer.firstname")));
        assertThat(actual.getLocation(), is(equalTo("order.customer.firstname")));
        assertThat(actual.getValidationCode(), is(ValidationResultProvider.TOO_LONG));
    }

    @Test
    public void validateString_HasFirstName_NoFailure() throws Exception {
        Order order = new Order();
        Person person = new Person();
        person.setFirstname("funny");
        order.setCustomer(person);

        ValidationResult actual = new ValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                $customer -> $customer
                    .validateString("firstname", Person::getFirstname, 5, Required.Mandatory))
            .result();

        assertThat(actual.hasFailure(), is(false));

    }
}
