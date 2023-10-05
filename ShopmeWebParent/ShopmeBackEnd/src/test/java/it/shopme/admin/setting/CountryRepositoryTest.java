package it.shopme.admin.setting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import it.shopme.admin.setting.country.CountryRepository;
import it.shopme.common.entity.Country;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CountryRepositoryTest {
	@Autowired CountryRepository repo;
	
	@Test
	public void createCountry() {
		Country country = repo.save(new Country("Italy", "IT"));
		assertThat(country).isNotNull();
		assertThat(country.getId()).isGreaterThan(0);
	}
	
	@Test
	public void listCountry() {
		List<Country> countries = (List<Country>) repo.findAll();
		countries.forEach(System.out::println);
		
		assertThat(countries.size()).isGreaterThan(0);
	}
	
	@Test
	public void updateCountry() {
		Integer id = 2;
		Country country = repo.findById(id).get();
		country.setCode("US");
		repo.save(country);
		assertThat(country).isNotNull();
	}
	
	@Test
	public void deleteCountry() {
		Integer id = 2;
		Country country = repo.findById(id).get();
		repo.delete(country);
		assertThat(country).isNotNull();
	}
}
