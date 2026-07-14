package service;

import dao.SystemSettingDAO;
import model.SystemSetting;

import java.util.List;

public class SystemSettingService {

    public static final String AI_RECOMMENDATION_ENABLED = "ai_recommendation_enabled";

    private final SystemSettingDAO systemSettingDAO = new SystemSettingDAO();

    public boolean isAiRecommendationEnabled() {
        String value = systemSettingDAO.getValue(AI_RECOMMENDATION_ENABLED);
        return "true".equalsIgnoreCase(value);
    }

    public void setAiRecommendationEnabled(boolean enabled) {
        systemSettingDAO.setValue(AI_RECOMMENDATION_ENABLED, String.valueOf(enabled));
    }

    public List<SystemSetting> getAll() {
        return systemSettingDAO.getAll();
    }

    public void setValue(String key, String value) {

        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key không được để trống");
        }

        systemSettingDAO.setValue(key, value);
    }
}
