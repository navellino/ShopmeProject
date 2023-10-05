package it.shopme.setting;

import java.util.List;	
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.shopme.common.entity.SettingCategory;
import it.shopme.common.entity.Settings;

@Service
public class SettingService {
	
	@Autowired
	private SettingRepository repoSett;
	
	public List<Settings> getGeneralSettings() {
		
		return repoSett.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
	}
}
