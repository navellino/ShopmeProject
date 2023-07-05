package it.shopme.admin.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import it.shopme.common.entity.Role;
import it.shopme.common.entity.User;

@Controller
public class UserController {
	
	@Autowired
	private UserService service;
	
	@GetMapping("/users")
	public String listAll(Model model) { //list first page
		
		/*List<User> users = new ArrayList<>();
		users = service.listAll();
		model.addAttribute("title", "User Management");
		model.addAttribute("users", users);
		return "users";*/
		return listByPage(1, model, "lastName", "asc");
	}
	
	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir
			
			) 
	{
		Page<User> page = service.listByPage(pageNum, sortField, sortDir);
		List<User> listUsers = page.getContent();

		
		long startCount = (pageNum-1) * UserService.USER_PER_PAGE + 1;
		long endoCount = startCount + UserService.USER_PER_PAGE - 1;
		
		if(endoCount > page.getTotalElements()) {
			endoCount = page.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		model.addAttribute("lastPage", page.getTotalPages());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endoCount);		
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("users", listUsers);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		return "users";
	}
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		List<Role> listRole = new ArrayList<>();
		listRole = service.roleList();
		
		User user = new User();
		model.addAttribute("title", "ShopMe - Nuovo Utente");
		model.addAttribute("user", user);
		model.addAttribute("roles", listRole);
		
		return "user_form";
	}
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = service.saveUser(user);
			String uploadDir = "user-photos/" + savedUser.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if(user.getPhotos().isEmpty()) user.setPhotos(null);
			service.saveUser(user);
		}
		redirectAttributes.addFlashAttribute("message", "Utente salvato con successo!"); 
		return "redirect:/users";	
	}
	
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			User user = service.getUserById(id);
			List<Role> listRole = service.roleList();
			model.addAttribute("title", "ShopMe - Modifica Utente ("+ user.getFirstName() + " " + user.getLastName() + ")");
			model.addAttribute("user", user);
			model.addAttribute("roles", listRole);
			return "user_form";
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage()); 
			return "redirect:/users";	
		}
	}
	@GetMapping("/users/{id}/enabled/{status}")
	public String enableDisable(
			@PathVariable(name = "id") Integer id,
			@PathVariable("status") boolean enabled,
			Model model,
			RedirectAttributes redirectAttributes)
	{
		service.updateUserEnabledStatus(id, enabled);
		String status = enabled ? "abilitato" : "disabilitato";
		String message = "L'utente è stato " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/users";
	}
	
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "L'utente è stato eliminato correttamente"); 
			return "redirect:/users";
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage()); 
			return "redirect:/users";	
		}
	}
}
