package service;

import com.google.gson.reflect.TypeToken;
import dao.SkillDAO;
import dto.SkillDTO;
import mapper.SkillMapper;
import model.Skill;
import validator.SkillValidator;

import java.util.ArrayList;
import java.util.List;

public class SkillService {

    private static final String SKILLS_CACHE_KEY = "catalog:skills";
    private static final long SKILLS_CACHE_TTL_SECONDS = 600;

    private final SkillDAO skillDAO;
    private final SkillValidator validator;
    private final RedisService redisService;

    public SkillService() {
        this.skillDAO = new SkillDAO();
        this.validator = new SkillValidator();
        this.redisService = new RedisService();
    }

    public void addSkill(SkillDTO dto) {

        List<String> errors = validator.validateSkill(dto);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }

        Skill skill = SkillMapper.toEntity(dto);

        skillDAO.add(skill);

        // Đồng bộ id vừa được DB sinh ra vào DTO
        dto.setId(skill.getId());
        redisService.delete(SKILLS_CACHE_KEY);
    }

    public void updateSkill(int id, SkillDTO dto) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID kỹ năng không hợp lệ");
        }

        List<String> errors = validator.validateSkill(dto);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }

        Skill skill = SkillMapper.toEntity(dto);
        skill.setId(id);

        skillDAO.update(skill);
        redisService.delete(SKILLS_CACHE_KEY);
    }

    public void deleteSkill(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID kỹ năng không hợp lệ");
        }

        skillDAO.delete(id);
        redisService.delete(SKILLS_CACHE_KEY);
    }

    public SkillDTO getSkillById(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID kỹ năng không hợp lệ");
        }

        Skill skill = skillDAO.getById(id);

        return SkillMapper.toDTO(skill);
    }

    public List<SkillDTO> getAllSkills() {

        List<SkillDTO> cachedSkills = redisService.getObject(
                SKILLS_CACHE_KEY,
                new TypeToken<List<SkillDTO>>() {}.getType()
        );
        if (cachedSkills != null) {
            return cachedSkills;
        }

        List<SkillDTO> skillDTOList = new ArrayList<>();

        List<Skill> skillList = skillDAO.getAll();

        skillList.forEach(x -> skillDTOList.add(new SkillDTO(x.getId(), x.getSkillName())));
        redisService.setObjiect(SKILLS_CACHE_KEY, skillDTOList, SKILLS_CACHE_TTL_SECONDS);

        return skillDTOList;
    }
}