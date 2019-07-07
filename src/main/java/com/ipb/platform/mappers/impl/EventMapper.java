package com.ipb.platform.mappers.impl;

import java.util.Date;

import com.ipb.platform.dto.requests.ObjectRequestDTO;
import com.ipb.platform.persistance.entities.ObjectEntity;
import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.EventRequestDTO;
import com.ipb.platform.dto.responses.EventResponseDTO;
import com.ipb.platform.persistance.entities.EventEntity;

import lombok.NoArgsConstructor;

@Service("eventMapper")
@NoArgsConstructor
public class EventMapper extends IPBObjectMapperImpl {
	
	@Override
	public EventEntity toEntity(ObjectRequestDTO obj) {
		EventEntity entity = new EventEntity();
		super.setEntityFields(entity, obj);

		EventRequestDTO event = this.getEventRequestDTOOrThrow(obj);
		if (event.getStartDate() != null) {
			entity.setStartDate(event.getStartDate().getTime());
		}

		if (event.getEndDate() != null) {
			entity.setEndDate(event.getEndDate().getTime());
		}

		entity.setWorkTime(event.getWorkTime());
		return entity;
	}

	@Override
	public EventResponseDTO toDTO(ObjectEntity objEntity) {
		EventResponseDTO dto = new EventResponseDTO();
		super.setDTOFields(dto, objEntity);
		EventEntity eventEntity = this.getEventEntityOrThrow(objEntity);

		dto.setId(eventEntity.getId());
		dto.setStartDate(new Date(eventEntity.getStartDate()));
		dto.setEndDate(new Date(eventEntity.getEndDate()));
		dto.setCityId(eventEntity.getCity().getId());
		dto.setWorkTime(eventEntity.getWorkTime());
		return dto;
	}

	private EventRequestDTO getEventRequestDTOOrThrow(ObjectRequestDTO obj) {
	    if (!(obj instanceof EventRequestDTO)) {
	        throw new IllegalArgumentException("Event object is not correct");
        }
	    return ((EventRequestDTO) obj);
    }

    private EventEntity getEventEntityOrThrow(ObjectEntity obj) {
        if (!(obj instanceof EventEntity)) {
            throw new IllegalArgumentException("Event entity is not correct");
        }
        return ((EventEntity) obj);
    }
}
