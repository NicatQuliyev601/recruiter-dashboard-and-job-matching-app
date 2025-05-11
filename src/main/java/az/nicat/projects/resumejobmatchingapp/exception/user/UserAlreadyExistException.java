package az.nicat.projects.resumejobmatchingapp.exception.user;


import az.nicat.projects.resumejobmatchingapp.exception.handler.ErrorCodes;

public class UserAlreadyExistException extends RuntimeException {

    public final ErrorCodes errorCode;
    public final transient Object[] arguments;

    public UserAlreadyExistException(ErrorCodes errorCode, Object... args) {
        this.errorCode = errorCode;
        this.arguments = args == null ? new Object[0] : args;
    }
}
