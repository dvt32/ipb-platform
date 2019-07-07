package com.ipb.platform.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ipb.platform.exceptions.IllegalRequestArgumentException;
import org.springframework.stereotype.Service;

import com.ipb.platform.dto.responses.ImageResponseDTO;
import com.ipb.platform.mappers.ImageMapping;
import com.ipb.platform.persistence.ImageRepository;
import com.ipb.platform.persistence.entities.ImageEntity;
import com.ipb.platform.services.ImageService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {

	private ImageRepository repository;

	private ImageMapping mapper;

	@Override
	public ImageResponseDTO findById(Long id) {
		this.checkImageExistOrThrow(id, "Try to get image from non-existing id");
		return this.mapper.toDTO(this.repository.findById(id).get());
	}

	@Override
	public boolean deleteById(Long id) {
		this.checkImageExistOrThrow(id, "Try to delete image from non-existing id");
		this.repository.deleteById(id);
		return true;
	}

	@Override
	public boolean deleteByObjectId(Long objectId) {
		this.repository.deleteByObjectId(objectId);
		return true;
	}

	@Override
	public List<ImageResponseDTO> findByObjectId(Long objId) {
		return this.repository.findAllByObjectId(objId)
				.stream()
				.map(entity -> mapper.toDTO(entity))
				.collect(Collectors.toList());
	}

	private ImageEntity checkImageExistOrThrow(Long imageId, String errorMessage) {

		Optional<ImageEntity> image = this.repository.findById(imageId);

		if (!image.isPresent()) {
			throw new IllegalRequestArgumentException(errorMessage);
		}

		return image.get();
	}
}
