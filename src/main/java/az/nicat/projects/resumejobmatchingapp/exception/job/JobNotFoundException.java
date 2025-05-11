package az.nicat.projects.resumejobmatchingapp.exception.job;


import az.nicat.projects.resumejobmatchingapp.exception.handler.ErrorCodes;

public class JobNotFoundException extends RuntimeException {

    public final ErrorCodes errorCode;
    public final transient Object[] arguments;

    public JobNotFoundException(ErrorCodes errorCode, Object... args) {
        this.errorCode = errorCode;
        this.arguments = args == null ? new Object[0] : args;
    }
}
