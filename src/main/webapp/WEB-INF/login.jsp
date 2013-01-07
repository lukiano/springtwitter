<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8"%>
<%@ page import="org.springframework.security.web.WebAttributes" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Spring Twitter</title>
    <spring:url value="/bootstrap/css/bootstrap.css" var="bootstrapCssUrl" />
    <spring:url value="/bootstrap/js/bootstrap.js" var="bootstrapJsUrl" />
	<link href="${bootstrapCssUrl}" rel="stylesheet">
	<spring:url value="/userAjaxBootstrap.htm" var="formUrl" />
	<spring:url value="/userAjaxBootstrap.json" var="formJsonUrl" />
    <style type="text/css">
        body {
            padding-top: 40px;
            padding-bottom: 40px;
            background-color: #f5f5f5;
        }

        .form-signin {
            max-width: 300px;
            padding: 19px 29px 29px;
            margin: 0 auto 20px;
            background-color: #fff;
            border: 1px solid #e5e5e5;
            -webkit-border-radius: 5px;
            -moz-border-radius: 5px;
            border-radius: 5px;
            -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.05);
            -moz-box-shadow: 0 1px 2px rgba(0,0,0,.05);
            box-shadow: 0 1px 2px rgba(0,0,0,.05);
        }
        .form-signin .form-signin-heading,
        .form-signin .checkbox {
            margin-bottom: 10px;
        }
        .form-signin input[type="text"],
        .form-signin input[type="password"] {
            font-size: 16px;
            height: auto;
            margin-bottom: 15px;
            padding: 7px 9px;
        }
    </style>
</head>
<body>
<div>
    <c:if test="${not empty param.login_error}">
        <div class="error">
            Your login attempt was not successful, try again.<br/><br/>
            Reason: ${exceptionMessage}
        </div>
    </c:if>
    <form name="f" action="spring_security_login" class="form-signin" method="post">
        <fieldset>
            <legend class="form-signin-heading">Login Information</legend>
            <div class="control-group" id="username">
                <input type="text" class="input-block-level" placeholder="your nickname" name="j_username" id="j_username"
                       <c:if test="${not empty param.login_error}">value="<%= request.getParameter("login_name") %>"
                </c:if> />
            </div>
            <div class="control-group" id="password">
                <input type="password" class="input-block-level" placeholder="password" name="j_password" id="j_password"/>
            </div>
            <label class="checkbox" for="remember_me"><input type="checkbox" name="_spring_security_remember_me" id="remember_me"/>Don't ask for my password again</label>
            <button id="submit" class="btn btn-large btn-primary" type="submit">Login</button>
            <button id="signup" class="btn btn-large" type="button">No nick? Sign Up!</button>
        </fieldset>
    </form>
</div>
<script type="text/javascript" src="http://code.jquery.com/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="${bootstrapJsUrl}"></script>
<script type="text/javascript">
    function signup() {
        window.location.href = "signup";
    }
    function ready() {
        $("#signup").click(signup);
    }
    $(document).ready(ready);
</script>
</body>
</html>