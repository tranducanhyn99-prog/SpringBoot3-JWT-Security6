package vn.iotstar.exceptions;

import com.nimbusds.jose.JOSEException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.ParseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception exception) {
        ProblemDetail errorDetail;

        if (exception instanceof BadCredentialsException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            errorDetail.setProperty("description", "The username or password is incorrect");
            return errorDetail;
        }

        if (exception instanceof AccountStatusException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The account is locked");
            return errorDetail;
        }

        if (exception instanceof AccessDeniedException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "You are not authorized to access this resource");
            return errorDetail;
        }

        if (exception instanceof JOSEException || (exception.getMessage() != null && exception.getMessage().toLowerCase().contains("signature"))) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            errorDetail.setProperty("description", "The JWT signature is invalid");
            return errorDetail;
        }

        if (exception instanceof ParseException || (exception.getMessage() != null && exception.getMessage().toLowerCase().contains("expired"))) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            errorDetail.setProperty("description", "The JWT token has expired or is invalid");
            return errorDetail;
        }

        String message = exception.getMessage() != null ? exception.getMessage() : "Unknown internal server error.";
        errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), message);
        errorDetail.setProperty("description", "Unknown internal server error.");

        return errorDetail;
    }
}
