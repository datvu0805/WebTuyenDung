package service;

import dao.CandidateEducationDAO;
import dto.CandidateEducationDTO;
import mapper.CandidateEducationMapper;
import model.CandidateEducation;
import validator.CandidateEducationValidator;

import java.util.List;
import java.util.stream.Collectors;

public class CandidateEducationService {

    private final CandidateEducationDAO candidateEducationDAO = new CandidateEducationDAO();

    public void add(CandidateEducationDTO dto) {

        CandidateEducationValidator.validate(dto);

        CandidateEducation entity = CandidateEducationMapper.toEntity(dto);

        candidateEducationDAO.add(entity);

        dto.setId(entity.getId());
    }

    public void update(CandidateEducationDTO dto) {

        CandidateEducationValidator.validate(dto);

        if (dto.getId() <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ.");
        }

        CandidateEducation entity = CandidateEducationMapper.toEntity(dto);

        candidateEducationDAO.update(entity);
    }

    public void delete(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID không hợp lệ.");
        }

        candidateEducationDAO.delete(id);
    }

    public CandidateEducationDTO getById(int id) {

        CandidateEducation entity = candidateEducationDAO.getById(id);

        return CandidateEducationMapper.toDTO(entity);
    }

    public List<CandidateEducationDTO> getByCandidateId(int candidateId) {

        return candidateEducationDAO.getByCandidateId(candidateId)
                .stream()
                .map(CandidateEducationMapper::toDTO)
                .collect(Collectors.toList());
    }
}
