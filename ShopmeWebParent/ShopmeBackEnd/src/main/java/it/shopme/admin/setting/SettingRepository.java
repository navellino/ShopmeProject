package it.shopme.admin.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.shopme.common.entity.SettingCategory;
import it.shopme.common.entity.Settings;

public interface SettingRepository extends CrudRepository<Settings, Integer> {

	public List<Settings> findByCategory(SettingCategory category);
}
