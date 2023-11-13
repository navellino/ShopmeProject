package it.shopme.admin.customer;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import it.shopme.common.entity.Country;
import it.shopme.common.entity.Customer;
import it.shopme.common.exception.CustomerNotFoundException;

@Controller
public class CustomerController {
	@Autowired
	private CustomerService service;
	
	@GetMapping("/customers")
	public String listFirstPage(Model model) {
		return listByPage(model, 1, "firstName", "asc", null);
	}
	
	@GetMapping("/customers/page/{pageNum}")
	public String listByPage(Model model, @PathVariable(name = "pageNum") int pageNum, 
							@Param("sortField") String sortField,
							@Param("sortDir") String sortDir,
							@Param("keyword") String keyword)
	{
		
		Page<Customer> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		List<Customer> listCustomer = page.getContent();
		
		long startCount =(pageNum-1)*CustomerService.CUSTOMER_PER_PAGE-1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount+CustomerService.CUSTOMER_PER_PAGE-1;
		
		if(endCount>page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		String curPaglink = "/customers/page/";
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listCustomer", listCustomer);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("endCount", endCount);
		model.addAttribute("link", curPaglink);
		
		return "customers/customers";
	}
	
	@GetMapping("/customers/{id}/enabled/{status}")
	public String updateCustomerStatus(@PathVariable(name = "id")Integer id, @PathVariable("status") boolean enabled,
										RedirectAttributes ra)
	{
		service.updateCustomerStatus(id, enabled);
		String status = enabled ? "Abilitato" :  "Disabilitato";
		String message = "Il cliente con id "+ id + " è stato "+status;
		ra.addFlashAttribute("message", message);
		
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/detail/{id}")
	public String viewCustomer(Model model, @PathVariable("id") Integer id, RedirectAttributes ra)
	{
			Customer customer = service.get(id);
			
			model.addAttribute("customer", customer);		
			return "/customers/customers_detail_modal";
	}
	
	@GetMapping("/customers/edit/{id}")
	public String editCustomer(Model model, @PathVariable(name = "id") Integer id)
	{
		Customer customer = service.get(id);
		List<Country> countries = service.listAllCountry();
		model.addAttribute("listCountries", countries);
		model.addAttribute("customer", customer);
		
		return "/customers/customer_form";
	}
	
	@PostMapping("/customers/save")
	public String saveCustomer(Model model, Customer customer, RedirectAttributes ra)
	{
		service.saveCustomer(customer);
		ra.addFlashAttribute("message", "Salvataggio avvenuto con successo");
		
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/delete/{id}")
	public String deleteCustomer( @PathVariable(name = "id") Integer id, RedirectAttributes ra) throws CustomerNotFoundException
	{
		service.deleteCustomer(id);
		ra.addFlashAttribute("message", "Il cliente con id "+id+" è stato eliminato");
		return "redirect:/customers";
	}
}
