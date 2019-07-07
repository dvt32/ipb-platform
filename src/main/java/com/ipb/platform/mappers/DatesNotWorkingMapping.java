package com.ipb.platform.mappers;

import com.ipb.platform.dto.requests.DatesNotWorkingRequestDTO;
import com.ipb.platform.dto.responses.DatesNotWorkingResponseDTO;
import com.ipb.platform.persistance.entities.DatesNotWorkingEntity;

public interface DatesNotWorkingMapping {
	
	DatesNotWorkingEntity toEntity(DatesNotWorkingRequestDTO datesNotWorkingDTO);

	DatesNotWorkingResponseDTO toDTO(DatesNotWorkingEntity datesNotWorkingEntity);
}
