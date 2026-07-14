package dto;

public class CandidateDTO {


        private int candidateId;
        private int userId;
        private String username;
        private String fullName;
        private String avatarUrl;
        private String email;
        private String phoneNumber;
        private String address;
        private String dateOfBirth;
        private String role;
        private Double desiredMinSalary;
        private Double desiredMaxSalary;


    public int getCandidateId() {
        return candidateId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Double getDesiredMinSalary() {
        return desiredMinSalary;
    }

    public void setDesiredMinSalary(Double desiredMinSalary) {
        this.desiredMinSalary = desiredMinSalary;
    }

    public Double getDesiredMaxSalary() {
        return desiredMaxSalary;
    }

    public void setDesiredMaxSalary(Double desiredMaxSalary) {
        this.desiredMaxSalary = desiredMaxSalary;
    }
}
