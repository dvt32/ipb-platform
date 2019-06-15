package com.ipb.platform.persistance.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "objects")
@Inheritance(strategy = InheritanceType.JOINED)

public class ObjectEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "object_seq")
	private Long id;
	
	private String name;
	
	private String description;
	
	private double latitude;
	
	private double longitude;
	
	@Enumerated(EnumType.STRING)
	private ObjectType type;
	
	@OneToMany(mappedBy = "object", fetch = FetchType.EAGER)
	private List<ImageEntity> images;
	
//	@ManyToMany(mappedBy = "objects", fetch = FetchType.EAGER)
	@ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                CascadeType.PERSIST,
                CascadeType.MERGE
            })
    @JoinTable(name = "object_categories",
            joinColumns = { @JoinColumn(name = "object_id",  referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "category_id",  referencedColumnName = "id") })
	private List<CategoryEntity> categories;
	
	/*@Column(name = "created_on")
	private Date createdOn;
	
	@Column(name = "modified_on")
	private Date modifiedOn;
	
	@PrePersist
	public void setDates() {
		this.createdOn = new Date();
		this.modifiedOn = new Date();
	}
	
	@PreUpdate
	public void updateDates() {
		this.modifiedOn = new Date();
	}*/
}
