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
		if(!checkFileSize(this)){
			return
		}
		showImageThumbnail(this);
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

function checkFileSize(fileInput){
	fileSize = fileInput.files[0].size;
		if(fileSize > MAX_FILE_SIZE){
			fileInput.setCustomValidity("Scegli un immagine più piccola di "+MAX_FLE_SIZE / 1000 +"mb!");
			fileInput.reportValidity();
			
			return false;
		}else{
			
			return true
		}
}
