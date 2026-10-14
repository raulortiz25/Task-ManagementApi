package com.TaskManagementApi.Exception;

import com.TaskManagementApi.Exception.Custom.BaseException;
import com.TaskManagementApi.Exception.DtoException.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex, HttpServletRequest request){

       log.error("Excepcion ocurrida: {} - path: {}", ex.getMessage(), request.getRequestURI());

        HttpStatus status = ex.getStatus();

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()

        );

        return new ResponseEntity<>(errorResponse, status);

    }

    //Captura errores tipo valid en los dto
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request){

        log.warn("Error de validacion en la peticion: {}", ex.getMessage(), request.getRequestURI());

        Map<String,String> errors = new HashMap<>();

        for(FieldError fieldError:ex.getBindingResult().getFieldErrors()){
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "Error de validacion en los datos de entrada",
                errors,
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, status);

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse>handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request){
        log.warn("Intento de inicio de sesion fallido en: {}", request.getRequestURI());

        HttpStatus status = HttpStatus.BAD_REQUEST;

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, status);
    }

    //Captura cualquier error no controlado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(BaseException ex, HttpServletRequest request){

        log.error("Error no controlado en la aplicacion: ", ex);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "Ocurrio un error interno e inseperado en el servidor",
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, status);

    }
}
