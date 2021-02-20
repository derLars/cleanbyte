
var request_running = false;

function start_processing() {
    if(!request_running){
        request_running = true;

        var submit_button = document.getElementById("submit-button");
        submit_button.disabled = true;
        submit_button.innerHTML = "Processing...";

        var example = document.getElementById("example");
        example.innerHTML = "";

        return true;
    }
    return false; 
}

function enable_selector() {
    request_running = false;

    var submit_button = document.getElementById("submit-button");
    submit_button.disabled = false;
    submit_button.innerHTML = "Upload an image";
}

$("#select-image").change(function() {
    if(start_processing()) {
        csrftoken = document.querySelector('[name=csrfmiddlewaretoken]').value;

        var formData = new FormData();
        formData.append('image', $('input[type=file]')[0].files[0]);
        formData.append('csrfmiddlewaretoken', csrftoken);

        $.ajax({
            url: '/background_remover/remove_bg/',
            method: "POST",
            data: formData,
            processData: false,
            contentType: false,

            success: function (data) {
                $.each(data, function (index, itemData) {
                    var results = document.getElementById("results");

                    var innerHTML = "";
                    
                    innerHTML += '<img class="center result-img" src="' + itemData.download_url + '"/><br>';
                    
                    innerHTML += '<button id="download-button" class="btn btn-primary"><a href=" ' + itemData.download_url + ' " target="_blank">Download</a></button>';

                    results.insertAdjacentHTML('afterbegin', innerHTML);
                });
                enable_selector();
            },
            fail: function(xhr, textStatus, errorThrown){
                enable_selector();
            },
        });   
    }
});

function select_image() {
	var terms_checkbox = document.getElementById("terms-checkbox");

	if(terms_checkbox.checked){
		document.getElementById("select-image").click();
	}else{
		alert("Please read and accept the terms & conditions.");
	}
	
}

function download_image(url, filename) {
	saveAs(url,filename);
}