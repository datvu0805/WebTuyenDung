package model;

public class Skills extends BaseEntity{
    private String skillName;

    public Skills(String skillName) {
        this.skillName = skillName;
    }

    public Skills(int id, String skillName) {
        super(id);
        this.skillName = skillName;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
