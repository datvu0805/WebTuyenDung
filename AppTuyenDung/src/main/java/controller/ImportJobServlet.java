package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dao.JobDAO;
import dto.ApiResponse;
import mapper.JobMapper;
import model.Employers;
import model.Job;
import constant.JobStatus;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/jobs/import")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024)
public class ImportJobServlet extends BaseServlet {

    private final JobDAO jobDAO = new JobDAO();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final DateTimeFormatter DT_FMT  = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DT_FMT2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // Regex parse "Tên bất kỳ (ID:123)" → group 1 = "123"
    private static final Pattern ID_PATTERN = Pattern.compile("\\(ID:(\\d+)\\)\\s*$");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        HttpSession session = req.getSession(false);
        Integer employerIdFromSession = (Integer) session.getAttribute("employerId");

        Part filePart = req.getPart("file");
        if (filePart == null || filePart.getSize() == 0) {
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, "Vui lòng chọn file Excel", null)));
            return;
        }

        List<String> errors = new ArrayList<>();
        List<Job> imported = new ArrayList<>();
        int row = 1;

        try (InputStream is = filePart.getInputStream();
             Workbook wb = new XSSFWorkbook(is)) {

            Sheet sheet = wb.getSheetAt(0);

            for (Row r : sheet) {
                if (r.getRowNum() == 0) continue; // bỏ header
                if (isRowEmpty(r)) break;
                row = r.getRowNum() + 1;

                try {
                    Job job = new Job();

                    // employerId: ưu tiên session, nếu không có thì đọc từ cột
                    int empId = employerIdFromSession != null
                            ? employerIdFromSession
                            : (int) getNumeric(r, 0);
                    job.setEmployerID(new Employers(empId));

                    job.setTitle(getString(r, 1));
                    job.setDescription(getString(r, 2));
                    job.setLocation(getString(r, 3));
                    job.setExperience(getString(r, 4));

                    String minS = getString(r, 5);
                    job.setMinSalary(minS.isBlank() ? 0.0 : Double.parseDouble(minS));

                    String maxS = getString(r, 6);
                    job.setMaxSalary(maxS.isBlank() ? 0.0 : Double.parseDouble(maxS));

                    job.setCurrency(getString(r, 7).isBlank() ? "VND" : getString(r, 7));

                    String qty = getString(r, 8);
                    job.setQuantity(qty.isBlank() ? 1 : Integer.parseInt(qty));

                    job.setPostedAt(parseDateTime(getString(r, 9)));
                    job.setExpiredAt(parseDateTime(getString(r, 10)));
                    job.setApplicationDeadline(parseDateTime(getString(r, 11)));

                    String statusStr = getString(r, 12);
                    short statusVal = statusStr.isBlank() ? 1 : Short.parseShort(statusStr);
                    job.setStatus(JobStatus.fromValue(statusVal));

                    // Công ty (cột 13) — "Tên công ty (ID:x)" hoặc số nguyên thuần
                    String companyVal = getString(r, 13);
                    if (!companyVal.isBlank()) {
                        Integer cid = parseIdFromCell(companyVal);
                        if (cid != null) job.setCompanyId(cid);
                    }

                    // Chức danh (cột 14) — "Tên chức danh (ID:x)" hoặc số nguyên thuần
                    String posVal = getString(r, 14);
                    if (!posVal.isBlank()) {
                        Integer pid = parseIdFromCell(posVal);
                        if (pid != null) job.setJobPositionId(pid);
                    }

                    job.setHiddenOnExpiry(false);

                    if (job.getTitle() == null || job.getTitle().isBlank()) {
                        errors.add("Dòng " + row + ": Thiếu tiêu đề công việc");
                        continue;
                    }

                    jobDAO.add(job);
                    imported.add(job);

                } catch (Exception e) {
                    errors.add("Dòng " + row + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            resp.getWriter().print(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, "Lỗi đọc file: " + e.getMessage(), null)));
            return;
        }

        String msg = "Đã import " + imported.size() + " công việc";
        if (!errors.isEmpty()) msg += ". Lỗi: " + String.join("; ", errors);

        resp.getWriter().print(objectMapper.writeValueAsString(
                new ApiResponse<>(true, msg, imported.stream().map(JobMapper::toDTO).toList())));
    }

    private String getString(Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().format(DT_FMT);
            }
            double d = cell.getNumericCellValue();
            return d == (long) d ? String.valueOf((long) d) : String.valueOf(d);
        }
        return cell.toString().trim();
    }

    private double getNumeric(Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
        try { return Double.parseDouble(cell.toString().trim()); } catch (Exception e) { return 0; }
    }

    private LocalDateTime parseDateTime(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDateTime.parse(s, DT_FMT); } catch (Exception e1) {
            try { return LocalDateTime.parse(s + " 00:00:00", DT_FMT); } catch (Exception e2) {
                return null;
            }
        }
    }

    /** Parse ID từ chuỗi "Tên bất kỳ (ID:123)" hoặc số thuần "123" */
    private Integer parseIdFromCell(String val) {
        if (val == null || val.isBlank()) return null;
        Matcher m = ID_PATTERN.matcher(val.trim());
        if (m.find()) {
            try { return Integer.parseInt(m.group(1)); } catch (NumberFormatException ignored) {}
        }
        // fallback: cả chuỗi là số nguyên
        try { return Integer.parseInt(val.trim()); } catch (NumberFormatException ignored) {}
        return null;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int i = 0; i <= 14; i++) {
            Cell c = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (c != null && c.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }
}
