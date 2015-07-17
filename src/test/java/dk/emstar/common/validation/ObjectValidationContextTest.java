package dk.emstar.common.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import dk.emstar.common.validation.domain.Address;
import dk.emstar.common.validation.domain.Order;
import dk.emstar.common.validation.domain.OrderLine;
import dk.emstar.common.validation.domain.Person;

public class ObjectValidationContextTest {

    @Test
    public void result_NoValidation_NoFailure() throws Exception {
        Order order = new Order();

        ValidationResult actual = new ObjectValidationContext<Order>("order", order).result();

        assertThat(actual.hasFailure()).isFalse();
        assertThat(actual.getContext()).isEqualTo("order");
        assertThat(actual.getCompletePath()).isEqualTo("order");
        assertThat(actual.getLocation()).isEqualTo("order");
    }

    @Test
    public void result_Null_HasMissingOrder() throws Exception {
        ValidationResult result = new ObjectValidationContext<Order>("order", null)
            .failWhenMissing()
            .result();

        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("order");
        assertThat(actual.getContextPath()).isEqualTo("order");
        assertThat(actual.getLocation()).isEqualTo("order");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISSING);
    }

    @Test
    public void asOptional_Null_WillNotFail() throws Exception {
        ValidationResult actual = new ObjectValidationContext<Order>("order", null)
            .asOptional()
            .evaluate("customer", Order::getCustomer, customer -> customer)
            .result();

        assertThat(actual.hasFailure()).isFalse();
    }

    @Test
    public void validate_NoOrder_RegisterWarningItemIsNotMarkedAsOptional() throws Exception {
        ValidationResult result = new ObjectValidationContext<Order>("order", null)
            .evaluate("customer", Order::getCustomer,
                    customer -> customer)
            .result();

        assertThat(result.hasWarning()).isTrue();
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("order");
        assertThat(actual.getContextPath()).isEqualTo("order");
        assertThat(actual.getLocation()).isEqualTo("order");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.NOT_MARKED_AS_OPTIONAL);
    }

    @Test
    public void validate_OrderNoCustomer_RegisterAsMissingCustomer() throws Exception {
        Order order = new Order();

        ValidationResult result = new ObjectValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer, 
                customer -> customer
                    .failWhenMissing())
                    .result();

        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("customer");
        assertThat(actual.getContextPath()).isEqualTo("order.customer");
        assertThat(actual.getLocation()).isEqualTo("order.customer");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISSING);
    }

    @Test
    public void validate_CustomerNoFirstName_RegisterAsMissingFirstName() throws Exception {
        Order order = new Order();
        Person person = new Person();
        order.setCustomer(person);

        ValidationResult result = new ObjectValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                    customer -> customer
                        .failWhenMissing()
                        .validateString("firstname", Person::getFirstname,
                            firstname -> firstname
                                .failWhenMissing()))
            .result();

        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("firstname");
        assertThat(actual.getContextPath()).isEqualTo("order.customer.firstname");
        assertThat(actual.getLocation()).isEqualTo("order.customer.firstname");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISSING);
    }

    @Test
    public void validate_OrderLinesMissing_RegisterAsOrderLineMissing() throws Exception {
        Order order = new Order();
        Person person = new Person();
        person.setFirstname("abc");
        order.setCustomer(person);

        ValidationResult result = new ObjectValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                customer -> customer
                    .failWhenMissing()
                    .validateString("firstname", Person::getFirstname,
                        firstname -> firstname
                            .failWhenMissing()))
            .evaluate("orderlines", Order::getOrderLine, 
                orderLines -> orderLines
                    .failWhenMissing())
            .result();

        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("orderlines");
        assertThat(actual.getContextPath()).isEqualTo("order.orderlines");
        assertThat(actual.getLocation()).isEqualTo("order.orderlines");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISSING);
    }

    @Test
    public void validate_NoOrderLines_RegisterAsIsEmpty() throws Exception {
        Person person = new Person();
        person.setFirstname("abc");

        Order order = new Order();
        order.setCustomer(person);
        order.setOrderLine(Lists.newArrayList());

        ValidationResult result = new ObjectValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                customer -> customer
                    .failWhenMissing()
                    .validateString("firstname", Person::getFirstname,
                        firstname -> firstname
                            .failWhenMissing()))
            .evaluateCollection("orderlines", 
                Order::getOrderLine, 
                    orderLines -> orderLines
                        .failWhenMissing()
                        .failWhenEmpty())
            .result();

        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("orderlines");
        assertThat(actual.getContextPath()).isEqualTo("order.orderlines");
        assertThat(actual.getLocation()).isEqualTo("order.orderlines");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.IS_EMPTY);
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

        ValidationResult result = new ObjectValidationContext<Order>("order", order)
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

        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().count()).isEqualTo(1l);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("orderline");
        assertThat(actual.getContextPath()).isEqualTo("order.orderlines.orderline");
        assertThat(actual.getLocation()).isEqualTo("order.orderlines[0]");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISSING);
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

        ValidationResult result = new ObjectValidationContext<Order>("order", order)
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

        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().count()).isEqualTo(1l);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("itemCode");
        assertThat(actual.getContextPath()).isEqualTo("order.orderlines.orderline.itemCode");
        assertThat(actual.getLocation()).isEqualTo("order.orderlines[0].itemCode");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISSING);
    }

    @Test
    public void validate_NoBillingAddress_NoFailure() throws Exception {
        Order order = new Order();
        Person person = new Person();
        order.setCustomer(person);

        ValidationResult result = new ObjectValidationContext<Order>("order", order)
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

        assertThat(result.hasFailure()).isFalse();
    }

    @Test
    public void validate_AdressLine1MissingInConditionalShippingAddress_RegisterAsMissingAddressLine1() throws Exception {
        Order order = new Order();
        Person person = new Person();
        person.setBillingAddress(new Address());
        order.setCustomer(person);

        ValidationResult result = new ObjectValidationContext<Order>("order", order)
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

        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("addressline1");
        assertThat(actual.getContextPath()).isEqualTo("order.customer.billingAddress.addressline1");
        assertThat(actual.getLocation()).isEqualTo("order.customer.billingAddress.addressline1");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISSING);
    }

    @Test
    public void validate_AdressLine1IsLongerThan10_RegisterAsTooLong() throws Exception {
        Order order = new Order();
        Person person = new Person();
        Address billingAddress = new Address();
        billingAddress.setAddressline1("Holmbladsgade 133");
        person.setBillingAddress(billingAddress);

        order.setCustomer(person);

        ValidationResult result = new ObjectValidationContext<Order>("order", order)
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

        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("addressline1");
        assertThat(actual.getContextPath()).isEqualTo("order.customer.billingAddress.addressline1");
        assertThat(actual.getLocation()).isEqualTo("order.customer.billingAddress.addressline1");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.TOO_LONG);
    }

    @Test
    public void validate_AdressLine1IsOk_NoFailure() throws Exception {
        Order order = new Order();
        Person person = new Person();
        Address billingAddress = new Address();
        billingAddress.setAddressline1("åvej 3");
        person.setBillingAddress(billingAddress);
        order.setCustomer(person);

        ValidationResult actual = new ObjectValidationContext<Order>("order", order)
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

        assertThat(actual.hasFailure()).isFalse();
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

        ValidationResult actual = new ObjectValidationContext<Order>("order", order)
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

        assertThat(actual.hasFailure()).isFalse();
    }

    @Test
    public void validateString_ConditionalAdressLine1IsOk_Failure() throws Exception {
        Order order = new Order();
        Person person = new Person();
        person.setFirstname("meang");
        Address billingAddress = new Address();
        person.setBillingAddress(billingAddress);
        order.setCustomer(person);

        ValidationResult result = new ObjectValidationContext<Order>("order", order)
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

        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("addressline1");
        assertThat(actual.getContextPath()).isEqualTo("order.customer.billingAddress.addressline1");
        assertThat(actual.getLocation()).isEqualTo("order.customer.billingAddress.addressline1");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISSING);
    }

    @Test
    public void validateString_HasNoFirstName_MissingFirstName() throws Exception {
        Order order = new Order();
        Person person = new Person();
        order.setCustomer(person);

        ValidationResult result = new ObjectValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                $customer -> $customer
                    .validateString("firstname", Person::getFirstname, 5, Required.Mandatory))
            .result();

        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().filter(o -> ValidationLevel.Failure.equals(o.getValidationLevel())).count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("firstname");
        assertThat(actual.getContextPath()).isEqualTo("order.customer.firstname");
        assertThat(actual.getLocation()).isEqualTo("order.customer.firstname");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.MISSING);

    }

    @Test
    public void validateString_HasLongFirstName_TooLongFirstName() throws Exception {
        Order order = new Order();
        Person person = new Person();
        person.setFirstname("funny bunny");
        order.setCustomer(person);

        ValidationResult result = new ObjectValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                $customer -> $customer
                    .validateString("firstname", Person::getFirstname, 5, Required.Mandatory))
            .result();

        assertThat(result.hasFailure()).isTrue();
        assertThat(result.stream().count()).isEqualTo(1);
        ValidationRegistration actual = result.stream().findFirst().get();

        assertThat(actual.getContext()).isEqualTo("firstname");
        assertThat(actual.getContextPath()).isEqualTo("order.customer.firstname");
        assertThat(actual.getLocation()).isEqualTo("order.customer.firstname");
        assertThat(actual.getValidationCode()).isEqualTo(ValidationResultProvider.TOO_LONG);
    }

    @Test
    public void validateString_HasFirstName_NoFailure() throws Exception {
        Order order = new Order();
        Person person = new Person();
        person.setFirstname("funny");
        order.setCustomer(person);

        ValidationResult actual = new ObjectValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer,
                $customer -> $customer
                    .validateString("firstname", Person::getFirstname, 5, Required.Mandatory))
            .result();

        assertThat(actual.hasFailure()).isFalse();

    }
}
