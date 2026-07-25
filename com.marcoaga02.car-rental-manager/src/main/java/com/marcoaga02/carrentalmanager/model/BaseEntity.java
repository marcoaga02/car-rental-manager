package com.marcoaga02.carrentalmanager.model;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private UUID uuid = UUID.randomUUID();

	protected BaseEntity() {
	}

	// Getters
	public Long getId() {
		return id;
	}

	public UUID getUuid() {
		return uuid;
	}

	// package-private for tests only
	void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public int hashCode() {
		return Objects.hash(uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof BaseEntity))
			return false;
		BaseEntity other = (BaseEntity) obj;
		return Objects.equals(uuid, other.getUuid());
	}

}