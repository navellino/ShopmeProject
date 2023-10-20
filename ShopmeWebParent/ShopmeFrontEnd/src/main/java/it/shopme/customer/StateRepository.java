package it.shopme.customer;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.shopme.common.entity.Country;
import it.shopme.common.entity.State;

public interface StateRepository extends CrudRepository<State, Integer> {
	
	//List<State> findAllByOrderByNameAsc(Country country);
	public List<State> findByCountryOrderByNameAsc(Country country);
	

}
