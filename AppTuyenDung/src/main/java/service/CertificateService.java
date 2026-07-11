package service;

import constant.ScoreType;
import dao.CertificateDAO;
import dto.CertificateDTO;
import mapper.CertificateMapper;
import model.Certificate;
import validator.CertificateValidator;

import java.util.List;
import java.util.stream.Collectors;

public class CertificateService {
    private final CertificateDAO certificateDAO = new CertificateDAO();


    public void add(CertificateDTO dto) {

        CertificateValidator.validate(dto);

        Certificate certificate = new Certificate(
                dto.getCertificateName(),
                ScoreType.valueOf(dto.getScoreType().toUpperCase())
        );

        certificateDAO.add(certificate);

        // Đồng bộ id vừa được DB sinh ra vào DTO
        dto.setId(certificate.getId());
    }


    public void update(CertificateDTO dto) {

        CertificateValidator.validate(dto);

        Certificate certificate = new Certificate(dto.getId(), dto.getCertificateName(), ScoreType.valueOf(dto.getScoreType().toUpperCase()));

        certificateDAO.update(certificate);
    }


    public void delete(int id) {

        certificateDAO.delete(id);

    }


    public CertificateDTO getById(int id) {

        return CertificateMapper.toDTO(certificateDAO.getById(id));

    }


    public List<CertificateDTO> getAll() {

        return certificateDAO.getAll().stream().map(CertificateMapper::toDTO).collect(Collectors.toList());

    }
}
