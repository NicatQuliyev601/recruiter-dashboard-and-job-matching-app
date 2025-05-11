package az.nicat.projects.resumejobmatchingapp.exception.schedule;


import az.nicat.projects.resumejobmatchingapp.exception.handler.ErrorCodes;

public class ScheduleNotFoundException extends RuntimeException {

    public final ErrorCodes errorCode;
    public final transient Object[] arguments;

    public ScheduleNotFoundException(ErrorCodes errorCode, Object... args) {
        this.errorCode = errorCode;
        this.arguments = args == null ? new Object[0] : args;
    }
}
