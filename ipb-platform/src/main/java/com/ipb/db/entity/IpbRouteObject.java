package com.ipb.db.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * This class represents 
 * the relationship between "routes" and "objects"
 * in the database.
 * 
 * It is converted to a table in the database via Hibernate.
 * The fields in the class become the table's attributes. 
 * Certain restrictions on the attributes' length/value are set via JPA annotations
 * and are then applied by Hibernate when creating the tables.
 * 
 * NOTE: This class may become unnecessary when replaced 
 * by proper JPA annotations for establishing table relationships.
 * 
 * @author Dimitar Trifonov (dvt32)
 */
@Entity
@Table(name = "Routes_Objects")
public class IpbRouteObject {
	
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
	public IpbRouteObject() {
		
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