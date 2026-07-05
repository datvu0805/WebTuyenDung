package validator;

public class UserValidator {
    public boolean isEmpty(String value){
        return value == null || value.trim().isEmpty();
    }


    public boolean isValidUsername(String username){
        if(isEmpty(username)){

            return false;
        }
        if(username.length() < 4 ||  username.length() > 20){
            return false;
        }

        return true;
    }

    public boolean isValidPassword(String password){
        if (isEmpty(password)){
            return false;
        }
        if (password.length() < 6 || password.length() > 30){
            return false;
        }
        return true;
    }
    public boolean isValidEmail(String email){
        if (isEmpty(email)){
            return false;
        }
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(regex)) {
            return false;
        }
        return true;
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        if (isEmpty(phoneNumber)) {
            return false;
        }

        String regex = "^(0|\\+84)[0-9]{9}$";
        if (!phoneNumber.matches(regex)) {
            return false;
        }
        return true;
    }

    public String validRegister(String username, String password, String email, String phone) {
        if(!isValidUsername(username)){
            return "Tên đăng nhập phải từ 4 đến 20 k tự";
        }
        if(!isValidPassword(password)){
            return "Mật  khẩu từ 6  đến 30 ký tự";
        }
        if(!isValidEmail(email)){
            return "Email không hợp lệ";
        }
        if (!isValidPhoneNumber(phone)) {
            return "Số điện thoạt không hợp  lệ";
        }
        return null;
    }
}
