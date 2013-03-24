function goBack() {
    window.location.href = "login";
}

function registerCallback(data) {
	"use strict";
    console.log(data);
	if (data.errorNumber) {
        if (data.errorNumber == 0) {
            jQuery("#checkemail").hide();
		    jQuery("#checkname").text(data.error.message);
            jQuery("#checkname").show();
        } else if (data.errorNumber == 1) {
            jQuery("#checkname").hide();
            jQuery("#checkemail").text(data.error.message);
            jQuery("#checkemail").show();
        }
	} else {
        //goBack();
        console.log("goback");
	}
}

function register() {
	"use strict";
    var usern = $("#j_username").val();
    var passw = $("#j_password").val();
    var email = $("#j_email").val();
    var jqxhr = jQuery.post("register", { username:usern, password:passw, email:email }, registerCallback, 'json');
    jqxhr.error(function() { registerCallback; })
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
    //jQuery("#j_username").blur(checkUser);
    var rules = {
        rules: {
            j_username: {
                minlength: 2,
                remote: "freetouse",
                required: true
            },
            j_email: {
                email: true,
                required: true
            },
            j_password: {
                minlength: 6,
                required: true
            }
        },
        highlight: function(label) {
            $(label).closest('.control-group').addClass('error');
        },
        success: function(label) {
            label
                .text('OK!').addClass('valid')
                .closest('.control-group').addClass('success');
        }
    };
    var validator = $("#f").validate(rules);

}
$(document).ready(ready);