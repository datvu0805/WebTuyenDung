package service;

import dao.ApplicationDAO;
import dto.ApplicationDTO;
import exception.BusinessException;
import model.Application;

import java.time.LocalDateTime;
import java.util.List;

public class ApplicationService {
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    public void submitApplication(Application app){
        int candidateID = app.getCandidateID().getId();
        int jobId = app.getCandidateID().getId();

        int appliadCount = applicationDAO.countApplications(candidateID, jobId);
        if (appliadCount >=3){
            throw new BusinessException("Bạn đã vượt quá 3 lần nộp đơn! " );
        }

        app.setStatus(0); // chờ duyệt
        app.setAppliedAt(LocalDateTime.now());
        app.setCreatedAt(LocalDateTime.now());
        app.setUpdatedAt(LocalDateTime.now());

        applicationDAO.add(app);
    }

    // cập nhật trạng thái đơn ứng tuyển
    public void updateStatus(int id, int newStatus){
        Application existingApp = applicationDAO.getByID(id);
        if(existingApp == null){
            throw new BusinessException("Đơn ứng tuyển của bạn không tồn tại trên hệ thống! ");
        }

        existingApp.setStatus(newStatus);
        existingApp.setUpdatedAt(LocalDateTime.now());
        applicationDAO.update(existingApp);
    }

    public List<ApplicationDTO> getRecruiteDashboard(int recruiterID){
        return applicationDAO.getApplicationsForRecruiter(recruiterID);
    }
}
