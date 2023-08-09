package it.shopme.admin.product;

import org.springframework.data.repository.PagingAndSortingRepository;

import it.shopme.common.entity.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer>{

}
