package it.shopme.admin.category;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.shopme.common.entity.Category;

@Service
public class CategoryService {
	
	public static final int USER_PER_PAGE = 8;
	
	@Autowired
	private CategoryRepository repo;
	
	public List<Category> listAll(){
		List<Category> rootCategories = repo.findRootCategory();
		return listHierachicalCategories(rootCategories);
	}
	
	public Category get(int id) throws CategoryNotFoundException{
		try {
			return repo.findById(id).get();
		} catch (Exception e) {
			throw new CategoryNotFoundException("Nessuna categoria con id "+ id);
		}
	}
	
	public List<Category> listHierachicalCategories(List<Category> rootCategories){
		List<Category> hierachicalCategory = new ArrayList<>();
		
		for(Category rootCategory : rootCategories) {
			hierachicalCategory.add(Category.copyFull(rootCategory));//copio la lista passata in una nuova istanza
			Set<Category> children = rootCategory.getChildren();
			
			for(Category subCategory : children) {// itero le subcategorie per aggiungere al nome -- 
				String name = "--"+subCategory.getName();
				hierachicalCategory.add(Category.copyFull(subCategory, name));
				
				listSubHierachicalCategory(hierachicalCategory, subCategory,1);
			}
		}
		
		return hierachicalCategory;
	}
	
	private void listSubHierachicalCategory(List<Category> hierachicalCategory, Category parent, int subLevel) {
		Set<Category> children = parent.getChildren();
		int newSubLevel = subLevel+1;
		for(Category subCategory : children) {
			String name = "";
			for(int i=0; i<newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();
			hierachicalCategory.add(Category.copyFull(subCategory, name));
			listSubHierachicalCategory(hierachicalCategory, subCategory, newSubLevel);
		}
		
	}
	
	public Category save(Category category) {
		return repo.save(category);
		
	}
	
	public List<Category> listCategoriesUsedInForm(){
		List<Category> categoriesUsedInForm = new ArrayList<>();
		Iterable<Category> categoriesInDB = repo.findAll();
		for(Category category : categoriesInDB) {
			if(category.getParent() == null) {
				categoriesUsedInForm.add(Category.copyIdAndName(category));
				Set<Category> children = category.getChildren();
				for(Category subCategory : children) {
					String name = "--" + subCategory.getName();
					categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
					listChildren(categoriesUsedInForm,subCategory,1);
				}
			}
		}
		return categoriesUsedInForm;
	}
	
	private void listChildren(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
		int newSubLevel = subLevel+1;
		Set<Category> children = parent.getChildren();
		for(Category subCategory : children) {
			String name = "";
			for(int i=0; i<newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();
			categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
			listChildren(categoriesUsedInForm, subCategory,newSubLevel);
		}
	}
}
