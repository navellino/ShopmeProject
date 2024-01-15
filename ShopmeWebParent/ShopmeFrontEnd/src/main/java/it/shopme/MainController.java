package it.shopme;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import it.shopme.category.CategoryService;
import it.shopme.common.entity.Category;

@Controller
public class MainController {
	
	@Autowired
	private CategoryService categoryService;

	@GetMapping("")
	public String getPage(Model model) {
		List<Category> listCategories = categoryService.noChildreListCategories();
		
		model.addAttribute("listCategories", listCategories);
		return "index";
	}
	
	@GetMapping("/login")
	public String viewLoginPage() {
		Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "login";
		}
		
		return "redirect:/";
	}
	
}
