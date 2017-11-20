/**
	 * AJAX helper
	 * 
	 * @param method - GET|POST|PUT|DELETE
	 * @param url - API end point
	 * @param callback - This the successful callback
	 * @param errorHandler - This is the failed callback
	 */

	function ajax(method, url, data, callback, errorHandler) {
		  var xhr = new XMLHttpRequest();

		  xhr.open(method, url, true);

		  xhr.onload = function () {
		    switch (xhr.status) {
		      case 200:
		        callback(xhr.responseText);
		        break;
		      case 403:
		        onSessionInvalid();
		        break;
		      case 401:
		        errorHandler();
		        break;
		    }
		  };

		  xhr.onerror = function () {
		    console.error("The request couldn't be completed.");
		    errorHandler();
		  };

		  if (data === null) {
		    xhr.send();
		  } else {
		    xhr.setRequestHeader("Content-Type", "application/json;charset=utf-8");
		    xhr.send(data);
		  }
	}


function verifyUser() {
		//
		console.log('user_login');
		
		// The request parameters
		var url = './login';
		var user_name = $('user_name').value;
		var user_password = $('user_password').value;

		// The request parameters
		var req = JSON.stringify({
			user_name : user_name,
			user_password : user_password
		});
		var method =  'POST';

		ajax(method, url, req,
		// successful callback
		function(res) {
			console.log("return ok!");
			var result = JSON.parse(res);
			if (result.result === 'FAILURE') {
				alert("LogIn fails!");
			}else {
				alert("LogIn Successful!");
				window.location = "./index.html";
			}
		});


	}

function $(tag, options) {
		if (!options) {
			return document.getElementById(tag);
		}

		var element = document.createElement(tag);

		for ( var option in options) {
			if (options.hasOwnProperty(option)) {
				element[option] = options[option];
			}
		}

		return element;
	}