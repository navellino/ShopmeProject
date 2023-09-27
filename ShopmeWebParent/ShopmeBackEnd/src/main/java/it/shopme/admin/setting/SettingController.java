package it.shopme.admin.setting;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.shopme.admin.FileUploadUtil;
import it.shopme.common.entity.Currency;
import it.shopme.common.entity.GeneralSettingBag;
import it.shopme.common.entity.Settings;

@Controller
public class SettingController {
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private CurrencyRepository currencyRepository;
	
	@GetMapping("/settings")
	public String getPage(Model model) {
		
		List<Settings> listSettings = settingService.listAllSettings();
		List<Currency> listCurrencies = currencyRepository.findAllByOrderByNameAsc();
		
		model.addAttribute("listCurrencies", listCurrencies);
		
		for(Settings setting : listSettings) {
			model.addAttribute(setting.getKey(),setting.getValue());
		}
				
		return "settings/settings";
	}
	
	@PostMapping("/settings/save_general")
	public String saveSettings(@RequestParam("fileImage") MultipartFile multipartFile, 
				HttpServletRequest request,RedirectAttributes ra) throws IOException {
		
		GeneralSettingBag settingsBag = settingService.getGeneralSettings();
		
		saveSiteLogo(multipartFile, settingsBag);
		saveCurrencySymbol(request, settingsBag);
		updateSettingValuesForm(request, settingsBag.list());
		
		ra.addFlashAttribute("message", "Setting aggiornati con successo");
		return "redirect:/settings";
	}

	private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingsBag) throws IOException {
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String value = "/site-logo/"+ fileName;
			String uploadDir = "../site-logo/";
			settingsBag.updateSiteLogo(value);
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}
	}
	
	private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingsBag) {
		Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
		Optional<Currency> currencyById = currencyRepository.findById(currencyId);
		
		if(currencyById.isPresent()) {
			Currency currency = currencyById.get();
			settingsBag.updateCurrencySymbol(currency.getSymbol());
		}
	}
	
	private void updateSettingValuesForm(HttpServletRequest request, List<Settings> listSettings) {
		for(Settings setting : listSettings ) {
			String value = request.getParameter(setting.getKey());
			if(value != null) {
				setting.setValue(value);
			}
		}
		settingService.saveAll(listSettings);
	}
}
