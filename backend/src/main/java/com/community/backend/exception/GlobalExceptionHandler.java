package com.community.backend.exception;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.persistence.OptimisticLockException;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public Map<String,String> handleResourceNotFoundException(ResourceNotFoundException exception) {
        return error(exception.getMessage());
    }
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({UnauthorizedException.class,AccessDeniedException.class})
    public Map<String,String> handleUnauthorizedException(RuntimeException exception) {
        return error(exception.getMessage());
    }
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({IllegalStateException.class,ObjectOptimisticLockingFailureException.class,OptimisticLockException.class,DataIntegrityViolationException.class})
    public Map<String,String> handleConflict(RuntimeException exception) {
        if(exception instanceof ObjectOptimisticLockingFailureException || exception instanceof OptimisticLockException) return error("This resource was changed by another operation. Refresh and try again");
        if(exception instanceof DataIntegrityViolationException) return error("The operation conflicts with existing data");
        return error(exception.getMessage());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class})
    public Map<String,String> handleBadRequest(IllegalArgumentException exception) {
        return error(exception.getMessage());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleValidation(MethodArgumentNotValidException exception) {
        String message=exception.getBindingResult().getFieldErrors().stream().findFirst().map(fieldError->fieldError.getField()+": "+fieldError.getDefaultMessage()).orElse("Request validation failed");
        return error(message);
    }
    private Map<String,String> error(String message) {
        Map<String,String> body=new LinkedHashMap<>();
        body.put("error",message==null || message.isBlank()?"Request failed":message);
        return body;
    }
}
