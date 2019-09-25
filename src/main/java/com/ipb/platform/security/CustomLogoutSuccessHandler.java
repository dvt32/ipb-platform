package com.ipb.platform.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * This class defines what happens when a user
 * logs out of the system successfully.
 * 
 * An OK response code is sent, along with an informative message.
 * 
 * @author dvt32
 */
@Component
public class CustomLogoutSuccessHandler 
	extends SimpleUrlLogoutSuccessHandler 
	implements LogoutSuccessHandler 
{
    @Override
    public void onLogoutSuccess(
      HttpServletRequest request, 
      HttpServletResponse response, 
      Authentication authentication) 
    	throws IOException, ServletException
    {
    	response.setStatus(200);
    	response.getWriter().write("Successfully logged out!");
    }
}