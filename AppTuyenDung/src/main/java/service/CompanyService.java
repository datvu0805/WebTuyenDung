package service;

import dao.CompanyDAO;
import dto.CompanyResponseDTO;
import dto.CreateCompanyDTO;
import dto.UpdateCompanyDTO;
import mapper.CompanyMapper;
import model.Company;

import java.util.List;
import java.util.stream.Collectors;

public class CompanyService {

    private final CompanyDAO companyDAO = new CompanyDAO();

    public final RedisService redis = new RedisService();

    public final CacheService cacheService = new CacheService();

    public String createCompany(CreateCompanyDTO dto) {
        if (dto == null) {
            return "Dữ liệu công ty không hợp lệ";
        }

        if (dto.getCompanyName() == null ||
                dto.getCompanyName().trim().isEmpty()) {
            return "Tên công ty không được để trống";
        }

        Company company = CompanyMapper.toEntity(dto);

        int companyId = companyDAO.add(company);

        if (companyId == -1) {
            return "Tạo công ty thất bại";
        }

        cacheService.clearAdminStatistic();

        cacheService.clearCacheCompany(companyId);
        return null;
    }

    public String updateCompany(UpdateCompanyDTO dto) {
        if (dto == null || dto.getId() <= 0) {
            return "ID công ty không hợp lệ";
        }

        if (dto.getCompanyName() == null ||
                dto.getCompanyName().trim().isEmpty()) {
            return "Tên công ty không được để trống";
        }

        Company existingCompany = companyDAO.findById(dto.getId());

        if (existingCompany == null) {
            return "Không tìm thấy công ty";
        }

        Company company = CompanyMapper.toEntity(dto);

        boolean updated = companyDAO.update(company);

        if (!updated) {
            return "Cập nhật công ty thất bại";
        }
        cacheService.clearCacheCompany(dto.getId());
        cacheService.clearAdminStatistic();
        return null;
    }

    public CompanyResponseDTO getCompanyById(int id) {
        Company company = companyDAO.findById(id);

        if (company == null) {
            return null;
        }

        return CompanyMapper.toResponseDTO(company);
    }

    public List<CompanyResponseDTO> getAllCompanies() {
        return companyDAO.getAll()
                .stream()
                .map(CompanyMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public String deleteCompany(int id) {
        if (id <= 0) {
            return "ID công ty không hợp lệ";
        }

        Company company = companyDAO.findById(id);

        if (company == null) {
            return "Không tìm thấy công ty";
        }

        boolean deleted = companyDAO.delete(id);

        if (!deleted) {
            return "Xóa công ty thất bại";
        }

        cacheService.clearCacheCompany(id);
        cacheService.clearAdminStatistic();
        return null;
    }
}