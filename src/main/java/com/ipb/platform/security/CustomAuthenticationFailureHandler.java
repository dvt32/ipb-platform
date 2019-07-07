package com.ipb.platform.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * This class defines what happens when a user
 * has entered incorrect login info.
 * 
 * If his IP is blocked, an informative message is sent via the HTTP response.
 * Otherwise he is simply informed that he entered a wrong username/password
 * and has the option of trying again.
 * 
 * @author dvt32
 */
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Autowired
	LoginAttemptService loginService;

    @Override
    public void onAuthenticationFailure(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException exception)
    	throws IOException, ServletException 
    {
    	String ip = getClientIP(request);
    	
    	if (loginService.isBlocked(ip)) {
        	response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "IP blocked for " + loginService.getBlockTimeInMinutes() + " minutes!");
    	}
    	else {
    		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bad credentials");
    	}
    }
    
    public String getClientIP(HttpServletRequest request) {
    	String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        else {
        	return xfHeader.split(",")[0];
        }
    }
    
}