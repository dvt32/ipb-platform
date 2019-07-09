package com.ipb.platform.controllers;

import java.util.List;
import java.util.StringJoiner;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ipb.platform.dto.requests.UserRequestDTO;
import com.ipb.platform.dto.responses.UserResponseDTO;
import com.ipb.platform.persistence.entities.UserType;
import com.ipb.platform.services.UserService;
import com.ipb.platform.validation.EmailExistsException;
import com.ipb.platform.validation.UserNotFoundException;

import lombok.AllArgsConstructor;

/**
 * This controller allows CRUD operations 
 * for users in the database via HTTP requests.
 * 
 * @author dvt32
 */
@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	/**
	 * This method retrieves all users (and their associated data) in JSON format
	 * 
	 * @return the list of users (empty if no users available)
	 */
	@GetMapping
	public List<UserResponseDTO> getAll() {
		return userService.getAll();
	}

	/**
	 * This method retrieves a user's info in JSON format.
	 * 
	 * @param id The id of the user to be retrieved
	 * @return a ResponseEntity containing the user's data and an appropriate response code
	 */
	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable Long id) {
		UserResponseDTO user = null;
		
		try {
			user = userService.findById(id);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	/**
	 * This method creates a user by passing the user's data in a POST request's body.
	 * The data is validated before the user is stored in the database.
	 * 
	 * @param user An object containing the user-to-be-created's data
	 * @param bindingResult The validator of the passed data
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@ResponseBody
	@PostMapping
	public ResponseEntity<String> createUser(
		@RequestBody @Valid UserRequestDTO user, 
		BindingResult bindingResult) 
	{
		if (bindingResult.hasErrors()) {
			String responseErrorMessage = getResponseErrorMessageFromBindingResult(bindingResult);
			return new ResponseEntity<>(responseErrorMessage, HttpStatus.BAD_REQUEST);
		}
		
		Long createdUserId;
		try {
			createdUserId = userService.createNewUser(user);
		} catch (EmailExistsException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>("Successfully created user with ID " + createdUserId, HttpStatus.CREATED);
	}
	
	/**
	 * This method updates an existing user in the database 
	 * by passing the updated user's data in a POST request's body.
	 * The data is validated before the user is updated in the database.
	 * 
	 * @param id The user-to-be-updated's ID
	 * @param user An object containing the user-to-be-updated's data
	 * @param bindingResult The validator of the passed data
	 * 
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<String> updateUserById(
		@PathVariable Long id, 
		@RequestBody @Valid UserRequestDTO user, 
		BindingResult bindingResult) 
	{
		if (bindingResult.hasErrors()) {
			String responseErrorMessage = getResponseErrorMessageFromBindingResult(bindingResult);
			return new ResponseEntity<>(responseErrorMessage, HttpStatus.BAD_REQUEST);
		}
			
		try {
			userService.updateUserById(id, user);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>("Successfully updated user with ID " + id, HttpStatus.OK);
	}
	
	/**
	 * This method deletes an existing user in the database by passing an ID.
	 * If the user does not exist, an exception is thrown.
	 * 
	 * @param id The ID of the user
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteUserById(@PathVariable Long id) 
	{
		try {
			userService.deleteUserById(id);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>("Successfully deleted user with ID " + id, HttpStatus.OK);
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
	
	/**
	 * Gives a user admin privileges by passing in the user's e-mail.
	 * 
	 * @param userEmailAddress The target user's e-mail
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@PostMapping(value="/make-user-admin", params="email")
	public ResponseEntity<String> makeUserAdminByEmail(@RequestParam("email") String userEmailAddress) 
	{	
		try {
			userService.setUserRoleByEmail(userEmailAddress, UserType.ADMIN);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>("Successfully set user with e-mail " + userEmailAddress + " to admin!", HttpStatus.OK);
	}
	
	/**
	 * Removes a user's admin privileges by passing in the user's ID.
	 * 
	 * @param userId The target user's ID
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@PostMapping(value="/make-user-admin", params="id")
	public ResponseEntity<String> makeUserAdminById(@RequestParam("id") Long userId) 
	{	
		try {
			userService.setUserRoleById(userId, UserType.ADMIN);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>("Successfully set user with ID " + userId + " to admin!", HttpStatus.OK);
	}
	
	/**
	 * Removes a user's admin privileges by passing in the user's e-mail.
	 * 
	 * @param userEmailAddress The target user's e-mail
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@PostMapping(value="/make-user-non-admin", params="email")
	public ResponseEntity<String> makeUserNonAdminByEmail(@RequestParam("email") String userEmailAddress) {
		try {
			userService.setUserRoleByEmail(userEmailAddress, UserType.USER);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>("Successfully set user with e-mail " + userEmailAddress + " to non-admin!", HttpStatus.OK);
	}
	
	/**
	 * Removes a user's admin privileges by passing in the user's ID.
	 * 
	 * @param userId The target user's id
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@PostMapping(value="/make-user-non-admin", params="id")
	public ResponseEntity<String> makeUserNonAdminById(@RequestParam("id") Long userId) 
	{	
		try {
			userService.setUserRoleById(userId, UserType.USER);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>("Successfully set user with ID " + userId + " to non-admin!", HttpStatus.OK);
	}

}