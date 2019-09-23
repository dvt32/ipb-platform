package com.ipb.platform.controllers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.StringJoiner;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ipb.platform.dto.requests.ContactFormSubmissionRequestDTO;
import com.ipb.platform.dto.requests.EventSuggestionRequestDTO;
import com.ipb.platform.dto.requests.ObjectSuggestionRequestDTO;
import com.ipb.platform.dto.responses.ContactFormSubmissionResponseDTO;
import com.ipb.platform.dto.responses.EventSuggestionResponseDTO;
import com.ipb.platform.dto.responses.ObjectSuggestionResponseDTO;
import com.ipb.platform.services.ContactFormSubmissionService;
import com.ipb.platform.validation.CityNotFoundException;
import com.ipb.platform.validation.ContactFormSubmissionException;
import com.ipb.platform.validation.UserNotFoundException;

import lombok.AllArgsConstructor;

/**
 * This controller allows CRUD operations 
 * for contact form submissions in the database (via HTTP requests).
 * 
 * @author dvt32
 */
@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/contact")
public class ContactFormSubmissionController {
	
	@Autowired
	private ContactFormSubmissionService submissionService;
	
	/**
	 * This method creates a contact form submission 
	 * by passing the submission's data in a POST request's body.
	 * The data is validated before the submission is stored in the database.
	 * 
	 * @param submissionRequestDTO An object containing the submission's data
	 * @param bindingResult The validator of the passed data
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@ResponseBody
	@PostMapping
	public ResponseEntity<String> addContactFormSubmission(
		@RequestBody @Valid ContactFormSubmissionRequestDTO submissionRequestDTO, 
		BindingResult bindingResult) 
	{
		if (bindingResult.hasErrors()) {
			String responseErrorMessage = getResponseErrorMessageFromBindingResult(bindingResult);
			return new ResponseEntity<>(responseErrorMessage, HttpStatus.BAD_REQUEST);
		}
		
		String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		submissionRequestDTO.setSenderEmail(loggedInUserEmail);
		
		Long createdContactFormSubmissionId;
		try {
			createdContactFormSubmissionId = submissionService.createNewSubmission(submissionRequestDTO);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		// Add created resource's location to headers
		HttpHeaders responseHeaders = new HttpHeaders();
		try {
			responseHeaders.setLocation( new URI("/" + createdContactFormSubmissionId) );
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		   
		return new ResponseEntity<>(
			"Successfully added contact form submission with ID " + createdContactFormSubmissionId, 
			responseHeaders,
			HttpStatus.CREATED
		);
	}
	
	/**
	 * This method attaches a file to a certain existing submission 
	 * (if it doesn't have a file attached to it already).
	 * 
	 * @param id The ID of the submission
	 * @param file The file, which will be associated with this submission
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@PostMapping("/{id}/file")
	public ResponseEntity<String> storeFileForSubmission(
		@PathVariable Long id, 
		@RequestParam MultipartFile file
	) {
		try {
			submissionService.storeFileForSubmissionById(id, file);
		} catch (ContactFormSubmissionException | IOException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>("Successfully attached file to contact form submission with ID " + id, HttpStatus.OK);
	}
	
	/**
	 * This method retrieves the stored file attachment for an existing submission.
	 * 
	 * @param id The id of the submission
	 * @return A ResponseEntity object containing the file or an error message
	 */
	@GetMapping("/{id}/file")
    @ResponseBody
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> getFileAttachedForSubmission(@PathVariable Long id) {
		byte[] fileBytes = null;
		String fileName = null;
		
		try {
			fileBytes = submissionService.getFileAttachedById(id);
			fileName = submissionService.getNameOfFileAttachedById(id);
		} catch (ContactFormSubmissionException | IOException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
	    HttpHeaders responseHeaders = new HttpHeaders();
	    responseHeaders.set("charset", "utf-8");
	    responseHeaders.setContentLength(fileBytes.length);
	    responseHeaders.set("Content-disposition", "attachment; filename=\"" + fileName +"\"");

	    return new ResponseEntity<byte[]>(fileBytes, responseHeaders, HttpStatus.OK);
    }
	
	/**
	 * This method retrieves all contact form submissions (and their associated data) in JSON format.
	 * Only an administrator has access to this list.
	 * 
	 * @return the list of contact form submissions (empty if no submissions available)
	 */
	@GetMapping("/all")
	@Secured("ROLE_ADMIN")
	public List<ContactFormSubmissionResponseDTO> getAllSubmissions() {
		return submissionService.getAllSubmissions();
	}
	
	/**
	 * This method retrieves a submission's data in JSON format.
	 * 
	 * @param id The id of the submission to be retrieved
	 * @return a ResponseEntity containing the submission's data and an appropriate response code
	 */
	@GetMapping("/{id}")
	@Secured({"ROLE_ADMIN"})
	public ResponseEntity<?> getSubmissionById(@PathVariable Long id) {
		ContactFormSubmissionResponseDTO submission = null;
		
		try {
			submission = submissionService.getSubmissionById(id);
		} catch (ContactFormSubmissionException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(submission, HttpStatus.OK);
	}
	
	/**
	 * This method deletes an existing submission in the database by passing an ID.
	 * If the submission does not exist, an exception is thrown.
	 * 
	 * @param id The ID of the contact form submission
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@DeleteMapping("/{id}")
	@Secured("ROLE_ADMIN")
	public ResponseEntity<String> deleteSubmissionById(@PathVariable Long id) 
	{
		try {
			submissionService.deleteSubmissionById(id);
		} catch (ContactFormSubmissionException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>("Successfully deleted contact form submission with ID " + id, HttpStatus.OK);
	}
	
	/**
	 * This method extracts a list of error messages from a BindingResult object (in the form of a string)
	 * 
	 * @param bindingResult The source BindingRresult object
	 * @return A string containing all the error messages (separated by a space)
	 */
	public static String getResponseErrorMessageFromBindingResult(BindingResult bindingResult) {
		List<ObjectError> errors = bindingResult.getAllErrors();
		StringJoiner responseErrorMessageJoiner = new StringJoiner(" ");
		for (ObjectError error : errors) {
			String errorMessage = error.getDefaultMessage();
			responseErrorMessageJoiner.add(errorMessage);
		}
		String responseErrorMessage = responseErrorMessageJoiner.toString();
		
		return responseErrorMessage;
	}
	
	/*
	 * ******************************************************************
	 * Extended contact form features below (object & event suggestions)
	 * ******************************************************************
	 */
	
	/**
	 * This method submits an object suggestion
	 * by passing the suggested object's data in a POST request's body.
	 * The data is validated before the suggestion is sent for review by an admin.
	 * 
	 * @param objectSuggestionRequestDTO An object containing the suggestion's data
	 * @param bindingResult The validator of the passed data
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@PostMapping("/object-suggestions")
	public ResponseEntity<String> addObjectSuggestion(
		@RequestBody @Valid ObjectSuggestionRequestDTO objectSuggestionRequestDTO, 
		BindingResult bindingResult) 
	{
		if (bindingResult.hasErrors()) {
			String responseErrorMessage = getResponseErrorMessageFromBindingResult(bindingResult);
			return new ResponseEntity<>(responseErrorMessage, HttpStatus.BAD_REQUEST);
		}
		
		String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		objectSuggestionRequestDTO.setSenderEmail(loggedInUserEmail);
		
		Long createdObjectSuggestionId;
		try {
			createdObjectSuggestionId = submissionService.createNewObjectSuggestion(objectSuggestionRequestDTO);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>("Successfully submitted object suggestion with ID " + createdObjectSuggestionId, HttpStatus.CREATED);
	}
	
	/**
	 * This method retrieves an object suggestion's data in JSON format.
	 * 
	 * @param id The id of the object suggestion to be retrieved
	 * @return a ResponseEntity containing the object suggestion's data and an appropriate response code
	 */
	@GetMapping("/object-suggestions/{id}")
	@Secured({"ROLE_ADMIN"})
	public ResponseEntity<?> getObjectSuggestionById(@PathVariable Long id) {
		ObjectSuggestionResponseDTO objectSuggestion = null;
		
		try {
			objectSuggestion = submissionService.getObjectSuggestionById(id);
		} catch (ContactFormSubmissionException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(objectSuggestion, HttpStatus.OK);
	}
	
	/**
	 * This method submits an event suggestion
	 * by passing the suggested event's data in a POST request's body.
	 * The data is validated before the suggestion is sent for review by an admin.
	 * 
	 * @param eventSuggestionRequestDTO An object containing the suggestion's data
	 * @param bindingResult The validator of the passed data
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@PostMapping("/event-suggestions")
	public ResponseEntity<String> addEventSuggestion(
		@RequestBody @Valid EventSuggestionRequestDTO eventSuggestionRequestDTO, 
		BindingResult bindingResult) 
	{
		if (bindingResult.hasErrors()) {
			String responseErrorMessage = getResponseErrorMessageFromBindingResult(bindingResult);
			return new ResponseEntity<>(responseErrorMessage, HttpStatus.BAD_REQUEST);
		}
		
		String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		eventSuggestionRequestDTO.setSenderEmail(loggedInUserEmail);
		
		Long createdEventSuggestionId;
		try {
			createdEventSuggestionId = submissionService.createNewEventSuggestion(eventSuggestionRequestDTO);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>("Successfully submitted event suggestion with ID " + createdEventSuggestionId, HttpStatus.CREATED);
	}
	
	/**
	 * This method retrieves an event suggestion's data in JSON format.
	 * 
	 * @param id The id of the event suggestion to be retrieved
	 * @return a ResponseEntity containing the event suggestion's data and an appropriate response code
	 */
	@GetMapping("/event-suggestions/{id}")
	@Secured({"ROLE_ADMIN"})
	public ResponseEntity<?> getEventSuggestionById(@PathVariable Long id) {
		EventSuggestionResponseDTO eventSuggestion = null;
		
		try {
			eventSuggestion = submissionService.getEventSuggestionById(id);
		} catch (ContactFormSubmissionException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(eventSuggestion, HttpStatus.OK);
	}
	
	/**
	 * This method retrieves all object suggestions (and their associated data) in JSON format.
	 * Only an administrator has access to this list.
	 * 
	 * @return the list of object suggestions (empty if no suggestions available)
	 */
	@GetMapping("/object-suggestions/all")
	@Secured("ROLE_ADMIN")
	public List<ObjectSuggestionResponseDTO> getAllObjectSuggestions() {
		return submissionService.getAllObjectSuggestions();
	}
	
	/**
	 * This method retrieves all event suggestions (and their associated data) in JSON format.
	 * Only an administrator has access to this list.
	 * 
	 * @return the list of event suggestions (empty if no suggestions available)
	 */
	@GetMapping("/event-suggestions/all")
	@Secured("ROLE_ADMIN")
	public List<EventSuggestionResponseDTO> getAllEventSuggestions() {
		return submissionService.getAllEventSuggestions();
	}
	
	/**
	 * This method accepts an object suggestion and adds that object to the database.
	 * The object suggestion is then deleted from the database 
	 * and a notification e-mail is sent to the user who suggested the object.
	 * 
	 * @param id The id of the object suggestion to be accepted
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@PostMapping("/object-suggestions/{id}/accept")
	@Secured("ROLE_ADMIN")
	public ResponseEntity<String> acceptObjectSuggestionById(@PathVariable Long id) {
		String objectSuggestionSenderEmail = null; 
		
		try {
			objectSuggestionSenderEmail = submissionService.getObjectSuggestionSenderEmailById(id);
			submissionService.acceptObjectSuggestionById(id);
		} catch (ContactFormSubmissionException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<>(
				"Successfully accepted & added object to database from suggestion with ID " + id + "! "
				+ e.getMessage(), 
				HttpStatus.NOT_FOUND
			);
		}
		
		return new ResponseEntity<>(
			"Successfully accepted & added object to database from suggestion with ID " + id + "! "
			+ "A notification e-mail was sent to " + objectSuggestionSenderEmail, 
			HttpStatus.OK
		);
	}
	
	/**
	 * This method rejects an object suggestion 
	 * and deletes that suggestion from the database.
	 * 
	 * A notification e-mail (with an optional rejection message) 
	 * is sent to the user who suggested the object.
	 * 
	 * @param id The id of the object suggestion to be rejected
	 * @param rejectionMessage an optional rejection message, which will be part of the notification email
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@PostMapping("/object-suggestions/{id}/reject")
	@Secured("ROLE_ADMIN")
	public ResponseEntity<String> rejectObjectSuggestionById(
		@PathVariable Long id, 
		@RequestParam(name="message", required=false) String rejectionMessage) 
	{
		String objectSuggestionSenderEmail = null;
		
		try {
			objectSuggestionSenderEmail = submissionService.getObjectSuggestionSenderEmailById(id);
			submissionService.rejectObjectSuggestionById(id, rejectionMessage);
		} catch (ContactFormSubmissionException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<>(
				"Successfully rejected object suggestion with ID " + id + "! "
				+ e.getMessage(), 
				HttpStatus.NOT_FOUND
			);
		}
		
		return new ResponseEntity<>(
			"Successfully rejected object suggestion with ID " + id + "! "
			+ "A notification e-mail was sent to " + objectSuggestionSenderEmail, 
			HttpStatus.OK
		);
	}
	
	/**
	 * This method accepts an event suggestion and adds that event to the database.
	 * The event suggestion is then deleted from the database 
	 * and a notification e-mail is sent to the user who suggested the event.
	 * 
	 * @param id The id of the event suggestion to be accepted
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@PostMapping("/event-suggestions/{id}/accept")
	@Secured("ROLE_ADMIN")
	public ResponseEntity<String> acceptEventSuggestionById(@PathVariable Long id) {
		String eventSuggestionSenderEmail = null; 
		
		try {
			eventSuggestionSenderEmail = submissionService.getEventSuggestionSenderEmailById(id);
			submissionService.acceptEventSuggestionById(id);
		} catch (ContactFormSubmissionException e) {
			return new ResponseEntity<>(
				e.getMessage(), 
				HttpStatus.NOT_FOUND
			);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<>(
				"Successfully accepted & added event to database from suggestion with ID " + id + "! "
				+ e.getMessage(), 
				HttpStatus.NOT_FOUND
			);
		} catch (CityNotFoundException e) {
			return new ResponseEntity<>(
				"Failed to accept event suggestion with ID " + id + "! "
				+ e.getMessage(), 
				HttpStatus.NOT_FOUND
			);
		}
		
		return new ResponseEntity<>(
			"Successfully accepted & added event to database from suggestion with ID " + id + "! "
			+ "A notification e-mail was sent to " + eventSuggestionSenderEmail, 
			HttpStatus.OK
		);
	}

	/**
	 * This method rejects an event suggestion 
	 * and deletes that suggestion from the database.
	 * 
	 * A notification e-mail (with an optional rejection message) 
	 * is sent to the user who suggested the event.
	 * 
	 * @param id The id of the event suggestion to be rejected
	 * @param rejectionMessage an optional rejection message, which will be part of the notification email
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@PostMapping("/event-suggestions/{id}/reject")
	@Secured("ROLE_ADMIN")
	public ResponseEntity<String> rejectEventSuggestionById(
		@PathVariable Long id, 
		@RequestParam(name="message", required=false) String rejectionMessage) 
	{
		String eventSuggestionSenderEmail = null;
		
		try {
			eventSuggestionSenderEmail = submissionService.getEventSuggestionSenderEmailById(id);
			submissionService.rejectEventSuggestionById(id, rejectionMessage);
		} catch (ContactFormSubmissionException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<>(
				"Successfully rejected event suggestion with ID " + id + "! "
				+ e.getMessage(), 
				HttpStatus.NOT_FOUND
			);
		}
		
		return new ResponseEntity<>(
			"Successfully rejected event suggestion with ID " + id + "! "
			+ "A notification e-mail was sent to " + eventSuggestionSenderEmail, 
			HttpStatus.OK
		);
	}
	
}