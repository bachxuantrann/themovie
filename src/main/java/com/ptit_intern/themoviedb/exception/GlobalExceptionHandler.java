package com.ptit_intern.themoviedb.exception;

import com.ptit_intern.themoviedb.dto.response.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {
            IdInvalidExceptions.class,
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            UserExisted.class,
            InvalidExceptions.class,
            AccessDeniedException.class,
            HttpMediaTypeNotSupportedException.class
    })
    public ResponseEntity<RestResponse<Object>> handleInvalidExceptions(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getMessage());
        res.setMessage("Invalid !");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<RestResponse<Object>> handleVailidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).collect(Collectors.toUnmodifiableList());
        res.setMessage(errors.size() > 1 ? errors : errors.get(0));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<RestResponse<Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        res.setError(ex.getMessage());
        res.setMessage("HTTP Method Not Allowed");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(res);
    }

    @ExceptionHandler(value = {
            NoResourceFoundException.class
    })
    public ResponseEntity<RestResponse<Object>> handleResourceNotFoundException(NoResourceFoundException ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError(ex.getMessage());
        res.setMessage("404... Resource not found... ");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }
    @ExceptionHandler(IOException.class)
    public ResponseEntity<RestResponse<Object>> handleIOException(IOException ex) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setError(ex.getMessage());
        res.setMessage("Lỗi xử lý file hoặc upload ảnh.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RestResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getMessage());
        res.setMessage("Lỗi dữ liệu đầu vào.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RestResponse<Object>> handleRuntimeException(RuntimeException ex) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setError(ex.getMessage());
        res.setMessage("Lỗi hệ thống.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

}
