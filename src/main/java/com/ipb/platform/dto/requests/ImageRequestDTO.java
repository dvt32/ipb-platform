package com.ipb.platform.dto.requests;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

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
	
	private String base64Code;
	
	private String path;
}
