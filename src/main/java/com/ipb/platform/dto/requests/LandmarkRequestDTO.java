package com.ipb.platform.dto.requests;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
// @Builder
@NoArgsConstructor
@AllArgsConstructor
public class LandmarkRequestDTO extends ObjectRequestDTO{

	private long cityId;
	
	private double altitude;
	
	private boolean inTop100;
	
	private List<Date> datesNotWorking;
}
