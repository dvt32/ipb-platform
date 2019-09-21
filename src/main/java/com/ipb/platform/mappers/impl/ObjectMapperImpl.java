package com.ipb.platform.mappers.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.CategoryResponseDTO;
import com.ipb.platform.dto.responses.CityResponseDTO;
import com.ipb.platform.dto.responses.EventResponseDTO;
import com.ipb.platform.dto.responses.ImageResponseDTO;
import com.ipb.platform.dto.responses.LandmarkResponseDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.mappers.CategoryMapper;
import com.ipb.platform.mappers.ImageMapper;
import com.ipb.platform.mappers.ObjectMapper;
import com.ipb.platform.persistence.CategoryRepository;
import com.ipb.platform.persistence.entities.CityEntity;
import com.ipb.platform.persistence.entities.EventEntity;
import com.ipb.platform.persistence.entities.ImageEntity;
import com.ipb.platform.persistence.entities.LandmarkEntity;
import com.ipb.platform.persistence.entities.ObjectEntity;
import com.ipb.platform.persistence.entities.ObjectType;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@Primary
@NoArgsConstructor
@AllArgsConstructor
public class ObjectMapperImpl implements ObjectMapper {
	
	private ImageMapper imageMapper;
	private CategoryMapper categoryMapper;
	
	private CategoryRepository categoryRepository;
	
	@Override
	public ObjectEntity toEntity(ObjectRequestDTO obj, ObjectType type) {		
		this.imageMapper = new ImageMapperImpl();
		this.categoryMapper = new CategoryMapperImpl();

		ObjectEntity entity = this.getObjectEntity(type);
		
		entity.setDescription(obj.getDescription());
		entity.setLatitude(obj.getLatitude());
		entity.setLongitude(obj.getLongitude());
		entity.setName(obj.getName());
		entity.setType(ObjectType.valueOf(obj.getType()));
		
		if(obj.getImages() != null) {
			// mapping images
			List<ImageEntity> images = obj.getImages().stream()
			.map(imgDTO -> {
				System.out.println("imgDTO: " + imgDTO);
				ImageEntity iamgeEntity = this.imageMapper.toEntity(imgDTO);
				iamgeEntity.setObject(entity);
				return iamgeEntity;
			})
			.collect(Collectors.toList());
			
			entity.setImages(images);
		}
		
		return entity;
	}

	@Override
	public ObjectResponseDTO toDTO(ObjectEntity objEntity, ObjectType type) {
		this.imageMapper = new ImageMapperImpl();
		
		ObjectResponseDTO objResponse = this.getObjectResponseDTO(type);
		
		objResponse.setId(objEntity.getId());
		objResponse.setDescription(objEntity.getDescription());
		objResponse.setLatitude(objEntity.getLatitude());
		objResponse.setLongitude(objEntity.getLongitude());
		objResponse.setName(objEntity.getName());
		objResponse.setType(objEntity.getType().toString());
		
		// mapping images
		if(objEntity.getImages() != null) {
			List<ImageResponseDTO> images = objEntity.getImages().stream()
					.map(imgEntity -> {
						System.out.println("imgEntity: " + imgEntity);
						return this.imageMapper.toDTO(imgEntity);
					})
					.collect(Collectors.toList());
			
			objResponse.setImages(images);
		}
		
		// mapping categories
		if(objEntity.getCategories() != null) {
			List<CategoryResponseDTO> categories = objEntity.getCategories().stream()
					.map(catEntity -> {
						System.out.println("catEntity: " + catEntity);
						return this.categoryMapper.toDTO(catEntity);
					})
					.collect(Collectors.toList());
			
			objResponse.setCategories(categories);
		}
		return objResponse;
	}
	
	private ObjectEntity getObjectEntity (ObjectType type) {
		if (type == null) {
			return new ObjectEntity();
		}

		switch (type) {
			case CITY:
				return new CityEntity();
			case LANDMARK:
				return new LandmarkEntity();
			case EVENT:
				return new EventEntity();
			default:
				return new ObjectEntity();
		}
	}
	
	private ObjectResponseDTO getObjectResponseDTO (ObjectType type) {
		if (type == null) {
			return new ObjectResponseDTO();
		}

		switch (type) {
			case CITY:
				return new CityResponseDTO();
			case LANDMARK:
				return new LandmarkResponseDTO();
			case EVENT:
				return new EventResponseDTO();
			default:
				return new ObjectResponseDTO();
		}
	}
}
