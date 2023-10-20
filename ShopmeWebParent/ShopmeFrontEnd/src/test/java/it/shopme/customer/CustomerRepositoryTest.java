package it.shopme.customer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import it.shopme.common.entity.Country;
import it.shopme.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTest {

	@Autowired
	private CustomerRepository repo;
	
	@Autowired
	private TestEntityManager manager;
	
	
	@Test
	public void testCreateCustomer() {
		Integer countryId = 111;
		Country country = manager.find(Country.class, countryId);
		
		Customer customer = new Customer();
		customer.setCountry(country);
		customer.setFirstName("Nicola");
		customer.setLastName("Avellino");
		customer.setCity("Catania");
		customer.setState("Sicilia");
		customer.setAddressLine1("via della Repubblica 19");
		customer.setEmail("navellino@gmail.com");
		customer.setPhoneNumber("3490610111");
		customer.setPassword("nicola1234");
		customer.setPostalCode("95032");
		
		Customer savedCustomer = repo.save(customer);
		
		assertThat(savedCustomer).isNotNull();
		assertThat(savedCustomer.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateCustomer2() {
		Integer countryId = 76;
		Country country = manager.find(Country.class, countryId);
		
		Customer customer = new Customer();
		customer.setCountry(country);
		customer.setFirstName("Alain");
		customer.setLastName("Cammal");
		customer.setCity("Marsiglia");
		customer.setState("Hauts-de-France");
		customer.setAddressLine1("via della Republique 21");
		customer.setEmail("cammalalain@gmail.com");
		customer.setPhoneNumber("3480610111");
		customer.setPassword("alain1234");
		customer.setPostalCode("009933");
		
		Customer savedCustomer = repo.save(customer);
		
		assertThat(savedCustomer).isNotNull();
		assertThat(savedCustomer.getId()).isGreaterThan(0);
	}
}
