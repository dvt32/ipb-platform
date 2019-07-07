package com.ipb.platform.services;

import java.util.List;

import com.ipb.platform.dto.CoordinatesDTO;
import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.mappers.impl.IPBObjectMapperImpl;
import com.ipb.platform.persistence.entities.ObjectEntity;
import com.ipb.platform.persistence.entities.ObjectType;

public interface IPBObjectService {

    Long save(ObjectRequestDTO obj, IPBObjectMapperImpl objectMapper);

    ObjectResponseDTO update(Long id, ObjectRequestDTO obj, IPBObjectMapperImpl objectMapper);

    List<ObjectResponseDTO> getAll(int page, int numberOfObjects, IPBObjectMapperImpl objectMapper);

    ObjectResponseDTO findById(Long id, IPBObjectMapperImpl objectMapper);

    List<ObjectResponseDTO> findAllAroundCoordinates(
            List<CoordinatesDTO> coordinates,
            int numberOfObjects,
            ObjectType type,
            IPBObjectMapperImpl objectMapper
    );

    void deleteById(Long id);
}
