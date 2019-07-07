package com.ipb.platform.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.ipb.platform.persistence.CityRepository;
import com.ipb.platform.persistence.ContactFormSubmissionRepository;
import com.ipb.platform.persistence.EventRepository;
import com.ipb.platform.persistence.EventSuggestionRepository;
import com.ipb.platform.persistence.ObjectRepository;
import com.ipb.platform.persistence.ObjectSuggestionRepository;
import com.ipb.platform.persistence.UserRepository;
import com.ipb.platform.persistence.entities.CityEntity;
import com.ipb.platform.persistence.entities.ContactFormSubmissionEntity;
import com.ipb.platform.persistence.entities.EventEntity;
import com.ipb.platform.persistence.entities.EventSuggestionEntity;
import com.ipb.platform.persistence.entities.ObjectEntity;
import com.ipb.platform.persistence.entities.ObjectSuggestionEntity;
import com.ipb.platform.persistence.entities.ObjectType;
import com.ipb.platform.persistence.entities.UserEntity;
import com.ipb.platform.persistence.entities.UserType;

/**
 * This class implements integration tests for the ContactFormSubmissionController class.
 *
 * The @Transactional annotation is used to rollback
 * database changes (made via a MockMvc request)
 * after the annotated test has finished executing.
 *
 * @author dvt32
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ContactFormSubmissionControllerTests {

	@Autowired
    private ContactFormSubmissionController contactFormSubmissionController;

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Autowired
    private UserRepository userRepository;

	@Autowired
	private ContactFormSubmissionRepository submissionRepository;

	@Autowired
	private ObjectRepository objectRepository;

	@Autowired
	private ObjectSuggestionRepository objectSuggestionRepository;

	@Autowired
	private EventSuggestionRepository eventSuggestionRepository;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private EventRepository eventRepository;

	private static final String SUBMISSIONS_FILES_DIRECTORY = System.getProperty("user.dir") + "/files/contact-form";

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
        assertThat(contactFormSubmissionController).isNotNull();
    }

    /**
     * addContactFormSubmission() tests
     * (POST "/contact")
     */

    @Test
    @Transactional
    public void addContactFormSubmissionMethodShouldReturnUnauthorizedStatusCode() throws Exception {
    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("message", "I found an error...");

        this.mockMvc.perform(
        	post("/contact")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content( postRequestBody.toString() )
        ).andExpect( status().isUnauthorized() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void addContactFormSubmissionMethodShouldReturnBadRequestStatusCodeBecauseOfMissingUser() throws Exception {
    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("message", "I found an error...");

        this.mockMvc.perform(
        	post("/contact")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content( postRequestBody.toString() )
        ).andExpect( status().isBadRequest() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER", username = "test@ipb.com")
    public void addContactFormSubmissionMethodShouldReturnCreatedStatusCode() throws Exception {
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
    			UserType.USER,
					null,
					null,
					null
    		)
    	);

    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("message", "I found an error...");

    	// Test if the response code is 201 CREATED
    	MvcResult result = this.mockMvc.perform(
        	post("/contact")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content( postRequestBody.toString() )
        ).andExpect( status().isCreated() ).andReturn();

    	// Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Successfully added contact form submission");

    	// Test if the response's location header has a value
    	String locationHeaderValue = result.getResponse().getHeaderValue("location").toString();
    	assertThat(locationHeaderValue).contains("/");

        // Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER", username = "test@ipb.com")
    public void addContactFormSubmissionMethodShouldReturnBadRequestStatusCodeBecauseOfBindingResultErrors() throws Exception {
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
    			UserType.USER,
					null,
					null,
					null
    		)
    	);

    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("message", ""); // message must not be blank

    	// Test if the response code is 400 BAD REQUEST
    	MvcResult result = this.mockMvc.perform(
        	post("/contact")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content( postRequestBody.toString() )
        ).andExpect( status().isBadRequest() ).andReturn();

    	// Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Form submission must not be null or blank!");

        // Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }

    /**
     * storeFileForSubmission() tests
     * (POST "/contact/{id}/file" with request param "file")
     */

    @Test
    @Transactional
    @WithMockUser(roles = "USER", username = "test@ipb.com")
    public void storeFileForSubmissionMethodShouldReturnOKStatusCode() throws Exception {
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
    			UserType.USER,
					null,
					null,
					null
    		)
    	);

    	// Insert test submission
    	ContactFormSubmissionEntity testSubmission = submissionRepository.save(
			new ContactFormSubmissionEntity(
    			null, // id is auto-generated
    			"test@ipb.com", // must match username in @WithMockUser annotation
    			"I found an error...",
    			null // no file attached
    		)
    	);

    	// Test if the response code is 200 OK
    	Long submissionId = testSubmission.getId();
    	MockMultipartFile fileAttached = new MockMultipartFile(
    		"file", // must match request param's name
    		"test-file.txt",
    		"text/plain",
    		"Text inside file".getBytes()
    	);
    	MvcResult result = this.mockMvc.perform(
        	multipart("/contact/" + submissionId + "/file")
        		.file(fileAttached)
        ).andExpect( status().isOk() ).andReturn();

    	// Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Successfully attached file to contact form submission");

    	// Test if the test submission's file path has been updated
    	ContactFormSubmissionEntity retrievedTestSubmission = submissionRepository.findById(submissionId).get();
    	assertTrue( retrievedTestSubmission.getPathToAttachedFile().contains("test-file.txt") );

    	// Delete attached file so that it does not interfere with other tests
    	Files.deleteIfExists( Paths.get(retrievedTestSubmission.getPathToAttachedFile()) );

    	// Delete test submission so that it does not interfere with other tests
    	submissionRepository.delete(testSubmission);

        // Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER", username = "test@ipb.com")
    public void storeFileForSubmissionMethodShouldReturnBadRequestStatusCodeBecauseOfMissingSubmission() throws Exception {
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
    			UserType.USER,
					null,
					null,
					null
    		)
    	);

    	// Test if the response code is 400 BAD REQUEST
    	MockMultipartFile fileAttached = new MockMultipartFile(
    		"file", // must match request param's name
    		"test-file.txt",
    		"text/plain",
    		"Text inside file".getBytes()
    	);
    	MvcResult result = this.mockMvc.perform(
        	multipart("/contact/1/file")
        		.file(fileAttached)
        ).andExpect( status().isBadRequest() ).andReturn();

    	// Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Contact form submission with this ID does not exist!");

        // Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER", username = "test@ipb.com")
    public void storeFileForSubmissionMethodShouldReturnBadRequestStatusCodeBecauseOfExistingFileAttachment() throws Exception {
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
    			UserType.USER,
					null,
					null,
					null
    		)
    	);

    	// Insert test submission
    	ContactFormSubmissionEntity testSubmission = submissionRepository.save(
			new ContactFormSubmissionEntity(
    			null, // id is auto-generated
    			"test@ipb.com", // must match username in @WithMockUser annotation
    			"I found an error...",
    			"C:/some-file.txt" // submission has file attached already
    		)
    	);

    	// Test if the response code is 400 BAD REQUEST
    	Long submissionId = testSubmission.getId();
    	MockMultipartFile fileAttached = new MockMultipartFile(
    		"file", // must match request param's name
    		"test-file.txt",
    		"text/plain",
    		"Text inside file".getBytes()
    	);
    	MvcResult result = this.mockMvc.perform(
        	multipart("/contact/" + submissionId + "/file")
        		.file(fileAttached)
        ).andExpect( status().isBadRequest() ).andReturn();

    	// Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Contact form submission already has a file attached!");

    	// Test if the test submission's file path has been updated
    	ContactFormSubmissionEntity retrievedTestSubmission = submissionRepository.findById(submissionId).get();
    	assertFalse( retrievedTestSubmission.getPathToAttachedFile().contains("test-file.txt") );

    	// Delete test submission so that it does not interfere with other tests
    	submissionRepository.delete(testSubmission);

        // Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }

    /**
     * getFileAttachedForSubmission() tests
     * (GET "/contact/{id}/file")
     */

    @Test
    @Transactional
    public void getFileAttachedForSubmissionMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform(
        	get("/contact/1/file")
        ).andExpect( status().isUnauthorized() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void getFileAttachedForSubmissionMethodShouldReturnForbiddenStatusCode() throws Exception {
        this.mockMvc.perform(
        	get("/contact/1/file")
        ).andExpect( status().isForbidden() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void getFileAttachedForSubmissionMethodShouldReturnOKStatusCode() throws Exception {
    	// Insert test submission
    	ContactFormSubmissionEntity testSubmission = submissionRepository.save(
			new ContactFormSubmissionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"I found an error...",
    			null
    		)
    	);

    	// Create file & attach it to submission
    	Long submissionId = testSubmission.getId();
    	String pathToAttachedFile = SUBMISSIONS_FILES_DIRECTORY + "/submission" + submissionId + "_test-file.txt";
    	testSubmission.setPathToAttachedFile(pathToAttachedFile);
    	submissionRepository.save(testSubmission);
    	Files.write( Paths.get(pathToAttachedFile), "Text inside file".getBytes() );

    	// Test if the response code is 200 OK
    	MvcResult result = this.mockMvc.perform(
        	get("/contact/" + submissionId + "/file")
        ).andExpect( status().isOk() ).andReturn();

    	// Test if the response headers & received bytes are correct
    	assertEquals(
    		"attachment; filename=\"submission" + submissionId + "_test-file.txt\"", // expected
    		result.getResponse().getHeaderValue("Content-disposition") // actual
    	);
    	byte[] receivedFileBytes = result.getResponse().getContentAsByteArray();
    	assertTrue( Arrays.equals("Text inside file".getBytes(), receivedFileBytes) );

    	// Delete attached file so that it does not interfere with other tests
    	Files.deleteIfExists( Paths.get(pathToAttachedFile) );

    	// Delete test submission so that it does not interfere with other tests
    	submissionRepository.delete(testSubmission);
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void getFileAttachedForSubmissionMethodShouldReturnBadRequestStatusCodeBecauseNoFileAttached() throws Exception {
    	// Insert test submission
    	ContactFormSubmissionEntity testSubmission = submissionRepository.save(
			new ContactFormSubmissionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"I found an error...",
    			null
    		)
    	);

    	// Test if the response code is 400 BAD REQUEST
    	Long submissionId = testSubmission.getId();
    	MvcResult result = this.mockMvc.perform(
        	get("/contact/" + submissionId + "/file")
        ).andExpect( status().isBadRequest() ).andReturn();

    	// Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Contact form submission does not have a file attached!");

    	// Delete test submission so that it does not interfere with other tests
    	submissionRepository.delete(testSubmission);
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void getFileAttachedForSubmissionMethodShouldReturnBadRequestStatusCodeBecauseOfMissingSubmission() throws Exception {
    	// Insert test submission
    	ContactFormSubmissionEntity testSubmission = submissionRepository.save(
			new ContactFormSubmissionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"I found an error...",
    			null
    		)
    	);

    	// Create file & attach it to submission
    	Long submissionId = testSubmission.getId();
    	String pathToAttachedFile = SUBMISSIONS_FILES_DIRECTORY + "/submission" + submissionId + "_test-file.txt";
    	testSubmission.setPathToAttachedFile(pathToAttachedFile);
    	submissionRepository.save(testSubmission);
    	Files.write( Paths.get(pathToAttachedFile), "Text inside file".getBytes() );

    	// Delete test submission so that its attached file cannot be retrieved
    	// and so that the submission does not interfere with other tests
    	submissionRepository.delete(testSubmission);

    	// Test if the response code is 400 BAD REQUEST
    	MvcResult result = this.mockMvc.perform(
        	get("/contact/" + submissionId + "/file")
        ).andExpect( status().isBadRequest() ).andReturn();

    	// Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Contact form submission with this ID does not exist!");

    	// Delete attached file so that it does not interfere with other tests
    	Files.deleteIfExists( Paths.get(pathToAttachedFile) );
    }

    /**
     * getAllSubmissions() tests
     * (GET "/contact/all")
     */

    @Test
    @Transactional
    public void getAllSubmissionsMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform(
        	get("/contact/all")
        ).andExpect( status().isUnauthorized() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void getAllSubmissionsMethodShouldReturnForbiddenStatusCode() throws Exception {
        this.mockMvc.perform(
        	get("/contact/all")
        ).andExpect( status().isForbidden() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void getAllSubmissionsMethodShouldReturnOKStatusCode() throws Exception {
    	// Insert test submission
    	ContactFormSubmissionEntity firstTestSubmission = submissionRepository.save(
			new ContactFormSubmissionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"I found an error...",
    			null
    		)
    	);

    	// Insert another test submission
    	ContactFormSubmissionEntity secondTestSubmission = submissionRepository.save(
			new ContactFormSubmissionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"I found ANOTHER error...",
    			null
    		)
    	);

    	// Test if the response code is 200 OK
    	MvcResult result = this.mockMvc.perform(
        	get("/contact/all")
        ).andExpect( status().isOk() ).andReturn();

    	// Test if the response string contains the submissions' data
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("I found an error...");
    	assertThat(responseString).contains("I found ANOTHER error...");

    	// Delete test submissions so that they do not interfere with other tests
    	submissionRepository.delete(firstTestSubmission);
    	submissionRepository.delete(secondTestSubmission);
    }

    /**
     * getSubmissionById() tests
     * (GET "/contact/{id}")
     */

    @Test
    @Transactional
    public void getSubmissionByIdMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform(
        	get("/contact/1")
        ).andExpect( status().isUnauthorized() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void getSubmissionByIdMethodShouldReturnForbiddenStatusCode() throws Exception {
        this.mockMvc.perform(
        	get("/contact/1")
        ).andExpect( status().isForbidden() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void getSubmissionByIdMethodShouldReturnNotFoundStatusCode() throws Exception {
        this.mockMvc.perform(
        	get("/contact/1")
        ).andExpect( status().isNotFound() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void getSubmissionByIdMethodShouldReturnOKStatusCode() throws Exception {
    	// Insert test submission
    	ContactFormSubmissionEntity testSubmission = submissionRepository.save(
			new ContactFormSubmissionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"I found an error...",
    			null
    		)
    	);

    	// Test if the response code is 200 OK
    	Long submissionId = testSubmission.getId();
    	MvcResult result = this.mockMvc.perform(
        	get("/contact/" + submissionId)
        ).andExpect( status().isOk() ).andReturn();

    	// Test if the response string contains the submission's data
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("I found an error...");

    	// Delete test submission so that it does not interfere with other tests
    	submissionRepository.delete(testSubmission);
    }

    /**
     * deleteSubmissionById() tests
     * (DELETE "/contact/{id}")
     */

    @Test
    @Transactional
    public void deleteSubmissionByIdMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform(
        	delete("/contact/1")
        ).andExpect( status().isUnauthorized() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void deleteSubmissionByIdMethodShouldReturnForbiddenStatusCode() throws Exception {
        this.mockMvc.perform(
        	delete("/contact/1")
        ).andExpect( status().isForbidden() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void deleteSubmissionByIdMethodShouldReturnNotFoundStatusCode() throws Exception {
        this.mockMvc.perform(
        	delete("/contact/1")
        ).andExpect( status().isNotFound() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void deleteSubmissionByIdMethodShouldReturnOKStatusCode() throws Exception {
    	// Insert test submission
    	ContactFormSubmissionEntity testSubmission = submissionRepository.save(
			new ContactFormSubmissionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"I found an error...",
    			null
    		)
    	);

    	// Test if the response code is 200 OK
    	Long submissionId = testSubmission.getId();
    	MvcResult result = this.mockMvc.perform(
        	delete("/contact/" + submissionId)
        ).andExpect( status().isOk() ).andReturn();

    	// Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Successfully deleted contact form submission with ID");

    	// Test if the submission was deleted and perform a delete operation just in case
    	// (so that the submission does not interfere with other tests)
    	assertTrue( !submissionRepository.existsById(submissionId) );
    	submissionRepository.delete(testSubmission);
    }

    /**
     * addObjectSuggestion() tests
     * (POST "/contact/object-suggestions")
     */

    @Test
    @Transactional
    public void addObjectSuggestionMethodShouldReturnUnauthorizedStatusCode() throws Exception {
    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("name", "My Object Suggestion");
    	postRequestBody.put("description", "My suggested object's description");
    	postRequestBody.put("latitude", "1.0");
    	postRequestBody.put("longitude", "1.0");
    	postRequestBody.put("type", "LANDMARK");

        this.mockMvc.perform(
        	post("/contact/object-suggestions")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content( postRequestBody.toString() )
        ).andExpect( status().isUnauthorized() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void addObjectSuggestionMethodShouldReturnBadRequestStatusCodeBecauseOfMissingUser() throws Exception {
    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("name", "My Object Suggestion");
    	postRequestBody.put("description", "My suggested object's description");
    	postRequestBody.put("latitude", "1.0");
    	postRequestBody.put("longitude", "1.0");
    	postRequestBody.put("type", "LANDMARK");

    	// Test if the response code is 400 BAD REQUEST
        MvcResult result = this.mockMvc.perform(
        	post("/contact/object-suggestions")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content( postRequestBody.toString() )
        ).andExpect( status().isBadRequest() ).andReturn();

        // Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("User with this e-mail does not exist!");
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER", username = "test@ipb.com")
    public void addObjectSuggestionMethodShouldReturnCreatedStatusCode() throws Exception {
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
    			UserType.USER,
					null,
					null,
					null
    		)
    	);

    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("name", "My Object Suggestion");
    	postRequestBody.put("description", "My suggested object's description");
    	postRequestBody.put("latitude", "1.0");
    	postRequestBody.put("longitude", "1.0");
    	postRequestBody.put("type", "LANDMARK");

    	// Test if the response code is 201 CREATED
        MvcResult result = this.mockMvc.perform(
        	post("/contact/object-suggestions")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content( postRequestBody.toString() )
        ).andExpect( status().isCreated() ).andReturn();

        // Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Successfully submitted object suggestion");

    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER", username = "test@ipb.com")
    public void addObjectSuggestionMethodShouldReturnBadRequestStatusCodeBecauseOfBindingResultErrors() throws Exception {
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
    			UserType.USER,
					null,
					null,
					null
    		)
    	);

    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("name", ""); // must not be blank
    	postRequestBody.put("description", ""); // must not be blank
    	postRequestBody.put("latitude", "1.0");
    	postRequestBody.put("longitude", "1.0");
    	postRequestBody.put("type", "LANDMARK");

    	// Test if the response code is 400 BAD REQUEST
        MvcResult result = this.mockMvc.perform(
        	post("/contact/object-suggestions")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content( postRequestBody.toString() )
        ).andExpect( status().isBadRequest() ).andReturn();

        // Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Suggested object's name must not be null or blank!");
    	assertThat(resultString).contains("Suggested object's description must not be null or blank!");

    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }

    /**
     * getObjectSuggestionById() tests
     * (GET "/contact/object-suggestions/{id}")
     */

    @Test
    @Transactional
    public void getObjectSuggestionByIdMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform(
        	get("/contact/object-suggestions/1")
        ).andExpect( status().isUnauthorized() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void getObjectSuggestionByIdMethodShouldReturnForbiddenStatusCode() throws Exception {
        this.mockMvc.perform(
        	get("/contact/object-suggestions/1")
        ).andExpect( status().isForbidden() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void getObjectSuggestionByIdMethodShouldReturnNotFoundStatusCode() throws Exception {
        this.mockMvc.perform(
        	get("/contact/object-suggestions/1")
        ).andExpect( status().isNotFound() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void getObjectSuggestionByIdMethodShouldReturnOKStatusCode() throws Exception {
    	// Insert test object suggestion
    	ObjectSuggestionEntity testObjectSuggestion = objectSuggestionRepository.save(
			new ObjectSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My Suggested Object",
    			"My suggested object's description...",
    			1.0,
    			1.0,
    			ObjectType.LANDMARK
    		)
    	);

    	// Test if the response code is 200 OK
    	Long objectSuggestionId = testObjectSuggestion.getId();
    	MvcResult result = this.mockMvc.perform(
        	get("/contact/object-suggestions/" + objectSuggestionId)
        ).andExpect( status().isOk() ).andReturn();

    	// Test if the response string contains the object suggestion's data
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("My Suggested Object");
    	assertThat(resultString).contains("My suggested object's description...");

    	// Delete test object suggestion so that it does not interfere with other tests
    	objectSuggestionRepository.delete(testObjectSuggestion);
    }

    /**
     * addEventSuggestion() tests
     * (POST "/contact/event-suggestions")
     */

    @Test
    @Transactional
    public void addEventSuggestionMethodShouldReturnUnauthorizedStatusCode() throws Exception {
    	// Insert test city
    	ObjectEntity testCity = new ObjectEntity();
    	testCity.setName("My City");
    	testCity.setDescription("My city's description");
    	testCity.setType(ObjectType.CITY);
    	testCity = objectRepository.save(testCity);
    	Long cityId = testCity.getId();

    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("name", "My Event Suggestion");
    	postRequestBody.put("description", "My suggested event's description");
    	postRequestBody.put("startDate", "10-10-1995 01:00");
    	postRequestBody.put("endDate", "10-10-1996 03:37");
    	postRequestBody.put("cityId", String.valueOf(cityId));

        this.mockMvc.perform(
        	post("/contact/event-suggestions")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content( postRequestBody.toString() )
        ).andExpect( status().isUnauthorized() );

        // Delete test city so that it does not interfere with other tests
        objectRepository.delete(testCity);
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void addEventSuggestionMethodShouldReturnBadRequestStatusCodeBecauseOfMissingUser() throws Exception {
    	// Insert test city
    	ObjectEntity testCity = new ObjectEntity();
    	testCity.setName("My City");
    	testCity.setDescription("My city's description");
    	testCity.setType(ObjectType.CITY);
    	testCity = objectRepository.save(testCity);
    	Long cityId = testCity.getId();

    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("name", "My Event Suggestion");
    	postRequestBody.put("description", "My suggested event's description");
    	postRequestBody.put("startDate", "10-10-1995 01:00");
    	postRequestBody.put("endDate", "10-10-1996 03:37");
    	postRequestBody.put("cityId", String.valueOf(cityId));

    	// Test if the response code is 400 BAD REQUEST
        MvcResult result = this.mockMvc.perform(
        	post("/contact/event-suggestions")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content( postRequestBody.toString() )
        ).andExpect( status().isBadRequest() ).andReturn();

        // Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("User with this e-mail does not exist!");

    	// Delete test city so that it does not interfere with other tests
        objectRepository.delete(testCity);
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER", username = "test@ipb.com")
    public void addEventSuggestionMethodShouldReturnCreatedStatusCode() throws Exception {
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
    			UserType.USER,
					null,
					null,
					null
    		)
    	);

    	// Insert test city
    	ObjectEntity testCity = new ObjectEntity();
    	testCity.setName("My City");
    	testCity.setDescription("My city's description");
    	testCity.setType(ObjectType.CITY);
    	testCity = objectRepository.save(testCity);
    	Long cityId = testCity.getId();

    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("name", "My Event Suggestion");
    	postRequestBody.put("description", "My suggested event's description");
    	postRequestBody.put("startDate", "10-10-1995 01:00");
    	postRequestBody.put("endDate", "10-10-1996 03:37");
    	postRequestBody.put("cityId", String.valueOf(cityId));

    	// Test if the response code is 201 CREATED
        MvcResult result = this.mockMvc.perform(
        	post("/contact/event-suggestions")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content( postRequestBody.toString() )
        ).andExpect( status().isCreated() ).andReturn();

        // Test if the response string is a success message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Successfully submitted event suggestion");

    	// Delete test city so that it does not interfere with other tests
        objectRepository.delete(testCity);

    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER", username = "test@ipb.com")
    public void addEventSuggestionMethodShouldReturnBadRequestStatusCodeBecauseOfBindingResultErrors() throws Exception {
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
    			UserType.USER,
					null,
					null,
					null
    		)
    	);

    	JSONObject postRequestBody = new JSONObject();
    	postRequestBody.put("name", ""); // must not be blank
    	postRequestBody.put("description", ""); // must not be blank
    	postRequestBody.put("startDate", "10-10-1995 01:00");
    	postRequestBody.put("endDate", "10-10-1996 03:37");
    	postRequestBody.put("cityId", null); // city can't be null

    	// Test if the response code is 400 BAD REQUEST
        MvcResult result = this.mockMvc.perform(
        	post("/contact/event-suggestions")
        		.contentType(MediaType.APPLICATION_JSON)
        		.content( postRequestBody.toString() )
        ).andExpect( status().isBadRequest() ).andReturn();

        // Test if the response string is an error message
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("Event's name must not be null or blank!");
    	assertThat(resultString).contains("Event's description must not be null or blank!");
    	assertThat(resultString).contains("Event's city ID must not be null!");

    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }

    /**
     * getEventSuggestionById() tests
     * (GET "/contact/event-suggestions/{id}")
     */

    @Test
    @Transactional
    public void getEventSuggestionByIdMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform(
        	get("/contact/event-suggestions/1")
        ).andExpect( status().isUnauthorized() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void getEventSuggestionByIdMethodShouldReturnForbiddenStatusCode() throws Exception {
        this.mockMvc.perform(
        	get("/contact/event-suggestions/1")
        ).andExpect( status().isForbidden() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void getEventSuggestionByIdMethodShouldReturnNotFoundStatusCode() throws Exception {
        this.mockMvc.perform(
        	get("/contact/event-suggestions/1")
        ).andExpect( status().isNotFound() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void getEventSuggestionByIdMethodShouldReturnOKStatusCode() throws Exception {
    	// Insert test event suggestion
    	EventSuggestionEntity testEventSuggestion = eventSuggestionRepository.save(
			new EventSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My Suggested Event",
    			"My suggested event's description...",
    			1L,
    			new Date(),
    			new Date(),
    			1.0,
    			1.0
    		)
    	);

    	// Test if the response code is 200 OK
    	Long eventSuggestionId = testEventSuggestion.getId();
    	MvcResult result = this.mockMvc.perform(
        	get("/contact/event-suggestions/" + eventSuggestionId)
        ).andExpect( status().isOk() ).andReturn();

    	// Test if the response string contains the event suggestion's data
    	String resultString = result.getResponse().getContentAsString();
    	assertThat(resultString).contains("My Suggested Event");
    	assertThat(resultString).contains("My suggested event's description...");

    	// Delete test event suggestion so that it does not interfere with other tests
    	eventSuggestionRepository.delete(testEventSuggestion);
    }

    /**
     * getAllObjectSuggestions() tests
     * (GET "/contact/object-suggestions/all")
     */

    @Test
    @Transactional
    public void getAllObjectSuggestionsMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform(
        	get("/contact/object-suggestions/all")
        ).andExpect( status().isUnauthorized() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void getAllObjectSuggestionsMethodShouldReturnForbiddenStatusCode() throws Exception {
        this.mockMvc.perform(
        	get("/contact/object-suggestions/all")
        ).andExpect( status().isForbidden() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void getAllObjectSuggestionsMethodShouldReturnOKStatusCode() throws Exception {
    	// Insert test object suggestions
    	ObjectSuggestionEntity firstTestObjectSuggestion = objectSuggestionRepository.save(
			new ObjectSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My First Suggested Object",
    			"My first suggested object's description...",
    			1.0,
    			1.0,
    			ObjectType.LANDMARK
    		)
    	);
    	ObjectSuggestionEntity secondTestObjectSuggestion = objectSuggestionRepository.save(
			new ObjectSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My Second Suggested Object",
    			"My second suggested object's description...",
    			1.0,
    			1.0,
    			ObjectType.LANDMARK
    		)
    	);

    	// Test if the response code is 200 OK
    	MvcResult result = this.mockMvc.perform(
        	get("/contact/object-suggestions/all")
        ).andExpect( status().isOk() ).andReturn();

    	// Test if the response string contains the object suggestions' data
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("My First Suggested Object");
    	assertThat(responseString).contains("My first suggested object's description...");
    	assertThat(responseString).contains("My Second Suggested Object");
    	assertThat(responseString).contains("My second suggested object's description...");

    	// Delete test object suggestions so that they do not interfere with other tests
    	objectSuggestionRepository.delete(firstTestObjectSuggestion);
    	objectSuggestionRepository.delete(secondTestObjectSuggestion);
    }

    /**
     * getAllEventSuggestions() tests
     * (GET "/contact/event-suggestions/all")
     */

    @Test
    @Transactional
    public void getAllEventSuggestionsMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform(
        	get("/contact/event-suggestions/all")
        ).andExpect( status().isUnauthorized() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void getAllEventSuggestionsMethodShouldReturnForbiddenStatusCode() throws Exception {
        this.mockMvc.perform(
        	get("/contact/event-suggestions/all")
        ).andExpect( status().isForbidden() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void getAllEventSuggestionsMethodShouldReturnOKStatusCode() throws Exception {
    	// Insert test event suggestions
    	EventSuggestionEntity firstTestEventSuggestion = eventSuggestionRepository.save(
			new EventSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My First Suggested Event",
    			"My first suggested event's description...",
    			1L,
    			new Date(),
    			new Date(),
    			1.0,
    			1.0
    		)
    	);
    	EventSuggestionEntity secondTestEventSuggestion = eventSuggestionRepository.save(
			new EventSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My Second Suggested Event",
    			"My second suggested event's description...",
    			1L,
    			new Date(),
    			new Date(),
    			1.0,
    			1.0
    		)
    	);

    	// Test if the response code is 200 OK
    	MvcResult result = this.mockMvc.perform(
        	get("/contact/event-suggestions/all")
        ).andExpect( status().isOk() ).andReturn();

    	// Test if the response string contains the event suggestions' data
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("My First Suggested Event");
    	assertThat(responseString).contains("My first suggested event's description...");
    	assertThat(responseString).contains("My Second Suggested Event");
    	assertThat(responseString).contains("My second suggested event's description...");

    	// Delete test event suggestions so that they do not interfere with other tests
    	eventSuggestionRepository.delete(firstTestEventSuggestion);
    	eventSuggestionRepository.delete(secondTestEventSuggestion);
    }

    /**
     * acceptObjectSuggestionById() tests
     * (POST "/contact/object-suggestions/{id}/accept")
     */

    @Test
    @Transactional
    public void acceptObjectSuggestionByIdMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform(
        	post("/contact/object-suggestions/1/accept")
        ).andExpect( status().isUnauthorized() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void acceptObjectSuggestionByIdMethodShouldReturnForbiddenStatusCode() throws Exception {
        this.mockMvc.perform(
        	post("/contact/object-suggestions/1/accept")
        ).andExpect( status().isForbidden() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void acceptObjectSuggestionByIdMethodShouldReturnNotFoundStatusCode() throws Exception {
        MvcResult result = this.mockMvc.perform(
        	post("/contact/object-suggestions/1/accept")
        ).andExpect( status().isNotFound() ).andReturn();

        // Test if the response string is an error message
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("Object suggestion with this ID does not exist!");
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void acceptObjectSuggestionByIdMethodShouldReturnOKStatusCodeWithNoEmailSent() throws Exception {
    	// Insert test object suggestion
    	ObjectSuggestionEntity testObjectSuggestion = objectSuggestionRepository.save(
			new ObjectSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My Suggested Object",
    			"My suggested object's description...",
    			1.0,
    			1.0,
    			ObjectType.LANDMARK
    		)
    	);

    	// Test if the response code is 200 OK
    	Long objectSuggestionId = testObjectSuggestion.getId();
        MvcResult result = this.mockMvc.perform(
        	post("/contact/object-suggestions/" + objectSuggestionId + "/accept")
        ).andExpect( status().isOk() ).andReturn();

        // Test if the response string is a success message (and no e-mail was sent)
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("Successfully accepted & added object to database from suggestion");
    	assertThat(responseString).contains("No e-mail sent (user with this e-mail address does not exist)!");

    	// Test if the object suggestion was deleted from database and perform delete operation just in case
    	assertTrue( !objectSuggestionRepository.existsById(objectSuggestionId) );
    	objectSuggestionRepository.delete(testObjectSuggestion);

    	// Test if the object was added to the database and delete it afterwards
    	// (so it doesn't interfere with other tests)
    	assertTrue( objectRepository.count() == 1 );
    	ObjectEntity retrievedObjectEntity = objectRepository.findAll().get(0);
    	assertTrue( retrievedObjectEntity.getName().equals("My Suggested Object") );
    	assertTrue( retrievedObjectEntity.getDescription().equals("My suggested object's description...") );
    	assertTrue( retrievedObjectEntity.getType() == ObjectType.LANDMARK );
    	objectRepository.delete(retrievedObjectEntity);
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void acceptObjectSuggestionByIdMethodShouldReturnOKStatusCodeWithEmailSent() throws Exception {
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
    			UserType.USER,
					null,
					null,
					null
    		)
    	);

    	// Insert test object suggestion
    	ObjectSuggestionEntity testObjectSuggestion = objectSuggestionRepository.save(
			new ObjectSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My Suggested Object",
    			"My suggested object's description...",
    			1.0,
    			1.0,
    			ObjectType.LANDMARK
    		)
    	);

    	// Test if the response code is 200 OK
    	Long objectSuggestionId = testObjectSuggestion.getId();
        MvcResult result = this.mockMvc.perform(
        	post("/contact/object-suggestions/" + objectSuggestionId + "/accept")
        ).andExpect( status().isOk() ).andReturn();

        // Test if the response string is a success message (and e-mail was sent)
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("Successfully accepted & added object to database from suggestion");
    	assertThat(responseString).contains("A notification e-mail was sent");

    	// Test if the object suggestion was deleted from database and perform delete operation just in case
    	assertTrue( !objectSuggestionRepository.existsById(objectSuggestionId) );
    	objectSuggestionRepository.delete(testObjectSuggestion);

    	// Test if the object was added to the database and delete it afterwards
    	// (so it doesn't interfere with other tests)
    	assertTrue( objectRepository.count() == 1 );
    	ObjectEntity retrievedObjectEntity = objectRepository.findAll().get(0);
    	assertTrue( retrievedObjectEntity.getName().equals("My Suggested Object") );
    	assertTrue( retrievedObjectEntity.getDescription().equals("My suggested object's description...") );
    	assertTrue( retrievedObjectEntity.getType() == ObjectType.LANDMARK );
    	objectRepository.delete(retrievedObjectEntity);

    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }

    /**
     * rejectObjectSuggestionById() tests
     * (POST "/contact/object-suggestions/{id}/reject")
     */

    @Test
    @Transactional
    public void rejectObjectSuggestionByIdMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform(
        	post("/contact/object-suggestions/1/reject")
        ).andExpect( status().isUnauthorized() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void rejectObjectSuggestionByIdMethodShouldReturnForbiddenStatusCode() throws Exception {
        this.mockMvc.perform(
        	post("/contact/object-suggestions/1/reject")
        ).andExpect( status().isForbidden() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void rejectObjectSuggestionByIdMethodShouldReturnNotFoundStatusCode() throws Exception {
        MvcResult result = this.mockMvc.perform(
        	post("/contact/object-suggestions/1/reject")
        ).andExpect( status().isNotFound() ).andReturn();

        // Test if the response string is an error message
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("Object suggestion with this ID does not exist!");
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void rejectObjectSuggestionByIdMethodShouldReturnOKStatusCodeWithNoEmailSent() throws Exception {
    	// Insert test object suggestion
    	ObjectSuggestionEntity testObjectSuggestion = objectSuggestionRepository.save(
			new ObjectSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My Suggested Object",
    			"My suggested object's description...",
    			1.0,
    			1.0,
    			ObjectType.LANDMARK
    		)
    	);

    	// Test if the response code is 200 OK
    	Long objectSuggestionId = testObjectSuggestion.getId();
        MvcResult result = this.mockMvc.perform(
        	post("/contact/object-suggestions/" + objectSuggestionId + "/reject")
        ).andExpect( status().isOk() ).andReturn();

        // Test if the response string is a success message (and no e-mail was sent)
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("Successfully rejected object suggestion");
    	assertThat(responseString).contains("No e-mail sent (user with this e-mail address does not exist)!");

    	// Test if the object suggestion was deleted from database and perform delete operation just in case
    	assertTrue( !objectSuggestionRepository.existsById(objectSuggestionId) );
    	objectSuggestionRepository.delete(testObjectSuggestion);

    	// Test if the object was added to the database
    	assertTrue( objectRepository.count() == 0 );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void rejectObjectSuggestionByIdMethodShouldReturnOKStatusCodeWithEmailSent() throws Exception {
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
    			UserType.USER,
					null,
					null,
					null
    		)
    	);

    	// Insert test object suggestion
    	ObjectSuggestionEntity testObjectSuggestion = objectSuggestionRepository.save(
			new ObjectSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My Suggested Object",
    			"My suggested object's description...",
    			1.0,
    			1.0,
    			ObjectType.LANDMARK
    		)
    	);

    	// Test if the response code is 200 OK
    	Long objectSuggestionId = testObjectSuggestion.getId();
        MvcResult result = this.mockMvc.perform(
        	post("/contact/object-suggestions/" + objectSuggestionId + "/reject")
        ).andExpect( status().isOk() ).andReturn();

        // Test if the response string is a success message (and e-mail was sent)
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("Successfully rejected object suggestion");
    	assertThat(responseString).contains("A notification e-mail was sent");

    	// Test if the object suggestion was deleted from database and perform delete operation just in case
    	assertTrue( !objectSuggestionRepository.existsById(objectSuggestionId) );
    	objectSuggestionRepository.delete(testObjectSuggestion);

    	// Test if the object was added to the database
    	assertTrue( objectRepository.count() == 0 );

    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void rejectObjectSuggestionByIdMethodShouldReturnOKStatusCodeWithNoEmailSentAndRejectionMessage() throws Exception {
    	// Insert test object suggestion
    	ObjectSuggestionEntity testObjectSuggestion = objectSuggestionRepository.save(
			new ObjectSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My Suggested Object",
    			"My suggested object's description...",
    			1.0,
    			1.0,
    			ObjectType.LANDMARK
    		)
    	);

    	// Test if the response code is 200 OK
    	Long objectSuggestionId = testObjectSuggestion.getId();
        MvcResult result = this.mockMvc.perform(
        	post("/contact/object-suggestions/" + objectSuggestionId + "/reject")
        		.param("message", "Your object was rejected because of reasons.")
        ).andExpect( status().isOk() ).andReturn();

        // Test if the response string is a success message (and no e-mail was sent)
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("Successfully rejected object suggestion");
    	assertThat(responseString).contains("No e-mail sent (user with this e-mail address does not exist)!");

    	// Test if the object suggestion was deleted from database and perform delete operation just in case
    	assertTrue( !objectSuggestionRepository.existsById(objectSuggestionId) );
    	objectSuggestionRepository.delete(testObjectSuggestion);

    	// Test if the object was added to the database
    	assertTrue( objectRepository.count() == 0 );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void rejectObjectSuggestionByIdMethodShouldReturnOKStatusCodeWithEmailSentAndRejectionMessage() throws Exception {
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
    			UserType.USER,
					null,
					null,
					null
    		)
    	);

    	// Insert test object suggestion
    	ObjectSuggestionEntity testObjectSuggestion = objectSuggestionRepository.save(
			new ObjectSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My Suggested Object",
    			"My suggested object's description...",
    			1.0,
    			1.0,
    			ObjectType.LANDMARK
    		)
    	);

    	// Test if the response code is 200 OK
    	Long objectSuggestionId = testObjectSuggestion.getId();
        MvcResult result = this.mockMvc.perform(
        	post("/contact/object-suggestions/" + objectSuggestionId + "/reject")
        		.param("message", "Your object was rejected because of reasons.")
        ).andExpect( status().isOk() ).andReturn();

        // Test if the response string is a success message (and e-mail was sent)
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("Successfully rejected object suggestion");
    	assertThat(responseString).contains("A notification e-mail was sent");

    	// Test if the object suggestion was deleted from database and perform delete operation just in case
    	assertTrue( !objectSuggestionRepository.existsById(objectSuggestionId) );
    	objectSuggestionRepository.delete(testObjectSuggestion);

    	// Test if the object was added to the database
    	assertTrue( objectRepository.count() == 0 );

    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }

    /**
     * acceptEventSuggestionById() tests
     * (POST "/contact/event-suggestions/{id}/accept")
     */

    @Test
    @Transactional
    public void acceptEventSuggestionByIdMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform(
        	post("/contact/event-suggestions/1/accept")
        ).andExpect( status().isUnauthorized() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void acceptEventSuggestionByIdMethodShouldReturnForbiddenStatusCode() throws Exception {
        this.mockMvc.perform(
        	post("/contact/event-suggestions/1/accept")
        ).andExpect( status().isForbidden() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void acceptEventSuggestionByIdMethodShouldReturnNotFoundStatusCode() throws Exception {
        MvcResult result = this.mockMvc.perform(
        	post("/contact/event-suggestions/1/accept")
        ).andExpect( status().isNotFound() ).andReturn();

        // Test if the response string is an error message
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("Event suggestion with this ID does not exist!");
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void acceptEventSuggestionByIdMethodShouldReturnOKStatusCodeWithNoEmailSent() throws Exception {
    	// Insert test city
    	CityEntity testCity = new CityEntity();
    	testCity.setName("My City");
    	testCity.setDescription("My city's description");
    	testCity.setType(ObjectType.CITY);
    	testCity = cityRepository.save(testCity);
    	Long cityId = testCity.getId();

    	// Insert test event suggestion
    	EventSuggestionEntity testEventSuggestion = eventSuggestionRepository.save(
			new EventSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My Suggested Event",
    			"My suggested event's description...",
    			cityId,
    			new Date(),
    			new Date(),
    			1.0,
    			1.0
    		)
    	);

    	// Test if the response code is 200 OK
    	Long eventSuggestionId = testEventSuggestion.getId();
        MvcResult result = this.mockMvc.perform(
        	post("/contact/event-suggestions/" + eventSuggestionId + "/accept")
        ).andExpect( status().isOk() ).andReturn();

        // Test if the response string is a success message (and no e-mail was sent)
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("Successfully accepted & added event to database from suggestion");
    	assertThat(responseString).contains("No e-mail sent (user with this e-mail address does not exist)!");

    	// Test if the event suggestion was deleted from database and perform delete operation just in case
    	assertTrue( !eventSuggestionRepository.existsById(eventSuggestionId) );
    	eventSuggestionRepository.delete(testEventSuggestion);

    	// Test if the event was added to the database
    	// and delete it afterwards, along with the test city
    	// (so they doesn't interfere with other tests)
    	assertTrue( eventRepository.count() == 1 );
    	EventEntity retrievedEventEntity = eventRepository.findAll().get(0);
    	assertTrue( retrievedEventEntity.getName().equals("My Suggested Event") );
    	assertTrue( retrievedEventEntity.getDescription().equals("My suggested event's description...") );
    	assertTrue( retrievedEventEntity.getType() == ObjectType.EVENT );
    	cityRepository.delete(testCity);
    	eventRepository.delete(retrievedEventEntity);
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void acceptEventSuggestionByIdMethodShouldReturnOKStatusCodeWithEmailSent() throws Exception {
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
    			UserType.USER,
					null,
					null,
					null
    		)
    	);

    	// Insert test city
    	CityEntity testCity = new CityEntity();
    	testCity.setName("My City");
    	testCity.setDescription("My city's description");
    	testCity.setType(ObjectType.CITY);
    	testCity = cityRepository.save(testCity);
    	Long cityId = testCity.getId();

    	// Insert test event suggestion
    	EventSuggestionEntity testEventSuggestion = eventSuggestionRepository.save(
			new EventSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My Suggested Event",
    			"My suggested event's description...",
    			cityId,
    			new Date(),
    			new Date(),
    			1.0,
    			1.0
    		)
    	);

    	// Test if the response code is 200 OK
    	Long eventSuggestionId = testEventSuggestion.getId();
        MvcResult result = this.mockMvc.perform(
        	post("/contact/event-suggestions/" + eventSuggestionId + "/accept")
        ).andExpect( status().isOk() ).andReturn();

        // Test if the response string is a success message (and no e-mail was sent)
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("Successfully accepted & added event to database from suggestion");
    	assertThat(responseString).contains("A notification e-mail was sent");

    	// Test if the event suggestion was deleted from database and perform delete operation just in case
    	assertTrue( !eventSuggestionRepository.existsById(eventSuggestionId) );
    	eventSuggestionRepository.delete(testEventSuggestion);

    	// Test if the event was added to the database
    	// and delete it afterwards, along with the test city
    	// (so they doesn't interfere with other tests)
    	assertTrue( eventRepository.count() == 1 );
    	EventEntity retrievedEventEntity = eventRepository.findAll().get(0);
    	assertTrue( retrievedEventEntity.getName().equals("My Suggested Event") );
    	assertTrue( retrievedEventEntity.getDescription().equals("My suggested event's description...") );
    	assertTrue( retrievedEventEntity.getType() == ObjectType.EVENT );
    	cityRepository.delete(testCity);
    	eventRepository.delete(retrievedEventEntity);

    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void acceptEventSuggestionByIdMethodShouldReturnNotFoundStatusCodeBecauseOfMissingCity() throws Exception {
    	// Insert test event suggestion
    	EventSuggestionEntity testEventSuggestion = eventSuggestionRepository.save(
			new EventSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My Suggested Event",
    			"My suggested event's description...",
    			1L, // no such city
    			new Date(),
    			new Date(),
    			1.0,
    			1.0
    		)
    	);

    	// Test if the response code is 404 NOT FOUND
    	Long eventSuggestionId = testEventSuggestion.getId();
        MvcResult result = this.mockMvc.perform(
        	post("/contact/event-suggestions/" + eventSuggestionId + "/accept")
        ).andExpect( status().isNotFound() ).andReturn();

        // Test if the response string is an error message
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("Failed to accept event suggestion");
    	assertThat(responseString).contains("The suggested city does not exist");

    	// Test to make sure that the event was not added to the database
    	assertTrue( eventRepository.count() == 0 );

    	// Delete test event suggestion from database so that it does not interfere with other tests
    	eventSuggestionRepository.delete(testEventSuggestion);
    }

    /**
     * rejectEventSuggestionById() tests
     * (POST "/contact/event-suggestions/{id}/reject")
     */

    @Test
    @Transactional
    public void rejectEventSuggestionByIdMethodShouldReturnUnauthorizedStatusCode() throws Exception {
        this.mockMvc.perform(
        	post("/contact/event-suggestions/1/reject")
        ).andExpect( status().isUnauthorized() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "USER")
    public void rejectEventSuggestionByIdMethodShouldReturnForbiddenStatusCode() throws Exception {
        this.mockMvc.perform(
        	post("/contact/event-suggestions/1/reject")
        ).andExpect( status().isForbidden() );
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void rejectEventSuggestionByIdMethodShouldReturnNotFoundStatusCode() throws Exception {
        MvcResult result = this.mockMvc.perform(
        	post("/contact/event-suggestions/1/reject")
        ).andExpect( status().isNotFound() ).andReturn();

        // Test if the response string is an error message
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("Event suggestion with this ID does not exist!");
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void rejectEventSuggestionByIdMethodShouldReturnOKStatusCodeWithNoEmailSent() throws Exception {
    	// Insert test city
    	CityEntity testCity = new CityEntity();
    	testCity.setName("My City");
    	testCity.setDescription("My city's description");
    	testCity.setType(ObjectType.CITY);
    	testCity = cityRepository.save(testCity);
    	Long cityId = testCity.getId();

    	// Insert test event suggestion
    	EventSuggestionEntity testEventSuggestion = eventSuggestionRepository.save(
			new EventSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My Suggested Event",
    			"My suggested event's description...",
    			cityId,
    			new Date(),
    			new Date(),
    			1.0,
    			1.0
    		)
    	);

    	// Test if the response code is 200 OK
    	Long eventSuggestionId = testEventSuggestion.getId();
        MvcResult result = this.mockMvc.perform(
        	post("/contact/event-suggestions/" + eventSuggestionId + "/reject")
        ).andExpect( status().isOk() ).andReturn();

        // Test if the response string is a success message (and no e-mail was sent)
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("Successfully rejected event suggestion");
    	assertThat(responseString).contains("No e-mail sent (user with this e-mail address does not exist)!");

    	// Test if the event suggestion was deleted from database and perform delete operation just in case
    	assertTrue( !eventSuggestionRepository.existsById(eventSuggestionId) );
    	eventSuggestionRepository.delete(testEventSuggestion);

    	// Test if the event was added to the database
    	assertTrue( eventRepository.count() == 0 );

    	// Delete test city so it does not interfere with other tests
    	cityRepository.delete(testCity);
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void rejectEventSuggestionByIdMethodShouldReturnOKStatusCodeWithEmailSent() throws Exception {
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
    			UserType.USER,
					null,
					null,
					null
    		)
    	);

    	// Insert test city
    	CityEntity testCity = new CityEntity();
    	testCity.setName("My City");
    	testCity.setDescription("My city's description");
    	testCity.setType(ObjectType.CITY);
    	testCity = cityRepository.save(testCity);
    	Long cityId = testCity.getId();

    	// Insert test event suggestion
    	EventSuggestionEntity testEventSuggestion = eventSuggestionRepository.save(
			new EventSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My Suggested Event",
    			"My suggested event's description...",
    			cityId,
    			new Date(),
    			new Date(),
    			1.0,
    			1.0
    		)
    	);

    	// Test if the response code is 200 OK
    	Long eventSuggestionId = testEventSuggestion.getId();
        MvcResult result = this.mockMvc.perform(
        	post("/contact/event-suggestions/" + eventSuggestionId + "/reject")
        ).andExpect( status().isOk() ).andReturn();

        // Test if the response string is a success message (and no e-mail was sent)
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("Successfully rejected event suggestion");
    	assertThat(responseString).contains("A notification e-mail was sent");

    	// Test if the event suggestion was deleted from database and perform delete operation just in case
    	assertTrue( !eventSuggestionRepository.existsById(eventSuggestionId) );
    	eventSuggestionRepository.delete(testEventSuggestion);

    	// Test if the event was added to the database
    	assertTrue( eventRepository.count() == 0 );

    	// Delete test city so that it does not interfere with other tests
    	cityRepository.delete(testCity);

    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void rejectEventSuggestionByIdMethodShouldReturnOKStatusCodeWithNoEmailSentAndRejectionMessage() throws Exception {
    	// Insert test city
    	CityEntity testCity = new CityEntity();
    	testCity.setName("My City");
    	testCity.setDescription("My city's description");
    	testCity.setType(ObjectType.CITY);
    	testCity = cityRepository.save(testCity);
    	Long cityId = testCity.getId();

    	// Insert test event suggestion
    	EventSuggestionEntity testEventSuggestion = eventSuggestionRepository.save(
			new EventSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My Suggested Event",
    			"My suggested event's description...",
    			cityId,
    			new Date(),
    			new Date(),
    			1.0,
    			1.0
    		)
    	);

    	// Test if the response code is 200 OK
    	Long eventSuggestionId = testEventSuggestion.getId();
        MvcResult result = this.mockMvc.perform(
        	post("/contact/event-suggestions/" + eventSuggestionId + "/reject")
        		.param("message", "Your event was rejected because of reasons.")
        ).andExpect( status().isOk() ).andReturn();

        // Test if the response string is a success message (and no e-mail was sent)
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("Successfully rejected event suggestion");
    	assertThat(responseString).contains("No e-mail sent (user with this e-mail address does not exist)!");

    	// Test if the event suggestion was deleted from database and perform delete operation just in case
    	assertTrue( !eventSuggestionRepository.existsById(eventSuggestionId) );
    	eventSuggestionRepository.delete(testEventSuggestion);

    	// Test if the event was added to the database
    	assertTrue( eventRepository.count() == 0 );

    	// Delete test city so it does not interfere with other tests
    	cityRepository.delete(testCity);
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void rejectEventSuggestionByIdMethodShouldReturnOKStatusCodeWithEmailSentAndRejectionMessage() throws Exception {
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
    			UserType.USER,
					null,
					null,
					null
    		)
    	);

    	// Insert test city
    	CityEntity testCity = new CityEntity();
    	testCity.setName("My City");
    	testCity.setDescription("My city's description");
    	testCity.setType(ObjectType.CITY);
    	testCity = cityRepository.save(testCity);
    	Long cityId = testCity.getId();

    	// Insert test event suggestion
    	EventSuggestionEntity testEventSuggestion = eventSuggestionRepository.save(
			new EventSuggestionEntity(
    			null, // id is auto-generated
    			"test@ipb.com",
    			"My Suggested Event",
    			"My suggested event's description...",
    			cityId,
    			new Date(),
    			new Date(),
    			1.0,
    			1.0
    		)
    	);

    	// Test if the response code is 200 OK
    	Long eventSuggestionId = testEventSuggestion.getId();
        MvcResult result = this.mockMvc.perform(
        	post("/contact/event-suggestions/" + eventSuggestionId + "/reject")
        		.param("message", "Your event was rejected because of reasons.")
        ).andExpect( status().isOk() ).andReturn();

        // Test if the response string is a success message (and no e-mail was sent)
    	String responseString = result.getResponse().getContentAsString();
    	assertThat(responseString).contains("Successfully rejected event suggestion");
    	assertThat(responseString).contains("A notification e-mail was sent");

    	// Test if the event suggestion was deleted from database and perform delete operation just in case
    	assertTrue( !eventSuggestionRepository.existsById(eventSuggestionId) );
    	eventSuggestionRepository.delete(testEventSuggestion);

    	// Test if the event was added to the database
    	assertTrue( eventRepository.count() == 0 );

    	// Delete test city so that it does not interfere with other tests
    	cityRepository.delete(testCity);

    	// Delete test user so that he does not interfere with other tests
    	userRepository.delete(testUser);
    }

}
