package az.nicat.projects.resumejobmatchingapp.exception.resume;


import az.nicat.projects.resumejobmatchingapp.exception.handler.ErrorCodes;

public class ResumeNotFoundException extends RuntimeException {

    public final ErrorCodes errorCode;
    public final transient Object[] arguments;

    public ResumeNotFoundException(ErrorCodes errorCode, Object... args) {
        this.errorCode = errorCode;
        this.arguments = args == null ? new Object[0] : args;
    }
}
