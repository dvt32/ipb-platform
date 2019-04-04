package com.ipb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "Users")
public class IpbUser {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	@Column(length = 100)
	@Size(max = 100)
	private String email;
	
	@NotNull
	@Column(length = 100)
	@Size(max = 100)
	private String password;
	
	@NotNull
	@Column(length = 255)
	@Size(max = 255)
	private String name;
	
	@NotNull
	private Long birthdayInMilliseconds;
	
	@NotNull
	@Column(length = 255)
	@Size(max = 255)
	private String rights;
	
	/*
	 * Constructors
	 */
	public IpbUser() {}

	/*
	 * Getters & setters
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getBirthdayInMilliseconds() {
		return birthdayInMilliseconds;
	}

	public void setBirthdayInMilliseconds(Long birthdayInMilliseconds) {
		this.birthdayInMilliseconds = birthdayInMilliseconds;
	}

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}
	
}