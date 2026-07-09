package constant;

public enum JobStatus {

    RECRUITING((short)1, "Đang tuyển"),
    PAUSED((short)2, "Tạm dừng"),
    EXPIRED((short)3, "Đã hết hạn"),
    CLOSED((short)4, "Đã đóng");

    private final short value;
    private final String displayName;

    JobStatus(short value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public short getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static JobStatus fromValue(short value) {
        for (JobStatus s : values()) {
            if (s.value == value) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status không hợp lệ");
    }
}