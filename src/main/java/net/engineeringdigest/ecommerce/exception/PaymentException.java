package net.engineeringdigest.ecommerce.exception;

public class PaymentException extends RuntimeException {
    
    private final String transactionId;
    
    public PaymentException(String message) {
        super(message);
        this.transactionId = null;
    }
    
    public PaymentException(String message, String transactionId) {
        super(message);
        this.transactionId = transactionId;
    }
    
    public PaymentException(String message, String transactionId, Throwable cause) {
        super(message, cause);
        this.transactionId = transactionId;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
}
