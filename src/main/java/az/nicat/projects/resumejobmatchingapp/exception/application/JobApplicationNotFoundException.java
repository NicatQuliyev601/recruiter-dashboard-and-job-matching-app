package az.nicat.projects.resumejobmatchingapp.exception.application;


import az.nicat.projects.resumejobmatchingapp.exception.handler.ErrorCodes;

public class JobApplicationNotFoundException extends RuntimeException {

    public final ErrorCodes errorCode;
    public final transient Object[] arguments;

    public JobApplicationNotFoundException(ErrorCodes errorCode, Object... args) {
        this.errorCode = errorCode;
        this.arguments = args == null ? new Object[0] : args;
    }
}
