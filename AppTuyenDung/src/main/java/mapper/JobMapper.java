package mapper;

import dto.JobDTO;
import model.Employers;
import model.Jobs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class JobMapper {


//      ResultSet -> Jobs

    public static Jobs map(ResultSet rs) throws SQLException {

        Jobs job = new Jobs();

        job.setId(rs.getInt("id"));
        job.setEmployerID(new Employers(rs.getInt("employer_id")));
        job.setTitle(rs.getString("title"));
        job.setDescription(rs.getString("description"));
        job.setSalary(rs.getDouble("salary"));
        job.setLocation(rs.getString("location"));
        job.setExperience(rs.getString("experience"));
        job.setQuantity(rs.getInt("quantity"));

        job.setPostedAt(rs.getObject("posted_at", LocalDateTime.class));
        job.setExpiredAt(rs.getObject("expired_at", LocalDateTime.class));
        job.setApplicationDeadline(rs.getObject("application_deadline", LocalDateTime.class));

        job.setStatus(rs.getShort("status"));
        job.setHiddenOnExpiry(rs.getBoolean("is_hidden_on_expiry"));

        job.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        job.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));

        return job;
    }


     // JobDTO -> Jobs

    public static Jobs toEntity(JobDTO dto) {

        Jobs job = new Jobs();

        job.setEmployerID(new Employers(dto.getEmployerId()));
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setSalary(dto.getSalary());
        job.setLocation(dto.getLocation());
        job.setExperience(dto.getExperience());
        job.setQuantity(dto.getQuantity());

        job.setPostedAt(dto.getPostedAt());
        job.setExpiredAt(dto.getExpiredAt());
        job.setApplicationDeadline(dto.getApplicationDeadline());

        job.setStatus(dto.getStatus());
        job.setHiddenOnExpiry(dto.getHiddenOnExpiry());

        return job;
    }
}