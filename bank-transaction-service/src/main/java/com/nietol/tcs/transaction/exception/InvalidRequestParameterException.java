package com.nietol.tcs.transaction.exception;

public class InvalidRequestParameterException extends TcsException {

    public InvalidRequestParameterException() {
        super();
    }

    public InvalidRequestParameterException(final Throwable throwable) {
        super(throwable);
    }

    public InvalidRequestParameterException(final String errorCode, final String message) {
        super(errorCode, message);
    }

    public InvalidRequestParameterException(final String errorCode, final String message,
                                            final String description) {
        super(errorCode, message, description);
    }

    public InvalidRequestParameterException(final String errorCode, final String message,
                                            final Throwable throwable) {
        super(errorCode, message, throwable);
    }
}
