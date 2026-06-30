package com.sam.utility;

import com.sam.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Locale;

@RestControllerAdvice
public class SecurityLevelException {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e, HttpServletRequest request)
    {
        ErrorResponse errorResponse =
             new ErrorResponse(
                     LocalDateTime.now(),
                     HttpStatus.FORBIDDEN.value(),
                     "ACCESS_DENIED",
                     e.getMessage(),
                     request.getContextPath()
             );

        return new ResponseEntity<>(errorResponse,HttpStatus.FORBIDDEN);
    }
}
