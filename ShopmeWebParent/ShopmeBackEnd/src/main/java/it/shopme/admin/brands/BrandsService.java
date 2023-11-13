package it.shopme.admin.brands;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import it.shopme.admin.paging.PagingAndSortingHelper;
import it.shopme.common.entity.Brand;

@Service
public class BrandsService {
	
	public static final int BRANDS_PER_PAGE = 10;
	
	@Autowired
	private BrandsRepository repository;
	
	public List<Brand> listAllBrands(){
		return (List<Brand>) repository.findAll();		
	}
	
	public void listByPage(int pageNum, PagingAndSortingHelper helper){
		Sort sort = Sort.by(helper.getSortField());
		
		sort = helper.getSortDir().equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum-1, BRANDS_PER_PAGE,sort);
		
		Page<Brand> page = null;
		
		if(helper.getKeyword() != null) {
			page = repository.findAll(helper.getKeyword(), pageable);
		} else {
			page = repository.findAll(pageable);
		}
		
		helper.updateModelAttribute(pageNum, page);
	}
	
	public Brand save(Brand brand) {
		return repository.save(brand);
	}
	
	public Brand findBrand(Integer id) {
		return repository.findById(id).get();
	}
	
	public void deleteBrand(Integer id) throws BrandNotFoundException{
		Long countById = repository.countById(id);
		if(countById == null || countById == 0) {
			throw new BrandNotFoundException("La Categoria con id: "+id+" non esiste");
		}
		repository.deleteById(id);
	}
	
	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		
		Brand brandByName = repository.findByName(name);
		
		if(isCreatingNew) {
			if(brandByName != null) return "Duplicato";
		}else {
			if(brandByName != null && brandByName.getId() != id) return "Duplicato";
		}
		
		return "OK";
	}
}
