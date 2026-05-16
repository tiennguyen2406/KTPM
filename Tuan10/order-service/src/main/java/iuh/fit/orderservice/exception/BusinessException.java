package iuh.fit.orderservice.exception;

/**
 * Custom business exception that should NOT trigger Circuit Breaker or Retry.
 * These are expected business-level errors (e.g., validation failures, not found).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
