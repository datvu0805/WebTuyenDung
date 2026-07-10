package dto;

public class CertificateDTO {

    private int id;
    private String certificateName;
    private String scoreType;

    public CertificateDTO() {
    }

    public CertificateDTO(int id, String certificateName, String scoreType) {
        this.id = id;
        this.certificateName = certificateName;
        this.scoreType = scoreType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCertificateName() {
        return certificateName;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }
}