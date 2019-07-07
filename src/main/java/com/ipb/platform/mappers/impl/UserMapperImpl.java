package com.ipb.platform.mappers.impl;

import com.ipb.platform.dto.responses.CategoryResponseDTO;
import com.ipb.platform.mappers.CategoryMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.UserRequestDTO;
import com.ipb.platform.dto.responses.UserResponseDTO;
import com.ipb.platform.mappers.UserMapper;
import com.ipb.platform.persistence.entities.UserEntity;

import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class implements the user mapper interface. 
 * It is used to convert entity objects to DTOs (and vice-versa).
 * 
 * @author dvt32
 */
@Service
@NoArgsConstructor
public class UserMapperImpl 
	implements UserMapper 
{
	@Autowired
	private CategoryMapping categoryMapper;

	@Override
	public UserEntity toEntity(UserRequestDTO userRequestDTO) {
		UserEntity entity = new UserEntity(); 
		
		entity.setId( userRequestDTO.getId() );
		entity.setEmail( userRequestDTO.getEmail() );
		entity.setPassword( userRequestDTO.getPassword() );
		entity.setMatchingPassword( userRequestDTO.getMatchingPassword() );
		entity.setFirstName( userRequestDTO.getFirstName() );
		entity.setLastName( userRequestDTO.getLastName() );
		entity.setBirthday( userRequestDTO.getBirthday() );
		entity.setType( userRequestDTO.getType() );

		return entity;
	}

	@Override
	public UserResponseDTO toResponseDTO(UserEntity userEntity) {
		UserResponseDTO dto = new UserResponseDTO();
		
		dto.setId( userEntity.getId() );
		dto.setEmail( userEntity.getEmail() );
		dto.setPassword( userEntity.getPassword() );
		dto.setMatchingPassword( userEntity.getMatchingPassword() );
		dto.setFirstName( userEntity.getFirstName() );
		dto.setLastName( userEntity.getLastName() );
		dto.setBirthday( userEntity.getBirthday() );
		dto.setType( userEntity.getType() );

		if(userEntity.getCategories() != null) {
			List<CategoryResponseDTO> categories = userEntity.getCategories().stream()
					.map(catEntity -> this.categoryMapper.toDTO(catEntity))
					.collect(Collectors.toList());

			dto.setCategories(categories);
		}
		return dto;
	}

	@Override
	public UserRequestDTO toRequestDTO(UserResponseDTO userResponseDTO) {
		UserRequestDTO dto = new UserRequestDTO();
		
		dto.setId( userResponseDTO.getId() );
		dto.setEmail( userResponseDTO.getEmail() );
		dto.setPassword( userResponseDTO.getPassword() );
		dto.setMatchingPassword( userResponseDTO.getMatchingPassword() );
		dto.setFirstName( userResponseDTO.getFirstName() );
		dto.setLastName( userResponseDTO.getLastName() );
		dto.setBirthday( userResponseDTO.getBirthday() );
		dto.setType( userResponseDTO.getType() );
		
		return dto;
	}
	
}
