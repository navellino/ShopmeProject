package it.shopme.common.entity;

import java.util.List;

public class SettingsBag {
	private List<Settings> listSettings;

	public SettingsBag(List<Settings> listSettings) {
		this.listSettings = listSettings;
	}
	
	public Settings get(String key) {
		int index = listSettings.indexOf(new Settings(key));
		
		if(index >= 0) {
			return listSettings.get(index);
		}
		
		return null;
	}
	
	public String getValue(String key) {
		Settings setting = get(key);
		if(setting != null) {
			return setting.getValue();
		}
		return null;
	}
	
	public void update(String key, String value) {
		Settings setting = get(key);
		if(setting != null && value != null) {
			setting.setValue(value);
		}
	}
	
	public List<Settings> list(){
		return listSettings;
	}
}
