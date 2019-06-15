package com.ipb.platform.persistance.entities;

public enum ObjectType {
	LANDMARK("LANDMARK"), EVENT("EVENT"), CITY("CITY"), VILLAGE("VILLAGE");

	private final String text;

	ObjectType(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

	public static String[] allRegisteredUsers() {
		String[] adminModAndUSer = { ObjectType.LANDMARK.toString(), ObjectType.EVENT.toString(), ObjectType.CITY.toString(), ObjectType.VILLAGE.toString() };
		return adminModAndUSer;
	}
}
