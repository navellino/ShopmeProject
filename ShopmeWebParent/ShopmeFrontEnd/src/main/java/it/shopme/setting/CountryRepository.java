package it.shopme.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.shopme.common.entity.Country;

public interface CountryRepository extends CrudRepository<Country, Integer>{

	public List<Country> findAllByOrderByNameAsc();

	public Country findByName(String name);
}
