package com.ipb.platform.services.impl;

import com.ipb.platform.dto.CoordinatesDTO;
import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.exceptions.IllegalRequestArgumentException;
import com.ipb.platform.mappers.ImageMapping;
import com.ipb.platform.mappers.impl.IPBObjectMapperImpl;
import com.ipb.platform.persistence.ObjectRepository;
import com.ipb.platform.persistence.UserRepository;
import com.ipb.platform.persistence.entities.*;
import com.ipb.platform.services.CategoryService;
import com.ipb.platform.services.IPBObjectService;
import com.ipb.platform.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class IPBObjectServiceImpl implements IPBObjectService {

    @Autowired
    private ObjectRepository objectRepository;

    @Qualifier("cityMapper")
    @Autowired
    private IPBObjectMapperImpl cityMapper;

    @Qualifier("eventMapper")
    @Autowired
    private IPBObjectMapperImpl eventMapper;

    @Qualifier("landmarkMapper")
    @Autowired
    private IPBObjectMapperImpl landmarkMapper;

    @Autowired
    private ImageMapping imageMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Long save(ObjectRequestDTO obj, IPBObjectMapperImpl objectMapper) {
        ObjectEntity entity = objectMapper.toEntity(obj);
        this.setCreator(entity);
        this.setImages(obj, entity);
        this.setCategories(obj, entity);
        ObjectEntity objectEntity = this.objectRepository.save(entity);
        return objectEntity.getId();
    }

    @Override
    public ObjectResponseDTO update(Long id, ObjectRequestDTO obj, IPBObjectMapperImpl objectMapper) {
        ObjectEntity objEntity =
                this.getObjectEntityOrThrow(id, "Tryed to update non-existant object.");

        objEntity.setDescription(obj.getDescription());
        objEntity.setName(obj.getName());
        objEntity.setType(ObjectType.valueOf(obj.getType()));
        objEntity.setLatitude(obj.getLatitude());
        objEntity.setLongitude(obj.getLongitude());
        this.setImages(obj, objEntity);
        this.setCategories(obj, objEntity);

        return objectMapper.toDTO(this.objectRepository.save(objEntity));
    }

    @Override
    public List<ObjectResponseDTO> getAll(int page, int numberOfObjects, IPBObjectMapperImpl objectMapper) {
        Pageable pageable = PageRequest.of(page, numberOfObjects);
        return this.objectRepository.findAll(pageable)
                .stream()
                .map(entity -> objectMapper.toDTO(entity))
                .collect(Collectors.toList());
    }

    @Override
    public ObjectResponseDTO findById(Long id, IPBObjectMapperImpl objectMapper) {
        ObjectEntity objEntity = this.getObjectEntityOrThrow(id, "Tryed to get non-existant object.");
        return objectMapper.toDTO(objEntity);
    }

    @Override
    public void deleteById(Long id) {
        this.getObjectEntityOrThrow(id, "Tryed to delete non-existant object.");
        this.objectRepository.deleteById(id);
    }

    @Override
    public List<ObjectResponseDTO> findAllAroundCoordinates(
            List<CoordinatesDTO> coordinates,
            int numberOfObjects,
            ObjectType type,
            IPBObjectMapperImpl objectMapper
    ) {
        Pageable pageable = PageRequest.of(0, numberOfObjects);
        List<ObjectResponseDTO> objectsAround = new ArrayList<>();
        coordinates
                .stream()
                .forEach(c -> objectsAround.addAll(this.getObjectsAroundCoordinates(c, pageable, type, objectMapper)));

        return objectsAround;
    }

    private List<ObjectResponseDTO> getObjectsAroundCoordinates(
            CoordinatesDTO coordinates,
            Pageable pageable,
            ObjectType type,
            IPBObjectMapperImpl objectMapper
    ) {
        return this.objectRepository
                .findAllAroundCoordinates(pageable, coordinates.getLatitude(), coordinates.getLongitude(), type)
                .stream()
                .map(obj -> objectMapper.toDTO(obj))
                .collect(Collectors.toList());
    }

    public void setImages(ObjectRequestDTO obj, ObjectEntity entity) {
        this.imageService.deleteByObjectId(entity.getId());
        if (obj.getImages() != null) {
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

    public ObjectEntity getObjectEntityOrThrow(Long objectId, String errorMessage) {

        Optional<ObjectEntity> object = this.objectRepository.findById(objectId);

        if (!object.isPresent()) {
            throw new IllegalRequestArgumentException(errorMessage);
        }

        return object.get();
    }

    public void setCreator(ObjectEntity entity) {
        String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity loggedUserEntity = userRepository.findByEmail(loggedInUserEmail).get();
        entity.setCreator(loggedUserEntity);
    }

    public List<ObjectResponseDTO> getAllNameContainsOrDescriptionContains(
            String searchValue,
            int page,
            int numberOfObjects
    ) {
        Pageable pageable = PageRequest.of(page, numberOfObjects);
        return this.objectRepository
                .findAllByNameContainsOrDescriptionContains(searchValue, searchValue, pageable)
                .stream()
                .map(obj -> this.getCorrectResponseDTO(obj))
                .collect(Collectors.toList());
    }

    private ObjectResponseDTO getCorrectResponseDTO(ObjectEntity entity) {
        switch (entity.getType()) {
            case LANDMARK:
                return this.landmarkMapper.toDTO(entity);
            case EVENT:
                return this.eventMapper.toDTO(entity);
            default:
                return this.cityMapper.toDTO(entity);
        }
    }
}
