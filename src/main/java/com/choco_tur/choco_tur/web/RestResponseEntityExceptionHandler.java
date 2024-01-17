package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.service.UserAlreadyExistAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler({ UsernameNotFoundException.class })
    public ResponseEntity<Object> handleUserNotFound(RuntimeException ex, WebRequest request) {
        logger.error("404 Status Code", ex);

        return handleExceptionInternal(ex, messageSource.getMessage("message.userNotFound",
                null, request.getLocale()), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ UserAlreadyExistAuthenticationException.class })
    public ResponseEntity<Object> handleUserAlreadyExist(RuntimeException ex, WebRequest request) {
        logger.error("409 Status Code", ex);

        return handleExceptionInternal(ex, messageSource.getMessage("message.regError",
                null, request.getLocale()), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ MailAuthenticationException.class })
    public ResponseEntity<Object> handleMail(RuntimeException ex, WebRequest request) {
        logger.error("500 Status Code", ex);

        return handleExceptionInternal(ex, messageSource.getMessage("message.email.config.error",
                null, request.getLocale()), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleInternal(RuntimeException ex, WebRequest request) {
        logger.error("500 Status Code", ex);

        return handleExceptionInternal(ex, messageSource.getMessage("message.error",
                null, request.getLocale()), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleBindException
            (BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.error("400 Status Code", ex);
        BindingResult result = ex.getBindingResult();

        return handleExceptionInternal(
                ex, result.getFieldErrors(), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    // TODO: Handle DisabledException, LockedException, BadCredentialsException
}
