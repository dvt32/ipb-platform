package com.ipb.platform.dto.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectResponseDTO {
	private long id;
	
	private String name;
	
	private String description;
	
	private double latitude;
	
	private double longitude;
	
	private String type;

	private boolean isApproved;
	
	private List<ImageResponseDTO> images;
	
	private List<CategoryResponseDTO> categories;
}
