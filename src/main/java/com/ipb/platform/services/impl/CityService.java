package com.ipb.platform.services.impl;

import com.ipb.platform.dto.CoordinatesDTO;
import com.ipb.platform.dto.requests.CityRequestDTO;
import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.exceptions.IllegalRequestArgumentException;
import com.ipb.platform.mappers.impl.CityMapper;
import com.ipb.platform.persistence.CityRepository;
import com.ipb.platform.persistence.entities.CityEntity;
import com.ipb.platform.persistence.entities.ObjectType;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CityService extends IPBObjectServiceImpl {

    private CityRepository repository;

    private CityMapper mapper;

    public Long save(ObjectRequestDTO object) {
        ObjectRequestDTO city = this.getCityRequestDTOInstanceOrThrow(object);
        List<CityEntity> citiesByName =
                this.repository.findAllByName(city.getName());
        if (citiesByName.size() > 0) {
            throw new IllegalArgumentException("City with name '"+ city.getName() +"' exists!");
        }
        CityEntity entity = (CityEntity) this.mapper.toEntity(city);
        super.setCreator(entity);
        super.setImages(city, entity);
        super.setCategories(city, entity);
        CityEntity objEntity = this.repository.save(entity);
        return objEntity.getId();
    }

    public ObjectResponseDTO update(Long id, ObjectRequestDTO city) {
        CityEntity objEntity =
                this.getCityEntityOrThrow(id, "Tryed to update non-existant city.");

        super.update(id, city, this.mapper);
        return this.mapper.toDTO(this.repository.save(objEntity));
    }

    public List<ObjectResponseDTO> getAll(){
        return this.repository.findAll().stream()
                .map(entity -> this.mapper.toDTO(entity))
                .collect(Collectors.toList());
    }

    public List<ObjectResponseDTO> getAll(int page, int numberOfObjects) {
        Pageable pageable = PageRequest.of(page, numberOfObjects);
        return this.repository.findAll(pageable).stream()
                .map(entity -> this.mapper.toDTO(entity))
                .collect(Collectors.toList());
    }

    public ObjectResponseDTO findById(Long id) {
        return this.mapper.toDTO(this.repository.findById(id).get());
    }

    @Override
    public void deleteById(Long id) {
        this.getCityEntityOrThrow(id, "Tryed to delete non-exixst city!!!");
        this.repository.deleteById(id);
    }

    public List<ObjectResponseDTO> findAllAroundCoordinates(List<CoordinatesDTO> coordinates, int numberOfObjects) {
        return super.findAllAroundCoordinates(coordinates, numberOfObjects, ObjectType.CITY, this.mapper);
    }

    public CityEntity getCityEntityOrThrow(Long cityId, String errorMessage) {

        Optional<CityEntity> object = this.repository.findById(cityId);

        if (!object.isPresent()) {
            throw new IllegalRequestArgumentException(errorMessage);
        }

        return object.get();
    }

    private ObjectRequestDTO getCityRequestDTOInstanceOrThrow(ObjectRequestDTO object) {
        if (!(object instanceof CityRequestDTO)) {
            throw new IllegalArgumentException("Incorrect city");
        }

        return object;
    }
}
