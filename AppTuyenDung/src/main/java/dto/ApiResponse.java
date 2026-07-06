package dto;

public class ApiResponse<T> {
    private boolean success; // true nếu thành công, false nếu thất bại
    private String message;  // Lời nhắn (Ví dụ: "Lấy dữ liệu thành công", "Sai mật khẩu")
    private T data;          // Dữ liệu trả về (có thể là User, List<User>, hoặc null nếu lỗi)

    // Constructor cho trường hợp THÀNH CÔNG (Có dữ liệu)
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Constructor cho trường hợp THẤT BẠI hoặc thông báo ngắn (Không có dữ liệu)
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }

    // Cần phải có Getters/Setters để Gson chuyển thành JSON
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
