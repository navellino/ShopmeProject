package it.shopme.admin.product;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.shopme.common.entity.Product;

@Service
public class ProductService {

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
			String defalutAlias = product.getName().toLowerCase().replaceAll(" ", "_");
			product.setAlias(defalutAlias);
		}else {
			product.setAlias(product.getAlias().toLowerCase().replace(" ", "_"));
		}
		product.setUpdatedTime(new Date());
		
		return repo.save(product);
	}
}
