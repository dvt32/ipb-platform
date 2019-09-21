package com.ipb.platform.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.ImageRequestDTO;
import com.ipb.platform.dto.responses.ImageResponseDTO;
import com.ipb.platform.mappers.ImageMapper;
import com.ipb.platform.persistence.ImageRepository;
import com.ipb.platform.persistence.entities.ImageEntity;
import com.ipb.platform.persistence.entities.ObjectEntity;
import com.ipb.platform.services.ImageService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {

	private ImageRepository repository;
	private ImageMapper mapper;

	@Override
	public Long save(ImageRequestDTO obj) {
		ImageEntity entiity = this.mapper.toEntity(obj);
		ImageEntity newEntiity = this.repository.save(entiity);
		return newEntiity.getId();
	}

	@Override
	public ImageResponseDTO findById(Long id) {
		return this.mapper.toDTO(this.repository.findById(id).get());
	}

	@Override
	public List<ImageResponseDTO> findByObjectId(Long objId) {
		return this.repository.findAll().stream()
				.filter(elem -> elem.getObject().getId() == objId)
				.map(entity -> mapper.toDTO(entity))
				.collect(Collectors.toList());
	}

	@Override
	public boolean save(List<ImageRequestDTO> images, ObjectEntity objectEntity) {
		// save all images
		if(images != null && objectEntity != null) {
			images.stream()
			.forEach(imageDTO -> {
				ImageEntity imgEntity = this.mapper.toEntity(imageDTO);
				imgEntity.setObject(objectEntity);
				this.repository.save(imgEntity);
			});
			
			return true;
		}
		
		return false;
	}

}
