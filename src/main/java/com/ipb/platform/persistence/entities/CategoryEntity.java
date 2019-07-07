package com.ipb.platform.persistence.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "categories")
@Inheritance(strategy = InheritanceType.JOINED)

public class CategoryEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
	private Long id;
	
	private Long parentId;
	
	private String name;
	
	@ManyToMany(fetch = FetchType.LAZY,
	     cascade = {
	         CascadeType.PERSIST,
	         CascadeType.MERGE
	     },
	     mappedBy = "categories"
	)
	private List<ObjectEntity> objects;

	@ManyToMany(fetch = FetchType.LAZY,
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE
			},
			mappedBy = "categories"
	)
	private List<UserEntity> users;
}
