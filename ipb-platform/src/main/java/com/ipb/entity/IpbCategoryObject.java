package com.ipb.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Categories_Objects")
public class IpbCategoryObject {

	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	private Long objectId;
	
	@NotNull
	private Long categoryId;
	
	/*
	 * Constructors
	 */
	public IpbCategoryObject() {
		
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

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
}
