package service;

import dao.CvCertificateDAO;
import dao.CvEducationDAO;
import model.CvEducation;

import java.util.List;

public class CvEducationService {

    private final CvEducationDAO cvEducationDAO = new CvEducationDAO();
    private final CvCertificateDAO cvCertificateDAO = new CvCertificateDAO(); // dùng chung getCandidateIdOfCv

    /**
     * Thay toàn bộ danh sách học vấn gắn vào 1 CV. Kiểm tra ownership: chỉ candidate sở hữu CV mới
     * được sửa (so sánh candidateId của CV với candidateId đang đăng nhập, thực hiện ở servlet).
     */
    public void replaceForCv(int cvId, List<Integer> candidateEducationIds) {

        if (cvId <= 0) {
            throw new IllegalArgumentException("CV không hợp lệ.");
        }

        cvEducationDAO.replaceForCv(cvId, candidateEducationIds);
    }

    public List<CvEducation> getByCvId(int cvId) {

        if (cvId <= 0) {
            throw new IllegalArgumentException("CV không hợp lệ.");
        }

        return cvEducationDAO.getByCvId(cvId);
    }

    public Integer getCandidateIdOfCv(int cvId) {
        return cvCertificateDAO.getCandidateIdOfCv(cvId);
    }
}
