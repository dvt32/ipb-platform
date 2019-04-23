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

import com.ipb.platform.dto.requests.ImageRequestDTO;
import com.ipb.platform.dto.responses.ImageResponseDTO;
import com.ipb.platform.mappers.ImageMapper;
import com.ipb.platform.persistance.entities.ImageEntity;

import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class ImageMapperImpl implements ImageMapper{
	
	@Override
	public ImageEntity toEntity(ImageRequestDTO image) {
		ImageEntity entity = new ImageEntity();

		String imgURL = image.getPath();

		// check path image is correct
		if (this.correctURL(imgURL))
			entity.setPath(imgURL);
		else
			throw new IllegalArgumentException("Image path is not correct!!!");

		entity.setBase64Code(this.getImageBase64Code(image.getFile()));
		
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
	private boolean correctURL(String imgUrl) {
		if (imgUrl == "")
			return true;

		String pattern = "^(http(s?)://).*\\.(?:jpg|jpeg|gif|png)$";

		// Create a Pattern object
		Pattern patt = Pattern.compile(pattern);

		// Now create matcher object.
		Matcher m = patt.matcher(imgUrl);

		return m.matches();
	}

	/**
	 * 
	 * @param image - image file 
	 * @return if image is empty return empty string (base64 code)
	 * Return base64 code if not throw an exception
	 */
	private String getImageBase64Code(File image) {
		if (image == null)
			return "";
		
		byte[] bytes =  this.getFileBytes(image);
		String encodedFile = "";
		
		try {
			encodedFile = new String(Base64.encodeBase64(bytes), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// throw new Exception
			e.printStackTrace();
		}
		
		return encodedFile;
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	private byte[] getFileBytes(File file) {
		byte[] bytesArray = new byte[(int) file.length()]; 
		
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(file);
			fis.read(bytesArray);
			
			// if FileInputStream is open close it
			if(fis != null) fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			// throw new Exception
		} catch (IOException e) {
			e.printStackTrace();
			// throw new Exception
		} 
		
		return bytesArray;		
	}
}
