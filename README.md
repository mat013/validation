# validation

A framework to write validation in a fluent approach.

The concept is that a validation context for each item that needs to be validation and 
then one can specify constraint which should be checked against the actual object being validated. 

The following example shows how an order is checked against null. If the actual order is null then this is registered
in the validation result as a failure with validation code MISSING for the context order". The order has a customer attribute, 
which is then validated whether it is null or not. if the customer is null it is registered as failure with validation
code MISSING for context "customer":

        ValidationResult result = new ValidationContext<Order>("order", order)
            .failWhenMissing()
            .evaluate("customer", Order::getCustomer, 
                customer -> customer
                    .failWhenMissing())
                    .result();

One can then check the validation result and check if it has any validation registration
		ValidationRegistration actual = ... 		
        actual.getContext(); 		// customer
        actual.getContextPath();	// order.customer
        actual.getLocation();		// order.customer
        actual.getValidationCode();	// ValidationResultProvider.MISSING

Following is a more complex example showing validation of an order, which should have a customer, 
with a customer id. The customer id should not be longer than 30 characters,
there should be at least one order line:

        ValidationResult actual = new ValidationContext<Order>("order", order)
                .failWhenMissingAs()
                .validate("customer", Order::getCustomer, 
                    $customer -> $customer
                        .failWhenMissing()
                        .validateString("customerid", Person::getCustomerid, 
                                $customerid -> $customerid
                                    .failWhenMissing()
                                    .failWhenLongerThan(30)
                                    .result())
                        .result())
                .validateCollection("orderlines", Order::getOrderLine,
                        $orderLines -> $orderLines
                            .failWhenMissing()
                            .failWhenEmpty()
                            .result())
                .result();
				
Please see the test for more examples.

