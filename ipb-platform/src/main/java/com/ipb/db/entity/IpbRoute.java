package com.ipb.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * This class represents 
 * a "route"
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
@Table(name = "Routes")
public class IpbRoute {

	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	private Long startDateInMilliseconds;
	
	@NotNull
	private Long endDateInMilliseconds;
	
	@NotNull
	private Long startCityId;
	
	@NotNull
	private Long endCityId;
	
	@NotNull
	@Column(length = 255)
	@Size(max = 255)
	private String name;
	
	@NotNull
	private boolean isVisited;
	
	/*
	 * Constructors
	 */
	public IpbRoute() {}
	
	/*
	 * Getters & setters
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getStartCityId() {
		return startCityId;
	}

	public void setStartCityId(Long startCityId) {
		this.startCityId = startCityId;
	}

	public Long getEndCityId() {
		return endCityId;
	}

	public void setEndCityId(Long endCityId) {
		this.endCityId = endCityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}
	
}