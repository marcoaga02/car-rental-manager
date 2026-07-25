package com.marcoaga02.carrentalmanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {

	@Column(nullable = false, unique = true)
	private String taxIdCode;

	@Column(nullable = false)
	private String firstname;

	@Column(nullable = false)
	private String lastname;

	@Column(nullable = false)
	private Boolean deleted = false;

	public Customer(String taxIdCode, String firstname, String lastname) {
		super();
		this.taxIdCode = taxIdCode;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	// Protected constructor required by JPA
	protected Customer() {
	}

	// Getters
	public String getTaxIdCode() {
		return taxIdCode;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	// Setters
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "Customer [taxIdCode=" + taxIdCode + ", firstname=" + firstname + ", lastname=" + lastname + ", deleted="
				+ deleted + "]";
	}

}
