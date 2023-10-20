var buttonLoad;
var dropDownCountry;
var buttonAddCountry;
var buttonUpdateCountry;
var buttonDeleteCountry;
var labelCountryName;
var fieldCountryName;
var selectedCountryName;
var selectedCountryCode;
var fieldCountryCode;

$(document).ready(function(){
	buttonLoad = $("#loadCountries");
	dropDownCountry = $("#dropDownCountries");
	buttonAddCountry = $("#btnAddCountry");
	buttonUpdateCountry = $("#btnUpdCountry");
	buttonDeleteCountry = $("#btnDelCountry");
	labelCountryName = $("#labelCountryName");
	fieldCountryName = $("#fieldCountryName");
	fieldCountryCode = $("#fieldCountryCode");
	buttonLoad.click(function(){
		loadCountries();
	});
	
	dropDownCountry.on("change", function(){
		changeFormSateToSelectedCountry();
	});
	
	buttonAddCountry.click(function(){
		if(buttonAddCountry.val() == "Aggiungi"){
			if(fieldCountryName.val() != "" && fieldCountryCode.val() != ""){
				addCountry();
			}else{
				showToastMessage("Tutti i campi sono obbligatori!");
			}
		}else{
			changeFormStateToNew();
		}
	});
	buttonUpdateCountry.click(function(){
		updateCountry();
	});
	buttonDeleteCountry.click(function(){
		deleteCountry();
	});
});

function deleteCountry(){
	optionValue = dropDownCountry.val();
	countryId = optionValue.split("-")[0];
	url = contextPath+"countries/delete/"+countryId;
	changeFormStateToNew();
	$.get(url, function(responseJSON){
		$("#dropDownCountries option[value='"+optionValue+"']").remove();
	}).done(function(){
		showToastMessage("Stato eliminato.");
	}).fail(function(){
		showToastMessage("ERRORE: impossibile collegarsi al server!");
	});
	
}

function updateCountry(){
	
	if(!validateFormCountry()) return;
	
	url = contextPath+"countries/save";
	countryName = fieldCountryName.val();
	countryCode = fieldCountryCode.val();
	
	countryId = dropDownCountry.val().split("-")[0];
	
	jsonData = {id: countryId, name: countryName, code: countryCode};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
			
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(countryId){
		if(countryId == 0){
			showToastMessage("Stato già in elenco");
			fieldCountryCode.val("");
			fieldCountryName.val("").focus();
		} else{
			$("#dropDownCountries option:selected").val(countryId+"-"+countryCode);
			$("#dropDownCountries option:selected").text(countryName);
			showToastMessage("Stato aggiornato con successo: "+countryId);
			changeFormStateToNew();
			loadCountries();
		}
		
	}).fail(function(){
		showToastMessage("Errore di counicazione col server!");
	});
}
//validiamo i campi di inserimento Stato o regione per evitare di insrire campi vuoti
function validateFormCountry(){
	formCountry = document.getElementById("formCountry");
	if(!formCountry.checkValidity()){
		formCountry.reportValidity();
		return false;
	}
	return true;
}

function addCountry(){
	
	if(!validateFormCountry()) return;
		
	url = contextPath+"countries/save";
	countryName = fieldCountryName.val();
	countryCode = fieldCountryCode.val();
	
	jsonData = {name: countryName, code: countryCode};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
			
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(countryId){
		if(countryId == 0){
			showToastMessage("Stato già in elenco");
			fieldCountryCode.val("");
			fieldCountryName.val("").focus();
		} else{
			//selectNewlyCountryAdded(countryId, countryCode, countryName);
			showToastMessage("Stato aggiunto con successo: "+countryId);
			loadCountries();
		}
		
	}).fail(function(){
		showToastMessage("Errore di counicazione col server!");
	});
}

function selectNewlyCountryAdded(countryId, countryCode, countryName){
	optionValue = countryId + "-" + countryCode;
	$("<option>").val(optionValue).text(countryName).appendTo(dropDownCountry);
	$("#dropDownCountries option[value'"+optionValue+"']").prop("selected", true);
	
	fieldCountryCode.val("");
	fieldCountryName.val("").focus;
	
}

function changeFormSateToSelectedCountry(){
	buttonAddCountry.prop("value","Nuovo");
	buttonUpdateCountry.prop("disabled",false);
	buttonDeleteCountry.prop("disabled",false);
	
	selectedCountryName = $("#dropDownCountries option:selected").text();
	fieldCountryName.val(selectedCountryName);
	
	countryCode =dropDownCountry.val().split("-")[1];
	fieldCountryCode.val(countryCode);
}

function loadCountries(){
	//url = "https://restcountries.com/v3.1/all"
	url = contextPath+"countries/list";
	$.get(url, function(responseJSON){
		dropDownCountry.empty();
		
		$.each(responseJSON, function(index, country){
			optionValue = country.id + "-" + country.code;
			$("<option>").val(optionValue).text(country.name).appendTo(dropDownCountry);
		});
	}).done(function(){
		buttonLoad.val("Aggiorna Lista");
		showToastMessage("Lista aggiornata");
	}).fail(function(){
		showToastMessage("ERRORE: impossibile collegarsi al server!");
	});
}

function showToastMessage(message){
	$("#toastMessage").text(message);
	$(".toast").toast('show');
}

function changeFormStateToNew(){
	buttonAddCountry.val("Aggiungi");
	buttonUpdateCountry.prop("disabled",true);
	buttonDeleteCountry.prop("disabled",true);
	
	fieldCountryCode.val("");
	fieldCountryName.val("").focus();
	
}