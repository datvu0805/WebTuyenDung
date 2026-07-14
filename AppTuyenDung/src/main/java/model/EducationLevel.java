package model;

// Bảng danh mục trình độ học vấn (THPT, Đại học, Thạc sĩ...)
public class EducationLevel extends BaseEntity {

    private String levelName;

    public EducationLevel() {
    }

    public EducationLevel(int id) {
        super(id);
    }

    public EducationLevel(String levelName) {
        this.levelName = levelName;
    }

    public EducationLevel(int id, String levelName) {
        super(id);
        this.levelName = levelName;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
