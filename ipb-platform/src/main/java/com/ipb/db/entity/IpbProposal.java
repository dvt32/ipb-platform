package com.ipb.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * This class represents 
 * a "proposal"
 * in the database.
 * 
 * It is converted to a table in the database via Hibernate.
 * The fields in the class become the table's attributes. 
 * Certain restrictions on the attributes' length/value are set via JPA annotations
 * and are then applied by Hibernate when creating the tables.
 * 
 * @author Dimitar Trifonov (dvt32)
 */
@Entity
@Table(name = "Proposals")
public class IpbProposal {

	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	private Long userId;
	
	@NotNull
	@Lob
	@Column(length=100000)
	private byte[] description;
	
	@NotNull
	private boolean isChecked;
	
	@NotNull
	private boolean isApproved;
	
	/*
	 * Constructors	
	 */
	public IpbProposal() {
		
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

	public byte[] getDescription() {
		return description;
	}

	public void setDescription(byte[] description) {
		this.description = description;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public boolean isApproved() {
		return isApproved;
	}

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}
	
}