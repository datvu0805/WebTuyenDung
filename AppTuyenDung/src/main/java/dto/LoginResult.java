package dto;

import model.Users;

public class LoginResult {

    private final boolean success;
    private final String message;
    private final Users user;
    private final long retryAfterSeconds;

    private LoginResult(
            boolean success,
            String message,
            Users user,
            long retryAfterSeconds
    ) {
        this.success = success;
        this.message = message;
        this.user = user;
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public static LoginResult success(Users user) {
        return new LoginResult(
                true,
                "Đăng nhập thành công",
                user,
                0
        );
    }

    public static LoginResult failure(String message) {
        return new LoginResult(
                false,
                message,
                null,
                0
        );
    }

    public static LoginResult locked(long seconds) {
        return new LoginResult(
                false,
                "Bạn đã nhập sai quá 5 lần. Vui lòng thử lại sau "
                        + seconds + " giây",
                null,
                seconds
        );
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Users getUser() {
        return user;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}