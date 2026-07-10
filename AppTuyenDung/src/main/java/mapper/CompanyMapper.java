package mapper;

import dto.CompanyResponseDTO;
import dto.CreateCompanyDTO;
import dto.UpdateCompanyDTO;
import model.Company;

import java.time.format.DateTimeFormatter;

public class CompanyMapper {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private CompanyMapper() {
    }

    public static Company toEntity(CreateCompanyDTO dto) {
        Company company = new Company();
        company.setCompanyName(dto.getCompanyName());
        company.setDescription(dto.getDescription());
        return company;
    }

    public static Company toEntity(UpdateCompanyDTO dto) {
        Company company = new Company();
        company.setId(dto.getId());
        company.setCompanyName(dto.getCompanyName());
        company.setDescription(dto.getDescription());
        return company;
    }

    public static CompanyResponseDTO toResponseDTO(Company company) {
        String createdAt = company.getCreatedAt() == null
                ? null
                : company.getCreatedAt().format(FORMATTER);

        String updatedAt = company.getUpdatedAt() == null
                ? null
                : company.getUpdatedAt().format(FORMATTER);

        return new CompanyResponseDTO(
                company.getId(),
                company.getCompanyName(),
                company.getDescription(),
                createdAt,
                updatedAt
        );
    }
}