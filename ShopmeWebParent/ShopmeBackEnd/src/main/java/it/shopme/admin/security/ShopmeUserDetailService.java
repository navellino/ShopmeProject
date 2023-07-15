package it.shopme.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import it.shopme.admin.user.UserRepository;
import it.shopme.common.entity.User;

public class ShopmeUserDetailService implements UserDetailsService {

	@Autowired
	private UserRepository repository;
	
	
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = repository.getUserByEmail(email);
		if(user != null) {
			return new ShopmeUserDetails(user);
		}
		throw new UsernameNotFoundException("Utente non presente con email: "+ email);
	}

}
