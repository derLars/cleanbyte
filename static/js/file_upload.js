document.getElementById("select-image").onchange = function() {
	alert("HERE. BUT NOTHING SHOULD CHANGE");
//    var submit_button = document.getElementById("submit-button");
//    submit_button.disabled = true;
//    submit_button.innerHTML = "Processing...";
//
//    document.getElementById("upload-form").submit();
};

function select_image() {
	var terms_checkbox = document.getElementById("terms-checkbox");

	if(terms_checkbox.checked){
		//document.getElementById("select-image").click();
	}else{
		alert("Please read and accept the terms & conditions.");
	}
	
}

function download_image(url, filename) {
	saveAs(url,filename);
}