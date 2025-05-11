package az.nicat.projects.resumejobmatchingapp.exception.handler;

import az.nicat.projects.resumejobmatchingapp.exception.application.AlreadyAppliedException;
import az.nicat.projects.resumejobmatchingapp.exception.application.JobApplicationNotFoundException;
import az.nicat.projects.resumejobmatchingapp.exception.job.JobNotFoundException;
import az.nicat.projects.resumejobmatchingapp.exception.password.InvalidPasswordException;
import az.nicat.projects.resumejobmatchingapp.exception.resume.ResumeNotFoundException;
import az.nicat.projects.resumejobmatchingapp.exception.schedule.ScheduleNotFoundException;
import az.nicat.projects.resumejobmatchingapp.exception.user.UserAlreadyExistException;
import az.nicat.projects.resumejobmatchingapp.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFoundException(UserNotFoundException ex, WebRequest req) {
        ex.printStackTrace();
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponseDto.setTitle("User Not Found");
        errorResponseDto.setDetails(ex.getMessage());


        return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExistException(UserAlreadyExistException ex, WebRequest req) {
        ex.printStackTrace();
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponseDto.setTitle("User Already exist Exception");
        errorResponseDto.setDetails(ex.getMessage());


        return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidPasswordException(InvalidPasswordException ex, WebRequest req) {
        ex.printStackTrace();
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponseDto.setTitle("Invalid Password Exception");
        errorResponseDto.setDetails(ex.getMessage());


        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResumeNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResumeNotFoundException(ResumeNotFoundException ex, WebRequest req) {
        ex.printStackTrace();
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponseDto.setTitle("Resume Not Found Exception");
        errorResponseDto.setDetails(ex.getMessage());


        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleJobNotFoundException(JobNotFoundException ex, WebRequest req) {
        ex.printStackTrace();
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponseDto.setTitle("Job Not Found Exception");
        errorResponseDto.setDetails(ex.getMessage());


        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JobApplicationNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleJobApplicationNotFoundException(JobApplicationNotFoundException ex, WebRequest req) {
        ex.printStackTrace();
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponseDto.setTitle("Job Application Not Found Exception");
        errorResponseDto.setDetails(ex.getMessage());


        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyAppliedException.class)
    public ResponseEntity<ErrorResponseDto> handleAlreadyAppliedException(AlreadyAppliedException ex, WebRequest req) {
        ex.printStackTrace();
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponseDto.setTitle("Job already applied");
        errorResponseDto.setDetails(ex.getMessage());


        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ScheduleNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleScheduleNotFoundException(ScheduleNotFoundException ex, WebRequest req) {
        ex.printStackTrace();
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponseDto.setTitle("Scheduled Interview not founded");
        errorResponseDto.setDetails(ex.getMessage());


        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

}
