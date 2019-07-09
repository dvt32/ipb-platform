package com.ipb.platform.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.ipb.platform.dto.requests.UserRequestDTO;
import com.ipb.platform.persistence.entities.UserEntity;

public class PasswordMatchesValidator 
	implements ConstraintValidator<PasswordMatches, Object> 
{
	@Override
	public void initialize(PasswordMatches constraintAnnotation) {}

	public boolean isValid(Object object, ConstraintValidatorContext context) {
		String typeOfObjectToValidate = object.getClass().getSimpleName();
		
		if (typeOfObjectToValidate.equals("UserRequestDTO")) {
			final UserRequestDTO user = (UserRequestDTO) object;
	        return user.getPassword().equals(user.getMatchingPassword());
		}
		else if (typeOfObjectToValidate.equals("UserEntity")) {
			final UserEntity user = (UserEntity) object;
	        return user.getPassword().equals(user.getMatchingPassword());
		}
		
		return false;
	}
}