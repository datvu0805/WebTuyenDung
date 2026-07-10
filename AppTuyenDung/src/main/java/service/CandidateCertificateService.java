package service;

import dao.CandidateCertificateDAO;
import dto.CandidateCertificateDTO;
import mapper.CandidateCertificateMapper;
import model.CandidateCertificate;
import model.Candidates;
import model.Certificate;
import validator.CandidateCertificateValidator;

import java.util.List;
import java.util.stream.Collectors;

public class CandidateCertificateService {

    private final CandidateCertificateDAO candidateCertificateDAO = new CandidateCertificateDAO();

    public void add(CandidateCertificateDTO dto) {

        CandidateCertificateValidator.validate(dto);

        CandidateCertificate entity = new CandidateCertificate(
                new Candidates(dto.getCandidateId()),
                new Certificate(dto.getCertificateId()),
                dto.getScore(),
                dto.getIssueDate(),
                dto.getExpiryDate(),
                dto.getDescription()
        );

        candidateCertificateDAO.add(entity);
    }

    public void update(CandidateCertificateDTO dto) {

        CandidateCertificateValidator.validate(dto);

        CandidateCertificate entity = new CandidateCertificate(
                dto.getId(),
                new Candidates(dto.getCandidateId()),
                new Certificate(dto.getCertificateId()),
                dto.getScore(),
                dto.getIssueDate(),
                dto.getExpiryDate(),
                dto.getDescription()
        );

        candidateCertificateDAO.update(entity);
    }

    public void delete(int id) {
        candidateCertificateDAO.delete(id);
    }

    public CandidateCertificateDTO getById(int id) {

        CandidateCertificate entity = candidateCertificateDAO.getById(id);

        if (entity == null) {
            return null;
        }

        return CandidateCertificateMapper.toDTO(entity);
    }

    public List<CandidateCertificateDTO> getAll() {

        return candidateCertificateDAO.getAll()
                .stream()
                .map(CandidateCertificateMapper::toDTO)
                .collect(Collectors.toList());
    }
}