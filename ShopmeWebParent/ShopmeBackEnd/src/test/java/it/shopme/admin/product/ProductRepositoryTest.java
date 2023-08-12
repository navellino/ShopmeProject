package it.shopme.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import it.shopme.common.entity.Brand;
import it.shopme.common.entity.Category;
import it.shopme.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTest {
	
	@Autowired
	private ProductRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct() {
		Brand brand = entityManager.find(Brand.class, 24);
		Category category = entityManager.find(Category.class, 29);
		
		Product product = new Product();
		product.setName("Il nuovo Java");
		product.setAlias("il_nuovo_java");
		product.setShortDescription("Guida completa alla programmazione moderna");
		product.setFullDescription("Una guida completa e aggiornata alla versione 17 LTS con tutto quello che serve per imparare a programmare in Java nel nuovo decennio.");
		
		product.setBrand(brand);
		product.setCategory(category);
		
		product.setPrice(52);
		product.setCost(35);
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		product.setEnabled(true);
		product.setInStock(true);
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
	} 
	
	@Test
	public void testListAllProduct() {
		Iterable<Product> iterableProduct = repo.findAll();
		iterableProduct.forEach(System.out :: println);
	}
	
	@Test
	public void findProduct() {
		Integer id = 2;
		Product product = repo.findById(id).get();
		System.out.println(product);
		assertThat(product).isNotNull();
	}
	
	@Test
	public void testUpdateProduct() {
		Integer id = 2;
		Product product = repo.findById(id).get();
		product.setEnabled(true);
		product.setInStock(true);
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testDeleteProduct() {
		Integer id = 3;
		repo.deleteById(id);
		Optional<Product> deletedProduct = repo.findById(id);
		assertThat(!deletedProduct.isPresent());
		
	}
	@Test
	public void replaceAll() {
		String REGEX = "[\\s-]";
		String testo = "Redmi-sNote 12 Pro+ 5G";
		String nuovaString = testo.replaceAll(REGEX, "_");
		System.out.println(nuovaString);
	}
}
