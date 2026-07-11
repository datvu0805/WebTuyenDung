package mapper;

import constant.JobStatus;
import dto.JobDTO;
import model.Employers;
import model.Job;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class JobMapper {


//      ResultSet -> Jobs

    public static Job map(ResultSet rs) throws SQLException {

        Job job = new Job();

        job.setId(rs.getInt("id"));
        job.setEmployerID(new Employers(rs.getInt("employer_id")));
        job.setTitle(rs.getString("title"));
        job.setDescription(rs.getString("description"));
        job.setMinSalary(rs.getDouble("min_salary"));
        job.setMaxSalary(rs.getDouble("max_salary"));
        job.setCurrency(rs.getString("currency"));
        job.setLocation(rs.getString("location"));
        job.setExperience(rs.getString("experience"));
        job.setQuantity(rs.getInt("quantity"));

        job.setPostedAt(rs.getObject("posted_at", LocalDateTime.class));
        job.setExpiredAt(rs.getObject("expired_at", LocalDateTime.class));
        job.setApplicationDeadline(rs.getObject("application_deadline", LocalDateTime.class));

        job.setStatus(JobStatus.fromValue(rs.getShort("status")));
        job.setHiddenOnExpiry(rs.getBoolean("is_hidden_on_expiry"));

        // company_id và job_position_id (nullable)
        int cid = rs.getInt("company_id");
        if (!rs.wasNull()) job.setCompanyId(cid);
        int pid = rs.getInt("job_position_id");
        if (!rs.wasNull()) job.setJobPositionId(pid);

        job.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        job.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));

        return job;
    }


    // JobDTO -> Jobs

    public static Job toEntity(JobDTO dto) {

        Job job = new Job();

        job.setId(dto.getId());

        if (dto.getEmployerId() != null) {
            Employers employer = new Employers(dto.getEmployerId());
            job.setEmployerID(employer);
        }

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setMinSalary(dto.getMinSalary());
        job.setMaxSalary(dto.getMaxSalary());
        job.setCurrency(dto.getCurrency());
        job.setLocation(dto.getLocation());
        job.setExperience(dto.getExperience());
        job.setQuantity(dto.getQuantity());

        job.setPostedAt(dto.getPostedAt());
        job.setExpiredAt(dto.getExpiredAt());
        job.setApplicationDeadline(dto.getApplicationDeadline());

        job.setStatus(JobStatus.fromValue(dto.getStatus()));
        job.setHiddenOnExpiry(dto.getHiddenOnExpiry());
        job.setCompanyId(dto.getCompanyId());
        job.setJobPositionId(dto.getJobPositionId());

        return job;
    }
    // JobDTO -> Jobs nhưng theo id

    public static JobDTO toDTO(Job job) {

        if (job == null) {
            return null;
        }

        JobDTO dto = new JobDTO();

        dto.setId(job.getId());

        if (job.getEmployerID() != null) {
            dto.setEmployerId(job.getEmployerID().getId());
        }

        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setMinSalary(job.getMinSalary());
        dto.setMaxSalary(job.getMaxSalary());
        dto.setCurrency(job.getCurrency());
        dto.setLocation(job.getLocation());
        dto.setExperience(job.getExperience());
        dto.setQuantity(job.getQuantity());

        dto.setPostedAt(job.getPostedAt());
        dto.setExpiredAt(job.getExpiredAt());
        dto.setApplicationDeadline(job.getApplicationDeadline());

        dto.setStatus(job.getStatus().getValue());
        dto.setHiddenOnExpiry(job.getHiddenOnExpiry());
        dto.setCompanyId(job.getCompanyId());
        dto.setJobPositionId(job.getJobPositionId());

        return dto;
    }
}