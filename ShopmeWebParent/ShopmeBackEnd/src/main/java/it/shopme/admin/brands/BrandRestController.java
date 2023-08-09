package it.shopme.admin.brands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import it.shopme.common.entity.Brand;
import it.shopme.common.entity.Category;

@RestController
public class BrandRestController {

	@Autowired
	private BrandsService service;
	
	@PostMapping("brands/check_unique")
	public String checkUnique(@Param("id") Integer id, @Param("name") String name) {
		return service.checkUnique(id, name);
	}
	
	@GetMapping("/brands/{id}/categories")
	public List<CategoryDTO> listCategoriesByBrand(@PathVariable(name = "id") Integer brandId){
		List<CategoryDTO> listCategories = new ArrayList<>();
		Brand brand = service.findBrand(brandId);
		Set<Category> categories = brand.getCategories();
		for(Category category : categories) {
			CategoryDTO dto = new CategoryDTO(category.getId(), category.getName());
			listCategories.add(dto);
		}
		return listCategories;
		
	}
}
