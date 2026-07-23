package com.marcoaga02.carrentalmanager.model;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cars")
public class Car {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String carPlate;
	
	private String brand;
	
	private String model;
	
	private BigDecimal dailyRate;
	
	public Car(String carPlate, String brand, String model, BigDecimal dailyRate) {
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
		Car other = (Car) obj;
		return Objects.equals(brand, other.brand) && Objects.equals(carPlate, other.carPlate)
				&& Objects.equals(dailyRate, other.dailyRate) && Objects.equals(id, other.id)
				&& Objects.equals(model, other.model);
	}

	@Override
	public String toString() {
		return "Car [id=" + id + ", carPlate=" + carPlate + ", brand=" + brand + ", model=" + model + ", dailyRate="
				+ dailyRate + "]";
	}

}
