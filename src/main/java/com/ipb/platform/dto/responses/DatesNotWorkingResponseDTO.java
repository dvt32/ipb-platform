package com.ipb.platform.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
// @Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatesNotWorkingResponseDTO{

	private Long id;

    private Long landmarkId;

	private Date startDate;
	
	private Date endDate;

}
