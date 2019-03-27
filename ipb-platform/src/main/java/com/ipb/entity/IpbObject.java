package com.ipb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Objects")
public class IpbObject {

	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	private String name;
	
	@Lob
	@Column(length=100000)
	@NotNull
	private byte[] description;
	
	@NotNull
	private double latitude;
	
	@NotNull
	private double longitude;
	
	@NotNull
	private String type;
	
	/*
	 * Constructors
	 */
	public IpbObject() {}

	/*
	 * Getters & setters
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getDescription() {
		return description;
	}

	public void setDescription(byte[] description) {
		this.description = description;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}