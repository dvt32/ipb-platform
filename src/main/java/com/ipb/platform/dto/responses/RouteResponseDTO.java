package com.ipb.platform.dto.responses;

import com.ipb.platform.dto.CoordinatesDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponseDTO {
	
	private long id;

	private CoordinatesDTO startPoint;

	private CoordinatesDTO endPoint;
	
	private Date startDate;
	
	private Date endDate;

	private String name;

	private boolean isVisit;

	private List<ObjectResponseDTO> objects;
}
