package com.ipb.platform.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.dto.responses.ObjectResponseDTO;
import com.ipb.platform.exceptions.IllegalRequestArgumentException;
import com.ipb.platform.mappers.impl.CityMapper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.CityRequestDTO;
import com.ipb.platform.persistance.CityRepository;
import com.ipb.platform.persistance.entities.CityEntity;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CityService extends IPBObjectServiceImpl {

	private CityRepository repository;

	private CityMapper mapper;

	@Override
	public Long save(ObjectRequestDTO object) {
		ObjectRequestDTO city = this.getCityRequestDTOInstanceOrThrow(object);
		CityEntity entity = (CityEntity) this.mapper.toEntity(city);
		super.setImages(city, entity);
		super.setCategories(city, entity);
		CityEntity objEntity = this.repository.save(entity);
		return objEntity.getId();
	}

	@Override
	public ObjectResponseDTO update(Long id, ObjectRequestDTO city) {
		CityEntity objEntity =
				this.getCityEntityOrThrow(id, "Tryed to update non-existant city.");

		super.update(id, city);
		return this.mapper.toDTO(this.repository.save(objEntity));
	}

	@Override
	public List<ObjectResponseDTO> getAll() {
		return this.repository.findAll().stream()
				.map(entity -> this.mapper.toDTO(entity))
				.collect(Collectors.toList());
	}

	@Override
	public ObjectResponseDTO findById(Long id) {
		return this.mapper.toDTO(this.repository.findById(id).get());
	}

	@Override
	public void deleteById(Long id) {
		this.getCityEntityOrThrow(id, "Tryed to delete non-exixst city!!!");
		this.repository.deleteById(id);
	}

	public CityEntity getCityEntityOrThrow(Long cityId, String errorMessage) {

		Optional<CityEntity> object = this.repository.findById(cityId);

		if (!object.isPresent()) {
			throw new IllegalRequestArgumentException(errorMessage);
		}

		return object.get();
	}

	private ObjectRequestDTO getCityRequestDTOInstanceOrThrow(ObjectRequestDTO object) {
		if (!(object instanceof CityRequestDTO))
		{
			throw new IllegalArgumentException("Incorrect Landmark");
		}

		return object;
	}
}
