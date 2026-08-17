package dao;

import model.Company;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import utils.HibernateUtil;

import java.util.List;


public class CompanyDAO1  implements JPARepository<Company, Integer> {
    private JPARepository<Company, Integer> repository =
            new SimpleJPARepository<>(
                    HibernateUtil.getSessionFactory(),
                    Company.class
            );
    @Override
    public void add(Company entity) {
        repository.add(entity);
    }

    @Override
    public void update(Company entity) {

    }

    @Override
    public void delete(Company entity) {

    }

    @Override
    public Company findById(Integer integer) {
        return null;
    }

    @Override
    public List<Company> getAll() {
        return List.of();
    }
}
