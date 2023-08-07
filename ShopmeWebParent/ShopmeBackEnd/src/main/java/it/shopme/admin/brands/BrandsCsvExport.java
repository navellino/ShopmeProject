package it.shopme.admin.brands;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import it.shopme.admin.AbstractExporter;
import it.shopme.common.entity.Brand;

public class BrandsCsvExport extends AbstractExporter {
	public void export(List<Brand> brands,  HttpServletResponse response) throws IOException{
		super.setResponseHeader(response, "text/csv", ".csv", "marche_");
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		
		String[] csvHeader = {"ID Marca", "Descrizione","Categorie"};
		String[] fieldMapping = {"id","name","categories"};
		
		csvWriter.writeHeader(csvHeader);
		
		for(Brand brand : brands) {
			csvWriter.write(brand, fieldMapping);
		}
		
		csvWriter.close();
	}
}
