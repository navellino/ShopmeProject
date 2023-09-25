package it.shopme.admin.category;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
import it.shopme.admin.category.export.CategoryCsvExport;
import it.shopme.common.entity.Category;
import it.shopme.common.exception.CategoryNotFoundException;

@Controller
public class CategoryController {

	@Autowired
	private CategoryService service;
	
	@GetMapping("/categories")
	public String listFirstPage(@Param("sortDir") String sortDir, Model model) {
		return listByPage(1, model, sortDir,null);
	}

	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
							@Param("sortDir") String sortDir,
							@Param("keyword") String keyword) {
		if(sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}
		
		CategoryPageInfo pageInfo = new CategoryPageInfo();
		
		long startCount = (pageNum-1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
		long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE- 1;
		

		
		List<Category> listCategories = service.listByPage(pageInfo,pageNum, sortDir,keyword);
		
		int totalItem = service.countItem();
		
		String reversSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		String curPaglink = "/categories/page/";
		
		model.addAttribute("totalPages", pageInfo.getTotalPages());
		model.addAttribute("totalItems", pageInfo.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("endCount", endCount);
		model.addAttribute("sortField", "name");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("startCount", startCount);
		model.addAttribute("categories", listCategories);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", reversSortDir);
		model.addAttribute("items", totalItem);
		model.addAttribute("link", curPaglink);
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
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public String enableDisable(
			@PathVariable(name="id") Integer id, @PathVariable(name = "status") boolean enabled, RedirectAttributes ra
			) throws CategoryNotFoundException {
		
		Category category = service.get(id);
		service.updateCategoryEnabledStatus(id, enabled);
		String status = enabled ? "abilitata" : "disabilitata";
		String message = "Categoria " +category.getName()+" "+ status;
		ra.addFlashAttribute("message", message);
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable(name="id") Integer id, Model model, RedirectAttributes ra) {
		try {
			service.deleteCategory(id);
			String categoryDir = "../category-images/"+id;
			FileUploadUtil.removeDir(categoryDir);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/export/csv")
	public void exportCsv(HttpServletResponse response) throws IOException{
		List<Category> listCategories = service.listCategoriesUsedInForm();
		CategoryCsvExport export = new CategoryCsvExport();
		export.export(listCategories, response);
	}
}
