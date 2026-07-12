package dao;

import config.DatabaseConfig;
import dto.JobSearchDTO;
import mapper.JobMapper;
import model.Job;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDAO extends DatabaseConfig implements TDAO<Job> {
    @Override
    public void add(Job jobs) {

        String sql = """
                INSERT INTO jobs (
                    employer_id,
                    title,
                    description,
                    min_salary,
                    max_salary,
                    currency,
                    location,
                    experience,
                    quantity,
                    posted_at,
                    expired_at,
                    application_deadline,
                    status,
                    is_hidden_on_expiry,
                    company_id,
                    job_position_id,
                    created_at,
                    updated_at
                )
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                RETURNING id
                """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobs.getEmployerID().getId());
            ps.setString(2, jobs.getTitle());
            ps.setString(3, jobs.getDescription());
            ps.setDouble(4, jobs.getMinSalary());
            ps.setDouble(5, jobs.getMaxSalary());
            ps.setString(6, jobs.getCurrency());
            ps.setString(7, jobs.getLocation());
            ps.setString(8, jobs.getExperience());
            ps.setInt(9, jobs.getQuantity());
            ps.setObject(10, jobs.getPostedAt());
            ps.setObject(11, jobs.getExpiredAt());
            ps.setObject(12, jobs.getApplicationDeadline());
            ps.setShort(13, jobs.getStatus().getValue());
            ps.setBoolean(14, jobs.getHiddenOnExpiry());
            // company_id nullable
            if (jobs.getCompanyId() != null) ps.setInt(15, jobs.getCompanyId());
            else ps.setNull(15, java.sql.Types.INTEGER);
            // job_position_id nullable
            if (jobs.getJobPositionId() != null) ps.setInt(16, jobs.getJobPositionId());
            else ps.setNull(16, java.sql.Types.INTEGER);
            ps.setObject(17, jobs.getCreatedAt());
            ps.setObject(18, jobs.getUpdatedAt());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    jobs.setId(rs.getInt("id"));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Job jobs) {

        String sql = """
                UPDATE jobs
                SET employer_id = ?,
                    title = ?,
                    description = ?,
                    min_salary = ?,
                    max_salary = ?,
                    currency = ?,
                    location = ?,
                    experience = ?,
                    quantity = ?,
                    posted_at = ?,
                    expired_at = ?,
                    application_deadline = ?,
                    status = ?,
                    is_hidden_on_expiry = ?,
                    company_id = ?,
                    job_position_id = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobs.getEmployerID().getId());
            ps.setString(2, jobs.getTitle());
            ps.setString(3, jobs.getDescription());
            ps.setDouble(4, jobs.getMinSalary());
            ps.setDouble(5, jobs.getMaxSalary());
            ps.setString(6, jobs.getCurrency());
            ps.setString(7, jobs.getLocation());
            ps.setString(8, jobs.getExperience());
            ps.setInt(9, jobs.getQuantity());
            ps.setObject(10, jobs.getPostedAt());
            ps.setObject(11, jobs.getExpiredAt());
            ps.setObject(12, jobs.getApplicationDeadline());
            ps.setShort(13, jobs.getStatus().getValue());
            ps.setBoolean(14, jobs.getHiddenOnExpiry());
            if (jobs.getCompanyId() != null) ps.setInt(15, jobs.getCompanyId());
            else ps.setNull(15, java.sql.Types.INTEGER);
            if (jobs.getJobPositionId() != null) ps.setInt(16, jobs.getJobPositionId());
            else ps.setNull(16, java.sql.Types.INTEGER);
            ps.setInt(17, jobs.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = " DELETE FROM jobs WHERE id=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Job getById(int id) {

        String sql = "SELECT j.*, c.company_name, e.user_id AS employer_user_id " +
                     "FROM jobs j " +
                     "LEFT JOIN companies c ON j.company_id = c.id " +
                     "LEFT JOIN employers e ON j.employer_id = e.id " +
                     "WHERE j.id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    Job job = JobMapper.map(rs);
                    String companyName = rs.getString("company_name");
                    if (!rs.wasNull()) job.setCompanyName(companyName);
                    // employerUserId stored as transient in Job via companyName pattern — pass via DTO layer
                    int empUserId = rs.getInt("employer_user_id");
                    if (!rs.wasNull()) job.setEmployerUserId(empUserId);
                    return job;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public List<Job> getAll() {

        String sql = "SELECT * FROM jobs";

        List<Job> list = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(JobMapper.map(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public List<Job> search(JobSearchDTO search) {

        StringBuilder sql = new StringBuilder(
            "SELECT j.*, c.company_name FROM jobs j LEFT JOIN companies c ON j.company_id = c.id WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (search.getTitle() != null && !search.getTitle().isBlank()) {

            sql.append(" AND j.title ILIKE ?");

            params.add("%" + search.getTitle() + "%");
        }

        if (search.getLocation() != null && !search.getLocation().isBlank()) {

            sql.append(" AND j.location ILIKE ?");

            params.add("%" + search.getLocation() + "%");
        }

        if (search.getMinSalary() != null) {

            sql.append(" AND j.min_salary>=?");

            params.add(search.getMinSalary());
        }

        if (search.getMaxSalary() != null) {

            sql.append(" AND j.max_salary<=?");

            params.add(search.getMaxSalary());
        }

        if (search.getStatus() != null) {

            sql.append(" AND j.status=?");

            params.add(search.getStatus());
        }

        if (search.getCompanyId() != null) {

            sql.append(" AND j.company_id=?");

            params.add(search.getCompanyId());
        }

        sql.append(" ORDER BY j.status ASC, j.posted_at DESC");
        sql.append(" LIMIT ? OFFSET ?");

        params.add(search.getSize());

        int offset = (search.getPage() - 1) * search.getSize();

        params.add(offset);

        List<Job> list = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {

                ps.setObject(i + 1, params.get(i));

            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Job job = JobMapper.map(rs);
                // Populate companyName from LEFT JOIN
                String companyName = rs.getString("company_name");
                if (!rs.wasNull()) job.setCompanyName(companyName);
                list.add(job);
            }

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

        return list;
    }
    public int count(JobSearchDTO search) {

        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM jobs WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (search.getTitle() != null && !search.getTitle().isBlank()) {
            sql.append(" AND title ILIKE ?");
            params.add("%" + search.getTitle() + "%");
        }

        if (search.getLocation() != null && !search.getLocation().isBlank()) {
            sql.append(" AND location ILIKE ?");
            params.add("%" + search.getLocation() + "%");
        }

        if (search.getMinSalary() != null) {
            sql.append(" AND min_salary >= ?");
            params.add(search.getMinSalary());
        }

        if (search.getMaxSalary() != null) {
            sql.append(" AND max_salary <= ?");
            params.add(search.getMaxSalary());
        }

        if (search.getStatus() != null) {
            sql.append(" AND status = ?");
            params.add(search.getStatus());
        }

        if (search.getCompanyId() != null) {
            sql.append(" AND company_id = ?");
            params.add(search.getCompanyId());
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return 0;
    }
}
