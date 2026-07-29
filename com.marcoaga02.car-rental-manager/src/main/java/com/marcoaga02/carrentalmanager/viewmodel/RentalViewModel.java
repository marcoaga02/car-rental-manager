package com.marcoaga02.carrentalmanager.viewmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

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

	@Override
	public int hashCode() {
		return Objects.hash(carDescription, customerFullname, endDate, id, rentalDays, startDate, totalAmount);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RentalViewModel other = (RentalViewModel) obj;
		return Objects.equals(carDescription, other.carDescription)
				&& Objects.equals(customerFullname, other.customerFullname) && Objects.equals(endDate, other.endDate)
				&& Objects.equals(id, other.id) && Objects.equals(rentalDays, other.rentalDays)
				&& Objects.equals(startDate, other.startDate) && Objects.equals(totalAmount, other.totalAmount);
	}

	@Override
	public String toString() {
		return "RentalViewModel [id=" + id + ", startDate=" + startDate + ", endDate=" + endDate + ", rentalDays="
				+ rentalDays + ", customerFullname=" + customerFullname + ", carDescription=" + carDescription
				+ ", totalAmount=" + totalAmount + "]";
	}

}
