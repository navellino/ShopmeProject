package it.shopme.customer;

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
import it.shopme.common.entity.Customer;
import it.shopme.common.exception.CustomerNotFoundException;
import it.shopme.setting.EmailSettingBag;
import it.shopme.setting.SettingService;

@Controller
public class ForgotPasswordController {

	@Autowired
	private CustomerService service;
	
	@Autowired
	private SettingService settingService;
	
	@GetMapping("/forgot_password")
	public String showRequestForm() {
		return "customer/forgot_password_form";
	}
	
	@PostMapping("/forgot_password")
	public String processRequestForm(HttpServletRequest request, Model model) {
		
		String email = request.getParameter("email");
		try {
			String token = service.updateResetPasswordToken(email);
			System.out.println("Emai: "+ email);
			System.out.println("Token: "+ token);
			String link = Utility.getSiteUrl(request) + "/reset_password?token="+token;
			sendEmail(link, email);
			model.addAttribute("message", "Ti abbiamo inoltrato una email per il reset della password");
		} catch (CustomerNotFoundException e) {
			model.addAttribute("error", e.getMessage());
		} catch (MessagingException e) {
			model.addAttribute("error", "Impossibile inoltrare la mail");
		}
		
		return "customer/forgot_password_form";
	}
	
	private void sendEmail(String link, String email) throws MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		
		String toAddress = email;
		String subject = "Inoltro link per reimpostare la password";
		
		String content = "<p>Ciao,</p>"
				+ "<p>Hai richiesto il reset della password.</p>"
				+ "<p>Clicca nel link sottostante per cambiare la tua password: </p>"
				+ "<p><a href=\""+link+"\">Clicca qui per cambiare la tua password</a></p>"
				+ "<p>Ignora questa email se non hai chiesto tu il reset della password</p>";
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFrom());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		helper.setText(content, true);
		mailSender.send(message);
	}
	
	@GetMapping("/reset_password")
	public String showResetForm(@Param("token") String token, Model model) {
		Customer customer = service.getByResetPasswordToken(token);
		System.out.println(token);
		if(customer != null) {
			model.addAttribute("token", token);
			System.out.println("SONO ENTRATO");
		}else{
			model.addAttribute("message", "Token non valido");
			return "message";
		}
		
		return "customer/reset_password_form";
	}
	
	@PostMapping("/reset_password")
	public String updatePasswrd(HttpServletRequest request, Model model) {
		String token = request.getParameter("token");
		String password = request.getParameter("password");
		
		try {
			service.updatePassword(token, password);
			model.addAttribute("message", "Password aggiornata correttamente");
			return "message";
		} catch (CustomerNotFoundException e) {
			e.printStackTrace();
			model.addAttribute("message", e.getMessage());
			return "message";
		}
	}
}
