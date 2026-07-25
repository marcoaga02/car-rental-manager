package com.marcoaga02.carrentalmanager.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String taxIdCode;

	@Column(nullable = false)
	private String firstname;

	@Column(nullable = false)
	private String lastname;

	@Column(nullable = false)
	private Boolean deleted = false;

	public Customer(String taxIdCode, String firstname, String lastname) {
		this.taxIdCode = taxIdCode;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	// Protected constructor required by JPA
	protected Customer() {
	}

	// Getters
	public Long getId() {
		return id;
	}

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
		return Objects.hash(deleted, firstname, id, lastname, taxIdCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Customer other = (Customer) obj;
		return Objects.equals(deleted, other.deleted) && Objects.equals(firstname, other.firstname)
				&& Objects.equals(id, other.id) && Objects.equals(lastname, other.lastname)
				&& Objects.equals(taxIdCode, other.taxIdCode);
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", taxIdCode=" + taxIdCode + ", firstname=" + firstname + ", lastname=" + lastname
				+ ", deleted=" + deleted + "]";
	}

}
