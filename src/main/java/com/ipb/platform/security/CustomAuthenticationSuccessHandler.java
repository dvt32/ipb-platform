package com.ipb.platform.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * This class defines what happens when a user
 * has entered correct login info.
 * 
 * If the user has entered correct info, but his IP is blocked,
 * authentication is revoked. Otherwise he has access 
 * to the protected resources.
 * 
 * @author dvt32
 */
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	
	private RequestCache requestCache = new HttpSessionRequestCache();
	
	@Autowired
	LoginAttemptService loginService;
 
	@Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication
			authentication) throws ServletException, IOException {
    	String ip = getClientIP(request);

    	if (loginService.isBlocked(ip)) {
    		authentication.setAuthenticated(false);
    		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "IP blocked for " + loginService.getBlockTimeInMinutes() + " minutes!");
    	}
    	else {
    		SavedRequest savedRequest = requestCache.getRequest(request, response);

    	    if (savedRequest == null) {
    	        clearAuthenticationAttributes(request);
    	        return;
    	    }
    	    String targetUrlParam = getTargetUrlParameter();
    	    if (isAlwaysUseDefaultTargetUrl() || 
    	    	(targetUrlParam != null && StringUtils.hasText(request.getParameter(targetUrlParam)))) 
    	    {
    	        requestCache.removeRequest(request, response);
    	        clearAuthenticationAttributes(request);
    	        return;
    	    }
    	
    	    clearAuthenticationAttributes(request);
    	}
    }

	public void setRequestCache(RequestCache requestCache) {
	    this.requestCache = requestCache;
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