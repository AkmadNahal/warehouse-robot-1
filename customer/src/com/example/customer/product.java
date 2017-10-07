package com.example.customer;

public class product {
private String name;
private String description;
private String discount;
public product(String name, String desc, String discount) {
	this.name = name;
	this.description = desc;
	this.discount = discount;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getDescription() {
	return description;
}
public void setDescription(String description) {
	this.description = description;
}
public String getDiscount() {
	return discount;
}
public void setDiscount(String discount) {
	this.discount = discount;
}
}
