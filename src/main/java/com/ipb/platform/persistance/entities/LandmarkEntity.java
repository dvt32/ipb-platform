package com.ipb.platform.persistance.entities;

import java.util.List;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "landmarks")
public class LandmarkEntity extends ObjectEntity {

//	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@ManyToOne()
	@JoinColumn(name = "city_id", referencedColumnName = "id", nullable = false)
	private CityEntity city;
	
	@JoinColumn(nullable = false)
	private double altitude;
	
	@JoinColumn(name = "in_top_100",  nullable = false)
	private boolean inTop100;

	@OneToMany(mappedBy = "landmark", cascade = CascadeType.ALL)
	private List<DatesNotWorkingEntity> datesNotWorking;

	@JoinColumn(name = "work_time",  nullable = false)
	private String workTime;
}
