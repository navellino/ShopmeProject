dropdownBrand = $("#brand");
dropdownCategory = $("#category");
var extraImageCount = 0;

$(document).ready(function(){
	
	$("#shortDescription").richText();
	$("#fullDescription").richText();
	
	dropdownBrand.change(function(){
		dropdownCategory.empty();
		getCategories();
	});
	
	//getCategories();
	getCategoriesForNewForm();
	
	
	$("input[name='extraFileImage']").each(function(index){
		extraImageCount++;
		$(this).change(function(){
			
			showExtraImageThumbnail(this, index);
		});
	});
	
	$("a[name='linkRemoveExtraImage']").each(function(index){
		$(this).click(function(){
			removeExtraImage(index);
		});
	});
	
});	

function getCategoriesForNewForm(){
	catIdField = $("#categoryId");
	editMode = false;
	
	if(catIdField.length){
		editMode = true;
	}
	
	if(!editMode) getCategories();
}

function showExtraImageThumbnail(fileInput, index){
	var file = fileInput.files[0];
	var reader = new FileReader();
	reader.onload = function(e){
		$("#extraThumbnail"+index).attr("src", e.target.result);
	};
	reader.readAsDataURL(file);
	
	if(index >= extraImageCount-1){
		addNextExtraImageSection(index+1);
	}
}

function addNextExtraImageSection(index){
	htmlExtraImage = `<div class="col-3 border m-3 p-2" id="divExtraImage${index}">
				<div>
					<label>Extra immagine</label>
					<div id="extraImageHeader${index}"></div>
				</div>
				<div>
					<img id="extraThumbnail${index}" alt="Immagine del prodotto" style="max-width: 300px;" class="rounded img-fluid" src="${defaultImageThumbnailSrc}" />
					<input type="file" name="extraFileImage" class="mb-2" onchange="showExtraImageThumbnail(this, ${index})" accept="image/png, image/jpeg, image/webp" />
				</div>
			</div>`;//ATTENZIONE usare carattere backtik per includere codice html 
	
	htmlLinkRemover = `<a class="fa-regular fa-circle-xmark fa-xl text-end"
						href="javascript:removeExtraImage(${index-1})"
						title="Rimuovi questa immagine"></a>`;
	
				
	$("#divProductImages").append(htmlExtraImage);
	$("#extraImageHeader" + (index-1)).append(htmlLinkRemover);
	extraImageCount++;
}

function removeExtraImage(index){
	$("#divExtraImage"+index).remove();
	//extraImageCount--;
}

function checkUnique(form){
	productId = $("#id").val();
	productName = $("#name").val();
	
	csrfValue = $("input[name='_csrf']").val();
	
	params = {id:productId, name:productName, _csrf:csrfValue};
	
	$.post(checkUniqueUrl, params, function(response){
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