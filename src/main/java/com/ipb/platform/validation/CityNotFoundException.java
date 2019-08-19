package com.ipb.platform.validation;

/**
 * This exception is thrown whenever someone 
 * attempts to manipulate a city
 * that does not exist in the database.
 * 
 * @author dvt32
 */
@SuppressWarnings("serial")
public class CityNotFoundException 
	extends Throwable 
{

    public CityNotFoundException(final String message) {
        super(message);
    }

}