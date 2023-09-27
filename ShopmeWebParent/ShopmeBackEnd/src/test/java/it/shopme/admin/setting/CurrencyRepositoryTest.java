package it.shopme.admin.setting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import it.shopme.common.entity.Currency;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CurrencyRepositoryTest {

	@Autowired
	private CurrencyRepository repo;
	
	@Test
	public void testCurrency() {
		List<Currency> currencyList = Arrays.asList( 
				new Currency("United States Dollar", "$", "USD"),
				new Currency("British Pound", "£", "GPB"),
				new Currency("Japanese Yen", "¥", "JPY"),
				new Currency("European Union", "€", "EUR"),
				new Currency("Russian Ruble", "₽", "RUB"),
				new Currency("South Corea Won", "₩", "KRW"),
				new Currency("Brazilian Real", "R$", "BRL"),
				new Currency("Chinese Yuan", "¥", "CNY"),
				new Currency("Australian Dollar", "$", "AUD"),
				new Currency("Canadian Dollar", "$", "CAD"),
				new Currency("Switzerland Franc", "F", "CHF"),
				new Currency("Indian Rupee", "₹", "INR"));
		
		repo.saveAll(currencyList);
		
		Iterable<Currency> list = repo.findAll();
		
		assertThat(list).size().isEqualTo(12);
	}
	
	@Test
	public void listAllOrderByName() {
		List<Currency> listOrder = repo.findAllByOrderByNameAsc();
		listOrder.forEach(System.out::println);
		
		assertThat(listOrder.size()).isGreaterThan(0);
	}
}
