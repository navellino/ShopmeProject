package it.shopme.admin.product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import it.shopme.admin.FileUploadUtil;
import it.shopme.common.entity.Product;
import it.shopme.common.entity.ProductImage;

public class ProductSaveHelper {
	static void deleteExtraImageWeredDeletedOnForm(Product product) {
			
			String extraImageDir = "../product-images/"+product.getId()+"/extras";
			Path dirPath = Paths.get(extraImageDir);
			
			try {
				Files.list(dirPath).forEach(file -> {
					String filename = file.toFile().getName();
					if(!product.containsImageName(filename)) {
						try {
							Files.delete(file);
						}catch (IOException ex) {
							System.out.println("Impossibile cancellare il file "+filename);
						}
					}
				});
			} catch (IOException ex) {
				System.out.println("Impossibile list la directory "+dirPath);
			}
			
		}
	
		static void setExistingExtraImageName(String[] imageIds, String[] imageNames, Product product) {
			if(imageIds == null || imageIds.length == 0) return;
			
			Set<ProductImage> images = new HashSet<>();
			
			for(int count = 0; count < imageIds.length; count++) {
				
				Integer id = Integer.parseInt(imageIds[count]);
				String name = imageNames[count];
				
				images.add(new ProductImage(id, name, product));
			}
			
			product.setImages(images);
		}
	
		static void setProductDetails(String[] detailId, String[] detailNames, String[] detailValues, Product product) {
			
			if(detailNames == null || detailNames.length == 0) return;
			
			for(int count = 0; count < detailNames.length; count++) {
				String name = detailNames[count];
				String value = detailValues[count];
				Integer id = Integer.parseInt(detailId[count]);
				
				if(id != 0) {
					product.addDetails(id, name, value);
				} else if(!name.isEmpty() && !value.isEmpty()) {
					product.addDetails(name, value);
				}
			}
			
		}
	
		static void saveUploadImage(MultipartFile mainImageMultipart, MultipartFile[] extraMultipartFile,
				Product savedProduct) throws IOException {
			
			if(!mainImageMultipart.isEmpty()) {
				String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
				String uploadDir = "../product-images/"+savedProduct.getId();
				
				FileUploadUtil.cleanDir(uploadDir);
				FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
			}
			
			if(extraMultipartFile.length > 0) {	
				for(MultipartFile multipartFile: extraMultipartFile) {
					if(multipartFile.isEmpty()) continue;
					
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename()); 
					String uploadExtraDir = "../product-images/"+ savedProduct.getId() +"/extras";
				
					FileUploadUtil.saveFile(uploadExtraDir, fileName, multipartFile);
				}
			}		
		}
	
		static void setNewExtraImageName(MultipartFile[] extraMultipartFile, Product product) {
			if(extraMultipartFile.length>0) {
				for(MultipartFile multipartFile: extraMultipartFile) {
					if(!multipartFile.isEmpty()) {
						String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
						if(!product.containsImageName(fileName)) {
							product.addExtraImage(fileName);
						}
					}
				}
			}	
		}
	
		static void setMainImageName(MultipartFile mainImageMultipart, Product product) {
			if(!mainImageMultipart.isEmpty()) {
				String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
				product.setMainImage(fileName);
			}
		}

}
