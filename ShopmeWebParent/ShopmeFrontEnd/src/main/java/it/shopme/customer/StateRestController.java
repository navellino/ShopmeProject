package it.shopme.customer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import it.shopme.common.entity.Country;
import it.shopme.common.entity.State;
import it.shopme.common.entity.StateDTO;

@RestController
public class StateRestController {
	
	@Autowired private StateRepository repo;
	
	@GetMapping("/settings/list_by_country/{id}")
	public List<StateDTO> listByCountry(@PathVariable("id") Integer countryId){
		List<State> listStates = repo.findByCountryOrderByNameAsc(new Country(countryId));
		List<StateDTO> states = new ArrayList<>();
		
		for(State state : listStates) {
			states.add(new StateDTO(state.getId(), state.getName()));
		}
		return states;
	}
}
