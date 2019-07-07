package com.ipb.platform.services.impl;

import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.exceptions.IllegalRequestArgumentException;
import com.ipb.platform.mappers.ImageMapping;
import com.ipb.platform.mappers.IPBObjectMapping;
import com.ipb.platform.mappers.impl.IPBObjectMapperImpl;
import com.ipb.platform.persistance.ObjectRepository;
import com.ipb.platform.persistance.entities.CategoryEntity;
import com.ipb.platform.persistance.entities.ImageEntity;
import com.ipb.platform.persistance.entities.ObjectEntity;
import com.ipb.platform.persistance.entities.ObjectType;
import com.ipb.platform.services.CategoryService;
import com.ipb.platform.services.ImageService;
import com.ipb.platform.services.IPBObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class IPBObjectServiceImpl implements IPBObjectService {

	@Autowired
	private ObjectRepository objectRepository;

	@Qualifier("cityMapper")
	@Autowired
	private IPBObjectMapperImpl objectMapper;
	@Autowired
	private ImageMapping imageMapper;

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ImageService imageService;

	@Override
	public Long save(ObjectRequestDTO obj) {
		ObjectEntity entity = this.objectMapper.toEntity(obj);
		this.setImages(obj,entity);
		this.setCategories(obj, entity);
		ObjectEntity objectEntity = this.objectRepository.save(entity);
		return objectEntity.getId();
	}

	@Override
	public ObjectResponseDTO update(Long id, ObjectRequestDTO obj) {
		ObjectEntity objEntity =
                this.getObjectEntityOrThrow(id, "Tryed to update non-existant object.");

		objEntity.setDescription(obj.getDescription());
		objEntity.setName(obj.getName());
		objEntity.setType(ObjectType.valueOf(obj.getType()));
		objEntity.setLatitude(obj.getLatitude());
		objEntity.setLongitude(obj.getLongitude());
		this.setImages(obj,objEntity);
		this.setCategories(obj, objEntity);

		return objectMapper.toDTO(this.objectRepository.save(objEntity));
	}

	@Override
	public List<ObjectResponseDTO> getAll() {
		return this.objectRepository.findAll()
                .stream()
				.map(entity -> objectMapper.toDTO(entity))
				.collect(Collectors.toList());
	}

	@Override
	public ObjectResponseDTO findById(Long id) {
		ObjectEntity objEntity = this.getObjectEntityOrThrow(id, "Tryed to get non-existant object.");
		return this.objectMapper.toDTO(objEntity);
	}

	@Override
	public void deleteById(Long id) {
		this.getObjectEntityOrThrow(id, "Tryed to delete non-existant object.");
		this.objectRepository.deleteById(id);
	}

	public void setImages (ObjectRequestDTO obj, ObjectEntity entity){
		this.imageService.deleteByObjectId(entity.getId());
		if(obj.getImages() != null) {
			List<ImageEntity> images = obj.getImages()
					.stream()
					.map(imgDTO -> {
						ImageEntity imageEntity = this.imageMapper.toEntity(imgDTO);
						imageEntity.setObject(entity);
						return imageEntity;
					})
					.collect(Collectors.toList());

			entity.setImages(images);
		}
	}

	public void setCategories(ObjectRequestDTO obj, ObjectEntity entity) {
		if (obj.getCategories() != null) {
			List<CategoryEntity> categoriesEntities = this.getCategoriesList(obj.getCategories());
			entity.setCategories(categoriesEntities);
		}
	}

	private List<CategoryEntity> getCategoriesList(List<Long> categoriesId) {
		return categoriesId
				.stream()
				.map(catId -> this.categoryService.findCategoryEntityById(catId))
				.collect(Collectors.toList());
	}

	private ObjectEntity getObjectEntityOrThrow(Long objectId, String errorMessage) {

		Optional<ObjectEntity> object = this.objectRepository.findById(objectId);

		if (!object.isPresent()) {
			throw new IllegalRequestArgumentException(errorMessage);
		}

		return object.get();
	}

}
