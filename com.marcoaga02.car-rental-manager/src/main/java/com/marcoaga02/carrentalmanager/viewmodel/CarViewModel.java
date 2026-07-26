package com.marcoaga02.carrentalmanager.viewmodel;

import java.math.BigDecimal;
import java.util.Objects;

public class CarViewModel {

	private Long id;

	private String carPlate;

	private String brand;

	private String model;

	private BigDecimal dailyRate;

	public CarViewModel(Long id, String carPlate, String brand, String model,
			BigDecimal dailyRate) {
		this.id = id;
		this.carPlate = carPlate;
		this.brand = brand;
		this.model = model;
		this.dailyRate = dailyRate;
	}

	// Getters
	public Long getId() {
		return id;
	}

	public String getCarPlate() {
		return carPlate;
	}

	public String getBrand() {
		return brand;
	}

	public String getModel() {
		return model;
	}

	public BigDecimal getDailyRate() {
		return dailyRate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(brand, carPlate, dailyRate, id, model);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarViewModel other = (CarViewModel) obj;
		return Objects.equals(brand, other.brand) && Objects.equals(carPlate, other.carPlate)
				&& Objects.equals(dailyRate, other.dailyRate) && Objects.equals(id, other.id)
				&& Objects.equals(model, other.model);
	}

	@Override
	public String toString() {
		return "CarViewModel [id=" + id + ", carPlate=" + carPlate + ", brand=" + brand + ", model="
				+ model + ", dailyRate=" + dailyRate + "]";
	}

}
