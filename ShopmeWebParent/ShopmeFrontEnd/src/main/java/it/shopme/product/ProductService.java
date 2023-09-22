package it.shopme.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import it.shopme.common.entity.Product;

@Service
public class ProductService {

	public static final int PRODUCT_PER_PAGE = 5;
	
	@Autowired
	private ProductRepository repo;
	
	public Page<Product> listByCategory(int pageNumber, Integer categoryID){
		String categoryMatch = "-"+String.valueOf(categoryID)+"-";
		Pageable pageable = PageRequest.of(pageNumber - 1, PRODUCT_PER_PAGE);
		return repo.listByCategory(categoryID, categoryMatch, pageable);
	}
	
	
}

