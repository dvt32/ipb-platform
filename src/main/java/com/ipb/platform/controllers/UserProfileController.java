package com.ipb.platform.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ipb.platform.dto.requests.UserRequestDTO;
import com.ipb.platform.dto.responses.UserResponseDTO;
import com.ipb.platform.services.UserService;
import com.ipb.platform.validation.UserNotFoundException;

import lombok.AllArgsConstructor;

/**
 * This controller is used for executing operations
 * when the user has logged in to his account 
 * (changing his password, preferences etc.)
 * 
 * @author dvt32
 */
@AllArgsConstructor
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/profile")
public class UserProfileController {
	
	@Autowired
	private UserService userService;
	
	/**
	 * This method is used for changing a user's password in the system.
	 * 
	 * The user's name is retrieved via his security credentials.
	 * The method checks if the old password is correct and if the new passwords match.
	 * 
	 * If so, the method changes the user's password to the new one. Otherwise an error is thrown.
	 * 
	 * @param oldPassword The user's old password, which is to be validated in the method
	 * @param newPassword The new password wanted by the user
	 * @param matchingNewPassword A confirmation of the new password wanted by the user
	 * 
	 * @return A ResponseEntity containing a success or an error message, depending on the operation's status
	 */
	@PostMapping("/change-password")
	@ResponseBody
	public ResponseEntity<String> changePassword(
		@RequestParam String oldPassword, 
		@RequestParam String newPassword,
		@RequestParam String matchingNewPassword) 
	{
		String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		UserRequestDTO user;
		try {
			user = userService.convertResponseDtoToRequestDto( 
				userService.findByEmail(loggedInUserEmail) 
			);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		if ( !(userService.isCorrectPasswordForUser(user, oldPassword)) ) {
			return new ResponseEntity<String>("Incorrect old password!", HttpStatus.UNAUTHORIZED);
	    }
		if ( !(newPassword.equals(matchingNewPassword)) ) {
			return new ResponseEntity<String>("New passwords don't match!", HttpStatus.BAD_REQUEST);
		}
		if ( !(userService.isValidPassword(newPassword)) ) {
			return new ResponseEntity<String>("Invalid new password!", HttpStatus.BAD_REQUEST);
		}
		
		userService.changePassword(user, newPassword);
		
		return new ResponseEntity<String>("Successfully updated password!", HttpStatus.OK);
	}
	
	/**
	 * This method returns the currently logged in user's data from the database
	 * (excluding his password, for security reasons)
	 * 
	 * @return a ResponseEntity object with the user's data in the body, or one with an error message
	 */
	@GetMapping("/get-data")
	public ResponseEntity<?> getUserData() {
		String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		
		UserResponseDTO userResponseDTO = null;
		try {
			userResponseDTO = userService.findByEmail(loggedInUserEmail);
			userResponseDTO.setPassword("");
			userResponseDTO.setMatchingPassword("");
		} catch (UserNotFoundException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<UserResponseDTO>(userResponseDTO, HttpStatus.OK);
	}
	
	/**
	 * This method updates the currently logged in user's data in the database 
	 * by passing the updated user's data in a POST request's body.
	 * The data is validated before the user is updated in the database.
	 * 
	 * @param user An object containing the user-to-be-updated's data
	 * @param bindingResult The validator of the passed data
	 * 
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@PutMapping("/edit-data")
	public ResponseEntity<String> updateUserData(
		@RequestBody @Valid UserRequestDTO userRequestDTO,
		BindingResult bindingResult) 
	{
		String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

		if (bindingResult.hasErrors()) {
			String responseErrorMessage = UserController.getResponseErrorMessageFromBindingResult(bindingResult);
			return new ResponseEntity<String>(responseErrorMessage, HttpStatus.BAD_REQUEST);
		}
			
		try {
			userService.updateUserByEmail(loggedInUserEmail, userRequestDTO);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		// If user changed his e-mail, log him out
		if ( !userRequestDTO.getEmail().equals(loggedInUserEmail) ) {
			SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
		}
		
		return new ResponseEntity<String>("Successfully updated user!", HttpStatus.OK);
	}
	
	/**
	 * This method deletes the currently logged in user from the database.
	 * 
	 * @return a ResponseEntity object with either a success or an error message.
	 */
	@DeleteMapping
	@Transactional
	public ResponseEntity<String> deleteUser(HttpServletRequest request) {
		String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		
		// Logout user and remove privileges
		SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
		
		try {
			userService.deleteUserByEmail(loggedInUserEmail);
		} catch (UserNotFoundException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<String>("Successfully deleted user!", HttpStatus.OK);
	}
	
}