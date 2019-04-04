package com.ipb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "Categories")
public class IpbCategory {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	private Long parentId;
	
	@NotNull
	@Column(length = 255)
	@Size(max = 255)
	private String name;
	
	/*
	 * Constructors
	 */
	public IpbCategory() {}
	
	/*
	 * Getters & setters
	 */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}