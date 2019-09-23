package com.ipb.platform.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents a suggestion for a new object 
 * to be added in the database (sent via a contact form).

 * @author dvt32
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="User_Object_Suggestions")
public class ObjectSuggestionEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "object_suggestion_sequence")
	private Long id;
	
	@NotBlank(message = "Sender e-mail must not be null or blank!")
	private String senderEmail;
	
	@NotBlank(message = "Suggested object's name must not be null or blank!")
	private String name;
	
	@NotBlank(message = "Suggested object's description must not be null or blank!")
	private String description;
	
	private double latitude;
	
	private double longitude;
	
	@Enumerated(EnumType.STRING)
	private ObjectType type;
	
}