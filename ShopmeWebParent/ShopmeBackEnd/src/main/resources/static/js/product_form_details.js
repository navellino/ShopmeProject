function addNextDetailSection() {
	allDivDetail = $("[id='divDetail']");
	divDetailsCount = allDivDetail.length;

	
	htmlDetailSection = `
			<div class="form row align-items-center mt-3" id="divDetail${divDetailsCount}">
				<input type="hidden" name="detailIds" value="0">
				<div class="col-4">
					<label>Nome:</label>
					<input type="text" class="form-control" name="detailNames" maxlength="50" />
				</div>
				<div class="col-4">
					<label>Valore:</label>
					<input type="text" class="form-control" name="detailValues" maxlength="255" />
				</div>
			</div>
		`;
		$("#divProductDetails").append(htmlDetailSection);
		
		previusDivDetailSection = allDivDetail.last();
		
		prviusDetailId = previusDivDetailSection.attr("id");
		
		htmlLinkRemover = `<a class="fa-regular fa-circle-xmark fa-xl text-end"
							href="javascript:removeDetailSectionById('${prviusDetailId}')"
							 title="Rimuovi"></a>`;
						
		previusDivDetailSection.append(htmlLinkRemover);
		
		$("input[name='detailNames']").last().focus();
}

$(document).ready(function(){
	$("a[name='linkRemoveDetail']").each(function(index){
		$(this).click(function(){
			removeDetailSectionByIndex(index);
		});
	});
});

function removeDetailSectionById(id){
	$("#"+id).remove();
}

function removeDetailSectionByIndex(index){
	$("#divDetail"+index).remove();
}