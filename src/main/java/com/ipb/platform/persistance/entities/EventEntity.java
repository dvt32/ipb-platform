package com.ipb.platform.persistance.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "events")
public class EventEntity extends ObjectEntity {
	
	@OneToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "city_id", referencedColumnName = "id", nullable = false)
	private CityEntity city;
	
	@JoinColumn(name = "start_date",  nullable = false)
	private long startDate;
	
	@JoinColumn(name = "end_date",  nullable = false)
	private long endDate;

	@JoinColumn(name = "work_time",  nullable = false)
	private String workTime;
}