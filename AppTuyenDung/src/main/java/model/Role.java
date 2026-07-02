package model;

public class  Role extends BaseEntity {

    private String roleName;
    private String description;

    public Role() {
    }

    @Override
    public String getInfo() {
        return "";
    }

    public Role(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}