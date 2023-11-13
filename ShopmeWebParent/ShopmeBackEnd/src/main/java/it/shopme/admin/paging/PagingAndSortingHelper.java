package it.shopme.admin.paging;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PagingAndSortingHelper {
	private String listName;
	private String moduleURL;
	private String sortField;
	private String sortDir;
	private String keyword;
	private ModelAndViewContainer model;
	
	public PagingAndSortingHelper(ModelAndViewContainer model, String moduleURL, String listName,
			String sortField, String sortDir, String keyword){
		this.model = model;
		this.listName = listName;
		this.sortField = sortField;
		this.sortDir = sortDir;
		this.keyword = keyword;
		this.moduleURL = moduleURL;
	}
	
	public void updateModelAttribute(int pageNum, Page<?> page) {
		List<?> listItems = page.getContent();
		int pageSize = page.getSize();
		
		long startCount = (pageNum-1) * pageSize + 1;
		long endoCount = startCount + pageSize - 1;
		if(endoCount > page.getTotalElements()) {
			endoCount = page.getTotalElements();
		}
		model.addAttribute("lastPage", page.getTotalPages());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endoCount);		
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute(listName, listItems);
		model.addAttribute("moduleURL", moduleURL);
	}

	public String getSortField() {
		return sortField;
	}

	public String getSortDir() {
		return sortDir;
	}

	public String getKeyword() {
		return keyword;
	}
}
