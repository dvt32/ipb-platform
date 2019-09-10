package com.ipb.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class IpbPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(IpbPlatformApplication.class, args);
	}

	/**
	 * A password encoder bean for storing passwords in an encrypted BCrypt format.
	 * 
	 * @return an instance of the bean
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
