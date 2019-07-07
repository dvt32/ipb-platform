package com.ipb.platform.dto.responses;

import lombok.Data;

import java.util.List;

@Data
public class CityResponseDTO extends ObjectResponseDTO{

    private List<ObjectResponseDTO> landmarks;

    private List<ObjectResponseDTO> events;
}
