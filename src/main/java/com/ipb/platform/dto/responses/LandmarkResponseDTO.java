package com.ipb.platform.dto.responses;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
// @Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandmarkResponseDTO extends ObjectResponseDTO{

	private long cityId;
	
	private double altitude;
	
	private boolean inTop100;
	
	private List<Date> datesNotWorking;
}
