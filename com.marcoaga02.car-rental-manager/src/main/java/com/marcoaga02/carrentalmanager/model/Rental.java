package com.marcoaga02.carrentalmanager.model;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rentals")
public class Rental {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "car_id", nullable = false)
	private Car car;

	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@Column(nullable = false)
	private LocalDate startDate;

	@Column(nullable = false)
	private Integer days;

	public Rental(Car car, Customer customer, LocalDate startDate, Integer days) {
		this.car = car;
		this.customer = customer;
		this.startDate = startDate;
		this.days = days;
	}

	// Protected constructor required by JPA
	protected Rental() {
	}

	// Getters
	public Long getId() {
		return id;
	}

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
		return Objects.hash(car, customer, days, id, startDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rental other = (Rental) obj;
		return Objects.equals(car, other.car) && Objects.equals(customer, other.customer)
				&& Objects.equals(days, other.days) && Objects.equals(id, other.id)
				&& Objects.equals(startDate, other.startDate);
	}

	@Override
	public String toString() {
		return "Rental [id=" + id + ", car=" + car + ", customer=" + customer + ", startDate=" + startDate + ", days="
				+ days + "]";
	}

}
