package com.ipb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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