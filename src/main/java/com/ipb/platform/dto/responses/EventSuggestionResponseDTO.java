package com.ipb.platform.dto.responses;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An instance of this class is sent back to the contact form submission controller
 * after the contact form submission service is done manipulating 
 * the event suggestion request DTO in some way.
 * 
 * Note: The @JsonFormat annotation is needed 
 * so that Jackson can deserialize the date properly
 * and so that any GET HTTP requests will give
 * a response with a properly-formatted date.
 * 
 * @author dvt32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSuggestionResponseDTO {
	
	private Long id;
	
	private String senderEmail;
	
	private String name;
	
	private String description;
	
	private Long cityId;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm", timezone = "Europe/Sofia")
    private java.util.Date startDate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm", timezone = "Europe/Sofia")
    private java.util.Date endDate;
	
	private double latitude;
	
	private double longitude;
	
}