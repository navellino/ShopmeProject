package it.shopme.admin.setting.country;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.shopme.common.entity.Country;

@RestController
public class CountryRestController {
	
	@Autowired
	private CountryRepository repo;
	
	@GetMapping("/countries/list")
	public List<Country> listAll(){
		return repo.findAllByOrderByNameAsc();
	}
	
	@PostMapping("/countries/save")
	public String save(@RequestBody Country country) {
		Country searchCountry = repo.findByName(country.getName());
		
		if(searchCountry != null) {
			return String.valueOf(0);
		}
		
		Country savedCountry = repo.save(country);
		return String.valueOf(savedCountry.getId());
	}
	
	@GetMapping("/countries/delete/{id}")
	public void deleteCountry(@PathVariable("id") Integer id) {
		repo.deleteById(id);
	}
}
