# validation

A framework to write validation in a fluent approach.

The concept is that a validation context is created with the root object and then one can
add constraint which will be checked against the object which is being checked. 

The following example shows how an order is checked if it is null. If it is null then this is registered
in a validation result as a failure with validation code MISSING-ORDER. The order has a customer attribute, 
which is then validated if it is null or not. if the customer is null this is registered as failure with validation
code as MISSING-CUSTOMER:

        ValidationResult actual = new ValidationContext<Order>("order", order)
                .failWhenMissingAs("MISSING-ORDER")
                .validate("customer", Order::getCustomer, 
                    $customer -> $customer
                        .failWhenMissingAs("MISSING-CUSTOMER")
                        .result())
                .result();

One can then check the validation result and check if it has any validation registration
				
        assertThat(actual.hasFailure(), is(true));
        assertThat(actual.hasValidationCode("MISSING-CUSTOMER"), is(true));

Following is a more complex example showing validation of an order, which should have a customer, 
with a customer id. The customer id should not be longer than 30 characters,
there should be at least one order line:

        ValidationResult actual = new ValidationContext<Order>("order", order)
                .failWhenMissingAs("MISSING-ORDER")
                .validate("customer", Order::getCustomer, 
                    $customer -> $customer
                        .failWhenMissingAs("MISSING-CUSTOMER")
                        .validateString("customerid", Person::getCustomerid, 
                                $customerid -> $customerid
                                    .failWhenMissingAs("MISSING-CUSTOMER-ID")
                                    .failWhenLongerThan(30, "CUSTOMER-ID-TOO-LONG")
                                    .result())
                        .result())
                .validateCollection("orderlines", Order::getOrderLine,
                        $orderLines -> $orderLines
                            .failWhenMissingAs("MISSING-ORDERLINES")
                            .failWhenEmpty("ORDERLINES-EMPTY")
                            .result())
                .result();
				
Please see the test for more examples.