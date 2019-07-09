package com.ipb.platform.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ipb.platform.persistence.UserRepository;
import com.ipb.platform.persistence.entities.UserEntity;

/**
 * This class implements a custom UserDetailsService.
 * It gets the users stored in the database 
 * and gives these users appropriate authorities.
 * (based on their role).
 * 
 * @author dvt32
 */
@Service
public class CustomUserDetailsService 
	implements UserDetailsService 
{

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) {
		UserEntity userEntity = userRepository.findByEmail(email).get();
		
		if (userEntity == null) {
			throw new UsernameNotFoundException(email);
		}

		String username = userEntity.getEmail();
		// password is auto-encoded by auth provider after login POST request
		String encodedPassword = userEntity.getPassword(); 
		String role = userEntity.getType().toString();
		List<SimpleGrantedAuthority> listOfAuthorities = getAuthorities(role);

		User user = new User(username, encodedPassword, listOfAuthorities);

		return user;
	}

	private List<SimpleGrantedAuthority> getAuthorities(String userRole) {
		List<SimpleGrantedAuthority> listOfAuthorities = new ArrayList<>();
		
		listOfAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		if (userRole.equals("ADMIN")) {
			listOfAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}

		return listOfAuthorities;
	}
	
}