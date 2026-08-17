package model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// bảng đơn ứng tuyển
@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Application extends BaseEntity{
    private Candidates candidateID;
    private Job jodID;
    private CV cvID;
    private LocalDateTime appliedAt;
    private String coverLetter;
    private String description;
    private int status;

}
