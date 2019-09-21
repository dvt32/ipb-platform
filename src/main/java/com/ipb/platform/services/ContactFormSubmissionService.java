package com.ipb.platform.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ipb.platform.dto.requests.ContactFormSubmissionRequestDTO;
import com.ipb.platform.dto.responses.ContactFormSubmissionResponseDTO;
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
	
}