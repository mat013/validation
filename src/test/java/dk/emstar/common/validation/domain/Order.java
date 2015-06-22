package dk.emstar.common.validation.domain;

import java.util.List;

public class Order {
	
	private Person customer;
	private List<OrderLine> orderLine;
	private Address shippingAddress;

	public Person getCustomer() {
		return customer;
	}
	
	public void setCustomer(Person customer) {
		this.customer = customer;
	}

	public List<OrderLine> getOrderLine() {
		return orderLine;
	}
	
	public void setOrderLine(List<OrderLine> orderLine) {
		this.orderLine = orderLine;
	}
	
	public Address getShippingAddress() {
		return shippingAddress;
	}
	
	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}
}
