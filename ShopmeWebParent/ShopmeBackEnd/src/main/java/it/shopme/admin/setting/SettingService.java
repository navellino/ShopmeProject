package it.shopme.admin.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.shopme.common.entity.GeneralSettingBag;
import it.shopme.common.entity.SettingCategory;
import it.shopme.common.entity.Settings;

@Service
public class SettingService {
	
	@Autowired
	private SettingRepository repoSett;
	
	public List<Settings> listAllSettings(){
		return (List<Settings>) repoSett.findAll();
	}
	
	public GeneralSettingBag getGeneralSettings() {
		List<Settings> settings = new ArrayList<>();
		
		List<Settings> generalSettings = repoSett.findByCategory(SettingCategory.GENERAL);
		List<Settings> currencySettings = repoSett.findByCategory(SettingCategory.CURRENCY);
		
		settings.addAll(generalSettings);
		settings.addAll(currencySettings);
		return new GeneralSettingBag(settings);
	}
	
	public void saveAll(Iterable<Settings> settings) {
		repoSett.saveAll(settings);
	}
}
