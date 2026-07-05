package utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    // Băm mk
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    //So sánh mật khẩu
    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
