package it.shopme.admin.customer;

import java.util.List;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import it.shopme.admin.setting.country.CountryRepository;
import it.shopme.common.entity.Country;
import it.shopme.common.entity.Customer;
import it.shopme.common.exception.CustomerNotFoundException;


@Service
@Transactional
public class CustomerService {
	
	public static final int CUSTOMER_PER_PAGE = 10;
	
	@Autowired
	private CustomerRepository customerRepo;
	
	@Autowired
	private CountryRepository countryRepo;
	
	@Autowired
	private PasswordEncoder encoder;
	
	public Page<Customer> listByPage(int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum -1, CUSTOMER_PER_PAGE, sort);
		
		if(keyword != null) {
		 	return customerRepo.findAll(keyword, pageable);
		}
		
		return customerRepo.findAll(pageable);
	}
	
	public void updateCustomerStatus(Integer id, boolean enabled) {
		customerRepo.enableStatus(id, enabled);
	}
	
	public Customer get(Integer id) {
		return customerRepo.findById(id).get();
	}
	
	public List<Country> listAllCountry(){
		return countryRepo.findAllByOrderByNameAsc();
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		Customer customer = customerRepo.findByEmail(email);
		
		if(customer != null && customer.getId() != id) {
			//è stato trovato un altro cliente con la stessa email
			return false;
		}
		return true;
	}
	
	public void saveCustomer(Customer customerInForm) {
		
		Customer customerinDb = customerRepo.findById(customerInForm.getId()).get();
		
		if(!customerInForm.getPassword().isEmpty()) {
			//se nel form è stata cambiata la password questa viene codificata e salvata
			String encodePassword = encoder.encode(customerInForm.getPassword());
			customerInForm.setPassword(encodePassword);
		}else {
			//se nel form non viene cambiata la password viene ripresa quella presente nel DB e viene salvata

			customerInForm.setPassword(customerinDb.getPassword());
		}
		
		customerInForm.setEnable(customerinDb.isEnable());
		customerInForm.setCreatedTime(customerinDb.getCreatedTime());
		customerInForm.setVerificationCode(customerinDb.getVerificationCode());
		customerInForm.setAuthenticationType(customerinDb.getAuthenticationType());
		customerInForm.setResetPasswordToken(customerinDb.getResetPasswordToken());
		
		customerRepo.save(customerInForm);
	}
	
	public void deleteCustomer(Integer id) throws CustomerNotFoundException {
		Long count = customerRepo.countById(id);
		
		if(count == null || count == 0) {
			throw new CustomerNotFoundException("Cliente non ttrovate con id: "+ id);
		}
		
		customerRepo.deleteById(id);
	}
}
