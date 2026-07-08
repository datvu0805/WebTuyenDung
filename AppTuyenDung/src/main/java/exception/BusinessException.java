package exception;

public class BusinessException extends RuntimeException{
    public BusinessException(String message){
        super(message);
    }
        // xử lý thiếu tiền
    public static class InsufficientBalanceException extends BusinessException{
        public InsufficientBalanceException(String message){
            super(message);
        }
    }
    // đầu vào không hợp lệ từ validator

    public static class ValidationException extends BusinessException {
        public ValidationException(String message) {
            super(message);
        }

    }
}
