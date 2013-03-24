package com.lucho.controller;

import com.lucho.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.ValidationException;

/**
 * Controller that provides register related endpoints.
 *
 * @author Luciano.Leggieri
 */
@Controller
public final class RegisterController {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);

    /**
     * I18n messages.
     */
    private final MessageSourceAccessor messages;

    /**
     * Class constructor.
     * @param aMessageSource i18n messages.
     */
    @Inject
    public RegisterController(final MessageSource aMessageSource) {
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
            ConstraintViolationException cve =(ConstraintViolationException) ve;
            ConstraintViolation<?> cv = cve.getConstraintViolations().iterator().next();
            returnMessage = Message.error(cv.getMessage());
        } else {
            returnMessage = Message.error(ve.getLocalizedMessage());
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
        if (pe.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) pe.getCause();
            ConstraintViolation<?> cv = cve.getConstraintViolations().iterator().next();
            returnMessage = Message.error(cv.getMessage());
        } else {
            returnMessage = Message.error(pe.getLocalizedMessage());
        }
        if (pe instanceof EntityExistsException) {
            returnMessage = Message.error(messages.getMessage("login.register.failure"));
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
            returnMessage = Message.error(messages.getMessage("login.register.failure"));
        } else {
            returnMessage = Message.error(de.getLocalizedMessage());
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
            returnMessage = this.handleValidationException((ValidationException) te.getMostSpecificCause());
        } else if (te.getMostSpecificCause() instanceof PersistenceException) {
            returnMessage = this.handlePersistenceException((PersistenceException) te.getMostSpecificCause());
        } else {
            returnMessage = Message.error(te.getLocalizedMessage());
        }
        return returnMessage;
    }

    /**
     * Shows a friendly message instead of the exception stack trace.
     * @param be exception.
     * @return the exception message.
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Message handleBindException(final BindException be) {
        Message returnMessage;
        BindingResult br = be.getBindingResult();
        if (br.hasFieldErrors()) {
            returnMessage = Message.error(br.getFieldError().getDefaultMessage());
        } else {
            returnMessage = Message.error(br.getGlobalError().getDefaultMessage());
        }
        return returnMessage;
    }

    /**
        * Creates a new user with the specified user name and password.
        * @param form User registration form contains data for a new user.
        * @return a string detailing success or failure.
        */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Message register(@Valid final UserRegistrationForm form) {
        User newUser = new User(form.getUsername(), form.getPassword(), form.getEmail());
        newUser.save();
        return Message.success("");
    }

}
