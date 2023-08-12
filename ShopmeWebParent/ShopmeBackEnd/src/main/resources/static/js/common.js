$(document).ready(function(){
	$("#LogoutLink").on("click", function(e){
		e.preventDefault();
		document.LogoutForm.submit();
	});
	customizeDropDownMenu();
});

function customizeDropDownMenu(){
	$(".dropdown > a").click(function(){
		location.href = this.href
	});
}

function showModalDialog(title, message){
	$("#modalTitle").text(title);
	$("#modalBody").text(message);
	$("#modalDialog").modal('show');
}

function showErrorModalDialog(message){
	showModalDialog("Errore", message);
}
function showWarningModalDialog(message){
	showModalDialog("Attenzione", message);
}

$(document).ready(function(){
	$("#fileImage").change(function(){
		fileSize = this.files[0].size;
		//alert("Dimensione Immagine " + fileSize);
		if(fileSize > MAX_FILE_SIZE){
			this.setCustomValidity("Scegli un immagine più piccola di 3mb!");
			this.reportValidity();
		}else{
			showImageThumbnail(this);
		}
	});
});
		
function showImageThumbnail(fileInput){
	var file = fileInput.files[0];
	var reader = new FileReader();
	reader.onload = function(e){
		$("#thumbnail").attr("src", e.target.result);
	};
	reader.readAsDataURL(file);
}
