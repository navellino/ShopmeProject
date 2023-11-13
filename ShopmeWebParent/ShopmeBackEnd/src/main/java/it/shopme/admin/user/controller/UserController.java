package it.shopme.admin.user.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import it.shopme.admin.paging.PagingAndSortingHelper;
import it.shopme.admin.paging.PagingAndSortingParam;
import it.shopme.admin.user.UserNotFoundException;
import it.shopme.admin.user.UserService;
import it.shopme.admin.user.export.UserCsvExporter;
import it.shopme.admin.user.export.UserExcelExporter;
import it.shopme.admin.user.export.UserPdfExporter;
import it.shopme.common.entity.Role;
import it.shopme.common.entity.User;

@Controller
public class UserController {
	
	@Autowired
	private UserService service;
	
	@GetMapping("/users")
	public String listAll() { 
		return "redirect:/users/page/1?sortField=lastName&sortDir=asc";
	}
	
	@GetMapping("/users/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "users", moduleURL = "/users") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum, Model model) 
	{

		service.listByPage(pageNum, helper);
		
		String curPaglink = "/users/page/";
		model.addAttribute("link", curPaglink);
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
		
		return getRedirectUrlafterUser(user);
	}

	private String getRedirectUrlafterUser(User user) {
		String firstPartOfEmail = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=lastName&sortDir=asc&keyword="+firstPartOfEmail;
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
	
	@GetMapping("/users/export/csv")
	public void exportToCsv(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserCsvExporter exporter = new UserCsvExporter();
		exporter.export(listUsers, response);
	}
	
	@GetMapping("/users/export/excel")
	public void exportToXls(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		
		UserExcelExporter exporter = new UserExcelExporter();
		exporter.export(listUsers, response);
	}
	
	@GetMapping("/users/export/pdf")
	public void exportToPdf(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		
		UserPdfExporter exporter = new UserPdfExporter();
		exporter.export(listUsers, response);
	}
}
