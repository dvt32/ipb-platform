package com.ipb.platform.mappers.impl;

import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.CityRequestDTO;
import com.ipb.platform.dto.responses.CityResponseDTO;
import com.ipb.platform.mappers.CityMapper;
import com.ipb.platform.persistence.entities.CityEntity;
import com.ipb.platform.persistence.entities.ObjectType;

import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class CityMapperImpl extends ObjectMapperImpl implements CityMapper{
	
	@Override
	public CityEntity toEntity(CityRequestDTO city) {
		
		//CityEntity entity = (CityEntity) objEntity;
		//CityEntity entity = new CityEntity();
		
		//entity.setDescription(city.getDescription());
	//	entity.setLatitude(city.getLatitude());
		//entity.setLongitude(city.getLongitude());
		//entity.setName(city.getName());
		//entity.setType(ObjectType.valueOf(city.getType()));
		
		CityEntity entity = (CityEntity) super.toEntity(city, ObjectType.CITY); 
		return entity;
	}

	@Override
	public CityResponseDTO toDTO(CityEntity city) {
		/*CityResponseDTO dto = new CityResponseDTO ();
		dto.setId(city.getId());
		dto.setDescription(city.getDescription());
		dto.setLatitude(city.getLatitude());
		dto.setLongitude(city.getLongitude());
		dto.setName(city.getName());
		dto.setType(city.getType().toString());
		*/
		
		CityResponseDTO dto = (CityResponseDTO) super.toDTO(city, ObjectType.CITY);
		return dto;
	}
}
