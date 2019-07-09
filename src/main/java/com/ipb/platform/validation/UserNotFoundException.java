package com.ipb.platform.validation;

/**
 * This exception is thrown whenever someone 
 * attempts to manipulate a user 
 * that does not exist in the database.
 * 
 * @author dvt32
 */
@SuppressWarnings("serial")
public class UserNotFoundException 
	extends Throwable 
{

    public UserNotFoundException(final String message) {
        super(message);
    }

}