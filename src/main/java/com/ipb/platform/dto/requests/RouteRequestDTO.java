package com.ipb.platform.dto.requests;

import com.ipb.platform.dto.CoordinatesDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteRequestDTO {

	private CoordinatesDTO startPoint;

	private CoordinatesDTO endPoint;

	private Date startDate;

	private Date endDate;

	private String name;

	private boolean isVisit;

	private List<Long> objectsIds;
}
