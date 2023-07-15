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