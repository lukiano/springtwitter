function signup() {
    window.location.href = "signup";
}

function submit() {

}
function ready() {
    var rules = {
        rules: {
            name: {
                minlength: 2,
                required: true
            },
            email: {
                required: true,
                email: true
            },
            subject: {
                minlength: 2,
                required: true
            },
            message: {
                minlength: 2,
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
    $("#f").validate(rules);
    $("#signup").click(signup);
    //$("#submit").click(submit);
}
$(document).ready(ready);
