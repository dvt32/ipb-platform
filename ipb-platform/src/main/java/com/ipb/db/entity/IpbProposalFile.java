package com.ipb.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * This class represents 
 * a "proposal file"
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
@Table(name = "Proposals_Files")
public class IpbProposalFile {

	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	private Long proposalId;
	
	@NotNull
	@Column(length = 2000)
	@Size(max = 2000)
	private String path;
	
	/*
	 * Constructors
	 */
	public IpbProposalFile() {
		
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

	public Long getProposalId() {
		return proposalId;
	}

	public void setProposalId(Long proposalId) {
		this.proposalId = proposalId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}