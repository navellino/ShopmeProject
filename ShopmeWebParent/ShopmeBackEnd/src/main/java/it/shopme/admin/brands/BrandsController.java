package it.shopme.admin.brands;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import it.shopme.common.entity.Category;


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
import it.shopme.admin.category.CategoryService;
import it.shopme.common.entity.Brand;


@Controller
public class BrandsController {

	@Autowired
	private BrandsService service;
	
	@Autowired
	private CategoryService catService;

	@GetMapping("/brands")
	public String listFirstPage(Model model) {
		/*List<Brand> listAllBrands = service.listAllBrands();
		model.addAttribute("brands", listAllBrands);*/
		return listByPage(1, model, "name", "asc", null);
	}
	
	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, 
			@Param("keyword") String keyword) {
		
		Page<Brand> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		List<Brand> listBrands = page.getContent();
		
		long startCount = (pageNum-1) * BrandsService.BRANDS_PER_PAGE + 1;
		long endCount = startCount + BrandsService.BRANDS_PER_PAGE-1;
		
		if(endCount>page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc"; 
		String curPaglink = "/brands/page/";
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("brands", listBrands);
		model.addAttribute("link", curPaglink);
		
		return "brands/brands";
	}
	
	@GetMapping("/brands/new")
	public String newBrands(Model model) {
		List<Category> listCategories = catService.listCategoriesUsedInForm();
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("brand", new Brand());
		model.addAttribute("newBrand", "new");
		return "brands/brand_form";
	}
	
	@PostMapping("/brands/save")
	public String saveBrand(Brand brand, @RequestParam("fileImage") MultipartFile multipartFile,
								RedirectAttributes redirectAttributes) throws IOException {
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(fileName);
			
			Brand savedBrand = service.save(brand);
			String uploadDir = "../brands-images/"+savedBrand.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			service.save(brand);
		}
		redirectAttributes.addFlashAttribute("message", "Marca salvata con successo!");
		
		return "redirect:/brands";
	}
	
	@GetMapping("/brands/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
			
		List<Category> listCategories = catService.listCategoriesUsedInForm();
			Brand brand = service.findBrand(id);
			
			model.addAttribute("brand", brand);
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("editBrand", "edit");
			
			return "brands/brand_form";	
	}
	
	@GetMapping("/brands/delete/{id}")
	public String deleteBrand(@PathVariable(name="id") Integer id, Model model, RedirectAttributes ra) {
		try {
			service.deleteBrand(id);
			String uploadDir = "../brands-images/"+id;
			FileUploadUtil.removeDir(uploadDir);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "redirect:/brands";
	}
	
	@GetMapping("/brands/export/csv")
	public void exportCsv(HttpServletResponse response) throws IOException{
		List<Brand> listBrand = service.listAllBrands();
		BrandsCsvExport export = new BrandsCsvExport();
		export.export(listBrand, response);
	}
	
}
