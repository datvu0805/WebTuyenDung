package service;

import dao.EducationLevelDAO;
import dto.EducationLevelDTO;
import mapper.EducationLevelMapper;
import model.EducationLevel;

import java.util.ArrayList;
import java.util.List;

public class EducationLevelService {

    private final EducationLevelDAO educationLevelDAO = new EducationLevelDAO();

    public void addEducationLevel(EducationLevelDTO dto) {

        if (dto.getLevelName() == null || dto.getLevelName().isBlank()) {
            throw new IllegalArgumentException("Tên trình độ học vấn không được để trống.");
        }

        EducationLevel entity = EducationLevelMapper.toEntity(dto);

        educationLevelDAO.add(entity);

        dto.setId(entity.getId());
    }

    public void updateEducationLevel(int id, EducationLevelDTO dto) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ");
        }

        if (dto.getLevelName() == null || dto.getLevelName().isBlank()) {
            throw new IllegalArgumentException("Tên trình độ học vấn không được để trống.");
        }

        EducationLevel entity = EducationLevelMapper.toEntity(dto);
        entity.setId(id);

        educationLevelDAO.update(entity);
    }

    public void deleteEducationLevel(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ");
        }

        educationLevelDAO.delete(id);
    }

    public EducationLevelDTO getEducationLevelById(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ");
        }

        return EducationLevelMapper.toDTO(educationLevelDAO.getById(id));
    }

    public List<EducationLevelDTO> getAllEducationLevels() {

        List<EducationLevelDTO> list = new ArrayList<>();

        for (EducationLevel entity : educationLevelDAO.getAll()) {
            list.add(EducationLevelMapper.toDTO(entity));
        }

        return list;
    }
}
