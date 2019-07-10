package com.ipb.platform.dto.requests;

import java.util.List;

import com.ipb.platform.persistence.entities.ObjectType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectRequestDTO {
	private String name;
	
	private String description;
	
	private double latitude;
	
	private double longitude;
	
	private String type;
	
	private List<ImageRequestDTO> images;
	
	private List<Long> categoriesID;
}
