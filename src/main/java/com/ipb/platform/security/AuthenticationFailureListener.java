package com.ipb.platform.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

/**
 * This class listens for a login failure
 * and informs the login service class about it.
 * Afterwards the login failure handler uses 
 * the information that was sent to the login service.
 * 
 * @author dvt32
 */
@Component
public class AuthenticationFailureListener 
  implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> 
{
	@Autowired
    private LoginAttemptService loginAttemptService;
	
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
		WebAuthenticationDetails auth = (WebAuthenticationDetails) e.getAuthentication().getDetails();     
		loginAttemptService.loginFailed(auth.getRemoteAddress());
    }
}