package it.shopme.customer;

import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.shopme.common.entity.AuthenticationType;
import it.shopme.common.entity.Country;
import it.shopme.common.entity.Customer;
import it.shopme.common.exception.CustomerNotFoundException;
import it.shopme.setting.CountryRepository;
import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {

	@Autowired
	private CountryRepository countryRepo;
	
	@Autowired
	private CustomerRepository customerRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public List<Country> listAllCountries(){
		return countryRepo.findAllByOrderByNameAsc();
	}
	
	public boolean isEmailUnique(String email) {
		Customer customer = customerRepo.findByEmail(email);

		return customer == null;
	}
	
	public void registerCustomer(Customer customer) {
		encodePassword(customer);
		customer.setEnable(false);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(AuthenticationType.DATABASE);
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		customerRepo.save(customer);
	}

	private void encodePassword(Customer customer) {
		String encodedPassword =  passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);
	}
	
	public Customer getCustomerByEmail(String email) {
		return customerRepo.findByEmail(email);
	}
	
	public boolean verifyCustomer(String verificationCode) {
		
		Customer customer = customerRepo.findByVerificationCode(verificationCode);
		
		if(customer == null || customer.isEnable()) {
			return false;
		}else {
			customerRepo.enable(customer.getId());
			return true;
		}
	}
	
	public void updateAuthenticationType(Customer customer, AuthenticationType type) {
		if(!customer.getAuthenticationType().equals(type)) {
			customerRepo.uptadeAuthenticationType(customer.getId(), type);
		}
	}
	
	public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode, AuthenticationType authenticationType) {
		Customer customer = new Customer();
		
		customer.setEmail(email);
		
		setName(name, customer);
		
		customer.setEnable(true);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(authenticationType);
		customer.setPassword("");
		customer.setAddressLine1("");
		customer.setCity("");
		customer.setState("");
		customer.setPhoneNumber("");
		customer.setPostalCode("");
		customer.setCountry(countryRepo.findByCode(countryCode));
		
		customerRepo.save(customer);
	}
	
	private void setName(String name, Customer customer) {
		String[] nameArray = name.split(" ");
		if(nameArray.length < 2) {
			customer.setFirstName(name);
			customer.setLastName("");
		}else {
			String firstName = nameArray[0];
			customer.setFirstName(firstName);
			
			String lastName = name.replaceFirst(firstName + " ", "");
			customer.setLastName(lastName);
		}
	}
	
	public void update(Customer customerInForm) {
		
		Customer customerinDb = customerRepo.findById(customerInForm.getId()).get();
		
		if(customerinDb.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
			if(!customerInForm.getPassword().isEmpty()) {
				//se nel form è stata cambiata la password questa viene codificata e salvata
				String newPassword = passwordEncoder.encode(customerInForm.getPassword());
				customerInForm.setPassword(newPassword);
			}else {
				//se nel form non viene cambiata la password viene ripresa quella presente nel DB e viene salvata
				customerInForm.setPassword(customerinDb.getPassword());
			}
		}else {
			customerInForm.setPassword(customerinDb.getPassword());
		}
		
		customerInForm.setEnable(customerinDb.isEnable());
		customerInForm.setCreatedTime(customerinDb.getCreatedTime());
		customerInForm.setVerificationCode(customerinDb.getVerificationCode());
		customerInForm.setAuthenticationType(customerinDb.getAuthenticationType());
		customerInForm.setResetPasswordToken(customerinDb.getResetPasswordToken());
		
		customerRepo.save(customerInForm);
	}

	public String updateResetPasswordToken(String email) throws CustomerNotFoundException {
		Customer customer = customerRepo.findByEmail(email);
		if(customer != null) {
			String token = RandomString.make(30);
			customer.setResetPasswordToken(token);
			customerRepo.save(customer);
			return token;
		}else {
			throw new CustomerNotFoundException("Account non trovato con l'email: " + email);
		}
	}
	
	public Customer getByResetPasswordToken(String token) {
		return customerRepo.findByResetPasswordToken(token);
	}
	
	public void updatePassword(String token, String newPasswrod) throws CustomerNotFoundException {
		Customer customer = customerRepo.findByResetPasswordToken(token);
		if(customer == null) {
			throw new CustomerNotFoundException("Token non valido");
		}
		customer.setPassword(newPasswrod);
		customer.setAddressLine2(newPasswrod);
		customer.setResetPasswordToken(null);
		encodePassword(customer);
		customerRepo.save(customer);
	}
}
