package com.algaworks.algalog.api.exceptionhandler;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.algaworks.algalog.domain.exception.DomainException;
import com.algaworks.algalog.domain.exception.EntityNotFoundException;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private MessageSource messageSource;

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<StandardError.Field> fields = new ArrayList<>();
		for (ObjectError objError : ex.getBindingResult().getAllErrors()) {
			String name = ((FieldError) objError).getField();
			// LocaleContextHolder muda a linguagem da mensagem de acordo com a região
			String message = messageSource.getMessage(objError, LocaleContextHolder.getLocale());
			fields.add(new StandardError.Field(name, message));
		}
		StandardError errors = new StandardError();
		errors.setStatus(status.value());
		errors.setDateTime(OffsetDateTime.now());
		errors.setTitle("Um ou mais campos estão inválidos");
		errors.setFields(fields);
		return handleExceptionInternal(ex, errors, headers, status, request);
	}

	@ExceptionHandler(DomainException.class)
	public ResponseEntity<Object> handleDomainException(DomainException ex, WebRequest request) {
		HttpStatus status = HttpStatus.BAD_REQUEST;

		StandardError error = new StandardError();
		error.setStatus(status.value());
		error.setDateTime(OffsetDateTime.now());
		error.setTitle(ex.getMessage());

		return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
		HttpStatus status = HttpStatus.NOT_FOUND;

		StandardError error = new StandardError();
		error.setStatus(status.value());
		error.setDateTime(OffsetDateTime.now());
		error.setTitle(ex.getMessage());

		return handleExceptionInternal(ex, error, new HttpHeaders(), status, request);
	}
}
