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

