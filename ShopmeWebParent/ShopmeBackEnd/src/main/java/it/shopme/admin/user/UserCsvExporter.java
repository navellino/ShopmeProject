package it.shopme.admin.user;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import it.shopme.common.entity.User;

public class UserCsvExporter {
	
	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
		DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String timestamp = dataFormat.format(new Date());
		String filename = "user_"+timestamp+".csv";
		response.setContentType("text/csv");
		String headerKey = "Content-Disposition";
		String headrValue = "attachment; filename=" + filename;
		response.setHeader(headerKey, headrValue);
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = {"User ID", "Cognome", "Nome", "E-mail", "Ruolo", "Abilitato"};
		String[] fieldMapping = {"id","lastName","firstName","email","roles","enabled"};
		csvWriter.writeHeader(csvHeader);
		
		for(User user : listUsers) {
			csvWriter.write(user, fieldMapping);
		}
		
		csvWriter.close();
		
		
	}
}
