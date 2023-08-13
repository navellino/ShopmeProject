
dropdownBrand = $("#brand");
dropdownCategory = $("#category");

$(document).ready(function(){
	
	$("#shortDescription").richText();
	$("#fullDescription").richText();
	
	dropdownBrand.change(function(){
		dropdownCategory.empty();
		getCategories();
	});
	getCategories();
	
	$("input[name='extraImage']").each(function(index){
		$(this).change(function(){
			showExtraImageThumbnail(this, index);
		});
	});
});	

function showExtraImageThumbnail(fileInput, index){
	var file = fileInput.files[0];
	var reader = new FileReader();
	reader.onload = function(e){
		$("#extraThumbnail"+index).attr("src", e.target.result);
	};
	reader.readAsDataURL(file);
	
	addNextExtraImageSection(index+1);
}

function addNextExtraImageSection(index){
	html = `<div class="col-3 border m-3 p-2">
				<div>
					<img id="extraThumbnail${index}" alt="Immagine del prodotto" style="max-width: 300px;" class="rounded img-fluid" src="${defaultImageThumbnailSrc}" />
					<input type="file" name="extraImage" class="mb-2" onchange="showExtraImageThumbnail(this, ${index})" accept="image/png, image/jpeg, image/webp" />
				</div>
			</div>`;
	
	htmlExtraImage = `
		<div class="col border m-3 p-2" id="divExtraImage${index}">
			<div id="extraImageHeader${index}"><label>Extra Image #${index + 1}:</label></div>
			<div class="m-2">
				<img id="extraThumbnail${index}" alt="Extra image #${index + 1} preview" class="img-fluid"
					src="${defaultImageThumbnailSrc}"/>
			</div>
			<div>
				<input type="file" name="extraImage"
					onchange="showExtraImageThumbnail(this, ${index})" 
					accept="image/png, image/jpeg" />
			</div>
		</div>	
	`;
	
	$("#divProductImages").append(html);
}

function checkUnique(form){
	productId = $("#id").val();
	productName = $("#name").val();
	
	csrfValue = $("input[name='_csrf']").val();
	url = "[[@{/products/check_unique}]]";
	
	params = {id:productId, name:productName, _csrf:csrfValue};
	
	$.post(url, params, function(response){
		if(response == "OK"){
			form.submit();
		}else if(response == "Duplicato"){
			showWarningModalDialog("Esiste un prodotto con nome "+ productName);
		}else{
			showWarningModalDialog("Nessuna risposta dal server");
		}
	}).fail(function(){
		showErrorModalDialog("Impossibile connettersi al server");
	});
	return false;
}

function getCategories(){
	brandId = dropdownBrand.val();
	url = brandsModuleURL +"/"+brandId+"/categories";
	$.get(url, function(responseJson){
		$.each(responseJson, function(index, category){
			$("<option>").val(category.id).text(category.name).appendTo(dropdownCategory);
		});
	});	
}


