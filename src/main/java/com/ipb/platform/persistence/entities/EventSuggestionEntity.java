package com.ipb.platform.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This class represents a suggestion for a new event 
 * to be added in the database (sent via a contact form).
 * 
 * The start & end dates are stored as java.util.Date objects, 
 * because both the time and the date 
 * need to be stored in the database 
 * (java.sql.Date stores only the date).

 * @author dvt32
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name="User_Event_Suggestions")
public class EventSuggestionEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_suggestion_sequence")
	private Long id;
	
	@NotBlank(message = "Sender e-mail must not be null or blank!")
	private String senderEmail;
	
	@NotBlank(message = "Event's name must not be null or blank!")
	private String name;
	
	@NotBlank(message = "Event's description must not be null or blank!")
	private String description;
	
	@NotNull(message = "Event's city ID must not be null!")
	private Long cityId;
	
	@NotNull(message = "Event's start date must not be be null!")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm", timezone = "Europe/Sofia")
    private java.util.Date startDate;
	
	@NotNull(message = "Event's end date must not be be null!")
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm", timezone = "Europe/Sofia")
    private java.util.Date endDate;
	
	private double latitude;
	
	private double longitude;
	
}