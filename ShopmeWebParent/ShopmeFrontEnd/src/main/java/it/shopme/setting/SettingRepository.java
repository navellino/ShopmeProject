package it.shopme.setting;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.shopme.common.entity.SettingCategory;
import it.shopme.common.entity.Settings;

public interface SettingRepository extends CrudRepository<Settings, Integer> {

	public List<Settings> findByCategory(SettingCategory category);
	
	@Query("SELECT s FROM Settings s WHERE s.category = ?1 OR s.category = ?2")
	public List<Settings> findByTwoCategories(SettingCategory catOne, SettingCategory catTwo);
}
