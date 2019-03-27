package com.ipb.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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