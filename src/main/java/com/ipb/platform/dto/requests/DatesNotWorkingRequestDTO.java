package com.ipb.platform.dto.requests;

import com.ipb.platform.dto.responses.ObjectResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatesNotWorkingRequestDTO extends ObjectResponseDTO{

	private Long landmarkId;

	private Date startDate;
	
	private Date endDate;

}
