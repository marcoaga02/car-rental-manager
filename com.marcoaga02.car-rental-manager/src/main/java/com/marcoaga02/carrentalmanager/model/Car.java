package com.marcoaga02.carrentalmanager.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "cars")
public class Car extends BaseEntity {

	@Column(nullable = false)
	private String carPlate;

	@Column(nullable = false)
	private String brand;

	@Column(nullable = false)
	private String model;

	@Column(nullable = false)
	private BigDecimal dailyRate;

	@Column(nullable = false)
	private Boolean deleted = false;

	public Car(String carPlate, String brand, String model, BigDecimal dailyRate) {
		super();
		this.carPlate = carPlate;
		this.brand = brand;
		this.model = model;
		this.dailyRate = dailyRate;
	}

	// Protected constructor required by JPA
	protected Car() {
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
		return "Car [carPlate=" + carPlate + ", brand=" + brand + ", model=" + model
				+ ", dailyRate=" + dailyRate + ", deleted=" + deleted + "]";
	}

}
