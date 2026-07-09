package validator;

import dto.JobDTO;

import java.util.ArrayList;
import java.util.List;

public class JobValidator {

    public boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public List<String> validateJob(JobDTO dto) {

        List<String> errors = new ArrayList<>();

        if (dto == null) {
            errors.add("Thông tin công việc không được để trống");
            return errors;
        }

//        // employer
//        if (dto.getEmployerId() == null || dto.getEmployerId() <= 0) {
//            errors.add("Nhà tuyển dụng không hợp lệ");
//        }

        // title
        if (isEmpty(dto.getTitle())) {
            errors.add("Tiêu đề tuyển dụng không được để trống");
        } else if (dto.getTitle().length() > 255) {
            errors.add("Tiêu đề tuyển dụng tối đa 255 ký tự");
        }

        // description
        if (dto.getDescription() != null && dto.getDescription().length() > 5000) {
            errors.add("Mô tả công việc quá dài");
        }
        // minSalary
        if (dto.getMinSalary() == null) {
            errors.add("Mức lương tối thiểu không được để trống");
        } else if (dto.getMinSalary() < 0) {
            errors.add("Mức lương tối thiểu không được nhỏ hơn 0");
        }

        // maxSalary
        if (dto.getMaxSalary() == null) {
            errors.add("Mức lương tối đa không được để trống");
        } else if (dto.getMaxSalary() < 0) {
            errors.add("Mức lương tối đa không được nhỏ hơn 0");
        }

        // So sánh khoảng lương
        if (dto.getMinSalary() != null && dto.getMaxSalary() != null && dto.getMinSalary() > dto.getMaxSalary()) {
            errors.add("Mức lương tối thiểu không được lớn hơn mức lương tối đa");
        }

        // Currency
        if (dto.getCurrency() == null || dto.getCurrency().isBlank()) {
            errors.add("Đơn vị tiền tệ không được để trống");
        }

        // location
        if (!isEmpty(dto.getLocation()) && dto.getLocation().length() > 255) {
            errors.add("Địa điểm tối đa 255 ký tự");
        }

        // experience
        if (!isEmpty(dto.getExperience()) && dto.getExperience().length() > 100) {
            errors.add("Yêu cầu kinh nghiệm tối đa 100 ký tự");
        }

        // quantity
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            errors.add("Số lượng tuyển phải lớn hơn 0");
        }

        // postedAt
        if (dto.getPostedAt() == null) {
            errors.add("Ngày đăng tuyển không được để trống");
        }

        // expiredAt
        if (dto.getExpiredAt() == null) {
            errors.add("Ngày hết hạn không được để trống");
        }

        // applicationDeadline
        if (dto.getApplicationDeadline() == null) {
            errors.add("Hạn nộp hồ sơ không được để trống");
        }

        // So sánh thời gian
        if (dto.getPostedAt() != null && dto.getExpiredAt() != null && dto.getPostedAt().isAfter(dto.getExpiredAt())) {

            errors.add("Ngày đăng phải trước ngày hết hạn");
        }

        if (dto.getApplicationDeadline() != null && dto.getExpiredAt() != null && dto.getApplicationDeadline().isAfter(dto.getExpiredAt())) {

            errors.add("Hạn nộp hồ sơ phải trước hoặc bằng ngày hết hạn");
        }

        // status
        if (dto.getStatus() == null) {
            errors.add("Trạng thái không được để trống");
        } else if (dto.getStatus() < 0 || dto.getStatus() > 2) {
            errors.add("Trạng thái không hợp lệ");
        }

        // hiddenOnExpiry
        if (dto.getHiddenOnExpiry() == null) {
            errors.add("Trạng thái ẩn khi hết hạn không hợp lệ");
        }

        return errors;
    }
}