<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8"%>
<%@ page import="org.springframework.security.web.WebAttributes" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="es">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
    <title>Spring Twitter</title>
    <spring:url value="/bootstrap/css/bootstrap.min.css" var="bootstrapUrl" />
	<link href="${bootstrapUrl}" rel="stylesheet"></link>
	<script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
    <form name="f" action="spring_security_login" class="form-horizontal" method="post">
        <fieldset>
            <legend>Información de inicio de sesión</legend>
            <div class="control-group">
                <label class="control-label" for="j_username">Usuario</label>
                <div class="controls">
	                <input type="text" name="j_username" id="j_username"
	                       <c:if test="${not empty param.login_error}">value="<%= request.getParameter("login_name") %>"
	                </c:if> ></input>
				    <c:if test="${not empty param.login_error}">
				        <div class="help-inline">
				            No pudiste iniciar sesión. Intentá de nuevo. Razón: ${exceptionMessage}
				        </div>
				    </c:if>
	            </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="j_password">Clave</label>
                <div class="controls">
                	<input type="password" name="j_password" id="j_password"></input>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="remember_me">No preguntarme la clave por dos semanas</label>
                <div class="controls">
	                <input type="checkbox" name="_spring_security_remember_me" id="remember_me"></input>
	            </div>
	        </div>
            <div class="form-actions">
                <button class="btn btn-primary" id="submit" type="submit">Ingresar</button>
            </div>
        </fieldset>
    </form>
    <p>
        <a href="signup">Registrarse con un usuario nuevo</a>
    </p>
</div>
</body>
</html>