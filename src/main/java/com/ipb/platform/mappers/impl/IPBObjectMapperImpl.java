package com.ipb.platform.mappers.impl;

import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.CategoryResponseDTO;
import com.ipb.platform.dto.responses.ImageResponseDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.mappers.CategoryMapping;
import com.ipb.platform.mappers.IPBObjectMapping;
import com.ipb.platform.mappers.ImageMapping;
import com.ipb.platform.persistance.entities.ObjectEntity;
import com.ipb.platform.persistance.entities.ObjectType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public abstract class IPBObjectMapperImpl implements IPBObjectMapping {

    @Autowired
    private ImageMapping imageMapper;
    @Autowired
    private CategoryMapping categoryMapper;

    public ObjectEntity setEntityFields(ObjectEntity entity, ObjectRequestDTO obj) {
        entity.setDescription(obj.getDescription());
        entity.setLatitude(obj.getLatitude());
        entity.setLongitude(obj.getLongitude());
        entity.setName(obj.getName());
        entity.setType(ObjectType.valueOf(obj.getType()));

        return entity;
    }

    public ObjectResponseDTO setDTOFields( ObjectResponseDTO objResponse, ObjectEntity objEntity) {
        objResponse.setId(objEntity.getId());
        objResponse.setDescription(objEntity.getDescription());
        objResponse.setLatitude(objEntity.getLatitude());
        objResponse.setLongitude(objEntity.getLongitude());
        objResponse.setName(objEntity.getName());
        objResponse.setType(objEntity.getType().toString());

        if(objEntity.getImages() != null) {
            List<ImageResponseDTO> images = objEntity.getImages().stream()
                    .map(imgEntity -> this.imageMapper.toDTO(imgEntity))
                    .collect(Collectors.toList());

            objResponse.setImages(images);
        }

        if(objEntity.getCategories() != null) {
            List<CategoryResponseDTO> categories = objEntity.getCategories().stream()
                    .map(catEntity -> this.categoryMapper.toDTO(catEntity))
                    .collect(Collectors.toList());

            objResponse.setCategories(categories);
        }
        return objResponse;
    }
}
