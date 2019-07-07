package com.ipb.platform.mappers.impl;

import com.ipb.platform.dto.CoordinatesDTO;
import com.ipb.platform.dto.requests.RouteRequestDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.dto.responses.RouteResponseDTO;
import com.ipb.platform.mappers.RouteMapping;
import com.ipb.platform.persistence.entities.ObjectEntity;
import com.ipb.platform.persistence.entities.RouteEntity;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class RouteMapperImpl implements RouteMapping {

    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private LandmarkMapper landmarkMapper;

    @Autowired
    private EventMapper eventMapper;

    @Override
    public RouteEntity toEntity(RouteRequestDTO routeDTO) {
        RouteEntity entity = new RouteEntity();
        entity.setName(routeDTO.getName());
        entity.setVisit(routeDTO.isVisit());

        if (routeDTO.getStartDate() != null) {
            entity.setStartDate(routeDTO.getStartDate().getTime());
        }

        if (routeDTO.getEndDate() != null) {
            entity.setEndDate(routeDTO.getEndDate().getTime());
        }

        return entity;
    }

    @Override
    public RouteResponseDTO toDTO(RouteEntity routeEntity) {
        RouteResponseDTO dto = new RouteResponseDTO();
        dto.setId(routeEntity.getId());
        dto.setName(routeEntity.getName());
        dto.setVisit(routeEntity.isVisit());
        dto.setStartDate(new Date(routeEntity.getStartDate()));
        dto.setEndDate(new Date(routeEntity.getEndDate()));

        dto.setStartPoint(new CoordinatesDTO(
                routeEntity.getStartLatitude(),
                routeEntity.getStartLongitude()
        ));

        dto.setEndPoint(new CoordinatesDTO(
                routeEntity.getEndLatitude(),
                routeEntity.getEndLongitude()
        ));

        this.setObjects(routeEntity, dto);
        return dto;
    }

    private void setObjects(RouteEntity routeEntity, RouteResponseDTO dto) {
        if (routeEntity.getObjects() != null) {
            List<ObjectResponseDTO> objects = routeEntity.getObjects().stream()
                    .map(objEntity -> this.getObject(objEntity))
                    .collect(Collectors.toList());

            dto.setObjects(objects);
        }
    }

    private ObjectResponseDTO getObject(ObjectEntity objEntity) {
        switch (objEntity.getType()) {
            case LANDMARK:
                return this.landmarkMapper.toDTO(objEntity);
            case EVENT:
                return this.eventMapper.toDTO(objEntity);
            default:
                return this.cityMapper.toDTO(objEntity);
        }
    }

}
