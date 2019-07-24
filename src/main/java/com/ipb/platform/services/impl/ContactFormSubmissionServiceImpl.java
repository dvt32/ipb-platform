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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ipb.platform.dto.requests.ContactFormSubmissionRequestDTO;
import com.ipb.platform.dto.responses.ContactFormSubmissionResponseDTO;
import com.ipb.platform.mappers.ContactFormSubmissionMapper;
import com.ipb.platform.persistence.ContactFormSubmissionRepository;
import com.ipb.platform.persistence.UserRepository;
import com.ipb.platform.persistence.entities.ContactFormSubmissionEntity;
import com.ipb.platform.services.ContactFormSubmissionService;
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
	private ContactFormSubmissionMapper submissionMapper;
	
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
	
}