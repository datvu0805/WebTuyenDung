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
                    created_at,
                    updated_at
                )
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
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
            ps.setObject(15, jobs.getCreatedAt());
            ps.setObject(16, jobs.getUpdatedAt());

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
            ps.setInt(15, jobs.getId());

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

        String sql = "SELECT * FROM jobs WHERE id = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return JobMapper.map(rs);
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

        StringBuilder sql = new StringBuilder("SELECT * FROM jobs WHERE 1=1");

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

            sql.append(" AND min_salary>=?");

            params.add(search.getMinSalary());
        }

        if (search.getMaxSalary() != null) {

            sql.append(" AND max_salary<=?");

            params.add(search.getMaxSalary());
        }

        if (search.getStatus() != null) {

            sql.append(" AND status=?");

            params.add(search.getStatus());
        }

        sql.append(" ORDER BY posted_at DESC");
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

                list.add(JobMapper.map(rs));

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
