package it.shopme.admin.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import it.shopme.admin.brands.BrandsService;
import it.shopme.admin.category.CategoryNotFoundException;
import it.shopme.common.entity.Brand;
import it.shopme.common.entity.Product;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private BrandsService brandService;
	
	
	@GetMapping("/products")
	public String listAll(Model model) {
		List<Product> listProducts = productService.listAll();
		model.addAttribute("listProducts", listProducts);
		
		return "products/products";
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = brandService.listAllBrands();
		
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("newProduct", "new");
		
		return "products/product_form";
	}
	
	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes ra) {
		productService.save(product);
		ra.addFlashAttribute("message", "Prodotto salvato con successo.");
		
		return "redirect:/products";
	}
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String enableDisable(
			@PathVariable(name="id") Integer id, @PathVariable(name = "status") boolean enabled, RedirectAttributes ra
			) throws CategoryNotFoundException {
		
		Product product = productService.get(id);
		productService.updateProductEnableStatus(id, enabled);
		String status = enabled ? "abilitata" : "disabilitata";
		String message = "Prodotto " +product.getName()+" "+ status;
		ra.addFlashAttribute("message", message);
		return "redirect:/products";
	}
	
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name="id") Integer id, Model model, RedirectAttributes ra) {
		try {
			productService.deleteProduct(id);
			//String categoryDir = "../category-images/"+id;
			//FileUploadUtil.removeDir(categoryDir);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "redirect:/products";
	}
}
