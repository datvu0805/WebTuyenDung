package service;

import dao.CandidateDAO;
import dao.CandidateSkillDAO;
import dao.SkillDAO;
import dto.CandidateSkillBatchDTO;
import dto.CandidateSkillDTO;
import model.Candidates;
import model.Skill;
import validator.CandidateSkillValidator;

import java.util.ArrayList;
import java.util.List;

public class CandidateSkillService {

    private final CandidateSkillDAO candidateSkillDAO = new CandidateSkillDAO();
    private final CandidateDAO candidateDAO = new CandidateDAO();
    private final SkillDAO skillDAO = new SkillDAO();

    /**
     * Thêm một kỹ năng cho ứng viên
     */
    public void add(CandidateSkillDTO dto) {

        CandidateSkillValidator.validate(dto);

        Candidates candidate = candidateDAO.getByID(dto.getCandidateId());
        if (candidate == null) {
            throw new IllegalArgumentException("Ứng viên không tồn tại.");
        }

        Skill skill = skillDAO.getById(dto.getSkillId());
        if (skill == null) {
            throw new IllegalArgumentException("Kỹ năng không tồn tại.");
        }

        if (candidateSkillDAO.exists(candidate, skill)) {
            throw new IllegalArgumentException("Ứng viên đã có kỹ năng này.");
        }

        candidateSkillDAO.add(candidate, skill);
    }

    /**
     * Thêm nhiều kỹ năng
     */
    public void add(CandidateSkillBatchDTO dto) {

        CandidateSkillValidator.validate(dto);

        Candidates candidate = candidateDAO.getByID(dto.getCandidateId());
        if (candidate == null) {
            throw new IllegalArgumentException("Ứng viên không tồn tại.");
        }

        List<Skill> skills = new ArrayList<>();

        for (Integer id : dto.getSkillIds()) {

            Skill skill = skillDAO.getById(id);

            if (skill == null) {
                throw new IllegalArgumentException("Kỹ năng không tồn tại. ID = " + id);
            }

            skills.add(skill);
        }

        candidateSkillDAO.addBatch(candidate, skills);
    }



    /**
     * Xóa một kỹ năng
     */
    public void delete(CandidateSkillDTO dto) {

        CandidateSkillValidator.validate(dto);

        Candidates candidate = candidateDAO.getByID(dto.getCandidateId());
        if (candidate == null) {
            throw new IllegalArgumentException("Ứng viên không tồn tại.");
        }

        Skill skill = skillDAO.getById(dto.getSkillId());
        if (skill == null) {
            throw new IllegalArgumentException("Kỹ năng không tồn tại.");
        }

        candidateSkillDAO.delete(candidate, skill);
    }

    /**
     * Lấy toàn bộ kỹ năng của ứng viên
     */
    public List<Skill> getSkillsByCandidate(int candidateId) {

        Candidates candidate = candidateDAO.getByID(candidateId);

        if (candidate == null) {
            throw new IllegalArgumentException("Ứng viên không tồn tại.");
        }

        return candidateSkillDAO.getSkillsByCandidateId(candidate);
    }

    /**
     * Lấy tất cả ứng viên có kỹ năng
     */
    public List<Candidates> getCandidatesBySkill(int skillId) {

        Skill skill = skillDAO.getById(skillId);

        if (skill == null) {
            throw new IllegalArgumentException("Kỹ năng không tồn tại.");
        }

        return candidateSkillDAO.getCandidatesBySkillId(skill);
    }

    /**
     * Kiểm tra ứng viên có kỹ năng hay không
     */
    public boolean exists(int candidateId, int skillId) {

        Candidates candidate = candidateDAO.getByID(candidateId);

        Skill skill = skillDAO.getById(skillId);

        if (candidate == null || skill == null) {
            return false;
        }

        return candidateSkillDAO.exists(candidate, skill);
    }
}