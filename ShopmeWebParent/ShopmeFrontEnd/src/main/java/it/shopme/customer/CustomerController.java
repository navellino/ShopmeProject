package it.shopme.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import it.shopme.Utility;
import it.shopme.common.entity.Country;
import it.shopme.common.entity.Customer;
import it.shopme.setting.EmailSettingBag;
import it.shopme.setting.SettingService;
import it.shopme.site.security.CustomerUserDetail;
import it.shopme.site.security.oauth.CustomerOAuth2User;

@Controller
public class CustomerController {
	@Autowired
	private CustomerService service;
	
	@Autowired
	private SettingService settingService;
	
	
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		List<Country> listAllCountries = service.listAllCountries();
		
		model.addAttribute("listAllCountries", listAllCountries);
		model.addAttribute("customer", new Customer());
		
		return "register/register_form";
	}
	
	@PostMapping("/create_customer")
	public String saveCustomer(Customer customer, HttpServletRequest request) 
			throws UnsupportedEncodingException, MessagingException {
		service.registerCustomer(customer);
		
		sendVerificationEmail(request, customer);
		
		return "register/register_success";
	}

	private void sendVerificationEmail(HttpServletRequest request, Customer customer) 
			throws UnsupportedEncodingException, MessagingException {
		
		EmailSettingBag emailSettings = settingService.getEmailSettings();
				
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		
		String toAddress = customer.getEmail();
		String subject = emailSettings.getCustomerVerifySubject();
		String content = emailSettings.getCustomerVerifyContent();
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFrom());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		content = content.replace("[[name]]", customer.getFullName());
		
		String verifyURL = Utility.getSiteUrl(request) + "/verify?code=" + customer.getVerificationCode();
		
		content = content.replace("[[URL]]", verifyURL);
		
		helper.setText(content, true);
		System.out.println(toAddress);
		System.out.println(verifyURL);

		mailSender.send(message);
	}
	
	@GetMapping("/verify")
	public String verifyCustomer(@Param("code") String code, Model model) {
		boolean verified =  service.verifyCustomer(code);
		
		return "register/" + (verified ? "verify_success" : "verify_fail");
		
	}
	
	@GetMapping("/account_details")
	public String getAccountDetials(Model model, HttpServletRequest request) {
		String email = getEmailOfAuthentiatedCustomer(request);
		
		Customer customer  = service.getCustomerByEmail(email);
		List<Country> listAllCountries = service.listAllCountries();
		
		model.addAttribute("listAllCountries", listAllCountries);
		model.addAttribute("customer", customer);
		
		return "customer/account_form";
	}
	
	private String getEmailOfAuthentiatedCustomer(HttpServletRequest request) {
		Object principal = request.getUserPrincipal();
		String customerEmail = null;
		
		if(principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken) {
			customerEmail = request.getUserPrincipal().getName();
		}else if (principal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
			CustomerOAuth2User oauth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
			customerEmail = oauth2User.getEmail();
		}
		return customerEmail;
	}
	
	@PostMapping("/update_account_detail")
	public String updateAccountDetail(HttpServletRequest request,Customer customer, Model model) {
		service.update(customer);
		
		updateNameForAuthenticatedCustomer(customer, request);
		
		return "redirect:/account_details";
	}

	private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
		Object principal = request.getUserPrincipal();
		
		if(principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken) {
			CustomerUserDetail userDetail = getCustomerUserDetailsObject(principal);
			Customer authenticatedCustomer = userDetail.getCustomer();
			authenticatedCustomer.setFirstName(customer.getFirstName());
			authenticatedCustomer.setLastName(customer.getLastName());
			
		}else if (principal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
			CustomerOAuth2User oauth2User = (CustomerOAuth2User) oauth2Token.getPrincipal();
			String fullName = customer.getFirstName() + " " + customer.getLastName();
			oauth2User.setFullName(fullName);
		}
	}
	
	private CustomerUserDetail getCustomerUserDetailsObject(Object principal) {
		CustomerUserDetail userDetails = null;
		if(principal instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
			userDetails = (CustomerUserDetail) token.getPrincipal();
		}else if(principal instanceof RememberMeAuthenticationToken) {
			RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
			userDetails = (CustomerUserDetail) token.getPrincipal();
					
		}
		return userDetails;
	}
}
