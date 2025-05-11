package az.nicat.projects.resumejobmatchingapp.exception.password;


import az.nicat.projects.resumejobmatchingapp.exception.handler.ErrorCodes;

public class InvalidPasswordException extends RuntimeException {

    public final ErrorCodes errorCode;
    public final transient Object[] arguments;

    public InvalidPasswordException(ErrorCodes errorCode, Object... args) {
        this.errorCode = errorCode;
        this.arguments = args == null ? new Object[0] : args;
    }
}
