package com.ipb.platform.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.mappers.ImageMapper;
import com.ipb.platform.mappers.ObjectMapper;
import com.ipb.platform.persistance.ImageRepository;
import com.ipb.platform.persistance.ObjectRepository;
import com.ipb.platform.persistance.entities.ImageEntity;
import com.ipb.platform.persistance.entities.ObjectEntity;
import com.ipb.platform.persistance.entities.ObjectType;
import com.ipb.platform.services.ImageService;
import com.ipb.platform.services.ObjectService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ObjectServiceImpl implements ObjectService {

	private ObjectRepository objectRepository;
	
	private ObjectMapper objectMapper;
	private ImageService imageService;
	
	@Override
	public boolean save(ObjectRequestDTO obj) {
		ObjectEntity entiity = this.objectMapper
				.toEntity(obj, ObjectType.valueOf(obj.getType())); 
		ObjectEntity objectEntity = this.objectRepository.save(entiity);
		
		// save all images
		this.imageService.save(obj.getImages(), objectEntity);
				
		return true;
	}

	@Override
	public List<ObjectResponseDTO> getAll() {
		return this.objectRepository.findAll().stream()
				.map(entity -> objectMapper.toDTO(entity, entity.getType()))
				.collect(Collectors.toList());
	}

	@Override
	public ObjectResponseDTO findById(Long id) {
		return this.objectMapper.toDTO(this.objectRepository.findById(id).get(), null);
	}

}
