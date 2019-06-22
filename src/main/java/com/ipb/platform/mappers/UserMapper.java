package com.ipb.platform.mappers;

import com.ipb.platform.dto.requests.UserRequestDTO;
import com.ipb.platform.dto.responses.UserResponseDTO;
import com.ipb.platform.persistence.entities.UserEntity;

/**
 * This interface specifies the method signatures for a user mapper implementation.
 * The purpose of the mapper implementation is to convert entity objects to DTOs (and vice-versa).
 * 
 * @author dvt32
 */
public interface UserMapper {
	UserEntity toEntity(UserRequestDTO userRequestDTO);
	UserResponseDTO toResponseDTO(UserEntity userEntity);
	UserRequestDTO toRequestDTO(UserResponseDTO userResponseDTO);
}