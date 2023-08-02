package it.shopme.admin.user.export;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import it.shopme.admin.AbstractExporter;
import it.shopme.common.entity.User;

public class UserCsvExporter extends AbstractExporter {
	
	public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
		super.setResponseHeader(response, "text/csv", ".csv","user_");
		
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
