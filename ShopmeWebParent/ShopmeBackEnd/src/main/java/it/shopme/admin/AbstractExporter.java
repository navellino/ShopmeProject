package it.shopme.admin;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;


public class AbstractExporter {
	public void setResponseHeader(HttpServletResponse response, String contentType, String extension, String prefix) throws IOException {
		DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String timestamp = dataFormat.format(new Date());
		String filename = prefix+timestamp+extension;
		response.setContentType(contentType);
		String headerKey = "Content-Disposition";
		String headrValue = "attachment; filename=" + filename;
		response.setHeader(headerKey, headrValue);
	}
}
