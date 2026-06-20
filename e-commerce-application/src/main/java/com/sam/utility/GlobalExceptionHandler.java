package com.sam.utility;

import com.sam.dto.ErrorResponse;
import com.sam.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
//@Aspect
public class GlobalExceptionHandler {

    @ExceptionHandler({UserNotFoundException.class,
                      OrderNotFoundException.class,
                      ProductNotFoundException.class,
                      AddressNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException e,
                                                            HttpServletRequest request)
    {
         ErrorResponse errorResponse = new ErrorResponse(
                 LocalDateTime.now(),
                 HttpStatus.NOT_FOUND.value(),
                 "NOT_FOUND",
                 e.getMessage(),
                 request.getRequestURI()
         );
       return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> generalExceptionMessages(Exception e,
                                                                  HttpServletRequest request)
    {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                e.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidActionException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(InvalidActionException e,HttpServletRequest request)
    {
        ErrorResponse errorResponse =
                new ErrorResponse(LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "BAD_REQUEST",
                        e.getMessage(),
                        request.getRequestURI()
                        );
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e,HttpServletRequest request)
    {
        String message = "Invalid Input";

        if(e.getRequiredType()!=null && e.getRequiredType().isEnum())
        {
            String validValues = Arrays.stream(e.getRequiredType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(","));

            message = String.format(
                    "Invalid value '%s' for '%s'. Valid values are: [%s]",
                    e.getValue(),
                    e.getName(),
                    validValues
            );
        }

        ErrorResponse errorResponse =
                new ErrorResponse(LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "BAD_REQUEST",
                        message,
                        request.getRequestURI()
                );
        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleStock(InsufficientStockException e , HttpServletRequest request)
    {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NO_CONTENT.value(),
                "NO_CONTENT",
                e.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse,HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleValidationLogic(MethodArgumentNotValidException e,HttpServletRequest request)
    {
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_FAILED",
                errorMessage,
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
    }
}
