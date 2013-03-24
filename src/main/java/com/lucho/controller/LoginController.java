package com.lucho.controller;

import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Controller that provides login related endpoints.
 *
 * @author Luciano.Leggieri
 */
@Controller
public final class LoginController {

    /**
     * Login call.
     * @param request servlet request.
     * @return Authentication exception string, or an empty string.
     */
    @RequestMapping(value = "/login")
    @ModelAttribute("exceptionMessage")
    public String login(final HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String exceptionMessage = "";
        if (session != null) {
            Throwable t = (Throwable) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (t != null) {
                session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
                exceptionMessage = t.getLocalizedMessage();
            }
        }
        return exceptionMessage;
    }

}
