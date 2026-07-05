package model;

import java.time.LocalDate;

public class Users extends BaseEntity{
        private String username;
        private String password;
        private String fullName;
        private String avatarUrl;
        private String email;
        private LocalDate dateOfBirth;
        private String phoneNumber;
        private String address;
        private Role role;

        public Users() {
        }

    @Override
    public String getInfo() {
        return "";
    }

    public Users(String username, String password, String fullName,
                     String avatarUrl, String email, LocalDate dateOfBirth,
                     String phoneNumber, String address, Role role) {

            this.username = username;
            this.password = password;
            this.fullName = fullName;
            this.avatarUrl = avatarUrl;
            this.email = email;
            this.dateOfBirth = dateOfBirth;
            this.phoneNumber = phoneNumber;
            this.address = address;
            this.role = role;
        }
    public Users(String username, String password, String fullName,
                 String avatarUrl, String email, LocalDate dateOfBirth,
                 String phoneNumber, String address) {

        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public Users(int id, String username, String password, String fullName, String avatarUrl, String email, LocalDate dateOfBirth, String phoneNumber, String address, Role role) {
        super(id);
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
    }

    public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public LocalDate getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }
}
