package com.lucho.controller;

import com.lucho.domain.User;
import com.lucho.repository.UserRepository;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

/**
 * Controller that provides login related endpoints.
 *
 * @author Luciano.Leggieri
 */
@Controller
public final class LoginController {

    /**
     * User service.
     */
    private final UserRepository userRepository;

    /**
     * I18n messages.
     */
    private final MessageSourceAccessor messages;

    /**
     * Class constructor.
     * @param anUserRepository user repository.
     * @param aMessageSource i18n messages.
     */
    @Inject
    public LoginController(final UserRepository anUserRepository,
                           final MessageSource aMessageSource) {
        this.userRepository = anUserRepository;
        this.messages = new MessageSourceAccessor(aMessageSource);
    }

    /**
     * Shows a friendly message instead of the exception stack trace.
     * @param ve exception.
     * @return the exception message.
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Message handleValidationException(final ValidationException ve) {
        Message returnMessage;
        if (ve instanceof ConstraintViolationException) {
            ConstraintViolationException cve =
                    (ConstraintViolationException) ve;
            ConstraintViolation<?> cv =
                    cve.getConstraintViolations().iterator().next();
            returnMessage = Message.build(cv.getMessage());
        } else {
            returnMessage = Message.build(ve.getLocalizedMessage());
        }
        return returnMessage;
    }

    /**
     * Shows a friendly message instead of the exception stack trace.
     * @param pe exception.
     * @return the exception message.
     */
    @ExceptionHandler(PersistenceException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Message handlePersistenceException(final PersistenceException pe) {
        Message returnMessage;
        if (pe.getCause()
                instanceof ConstraintViolationException) {
            ConstraintViolationException cve =
                    (ConstraintViolationException) pe.getCause();
            ConstraintViolation<?> cv =
                    cve.getConstraintViolations().iterator().next();
            returnMessage = Message.build(cv.getMessage());
        } else {
            returnMessage = Message.build(pe.getLocalizedMessage());
        }
        if (pe instanceof EntityExistsException) {
            returnMessage = Message.build(
                    messages.getMessage("login.register.failure"));
        }
        return returnMessage;
    }

    /**
     * Shows a friendly message instead of the exception stack trace.
     * @param de exception.
     * @return the exception message.
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Message handleDataAccessException(final DataAccessException de) {
        Message returnMessage;
        if (de instanceof DataIntegrityViolationException) {
            returnMessage = Message.build(
                    messages.getMessage("login.register.failure"));
        } else {
            returnMessage = Message.build(de.getLocalizedMessage());
        }
        return returnMessage;
    }

    /**
     * Shows a friendly message instead of the exception stack trace.
     * @param te exception.
     * @return the exception message.
     */
    @ExceptionHandler(TransactionException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Message handleTransactionException(final TransactionException te) {
        Message returnMessage;
        if (te.getMostSpecificCause() instanceof ValidationException) {
            returnMessage = this.handleValidationException(
                    (ValidationException) te.getMostSpecificCause());
        } else if (te.getMostSpecificCause() instanceof PersistenceException) {
            returnMessage = this.handlePersistenceException(
                    (PersistenceException) te.getMostSpecificCause());
        } else {
            returnMessage = Message.build(te.getLocalizedMessage());
        }
        return returnMessage;
    }

    /**
     * Login call.
     * @param request servlet request.
     * @return Authentication exception string, or an empty string.
     */
    @RequestMapping(value = "/login")
    @ModelAttribute("exceptionMessage")
    public String login(final HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "";
        }
        Throwable t = (Throwable) session.getAttribute(
            WebAttributes.AUTHENTICATION_EXCEPTION);
        if (t == null) {
            return "";
        } else {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            return t.getLocalizedMessage();
        }
    }

        /**
        * Creates a new user with the specified user name and password.
        * @param username the name of the new user.
        * @param password the password for the new user.
        * @return a string detailing success or failure.
        */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Message register(final String username, final String password) {
        User newUser = new User(username, password);
        newUser.save();
        return Message.build("");
    }

    /**
     * Checks if an user with the specified username exists.
     *
     * @param username a string with the user name.
     * @return a string detailing if the user name is free to use.
     */
    @RequestMapping(value = "/exists")
    @ResponseBody
    public Message exists(@RequestParam(value = "name") final String username) {
        Message message;
        if (this.userRepository.findByUsername(username) == null) {
            message = Message.build(
                    messages.getMessage("login.username.free"));
        } else {
            message = Message.build(
                    messages.getMessage("login.username.exists"));
        }
        return message;
    }

}
