package it.shopme.admin.product;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import it.shopme.common.entity.Product;

@Service
@Transactional
public class ProductService {

	public static final int PRODUCT_PER_PAGE = 5;
	
	String REGEX = "[\\s-]"; 
	
	@Autowired
	private ProductRepository repo;
	
	public List<Product> listAll(){
		return (List<Product>) repo.findAll();
	}
	
	public Product save(Product product) {
		if(product.getId()==null) {
			product.setCreatedTime(new Date());
		}
		if(product.getAlias() == null || product.getAlias().isEmpty()) {
			String defalutAlias = product.getName().toLowerCase().replaceAll(REGEX, "_");
			product.setAlias(defalutAlias);
		}else {
			product.setAlias(product.getAlias().toLowerCase().replaceAll(REGEX, "_"));
		}
		product.setUpdatedTime(new Date());
		
		return repo.save(product);
	}
	public void saveProductPrice(Product productInForm) {
		Product productInDB = repo.findById(productInForm.getId()).get();
		
		productInDB.setPrice(productInForm.getPrice());
		productInDB.setCost(productInForm.getCost());
		productInDB.setDiscountPercent(productInForm.getDiscountPercent());
		
		repo.save(productInDB);
		
	}
	
	public Page<Product> listByPage(int pageNum, String sortField, String sortDir, String keyword, Integer categoryId){
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum-1, PRODUCT_PER_PAGE,sort);
		
		if(keyword != null && !keyword.isEmpty()) {
			if(categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-"+String.valueOf(categoryId)+"-";
				return repo.searchInCategory(categoryId, categoryIdMatch,keyword ,pageable);
			}
			return repo.findAll(keyword, pageable);
		}
		
		if(categoryId != null && categoryId > 0) {
			String categoryIdMatch = "-"+String.valueOf(categoryId)+"-";
			return repo.findByCategory(categoryId, categoryIdMatch, pageable);
		}
		
		return repo.findAll(pageable);
	}
	
	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		
			Product productByName = repo.findByName(name);
		
		if(isCreatingNew) {
			if(productByName != null) return "Duplicato";
		}else {
			if(productByName != null && productByName.getId() != id) return "Duplicato";
		}
		
		return "OK";
	}
	
	public void updateProductEnableStatus(Integer id, boolean enabled) {
		repo.updateEnableStatus(id, enabled);
	}
	
	public Product get(Integer id) {
		return repo.findById(id).get();
	}

	public void deleteProduct(Integer id) {
		repo.deleteById(id);
	}
	
}
