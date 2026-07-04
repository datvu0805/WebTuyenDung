package dao;

import config.DatabaseConfig;

import java.util.List;

public class CandidateDAO<Candidate> extends DatabaseConfig implements IDAO<Candidate> {
    @Override
    public void add(Candidate candidate) {

    }

    @Override
    public void update(Candidate candidate) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public List<Candidate> getAll() {
        return List.of();
    }
}
