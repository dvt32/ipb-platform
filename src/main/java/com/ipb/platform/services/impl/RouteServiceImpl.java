package com.ipb.platform.services.impl;

import com.ipb.platform.dto.CoordinatesDTO;
import com.ipb.platform.dto.requests.RouteRequestDTO;
import com.ipb.platform.dto.responses.RouteResponseDTO;
import com.ipb.platform.mappers.impl.RouteMapperImpl;
import com.ipb.platform.persistence.ObjectRepository;
import com.ipb.platform.persistence.RouteRepository;
import com.ipb.platform.persistence.UserRepository;
import com.ipb.platform.persistence.entities.*;
import com.ipb.platform.services.RouteService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RouteServiceImpl implements RouteService {
	
	private RouteRepository repository;

	private RouteMapperImpl mapper;
	
	private ObjectRepository objectRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Long save(RouteRequestDTO route) {
		this.checkRouteFields(route);
		RouteEntity entity = this.mapper.toEntity(route);
		this.setCreator(entity);
		this.setObjects(route, entity);
		this.setStartAndEndPoint(route, entity);
		RouteEntity saveEntity = this.repository.save(entity);
		return saveEntity.getId();
	}

	@Override
	public RouteResponseDTO update(Long routeId, RouteRequestDTO route) {
		RouteEntity entity = getRouteEntityOrThrow(routeId, "Tried to update not exist route.");
		this.checkRouteFields(route);
		this.setObjects(route, entity);
		entity.setName(route.getName());
		entity.setVisit(route.isVisit());
		entity.setStartDate(route.getStartDate().getTime());
		entity.setEndDate(route.getEndDate().getTime());
		this.setStartAndEndPoint(route, entity);
		RouteEntity saveEntity = this.repository.save(entity);
		return this.mapper.toDTO(saveEntity);
	}

	@Override
	public List<RouteResponseDTO> getAll() {
		return this.repository
				.findAll()
				.stream()
				.map(entity -> this.mapper.toDTO(entity))
				.collect(Collectors.toList());
	}

	@Override
	public List<RouteResponseDTO> findByUserId(Long userId) {
		// return this.mapper.toDTO(this.repository.findByUserId(id));
		return null;
	}

	@Override
	public RouteResponseDTO findById(Long id) {
		RouteEntity entity = getRouteEntityOrThrow(id, "Tried to get non-existant route.");
		return this.mapper.toDTO(entity);
	}

	@Override
	public boolean deleteById(Long id) {
		RouteEntity entity = getRouteEntityOrThrow(id, "Tried to delete non-existant route.");
		this.repository.deleteById(entity.getId());
		return true;
	}

	private void checkRouteFields(RouteRequestDTO routeDTO) {
		checkDate(routeDTO);
	}

	private void checkDate(RouteRequestDTO routeDTO) {
		if (routeDTO.getStartDate() == null || routeDTO.getEndDate() == null) {
			throw new IllegalArgumentException("Tried to add a route with empty stat date or end date.");
		}
	}

	private void setStartAndEndPoint(RouteRequestDTO routeDTO, RouteEntity routeEntity){

		routeEntity.setStartLatitude(routeDTO.getStartPoint().getLatitude());
		routeEntity.setStartLongitude(routeDTO.getStartPoint().getLongitude());

		routeEntity.setEndLatitude(routeDTO.getEndPoint().getLatitude());
		routeEntity.setEndLongitude(routeDTO.getEndPoint().getLongitude());
	}

	private RouteEntity getRouteEntityOrThrow(Long id, String message){
		Optional<RouteEntity> routeOptional = this.repository.findById(id);
		if(!routeOptional.isPresent()){
			throw new IllegalArgumentException(message);
		}
		return routeOptional.get();
	}

	private void setObjects(RouteRequestDTO obj, RouteEntity entity) {
		if (obj.getObjectsIds() != null) {
			List<ObjectEntity> objectsEntities = this.getObjectsList(obj.getObjectsIds());
			entity.setObjects(objectsEntities);
		}
	}

	private List<ObjectEntity> getObjectsList(List<Long> objectsId) {
		return objectsId
				.stream()
				.map(objectId -> this.objectRepository.findById(objectId).get())
				.collect(Collectors.toList());
	}

	private void setCreator(RouteEntity entity) {
		String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		UserEntity loggedUserEntity = userRepository.findByEmail(loggedInUserEmail).get();
		entity.setCreator(loggedUserEntity);
	}
}
