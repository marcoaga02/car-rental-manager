package com.marcoaga02.carrentalmanager.viewmodel;

import java.util.Objects;

public class RentalCreationRequest {

	private Long carId;

	private Long customerId;

	private Integer days;

	public RentalCreationRequest(Long carId, Long customerId, Integer days) {
		this.carId = carId;
		this.customerId = customerId;
		this.days = days;
	}

	// Getters
	public Long getCarId() {
		return carId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public Integer getDays() {
		return days;
	}

	@Override
	public int hashCode() {
		return Objects.hash(carId, customerId, days);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RentalCreationRequest other = (RentalCreationRequest) obj;
		return Objects.equals(carId, other.carId) && Objects.equals(customerId, other.customerId)
				&& Objects.equals(days, other.days);
	}

	@Override
	public String toString() {
		return "RentalCreationRequest [carId=" + carId + ", customerId=" + customerId + ", days=" + days + "]";
	}

}
