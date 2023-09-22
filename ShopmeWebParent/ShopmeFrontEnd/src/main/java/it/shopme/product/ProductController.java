package it.shopme.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import it.shopme.category.CategoryService;
import it.shopme.common.entity.Category;
import it.shopme.common.entity.Product;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable("category_alias") String categoryAlias, Model model) {
		return viewCategoryByPage(categoryAlias, model, 1);
	}
	
	@GetMapping("/c/{category_alias}/page/pageNum")
	public String viewCategoryByPage(@PathVariable("category_alias") String categoryAlias, Model model,
			@PathVariable(name = "pageNum") int pageNum) {
		Category category = categoryService.getCategory(categoryAlias);
		
		if(category == null) return "error/404";
		
		List<Category> listCategoryParents = categoryService.getCategoryParent(category);
		
		Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());
		List<Product> listProducts = pageProducts.getContent();
		
		long startCount = (pageNum-1) * ProductService.PRODUCT_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCT_PER_PAGE-1;
		
		if(endCount>pageProducts.getTotalElements()) {
			endCount = pageProducts.getTotalElements();
		}
		
		//String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc"; 
		//String curPaglink = "/products/page/";
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageProducts.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageProducts.getTotalElements());
		model.addAttribute("listCategoryParents", listCategoryParents);	
		model.addAttribute("listProducts",listProducts);
		model.addAttribute("category", category);
		
		
		return "product_by_category";
	}
	
}
