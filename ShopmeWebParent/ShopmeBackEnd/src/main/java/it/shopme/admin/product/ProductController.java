package it.shopme.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.shopme.admin.FileUploadUtil;
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
	public String saveProduct(Product product, RedirectAttributes ra, 
			@RequestParam("fileImage") MultipartFile mainImageMultipart,
			@RequestParam("extraFileImage") MultipartFile[] extraMultipartFile,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues
			) throws IOException{
		
		setMainImageName(mainImageMultipart, product);
		setExtraImageName(extraMultipartFile, product);
		setProductDetails(detailNames, detailValues, product);
		
		Product savedProduct = productService.save(product);
		
		saveUploadImage(mainImageMultipart, extraMultipartFile, savedProduct);
		
		ra.addFlashAttribute("message", "Prodotto salvato con successo.");
		
		return "redirect:/products";
	}
	
	private void setProductDetails(String[] detailNames, String[] detailValues, Product product) {
		if(detailNames == null || detailNames.length == 0) return;
		
		for(int count = 0; count < detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValues[count];
			
			if(!name.isEmpty() && !value.isEmpty()) {
				product.addDetails(name, value);
			}
		}
		
	}

	private void saveUploadImage(MultipartFile mainImageMultipart, MultipartFile[] extraMultipartFile,
			Product savedProduct) throws IOException {
		
		if(!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			String uploadDir = "../product-images/"+savedProduct.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
		}
		
		if(extraMultipartFile.length > 0) {	
			for(MultipartFile multipartFile: extraMultipartFile) {
				if(multipartFile.isEmpty()) continue;
				
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename()); 
				String uploadExtraDir = "../product-images/"+ savedProduct.getId() +"/extras";
			
				FileUploadUtil.saveFile(uploadExtraDir, fileName, multipartFile);
			}
		}		
	}

	private void setExtraImageName(MultipartFile[] extraMultipartFile, Product product) {
		if(extraMultipartFile.length>0) {
			for(MultipartFile multipartFile: extraMultipartFile) {
				if(!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					product.addExtraImage(fileName);
				}
			}
		}	
	}

	private void setMainImageName(MultipartFile mainImageMultipart, Product product) {
		if(!mainImageMultipart.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
			product.setMainImage(fileName);
		}
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
}
