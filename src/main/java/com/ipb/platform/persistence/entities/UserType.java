package com.ipb.platform.persistence.entities;

/**
 * This enum describes the type of allowed user roles in the system.
 * 
 * @author dvt32
 */
public enum UserType {

	ADMIN("ADMIN"), USER("USER");

	private final String text;

	UserType(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

	public static String[] allRegisteredUsers() {
		String[] adminAndUser = { 
			UserType.ADMIN.toString(),
			UserType.USER.toString() 
		};
		
		return adminAndUser;
	}

}