package it.shopme.admin.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.shopme.common.entity.Role;
import it.shopme.common.entity.User;

@Controller
public class UserController {
	
	@Autowired
	private UserService service;
	
	@GetMapping("/users")
	public String listAll(Model model) {
		
		List<User> users = new ArrayList<>();
		users = service.listAll();
		model.addAttribute("title", "User Management");
		model.addAttribute("users", users);
		return "users";
	}
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		List<Role> listRole = new ArrayList<>();
		listRole = service.roleList();
		
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("roles", listRole);
		
		return "user_form";
	}
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes) {
		service.saveUser(user);
		redirectAttributes.addFlashAttribute("message", "Utente salvato con successo!"); 
		return "redirect:/users";	
	}
	

}
