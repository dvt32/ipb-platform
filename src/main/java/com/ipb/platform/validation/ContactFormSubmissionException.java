package com.ipb.platform.validation;

/**
 * This exception is thrown whenever someone 
 * attempts to manipulate a contact form submission 
 * that does not exist in the database.
 * 
 * @author dvt32
 */
@SuppressWarnings("serial")
public class ContactFormSubmissionException 
	extends Throwable 
{

    public ContactFormSubmissionException(final String message) {
        super(message);
    }

}