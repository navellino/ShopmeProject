package it.shopme.customer;

import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import it.shopme.common.entity.Country;
import it.shopme.common.entity.Customer;
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
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		customerRepo.save(customer);
	}

	private void encodePassword(Customer customer) {
		String encodedPassword =  passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);
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
}
