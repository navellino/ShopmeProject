package it.shopme.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
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
import it.shopme.common.entity.ProductImage;

@Controller
public class ProductController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private BrandsService brandService;
	
	/*
	@GetMapping("/products")
	public String listAll(Model model) {
		List<Product> listProducts = productService.listAll();
		model.addAttribute("listProducts", listProducts);
		
		return "products/products";
	}
	*/
	@GetMapping("/products")
	public String listFirstPage(Model model) {
		return listByPage(1, model, "name", "asc", null);
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, 
			@Param("keyword") String keyword) {
		
		Page<Product> page = productService.listByPage(pageNum, sortField, sortDir, keyword);
		List<Product> listProducts = page.getContent();
		
		long startCount = (pageNum-1) * ProductService.PRODUCT_PER_PAGE + 1;
		long endCount = startCount + ProductService.PRODUCT_PER_PAGE-1;
		
		if(endCount>page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc"; 
		String curPaglink = "/products/page/";
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
			@RequestParam(name = "detailIds", required = false) String[] detailIds,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "imageIds", required = false) String[] imageIds,
			@RequestParam(name = "imageNames", required = false) String[] imageNames
			) throws IOException{
		
		setMainImageName(mainImageMultipart, product);
		setExistingExtraImageName(imageIds, imageNames, product);
		setNewExtraImageName(extraMultipartFile, product);
		setProductDetails(detailIds, detailNames, detailValues, product);
		
		Product savedProduct = productService.save(product);
		
		saveUploadImage(mainImageMultipart, extraMultipartFile, savedProduct);
		
		deleteExtraImageWeredDeletedOnForm(product);
		
		ra.addFlashAttribute("message", "Prodotto salvato con successo.");
		
		return "redirect:/products";
	}
	
	private void deleteExtraImageWeredDeletedOnForm(Product product) {
		
		String extraImageDir = "../product-images/"+product.getId()+"/extras";
		Path dirPath = Paths.get(extraImageDir);
		
		try {
			Files.list(dirPath).forEach(file -> {
				String filename = file.toFile().getName();
				if(!product.containsImageName(filename)) {
					try {
						Files.delete(file);
					}catch (IOException ex) {
						System.out.println("Impossibile cancellare il file "+filename);
					}
				}
			});
		} catch (IOException ex) {
			System.out.println("Impossibile list la directory "+dirPath);
		}
		
	}

	private void setExistingExtraImageName(String[] imageIds, String[] imageNames, Product product) {
		if(imageIds == null || imageIds.length == 0) return;
		
		Set<ProductImage> images = new HashSet<>();
		
		for(int count = 0; count < imageIds.length; count++) {
			
			Integer id = Integer.parseInt(imageIds[count]);
			String name = imageNames[count];
			
			images.add(new ProductImage(id, name, product));
		}
		
		product.setImages(images);
	}

	private void setProductDetails(String[] detailId, String[] detailNames, String[] detailValues, Product product) {
		
		if(detailNames == null || detailNames.length == 0) return;
		
		for(int count = 0; count < detailNames.length; count++) {
			String name = detailNames[count];
			String value = detailValues[count];
			Integer id = Integer.parseInt(detailId[count]);
			
			if(id != 0) {
				product.addDetails(id, name, value);
			} else if(!name.isEmpty() && !value.isEmpty()) {
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

	private void setNewExtraImageName(MultipartFile[] extraMultipartFile, Product product) {
		if(extraMultipartFile.length>0) {
			for(MultipartFile multipartFile: extraMultipartFile) {
				if(!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					if(!product.containsImageName(fileName)) {
						product.addExtraImage(fileName);
					}
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
	
	@GetMapping("/products/detail/{id}")
	public String viewProductDetail(@PathVariable("id") Integer id, Model model) {
		Product product = productService.get(id);
		
		model.addAttribute("product", product);
				
		return "products/product_detail_modal";
	}
}
