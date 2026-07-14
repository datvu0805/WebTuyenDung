package service;

import dao.CvCertificateDAO;
import model.CvCertificate;

import java.util.List;

public class CvCertificateService {

    private final CvCertificateDAO cvCertificateDAO = new CvCertificateDAO();

    /**
     * Thay toàn bộ danh sách chứng chỉ gắn vào 1 CV. Kiểm tra ownership: chỉ candidate sở hữu CV mới
     * được sửa (so sánh candidateId của CV với candidateId đang đăng nhập, thực hiện ở servlet).
     */
    public void replaceForCv(int cvId, List<Integer> candidateCertificateIds) {

        if (cvId <= 0) {
            throw new IllegalArgumentException("CV không hợp lệ.");
        }

        cvCertificateDAO.replaceForCv(cvId, candidateCertificateIds);
    }

    public List<CvCertificate> getByCvId(int cvId) {

        if (cvId <= 0) {
            throw new IllegalArgumentException("CV không hợp lệ.");
        }

        return cvCertificateDAO.getByCvId(cvId);
    }

    public Integer getCandidateIdOfCv(int cvId) {
        return cvCertificateDAO.getCandidateIdOfCv(cvId);
    }
}
