package com.ipb.platform.mappers.impl;

import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ipb.platform.dto.responses.CityResponseDTO;
import com.ipb.platform.persistance.entities.CityEntity;
import com.ipb.platform.persistance.entities.ObjectEntity;
import lombok.NoArgsConstructor;


@Service("cityMapper")
@NoArgsConstructor
public class CityMapper extends IPBObjectMapperImpl {

	@Override
	public ObjectEntity toEntity(ObjectRequestDTO obj) {
		CityEntity entity = new CityEntity();
		return super.setEntityFields(entity, obj);
	}

	@Override
	public ObjectResponseDTO toDTO(ObjectEntity objEntity) {
		CityResponseDTO objResponse = new CityResponseDTO();
		return super.setDTOFields(objResponse, objEntity);
	}
}
