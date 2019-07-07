package com.ipb.platform.persistence.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "cities")
@PrimaryKeyJoinColumn(name = "id")
public class CityEntity extends ObjectEntity{

	@OneToMany(mappedBy = "city")
    private List<LandmarkEntity> landmarks;
	
	@OneToMany(mappedBy = "city")
    private List<EventEntity> events;

}
