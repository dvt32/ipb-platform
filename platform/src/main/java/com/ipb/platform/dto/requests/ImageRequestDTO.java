package com.ipb.platform.dto.requests;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageRequestDTO {
	private Long ipbObjectId;
	
	private File file;
	
	private String path;
}
