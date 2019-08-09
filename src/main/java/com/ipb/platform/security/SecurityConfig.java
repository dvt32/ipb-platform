package com.ipb.platform.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * This class defines Spring Security configuration
 * like what users have what privileges,
 * which pages are protected, how is login/logout handled etc.
 * 
 * @author dvt32
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig 
	extends WebSecurityConfigurerAdapter 
{
	@Autowired
	private CustomUserDetailsService userDetailsService;
	 
	@Override
	protected void configure(final AuthenticationManagerBuilder auth) 
		throws Exception 
	{
		auth.authenticationProvider(authProvider());
	}
	
	@Override
    protected void configure(HttpSecurity http) 
    	throws Exception 
	{
		http
		    .csrf().disable()
		    .exceptionHandling()
		    .authenticationEntryPoint(restAuthenticationEntryPoint())
		    .and()
			    .authorizeRequests()
			    	.antMatchers("/profile/**").authenticated()
		    .and()
		    	.formLogin()
		    	.successHandler(customAuthenticationSuccessHandler())
		    	.failureHandler(customAuthenticationFailureHandler())
		    .and()
		    	.logout();
		
		// Allow access to H2 console for development purposes
		http.authorizeRequests().antMatchers("/h2_console/**").permitAll();
		http.headers().frameOptions().disable();
    }

	@Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
	
    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
    
    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }
    
    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
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