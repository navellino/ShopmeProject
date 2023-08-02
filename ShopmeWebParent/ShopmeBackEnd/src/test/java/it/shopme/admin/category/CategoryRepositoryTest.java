package it.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import it.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTest {
	
	@Autowired
	private CategoryRepository repository;
	
	@Test
	public void testCreateRootCategory() {
		Category category = new Category("Electronics");
		Category saveCategory = repository.save(category);
		
		assertThat(saveCategory.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateSubCategory() {
		Category parent = new Category(7);
		Category laptops = new Category("iOS", parent);
		Category components = new Category("Android", parent);
		repository.saveAll(List.of(laptops, components));
	}
	
	@Test
	public void getCategory() {
		Category category = repository.findById(2).get();
		
		System.out.println(category.getName()+" "+category.getImage());
		
		Set<Category> subCtegory = category.getChildren();
		
		for(Category children : subCtegory) {
			System.out.println(children.getName());
		}
		assertThat(subCtegory.size()).isGreaterThan(0);
	}
	
	@Test
	public void testHierachicalCategory() {
		Iterable<Category> categories = repository.findAll();
		
		for(Category category : categories) {
			if(category.getParent() == null) {
				System.out.println(category.getName());
				Set<Category> children = category.getChildren();
				for(Category subCategory : children) {
					System.out.println("  "+subCategory.getName());
					printChildren(subCategory,1);
				}
			}
		}
	}
	
	private void printChildren(Category parent, int subLevel) {
		
		int newSubLevel = subLevel+1;
		
		Set<Category> children = parent.getChildren();
		
		for(Category subCategory : children) {
			
			for(int i=0; i<newSubLevel; i++) {
				System.out.print("  ");
			}
			
			System.out.println(subCategory.getName());
			printChildren(subCategory,newSubLevel);
		}
	}
	
	@Test
	public void testListRootCetgory() {
		List<Category> listRootCategories = repository.findRootCategory(Sort.by("name").ascending());
		listRootCategories.forEach(cat -> System.out.println(cat.getName()));
	}
	
	@Test
	public void testFindByName() {
		String name = "Cameras";
		Category category = repository.findByAlias(name);
		
		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo(name);
	}
}
