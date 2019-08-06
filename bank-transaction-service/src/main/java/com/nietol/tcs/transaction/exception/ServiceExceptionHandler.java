package com.nietol.tcs.transaction.exception;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javassist.NotFoundException;

@ControllerAdvice
public class ServiceExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceExceptionHandler.class);

  // ****
  // General unhandled exceptions
  @ExceptionHandler(Throwable.class)
  protected ResponseEntity<Object> internalExceptionHandler(final Throwable ex) {
    LOGGER.error("Unexpected service error: " + ex.getMessage(), ex);
    final Error error = new Error().code(CommonErrorCodes.GENERIC_INTERNAL_ERROR)
        .message(String.format("Unexpected service error: %s: %s", ex, ex.getMessage()));
    return createInternalErrorResponse(error);
  }

  // ****
  // Request/Data validation exceptions

  // Invalid request parameter SCP custom error
  @ExceptionHandler({InvalidRequestParameterException.class})
  protected ResponseEntity<Object> invalidRequestParameterExceptionHandler(final TcsException ex) {
    LOGGER.error("Invalid parameter error: " + ex.getMessage(), ex);
    final Error error = new Error().code(ex.getErrorCode()).message(ex.getMessage());
    return createBadRequestResponse(error);
  }

  // Not Found SCP custom error
  @ExceptionHandler(NotFoundException.class)
  protected ResponseEntity<Object> notFoundHandler(final RuntimeException ex) {
    LOGGER.error("Not found error: " + ex.getMessage(), ex);
    final Error error =
        new Error().code(CommonErrorCodes.GENERIC_RESOURCE_NOT_FOUND).message(ex.getMessage());
    return createNotFoundResponse(error);
  }

  // Missing request parameter
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    LOGGER.error("Request Validation error - Missing parameter: " + ex.getMessage(), ex);
    final Error error = new Error().code(CommonErrorCodes.GENERIC_PARAM_MUST_NOT_BE_NULL)
        .message("Validation error: " + ex.getMessage());
    return createBadRequestResponse(error);
  }

  // @Valid validation errors
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    LOGGER.error("Request Validation error: " + ex.getMessage(), ex);
    final Error error = new Error().code(CommonErrorCodes.GENERIC_INVALID_PARAM_VALUE)
        .message("Validation error: " + ex.getMessage());
    return createBadRequestResponse(error);
  }


  // Parameter type mismatch
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    LOGGER.error("Request validation error - Type mismatch on parameter '" + ex.getName() + "': "
        + ex.getMessage(), ex);
    final Error error = new Error().code(CommonErrorCodes.GENERIC_INVALID_PARAM_VALUE).message(
        "Validation error: Type mismatch on parameter '" + ex.getName() + "': " + ex.getMessage());
    return createBadRequestResponse(error);
  }

  // Malformed/Unreadable Request
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    LOGGER.error("Malformed request error: " + ex.getMessage(), ex);
    final Error error = new Error().code(CommonErrorCodes.GENERIC_INVALID_PARAM_VALUE)
        .message("Malformed/Unreadable request: " + ex.getMessage());
    return createBadRequestResponse(error);
  }

  // Invalid Media type/Invalid JSON request value
  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    LOGGER.error("Media Type not supported error: " + ex.getMessage(), ex);
    final Error error = new Error().code(CommonErrorCodes.GENERIC_INVALID_PARAM_VALUE)
        .message("Media Type not supported: " + ex.getMessage());
    return createResponse(error, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  // Not acceptable Media type
  @Override
  protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
      HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    LOGGER.error("Media Type not acceptable error: " + ex.getMessage(), ex);
    final Error error = new Error().code(CommonErrorCodes.GENERIC_INVALID_PARAM_VALUE)
        .message("Media Type not acceptable: " + ex.getMessage());
    return createResponse(error, HttpStatus.NOT_ACCEPTABLE);
  }

  // Invalid HTTP Method
  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    LOGGER.error("HTTP Method not allowed error: " + ex.getMessage(), ex);
    final Error error = new Error().code(CommonErrorCodes.GENERIC_INVALID_PARAM_VALUE).message(
        String.format("HTTP Method not allowed for this operation: %s. Allowed methods: %s",
            ex.getMessage(), ex.getSupportedHttpMethods()));
    return createResponse(error, HttpStatus.METHOD_NOT_ALLOWED);
  }

  // ****
  // Data access exceptions

  // General DAO Exceptions
  @ExceptionHandler({SQLException.class, DataAccessException.class})
  protected ResponseEntity<Object> daoExceptionHandler(final Exception ex) {
    LOGGER.error("Error accessing SQL data: " + ex.getMessage(), ex);
    return handleDaoException(ex);
  }

  private ResponseEntity<Object> handleDaoException(final Exception ex) {

    final Error error = new Error();

    // Expected resource not found
    if (ex instanceof EmptyResultDataAccessException) {
      error.code(CommonErrorCodes.GENERIC_RESOURCE_NOT_FOUND)
          .message("Resource not found error accessing database: " + ex.getMessage());
      return createNotFoundResponse(error);
    }

    // Integrity constraint violation (missing primary keys, missing non-nullables...)
    else if (ex instanceof DataIntegrityViolationException) {
      error.code(CommonErrorCodes.DAO_INTEGRITY_CONSTRAINT_ERROR)
          .message("Integrity data error accessing database: " + ex.getMessage());
      return createBadRequestResponse(error);
    }

    // Unknown Data access error
    error.code(CommonErrorCodes.DAO_GENERIC_DATA_ACCESS_ERROR)
        .message("Error accessing database: " + ex.getMessage());
    return createInternalErrorResponse(error);
  }

  private ResponseEntity<Object> createResponse(final Error error, final HttpStatus httpStatus) {
    return new ResponseEntity<>(error, createJsonContentTypeHeader(), httpStatus);
  }

  private ResponseEntity<Object> createBadRequestResponse(final Error error) {
    return createResponse(error, HttpStatus.BAD_REQUEST);
  }

  private ResponseEntity<Object> createNotFoundResponse(final Error error) {
    return createResponse(error, HttpStatus.NOT_FOUND);
  }

  private ResponseEntity<Object> createInternalErrorResponse(final Error error) {
    return createResponse(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private HttpHeaders createJsonContentTypeHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return headers;
  }

}
