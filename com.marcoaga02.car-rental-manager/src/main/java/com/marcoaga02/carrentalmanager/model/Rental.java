package com.marcoaga02.carrentalmanager.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rentals")
public class Rental extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "car_id", nullable = false)
	private Car car;

	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(name = "days", nullable = false)
	private Integer days;

	public Rental(Car car, Customer customer, LocalDate startDate, Integer days) {
		super();
		this.car = car;
		this.customer = customer;
		this.startDate = startDate;
		this.days = days;
	}

	// Protected constructor required by JPA
	protected Rental() {
	}

	// Getters
	public Car getCar() {
		return car;
	}

	public Customer getCustomer() {
		return customer;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public Integer getDays() {
		return days;
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
		return "Rental [car=" + car + ", customer=" + customer + ", startDate=" + startDate + ", days=" + days + "]";
	}

}
