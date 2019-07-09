package com.ipb.platform.dto.requests;

import java.util.Date;
import java.util.List;

import javax.persistence.JoinColumn;

import com.ipb.platform.persistence.entities.ObjectType;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
