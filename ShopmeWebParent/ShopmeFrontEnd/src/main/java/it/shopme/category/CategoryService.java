package it.shopme.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.shopme.common.entity.Category;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repo;
	
	/*Ottengo una lista delle categorie che non ha children per avere tutte le categorie abilitate che contengono prodotti*/
	public List<Category> noChildreListCategories(){
		List<Category> noChildreCategories = new ArrayList<>();
		List<Category> listaCategories = repo.findAllEnabled();
		listaCategories.forEach(category ->{
			Set<Category> children = category.getChildren();
			if(children == null || children.size()== 0) {
				noChildreCategories.add(category);
			}
		});
		return noChildreCategories;
	}
	
	
	public Category getCategory(String alias) {
		return repo.findByAliasEnabled(alias);
	}
	
	/*Metodo per implementare il breadcrumb navigation andando a cercare tutti le sopracategorie delle categorie figlie*/
	public List<Category> getCategoryParent(Category child){
		List<Category> listParents = new ArrayList<>();
		
		Category parent = child.getParent();
		
		while(parent != null) {
			listParents.add(parent);
			parent = parent.getParent();
		}
		
		for(int low = 0, high = listParents.size() - 1; low < high; low++, high--) {
			Collections.swap(listParents, low, high);
		}
		
		listParents.add(child);
		
		return listParents;
	}
	
}
