package it.shopme.admin.setting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import it.shopme.common.entity.SettingCategory;
import it.shopme.common.entity.Settings;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SettingRepositoryTest {
	
	@Autowired
	private SettingRepository repo;
	
	@Test
	public void testCreateGeneralSettings() {
		//Settings siteName = new Settings("SITE_NAME", "ShopMe", SettingCategory.GENERAL);
		Settings siteLogo = new Settings("SITE_LOGO", "ShopMe.png", SettingCategory.GENERAL);
		Settings copyright = new Settings("COPIRYGHT", "Copyright (c) 2023 ShopMe Ltd.", SettingCategory.GENERAL);
		
		repo.saveAll(List.of(siteLogo, copyright));
		
		Iterable<Settings> iterable = repo.findAll();
		
		assertThat(iterable).size().isGreaterThan(0);
		
	}
	
	@Test
	public void testCreateCurrencySettings() {
		Settings currencyId = new Settings("CURRENCY_ID", "1", SettingCategory.CURRENCY);
		Settings symbol = new Settings("CURRENCY_SYMBOL", "€", SettingCategory.CURRENCY);
		Settings symbolPosition = new Settings("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
		Settings decimalPointType = new Settings("DECIMAL_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);
		Settings decimalDigits = new Settings("DECIMAL_DICITS", "2", SettingCategory.CURRENCY);
		Settings thousandPointType = new Settings("THOUSAND_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
		
		Iterable<Settings> iterable = repo.saveAll(List.of(currencyId,symbol,symbolPosition,decimalPointType,decimalDigits,thousandPointType));
		
		assertThat(iterable).size().isGreaterThan(0);
	}
	
	@Test
	public void listSettingByCategory() {
		List<Settings> settings = repo.findByCategory(SettingCategory.GENERAL);
		settings.forEach(System.out::println);
		
	}
}
