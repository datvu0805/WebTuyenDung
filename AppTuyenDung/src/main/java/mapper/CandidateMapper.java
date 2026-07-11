package mapper;

import model.Candidates;
import model.Users;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CandidateMapper {

    public static Candidates map(ResultSet rs) throws SQLException {

        Candidates candidate = new Candidates();

        candidate.setId(rs.getInt("id"));

        Users user = new Users();
        user.setId(rs.getInt("user_id"));

        candidate.setUser(user);

        candidate.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        candidate.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return candidate;
    }
}