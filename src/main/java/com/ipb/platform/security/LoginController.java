package com.ipb.platform.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A temporary controller class for testing protected resources.
 * 
 * @author dvt32
 */
@RestController
public class LoginController {
	
	@GetMapping("/catalog")
	public String catalog() {
		return "Here is our protected catalog...";
	}
	
}