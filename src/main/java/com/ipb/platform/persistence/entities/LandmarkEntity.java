package com.ipb.platform.persistence.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "landmarks")
public class LandmarkEntity extends ObjectEntity {
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "city_id", referencedColumnName = "id", nullable = false)
	private CityEntity city;
	
	@JoinColumn(nullable = false)
	private double altitude;
	
	@JoinColumn(name = "in_top_100",  nullable = false)
	private boolean inTop100;
	
	@JoinColumn(name = "dates_not_working",  nullable = false)
	private byte[] datesNotWorking;
}
