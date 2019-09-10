package com.ipb.platform.dto.responses;

import com.ipb.platform.persistance.entities.ObjectType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class CityResponseDTO extends ObjectResponseDTO{

    private List<ObjectResponseDTO> landmarks;

    private List<ObjectResponseDTO> events;
}
