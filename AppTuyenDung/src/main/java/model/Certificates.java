package model;

import constant.ScoreType;

public class Certificates extends BaseEntity{
    private String certificatesName;
    private ScoreType scoreType;

    public Certificates(String certificatesName, ScoreType scoreType) {
        this.certificatesName = certificatesName;
        this.scoreType = scoreType;
    }

    public Certificates(int id, String certificatesName, ScoreType scoreType) {
        super(id);
        this.certificatesName = certificatesName;
        this.scoreType = scoreType;
    }

    public String getCertificatesName() {
        return certificatesName;
    }

    public void setCertificatesName(String certificatesName) {
        this.certificatesName = certificatesName;
    }

    public ScoreType getScoreType() {
        return scoreType;
    }

    public void setScoreType(ScoreType scoreType) {
        this.scoreType = scoreType;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
