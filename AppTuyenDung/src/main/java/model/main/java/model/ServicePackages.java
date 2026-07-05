package model.main.java.model;

// gói dịch vụ VIP
public class ServicePackages extends BaseEntity {
    private ServicePackages packageName;
    private String targetAudience;
    private Double price;
    private int durationDays;
    private  String benifitType;
    private String description;

    public ServicePackages() {
    }

    public ServicePackages(ServicePackages packageName, String targetAudience, Double price, int durationDays, String benifitType, String description) {
        this.packageName = packageName;
        this.targetAudience = targetAudience;
        this.price = price;
        this.durationDays = durationDays;
        this.benifitType = benifitType;
        this.description = description;
    }

    public ServicePackages(int id, ServicePackages packageName, String targetAudience, Double price, int durationDays, String benifitType, String description) {
        super(id);
        this.packageName = packageName;
        this.targetAudience = targetAudience;
        this.price = price;
        this.durationDays = durationDays;
        this.benifitType = benifitType;
        this.description = description;
    }

    public ServicePackages getPackageName() {
        return packageName;
    }

    public void setPackageName(ServicePackages packageName) {
        this.packageName = packageName;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public String getBenifitType() {
        return benifitType;
    }

    public void setBenifitType(String benifitType) {
        this.benifitType = benifitType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getInfo() {
        return "";
    }
}
