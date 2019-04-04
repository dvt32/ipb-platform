package com.ipb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "Images")
public class IpbImage {

	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	@Column(length = 2000)
	@Size(max = 2000)
	private String path;
	
	@NotNull
	private Long objectId;
	
	/*
	 * Constructors
	 */
	public IpbImage() {
		
	}
	
	/*
	 * Getters & setters
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}
}
