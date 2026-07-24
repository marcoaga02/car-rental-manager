package com.marcoaga02.carrentalmanager.viewmodel;

import java.math.BigDecimal;
import java.util.Objects;

public class CarViewModel {
	
	private String carPlate;
	
	private String brand;
	
	private String model;
	
	private BigDecimal dailyRate;
	
	public CarViewModel(String carPlate, String brand, String model, BigDecimal dailyRate) {
		this.carPlate = carPlate;
		this.brand = brand;
		this.model = model;
		this.dailyRate = dailyRate;
	}
	
	// Getters
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
		return Objects.hash(brand, carPlate, dailyRate, model);
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
				&& Objects.equals(dailyRate, other.dailyRate) && Objects.equals(model, other.model);
	}

	@Override
	public String toString() {
		return "CarViewModel [carPlate=" + carPlate + ", brand=" + brand + ", model=" + model + ", dailyRate="
				+ dailyRate + "]";
	}
	
}
