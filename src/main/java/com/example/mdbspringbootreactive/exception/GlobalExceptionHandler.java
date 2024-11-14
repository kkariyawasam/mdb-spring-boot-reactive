package com.example.mdbspringbootreactive.exception;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.mdbspringbootreactive.entity.ResponseMessage;
import com.example.mdbspringbootreactive.enumeration.ErrorReason;
import com.example.mdbspringbootreactive.service.TxnService;

/**
 * Handles global exceptions in a centralized manner for the application.
 * Provides specific error responses for known exceptions, ensuring consistent
 * and user-friendly error messages across the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final TxnService txnService;

    public GlobalExceptionHandler(TxnService txnService) {
        this.txnService = txnService;
    }

    /**
     * Handles exceptions when an account is not found in the system.
     *
     * @param ex the AccountNotFoundException thrown
     * @return ResponseEntity with a bad request status and a custom error message
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ResponseMessage> accountNotFound(AccountNotFoundException ex) {
        return ResponseEntity.badRequest()
                .body(new ResponseMessage(ErrorReason.ACCOUNT_NOT_FOUND.name()));
    }

    /**
     * Handles exceptions when a duplicate account is encountered.
     *
     * @param ex the DuplicateKeyException thrown
     * @return ResponseEntity with a bad request status and a custom error message
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ResponseMessage> duplicateAccount(DuplicateKeyException ex) {
        return ResponseEntity.badRequest()
                .body(new ResponseMessage(ErrorReason.DUPLICATE_ACCOUNT.name()));
    }

    /**
     * Handles exceptions related to transaction processing, such as insufficient balance.
     *
     * @param ex the TransactionException thrown, which includes the failed transaction details
     * @return ResponseEntity with an unprocessable entity status and a Mono of the saved transaction
     */
    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<ResponseMessage> insufficientBalance(TransactionException ex) {
        return ResponseEntity.unprocessableEntity()
                .body(new ResponseMessage(ErrorReason.INSUFFICIENT_BALANCE.name()));
    }
}

