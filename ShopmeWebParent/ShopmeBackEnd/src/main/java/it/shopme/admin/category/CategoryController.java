package it.shopme.admin.category;

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
import it.shopme.common.entity.Category;

@Controller
public class CategoryController {

	@Autowired
	private CategoryService service;
	
	@GetMapping("/categories")
	public String listAll(Model model) {
		List<Category> listCategories = service.listAll();
		model.addAttribute("categories", listCategories);
		return "categories";
	}
	
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		
		List<Category> listCatgories = service.listCategoriesUsedInForm();
		model.addAttribute("listCategories", listCatgories);
		model.addAttribute("category", new Category());
		return "category_form";
	}
	
	@GetMapping("/categories/prova")
	public String newProva(Model model) {
		model.addAttribute("category", new Category());
		return "prova";
	}
	/* Old version prima di edit categories
	@PostMapping("/categories/save")
	public String saveCategory(Category category, 
								@RequestParam("fileImage") MultipartFile multipartFile,
								RedirectAttributes redirectAttributes) throws IOException {
		
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		category.setImage(fileName);
		Category savedCategory = service.save(category);
		String uploadDir = "../category-images/"+savedCategory.getId();
		FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		redirectAttributes.addFlashAttribute("message", "categoria salvata con successo!");
		
		return "redirect:/categories";
	}*/
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category, 
								@RequestParam("fileImage") MultipartFile multipartFile,
								RedirectAttributes redirectAttributes) throws IOException {
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);
			Category savedCategory = service.save(category);
			String uploadDir = "../category-images/"+savedCategory.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			service.save(category);
		}
		redirectAttributes.addFlashAttribute("message", "categoria salvata con successo!");
		
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			List<Category> listCategories = service.listCategoriesUsedInForm();
			Category category = service.get(id);
			model.addAttribute("category", category);
			model.addAttribute("listCategories", listCategories);
			return "category_form";
		} catch (CategoryNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/categories";
		}
		
	}
		
}
