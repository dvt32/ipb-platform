package com.ipb.platform.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.ipb.platform.persistence.UserRepository;
import com.ipb.platform.persistence.entities.UserEntity;
import com.ipb.platform.persistence.entities.UserType;

/**
 * This class implements integration tests for the UserProfileController class.
 * 
 * The @Transactional annotation is used to rollback 
 * database changes (made via a MockMvc request)
 * after the annotated test has finished executing.
 * 
 * @author dvt32
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserPlatformControllerTests {

	@Autowired
    private UserProfileController userProfileController;
	
	@Autowired
	private WebApplicationContext context;
	
	private MockMvc mockMvc;
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	/**
	 * Set context & configure Spring Security 
	 * for testing secured methods / REST endpoints.
	 */
	@Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
          .webAppContextSetup(context)
          .apply(springSecurity())
          .build();
    }
	
	/**
     * Smoke test (tests if the controller loads properly)
     */
    @Test
    public void controllerShouldNotBeNull() 
    	throws Exception 
    {
        assertThat(userProfileController).isNotNull();
    }
    
    /**
     * changePassword() tests
     * (POST "/profile/change-password" with request params 
     * "oldPassword", "newPassword", "matchingNewPassword")
     */

    @Test
    @Transactional
    public void changePasswordMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform( 
        	post("/profile/change-password")
        		.param("oldPassword", "123456")
        		.param("newPassword", "123456789")
        		.param("matchingNewPassword", "123456789")
        ).andExpect( status().isUnauthorized() );
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void changePasswordMethodShouldReturnNotFoundStatusCodeBecauseOfMissingUser() throws Exception {
        MvcResult result = this.mockMvc.perform( 
        	post("/profile/change-password")
        		.param("oldPassword", "123456")
        		.param("newPassword", "123456789")
        		.param("matchingNewPassword", "123456789")
        	)
        	.andExpect( status().isNotFound() )
        	.andReturn();
        
        // Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("User with this e-mail does not exist!");
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "USER", username="test@ipb.com")
    public void changePasswordMethodShouldReturnOKStatusCode() throws Exception {
    	// Insert test user with the logged in mock user's data
    	String encodedPassword = passwordEncoder.encode("123456");
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", // must match username in @WithMockUser annotation
    			/*
	    			NOTE: Passwords are stored encrypted in the database 
	    			and when attempting to update a user's password via the UserService, 
	    			the service compares the passed old password to its encoded equivalent.
	    			That's why we need the test user's password to be encoded.
    			*/
    			encodedPassword,
    			encodedPassword, 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	// Test if the response code is 200 OK
        MvcResult result = this.mockMvc.perform( 
        	post("/profile/change-password")
        		.param("oldPassword", "123456")
        		.param("newPassword", "123456789")
        		.param("matchingNewPassword", "123456789")
        	)
        	.andExpect( status().isOk() )
        	.andReturn();
        
        // Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Successfully updated password!");
    	
    	// Test if the user's info has been updated
    	Long testUserId = testUser.getId();
    	String testUserEncryptedPassword = userRepository.findById(testUserId).get().getPassword();
    	assertTrue( passwordEncoder.matches("123456789", testUserEncryptedPassword) );
    	
    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "USER", username="test@ipb.com")
    public void changePasswordMethodShouldReturnBadRequestStatusCodeBecauseOfMismatchingPassword() throws Exception {
    	// Insert test user with the logged in mock user's data
    	String encodedPassword = passwordEncoder.encode("123456");
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", // must match username in @WithMockUser annotation
    			/*
	    			NOTE: Passwords are stored encrypted in the database 
	    			and when attempting to update a user's password via the UserService, 
	    			the service compares the passed old password to its encoded equivalent.
	    			That's why we need the test user's password to be encoded.
    			*/
    			encodedPassword,
    			encodedPassword, 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	// Test if the response code is 400 BAD REQUEST
        MvcResult result = this.mockMvc.perform( 
        	post("/profile/change-password")
        		.param("oldPassword", "123456")
        		.param("newPassword", "12345678")
        		.param("matchingNewPassword", "123456789") // mismatching password
        	)
        	.andExpect( status().isBadRequest() )
        	.andReturn();
        
        // Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("New passwords don't match!");
    	
    	// Test if the user's info has been updated
    	Long testUserId = testUser.getId();
    	String testUserEncryptedPassword = userRepository.findById(testUserId).get().getPassword();
    	assertTrue( passwordEncoder.matches("123456", testUserEncryptedPassword) );
    	
    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "USER", username="test@ipb.com")
    public void changePasswordMethodShouldReturnBadRequestStatusCodeBecauseOfInvalidNewPassword() throws Exception {
    	// Insert test user with the logged in mock user's data
    	String encodedPassword = passwordEncoder.encode("123456");
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", // must match username in @WithMockUser annotation
    			/*
	    			NOTE: Passwords are stored encrypted in the database 
	    			and when attempting to update a user's password via the UserService, 
	    			the service compares the passed old password to its encoded equivalent.
	    			That's why we need the test user's password to be encoded.
    			*/
    			encodedPassword,
    			encodedPassword, 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	// Test if the response code is 400 BAD REQUEST
        MvcResult result = this.mockMvc.perform( 
        	post("/profile/change-password")
        		.param("oldPassword", "123456")
        		.param("newPassword", "123") // invalid new password
        		.param("matchingNewPassword", "123") // invalid new password
        	)
        	.andExpect( status().isBadRequest() )
        	.andReturn();
        
        // Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Invalid new password!");
    	
    	// Test if the user's info has been updated
    	Long testUserId = testUser.getId();
    	String testUserEncryptedPassword = userRepository.findById(testUserId).get().getPassword();
    	assertTrue( passwordEncoder.matches("123456", testUserEncryptedPassword) );
    	
    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }
    
    /**
     * getUserData() tests
     * (GET "/profile/get-data")
     */
    
    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void getUserDataMethodShouldReturnNotFoundStatusCodeBecauseOfMissingUser() throws Exception {
        MvcResult result = this.mockMvc
        	.perform( get("/profile/get-data") )
        	.andExpect( status().isNotFound() )
        	.andReturn();
        
        // Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("User with this e-mail does not exist!");
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "USER", username="test@ipb.com")
    public void getUserDataMethodShouldReturnOKStatusCode() throws Exception {
    	// Insert test user with the logged in mock user's data
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", // must match username in @WithMockUser annotation
    			"123456",
    			"123456",
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	// Test if the response code is 200 OK
        MvcResult result = this.mockMvc
        	.perform( get("/profile/get-data") )
        	.andExpect( status().isOk() )
        	.andReturn();
        
        // Test if the response string is the expected JSON object
    	String resultString = result.getResponse().getContentAsString();
    	JSONObject output = new JSONObject(resultString);
    	assertTrue( output.get("email").equals("test@ipb.com") );
    	assertTrue( output.get("password").equals("") ); // password not retrieved for security reasons
    	assertTrue( output.get("matchingPassword").equals("") );
    	assertTrue( output.get("firstName").equals("Ivan") );
    	assertTrue( output.get("lastName").equals("Ivanov") );
    	assertTrue( output.get("type").equals("USER") );
    	
    	// Test if the user's info matches the output
    	Long testUserId = testUser.getId();
    	UserEntity retrievedTestUser = userRepository.findById(testUserId).get();
    	assertTrue( retrievedTestUser.getEmail().equals(output.get("email")) );
    	assertTrue( retrievedTestUser.getPassword().equals("123456") );
    	assertTrue( retrievedTestUser.getMatchingPassword().equals("123456") );
    	assertTrue( retrievedTestUser.getFirstName().equals(output.get("firstName")) );
    	assertTrue( retrievedTestUser.getLastName().equals(output.get("lastName")) );
    	assertTrue( retrievedTestUser.getType() == UserType.USER );
    	
    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }
    
	/**
	* updateUserData() tests
	* (PUT "/profile/edit-data")
	*/
    
    @Test
    @Transactional
    @WithMockUser(roles = "USER", username="test@ipb.com")
    public void updateUserDataMethodShouldReturnNotFoundStatusCodeBecauseOfMissingUser() throws Exception {
    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("email", "test_user@ipb.com");
    	postRequestBody.put("password", "123456");
    	postRequestBody.put("matchingPassword", "123456");
    	postRequestBody.put("firstName", "Ivan");
    	postRequestBody.put("lastName", "Ivanov");
    	postRequestBody.put("birthday", "1969-01-01");
    	postRequestBody.put("type", "USER");
    	
        MvcResult result = this.mockMvc
        	.perform( 
        		put("/profile/edit-data")
        			.contentType(MediaType.APPLICATION_JSON)
        			.content( postRequestBody.toString()
        		)
        	)
        	.andExpect( status().isNotFound() )
        	.andReturn();
        
        // Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("User with this e-mail does not exist!");
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "USER", username="test@ipb.com")
    public void updateUserDataMethodShouldReturnOKStatusCode() throws Exception {
    	// Insert test user with the logged in mock user's data
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", // must match username in @WithMockUser annotation
    			"123456",
    			"123456",
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("email", "test_updated@ipb.com");
    	postRequestBody.put("password", "1234567");
    	postRequestBody.put("matchingPassword", "1234567");
    	postRequestBody.put("firstName", "Petar");
    	postRequestBody.put("lastName", "Petrov");
    	postRequestBody.put("birthday", "1969-01-01");
    	postRequestBody.put("type", "USER");
    	
        MvcResult result = this.mockMvc
        	.perform( 
        		put("/profile/edit-data")
        			.contentType(MediaType.APPLICATION_JSON)
        			.content( postRequestBody.toString()
        		)
        	)
        	.andExpect( status().isOk() )
        	.andReturn();
        
        // Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Successfully updated user!");
    	
    	// Test if the user's info has been updated
    	Long testUserId = testUser.getId();
    	UserEntity retrievedTestUser = userRepository.findById(testUserId).get();
    	assertTrue( retrievedTestUser.getEmail().equals("test_updated@ipb.com") );
    	assertTrue( passwordEncoder.matches("1234567", retrievedTestUser.getPassword()) ); // update method stores the password encrypted in the database
    	assertTrue( passwordEncoder.matches("1234567", retrievedTestUser.getMatchingPassword()) );
    	assertTrue( retrievedTestUser.getFirstName().equals("Petar") );
    	assertTrue( retrievedTestUser.getLastName().equals("Petrov") );
    	assertTrue( retrievedTestUser.getType() == UserType.USER );
    	
    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "USER", username="test@ipb.com")
    public void updateUserDataMethodShouldReturnBadRequestStatusCodeBecauseOfBindingResultErrors() throws Exception {
    	// Insert test user with the logged in mock user's data
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", // must match username in @WithMockUser annotation
    			"123456",
    			"123456",
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("email", "my-email"); // invalid e-mail
    	postRequestBody.put("password", "123"); // invalid password
    	postRequestBody.put("matchingPassword", "1234"); // mimsmatching password
    	postRequestBody.put("firstName", "Petar");
    	postRequestBody.put("lastName", "Petrov");
    	postRequestBody.put("birthday", "1969-01-01");
    	postRequestBody.put("type", "USER");
    	
        MvcResult result = this.mockMvc
        	.perform( 
        		put("/profile/edit-data")
        			.contentType(MediaType.APPLICATION_JSON)
        			.content( postRequestBody.toString()
        		)
        	)
        	.andExpect( status().isBadRequest() )
        	.andReturn();
        
        // Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Invalid e-mail!");
    	assertThat(resultString).contains("User password must be at least 6 characters!");
    	assertThat(resultString).contains("Passwords don't match!");
    	
    	// Test if the user's info has been updated
    	Long testUserId = testUser.getId();
    	UserEntity retrievedTestUser = userRepository.findById(testUserId).get();
    	assertTrue( retrievedTestUser.getEmail().equals("test@ipb.com") );
    	assertTrue( retrievedTestUser.getPassword().equals("123456") );
    	assertTrue( retrievedTestUser.getMatchingPassword().equals("123456") );
    	assertTrue( retrievedTestUser.getFirstName().equals("Ivan") );
    	assertTrue( retrievedTestUser.getLastName().equals("Ivanov") );
    	assertTrue( retrievedTestUser.getType() == UserType.USER );

    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }

    /**
	* deleteUser() tests
	* (DELETE "/profile")
	*/
    
    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void deleteUserMethodShouldReturnNotFoundStatusCodeBecauseOfMissingUser() throws Exception {
        MvcResult result = this.mockMvc
        	.perform( delete("/profile") )
        	.andExpect( status().isNotFound() )
        	.andReturn();
        
        // Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("User with this e-mail does not exist!");
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "USER", username="test@ipb.com")
    public void deleteUserMethodShouldReturnOKStatusCode() throws Exception {
    	// Insert test user with the logged in mock user's data
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", // must match username in @WithMockUser annotation
    			"123456",
    			"123456",
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	// Test if the response code is 200 OK
        MvcResult result = this.mockMvc
        	.perform( delete("/profile") )
        	.andExpect( status().isOk() )
        	.andReturn();
        
        // Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Successfully deleted user!");
    	
    	// Test if the user has been deleted and perform delete operation just in case
    	Long testUserId = testUser.getId();
    	assertTrue( !userRepository.existsById(testUserId) );
    	userRepository.delete(testUser);
    }

}