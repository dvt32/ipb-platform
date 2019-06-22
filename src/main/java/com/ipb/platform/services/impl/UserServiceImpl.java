package com.ipb.platform.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ipb.platform.dto.requests.UserRequestDTO;
import com.ipb.platform.dto.responses.UserResponseDTO;
import com.ipb.platform.mappers.UserMapper;
import com.ipb.platform.persistence.UserRepository;
import com.ipb.platform.persistence.entities.UserEntity;
import com.ipb.platform.persistence.entities.UserType;
import com.ipb.platform.security.PasswordResetToken;
import com.ipb.platform.security.PasswordResetTokenRepository;
import com.ipb.platform.services.UserService;
import com.ipb.platform.validation.EmailExistsException;
import com.ipb.platform.validation.UserNotFoundException;

import lombok.AllArgsConstructor;

/**
 * This class is responsible for the business logic
 * when dealing with users in the system 
 * (CRUD operations, sending reset password e-mails etc.)
 * 
 * @author dvt32
 */
@Service
@AllArgsConstructor
public class UserServiceImpl 
	implements UserService
{
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private PasswordResetTokenRepository tokenRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	/**
	 * Creates a new user from a request DTO, which contains the user's data.
	 */
	@Override
	public Long createNewUser(UserRequestDTO userRequestDTO) 
		throws EmailExistsException 
	{
		String userEmail = userRequestDTO.getEmail();
		if ( emailExists(userEmail) ) {
			throw new EmailExistsException("User with this e-mail address already exists!");
		}

		// Make sure the password is stored encrypted in the database
		String userPassword = userRequestDTO.getPassword();
		String encodedUserPassword = passwordEncoder.encode(userPassword);
		userRequestDTO.setPassword(encodedUserPassword);
		userRequestDTO.setMatchingPassword(encodedUserPassword);
			
		UserEntity userEntity = userMapper.toEntity(userRequestDTO);
		userEntity = userRepository.save(userEntity);
		return userEntity.getId();
	}
	
	/**
	 * Updates an existing user with a specified ID.
	 */
	@Override
	public void updateUserById(Long id, UserRequestDTO userRequestDTO) 
		throws UserNotFoundException 
	{
		if ( !userExists(id) ) {
			throw new UserNotFoundException("User with this ID does not exist!");
		}
		
		// User ID is auto-generated, so it should not be manually changed via the HTTP request body
		userRequestDTO.setId(id);
		
		// Make sure the password is stored encrypted in the database
		String userPassword = userRequestDTO.getPassword();
		String encodedUserPassword = passwordEncoder.encode(userPassword);
		userRequestDTO.setPassword(encodedUserPassword);
		userRequestDTO.setMatchingPassword(encodedUserPassword);
		
		UserEntity userEntity = userMapper.toEntity(userRequestDTO);
		userRepository.save(userEntity);
	}
	
	/**
	 * Updates an existing user with a specified e-mail.
	 */
	@Override
	public void updateUserByEmail(String email, UserRequestDTO userRequestDTO) 
		throws UserNotFoundException 
	{
		if ( !emailExists(email) ) {
			throw new UserNotFoundException("User with this e-mail does not exist!");
		}
		
		// User ID is auto-generated, so it should not be manually changed via the HTTP request body
		Long id = userRepository.findByEmail(email).get().getId();
		userRequestDTO.setId(id);
		
		// Make sure the password is stored encrypted in the database
		String userPassword = userRequestDTO.getPassword();
		String encodedUserPassword = passwordEncoder.encode(userPassword);
		userRequestDTO.setPassword(encodedUserPassword);
		userRequestDTO.setMatchingPassword(encodedUserPassword);
		
		UserEntity userEntity = userMapper.toEntity(userRequestDTO);
		userRepository.save(userEntity);
	}
	
	/**
	 * Deletes an existing user with a specified ID.
	 */
	@Override
	public void deleteUserById(Long id) 
		throws UserNotFoundException 
	{
		if ( !userExists(id) ) {
			throw new UserNotFoundException("User with this ID does not exist!");
		}
		
		userRepository.deleteById(id);
	}
	
	/**
	 * Deletes an existing user with a specified e-mail
	 */
	@Override
	public void deleteUserByEmail(String email) 
		throws UserNotFoundException 
	{
		if ( !emailExists(email) ) {
			throw new UserNotFoundException("User with this e-mail does not exist!");
		}
		
		userRepository.deleteByEmail(email);
	}

	/**
	 * Returns all existing users' data as a list of users.
	 */
	@Override
	public List<UserResponseDTO> getAll() {
		return userRepository.findAll().stream()
			.map(entity -> userMapper.toResponseDTO(entity))
			.collect(Collectors.toList());
	}

	/**
	 * Finds an existing user by a specified ID.
	 */
	@Override
	public UserResponseDTO findById(Long id) 
		throws UserNotFoundException 
	{
		if ( !userExists(id) ) {
			throw new UserNotFoundException("User with this ID does not exist!");
		}
		return userMapper.toResponseDTO( userRepository.findById(id).get() );
	}

	/**
	 * Finds an existing user by a specified e-mail address.
	 */
	@Override
	public UserResponseDTO findByEmail(String email) 
		throws UserNotFoundException 
	{
		if ( !emailExists(email) ) {
			throw new UserNotFoundException("User with this e-mail does not exist!");
		}
		return userMapper.toResponseDTO( userRepository.findByEmail(email).get() );
	}
	
	/**
	 * Converts a response DTO to a request DTO 
	 * (so that the response from a method can be used as a request for another one).
	 */
	@Override
	public UserRequestDTO convertResponseDtoToRequestDto(UserResponseDTO userResponseDTO) {
		return userMapper.toRequestDTO(userResponseDTO);
	}

	/**
	 * Checks if the passed password is equal to the user's actual password.
	 */
	@Override
	public boolean isCorrectPasswordForUser(UserRequestDTO userRequestDTO, String password) {
		String actualPassword = userRequestDTO.getPassword();
		return ( passwordEncoder.matches(password, actualPassword) );
	}

	/**
	 * Updates the user's password in the database
	 */
	@Override
	public void changePassword(UserRequestDTO userRequestDTO, String newPassword) {
		// if is valid password (not empty, six chars etc)
		String newEncodedPassword = passwordEncoder.encode(newPassword);
		userRequestDTO.setPassword(newEncodedPassword);
		userRequestDTO.setMatchingPassword(newEncodedPassword);
		UserEntity userEntity = userMapper.toEntity(userRequestDTO);
		userRepository.save(userEntity);
	}
	
	/**
	 * Checks if there is a user in the database with the specified e-mail.
	 * 
	 * @param email The e-mail to look up in the database
	 * @return true if there is a user with such an e-mail address, false otherwise
	 */
	private boolean emailExists(String email) {
		Optional<UserEntity> userWithThisEmail = userRepository.findByEmail(email);
		return userWithThisEmail.isPresent();
    }
	
	/**
	 * Checks if there is a user in the database with the specified ID.
	 * 
	 * @param id The id number to look up in the database
	 * @return true if there is a user with such an id, false otherwise
	 */
	private boolean userExists(Long id) {
		Optional<UserEntity> userWithThisId = userRepository.findById(id);
		return userWithThisId.isPresent();
    }

	/**
	 * Creates a random UUID string which serves as a password reset token and sends an e-mail
	 * with a password reset link, containing that reset token.
	 * 
	 * The link's URL is retrieved from a property defined in the application.properties file.
	 * 
	 * The method has an @Async annotation because sending the e-mail is an asynchronous operation.
	 */
	@Async
	public void createResetPasswordTokenAndSendEmail(String userEmailAddress) 
		throws UserNotFoundException 
	{
		if ( !emailExists(userEmailAddress) ) {
			throw new UserNotFoundException("User with this e-mail address does not exist!");
		}
		
		String resetToken = UUID.randomUUID().toString();
		UserEntity user = userRepository.findByEmail(userEmailAddress).get();
		createPasswordResetTokenForUser(user, resetToken);
		
		String appUrl = environment.getProperty("ipb.platform.url");
		
		SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
		passwordResetEmail.setTo(userEmailAddress);
		passwordResetEmail.setSubject("IPB Password Reset Request");
		passwordResetEmail.setText(
			"To reset your password, click the link below: \n" 
			+ appUrl + "/users/reset-password?token=" + resetToken
		);
		
		mailSender.send(passwordResetEmail);
	}

	/**
	 * Creates a password reset token object 
	 * from a specified token string (UUID string) 
	 * and an existing user.
	 */
	@Override
	public PasswordResetToken createPasswordResetTokenForUser(UserEntity user, String token) {
		PasswordResetToken myToken = new PasswordResetToken();
		myToken.setUser(user);
		myToken.setToken(token);
	    return tokenRepository.save(myToken);
	}

	/**
	 * Checks if there's an existing token with a specified token string.
	 */
	@Override
	public boolean isValidPasswordResetToken(String token) {
		return tokenRepository.existsByToken(token);
	}

	/**
	 * Changes the password of an existing user with a valid password reset token.
	 * 
	 * @param token A token string
	 * @param newPassword The user's desired new password.
	 */
	@Override
	public void changePasswordByToken(String token, String newPassword) 
		throws UserNotFoundException 
	{
		PasswordResetToken resetToken = tokenRepository.findByToken(token);
		UserEntity user = resetToken.getUser();

		if (user == null || !userExists(user.getId()) ) {
			throw new UserNotFoundException("User for this reset token is null or does not exist in database!");
		}
		
		String newEncodedPassword = passwordEncoder.encode(newPassword);
		user.setPassword(newEncodedPassword);
		user.setMatchingPassword(newEncodedPassword);
		userRepository.save(user);
		
		// Remove token after it has been used
		tokenRepository.delete(resetToken);
	}

	/**
	 * Changes a user's privileges by passing in the user's e-mail and his new role.
	 */
	@Override
	public void setUserRoleByEmail(String userEmail, UserType role) throws 
		UserNotFoundException 
	{
		if ( !emailExists(userEmail) ) {
			throw new UserNotFoundException("User with this e-mail does not exist!");
		}
		
		UserEntity user = userRepository.findByEmail(userEmail).get();
		user.setType(role);
		userRepository.save(user);
	}
	
	/**
	 * Changes a user's privileges by passing in the user's ID and his new role.
	 */
	@Override
	public void setUserRoleById(Long userId, UserType role) throws 
		UserNotFoundException 
	{
		if ( !userExists(userId) ) {
			throw new UserNotFoundException("User with this ID does not exist!");
		}
		
		UserEntity user = userRepository.findById(userId).get();
		user.setType(role);
		userRepository.save(user);
	}

	/**
	 * Checks if a password is valid (not null, not blank and with a minimum length of 6)
	 */
	@Override
	public boolean isValidPassword(String password) {
		return ( 
			password != null && 
			!password.trim().isEmpty() && 
			password.length() >= 6 
		);
	}

}