package it.shopme.site.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import it.shopme.common.entity.Customer;

public class CustomerUserDetail implements UserDetails {

	private static final long serialVersionUID = 1118508495528107464L;
	
	private Customer customer;

	public CustomerUserDetail(Customer customer) {
		this.setCustomer(customer);
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Lo lascio invariato perché nel cliente non c'è gestione dei ruoli
		return null;
	}

	@Override
	public String getPassword() {
		return customer.getPassword();
	}

	@Override
	public String getUsername() {
		//come username utilizzo l'email del cliente
		return customer.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return customer.isEnable();
	}

	public String getFullName() {
		return customer.getLastName() + " " + customer.getFirstName();
	}
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
}
