package com.ipb.platform.mappers.impl;

import com.ipb.platform.dto.requests.DatesNotWorkingRequestDTO;
import com.ipb.platform.dto.responses.DatesNotWorkingResponseDTO;
import com.ipb.platform.mappers.DatesNotWorkingMapping;
import com.ipb.platform.persistance.entities.DatesNotWorkingEntity;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@NoArgsConstructor
public class DatesNotWorkingMapperImpl implements DatesNotWorkingMapping {

	@Override
	public DatesNotWorkingEntity toEntity(DatesNotWorkingRequestDTO dto) {
		DatesNotWorkingEntity entity = new DatesNotWorkingEntity();

		if (dto.getStartDate() != null){
			entity.setStartDate(dto.getStartDate().getTime());
		}

		if (dto.getEndDate() != null) {
			entity.setEndDate(dto.getEndDate().getTime());
		}

		return entity;
	}

	@Override
	public DatesNotWorkingResponseDTO toDTO(DatesNotWorkingEntity entity) {
		DatesNotWorkingResponseDTO responseDTO = new DatesNotWorkingResponseDTO();
		responseDTO.setId(entity.getId());
		responseDTO.setLandmarkId(entity.getLandmark().getId());
		responseDTO.setStartDate(new Date(entity.getStartDate()));
		responseDTO.setEndDate(new Date(entity.getEndDate()));
		return responseDTO;
	}
}
