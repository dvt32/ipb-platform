package com.ipb.platform.mappers.impl;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.LandmarkRequestDTO;
import com.ipb.platform.dto.responses.LandmarkResponseDTO;
import com.ipb.platform.mappers.LandmarkMapper;
import com.ipb.platform.persistence.entities.LandmarkEntity;
import com.ipb.platform.persistence.entities.ObjectType;

import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class LandmarkMapperImpl extends ObjectMapperImpl implements LandmarkMapper {

	@Override
	public LandmarkEntity toEntity(LandmarkRequestDTO landmark) {
		LandmarkEntity entity = (LandmarkEntity) super.toEntity(landmark, ObjectType.LANDMARK);

		entity.setAltitude(landmark.getAltitude());
		entity.setInTop100(landmark.isInTop100());
		
		if(landmark.getDatesNotWorking() != null)
			entity.setDatesNotWorking(this.getDateBytes(landmark.getDatesNotWorking()));

		return entity;
	}

	@Override
	public LandmarkResponseDTO toDTO(LandmarkEntity landmarkEntity) {
		LandmarkResponseDTO landmarkDTO = (LandmarkResponseDTO) super.toDTO(landmarkEntity, ObjectType.LANDMARK);

		landmarkDTO.setAltitude(landmarkEntity.getAltitude());
		landmarkDTO.setCityId(landmarkEntity.getCity().getId());
		landmarkDTO.setInTop100(landmarkEntity.isInTop100());
		
		if (landmarkEntity.getDatesNotWorking() != null) {
			landmarkDTO.setDatesNotWorking(this.getListDate(landmarkEntity.getDatesNotWorking()));
		}
		return landmarkDTO;
	}

	private byte[] getDateBytes(List<Date> dateList) {
		List<Long> dateMilliseconds = dateList.stream()
				.map(date -> date.getTime())
				.collect(Collectors.toList());
		
		return Arrays.toString(dateMilliseconds.toArray()).getBytes();
	}

	private List<Date> getListDate(byte[] dates) {

		String dateStr = new String(dates, StandardCharsets.UTF_8);
		dateStr = dateStr.substring(1, dateStr.length()-1); // remove [ ]
		String[] dateMilliseconds = dateStr.split(",");
		
		List<Date> dateList = Arrays.asList(dateMilliseconds)
				.stream().map(dateLong -> new Date(Long.parseLong(dateLong.trim())))
				.collect(Collectors.toList());

		return dateList;
	}
}
