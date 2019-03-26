package com.ipb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Landmarks")
public class IpbLandmark {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	private Long cityId;
	
	@NotNull
	private double altitude;
	
	@NotNull
	private boolean inTop100;
	
	@Lob
	@Column(length=100000)
	@NotNull
	private byte[] datesNotWorking;
	
	/*
	 * Constructors
	 */
	public IpbLandmark() {}

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

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public boolean isInTop100() {
		return inTop100;
	}

	public void setInTop100(boolean inTop100) {
		this.inTop100 = inTop100;
	}

	public byte[] getDatesNotWorking() {
		return datesNotWorking;
	}

	public void setDatesNotWorking(byte[] datesNotWorking) {
		this.datesNotWorking = datesNotWorking;
	}
	
}