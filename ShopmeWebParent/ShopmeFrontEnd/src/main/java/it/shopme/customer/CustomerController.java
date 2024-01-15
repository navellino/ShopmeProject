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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import it.shopme.Utility;
import it.shopme.common.entity.Country;
import it.shopme.common.entity.Customer;
import it.shopme.setting.EmailSettingBag;
import it.shopme.setting.SettingService;

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
	
}
