package com.ipb.platform.mappers.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.UnsupportedEncodingException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ipb.platform.dto.requests.ImageRequestDTO;
import com.ipb.platform.dto.responses.ImageResponseDTO;
import com.ipb.platform.mappers.ImageMapper;
import com.ipb.platform.persistence.entities.ImageEntity;

import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class ImageMapperImpl implements ImageMapper{
	
	@Override
	public ImageEntity toEntity(ImageRequestDTO image) {
		ImageEntity entity = new ImageEntity();

		String imgURL = image.getPath();

		// check path image is correct
		if (this.isCorrectURL(imgURL))
			entity.setPath(imgURL);
		else
			throw new IllegalArgumentException("Image path is not correct!!!");

		String base64 =image.getBase64Code();
		
		if(this.isCorrectBase64Code(base64)) 
			entity.setBase64Code(base64);
		else 
			throw new IllegalArgumentException("Base code is not correct!!!");
		
		return entity;
	}
	
	@Override
	public ImageResponseDTO toDTO(ImageEntity imageEntity) {
		ImageResponseDTO imageDTO = new ImageResponseDTO();

		imageDTO.setBase64code(imageEntity.getBase64Code());
		imageDTO.setPath(imageEntity.getPath());
		imageDTO.setIpbObjectId(imageEntity.getObject().getId());
		
		return imageDTO;
	}

	/**
	 *  
	 * @param imgUrl - path to the image
	 * @return true if imgPath is empty or is correct path. 
	 * Return false if imgPath not start http:// or https:// and 
	 * not ends with .jpg or .jpeg or .gif or .png
	 * 
	 */
	private boolean isCorrectURL(String imgUrl) {
		if (imgUrl == "" || imgUrl == null)
			return true;

		String pattern = "^(http(s?)://).*\\.(?:jpg|jpeg|png)$";

		// Create a Pattern object
		Pattern patt = Pattern.compile(pattern);

		// Now create matcher object.
		Matcher m = patt.matcher(imgUrl);

		return m.matches();
	}
	
	private boolean isCorrectBase64Code(String base64code) {
		if (base64code == "" || base64code == null)
			return true;
		
		String[] data = base64code.split(",");
		// data[0] -> data:image/jpeg;base64
		// data[1] -> /98.....kj= 

		if(data.length != 2) return false;
	
		boolean isCorrectBAse64Type = this.isCorrectBase64Type(data[0]);
		boolean isBae64Code = Base64.isBase64(data[1].getBytes());
		
		if(!isCorrectBAse64Type || !isBae64Code)
			return false;
		
		return true;
	}
	
	private boolean isCorrectBase64Type(String base64Type) {
		String pattern = "^data:image/(?:jpg|jpeg|png);base64$";

		Pattern patt = Pattern.compile(pattern);
		Matcher m = patt.matcher(base64Type);

		return m.matches();
	}
	
}
