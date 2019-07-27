package com.ipb.platform.validation;

/**
 * This exception is thrown whenever someone 
 * attempts to create a user with an e-mail address 
 * that already exists in the database.
 * 
 * @author dvt32
 */
@SuppressWarnings("serial")
public class EmailExistsException 
	extends Throwable 
{

    public EmailExistsException(final String message) {
        super(message);
    }

}