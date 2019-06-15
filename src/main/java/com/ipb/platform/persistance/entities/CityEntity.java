package com.ipb.platform.persistance.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
    private List<LandmarkEntity> landmark;
	
	@OneToOne(mappedBy = "city")
    private EventEntity event;
}
