package com.marcoaga02.carrentalmanager.viewmodel;

import java.util.Objects;

public class CustomerViewModel {

	private Long id;

	private String taxIdCode;

	private String firstname;

	private String lastname;

	public CustomerViewModel(Long id, String taxIdCode, String firstname, String lastname) {
		this.id = id;
		this.taxIdCode = taxIdCode;
		this.firstname = firstname;
		this.lastname = lastname;
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

	@Override
	public int hashCode() {
		return Objects.hash(firstname, id, lastname, taxIdCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomerViewModel other = (CustomerViewModel) obj;
		return Objects.equals(firstname, other.firstname) && Objects.equals(id, other.id)
				&& Objects.equals(lastname, other.lastname) && Objects.equals(taxIdCode, other.taxIdCode);
	}

	@Override
	public String toString() {
		return "CustomerViewModel [id=" + id + ", taxIdCode=" + taxIdCode + ", firstname=" + firstname + ", lastname="
				+ lastname + "]";
	}

}
