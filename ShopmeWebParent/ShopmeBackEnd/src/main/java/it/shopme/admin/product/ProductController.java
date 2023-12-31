package it.shopme.admin.product;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import it.shopme.admin.FileUploadUtil;
import it.shopme.admin.brands.BrandsService;
import it.shopme.admin.category.CategoryService;
import it.shopme.admin.security.ShopmeUserDetails;
import it.shopme.common.entity.Brand;
import it.shopme.common.entity.Category;
import it.shopme.common.entity.Product;
import it.shopme.common.exception.CategoryNotFoundException;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private BrandsService brandService;
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/products")
	public String listFirstPage(Model model) {
		return listByPage(1, model, "name", "asc", null,0);
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, 
			@Param("keyword") String keyword, @Param("categoryId") Integer categoryId) {
		
		Page<Product> page = productService.listByPage(pageNum, sortField, sortDir, keyword, categoryId);
		List<Product> listProducts = page.getContent();
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		long startCount = (pageNum-1) * ProductService.PRODUCT_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCT_PER_PAGE-1;
		
		if(endCount>page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc"; 
		String curPaglink = "/products/page/";
		
		if(categoryId != null) model.addAttribute("categoryId", categoryId);
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("link", curPaglink);
		model.addAttribute("listCategories", listCategories);
		
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
		model.addAttribute("numberOfExtraImages", 0);
		
		return "products/product_form";
	}
	
	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes ra, 
			@RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart,
			@RequestParam(value = "extraFileImage", required = false) MultipartFile[] extraMultipartFile,
			@RequestParam(name = "detailIds", required = false) String[] detailIds,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "imageIds", required = false) String[] imageIds,
			@RequestParam(name = "imageNames", required = false) String[] imageNames,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser
			) throws IOException{
		
		if(loggedUser.hasRole("Commerciale")) {
			productService.saveProductPrice(product);
			ra.addFlashAttribute("message", "Prodotto salvato con successo da Commerciale.");
			return "redirect:/products";
		}
		
		ProductSaveHelper.setMainImageName(mainImageMultipart, product);
		ProductSaveHelper.setExistingExtraImageName(imageIds, imageNames, product);
		ProductSaveHelper.setNewExtraImageName(extraMultipartFile, product);
		ProductSaveHelper.setProductDetails(detailIds, detailNames, detailValues, product);
		
		Product savedProduct = productService.save(product);
		
		ProductSaveHelper.saveUploadImage(mainImageMultipart, extraMultipartFile, savedProduct);
		
		ProductSaveHelper.deleteExtraImageWeredDeletedOnForm(product);
		
		ra.addFlashAttribute("message", "Prodotto salvato con successo da Admin.");
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
			String productExtraImageDir = "../product-images/"+ id +"/extras";
			String productImageDir = "../product-images/"+ id;
			FileUploadUtil.removeDir(productExtraImageDir);
			FileUploadUtil.removeDir(productImageDir);
			ra.addFlashAttribute("message", "Prodotto eliminato correttamente");
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "redirect:/products";
	}
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable("id") Integer id, Model model) {
		Product product = productService.get(id);
		List<Brand> listBrands = brandService.listAllBrands();
		
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("newProduct", "edit");
		Integer numberOfExistingExtraImage = product.getImages().size();
		model.addAttribute("numberOfExtraImages", numberOfExistingExtraImage);
		
		return "products/product_form";
	}
	
	@GetMapping("/products/detail/{id}")
	public String viewProductDetail(@PathVariable("id") Integer id, Model model) {
		Product product = productService.get(id);
		
		model.addAttribute("product", product);
				
		return "products/product_detail_modal";
	}
}
