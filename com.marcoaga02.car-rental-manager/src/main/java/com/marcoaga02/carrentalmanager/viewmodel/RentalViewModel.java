package com.marcoaga02.carrentalmanager.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RentalViewModel {
	
	private Long id;
	
	private LocalDate startDate;
	
	private LocalDate endDate;
	
	private Integer rentalDays;
	
	private String customerFullname;
	
	private String carDescription;
	
	private BigDecimal totalAmount;

	public RentalViewModel(Long id, LocalDate startDate, LocalDate endDate, Integer rentalDays, String customerFullname,
			String carDescription, BigDecimal totalAmount) {
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
		this.rentalDays = rentalDays;
		this.customerFullname = customerFullname;
		this.carDescription = carDescription;
		this.totalAmount = totalAmount;
	}

	public Long getId() {
		return id;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public Integer getRentalDays() {
		return rentalDays;
	}

	public String getCustomerFullname() {
		return customerFullname;
	}

	public String getCarDescription() {
		return carDescription;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	
}
