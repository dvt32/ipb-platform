package com.ipb.db.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * This class represents 
 * the relationship between "users" and "categories"
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
@Table(name = "Users_Categories")
public class IpbUserCategory {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	private Long userId;
	
	@NotNull
	private Long categoryId;
	
	/*
	 * Constructors
	 */
	public IpbUserCategory() {
		
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	
}