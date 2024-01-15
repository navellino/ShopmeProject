package it.shopme.site.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import it.shopme.common.entity.AuthenticationType;
import it.shopme.common.entity.Customer;
import it.shopme.customer.CustomerService;

@Component
public class DatabaseLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private CustomerService service;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		
		CustomerUserDetail userDetail = (CustomerUserDetail) authentication.getPrincipal();
		Customer customer = userDetail.getCustomer();
		service.updateAuthenticationType(customer, AuthenticationType.DATABASE);
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
	
	
}
