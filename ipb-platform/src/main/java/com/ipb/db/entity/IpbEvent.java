package com.ipb.db.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * This class represents 
 * an "event"
 * in the database.
 * 
 * It is converted to a table in the database via Hibernate.
 * The fields in the class become the table's attributes. 
 * Certain restrictions on the attributes' length/value are set via JPA annotations
 * and are then applied by Hibernate when creating the tables.
 * 
 * @author Dimitar Trifonov (dvt32)
 */
@Entity
@Table(name = "Events")
public class IpbEvent {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	private Long cityId;
	
	@NotNull
	private Long startDateInMilliseconds;
	
	@NotNull
	private Long endDateInMilliseconds;
	
	/*
	 * Constructors
	 */
	public IpbEvent() {}

	/*
	 * Getters & setters
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	public Long getStartDateInMilliseconds() {
		return startDateInMilliseconds;
	}

	public void setStartDateInMilliseconds(Long startDateInMilliseconds) {
		this.startDateInMilliseconds = startDateInMilliseconds;
	}

	public Long getEndDateInMilliseconds() {
		return endDateInMilliseconds;
	}

	public void setEndDateInMilliseconds(Long endDateInMilliseconds) {
		this.endDateInMilliseconds = endDateInMilliseconds;
	}

}