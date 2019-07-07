package com.ipb.platform.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ipb.platform.dto.CoordinatesDTO;
import com.ipb.platform.dto.requests.DatesNotWorkingRequestDTO;
import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.exceptions.IllegalRequestArgumentException;
import com.ipb.platform.mappers.DatesNotWorkingMapping;
import com.ipb.platform.mappers.impl.LandmarkMapper;
import com.ipb.platform.persistence.entities.*;
import com.ipb.platform.services.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.LandmarkRequestDTO;
import com.ipb.platform.persistence.LandmarkRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LandmarkService extends IPBObjectServiceImpl {
	
	private LandmarkRepository repository;

	private LandmarkMapper mapper;
	private DatesNotWorkingMapping datesNotWorkingMapper;

	private CityService cityService;
	private DatesNotWorkingService datesNotWorkingService;

	public Long save(ObjectRequestDTO object) {

		LandmarkRequestDTO landmark = this.getLandmarkRequestDTOOrThrow(object);

		this.checkForExistLandmarkByName(landmark.getName());
		CityEntity city =
				this.cityService.getCityEntityOrThrow(
						landmark.getCityId(),
						"Tryed to add a landmark for non-existant city."
				);
		
		LandmarkEntity entity = this.getLandmarkEntityOrThrow(this.mapper.toEntity(landmark));
		entity.setCity(city);
		super.setCreator(entity);
		super.setImages(landmark, entity);
		super.setCategories(landmark, entity);
		this.setDatesNotWorking(landmark, entity);

		LandmarkEntity objEntity = this.repository.save(entity);
		return objEntity.getId();
	}

	public List<ObjectResponseDTO> getAll(int page, int numberOfObjects) {
		Pageable pageable = PageRequest.of(page, numberOfObjects);
		return this.repository.findAll(pageable).stream()
				.map(entity -> this.mapper.toDTO(entity))
				.collect(Collectors.toList());
	}

	public ObjectResponseDTO update(Long id, ObjectRequestDTO objectDTO) {
		LandmarkEntity objEntity =
				this.getLandmarkEntityOrThrow(id, "Tryed to update non-existant landmark.");

		LandmarkRequestDTO landmark = getLandmarkRequestDTOOrThrow(objectDTO);

		CityEntity city =
				this.cityService.getCityEntityOrThrow(
						landmark.getCityId(),
						"Tryed to add a landmark for non-existant city."
				);

		super.update(id, landmark, this.mapper);
		LandmarkEntity mapEntity = this.getLandmarkEntityOrThrow(this.mapper.toEntity(landmark));

		objEntity.setAltitude(mapEntity.getAltitude());
		objEntity.setInTop100(mapEntity.isInTop100());
		objEntity.setWorkTime(mapEntity.getWorkTime());
		objEntity.setDatesNotWorking(mapEntity.getDatesNotWorking());
		objEntity.setCity(city);

		return this.mapper.toDTO(this.repository.save(objEntity));
	}

	public ObjectResponseDTO findById(Long id) {
		return this.mapper.toDTO(this.repository.findById(id).get());
	}

	@Override
	public void deleteById(Long id) {
		super.deleteById(id);
	}

	public List<ObjectResponseDTO> findAllAroundCoordinates(List<CoordinatesDTO> coordinates, int numberOfObjects){
		return super.findAllAroundCoordinates(coordinates, numberOfObjects, ObjectType.LANDMARK, this.mapper);
	}

	private LandmarkEntity getLandmarkEntityOrThrow(Long eventId, String errorMessage) {

		Optional<LandmarkEntity> object = this.repository.findById(eventId);

		if (!object.isPresent()) {
			throw new IllegalRequestArgumentException(errorMessage);
		}

		return object.get();
	}

	private void setDatesNotWorking (LandmarkRequestDTO obj, LandmarkEntity entity){
		this.datesNotWorkingService.deleteByObjectId(entity.getId());
		if(obj.getDatesNotWorking() != null) {
			List<DatesNotWorkingEntity> datesNotWorking = obj.getDatesNotWorking()
					.stream()
					.map(dateDTO -> {
						this.checkDateNotWorkingOrThrow(dateDTO);
						DatesNotWorkingEntity dateEntity = this.datesNotWorkingMapper.toEntity(dateDTO);
						dateEntity.setLandmark(entity);
						return dateEntity;
					})
					.collect(Collectors.toList());

			 entity.setDatesNotWorking(datesNotWorking);
		}
	}

	private void checkForExistLandmarkByName(String name){
		List<LandmarkEntity> landmarksByName =
				this.repository.findAllByName(name);
		if (landmarksByName.size() > 0) {
			throw new IllegalArgumentException("Landmark with name '"+ name +"' exists!");
		}
	}

	private boolean checkDateNotWorkingOrThrow(DatesNotWorkingRequestDTO dto) {
		if (dto.getStartDate() == null || dto.getStartDate() == null ) {
			throw new IllegalRequestArgumentException("Tryed to add landmark with date not working start or end empty date!!!");
		}
		return true;
	}

	private LandmarkRequestDTO getLandmarkRequestDTOOrThrow(ObjectRequestDTO object) {
		if (!(object instanceof LandmarkRequestDTO))
		{
			throw new IllegalArgumentException("Incorrect Landmark");
		}

		return ((LandmarkRequestDTO) object) ;
	}

	private LandmarkEntity getLandmarkEntityOrThrow(ObjectEntity object) {
		if (!(object instanceof LandmarkEntity))
		{
			throw new IllegalArgumentException("Incorrect Landmark");
		}

		return ((LandmarkEntity) object) ;
	}
}
