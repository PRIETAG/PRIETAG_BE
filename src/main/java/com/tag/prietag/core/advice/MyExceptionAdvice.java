package com.tag.prietag.core.advice;

import com.tag.prietag.core.annotation.MyErrorLog;
import com.tag.prietag.core.exception.*;
import com.tag.prietag.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class MyExceptionAdvice {

    @MyErrorLog
    @ExceptionHandler(Exception400.class)
    public ResponseEntity<?> badRequest(Exception400 e){
        return new ResponseEntity<>(e.body(), e.status());
    }

    @MyErrorLog
    @ExceptionHandler(Exception401.class)
    public ResponseEntity<?> unAuthorized(Exception401 e){
        return new ResponseEntity<>(e.body(), e.status());
    }

    @MyErrorLog
    @ExceptionHandler(Exception403.class)
    public ResponseEntity<?> forbidden(Exception403 e){
        return new ResponseEntity<>(e.body(), e.status());
    }

    @MyErrorLog
    @ExceptionHandler(Exception404.class)
    public ResponseEntity<?> notFound(Exception404 e){
        return new ResponseEntity<>(e.body(), e.status());
    }

    @MyErrorLog
    @ExceptionHandler(Exception500.class)
    public ResponseEntity<?> serverError(Exception500 e){
        return new ResponseEntity<>(e.body(), e.status());
    }

    @MyErrorLog
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> unknownServerError(Exception e){
        ResponseDTO<String> responseDTO = new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR, "unknownServerError", e.getMessage());
        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
