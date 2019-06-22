package com.ipb.platform.mappers.impl;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.CityRequestDTO;
import com.ipb.platform.dto.requests.EventRequestDTO;
import com.ipb.platform.dto.responses.CityResponseDTO;
import com.ipb.platform.dto.responses.EventResponseDTO;
import com.ipb.platform.mappers.EventMapper;
import com.ipb.platform.persistence.entities.CityEntity;
import com.ipb.platform.persistence.entities.EventEntity;
import com.ipb.platform.persistence.entities.ObjectType;

import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class EventMapperImpl extends ObjectMapperImpl implements EventMapper {
	
	@Override
	public EventEntity toEntity(EventRequestDTO event) {
		EventEntity entity = (EventEntity) super.toEntity(event, ObjectType.EVENT);
		
		entity.setStartDate(event.getStartDate().getTime());
		entity.setEndDate(event.getEndDate().getTime());

		return entity;
	}

	@Override
	public EventResponseDTO toDTO(EventEntity eventEntity) {
		EventResponseDTO dto = (EventResponseDTO) super.toDTO(eventEntity, ObjectType.EVENT);
		
		dto.setId(eventEntity.getId());
		dto.setStartDate(new Date(eventEntity.getStartDate())); // entity start date is type Long
		dto.setEndDate(new Date(eventEntity.getEndDate())); // entity end date is type Long
		dto.setCityId(eventEntity.getCity().getId());

		return dto;
	}
}
