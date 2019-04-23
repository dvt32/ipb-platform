package com.ipb.platform.dto.responses;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDTO extends ObjectResponseDTO{
	
	private long cityId;
	
	private Date startDate;
	
	private Date endDate;
}
