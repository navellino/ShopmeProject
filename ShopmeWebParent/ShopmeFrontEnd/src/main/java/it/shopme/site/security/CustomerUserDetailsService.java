package it.shopme.site.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import it.shopme.common.entity.Customer;
import it.shopme.customer.CustomerRepository;

public class CustomerUserDetailsService implements UserDetailsService {

	@Autowired
	private CustomerRepository repository;
		
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Customer customer = repository.findByEmail(email);//per username utilizzeremo la email del cliente
		if(customer == null) {
			throw new UsernameNotFoundException("nessun cliente con email "+ email);
		}
		return new CustomerUserDetail(customer);
	}

}
