package com.ipb.platform.persistance.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "images")
public class ImageEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_seq")
	private long id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "object_id")
	private ObjectEntity object;
	
	private String base64Code;
	
	private String path;
}
