package com.ipb.platform.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.ipb.platform.dto.requests.ContactFormSubmissionRequestDTO;
import com.ipb.platform.dto.requests.EventSuggestionRequestDTO;
import com.ipb.platform.dto.requests.ObjectSuggestionRequestDTO;
import com.ipb.platform.dto.responses.ContactFormSubmissionResponseDTO;
import com.ipb.platform.dto.responses.EventSuggestionResponseDTO;
import com.ipb.platform.dto.responses.ObjectSuggestionResponseDTO;
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
import com.ipb.platform.services.ContactFormSubmissionService;
import com.ipb.platform.validation.CityNotFoundException;
import com.ipb.platform.validation.ContactFormSubmissionException;
import com.ipb.platform.validation.UserNotFoundException;

/**
 * This class implements unit tests for the ContactFormSubmissionServiceImpl class.
 * 
 * The @Transactional annotation is used to rollback 
 * database changes after a test has finished executing.
 * 
 * @author dvt32
 */
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class ContactFormSubmissionServiceImplTests {
	
	@Autowired
	private ContactFormSubmissionService submissionService;
	
	@Autowired
	private ContactFormSubmissionRepository submissionRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ObjectSuggestionRepository objectSuggestionRepository;
	
	@Autowired
	private EventSuggestionRepository eventSuggestionRepository;
	
	@Autowired
	private ObjectRepository objectRepository;
	
	@Autowired
	private EventRepository eventRepository;
	
	@Autowired
	private CityRepository cityRepository;
	
	private static final String SUBMISSIONS_FILES_DIRECTORY = System.getProperty("user.dir") + "/files/contact-form";
	
	/**
	 * Helper methods
	 */
	
	public ContactFormSubmissionRequestDTO getTestContactFormSubmissionRequestDTO() {
		ContactFormSubmissionRequestDTO testSubmissionRequestDTO = new ContactFormSubmissionRequestDTO();
		testSubmissionRequestDTO.setSenderEmail("test@ipb.com");
		testSubmissionRequestDTO.setMessage("I found an error...");
		return testSubmissionRequestDTO;
	}
	
	public ContactFormSubmissionEntity getTestContactFormSubmissionEntity() {
		ContactFormSubmissionEntity testSubmissionEntity = new ContactFormSubmissionEntity();
		testSubmissionEntity.setSenderEmail("test@ipb.com");
		testSubmissionEntity.setMessage("I found an error...");
		return testSubmissionEntity;
	}
	
	public UserEntity getTestUserEntity() {
		UserEntity testUserEntity = new UserEntity(
			null, // id is auto-generated
			"test@ipb.com",
			"123456",
			"123456",
			"Ivan", 
			"Ivanov",
			new java.sql.Date(0), 
			UserType.USER
		);
		return testUserEntity;
	}
	
	public ObjectSuggestionRequestDTO getTestObjectSuggestionRequestDTO() {
		ObjectSuggestionRequestDTO testObjectSuggestionRequestDTO = new ObjectSuggestionRequestDTO();
		testObjectSuggestionRequestDTO.setSenderEmail("test@ipb.com");
		testObjectSuggestionRequestDTO.setName("My Suggested Object");
		testObjectSuggestionRequestDTO.setDescription("My suggested object's description...");
		testObjectSuggestionRequestDTO.setLatitude(1.0);
		testObjectSuggestionRequestDTO.setLongitude(1.0);
		testObjectSuggestionRequestDTO.setType(ObjectType.LANDMARK);
		return testObjectSuggestionRequestDTO;
	}
	
	public EventSuggestionRequestDTO getTestEventSuggestionRequestDTO() {
		EventSuggestionRequestDTO testEventSuggestionRequestDTO = new EventSuggestionRequestDTO();
		testEventSuggestionRequestDTO.setSenderEmail("test@ipb.com");
		testEventSuggestionRequestDTO.setName("My Suggested Event");
		testEventSuggestionRequestDTO.setDescription("My suggested event's description...");
		testEventSuggestionRequestDTO.setCityId(1L);
		testEventSuggestionRequestDTO.setStartDate( new Date() );
		testEventSuggestionRequestDTO.setEndDate( new Date() );
		testEventSuggestionRequestDTO.setLatitude(1.0);
		testEventSuggestionRequestDTO.setLongitude(1.0);
		return testEventSuggestionRequestDTO;
	}
	
	public ObjectSuggestionEntity getTestObjectSuggestionEntity() {
		ObjectSuggestionEntity testObjectSuggestionEntity = new ObjectSuggestionEntity();
		testObjectSuggestionEntity.setSenderEmail("test@ipb.com");
		testObjectSuggestionEntity.setName("My Suggested Object");
		testObjectSuggestionEntity.setDescription("My suggested object's description...");
		testObjectSuggestionEntity.setLatitude(1.0);
		testObjectSuggestionEntity.setLongitude(1.0);
		testObjectSuggestionEntity.setType(ObjectType.LANDMARK);
		return testObjectSuggestionEntity;
	}
	
	public EventSuggestionEntity getTestEventSuggestionEntity() {
		EventSuggestionEntity testEventSuggestionEntity = new EventSuggestionEntity();
		testEventSuggestionEntity.setSenderEmail("test@ipb.com");
		testEventSuggestionEntity.setName("My Suggested Event");
		testEventSuggestionEntity.setDescription("My suggested event's description...");
		testEventSuggestionEntity.setCityId(1L);
		testEventSuggestionEntity.setStartDate( new Date() );
		testEventSuggestionEntity.setEndDate( new Date() );
		testEventSuggestionEntity.setLatitude(1.0);
		testEventSuggestionEntity.setLongitude(1.0);
		return testEventSuggestionEntity;
	}
	
	/**
	 * createNewSubmission() tests
	 */
	
	@Test(expected = UserNotFoundException.class)
	public void createNewSubmissionMethodShouldThrowUserNotFoundException()
		throws UserNotFoundException 
	{
		ContactFormSubmissionRequestDTO testSubmissionRequestDTO = getTestContactFormSubmissionRequestDTO();
		submissionService.createNewSubmission(testSubmissionRequestDTO);
	}
	
	@Test
	public void createNewSubmissionMethodShouldSuccessfullyAddSubmissionToDatabase()
		throws UserNotFoundException 
	{
		ContactFormSubmissionRequestDTO testSubmissionRequestDTO = getTestContactFormSubmissionRequestDTO();
		UserEntity testUserEntity = getTestUserEntity();
		userRepository.save(testUserEntity);
		
		Long createdSubmissionId = submissionService.createNewSubmission(testSubmissionRequestDTO);
		
		assertTrue( submissionRepository.count() == 1 );
		assertTrue( submissionRepository.existsById(createdSubmissionId) );
		assertTrue( 
			submissionRepository.findById(createdSubmissionId).get().getMessage().equals("I found an error...") 
		);
	}
	
	/**
	 * storeFileForSubmissionById() tests
	 */
	
	@Test(expected = ContactFormSubmissionException.class)
	public void storeFileForSubmissionByIdMethodShouldThrowContactFormSubmissionExceptionDueToMissingSubmission() 
		throws IOException, ContactFormSubmissionException 
	{
		submissionService.storeFileForSubmissionById(1L, null);
	}
	
	@Test(expected = ContactFormSubmissionException.class)
	public void storeFileForSubmissionByIdMethodShouldThrowContactFormSubmissionExceptionDueToFileAlreadyAttached() 
		throws IOException, ContactFormSubmissionException 
	{
		ContactFormSubmissionEntity testSubmission = getTestContactFormSubmissionEntity();
		testSubmission.setPathToAttachedFile("path-to-file");
		submissionRepository.save(testSubmission);
		Long testSubmissionId = testSubmission.getId();
		submissionService.storeFileForSubmissionById(testSubmissionId, null);
	}
	
	@Test(expected = NullPointerException.class)
	public void storeFileForSubmissionByIdMethodShouldThrowNullPointerExceptionDueToNullFile() 
		throws IOException, ContactFormSubmissionException 
	{
		ContactFormSubmissionEntity testSubmission = getTestContactFormSubmissionEntity();
		submissionRepository.save(testSubmission);
		Long testSubmissionId = testSubmission.getId();
		submissionService.storeFileForSubmissionById(testSubmissionId, null);
	}
	
	@Test
	public void storeFileForSubmissionByIdMethodShouldSuccessfullyAttachFile() 
		throws IOException, ContactFormSubmissionException 
	{
		ContactFormSubmissionEntity testSubmission = getTestContactFormSubmissionEntity();
		submissionRepository.save(testSubmission);
		Long testSubmissionId = testSubmission.getId();
		
		MockMultipartFile fileAttached = new MockMultipartFile(
    		"file",
    		"test-file.txt", 
    		"text/plain", 
    		"Text inside file".getBytes()
    	);
		
		submissionService.storeFileForSubmissionById(testSubmissionId, fileAttached);
		
		// Test if the test submission's file path has been updated
    	ContactFormSubmissionEntity retrievedTestSubmission = submissionRepository.findById(testSubmissionId).get();
    	assertTrue( retrievedTestSubmission.getPathToAttachedFile().contains("test-file.txt") );
    	
    	// Delete attached file so that it does not interfere with other tests
    	Files.deleteIfExists( Paths.get(retrievedTestSubmission.getPathToAttachedFile()) );
	}
	
	/**
	 * getFileAttachedById() tests
	 */
	
	@Test(expected = ContactFormSubmissionException.class)
	public void getFileAttachedByIdMethodShouldThrowContactFormSubmissionExceptionDueToMissingSubmission() 
		throws IOException, ContactFormSubmissionException 
	{
		submissionService.getFileAttachedById(1L);
	}
	
	@Test(expected = ContactFormSubmissionException.class)
	public void getFileAttachedByIdMethodShouldThrowContactFormSubmissionExceptionBecauseNoFileAttached() 
		throws IOException, ContactFormSubmissionException 
	{
		ContactFormSubmissionEntity testSubmission = getTestContactFormSubmissionEntity();
		submissionRepository.save(testSubmission);
		Long testSubmissionId = testSubmission.getId();
		submissionService.getFileAttachedById(testSubmissionId);
	}
	
	@Test(expected = IOException.class)
	public void getFileAttachedByIdMethodShouldThrowIOExceptionBecauseOfInvalidFilePath() 
		throws IOException, ContactFormSubmissionException 
	{
		ContactFormSubmissionEntity testSubmission = getTestContactFormSubmissionEntity();
		testSubmission.setPathToAttachedFile("path-to-file");
		submissionRepository.save(testSubmission);
		Long testSubmissionId = testSubmission.getId();
		submissionService.getFileAttachedById(testSubmissionId);
	}
	
	@Test
	public void getFileAttachedByIdMethodShouldSuccessfullyRetrieveAttachedFile() 
		throws IOException, ContactFormSubmissionException 
	{
		ContactFormSubmissionEntity testSubmission = getTestContactFormSubmissionEntity();
		submissionRepository.save(testSubmission);
		Long testSubmissionId = testSubmission.getId();
		String pathToAttachedFile = SUBMISSIONS_FILES_DIRECTORY + "/submission" + testSubmissionId + "_test-file.txt";
    	testSubmission.setPathToAttachedFile(pathToAttachedFile);
    	submissionRepository.save(testSubmission);
    	
    	// Create file
    	Files.write( Paths.get(pathToAttachedFile), "Text inside file".getBytes() );
    	
    	// Test if the retrieved file's data is the same as the created file's data
		byte[] fileBytes = submissionService.getFileAttachedById(testSubmissionId);
		assertTrue( Arrays.equals("Text inside file".getBytes(), fileBytes) );
		
		// Delete attached file so that it does not interfere with other tests
    	Files.deleteIfExists( Paths.get(pathToAttachedFile) );
	}
	
	/**
	 * getNameOfFileAttachedById() tests
	 */
	
	@Test(expected = ContactFormSubmissionException.class)
	public void getNameOfFileAttachedByIdMethodShouldThrowContactFormSubmissionExceptionDueToMissingSubmission() 
		throws ContactFormSubmissionException 
	{
		submissionService.getNameOfFileAttachedById(1L);
	}
	
	@Test(expected = ContactFormSubmissionException.class)
	public void getNameOfFileAttachedByIdMethodShouldThrowContactFormSubmissionExceptionBecauseNoFileAttached() 
		throws ContactFormSubmissionException 
	{
		ContactFormSubmissionEntity testSubmission = getTestContactFormSubmissionEntity();
		submissionRepository.save(testSubmission);
		Long testSubmissionId = testSubmission.getId();
		submissionService.getNameOfFileAttachedById(testSubmissionId);
	}
	
	@Test
	public void getNameOfFileAttachedByIdMethodShouldReturnFileName() 
		throws ContactFormSubmissionException 
	{
		ContactFormSubmissionEntity testSubmission = getTestContactFormSubmissionEntity();
		String pathToAttachedFile = SUBMISSIONS_FILES_DIRECTORY + "/test-file.txt";
		testSubmission.setPathToAttachedFile(pathToAttachedFile);
		submissionRepository.save(testSubmission);
		Long testSubmissionId = testSubmission.getId();
		String fileName = submissionService.getNameOfFileAttachedById(testSubmissionId);
		assertTrue( fileName.equals("test-file.txt") );
	}
	
	/**
	 * getAllSubmissions() tests
	 */
	
	@Test
	public void getAllSubmissionsMethodShouldReturnAllSubmissionsData() {
		ContactFormSubmissionEntity firstTestSubmission = getTestContactFormSubmissionEntity();
		ContactFormSubmissionEntity secondTestSubmission = getTestContactFormSubmissionEntity();
		secondTestSubmission.setMessage("I found another error...");
		submissionRepository.save(firstTestSubmission);
		submissionRepository.save(secondTestSubmission);
		
		List<ContactFormSubmissionResponseDTO> submissions = submissionService.getAllSubmissions();
		assertTrue( submissions.size() == 2 );
		String submissionsToString = submissions.toString();
		assertThat(submissionsToString).contains("I found an error...");
		assertThat(submissionsToString).contains("I found another error...");
	}
	
	@Test
	public void getAllSubmissionsMethodShouldReturnEmptyList() {
		List<ContactFormSubmissionResponseDTO> submissions = submissionService.getAllSubmissions();
		assertTrue( submissions.size() == 0 );
	}
	
	/**
	 * getSubmissionById() tests
	 */
	
	@Test(expected = ContactFormSubmissionException.class)
	public void getSubmissionByIdMethodShouldThrowContactFormSubmissionExceptionDueToMissingSubmission()
		throws ContactFormSubmissionException
	{
		submissionService.getSubmissionById(1L);
	}
	
	@Test
	public void getSubmissionByIdMethodShouldReturnExistingSubmissionData()
		throws ContactFormSubmissionException
	{
		ContactFormSubmissionEntity testSubmission = getTestContactFormSubmissionEntity();
		testSubmission = submissionRepository.save(testSubmission);
		Long testSubmissionId = testSubmission.getId();
		ContactFormSubmissionResponseDTO retrievedSubmission = submissionService.getSubmissionById(testSubmissionId);
		assertTrue( retrievedSubmission.getMessage().equals("I found an error...") );
	}
	
	/**
	 * deleteSubmissionById() tests
	 */
	
	@Test(expected = ContactFormSubmissionException.class)
	public void deleteSubmissionByIdMethodShouldThrowContactFormSubmissionExceptionDueToMissingSubmission()
		throws ContactFormSubmissionException
	{
		submissionService.deleteSubmissionById(1L);
	}
	
	@Test
	public void deleteSubmissionByIdMethodShouldDeleteExistingSubmission()
		throws ContactFormSubmissionException
	{
		ContactFormSubmissionEntity testSubmission = getTestContactFormSubmissionEntity();
		testSubmission = submissionRepository.save(testSubmission);
		Long testSubmissionId = testSubmission.getId();
		submissionService.deleteSubmissionById(testSubmissionId);
		assertTrue( !submissionRepository.existsById(testSubmissionId) );
	}
	
	/**
	 * createNewObjectSuggestion() tests
	 */
	
	@Test(expected = UserNotFoundException.class)
	public void createNewObjectSuggestionMethodShouldThrowUserNotFoundExceptionDueToMissingUser() 
		throws UserNotFoundException 
	{
		ObjectSuggestionRequestDTO testObjectSuggestion = getTestObjectSuggestionRequestDTO();
		submissionService.createNewObjectSuggestion(testObjectSuggestion);
	}
	
	@Test(expected = NullPointerException.class)
	public void createNewObjectSuggestionMethodShouldThrowNullPointerException() 
		throws UserNotFoundException 
	{
		submissionService.createNewObjectSuggestion(null);
	}
	
	@Test
	public void createNewObjectSuggestionMethodShouldSuccessfullyAddSuggestion() 
		throws UserNotFoundException 
	{
		UserEntity testUser = getTestUserEntity();
		userRepository.save(testUser);
		ObjectSuggestionRequestDTO testObjectSuggestion = getTestObjectSuggestionRequestDTO();
		Long createdObjectSuggestionId = submissionService.createNewObjectSuggestion(testObjectSuggestion);
		assertTrue( objectSuggestionRepository.existsById(createdObjectSuggestionId) );
	}
	
	/**
	 * createNewEventSuggestion() tests
	 */
	
	@Test(expected = UserNotFoundException.class)
	public void createNewEventSuggestionMethodShouldThrowUserNotFoundExceptionDueToMissingUser() 
		throws UserNotFoundException 
	{
		EventSuggestionRequestDTO testEventSuggestion = getTestEventSuggestionRequestDTO();
		submissionService.createNewEventSuggestion(testEventSuggestion);
	}
	
	@Test(expected = NullPointerException.class)
	public void createNewEventSuggestionMethodShouldThrowNullPointerException() 
		throws UserNotFoundException 
	{
		submissionService.createNewEventSuggestion(null);
	}
	
	@Test
	public void createNewEventSuggestionMethodShouldSuccessfullyAddSuggestion() 
		throws UserNotFoundException 
	{
		UserEntity testUser = getTestUserEntity();
		userRepository.save(testUser);
		EventSuggestionRequestDTO testEventSuggestion = getTestEventSuggestionRequestDTO();
		Long createdEventSuggestionId = submissionService.createNewEventSuggestion(testEventSuggestion);
		assertTrue( eventSuggestionRepository.existsById(createdEventSuggestionId) );
	}
	
	/**
	 * getObjectSuggestionById() tests
	 */
	
	@Test(expected = ContactFormSubmissionException.class)
	public void getObjectSuggestionByIdMethodShouldThrowContactFormSubmissionExceptionDueToMissingSuggestion()
		throws ContactFormSubmissionException
	{
		submissionService.getObjectSuggestionById(1L);
	}
	
	@Test
	public void getObjectSuggestionByIdMethodShouldReturnExistingSuggestionData()
		throws ContactFormSubmissionException
	{
		ObjectSuggestionEntity testObjectSuggestion = getTestObjectSuggestionEntity();
		testObjectSuggestion = objectSuggestionRepository.save(testObjectSuggestion);
		Long testObjectSuggestionId = testObjectSuggestion.getId();
		ObjectSuggestionResponseDTO retrievedObjectSuggestion = submissionService.getObjectSuggestionById(testObjectSuggestionId);
		assertTrue( retrievedObjectSuggestion.getName().equals("My Suggested Object") );
		assertTrue( retrievedObjectSuggestion.getDescription().equals("My suggested object's description...") );
	}
	
	/**
	 * getEventSuggestionById() tests
	 */
	
	@Test(expected = ContactFormSubmissionException.class)
	public void getEventSuggestionByIdMethodShouldThrowContactFormSubmissionExceptionDueToMissingSuggestion()
		throws ContactFormSubmissionException
	{
		submissionService.getEventSuggestionById(1L);
	}
	
	@Test
	public void getEventSuggestionByIdMethodShouldReturnExistingSuggestionData()
		throws ContactFormSubmissionException
	{
		EventSuggestionEntity testEventSuggestion = getTestEventSuggestionEntity();
		testEventSuggestion = eventSuggestionRepository.save(testEventSuggestion);
		Long testEventSuggestionId = testEventSuggestion.getId();
		EventSuggestionResponseDTO retrievedEventSuggestion = submissionService.getEventSuggestionById(testEventSuggestionId);
		assertTrue( retrievedEventSuggestion.getName().equals("My Suggested Event") );
		assertTrue( retrievedEventSuggestion.getDescription().equals("My suggested event's description...") );
	}
	
	/**
	 * getAllObjectSuggestions() tests
	 */
	
	@Test
	public void getAllObjectSuggestionsMethodShouldReturnAllSuggestionsData() {
		ObjectSuggestionEntity firstTestObjectSuggestion = getTestObjectSuggestionEntity();
		ObjectSuggestionEntity secondTestObjectSuggestion = getTestObjectSuggestionEntity();
		firstTestObjectSuggestion.setName("My First Object Suggestion");
		secondTestObjectSuggestion.setName("My Second Object Suggestion");
		firstTestObjectSuggestion.setDescription("My first suggested object's description...");
		secondTestObjectSuggestion.setDescription("My second suggested object's description...");
		
		objectSuggestionRepository.save(firstTestObjectSuggestion);
		objectSuggestionRepository.save(secondTestObjectSuggestion);
		
		List<ObjectSuggestionResponseDTO> objectSuggestions = submissionService.getAllObjectSuggestions();
		assertTrue( objectSuggestions.size() == 2 );
		String objectSuggestionsToString = objectSuggestions.toString();
		assertThat(objectSuggestionsToString).contains("My First Object Suggestion");
		assertThat(objectSuggestionsToString).contains("My Second Object Suggestion");
		assertThat(objectSuggestionsToString).contains("My first suggested object's description...");
		assertThat(objectSuggestionsToString).contains("My second suggested object's description...");
	}
	
	@Test
	public void getAllObjectSuggestionsMethodShouldReturnEmptyList() {
		List<ObjectSuggestionResponseDTO> objectSuggestions = submissionService.getAllObjectSuggestions();
		assertTrue( objectSuggestions.size() == 0 );
	}
	
	/**
	 * getAllEventSuggestions() tests
	 */
	
	@Test
	public void getAllEventSuggestionsMethodShouldReturnAllSuggestionsData() {
		EventSuggestionEntity firstTestEventSuggestion = getTestEventSuggestionEntity();
		EventSuggestionEntity secondTestEventSuggestion = getTestEventSuggestionEntity();
		firstTestEventSuggestion.setName("My First Event Suggestion");
		secondTestEventSuggestion.setName("My Second Event Suggestion");
		firstTestEventSuggestion.setDescription("My first suggested event's description...");
		secondTestEventSuggestion.setDescription("My second suggested event's description...");
		
		eventSuggestionRepository.save(firstTestEventSuggestion);
		eventSuggestionRepository.save(secondTestEventSuggestion);
		
		List<EventSuggestionResponseDTO> eventSuggestions = submissionService.getAllEventSuggestions();
		assertTrue( eventSuggestions.size() == 2 );
		String eventSuggestionsToString = eventSuggestions.toString();
		assertThat(eventSuggestionsToString).contains("My First Event Suggestion");
		assertThat(eventSuggestionsToString).contains("My Second Event Suggestion");
		assertThat(eventSuggestionsToString).contains("My first suggested event's description...");
		assertThat(eventSuggestionsToString).contains("My second suggested event's description...");
	}
	
	@Test
	public void getAllEventSuggestionsMethodShouldReturnEmptyList() {
		List<EventSuggestionResponseDTO> eventSuggestions = submissionService.getAllEventSuggestions();
		assertTrue( eventSuggestions.size() == 0 );
	}
	
	/**
	 * acceptObjectSuggestionById() tests
	 */
	
	@Test(expected = ContactFormSubmissionException.class)
	public void acceptObjectSuggestionByIdMethodShouldThrowContactFormSubmissionExceptionDueToMissingSuggestion()
		throws ContactFormSubmissionException, UserNotFoundException
	{
		submissionService.acceptObjectSuggestionById(1L);
	}
	
	@Test
	public void acceptObjectSuggestionByIdMethodShouldAcceptSuggestionWithNoEmailSentAndThrowUserNotFoundException()
		throws ContactFormSubmissionException
	{
		ObjectSuggestionEntity testObjectSuggestion = getTestObjectSuggestionEntity();
		testObjectSuggestion = objectSuggestionRepository.save(testObjectSuggestion);
		Long testObjectSuggestionId = testObjectSuggestion.getId();
		
		boolean userNotFoundExceptionThrown = false;
		try {
			submissionService.acceptObjectSuggestionById(testObjectSuggestionId);
		}
		catch (UserNotFoundException e) {
			userNotFoundExceptionThrown = true;
		}
		
		assertTrue( !objectSuggestionRepository.existsById(testObjectSuggestionId) );
		assertTrue(userNotFoundExceptionThrown);
    	assertTrue( objectRepository.count() == 1 );
    	ObjectEntity retrievedObjectEntity = objectRepository.findAll().get(0);
    	assertTrue( retrievedObjectEntity.getName().equals("My Suggested Object") );
    	assertTrue( retrievedObjectEntity.getDescription().equals("My suggested object's description...") );
    	assertTrue( retrievedObjectEntity.getType() == ObjectType.LANDMARK );
	}
	
	@Test
	public void acceptObjectSuggestionByIdMethodShouldAcceptSuggestionWithEmailSentAndNoExceptionThrown()
		throws ContactFormSubmissionException
	{
		ObjectSuggestionEntity testObjectSuggestion = getTestObjectSuggestionEntity();
		testObjectSuggestion = objectSuggestionRepository.save(testObjectSuggestion);
		Long testObjectSuggestionId = testObjectSuggestion.getId();
		
		// Insert test user
		UserEntity testUserEntity = getTestUserEntity();
		userRepository.save(testUserEntity);
		
		boolean userNotFoundExceptionThrown = false;
		try {
			submissionService.acceptObjectSuggestionById(testObjectSuggestionId);
		}
		catch (UserNotFoundException e) {
			userNotFoundExceptionThrown = true;
		}
		
		assertTrue( !objectSuggestionRepository.existsById(testObjectSuggestionId) );
		assertTrue( !userNotFoundExceptionThrown );
    	assertTrue( objectRepository.count() == 1 );
    	ObjectEntity retrievedObjectEntity = objectRepository.findAll().get(0);
    	assertTrue( retrievedObjectEntity.getName().equals("My Suggested Object") );
    	assertTrue( retrievedObjectEntity.getDescription().equals("My suggested object's description...") );
    	assertTrue( retrievedObjectEntity.getType() == ObjectType.LANDMARK );
	}
	
	/**
	 * getObjectSuggestionSenderEmailById() tests
	 */
	
	@Test(expected = ContactFormSubmissionException.class)
	public void getObjectSuggestionSenderEmailByIdMethodShouldThrowContactFormSubmissionExceptionDueToMissingSuggestion()
		throws ContactFormSubmissionException
	{
		submissionService.getObjectSuggestionSenderEmailById(1L);
	}
	
	@Test
	public void getObjectSuggestionSenderEmailByIdMethodShouldReturnExistingSuggestionSenderEmail()
		throws ContactFormSubmissionException
	{
		ObjectSuggestionEntity testObjectSuggestion = getTestObjectSuggestionEntity();
		testObjectSuggestion = objectSuggestionRepository.save(testObjectSuggestion);
		Long testObjectSuggestionId = testObjectSuggestion.getId();
		String senderEmail = submissionService.getObjectSuggestionSenderEmailById(testObjectSuggestionId);
		assertTrue( senderEmail.equals("test@ipb.com") );
	}
	
	/**
	 * rejectObjectSuggestionById() tests
	 */
	
	@Test(expected = ContactFormSubmissionException.class)
	public void rejectObjectSuggestionByIdMethodShouldThrowContactFormSubmissionExceptionDueToMissingSuggestion()
		throws ContactFormSubmissionException, UserNotFoundException
	{
		submissionService.rejectObjectSuggestionById(1L, "Your object suggestion was rejected because of reasons.");
	}
	
	@Test
	public void rejectObjectSuggestionByIdMethodShouldRejectSuggestionWithNoEmailSentAndThrowUserNotFoundException()
		throws ContactFormSubmissionException
	{
		ObjectSuggestionEntity testObjectSuggestion = getTestObjectSuggestionEntity();
		testObjectSuggestion = objectSuggestionRepository.save(testObjectSuggestion);
		Long testObjectSuggestionId = testObjectSuggestion.getId();
		
		boolean userNotFoundExceptionThrown = false;
		try {
			submissionService.rejectObjectSuggestionById(testObjectSuggestionId, "Your object suggestion was rejected because of reasons.");
		}
		catch (UserNotFoundException e) {
			userNotFoundExceptionThrown = true;
		}
		
		assertTrue( !objectSuggestionRepository.existsById(testObjectSuggestionId) );
		assertTrue(userNotFoundExceptionThrown);
    	assertTrue( objectRepository.count() == 0 );
	}
	
	@Test
	public void rejectObjectSuggestionByIdMethodShouldRejectSuggestionWithEmailSentAndNoExceptionThrown()
		throws ContactFormSubmissionException
	{
		ObjectSuggestionEntity testObjectSuggestion = getTestObjectSuggestionEntity();
		testObjectSuggestion = objectSuggestionRepository.save(testObjectSuggestion);
		Long testObjectSuggestionId = testObjectSuggestion.getId();
		
		// Insert test user
		UserEntity testUserEntity = getTestUserEntity();
		userRepository.save(testUserEntity);
		
		boolean userNotFoundExceptionThrown = false;
		try {
			submissionService.rejectObjectSuggestionById(testObjectSuggestionId, "Your object suggestion was rejected because of reasons.");
		}
		catch (UserNotFoundException e) {
			userNotFoundExceptionThrown = true;
		}
		
		assertTrue( !objectSuggestionRepository.existsById(testObjectSuggestionId) );
		assertTrue( !userNotFoundExceptionThrown );
    	assertTrue( objectRepository.count() == 0 );
	}
	
	/**
	 * acceptEventSuggestionById() tests
	 */
	
	@Test(expected = ContactFormSubmissionException.class)
	public void acceptEventSuggestionByIdMethodShouldThrowContactFormSubmissionExceptionDueToMissingSuggestion()
		throws ContactFormSubmissionException, UserNotFoundException, CityNotFoundException
	{
		submissionService.acceptEventSuggestionById(1L);
	}
	
	@Test(expected = CityNotFoundException.class)
	public void acceptEventSuggestionByIdMethodShouldThrowCityNotFoundExceptionDueToMissingCity()
		throws ContactFormSubmissionException, UserNotFoundException, CityNotFoundException
	{
		EventSuggestionEntity testEventSuggestion = getTestEventSuggestionEntity();
		testEventSuggestion = eventSuggestionRepository.save(testEventSuggestion);
		Long testEventSuggestionId = testEventSuggestion.getId();
		submissionService.acceptEventSuggestionById(testEventSuggestionId);
	}
	
	@Test
	public void acceptEventSuggestionByIdMethodShouldAcceptSuggestionWithNoEmailSentAndThrowUserNotFoundException()
		throws ContactFormSubmissionException, CityNotFoundException
	{
		// Insert test city
    	CityEntity testCity = new CityEntity();
    	testCity.setName("My City");
    	testCity.setDescription("My city's description");
    	testCity.setType(ObjectType.CITY);
    	testCity = cityRepository.save(testCity);
    	Long cityId = testCity.getId();
    	
		EventSuggestionEntity testEventSuggestion = getTestEventSuggestionEntity();
		testEventSuggestion.setCityId(cityId);
		testEventSuggestion = eventSuggestionRepository.save(testEventSuggestion);
		Long testEventSuggestionId = testEventSuggestion.getId();
		
		boolean userNotFoundExceptionThrown = false;
		try {
			submissionService.acceptEventSuggestionById(testEventSuggestionId);
		}
		catch (UserNotFoundException e) {
			userNotFoundExceptionThrown = true;
		}
		
		assertTrue( !eventSuggestionRepository.existsById(testEventSuggestionId) );
		assertTrue(userNotFoundExceptionThrown);
    	assertTrue( eventRepository.count() == 1 );
    	EventEntity retrievedEventEntity = eventRepository.findAll().get(0);
    	assertTrue( retrievedEventEntity.getName().equals("My Suggested Event") );
    	assertTrue( retrievedEventEntity.getDescription().equals("My suggested event's description...") );
	}
	
	@Test
	public void acceptEventSuggestionByIdMethodShouldAcceptSuggestionWithEmailSentAndNoExceptionThrown()
		throws ContactFormSubmissionException, CityNotFoundException
	{
		// Insert test city
    	CityEntity testCity = new CityEntity();
    	testCity.setName("My City");
    	testCity.setDescription("My city's description");
    	testCity.setType(ObjectType.CITY);
    	testCity = cityRepository.save(testCity);
    	Long cityId = testCity.getId();
    	
		EventSuggestionEntity testEventSuggestion = getTestEventSuggestionEntity();
		testEventSuggestion.setCityId(cityId);
		testEventSuggestion = eventSuggestionRepository.save(testEventSuggestion);
		Long testEventSuggestionId = testEventSuggestion.getId();
		
		// Insert test user
		UserEntity testUserEntity = getTestUserEntity();
		userRepository.save(testUserEntity);
		
		boolean userNotFoundExceptionThrown = false;
		try {
			submissionService.acceptEventSuggestionById(testEventSuggestionId);
		}
		catch (UserNotFoundException e) {
			userNotFoundExceptionThrown = true;
		}
		
		assertTrue( !eventSuggestionRepository.existsById(testEventSuggestionId) );
		assertTrue( !userNotFoundExceptionThrown );
    	assertTrue( eventRepository.count() == 1 );
    	EventEntity retrievedEventEntity = eventRepository.findAll().get(0);
    	assertTrue( retrievedEventEntity.getName().equals("My Suggested Event") );
    	assertTrue( retrievedEventEntity.getDescription().equals("My suggested event's description...") );
	}
	
	/**
	 * getEventSuggestionSenderEmailById() tests
	 */
	
	@Test(expected = ContactFormSubmissionException.class)
	public void getEventSuggestionSenderEmailByIdMethodShouldThrowContactFormSubmissionExceptionDueToMissingSuggestion()
		throws ContactFormSubmissionException
	{
		submissionService.getEventSuggestionSenderEmailById(1L);
	}
	
	@Test
	public void getEventSuggestionSenderEmailByIdMethodShouldReturnExistingSuggestionSenderEmail()
		throws ContactFormSubmissionException
	{
		EventSuggestionEntity testEventSuggestion = getTestEventSuggestionEntity();
		testEventSuggestion = eventSuggestionRepository.save(testEventSuggestion);
		Long testEventSuggestionId = testEventSuggestion.getId();
		String senderEmail = submissionService.getEventSuggestionSenderEmailById(testEventSuggestionId);
		assertTrue( senderEmail.equals("test@ipb.com") );
	}
	
	/**
	 * rejectEventSuggestionById() tests
	 */
	
	@Test(expected = ContactFormSubmissionException.class)
	public void rejectEventSuggestionByIdMethodShouldThrowContactFormSubmissionExceptionDueToMissingSuggestion()
		throws ContactFormSubmissionException, UserNotFoundException
	{
		submissionService.rejectEventSuggestionById(1L, "Your event suggestion was rejected because of reasons.");
	}
	
	@Test
	public void rejectEventSuggestionByIdMethodShouldRejectSuggestionWithNoEmailSentAndThrowUserNotFoundException()
		throws ContactFormSubmissionException
	{
		EventSuggestionEntity testEventSuggestion = getTestEventSuggestionEntity();
		testEventSuggestion = eventSuggestionRepository.save(testEventSuggestion);
		Long testEventSuggestionId = testEventSuggestion.getId();
		
		boolean userNotFoundExceptionThrown = false;
		try {
			submissionService.rejectEventSuggestionById(testEventSuggestionId, "Your event suggestion was rejected because of reasons.");
		}
		catch (UserNotFoundException e) {
			userNotFoundExceptionThrown = true;
		}
		
		assertTrue( !eventSuggestionRepository.existsById(testEventSuggestionId) );
		assertTrue(userNotFoundExceptionThrown);
    	assertTrue( eventRepository.count() == 0 );
	}
	
	@Test
	public void rejectEventSuggestionByIdMethodShouldRejectSuggestionWithEmailSentAndNoExceptionThrown()
		throws ContactFormSubmissionException
	{
		EventSuggestionEntity testEventSuggestion = getTestEventSuggestionEntity();
		testEventSuggestion = eventSuggestionRepository.save(testEventSuggestion);
		Long testEventSuggestionId = testEventSuggestion.getId();
		
		// Insert test user
		UserEntity testUserEntity = getTestUserEntity();
		userRepository.save(testUserEntity);
		
		boolean userNotFoundExceptionThrown = false;
		try {
			submissionService.rejectEventSuggestionById(testEventSuggestionId, "Your event suggestion was rejected because of reasons.");
		}
		catch (UserNotFoundException e) {
			userNotFoundExceptionThrown = true;
		}
		
		assertTrue( !eventSuggestionRepository.existsById(testEventSuggestionId) );
		assertTrue( !userNotFoundExceptionThrown );
    	assertTrue( eventRepository.count() == 0 );
	}
	
}