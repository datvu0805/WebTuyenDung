package model;

public class Skill extends BaseEntity{
    private String skillName;

    public Skill(String skillName) {
        this.skillName = skillName;
    }

    public Skill(int id, String skillName) {
        super(id);
        this.skillName = skillName;
    }

    public Skill() {

    }
    public Skill(int id) {
        super(id);
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
