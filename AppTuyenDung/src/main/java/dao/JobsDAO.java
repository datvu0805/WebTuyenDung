package dao;

import config.DatabaseConfig;
import model.JobSkills;
import model.Jobs;
import model.Skills;

import java.util.List;

public class JobsDAO extends DatabaseConfig implements IDAO<Jobs>{
    @Override
    public void add(Jobs entity) {

    }

    @Override
    public void update(Jobs entity) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public Jobs getById(int id) {
        return null;
    }

    @Override
    public List<Jobs> getAll() {
        return List.of();
    }

    public void addSkill(Jobs jobsID, Skills skillsID){

    }
    public void delete(Jobs jobsID, Skills skillsID){

    }
    public List<JobSkills> getAll(Jobs jobsID, Skills skillsID){
        return List.of();
    }
}
