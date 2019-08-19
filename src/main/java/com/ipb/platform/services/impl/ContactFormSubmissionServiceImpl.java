package com.ipb.platform.services.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ipb.platform.dto.requests.ContactFormSubmissionRequestDTO;
import com.ipb.platform.dto.requests.EventSuggestionRequestDTO;
import com.ipb.platform.dto.requests.ObjectSuggestionRequestDTO;
import com.ipb.platform.dto.responses.ContactFormSubmissionResponseDTO;
import com.ipb.platform.dto.responses.EventSuggestionResponseDTO;
import com.ipb.platform.dto.responses.ObjectSuggestionResponseDTO;
import com.ipb.platform.mappers.ContactFormSubmissionMapper;
import com.ipb.platform.mappers.EventSuggestionMapper;
import com.ipb.platform.mappers.ObjectSuggestionMapper;
import com.ipb.platform.persistence.CityRepository;
import com.ipb.platform.persistence.ContactFormSubmissionRepository;
import com.ipb.platform.persistence.EventRepository;
import com.ipb.platform.persistence.EventSuggestionRepository;
import com.ipb.platform.persistence.ObjectRepository;
import com.ipb.platform.persistence.ObjectSuggestionRepository;
import com.ipb.platform.persistence.UserRepository;
import com.ipb.platform.persistence.entities.ContactFormSubmissionEntity;
import com.ipb.platform.persistence.entities.EventEntity;
import com.ipb.platform.persistence.entities.EventSuggestionEntity;
import com.ipb.platform.persistence.entities.ObjectEntity;
import com.ipb.platform.persistence.entities.ObjectSuggestionEntity;
import com.ipb.platform.persistence.entities.ObjectType;
import com.ipb.platform.services.ContactFormSubmissionService;
import com.ipb.platform.validation.CityNotFoundException;
import com.ipb.platform.validation.ContactFormSubmissionException;
import com.ipb.platform.validation.UserNotFoundException;

import lombok.AllArgsConstructor;

/**
 * This class is responsible for the business logic
 * when dealing with contact form submissions in the system.

 * @author dvt32
 */
@Service
@AllArgsConstructor
public class ContactFormSubmissionServiceImpl
	implements ContactFormSubmissionService
{
	
	@Autowired
	private ContactFormSubmissionRepository submissionRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ObjectSuggestionRepository objectSuggestionRepository;
	
	@Autowired
	private EventSuggestionRepository eventSuggestionRepository;
	
	@Autowired
	private ContactFormSubmissionMapper submissionMapper;
	
	@Autowired
	private ObjectSuggestionMapper objectSuggestionMapper;
	
	@Autowired
	private EventSuggestionMapper eventSuggestionMapper;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private ObjectRepository objectRepository;
	
	@Autowired
	private EventRepository eventRepository;
	
	@Autowired
	private CityRepository cityRepository;
	
	private static final String SUBMISSIONS_FILES_DIRECTORY = System.getProperty("user.dir") + "/files/contact-form";
	
	/**
	 * Adds a new contact form submission to the database (if it was submitted by a valid user).
	 */
	@Override
	public Long createNewSubmission(ContactFormSubmissionRequestDTO submissionRequestDTO) 
		throws UserNotFoundException
	{
		String userEmail = submissionRequestDTO.getSenderEmail();
		if ( !userRepository.existsByEmail(userEmail) ) {
			throw new UserNotFoundException("User with this e-mail does not exist!");
		}
		
		ContactFormSubmissionEntity submissionEntity = submissionMapper.toEntity(submissionRequestDTO);
		submissionEntity = submissionRepository.save(submissionEntity);
		return submissionEntity.getId();
	}
	
	/**
	 * Adds a file, which will be associated with an existing contact form submission
	 */
	@Override
	public void storeFileForSubmissionById(Long id, MultipartFile file) 
		throws IOException, ContactFormSubmissionException
	{
		if ( !submissionRepository.existsById(id) ) {
			throw new ContactFormSubmissionException("Contact form submission with this ID does not exist!");
		}
		
		// If there is already a file attached for this submission, abort operation
		if ( hasFileAttachedById(id) ) {
			throw new ContactFormSubmissionException("Contact form submission already has a file attached!");		
		}
		
		// Create output folder if it doesn't exist
		Path pathToOutputFolder = Paths.get(SUBMISSIONS_FILES_DIRECTORY);
		if ( !Files.exists(pathToOutputFolder) ) {
			Files.createDirectories(pathToOutputFolder);
		}
		
		// Store file
		String fileName = "submission" + id + "_" + file.getOriginalFilename();
		Path filePath = Paths.get(SUBMISSIONS_FILES_DIRECTORY + "/" + fileName);
		Files.copy(
			file.getInputStream(), 
			filePath, 
			StandardCopyOption.REPLACE_EXISTING
		);
		
		// Update submission's path to attached file
		String pathToAttachedFile = filePath.toString();
		ContactFormSubmissionEntity submissionEntity = submissionRepository.findById(id).get();
		submissionEntity.setPathToAttachedFile(pathToAttachedFile);
		submissionRepository.save(submissionEntity);
	}

	/**
	 * Checks if an existing submission has a file attached to it.
	 */
	@Override
	public boolean hasFileAttachedById(Long id) {
		return ( submissionRepository.findById(id).get().getPathToAttachedFile() != null );
	}
	
	/**
	 * Gets the file attached to a submission (if the submission and the file exist) as a byte array.
	 */
	@Override
	public byte[] getFileAttachedById(Long id) 
		throws IOException, ContactFormSubmissionException
	{
		if ( !submissionRepository.existsById(id) ) {
			throw new ContactFormSubmissionException("Contact form submission with this ID does not exist!");
		}
		
		if ( !hasFileAttachedById(id) ) {
			throw new ContactFormSubmissionException("Contact form submission does not have a file attached!");		
		}
		
		String pathToAttachedFile = submissionRepository.findById(id).get().getPathToAttachedFile();
		Path filePath = Paths.get(pathToAttachedFile);
		byte[] fileBytes = Files.readAllBytes(filePath);
		
		return fileBytes;
	}
	
	/**
	 * Gets the name of the attached file of an existing submission
	 */
	@Override
	public String getNameOfFileAttachedById(Long id) 
		throws ContactFormSubmissionException 
	{
		if ( !submissionRepository.existsById(id) ) {
			throw new ContactFormSubmissionException("Contact form submission with this ID does not exist!");
		}
		
		if ( !hasFileAttachedById(id) ) {
			throw new ContactFormSubmissionException("Contact form submission does not have a file attached!");		
		}
		
		String pathToAttachedFile = submissionRepository.findById(id).get().getPathToAttachedFile();
		String fileName = new File(pathToAttachedFile).getName();
		
		return fileName;
	}

	/**
	 * Returns all existing contact form submissions' data as a list of submissions.
	 */
	@Override
	public List<ContactFormSubmissionResponseDTO> getAllSubmissions() {
		return (
			submissionRepository.findAll().stream()
				.map( entity -> submissionMapper.toResponseDTO(entity) )
				.collect( Collectors.toList() )
		);
	}
	
	/**
	 * Finds an existing submission by a specified ID.
	 */
	@Override
	public ContactFormSubmissionResponseDTO getSubmissionById(Long id) 
		throws ContactFormSubmissionException 
	{
		if ( !submissionRepository.existsById(id) ) {
			throw new ContactFormSubmissionException("Contact form submission with this ID does not exist!");
		}
		return submissionMapper.toResponseDTO( submissionRepository.findById(id).get() );
	}
	
	/**
	 * Deletes an existing submission with a specified ID.
	 */
	@Override
	public void deleteSubmissionById(Long id) 
		throws ContactFormSubmissionException 
	{
		if ( !submissionRepository.existsById(id) ) {
			throw new ContactFormSubmissionException("Contact form submission with this ID does not exist!");
		}
		
		submissionRepository.deleteById(id);
	}

	/**
	 * Adds a new object suggestion to the database (if it was submitted by a valid user).
	 */
	@Override
	public Long createNewObjectSuggestion(ObjectSuggestionRequestDTO objectSuggestionRequestDTO)
		throws UserNotFoundException 
	{
		String userEmail = objectSuggestionRequestDTO.getSenderEmail();
		if ( !userRepository.existsByEmail(userEmail) ) {
			throw new UserNotFoundException("User with this e-mail does not exist!");
		}
		
		ObjectSuggestionEntity objectSuggestionEntity = objectSuggestionMapper.toEntity(objectSuggestionRequestDTO);
		objectSuggestionEntity = objectSuggestionRepository.save(objectSuggestionEntity);
		return objectSuggestionEntity.getId();
	}
	
	/**
	 * Adds a new event suggestion to the database (if it was submitted by a valid user).
	 */
	@Override
	public Long createNewEventSuggestion(EventSuggestionRequestDTO eventSuggestionRequestDTO)
		throws UserNotFoundException 
	{
		String userEmail = eventSuggestionRequestDTO.getSenderEmail();
		if ( !userRepository.existsByEmail(userEmail) ) {
			throw new UserNotFoundException("User with this e-mail does not exist!");
		}
		
		EventSuggestionEntity eventSuggestionEntity = eventSuggestionMapper.toEntity(eventSuggestionRequestDTO);
		eventSuggestionEntity = eventSuggestionRepository.save(eventSuggestionEntity);
		return eventSuggestionEntity.getId();
	}

	/**
	 * Finds an existing object suggestion by a specified ID.
	 */
	@Override
	public ObjectSuggestionResponseDTO getObjectSuggestionById(Long id) 
		throws ContactFormSubmissionException 
	{
		if ( !objectSuggestionRepository.existsById(id) ) {
			throw new ContactFormSubmissionException("Object suggestion with this ID does not exist!");
		}
		return objectSuggestionMapper.toResponseDTO( objectSuggestionRepository.findById(id).get() );
	}
	
	/**
	 * Finds an existing event suggestion by a specified ID.
	 */
	@Override
	public EventSuggestionResponseDTO getEventSuggestionById(Long id) 
		throws ContactFormSubmissionException 
	{
		if ( !eventSuggestionRepository.existsById(id) ) {
			throw new ContactFormSubmissionException("Event suggestion with this ID does not exist!");
		}
		return eventSuggestionMapper.toResponseDTO( eventSuggestionRepository.findById(id).get() );
	}

	/**
	 * Returns all existing object suggestions' data as a list.
	 */
	@Override
	public List<ObjectSuggestionResponseDTO> getAllObjectSuggestions() {
		return (
			objectSuggestionRepository.findAll().stream()
				.map( entity -> objectSuggestionMapper.toResponseDTO(entity) )
				.collect( Collectors.toList() )
		);
	}
	
	/**
	 * Returns all existing event suggestions' data as a list.
	 */
	@Override
	public List<EventSuggestionResponseDTO> getAllEventSuggestions() {
		return (
			eventSuggestionRepository.findAll().stream()
				.map( entity -> eventSuggestionMapper.toResponseDTO(entity) )
				.collect( Collectors.toList() )
		);
	}
	
	/**
	 * Adds an object to the database from an existing object suggestion's data.
	 * 
	 * The object suggestion is then deleted from the database 
	 * and a notification is sent to the user who submitted the object suggestion. 
	 * 
	 * If this user no longer exists, no e-mail is sent.
	 */
	@Override
	public void acceptObjectSuggestionById(Long id) 
		throws ContactFormSubmissionException, UserNotFoundException 
	{
		if ( !objectSuggestionRepository.existsById(id) ) {
			throw new ContactFormSubmissionException("Object suggestion with this ID does not exist!");
		}
		
		ObjectSuggestionEntity objectSuggestionEntity = objectSuggestionRepository.findById(id).get();
		String objectSuggestionName = objectSuggestionEntity.getName();
		String objectSuggestionSenderEmail = objectSuggestionEntity.getSenderEmail();
		
		// Add object to database from suggestion data
		ObjectEntity objectEntity = new ObjectEntity();
		objectEntity.setName( objectSuggestionEntity.getName() );
		objectEntity.setDescription( objectSuggestionEntity.getDescription() );
		objectEntity.setLatitude( objectSuggestionEntity.getLatitude() );
		objectEntity.setLongitude( objectSuggestionEntity.getLongitude() );
		objectEntity.setType( objectSuggestionEntity.getType() );
		objectRepository.save(objectEntity);
		
		// Delete suggestion from database
		objectSuggestionRepository.deleteById(id);
		
		// Send e-mail only if a user with this e-mail address exists
		if ( !userRepository.existsByEmail(objectSuggestionSenderEmail) ) {
			throw new UserNotFoundException("No e-mail sent (user with this e-mail address does not exist)!");
		}
		else {
			SimpleMailMessage objectAcceptedEmail = new SimpleMailMessage();
			objectAcceptedEmail.setTo(objectSuggestionSenderEmail);
			objectAcceptedEmail.setSubject("IPB Object Suggestion - Accepted");
			objectAcceptedEmail.setText(
				"Your object suggestion with the name \"" 
				+ objectSuggestionName 
				+ "\" was approved and has been added to the database!"
			);
			mailSender.send(objectAcceptedEmail);
		}
	}

	/**
	 * Returns an existing object suggestion's sender e-mail.
	 */
	@Override
	public String getObjectSuggestionSenderEmailById(Long id) 
		throws ContactFormSubmissionException
	{
		if ( !objectSuggestionRepository.existsById(id) ) {
			throw new ContactFormSubmissionException("Object suggestion with this ID does not exist!");
		}
		
		return objectSuggestionRepository.findById(id).get().getSenderEmail();
	}
	
	/**
	 * Deletes an object suggestion from the database 
	 * and sends a notification e-mail to the user 
	 * who suggested the object (with an optional rejection message)
	 * 
	 * If this user no longer exists, no e-mail is sent.
	 */
	@Override
	public void rejectObjectSuggestionById(Long id, String rejectionMessage)
		throws ContactFormSubmissionException, UserNotFoundException 
	{
		if ( !objectSuggestionRepository.existsById(id) ) {
			throw new ContactFormSubmissionException("Object suggestion with this ID does not exist!");
		}
		
		ObjectSuggestionEntity objectSuggestionEntity = objectSuggestionRepository.findById(id).get();
		String objectSuggestionName = objectSuggestionEntity.getName();
		String senderEmail = objectSuggestionEntity.getSenderEmail();
		
		// Delete suggestion from database
		objectSuggestionRepository.deleteById(id);
		
		// Send e-mail only if a user with this e-mail address exists
		if ( !userRepository.existsByEmail(senderEmail) ) {
			throw new UserNotFoundException("No e-mail sent (user with this e-mail address does not exist)!");
		}
		else {
			SimpleMailMessage objectRejectedEmail = new SimpleMailMessage();
			objectRejectedEmail.setTo(senderEmail);
			objectRejectedEmail.setSubject("IPB Object Suggestion - Rejected");
			objectRejectedEmail.setText(
				"Your object suggestion with the name \"" 
				+ objectSuggestionName 
				+ "\" was rejected! Reason for rejection: "
				+ ( (rejectionMessage != null) ? rejectionMessage : "no reason specified" )
			);
			mailSender.send(objectRejectedEmail);
		}
	}
	
	/**
	 * Adds an event to the database from an existing event suggestion's data.
	 * 
	 * The event suggestion is then deleted from the database 
	 * and a notification is sent to the user who submitted the event suggestion. 
	 * 
	 * If this user no longer exists, no e-mail is sent.
	 */
	@Override
	public void acceptEventSuggestionById(Long id) 
		throws ContactFormSubmissionException, UserNotFoundException, CityNotFoundException
	{
		if ( !eventSuggestionRepository.existsById(id) ) {
			throw new ContactFormSubmissionException("Event suggestion with this ID does not exist!");
		}
		
		EventSuggestionEntity eventSuggestionEntity = eventSuggestionRepository.findById(id).get();
		String eventSuggestionName = eventSuggestionEntity.getName();
		String eventSuggestionSenderEmail = eventSuggestionEntity.getSenderEmail();
		Long eventSuggestionCityId = eventSuggestionEntity.getCityId();
		
		// Add event to database from suggestion data
		EventEntity eventEntity = new EventEntity();
		eventEntity.setName( eventSuggestionEntity.getName() );
		eventEntity.setDescription( eventSuggestionEntity.getDescription() );
		if ( !cityRepository.existsById(eventSuggestionCityId) ) {
			throw new CityNotFoundException(
				"The suggested city does not exist (no city with ID " + eventSuggestionCityId + ")!"
			);
		}
		else {
			eventEntity.setCity( cityRepository.findById(eventSuggestionCityId).get() );
		}
		eventEntity.setStartDate( eventSuggestionEntity.getStartDate().getTime() );
		eventEntity.setEndDate( eventSuggestionEntity.getEndDate().getTime() );
		eventEntity.setLatitude( eventSuggestionEntity.getLatitude() );
		eventEntity.setLongitude( eventSuggestionEntity.getLongitude() );
		eventEntity.setType(ObjectType.EVENT);
		eventRepository.save(eventEntity);
		
		// Delete suggestion from database
		eventSuggestionRepository.deleteById(id);
		
		// Send e-mail only if a user with this e-mail address exists
		if ( !userRepository.existsByEmail(eventSuggestionSenderEmail) ) {
			throw new UserNotFoundException("No e-mail sent (user with this e-mail address does not exist)!");
		}
		else {
			SimpleMailMessage eventAcceptedEmail = new SimpleMailMessage();
			eventAcceptedEmail.setTo(eventSuggestionSenderEmail);
			eventAcceptedEmail.setSubject("IPB Event Suggestion - Accepted");
			eventAcceptedEmail.setText(
				"Your event suggestion with the name \"" 
				+ eventSuggestionName 
				+ "\" was approved and has been added to the database!"
			);
			mailSender.send(eventAcceptedEmail);
		}
	}
	
	/**
	 * Returns an existing event suggestion's sender e-mail.
	 */
	@Override
	public String getEventSuggestionSenderEmailById(Long id) 
		throws ContactFormSubmissionException
	{
		if ( !eventSuggestionRepository.existsById(id) ) {
			throw new ContactFormSubmissionException("Event suggestion with this ID does not exist!");
		}
		
		return eventSuggestionRepository.findById(id).get().getSenderEmail();
	}
	
	/**
	 * Deletes an event suggestion from the database 
	 * and sends a notification e-mail to the user 
	 * who suggested the event (with an optional rejection message)
	 * 
	 * If this user no longer exists, no e-mail is sent.
	 */
	@Override
	public void rejectEventSuggestionById(Long id, String rejectionMessage)
		throws ContactFormSubmissionException, UserNotFoundException 
	{
		if ( !eventSuggestionRepository.existsById(id) ) {
			throw new ContactFormSubmissionException("Event suggestion with this ID does not exist!");
		}
		
		EventSuggestionEntity eventSuggestionEntity = eventSuggestionRepository.findById(id).get();
		String eventSuggestionName = eventSuggestionEntity.getName();
		String senderEmail = eventSuggestionEntity.getSenderEmail();
		
		// Delete suggestion from database
		eventSuggestionRepository.deleteById(id);
		
		// Send e-mail only if a user with this e-mail address exists
		if ( !userRepository.existsByEmail(senderEmail) ) {
			throw new UserNotFoundException("No e-mail sent (user with this e-mail address does not exist)!");
		}
		else {
			SimpleMailMessage eventRejectedEmail = new SimpleMailMessage();
			eventRejectedEmail.setTo(senderEmail);
			eventRejectedEmail.setSubject("IPB Event Suggestion - Rejected");
			eventRejectedEmail.setText(
				"Your event suggestion with the name \"" 
				+ eventSuggestionName 
				+ "\" was rejected! Reason for rejection: "
				+ ( (rejectionMessage != null) ? rejectionMessage : "no reason specified" )
			);
			mailSender.send(eventRejectedEmail);
		}
	}
	
}