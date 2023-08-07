package it.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import it.shopme.admin.brands.BrandsRepository;
import it.shopme.common.entity.Category;
import it.shopme.common.entity.Brand;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTest {
	
	@Autowired
	private BrandsRepository repository;
	
	@Test
	public void testCreateBrand1() {
		Category laptops = new Category(6);
		Brand acer = new Brand("Acer");
		acer.getCategories().add(laptops);
		
		Brand savedBrand = repository.save(acer);
		
		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateBrand2() {
		Category cellphones = new Category(13);
		Category tablets = new Category(23);
		Brand samsung = new Brand("Samsung");
		
		samsung.getCategories().add(cellphones);
		samsung.getCategories().add(tablets);
		
		Brand savedBrand = repository.save(samsung);
		
		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateBrand3() {
		Category memory = new Category(13);
		Category hardDisk = new Category(9);
		Brand kingstone = new Brand("KingStone");
		
		kingstone.getCategories().add(memory);
		kingstone.getCategories().add(hardDisk);
		
		Brand savedBrand = repository.save(kingstone);
		
		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void findAllTest() {
		Iterable<Brand> brands = repository.findAll();
		brands.forEach(System.out::println);
		assertThat(brands).isNotEmpty();
	}
	
	@Test
	public void testFindById() {
		Brand brand = repository.findById(1).get();
		assertThat(brand.getName()).isEqualTo("Acer");
	}
	
	@Test
	public void updateName() {
		String newName = "Samsung Electronics";
		Brand samsung = repository.findById(2).get();
		samsung.setName(newName);
		Brand newsavedBrand = repository.save(samsung);
		assertThat(newsavedBrand.getName()).isEqualTo(newName);	
	}
	
	@Test
	public void testDeleteBrand() {
		Integer id = 3;
		repository.deleteById(id);
		Optional<Brand> deleted = repository.findById(id);
		assertThat(deleted).isEmpty();
	}
}
