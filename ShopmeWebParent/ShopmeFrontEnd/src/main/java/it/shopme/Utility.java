package it.shopme;

import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import it.shopme.setting.EmailSettingBag;

public class Utility {
	public static String getSiteUrl(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}
	
	public static JavaMailSenderImpl prepareMailSender(EmailSettingBag settings) {
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		
		
		mailSender.setHost(settings.getHost());
		mailSender.setPort(settings.getPort());
		mailSender.setUsername(settings.getUsername());
		mailSender.setPassword(settings.getPassword());
		
		Properties mailProperties = mailSender.getJavaMailProperties();
		
		mailProperties.put("mail.transport.protocol", "smtp");
		mailProperties.put("mail.smtp.auth", settings.getSmtpAuth());
		mailProperties.put("mail.smtp.starttls.enable", settings.getSmtpSecured());
		//mailProperties.put("mail.smtp.ssl.enable", "true");
		mailProperties.put("mail.debug", "true");
		
		mailSender.setJavaMailProperties(mailProperties);
		
		return mailSender;
		
	}
}
