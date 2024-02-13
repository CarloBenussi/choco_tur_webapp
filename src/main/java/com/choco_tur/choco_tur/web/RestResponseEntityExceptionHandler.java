package com.choco_tur.choco_tur.web;

import com.choco_tur.choco_tur.service.UserAlreadyExistAuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.concurrent.ExecutionException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler({ UsernameNotFoundException.class })
    public ResponseEntity<Object> handleUserNotFound(RuntimeException ex, WebRequest request) {
        logger.error("404 Status Code", ex);

        return handleExceptionInternal(ex, messageSource.getMessage("userNotFound",
                null, request.getLocale()), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ UserAlreadyExistAuthenticationException.class })
    public ResponseEntity<Object> handleUserAlreadyExist(RuntimeException ex, WebRequest request) {
        logger.error("409 Status Code", ex);

        return handleExceptionInternal(ex, messageSource.getMessage("regError",
                null, request.getLocale()), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ MailAuthenticationException.class })
    public ResponseEntity<Object> handleMail(RuntimeException ex, WebRequest request) {
        logger.error("500 Status Code", ex);

        return handleExceptionInternal(ex, messageSource.getMessage("emailConfigError",
                null, request.getLocale()), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ BadCredentialsException.class })
    public ResponseEntity<Object> handleMail(BadCredentialsException ex, WebRequest request) {
        logger.error("403 Status Code", ex);

        return handleExceptionInternal(ex, messageSource.getMessage("badCredentialsError",
                null, request.getLocale()), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ DisabledException.class })
    public ResponseEntity<Object> handleBindException(DisabledException ex, WebRequest request) {
        logger.error("400 Status Code", ex);

        return handleExceptionInternal(ex, messageSource.getMessage("emailUnconfirmedError", null, request.getLocale()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({ ExecutionException.class })
    public ResponseEntity<Object> handleMail(ExecutionException ex, WebRequest request) {
        logger.error("500 Status Code", ex);

        return handleExceptionInternal(ex, messageSource.getMessage("firebaseExecption",
                null, request.getLocale()), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ InterruptedException.class })
    public ResponseEntity<Object> handleMail(InterruptedException ex, WebRequest request) {
        logger.error("500 Status Code", ex);

        return handleExceptionInternal(ex, messageSource.getMessage("firebaseExecption",
                null, request.getLocale()), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ ExpiredJwtException.class })
    public ResponseEntity<Object> handleMail(ExpiredJwtException ex, WebRequest request) {
        logger.error("401 Status Code", ex);

        return handleExceptionInternal(ex, messageSource.getMessage("tokenExpiredError",
                null, request.getLocale()), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }
}
