package it.shopme.setting;

import java.util.List;

import it.shopme.common.entity.Settings;
import it.shopme.common.entity.SettingsBag;

public class EmailSettingBag extends SettingsBag {

	public EmailSettingBag(List<Settings> listSettings) {
		super(listSettings);
	}
	
	public String getHost() {
		return super.getValue("MAIL_HOST");
	}
	
	public String getUsername() {
		return super.getValue("MAIL_USERNAME");
	}
	
	public int getPort() {
		return Integer.parseInt(super.getValue("MAIL_PORT"));
	}
	
	public String getPassword() {
		return super.getValue("MAIL_PASSWORD");
	}
	
	public String getFrom() {
		return super.getValue("MAIL_FROM");
	}
	
	public String getSenderName() {
		return super.getValue("MAIL_SENDER_MAIL");
	}
	
	public String getSmtpAuth() {
		return super.getValue("SMTP_AUTH");
	}
	
	public String getSmtpSecured() {
		return super.getValue("SMTP_SECURED");
	}
	
	public String getCustomerVerifySubject() {
		return super.getValue("CUSTOMER_VERIFY_SUBJECT");
	}
	
	public String getCustomerVerifyContent() {
		return super.getValue("CUSTOMER_VERIFY_CONTENT");
	}

	@Override
	public String toString() {
		return "EmailSettingBag [getHost()=" + getHost() + ", getUsername()=" + getUsername() + ", getPort()="
				+ getPort() + ", getPassword()=" + getPassword() + ", getFrom()=" + getFrom() + ", getSenderName()="
				+ getSenderName() + ", getSmtpAuth()=" + getSmtpAuth() + ", getSmtpSecured()=" + getSmtpSecured()
				+ ", getCustomerVerifySubject()=" + getCustomerVerifySubject() + ", getCustomerVerifyContent()="
				+ getCustomerVerifyContent() + "]";
	}
	
	
}
