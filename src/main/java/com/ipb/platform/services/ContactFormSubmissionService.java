package com.ipb.platform.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ipb.platform.dto.requests.ContactFormSubmissionRequestDTO;
import com.ipb.platform.dto.requests.EventSuggestionRequestDTO;
import com.ipb.platform.dto.requests.ObjectSuggestionRequestDTO;
import com.ipb.platform.dto.responses.ContactFormSubmissionResponseDTO;
import com.ipb.platform.dto.responses.EventSuggestionResponseDTO;
import com.ipb.platform.dto.responses.ObjectSuggestionResponseDTO;
import com.ipb.platform.validation.CityNotFoundException;
import com.ipb.platform.validation.ContactFormSubmissionException;
import com.ipb.platform.validation.UserNotFoundException;

/**
 * This interface specifies the list of operations allowed on contact form submissions.
 * 
 * @author dvt32
 */
public interface ContactFormSubmissionService {
	Long createNewSubmission(ContactFormSubmissionRequestDTO submissionRequestDTO) throws UserNotFoundException;
	
	void storeFileForSubmissionById(Long id, MultipartFile file) throws IOException, ContactFormSubmissionException;
	
	boolean hasFileAttachedById(Long id);
	
	byte[] getFileAttachedById(Long id) throws IOException, ContactFormSubmissionException;
	
	String getNameOfFileAttachedById(Long id) throws ContactFormSubmissionException;
	
	List<ContactFormSubmissionResponseDTO> getAllSubmissions();
	
	ContactFormSubmissionResponseDTO getSubmissionById(Long id) throws ContactFormSubmissionException;
	
	void deleteSubmissionById(Long id) throws ContactFormSubmissionException;
	
	Long createNewObjectSuggestion(ObjectSuggestionRequestDTO objectSuggestionRequestDTO) throws UserNotFoundException;
	
	Long createNewEventSuggestion(EventSuggestionRequestDTO eventSuggestionRequestDTO) throws UserNotFoundException;

	ObjectSuggestionResponseDTO getObjectSuggestionById(Long id) throws ContactFormSubmissionException;
	
	EventSuggestionResponseDTO getEventSuggestionById(Long id) throws ContactFormSubmissionException;
	
	List<ObjectSuggestionResponseDTO> getAllObjectSuggestions();
	
	List<EventSuggestionResponseDTO> getAllEventSuggestions();
	
	void acceptObjectSuggestionById(Long id) throws ContactFormSubmissionException, UserNotFoundException;
	
	String getObjectSuggestionSenderEmailById(Long id) throws ContactFormSubmissionException;
	
	void rejectObjectSuggestionById(Long id, String rejectionMessage) throws ContactFormSubmissionException, UserNotFoundException;
	
	void acceptEventSuggestionById(Long id) throws ContactFormSubmissionException, UserNotFoundException, CityNotFoundException;
	
	String getEventSuggestionSenderEmailById(Long id) throws ContactFormSubmissionException;
	
	void rejectEventSuggestionById(Long id, String rejectionMessage) throws ContactFormSubmissionException, UserNotFoundException;
}