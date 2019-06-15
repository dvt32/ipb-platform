package com.ipb.platform.dto.responses;

import com.ipb.platform.persistance.entities.ObjectType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CategoryResponseDTO {
	private Long id;
	
	private Long parentId;
	
	private String name;
}
