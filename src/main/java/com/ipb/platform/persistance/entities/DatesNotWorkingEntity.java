package com.ipb.platform.persistance.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "dates_not_working")
public class DatesNotWorkingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dates_not_working_seq")
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "landmark_id")
	private LandmarkEntity landmark;

	@JoinColumn(name = "startDate",  nullable = false)
	private Long startDate;

	@JoinColumn(name = "end_date")
	private Long endDate;
}
