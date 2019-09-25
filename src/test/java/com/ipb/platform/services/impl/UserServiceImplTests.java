package com.ipb.platform.services.impl;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ipb.platform.dto.requests.UserRequestDTO;
import com.ipb.platform.dto.responses.UserResponseDTO;
import com.ipb.platform.persistence.UserRepository;
import com.ipb.platform.persistence.entities.UserEntity;
import com.ipb.platform.persistence.entities.UserType;
import com.ipb.platform.security.PasswordResetToken;
import com.ipb.platform.security.PasswordResetTokenRepository;
import com.ipb.platform.services.UserService;
import com.ipb.platform.validation.EmailExistsException;
import com.ipb.platform.validation.UserNotFoundException;

/**
 * This class implements unit tests for the UserServiceImpl class.
 * 
 * The @Transactional annotation is used to rollback 
 * database changes after a test has finished executing.
 * 
 * @author dvt32
 */
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class UserServiceImplTests {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private PasswordResetTokenRepository tokenRepository;
	
	/**
	 * Helper methods
	 */
	
	public UserRequestDTO getTestUserRequestDTO() {
		UserRequestDTO testUserRequestDTO = new UserRequestDTO();
		testUserRequestDTO.setEmail("test_user@ipb.com");
		testUserRequestDTO.setPassword("123456");
		testUserRequestDTO.setMatchingPassword("123456");
		testUserRequestDTO.setFirstName("Ivan");
		testUserRequestDTO.setLastName("Ivanov");
		testUserRequestDTO.setBirthday( new java.sql.Date(123456789) );
		testUserRequestDTO.setType(UserType.USER);
    	return testUserRequestDTO;
	}
	
	/**
	 * createNewUser() tests
	 */
	
	@Test
	public void createNewUserMethodShouldCreateNewUserAndPasswordShouldBeEncoded() 
		throws EmailExistsException 
	{
		UserRequestDTO userRequestDTO = getTestUserRequestDTO();
		String dtoPassword = userRequestDTO.getPassword();
		
		Long userId = userService.createNewUser(userRequestDTO);
		assertTrue( userRepository.existsById(userId) );
		
		String entityPassword = userRepository.findById(userId).get().getPassword();
		assertTrue( !dtoPassword.equals(entityPassword) );
		assertTrue( passwordEncoder.matches( dtoPassword, entityPassword ) );
	}
	
	@Test(expected = EmailExistsException.class)
	public void createNewUserMethodShouldThrowEmailExistsException() 
		throws EmailExistsException 
	{
		UserRequestDTO userRequestDTO = getTestUserRequestDTO();
		userService.createNewUser(userRequestDTO);
		UserRequestDTO anotherUserRequestDTO = getTestUserRequestDTO();
		userService.createNewUser(anotherUserRequestDTO);
	}
	
	/**
	 * updateUserById() tests
	 */
	
	@Test
	public void updateUserByIdMethodShouldUpdateExistingUserData() 
		throws UserNotFoundException 
	{
		// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"1234567", 
    			"1234567", 
    			"Petar", 
    			"Petrov",
    			new java.sql.Date(0), 
    			UserType.ADMIN
    		)
    	);
    	
    	Long testUserId = testUser.getId();
		UserRequestDTO userRequestDTO = getTestUserRequestDTO();
		userService.updateUserById(testUserId, userRequestDTO);
		
		// Test if the user's data has been updated
		UserEntity retrievedUser = userRepository.findById(testUserId).get();
		assertTrue( retrievedUser.getEmail().equals("test_user@ipb.com") );
		assertTrue( passwordEncoder.matches("123456", retrievedUser.getPassword()) );
		assertTrue( passwordEncoder.matches("123456", retrievedUser.getMatchingPassword()) );
		assertTrue( retrievedUser.getFirstName().equals("Ivan") );
		assertTrue( retrievedUser.getLastName().equals("Ivanov") );
		assertTrue( retrievedUser.getType() == UserType.USER );
	}
	
	@Test(expected = UserNotFoundException.class)
	public void updateUserByIdMethodShouldThrowUserNotFoundException() 
		throws UserNotFoundException 
	{
		UserRequestDTO userRequestDTO = getTestUserRequestDTO();
		userService.updateUserById(1L, userRequestDTO);
	}
	
	@Test
	public void updateUserByIdMethodShouldUpdateExistingUserDataAndIdShouldNotBeChanged() 
		throws UserNotFoundException 
	{
		// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"1234567", 
    			"1234567", 
    			"Petar", 
    			"Petrov",
    			new java.sql.Date(0), 
    			UserType.ADMIN
    		)
    	);
    	
    	Long testUserId = testUser.getId();
		UserRequestDTO userRequestDTO = getTestUserRequestDTO();
		userRequestDTO.setId(1337L);
		userService.updateUserById(testUserId, userRequestDTO);
		
		// Test if the user's ID has been updated
		assertTrue( !userRepository.existsById(1337L) );
		assertTrue( userRepository.existsById(testUserId) );
		UserEntity retrievedUser = userRepository.findById(testUserId).get();
		assertTrue( retrievedUser.getId() == testUserId );
	}
	
	/**
	 * updateUserByEmail() tests
	 */
	
	@Test
	public void updateUserByEmailMethodShouldUpdateExistingUserData() 
		throws UserNotFoundException 
	{
		// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"1234567", 
    			"1234567", 
    			"Petar", 
    			"Petrov",
    			new java.sql.Date(0), 
    			UserType.ADMIN
    		)
    	);
    	
    	String testUserEmail = testUser.getEmail();
		UserRequestDTO userRequestDTO = getTestUserRequestDTO();
		userService.updateUserByEmail(testUserEmail, userRequestDTO);
		
		// Test if the user's data has been updated
		assertTrue( !userRepository.existsByEmail(testUserEmail) );
		UserEntity retrievedUser = userRepository.findByEmail("test_user@ipb.com").get();
		assertTrue( passwordEncoder.matches("123456", retrievedUser.getPassword()) );
		assertTrue( passwordEncoder.matches("123456", retrievedUser.getMatchingPassword()) );
		assertTrue( retrievedUser.getFirstName().equals("Ivan") );
		assertTrue( retrievedUser.getLastName().equals("Ivanov") );
		assertTrue( retrievedUser.getType() == UserType.USER );
	}
	
	@Test(expected = UserNotFoundException.class)
	public void updateUserByEmailMethodShouldThrowUserNotFoundException() 
		throws UserNotFoundException 
	{
		UserRequestDTO userRequestDTO = getTestUserRequestDTO();
		userService.updateUserByEmail("test_user@ipb.com", userRequestDTO);
	}
	
	@Test
	public void updateUserByEmailMethodShouldUpdateExistingUserDataAndIdShouldNotBeChanged() 
		throws UserNotFoundException 
	{
		// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"1234567", 
    			"1234567", 
    			"Petar", 
    			"Petrov",
    			new java.sql.Date(0), 
    			UserType.ADMIN
    		)
    	);
    	
    	Long testUserId = testUser.getId();
    	String testUserEmail = testUser.getEmail();
		UserRequestDTO userRequestDTO = getTestUserRequestDTO();
		userRequestDTO.setId(1337L);
		userService.updateUserByEmail(testUserEmail, userRequestDTO);
		
		// Test if the user's ID has been updated
		assertTrue( !userRepository.existsById(1337L) );
		assertTrue( userRepository.existsById(testUserId) );
		UserEntity retrievedUser = userRepository.findByEmail("test_user@ipb.com").get();
		assertTrue( retrievedUser.getId() == testUserId );
	}
	
	/**
	 * deleteUserById() tests
	 */
	
	@Test
	public void deleteUserByIdMethodShouldDeleteExistingUser() 
		throws UserNotFoundException 
	{
		// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"1234567", 
    			"1234567", 
    			"Petar", 
    			"Petrov",
    			new java.sql.Date(0), 
    			UserType.ADMIN
    		)
    	);
    	
    	Long testUserId = testUser.getId();
		userService.deleteUserById(testUserId);
		
		// Test if the user has been deleted
		assertTrue( !userRepository.existsById(testUserId) );
	}
	
	@Test(expected = UserNotFoundException.class)
	public void deleteUserByIdMethodShouldThrowUserNotFoundException() 
		throws UserNotFoundException 
	{
		userService.deleteUserById(1L);
	}
	
	/**
	 * deleteUserByEmail() tests
	 */
	
	@Test
	public void deleteUserByEmailMethodShouldDeleteExistingUser() 
		throws UserNotFoundException 
	{
		// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"1234567", 
    			"1234567", 
    			"Petar", 
    			"Petrov",
    			new java.sql.Date(0), 
    			UserType.ADMIN
    		)
    	);
    	
    	String testUserEmail = testUser.getEmail();
		userService.deleteUserByEmail(testUserEmail);
		
		// Test if the user has been deleted
		assertTrue( !userRepository.existsByEmail(testUserEmail) );
	}
	
	@Test(expected = UserNotFoundException.class)
	public void deleteUserByEmailMethodShouldThrowUserNotFoundException() 
		throws UserNotFoundException 
	{
		userService.deleteUserByEmail("test@ipb.com");
	}
	
	/**
	 * getAll() tests
	 */
	
	@Test
	public void getAllMethodShouldReturnInsertedUsers() {
		// Insert test users
    	UserEntity firstTestUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test_user1@ipb.com", 
    			"1234567", 
    			"1234567", 
    			"Petar", 
    			"Petrov",
    			new java.sql.Date(0), 
    			UserType.ADMIN
    		)
    	);
    	UserEntity secondTestUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test_user2@ipb.com", 
    			"123456", 
    			"123456", 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	List<UserResponseDTO> users = userService.getAll();
    	String usersListToString = users.toString();
		
		// Test if the users' data has been retrieved
		assertTrue( users.size() >= 2 ); // may be greater than 2 if there are users pre-added in the database
		assertTrue( usersListToString.contains("test_user1@ipb.com") );
		assertTrue( usersListToString.contains("test_user2@ipb.com") );
	}
	
	/**
	 * findById() tests
	 */
	
	@Test
	public void findByIdShouldReturnInsertedUser() 
		throws UserNotFoundException 
	{
		// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test_user@ipb.com", 
    			"1234567", 
    			"1234567", 
    			"Petar", 
    			"Petrov",
    			new java.sql.Date(0), 
    			UserType.ADMIN
    		)
    	);
    	
    	Long testUserId = testUser.getId();
    	UserResponseDTO userResponseDTO = userService.findById(testUserId);
		assertTrue( userResponseDTO.getEmail().equals("test_user@ipb.com") );
		assertTrue( userResponseDTO.getFirstName().equals("Petar") );
		assertTrue( userResponseDTO.getLastName().equals("Petrov") );
	}
	
	@Test(expected = UserNotFoundException.class)
	public void findByIdShouldThrowUserNotFoundException() 
		throws UserNotFoundException 
	{
		userService.findById(1L);
	}
	
	/**
	 * findByEmail() tests
	 */
	
	@Test
	public void findByEmailShouldReturnInsertedUser() 
		throws UserNotFoundException 
	{
		// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test_user@ipb.com", 
    			"1234567", 
    			"1234567", 
    			"Petar", 
    			"Petrov",
    			new java.sql.Date(0), 
    			UserType.ADMIN
    		)
    	);
    	
    	Long testUserId = testUser.getId();
    	String testUserEmail = testUser.getEmail();
    	
    	UserResponseDTO userResponseDTO = userService.findByEmail(testUserEmail);
    	assertTrue( userResponseDTO.getId() == testUserId );
		assertTrue( userResponseDTO.getEmail().equals("test_user@ipb.com") );
		assertTrue( userResponseDTO.getFirstName().equals("Petar") );
		assertTrue( userResponseDTO.getLastName().equals("Petrov") );
	}
	
	@Test(expected = UserNotFoundException.class)
	public void findByEmailShouldThrowUserNotFoundException() 
		throws UserNotFoundException 
	{
		userService.findByEmail("test_user@ipb.com");
	}
	
	/**
	 * isCorrectPasswordForUser() tests
	 */
	
	@Test
	public void isCorrectPasswordForUserMethodShouldReturnTrue() {
		UserRequestDTO user = getTestUserRequestDTO();
		
		String userPassword = user.getPassword();
		String encodedUserPassword = passwordEncoder.encode(userPassword);
		user.setPassword(encodedUserPassword);
		user.setMatchingPassword(encodedUserPassword);
		
		assertTrue( userService.isCorrectPasswordForUser(user, userPassword) );
	}
	
	@Test
	public void isCorrectPasswordForUserMethodShouldReturnFalse() {
		UserRequestDTO user = getTestUserRequestDTO();
		
		String userPassword = user.getPassword();
		String encodedUserPassword = passwordEncoder.encode(userPassword);
		user.setPassword(encodedUserPassword);
		user.setMatchingPassword(encodedUserPassword);
		
		assertTrue( !userService.isCorrectPasswordForUser(user, "wrong password") );
	}
	
	/**
	 * changePassword() tests
	 */
	
	@Test
	public void changePasswordMethodShouldUpdateExistingUserPassword() {
		// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test_user@ipb.com", 
    			"1234567", 
    			"1234567", 
    			"Petar", 
    			"Petrov",
    			new java.sql.Date(0), 
    			UserType.ADMIN
    		)
    	);
    	
    	Long testUserId = testUser.getId();
    	
    	UserRequestDTO userRequestDTO = new UserRequestDTO(
    		testUserId,
    		testUser.getEmail(),
    		testUser.getPassword(),
    		testUser.getMatchingPassword(),
    		testUser.getFirstName(),
    		testUser.getLastName(),
    		testUser.getBirthday(),
    		testUser.getType()
    	);
    	String newValidPassword = "qwerty";
    	
    	userService.changePassword(userRequestDTO, newValidPassword);
    	
    	UserEntity retrievedUser = userRepository.findById(testUserId).get();
    	assertTrue( passwordEncoder.matches(newValidPassword, retrievedUser.getPassword()) );
	}
	
	/**
	 * createResetPasswordTokenAndSendEmail() tests
	 */
	
	@Test(expected = UserNotFoundException.class)
	public void createResetPasswordTokenAndSendEmailMethodShouldThrowUserNotFoundException() 
		throws UserNotFoundException 
	{
		userService.createResetPasswordTokenAndSendEmail("test@ipb.com");
	}
	
	@Test
	public void createResetPasswordTokenAndSendEmailMethodShouldCreateTokenAndThrowNoExceptions() 
		throws UserNotFoundException 
	{
		// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"1234567", 
    			"1234567", 
    			"Petar", 
    			"Petrov",
    			new java.sql.Date(0), 
    			UserType.ADMIN
    		)
    	);
    	
		userService.createResetPasswordTokenAndSendEmail("test@ipb.com");
		assertTrue( tokenRepository.count() == 1 );
		assertTrue( tokenRepository.findAll().get(0).getUser() == testUser );
	}
	
	/**
	 * createPasswordResetTokenForUser() tests
	 */
	
	@Test
	public void createPasswordResetTokenForUserMethodShouldCreateToken() {
		UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"1234567", 
    			"1234567", 
    			"Petar", 
    			"Petrov",
    			new java.sql.Date(0), 
    			UserType.ADMIN
    		)
    	);
		String tokenString = UUID.randomUUID().toString();
		
		userService.createPasswordResetTokenForUser(testUser, tokenString);
		assertTrue( tokenRepository.count() == 1 );
		assertTrue( tokenRepository.findAll().get(0).getUser() == testUser );
		assertTrue( tokenRepository.findAll().get(0).getToken().equals(tokenString) );
	}
	
	/**
	 * changePasswordByToken() tests
	 */
	
	@Test
	public void changePasswordByTokenMethodShouldUpdateExistingUserPassword() {
		UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"1234567", 
    			"1234567", 
    			"Petar", 
    			"Petrov",
    			new java.sql.Date(0), 
    			UserType.ADMIN
    		)
    	);
		String tokenString = UUID.randomUUID().toString();
		
		// Insert test reset token
		PasswordResetToken resetToken = new PasswordResetToken();
		resetToken.setUser(testUser);
		resetToken.setToken(tokenString);
		tokenRepository.save(resetToken);
		
		String newValidPassword = "qwerty";
		userService.changePasswordByToken(tokenString, newValidPassword);
	
		// Test if the token was removed and if the password was successfully changed
		assertTrue( tokenRepository.count() == 0 );
		Long testUserId = testUser.getId();
		UserEntity retrievedUser = userRepository.findById(testUserId).get();
		assertTrue( passwordEncoder.matches(newValidPassword, retrievedUser.getPassword()) );
	}
	
	@Test(expected = NullPointerException.class)
	public void changePasswordByTokenMethodShouldThrowNullPointerExceptionBecauseOfInvalidToken() {
		String newValidPassword = "qwerty";
		userService.changePasswordByToken("invalid token", newValidPassword);
	}
	
	@Test(expected = NullPointerException.class)
	public void changePasswordByTokenMethodShouldThrowNullPointerExceptionBecauseOfInvalidUser() {
		// Insert test reset token
		String tokenString = UUID.randomUUID().toString();
		PasswordResetToken resetToken = new PasswordResetToken();
		resetToken.setToken(tokenString);
		tokenRepository.save(resetToken);
		
		String newValidPassword = "qwerty";
		userService.changePasswordByToken(tokenString, newValidPassword);
	}
	
	/**
	 * setUserRoleByEmail() tests
	 */
	
	@Test(expected = UserNotFoundException.class)
	public void setUserRoleByEmailMethodShouldThrowUserNotFoundException() 
		throws UserNotFoundException 
	{
		userService.setUserRoleByEmail("test@ipb.com", UserType.ADMIN);
	}
	
	@Test
	public void setUserRoleByEmailMethodShouldMakeExistingUserAdmin() 
		throws UserNotFoundException 
	{
		UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"1234567", 
    			"1234567", 
    			"Petar", 
    			"Petrov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
		Long testUserId = testUser.getId();
		
		userService.setUserRoleByEmail("test@ipb.com", UserType.ADMIN);
	
		UserEntity retrievedUser = userRepository.findById(testUserId).get();
		assertTrue( retrievedUser.getType() == UserType.ADMIN );
	}
	
	@Test
	public void setUserRoleByEmailMethodShouldMakeExistingUserNonAdmin() 
		throws UserNotFoundException 
	{
		UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"1234567", 
    			"1234567", 
    			"Petar", 
    			"Petrov",
    			new java.sql.Date(0), 
    			UserType.ADMIN
    		)
    	);
		Long testUserId = testUser.getId();
		
		userService.setUserRoleByEmail("test@ipb.com", UserType.USER);
	
		UserEntity retrievedUser = userRepository.findById(testUserId).get();
		assertTrue( retrievedUser.getType() == UserType.USER );
	}
	
	/**
	 * setUserRoleById() tests
	 */
	
	@Test(expected = UserNotFoundException.class)
	public void setUserRoleByIdMethodShouldThrowUserNotFoundException() 
		throws UserNotFoundException 
	{
		userService.setUserRoleById(1L, UserType.ADMIN);
	}
	
	@Test
	public void setUserRoleByIdMethodShouldMakeExistingUserAdmin() 
		throws UserNotFoundException 
	{
		UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"1234567", 
    			"1234567", 
    			"Petar", 
    			"Petrov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
		Long testUserId = testUser.getId();
		
		userService.setUserRoleById(testUserId, UserType.ADMIN);
	
		UserEntity retrievedUser = userRepository.findById(testUserId).get();
		assertTrue( retrievedUser.getType() == UserType.ADMIN );
	}
	
	@Test
	public void setUserRoleByIdMethodShouldMakeExistingUserNonAdmin() 
		throws UserNotFoundException 
	{
		UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"1234567", 
    			"1234567", 
    			"Petar", 
    			"Petrov",
    			new java.sql.Date(0), 
    			UserType.ADMIN
    		)
    	);
		Long testUserId = testUser.getId();
		
		userService.setUserRoleById(testUserId, UserType.USER);
	
		UserEntity retrievedUser = userRepository.findById(testUserId).get();
		assertTrue( retrievedUser.getType() == UserType.USER );
	}
	
	/**
	 * isValidPassword() tests
	 */
	
	@Test
	public void isValidPasswordMethodShouldReturnTrue() {
		assertTrue( userService.isValidPassword("123456") );
	}
	
	@Test
	public void isValidPasswordMethodShouldReturnFalseBecausePasswordIsTooShort() {
		assertTrue( !userService.isValidPassword("12345") );
	}
	
	@Test
	public void isValidPasswordMethodShouldReturnFalseBecausePasswordIsNull() {
		assertTrue( !userService.isValidPassword(null) );
	}
	
	@Test
	public void isValidPasswordMethodShouldReturnFalseBecausePasswordIsTooShortWhenTrimmed() {
		assertTrue( !userService.isValidPassword("12345 ") );
	}
	
}