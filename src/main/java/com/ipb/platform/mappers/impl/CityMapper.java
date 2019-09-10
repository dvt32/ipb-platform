package com.ipb.platform.mappers.impl;

import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.LandmarkResponseDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ipb.platform.dto.responses.CityResponseDTO;
import com.ipb.platform.persistance.entities.CityEntity;
import com.ipb.platform.persistance.entities.ObjectEntity;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;


@Service("cityMapper")
@NoArgsConstructor
public class CityMapper extends IPBObjectMapperImpl {

	@Autowired
	private LandmarkMapper landmarkMapper;

	@Autowired
	private EventMapper eventMapper;

	@Override
	public ObjectEntity toEntity(ObjectRequestDTO obj) {
		CityEntity entity = new CityEntity();
		return super.setEntityFields(entity, obj);
	}

	@Override
	public ObjectResponseDTO toDTO(ObjectEntity objEntity) {
		CityResponseDTO objResponse = new CityResponseDTO();
		CityEntity cityEntity = this.getCityEntityOrThrow(objEntity);

		if (cityEntity.getLandmarks() != null) {
			List<ObjectResponseDTO> landmarksDTO = cityEntity.getLandmarks()
					.stream()
					.map(landmarkEntity -> this.landmarkMapper.toDTO(landmarkEntity))
					.collect(Collectors.toList());
			objResponse.setLandmarks(landmarksDTO);
		}

		if (cityEntity.getEvents() != null) {
			List<ObjectResponseDTO> eventsDTO = cityEntity.getEvents()
					.stream()
					.map(landmarkEntity -> this.eventMapper.toDTO(landmarkEntity))
					.collect(Collectors.toList());
			objResponse.setEvents(eventsDTO);
		}

		return super.setDTOFields(objResponse, cityEntity);
	}

	private CityEntity getCityEntityOrThrow(ObjectEntity obj) {
		if (!(obj instanceof CityEntity)) {
			throw new IllegalArgumentException("City entity is not correct");
		}
		return ((CityEntity) obj);
	}
}
