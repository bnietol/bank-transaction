package com.nietol.tcs.transaction.exception;

public class TcsException extends RuntimeException {

  private String errorCode;
  private String description;

  public TcsException() {
    super();
  }

  public TcsException(final Throwable throwable) {
    super(throwable);
  }

  public TcsException(final String message) {
    super(message);
  }

  public TcsException(final String message, final Throwable throwable) {
    super(message, throwable);
  }

  public TcsException(final String errorCode, final String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public TcsException(final String errorCode, final String message, final String description) {
    super(message);
    this.errorCode = errorCode;
    this.description = description;
  }

  public TcsException(final String errorCode, final String message, final Throwable throwable) {
    super(message, throwable);
    this.errorCode = errorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(final String errorCode) {
    this.errorCode = errorCode;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

}
