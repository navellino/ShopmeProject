package it.shopme.admin.category;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import it.shopme.common.entity.Category;

@Service
@Transactional
public class CategoryService {
	public static final int ROOT_CATEGORIES_PER_PAGE = 2;
		
	@Autowired
	private CategoryRepository repo;
	
	public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword){
		Sort sort = Sort.by("name");
		
		switch (sortDir) {
		case "asc":
			sort = sort.ascending();
			break;
		case "desc":
			sort = sort.descending();
			break;
		default:
			sort = sort.ascending();
		}
		
		Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);
		
		Page<Category> pageCategories = null;
		
		if(keyword != null && !keyword.isEmpty()) {
			pageCategories = repo.search(keyword, pageable);
		} else {
			pageCategories = repo.findRootCategory(pageable);
		}
		
		List<Category> rootCategories = pageCategories.getContent();
		
		pageInfo.setTotalElements(pageCategories.getTotalElements());
		pageInfo.setTotalPages(pageCategories.getTotalPages());
		
		if(keyword != null && !keyword.isEmpty()) {
			List<Category>  searchResult = pageCategories.getContent();
			
			for(Category category : searchResult) {
				category.setHasChildren(category.getChildren().size()>0);
			}
			return searchResult;
		}else {
			return listHierachicalCategories(rootCategories, sortDir);
		}
	}
	
	public List<Category> listAll(String sortDir){
		Sort sort = Sort.by("name");
		/*
		if(sortDir == "asc") {
			sort = sort.ascending();
		} else if(sortDir == "desc") {
			sort = sort.descending();
		}*/
		switch (sortDir) {
		case "asc":
			sort = sort.ascending();
			break;
		case "desc":
			sort = sort.descending();
			break;
		default:
			sort = sort.ascending();
		}
		List<Category> rootCategories = repo.findRootCategory(sort);
		return listHierachicalCategories(rootCategories, sortDir);
	}
	
	public Category get(int id) throws CategoryNotFoundException{
		try {
			return repo.findById(id).get();
		} catch (Exception e) {
			throw new CategoryNotFoundException("Nessuna categoria con id "+ id);
		}
	}
	
	public List<Category> listHierachicalCategories(List<Category> rootCategories, String sortDir){
		List<Category> hierachicalCategory = new ArrayList<>();
		
		for(Category rootCategory : rootCategories) {
			hierachicalCategory.add(Category.copyFull(rootCategory));//copio la lista passata in una nuova istanza
			
			Set<Category> children = sortSubCategories(rootCategory.getChildren());
			
			for(Category subCategory : children) {// itero le subcategorie per aggiungere al nome -- 
				String name = "--"+subCategory.getName();
				hierachicalCategory.add(Category.copyFull(subCategory, name));
				
				listSubHierachicalCategory(hierachicalCategory, subCategory,1, sortDir);
			}
		}
		return hierachicalCategory;
	}
	
	private void listSubHierachicalCategory(List<Category> hierachicalCategory, Category parent, int subLevel, String sortDir) {
		Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
		int newSubLevel = subLevel+1;
		for(Category subCategory : children) {
			String name = "";
			for(int i=0; i<newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();
			hierachicalCategory.add(Category.copyFull(subCategory, name));
			listSubHierachicalCategory(hierachicalCategory, subCategory, newSubLevel, sortDir);
		}
	}
	
	public Category save(Category category) {
		Category parent = category.getParent();
		if(parent != null) {
			String allParentIds = parent.getAllParentIds() == null ? "-" : parent.getAllParentIds();
			allParentIds += String.valueOf(parent.getId())+"-";
			category.setAllParentIds(allParentIds);
		}
		return repo.save(category);	
	}
	
	public List<Category> listCategoriesUsedInForm(){
		List<Category> categoriesUsedInForm = new ArrayList<>();
		Iterable<Category> categoriesInDB = repo.findRootCategory(Sort.by("name").ascending());
		for(Category category : categoriesInDB) {
			if(category.getParent() == null) {
				categoriesUsedInForm.add(Category.copyIdAndName(category));
				Set<Category> children =  sortSubCategories(category.getChildren());
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
		Set<Category> children = sortSubCategories(parent.getChildren());
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
	
	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);
		
		Category findByName = repo.findByName(name);
		if(isCreatingNew) {
			if(findByName != null) {
				return "Nome duplicato";
			}else {
				Category findByAlias = repo.findByAlias(alias);
				if(findByAlias != null) {
					return "Alias duplicato";
				}
			}
		}else {
			if(findByName != null && findByName.getId() != id) {
				return "Nome duplicato";
			}
			Category findByAlias = repo.findByAlias(alias);
			if(findByAlias != null && findByAlias.getId() != id) {
				return "Alias duplicato";
			}
		}
		return "OK";
	}
	
	private SortedSet<Category> sortSubCategories(Set<Category> children){
		return sortSubCategories(children, "asc");
	}
	
	
	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir){
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {

			@Override
			public int compare(Category cat1, Category cat2) {
				if(sortDir.equals("asc")){
					return cat1.getName().compareTo(cat2.getName());
				} else {
					return cat2.getName().compareTo(cat1.getName());
				}	
			}
		});
		sortedChildren.addAll(children);
		
		return sortedChildren;
	}
	
	public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnableStatus(id, enabled);
	}
	
	public void deleteCategory(Integer id) throws CategoryNotFoundException{
		Long countById = repo.countById(id);
		if(countById == null || countById == 0) {
			throw new CategoryNotFoundException("La Categoria con id: "+id+" non esiste");
		}
		repo.deleteById(id);
	}
	
	public int countItem() {
		int i = repo.countByName();
		return i;
	}
}
