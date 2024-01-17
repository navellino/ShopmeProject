var dropdownCountry;
var dataListState;
var fieldState;

$(document).ready(function(){
	dropdownCountry = $("#country");
	dataListState = $("#listState");
	fieldState = $("#state");
	
	dropdownCountry.on("change", function(){
		loadStatesForCountry();
		fieldState.val("").focus();
	});
});

function checkPasswordMatch(confirmPassword){
	if(confirmPassword.value != $("#password").val()){
		confirmPassword.setCustomValidity("Le password non corrispondono");
	}else{
		confirmPassword.setCustomValidity("");
	}
}

function loadStatesForCountry(){
	selectedCountry = $("#country option:selected");
	countryId = selectedCountry.val();
	url = contextPath + "settings/list_by_country/"+countryId;
	
	$.get(url, function(responseJSON){
		dataListState.empty();
		$.each(responseJSON, function(index, state){
			$("<option>").val(state.name).text(state.name).appendTo(dataListState);
		});
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