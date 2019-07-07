package com.ipb.platform.dto.requests;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDTO extends ObjectRequestDTO{
	private long cityId;
	private Date startDate;
	private Date endDate;
	private String workTime;
}
