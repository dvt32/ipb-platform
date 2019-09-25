package com.ipb.platform.security;

import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
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
 * This class implements authentication (login & logout) tests.
 * 
 * The @Transactional annotation is used to rollback 
 * database changes after a test has finished executing.
 * 
 * @author dvt32
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class AuthenticationTests {

	@Autowired
	private WebApplicationContext context;
	
	private MockMvc mockMvc;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	/**
	 * Set context & configure Spring Security 
	 * for testing secured REST endpoints.
	 */
	@Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
          .webAppContextSetup(context)
          .apply(springSecurity())
          .build();
    }
	
	/**
	 * Helper methods
	 */
	
	public UserEntity getTestNonAdminUser() {
		UserEntity testNonAdminUser = new UserEntity(
			null, // id is auto-generated
			"test_user@ipb.com",
			"123456",
			"123456",
			"Ivan", 
			"Ivanov",
			new java.sql.Date(0), 
			UserType.USER
		);
		return testNonAdminUser;
	}
	
	public UserEntity getTestAdminUser() {
		UserEntity testAdminUser = new UserEntity(
			null, // id is auto-generated
			"test_admin@ipb.com",
			"123456",
			"123456",
			"Petar", 
			"Petrov",
			new java.sql.Date(0), 
			UserType.ADMIN
		);
		return testAdminUser;
	}
	
	/**
	 * Login tests
	 */
	
	@Test
	public void loginAttemptShouldReturnUnauthorizedStatusCodeDueToMissingUser() throws Exception {
		this.mockMvc.perform( 
			post("/login")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "test_user@ipb.com")
				.param("password", "123456")
		).andExpect( status().isUnauthorized() );
	}
	
	@Test
	public void loginAttemptWithNonAdminUserShouldReturnOKStatusCode() throws Exception {
		UserEntity testUser = getTestNonAdminUser();
		String testUserEmail = testUser.getEmail();
		String testUserPassword = testUser.getPassword();
		String encodedPassword =  passwordEncoder.encode(testUserPassword);
		testUser.setPassword( encodedPassword );
		testUser.setMatchingPassword( encodedPassword );
		testUser = userRepository.save(testUser);
		
		MvcResult result = this.mockMvc.perform( 
			post("/login")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", testUserEmail)
				.param("password", testUserPassword)
		).andExpect( status().isOk() ).andReturn();
		
		String responseString = result.getResponse().getContentAsString();
		assertTrue( responseString.contains("Successfully logged in!") );
	}
	
	@Test
	public void loginAttemptWithAdminUserShouldReturnOKStatusCode() throws Exception {
		UserEntity testUser = getTestAdminUser();
		String testUserEmail = testUser.getEmail();
		String testUserPassword = testUser.getPassword();
		String encodedPassword =  passwordEncoder.encode(testUserPassword);
		testUser.setPassword( encodedPassword );
		testUser.setMatchingPassword( encodedPassword );
		testUser = userRepository.save(testUser);
		
		MvcResult result = this.mockMvc.perform( 
			post("/login")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", testUserEmail)
				.param("password", testUserPassword)
		).andExpect( status().isOk() ).andReturn();
		
		String responseString = result.getResponse().getContentAsString();
		assertTrue( responseString.contains("Successfully logged in!") );
	}
	
	/**
	 * Logout tests
	 */
	
	@Test
	public void logoutAttemptWithGetRequestShouldReturnOKStatusCode() throws Exception {
		MvcResult result = this.mockMvc.perform( 
			get("/logout")
		).andExpect( status().isOk() ).andReturn();
		String responseString = result.getResponse().getContentAsString();
		assertTrue( responseString.contains("Successfully logged out!") );
	}
	
	@Test
	public void logoutAttemptWithPostRequestShouldReturnOKStatusCode() throws Exception {
		MvcResult result = this.mockMvc.perform( 
			post("/logout")
		).andExpect( status().isOk() ).andReturn();
		String responseString = result.getResponse().getContentAsString();
		assertTrue( responseString.contains("Successfully logged out!") );
	}

}