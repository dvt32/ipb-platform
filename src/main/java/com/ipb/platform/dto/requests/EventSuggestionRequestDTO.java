package com.ipb.platform.dto.requests;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An instance of this class is used 
 * when making HTTP requests related to users'
 * event suggestions (made via the contact form).
 * 
 * Note: JPA annotations are added here, 
 * so that Spring can validate the request object 
 * before sending it to the contact form submission service.
 * 
 * @author dvt32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSuggestionRequestDTO {
	
	private Long id;
	
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