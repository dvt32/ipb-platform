package com.ipb.platform.dto.responses;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponseDTO {
	private Long ipbObjectId;
	
	private String base64code;
	
	private String path;
}
