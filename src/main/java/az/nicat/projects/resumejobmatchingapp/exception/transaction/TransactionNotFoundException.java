package az.nicat.projects.resumejobmatchingapp.exception.transaction;


import az.nicat.projects.resumejobmatchingapp.exception.handler.ErrorCodes;

public class TransactionNotFoundException extends RuntimeException {

    public final ErrorCodes errorCode;
    public final transient Object[] arguments;

    public TransactionNotFoundException(ErrorCodes errorCode, Object... args) {
        this.errorCode = errorCode;
        this.arguments = args == null ? new Object[0] : args;
    }
}
