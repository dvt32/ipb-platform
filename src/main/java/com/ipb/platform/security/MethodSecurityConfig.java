package com.ipb.platform.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * This class allows the use of Spring Security on a class/method level.
 * 
 * For example annotating a method with @Secured("ROLE_ADMIN") 
 * makes the method accessible only to a logged-in administrator.
 * This can also be applied on a class (to make every method accessible
 * only by an admin).
 *
 * @author dvt32
 */
@Configuration
@EnableGlobalMethodSecurity(
  prePostEnabled = true, // enables Spring Security pre/post annotations
  securedEnabled = true, // determines if the @Secured annotation should be enabled
  jsr250Enabled = true) // allows us to use the @RoleAllowed annotation
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
	
}