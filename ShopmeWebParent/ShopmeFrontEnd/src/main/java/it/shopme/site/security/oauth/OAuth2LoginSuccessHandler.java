package it.shopme.site.security.oauth;

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
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private CustomerService service;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		
		CustomerOAuth2User oAuth2User = (CustomerOAuth2User) authentication.getPrincipal();
		
		String name = oAuth2User.getName();
		String email = oAuth2User.getEmail();
		String countryCode = request.getLocale().getCountry();
		String clinetName = oAuth2User.getClientName();
		
		System.out.println("CustomerOAuth2User: "+ name + " | " + email + " | " + countryCode);
		System.out.println("Clinet Name: " + clinetName);
		
		
		Customer customer = service.getCustomerByEmail(email);
		
		if(customer == null) {
			service.addNewCustomerUponOAuthLogin(name, email, countryCode, getAuthenticationType(clinetName));
		}else {
			oAuth2User.setFullName(customer.getFullName());
			service.updateAuthenticationType(customer, getAuthenticationType(clinetName));
		}

		
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
	private AuthenticationType getAuthenticationType(String clientName) {
		if(clientName.equals("Google")) {
			return AuthenticationType.GOOGLE;
		}else if (clientName.equals("Facebook")) {
			return AuthenticationType.FACEBOOK;
		}else {
			return AuthenticationType.DATABASE;
		}
	}
}
