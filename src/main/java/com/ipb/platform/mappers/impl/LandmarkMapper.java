package com.ipb.platform.mappers.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.DatesNotWorkingResponseDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.mappers.DatesNotWorkingMapping;
import com.ipb.platform.persistance.entities.ObjectEntity;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ipb.platform.dto.requests.LandmarkRequestDTO;
import com.ipb.platform.dto.responses.LandmarkResponseDTO;
import com.ipb.platform.persistance.entities.LandmarkEntity;

@Component("landmarkMapper")
@AllArgsConstructor
public class LandmarkMapper extends IPBObjectMapperImpl {

	@Autowired
	DatesNotWorkingMapping datesNotWorkingMapper;

	@Override
	public ObjectEntity toEntity(ObjectRequestDTO object) {

		LandmarkEntity entity = new LandmarkEntity();
		super.setEntityFields(entity, object);
		LandmarkRequestDTO landmark = this.getLandmarkRequestDTOOrThrow(object);

		entity.setAltitude(landmark.getAltitude());
		entity.setInTop100(landmark.isInTop100());
		entity.setWorkTime(landmark.getWorkTime());
		return entity;
	}

	@Override
	public ObjectResponseDTO toDTO(ObjectEntity objectEntity) {
		LandmarkResponseDTO landmarkDTO = new LandmarkResponseDTO();
		super.setDTOFields(landmarkDTO, objectEntity);
		LandmarkEntity landmarkEntity = this.getLandmarkEntityOrThrow(objectEntity);

		landmarkDTO.setAltitude(landmarkEntity.getAltitude());
		landmarkDTO.setCityId(landmarkEntity.getCity().getId());
		landmarkDTO.setInTop100(landmarkEntity.isInTop100());
		landmarkDTO.setWorkTime(landmarkEntity.getWorkTime());
		
		if (landmarkEntity.getDatesNotWorking() != null) {
			List<DatesNotWorkingResponseDTO> dates = landmarkEntity.getDatesNotWorking()
					.stream()
					.map(datesEntity -> this.datesNotWorkingMapper.toDTO(datesEntity))
					.collect(Collectors.toList());
			landmarkDTO.setDatesNotWorking(dates);
		}
		return landmarkDTO;
	}

	private LandmarkRequestDTO getLandmarkRequestDTOOrThrow(ObjectRequestDTO obj) {
		if (!(obj instanceof LandmarkRequestDTO)) {
			throw new IllegalArgumentException("Landmark object is not correct");
		}
		return ((LandmarkRequestDTO) obj);
	}

	private LandmarkEntity getLandmarkEntityOrThrow(ObjectEntity obj) {
		if (!(obj instanceof LandmarkEntity)) {
			throw new IllegalArgumentException("Landmark entity is not correct");
		}
		return ((LandmarkEntity) obj);
	}
}
