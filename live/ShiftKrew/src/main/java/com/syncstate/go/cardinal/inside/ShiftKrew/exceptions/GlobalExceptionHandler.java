package com.syncstate.go.cardinal.inside.ShiftKrew.exceptions;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.responses.AutoGraphResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InstanceExistsException.class)
    public ResponseEntity<AutoGraphResponse> handleInstanceExistsException(InstanceExistsException ex)
    {
        AutoGraphResponse autoGraphResponse = new AutoGraphResponse();
        autoGraphResponse.setStatus(409);
        autoGraphResponse.setStatusMessage(ex.getMessage());
        autoGraphResponse.setResponseData(null);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(autoGraphResponse);
    }
}
