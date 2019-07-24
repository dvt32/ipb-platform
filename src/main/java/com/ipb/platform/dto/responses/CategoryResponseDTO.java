package com.ipb.platform.dto.responses;

import lombok.Data;

@Data
public class CategoryResponseDTO {
	private Long id;
	
	private Long parentId;
	
	private String name;
}
