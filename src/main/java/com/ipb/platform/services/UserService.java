package com.ipb.platform.services;

import java.util.List;

import com.ipb.platform.dto.requests.UserRequestDTO;
import com.ipb.platform.dto.responses.UserResponseDTO;
import com.ipb.platform.persistence.entities.UserEntity;
import com.ipb.platform.persistence.entities.UserType;
import com.ipb.platform.security.PasswordResetToken;
import com.ipb.platform.validation.EmailExistsException;
import com.ipb.platform.validation.UserNotFoundException;

/**
 * This interface specifies the list of operations allowed on users.
 * 
 * @author dvt32
 */
public interface UserService {
	Long createNewUser(UserRequestDTO user) throws EmailExistsException;
	
	void updateUserById(Long id, UserRequestDTO userRequestDTO) throws UserNotFoundException;
	
	void updateUserByEmail(String email, UserRequestDTO userRequestDTO) throws UserNotFoundException;
	
	void deleteUserById(Long id) throws UserNotFoundException;
	
	void deleteUserByEmail(String email) throws UserNotFoundException;
	
	List<UserResponseDTO> getAll();
	
	UserResponseDTO findById(Long id) throws UserNotFoundException;
	
	UserResponseDTO findByEmail(String email) throws UserNotFoundException;
	
	boolean isCorrectPasswordForUser(UserRequestDTO user, String password);
	
	void changePassword(UserRequestDTO user, String newPassword);
	
	UserRequestDTO convertResponseDtoToRequestDto(UserResponseDTO userResponseDTO);
	
	void createResetPasswordTokenAndSendEmail(String emailAddress) throws UserNotFoundException;
	
	PasswordResetToken createPasswordResetTokenForUser(UserEntity user, String token);
	
	boolean isValidPasswordResetToken(String token);
	
	void changePasswordByToken(String token, String newPassword) throws UserNotFoundException;
	
	void setUserRoleByEmail(String userEmail, UserType role) throws UserNotFoundException;
	
	void setUserRoleById(Long userId, UserType role) throws UserNotFoundException;
	
	boolean isValidPassword(String password);
}