package com.marcoaga02.carrentalmanager.viewmodel;

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

}
