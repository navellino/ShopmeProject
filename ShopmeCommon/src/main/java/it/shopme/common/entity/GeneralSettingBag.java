package it.shopme.common.entity;

import java.util.List;

public class GeneralSettingBag extends SettingsBag {

	public GeneralSettingBag(List<Settings> listSettings) {
		super(listSettings);
	}
	
	public void updateCurrencySymbol(String value) {
		super.update("CURRENCY_SYMBOL", value);
	}
	
	public void updateSiteLogo(String value) {
		super.update("SITE_LOGO", value);
	}
}
