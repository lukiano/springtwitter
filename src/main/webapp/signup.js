function registerCallback(data) {
	"use strict";
	if (data.message) {
		jQuery("#checkname").text(data.message);
	} else {
		$("#f").submit();
	}
}

function goBack() {
	window.location.href = "login";
}

function register() {
	"use strict";
    var usern = $("#j_username").val();
    var passw = $("#j_password").val();
    jQuery.post("register", { username:usern, password:passw }, registerCallback, 'json');
}

function checkUserCallback(data) {
	"use strict";
    jQuery("#checkname").text(data.message);
}

function checkUser() {
	"use strict";
    var usern = $("#j_username").val();
    var param = {name: usern};
    jQuery.getJSON("exists", param, checkUserCallback);
}

function ready() {
	"use strict";
    jQuery("#register").click(register);
    jQuery("#goback").click(goBack);
    jQuery("#j_username").blur(checkUser);
}