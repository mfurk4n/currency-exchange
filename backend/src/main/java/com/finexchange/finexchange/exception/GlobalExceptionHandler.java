package com.finexchange.finexchange.exception;

import com.finexchange.finexchange.util.CustomErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        StringBuilder errorMessageBuilder = new StringBuilder();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
            errorMessageBuilder.append(fieldName).append(": ").append(errorMessage).append(" ");
        });

        String combinedErrorMessage = errorMessageBuilder.toString().trim();
        Map<String, String> response = new HashMap<>();
        response.put("message", combinedErrorMessage);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(AuthenticatedUserNotFoundException.class)
    public ResponseEntity<?> authenticatedUserNotFoundExceptionHandler(AuthenticatedUserNotFoundException exception) {
        return CustomErrorResponse.sendError(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class, CurrencyNotFoundException.class, CustomerNotFoundException.class,
            TransactionNotFoundException.class, BalanceNotFoundException.class, ExchangeRateNotFoundException.class,
            OrderNotFoundException.class, WalletNotFoundException.class})
    public ResponseEntity<?> notFoundExceptionHandler(RuntimeException exception) {
        return CustomErrorResponse.sendError(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler({UserAlreadyExistsException.class, InvalidLoginException.class, LimitOrderPriceException.class,
            StopOrderPriceException.class, InsufficientBalanceException.class, UserAlreadyAdminException.class, CustomerAlreadyExistsException.class,
            InvalidCustomerTaxIdException.class, InvalidCustomerNationalIdException.class, WalletAlreadyExistException.class})
    public ResponseEntity<?> badRequestExceptionHandler(RuntimeException exception) {
        return CustomErrorResponse.sendError(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler({UserInvalidAuthorizationException.class, InsufficientAuthorityException.class})
    public ResponseEntity<?> forbiddenExceptionHandler(RuntimeException exception) {
        return CustomErrorResponse.sendError(HttpStatus.FORBIDDEN, exception.getMessage());
    }

}