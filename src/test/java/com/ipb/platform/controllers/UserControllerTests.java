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
import com.ipb.platform.security.PasswordResetToken;
import com.ipb.platform.security.PasswordResetTokenRepository;

/**
 * This class implements integration tests for the UserController class.
 * 
 * The @Transactional annotation is used to rollback 
 * database changes (made via a MockMvc request)
 * after the annotated test has finished executing.
 * 
 * @author dvt32
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerTests {
	
	@Autowired
    private UserController userController;
	
	@Autowired
	private WebApplicationContext context;
	
	private MockMvc mockMvc;
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private PasswordResetTokenRepository tokenRepository;
	
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
        assertThat(userController).isNotNull();
    }
    
    /**
     * getAll() tests
     * (GET "/users")
     */
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getUsersMethodShouldReturnOKStatusCode() throws Exception {
        this.mockMvc.perform( get("/users") ).andExpect( status().isOk() );
    }
    
    @Test
    @WithMockUser(roles = "USER")
    public void getUsersMethodShouldReturnForbiddenStatusCode() throws Exception {
        this.mockMvc.perform( get("/users") ).andExpect( status().isForbidden() );
    }
    
    @Test
    public void getUsersMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform( get("/users") ).andExpect( status().isUnauthorized() );
    }
    
    /**
     * getUserById() tests
     * (GET "/users/{id}")
     */
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getUserByIdMethodShouldReturnNotFoundStatusCode() throws Exception {
        this.mockMvc.perform( get("/users/1") ).andExpect( status().isNotFound() );
    }
    
    @Test
    @WithMockUser(roles = "USER")
    public void getUserByIdMethodShouldReturnForbiddenStatusCode() throws Exception {
        this.mockMvc.perform( get("/users/1") ).andExpect( status().isForbidden() );
    }
    
    @Test
    public void getUserByIdMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform( get("/users/1") ).andExpect( status().isUnauthorized() );
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void getUserByIdMethodShouldReturnOKStatusCodeAndCorrectUserEmail() throws Exception {
    	// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"123456", 
    			"123456", 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	// Test if the response code is 200 OK
    	Long testUserId = testUser.getId();
    	MvcResult result = this.mockMvc
    		.perform( get("/users/" + testUserId) )
    		.andExpect( status().isOk() )
    		.andReturn();
		
    	// Test if the response string contains the inserted user's email.
		String jsonResponseString = result.getResponse().getContentAsString();
		assertThat(jsonResponseString).contains("test@ipb.com");
    	
		// Delete test user so that he does not interfere with other tests
		userRepository.delete(testUser);
    }
    
    /**
     * createUser() tests
     * (POST "/users")
     */
    
    @Test
    @Transactional
    public void createUserMethodShouldReturnCreatedStatusCodeAndSuccessMessage() throws Exception {
    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("email", "test_user@ipb.com");
    	postRequestBody.put("password", "123456");
    	postRequestBody.put("matchingPassword", "123456");
    	postRequestBody.put("firstName", "Ivan");
    	postRequestBody.put("lastName", "Ivanov");
    	postRequestBody.put("birthday", "1969-01-01");
    	postRequestBody.put("type", "USER");
    	
    	// Test if the response code is 201 CREATED
    	MvcResult result = this.mockMvc
    		.perform( 
    			post("/users").contentType(MediaType.APPLICATION_JSON).content( postRequestBody.toString() )
    		)
    		.andExpect( status().isCreated() )
    		.andReturn();
    	
    	// Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Successfully created user");
    }
    
    @Test
    @Transactional
    public void createUserMethodShouldReturnBadRequestStatusCodeBecauseOfInvalidEmail() throws Exception {
    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("email", "invalid-email"); // use invalid e-mail
    	postRequestBody.put("password", "123456");
    	postRequestBody.put("matchingPassword", "123456");
    	postRequestBody.put("firstName", "Ivan");
    	postRequestBody.put("lastName", "Ivanov");
    	postRequestBody.put("birthday", "1969-01-01");
    	postRequestBody.put("type", "USER");
    	
    	// Test if the response code is 400 BAD REQUEST
    	MvcResult result = this.mockMvc
    		.perform( 
    			post("/users").contentType(MediaType.APPLICATION_JSON).content( postRequestBody.toString() )
    		)
    		.andExpect( status().isBadRequest() )
    		.andReturn();
    	
    	// Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Invalid e-mail!");
    }
    
    @Test
    @Transactional
    public void createUserMethodShouldReturnBadRequestStatusCodeBecauseOfInvalidPassword() throws Exception {
    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("email", "test_user@ipb.com");
    	postRequestBody.put("password", "123"); // invalid password
    	postRequestBody.put("matchingPassword", "123");
    	postRequestBody.put("firstName", "Ivan");
    	postRequestBody.put("lastName", "Ivanov");
    	postRequestBody.put("birthday", "1969-01-01");
    	postRequestBody.put("type", "USER");
    	
    	// Test if the response code is 400 BAD REQUEST
    	MvcResult result = this.mockMvc
    		.perform( 
    			post("/users").contentType(MediaType.APPLICATION_JSON).content( postRequestBody.toString() )
    		)
    		.andExpect( status().isBadRequest() )
    		.andReturn();
    	
    	// Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("User password must be at least 6 characters!");
    }
    
    @Test
    @Transactional
    public void createUserMethodShouldReturnBadRequestStatusCodeBecausePasswordsDontMatch() throws Exception {
    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("email", "test_user@ipb.com");
    	postRequestBody.put("password", "123456");
    	postRequestBody.put("matchingPassword", "1234567"); // passwords don't match
    	postRequestBody.put("firstName", "Ivan");
    	postRequestBody.put("lastName", "Ivanov");
    	postRequestBody.put("birthday", "1969-01-01");
    	postRequestBody.put("type", "USER");
    	
    	// Test if the response code is 400 BAD REQUEST
    	MvcResult result = this.mockMvc
    		.perform( 
    			post("/users").contentType(MediaType.APPLICATION_JSON).content( postRequestBody.toString() )
    		)
    		.andExpect( status().isBadRequest() )
    		.andReturn();
    	
    	// Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Passwords don't match!");
    }
    
    @Test
    @Transactional
    public void createUserMethodShouldReturnBadRequestStatusCodeBecauseOfExistingEmail() throws Exception {
    	// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"123456", 
    			"123456", 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("email", "test@ipb.com"); // e-mail already exists
    	postRequestBody.put("password", "1234567");
    	postRequestBody.put("matchingPassword", "1234567");
    	postRequestBody.put("firstName", "Ivan");
    	postRequestBody.put("lastName", "Ivanov");
    	postRequestBody.put("birthday", "1979-01-01");
    	postRequestBody.put("type", "USER");
    	
    	// Test if the response code is 400 BAD REQUEST
    	MvcResult result = this.mockMvc
    		.perform( 
    			post("/users").contentType(MediaType.APPLICATION_JSON).content( postRequestBody.toString() )
    		)
    		.andExpect( status().isBadRequest() )
    		.andReturn();
    	
    	// Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("User with this e-mail address already exists!");
    	
    	// Delete test user so that he does not interfere with other tests
		userRepository.delete(testUser);
    }
    
    /**
     * updateUserById() tests
     * (PUT "/users/{id}")
     */
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateUserByIdMethodShouldReturnNotFoundStatusCode() throws Exception {
    	JSONObject requestBody = new JSONObject();
    	requestBody.put("email", "test_updated@ipb.com");
    	requestBody.put("password", "1234567");
    	requestBody.put("matchingPassword", "1234567");
    	requestBody.put("firstName", "Ivan");
    	requestBody.put("lastName", "Ivanov");
    	requestBody.put("birthday", "1979-01-01");
    	requestBody.put("type", "USER");
    	
        this.mockMvc.perform( 
        	put("/users/1").contentType(MediaType.APPLICATION_JSON).content( requestBody.toString()) 
        ).andExpect( status().isNotFound() );
    }
    
    @Test
    @WithMockUser(roles = "USER")
    public void updateUserByIdMethodShouldReturnForbiddenStatusCode() throws Exception {
    	JSONObject requestBody = new JSONObject();
    	requestBody.put("email", "test_updated@ipb.com");
    	requestBody.put("password", "1234567");
    	requestBody.put("matchingPassword", "1234567");
    	requestBody.put("firstName", "Ivan");
    	requestBody.put("lastName", "Ivanov");
    	requestBody.put("birthday", "1979-01-01");
    	requestBody.put("type", "USER");
    	
        this.mockMvc.perform( 
        	put("/users/1").contentType(MediaType.APPLICATION_JSON).content( requestBody.toString()) 
        ).andExpect( status().isForbidden() );
    }
    
    @Test
    public void updateUserByIdMethodShouldReturnUnauthorizedStatusCode() throws Exception {
    	JSONObject requestBody = new JSONObject();
    	requestBody.put("email", "test_updated@ipb.com");
    	requestBody.put("password", "1234567");
    	requestBody.put("matchingPassword", "1234567");
    	requestBody.put("firstName", "Ivan");
    	requestBody.put("lastName", "Ivanov");
    	requestBody.put("birthday", "1979-01-01");
    	requestBody.put("type", "USER");
    	
        this.mockMvc.perform( 
        	put("/users/1").contentType(MediaType.APPLICATION_JSON).content( requestBody.toString()) 
        ).andExpect( status().isUnauthorized() );
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void updateUserByIdMethodShouldReturnBadRequestStatusCodeBecauseOfNoBody() throws Exception {
        this.mockMvc.perform( put("/users/1") ).andExpect( status().isBadRequest() );
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void updateUserByIdMethodShouldReturnOKStatusCode() throws Exception {
    	// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"123456", 
    			"123456", 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	JSONObject requestBody = new JSONObject();
    	requestBody.put("email", "test_updated@ipb.com");
    	requestBody.put("password", "1234567");
    	requestBody.put("matchingPassword", "1234567");
    	requestBody.put("firstName", "Petar");
    	requestBody.put("lastName", "Petrov");
    	requestBody.put("birthday", "1980-01-01");
    	requestBody.put("type", "USER");
    	
    	// Test if the response code is 200 OK
    	Long testUserId = testUser.getId();
        MvcResult result = this.mockMvc
        	.perform( 
        		put("/users/" + testUserId).contentType(MediaType.APPLICATION_JSON).content( requestBody.toString()) 
        	)
        	.andExpect( status().isOk() )
        	.andReturn();
        
        // Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Successfully updated user");
    	
    	// Test if the user's data has really been updated
    	assertTrue( userRepository.findById(testUserId).get().getEmail().equals("test_updated@ipb.com") );
        
	     // Delete test user so that he does not interfere with other tests
		userRepository.delete(testUser);
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void updateUserByIdMethodShouldReturnBadRequestStatusCodeBecauseOfMismatchingPasswords() throws Exception {
    	// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"123456", 
    			"123456", 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	JSONObject requestBody = new JSONObject();
    	requestBody.put("email", "test_updated@ipb.com");
    	requestBody.put("password", "1234567");
    	requestBody.put("matchingPassword", "12345678");
    	
    	// Test if the response code is 400 BAD REQUEST
    	Long testUserId = testUser.getId();
        MvcResult result = this.mockMvc
        	.perform( 
        		put("/users/" + testUserId).contentType(MediaType.APPLICATION_JSON).content( requestBody.toString()) 
        	)
        	.andExpect( status().isBadRequest() )
        	.andReturn();
        
        // Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Passwords don't match!");
    	
    	// Test if the user's data has not been updated
    	assertTrue( userRepository.findById(testUserId).get().getEmail().equals("test@ipb.com") );
        
	    // Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }
    
	/**
	* deleteUserById() tests
	* (DELETE "/users/{id}")
	*/
    
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void deleteUserByIdMethodShouldReturnOKStatusCode() throws Exception {
    	// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"123456", 
    			"123456", 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	// Test if the response code is 200 OK
    	Long testUserId = testUser.getId();
        MvcResult result = this.mockMvc
        	.perform( delete("/users/" + testUserId) )
        	.andExpect( status().isOk() )
        	.andReturn();
        
        // Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Successfully deleted user");
    	
    	// Test if the user has been deleted
    	assertTrue( !userRepository.existsById(testUserId) );
    	
    	// Just in case the user was not deleted via the request, delete him manually via repository
    	userRepository.delete(testUser);
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void deleteUserByIdMethodShouldReturnNotFoundStatusCode() throws Exception {
        this.mockMvc.perform( delete("/users/1") ).andExpect( status().isNotFound() );
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void deleteUserByIdMethodShouldReturnForbiddenStatusCode() throws Exception {
        this.mockMvc.perform( delete("/users/1") ).andExpect( status().isForbidden() );
    }
    
    @Test
    @Transactional
    public void deleteUserByIdMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform( delete("/users/1") ).andExpect( status().isUnauthorized() );
    }
    
	/**
	* makeUserAdminByEmail() tests
	* (POST "/users/make-user-admin" with request param "email")
	*/
    
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void makeUserAdminByEmailMethodShouldReturnNotFoundStatusCode() throws Exception {
        this.mockMvc.perform( 
        	post("/users/make-user-admin").param("email", "test@ipb.com") 
        ).andExpect( status().isNotFound() );
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void makeUserAdminByEmailMethodShouldReturnForbiddenStatusCode() throws Exception {
    	this.mockMvc.perform( 
        	post("/users/make-user-admin").param("email", "test@ipb.com") 
        ).andExpect( status().isForbidden() );
    }
    
    @Test
    @Transactional
    public void makeUserAdminByEmailMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform( 
        	post("/users/make-user-admin").param("email", "test@ipb.com") 
        ).andExpect( status().isUnauthorized() );
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void makeUserAdminByEmailMethodShouldReturnOkStatusCode() throws Exception {
    	// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
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
        	.perform( post("/users/make-user-admin").param("email", "test@ipb.com") )
        	.andExpect( status().isOk() )
        	.andReturn();
        
        // Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Successfully set user");
    	assertThat(resultString).contains("to admin");
    	
    	// Test if the user has been updated
    	Long testUserId = testUser.getId();
    	assertTrue( userRepository.findById(testUserId).get().getType() == UserType.ADMIN );
    	
    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }
    
    /**
	* makeUserAdminById() tests
	* (POST "/users/make-user-admin" with request param "id")
	*/
    
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void makeUserAdminByIdMethodShouldReturnNotFoundStatusCode() throws Exception {
        this.mockMvc.perform( 
        	post("/users/make-user-admin").param("id", "1") 
        ).andExpect( status().isNotFound() );
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void makeUserAdminByIdMethodShouldReturnForbiddenStatusCode() throws Exception {
    	this.mockMvc.perform( 
        	post("/users/make-user-admin").param("id", "1") 
        ).andExpect( status().isForbidden() );
    }
    
    @Test
    @Transactional
    public void makeUserAdminByIdMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform( 
        	post("/users/make-user-admin").param("id", "1") 
        ).andExpect( status().isUnauthorized() );
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void makeUserAdminByIdMethodShouldReturnOkStatusCode() throws Exception {
    	// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"123456", 
    			"123456", 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	// Test if the response code is 200 OK
    	Long testUserId = testUser.getId();
        MvcResult result = this.mockMvc
        	.perform( post("/users/make-user-admin").param("id", String.valueOf(testUserId)) )
        	.andExpect( status().isOk() )
        	.andReturn();
        
        // Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Successfully set user");
    	assertThat(resultString).contains("to admin");
    	
    	// Test if the user has been updated
    	assertTrue( userRepository.findById(testUserId).get().getType() == UserType.ADMIN );
    	
    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }
       
	/**
	* makeUserNonAdminByEmail() tests
	* (POST "/users/make-user-non-admin" with request param "email")
	*/
	
	@Test
	@Transactional
	@WithMockUser(roles = "ADMIN")
	public void makeUserNonAdminByEmailMethodShouldReturnNotFoundStatusCode() throws Exception {
		this.mockMvc.perform( 
			post("/users/make-user-non-admin").param("email", "test@ipb.com") 
		).andExpect( status().isNotFound() );
	}
	
	@Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void makeUserNonAdminByEmailMethodShouldReturnForbiddenStatusCode() throws Exception {
    	this.mockMvc.perform( 
        	post("/users/make-user-non-admin").param("email", "test@ipb.com") 
        ).andExpect( status().isForbidden() );
    }
    
    @Test
    @Transactional
    public void makeUserNonAdminByEmailMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform( 
        	post("/users/make-user-non-admin").param("email", "test@ipb.com") 
        ).andExpect( status().isUnauthorized() );
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void makeUserNonAdminByEmailMethodShouldReturnOkStatusCode() throws Exception {
    	// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"123456", 
    			"123456", 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.ADMIN
    		)
    	);
    	
    	// Test if the response code is 200 OK
        MvcResult result = this.mockMvc
        	.perform( post("/users/make-user-non-admin").param("email", "test@ipb.com") )
        	.andExpect( status().isOk() )
        	.andReturn();
        
        // Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Successfully set user");
    	assertThat(resultString).contains("to non-admin");
    	
    	// Test if the user has been updated
    	Long testUserId = testUser.getId();
    	assertTrue( userRepository.findById(testUserId).get().getType() == UserType.USER );
    	
    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }
    
    /**
	* makeUserNonAdminById() tests
	* (POST "/users/make-user-non-admin" with request param "id")
	*/
    
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void makeUserNonAdminByIdMethodShouldReturnNotFoundStatusCode() throws Exception {
        this.mockMvc.perform( 
        	post("/users/make-user-non-admin").param("id", "1") 
        ).andExpect( status().isNotFound() );
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void makeUserNonAdminByIdMethodShouldReturnForbiddenStatusCode() throws Exception {
    	this.mockMvc.perform( 
        	post("/users/make-user-non-admin").param("id", "1") 
        ).andExpect( status().isForbidden() );
    }
    
    @Test
    @Transactional
    public void makeUserNonAdminByIdMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform( 
        	post("/users/make-user-non-admin").param("id", "1") 
        ).andExpect( status().isUnauthorized() );
    }
    
    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void makeUserNonAdminByIdMethodShouldReturnOkStatusCode() throws Exception {
    	// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"123456", 
    			"123456", 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.ADMIN
    		)
    	);
    	
    	// Test if the response code is 200 OK
    	Long testUserId = testUser.getId();
        MvcResult result = this.mockMvc
        	.perform( post("/users/make-user-non-admin").param("id", String.valueOf(testUserId)) )
        	.andExpect( status().isOk() )
        	.andReturn();
        
        // Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Successfully set user");
    	assertThat(resultString).contains("to non-admin");
    	
    	// Test if the user has been updated
    	assertTrue( userRepository.findById(testUserId).get().getType() == UserType.USER );
    	
    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }

	/**
	* sendResetPasswordEmail() tests
	* (POST "/users/forgot-password" with request param "email")
	*/
    
    @Test
    @Transactional
    public void sendResetPasswordEmailMethodShouldReturnNotFoundStatusCode() throws Exception {
        this.mockMvc.perform( 
        	post("/users/forgot-password").param("email", "test@ipb.com") 
        ).andExpect( status().isNotFound() );
    }
    
    @Test
    @Transactional
    public void sendRestPasswordEmailMethodShouldReturnOKStatusCode() throws Exception {
    	// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"123456", 
    			"123456", 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	// Test if the response code is 200 OK
    	String testUserEmail = testUser.getEmail();
        MvcResult result = this.mockMvc
        	.perform( post("/users/forgot-password").param("email", testUserEmail) )
        	.andExpect( status().isOk() )
        	.andReturn();
        
        // Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Successfully sent password reset e-mail");
    	assertThat(resultString).contains(testUserEmail);
    	
    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }
    
	/**
	* setNewPassword() tests
	* (POST "/users/reset-password" with request params "token", "newPassword", "matchingNewPassword")
	*/
    
    @Test
    @Transactional
    public void setNewPasswordMethodShouldReturnOKStatusCode() throws Exception {
    	// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"123456", 
    			"123456", 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	// Insert test password reset token
    	PasswordResetToken token = tokenRepository.save(
    		new PasswordResetToken(
    			null, // id is auto-generated
    			"TOKEN-STRING",
    			testUser
    		)
    	);
    	Long tokenId = token.getId();
    	
    	// Test if the response code is 200 OK
    	String tokenString = token.getToken();
        MvcResult result = this.mockMvc
        	.perform( 
        		post("/users/reset-password")
        			.param("token", tokenString)
        			.param("newPassword", "123456789")
        			.param("matchingNewPassword", "123456789")
        	)
        	.andExpect( status().isOk() )
        	.andReturn();
        
        // Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Successfully changed user password via token!");
    	
    	// Test if the reset token has been deleted and perform a delete operation just in case
    	assertTrue( !tokenRepository.existsById(tokenId) );
    	tokenRepository.delete(token);
    	
    	// Test if the user's info has been updated
    	Long testUserId = testUser.getId();
    	String testUserEncryptedPassword = userRepository.findById(testUserId).get().getPassword();
    	assertTrue( passwordEncoder.matches("123456789", testUserEncryptedPassword) );
    	
    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }
    
    @Test
    @Transactional
    public void setNewPasswordMethodShouldReturnBadRequestStatusCodeBecauseOfInvalidToken() throws Exception {
    	// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"123456", 
    			"123456", 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	// Test if the response code is 400 BAD REQUEST
        MvcResult result = this.mockMvc
        	.perform( 
        		post("/users/reset-password")
        			.param("token", "TOKEN-STRING") // invalid token string
        			.param("newPassword", "123456789")
        			.param("matchingNewPassword", "123456789")
        	)
        	.andExpect( status().isBadRequest() )
        	.andReturn();
        
        // Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Invalid password reset link/token!");
    	
    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }
    
    @Test
    @Transactional
    public void setNewPasswordMethodShouldReturnBadRequestStatusCodeBecauseOfInvalidPassword() throws Exception {
    	// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"123456", 
    			"123456", 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	// Insert test password reset token
    	PasswordResetToken token = tokenRepository.save(
    		new PasswordResetToken(
    			null, // id is auto-generated
    			"TOKEN-STRING",
    			testUser
    		)
    	);
    	Long tokenId = token.getId();
    	
    	// Test if the response code is 400 BAD REQUEST
    	String tokenString = token.getToken();
        MvcResult result = this.mockMvc
        	.perform( 
        		post("/users/reset-password")
        			.param("token", tokenString)
        			.param("newPassword", "123") // invalid password
        			.param("matchingNewPassword", "123") 
        	)
        	.andExpect( status().isBadRequest() )
        	.andReturn();
        
        // Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Invalid new password!");
    	
    	// Test if the reset token is still valid and delete it so that it doesn't interfere with other tests
    	assertTrue( tokenRepository.existsById(tokenId) );
    	tokenRepository.delete(token);
    	
    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }
    
    @Test
    @Transactional
    public void setNewPasswordMethodShouldReturnBadRequestStatusCodeBecauseOfMismatchingPasswords() throws Exception {
    	// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"123456", 
    			"123456", 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	// Insert test password reset token
    	PasswordResetToken token = tokenRepository.save(
    		new PasswordResetToken(
    			null, // id is auto-generated
    			"TOKEN-STRING",
    			testUser
    		)
    	);
    	Long tokenId = token.getId();
    	
    	// Test if the response code is 400 BAD REQUEST
    	String tokenString = token.getToken();
        MvcResult result = this.mockMvc
        	.perform( 
        		post("/users/reset-password")
        			.param("token", tokenString)
        			.param("newPassword", "123456789")
        			.param("matchingNewPassword", "0123456789") // passwords don't match
        	)
        	.andExpect( status().isBadRequest() )
        	.andReturn();
        
        // Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("New passwords don't match!");
    	
    	// Test if the reset token is still valid and delete it so that it doesn't interfere with other tests
    	assertTrue( tokenRepository.existsById(tokenId) );
    	tokenRepository.delete(token);
    	
    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }
    
    @Test
    @Transactional
    public void setNewPasswordMethodShouldReturnBadRequestStatusCodeBecauseOfDeletedUser() throws Exception {
    	// Insert test user
    	UserEntity testUser = userRepository.save( 
    		new UserEntity(
    			null, // id is auto-generated
    			"test@ipb.com", 
    			"123456", 
    			"123456", 
    			"Ivan", 
    			"Ivanov",
    			new java.sql.Date(0), 
    			UserType.USER
    		)
    	);
    	
    	// Insert test password reset token
    	PasswordResetToken token = tokenRepository.save(
    		new PasswordResetToken(
    			null, // id is auto-generated
    			"TOKEN-STRING",
    			testUser
    		)
    	);
    	Long tokenId = token.getId();
    	
    	// Delete test user so that the token becomes invalid (gets deleted)
    	// and so that the user does not interfere with other tests
    	userRepository.delete(testUser);
    	
    	// Test if the response code is 400 BAD REQUEST
    	String tokenString = token.getToken();
        MvcResult result = this.mockMvc
        	.perform( 
        		post("/users/reset-password")
        			.param("token", tokenString)
        			.param("newPassword", "123456789")
        			.param("matchingNewPassword", "123456789")
        	)
        	.andExpect( status().isBadRequest() )
        	.andReturn();
        
        // Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Invalid password reset link/token!");
    	
    	// Test if the reset token has been deleted and perform a delete operation just in case
    	assertTrue( !tokenRepository.existsById(tokenId) );
    	tokenRepository.delete(token);
    }
    
}