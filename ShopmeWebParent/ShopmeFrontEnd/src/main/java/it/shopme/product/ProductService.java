package it.shopme.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import it.shopme.common.entity.Product;
import it.shopme.common.exception.ProductNotFoundException;

@Service
public class ProductService {

	public static final int PRODUCT_PER_PAGE = 10;
	public static final int SEARCH_RESUL_PER_PAGE = 10;
	
	@Autowired
	private ProductRepository repo;
	
	public Page<Product> listByCategory(int pageNum, Integer categoryID){
		String categoryIdMatch = "-" + String.valueOf(categoryID)+"-";
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCT_PER_PAGE);
		
		return repo.listByCategory(categoryID, categoryIdMatch, pageable);
	}
	
	public Product getProductByAlias(String alias) throws ProductNotFoundException {
		Product product = repo.findByAlias(alias);
		if(product == null) {
			throw new ProductNotFoundException("Nessun prodotto con alias "+alias);			
		}
		return product;
	}
	
	public Page<Product> search(String keyword, int pageNum){
		Pageable pageable = PageRequest.of(pageNum-1, SEARCH_RESUL_PER_PAGE);
		return repo.search(keyword, pageable);
	}
}

