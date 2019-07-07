package com.ipb.platform.persistence.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "routes")
public class RouteEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "route_seq")
	private Long id;

	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
	private UserEntity creator;

	@JoinColumn(name = "start_latitude",  nullable = false)
	private double startLatitude;

	@JoinColumn(name = "start_longitude",  nullable = false)
	private double startLongitude;

	@JoinColumn(name = "end_latitude",  nullable = false)
	private double endLatitude;

	@JoinColumn(name = "end_longitude",  nullable = false)
	private double endLongitude;

	@JoinColumn(name = "start_date",  nullable = false)
	private long startDate;
	
	@JoinColumn(name = "end_date",  nullable = false)
	private long endDate;

	@JoinColumn(name = "name",  nullable = false)
	private String name;

	@JoinColumn(name = "is_visit",  nullable = false)
	private boolean isVisit;

	@ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                CascadeType.PERSIST,
                CascadeType.MERGE,
            })
	@JoinTable(name = "route_objects",
			joinColumns = @JoinColumn(name = "route_id",  referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "object_id",  referencedColumnName = "id"))
	private List<ObjectEntity> objects;
}
