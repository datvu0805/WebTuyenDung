package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dao.JobPositionDAO;
import dto.ApiResponse;
import model.JobPosition;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/job-positions/import")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024)
public class ImportJobPositionServlet extends BaseServlet {

    private final JobPositionDAO dao = new JobPositionDAO();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        Part filePart = req.getPart("file");
        if (filePart == null || filePart.getSize() == 0) {
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, "Vui lòng chọn file Excel", null)));
            return;
        }

        List<String> errors = new ArrayList<>();
        int successCount = 0;

        try (InputStream is = filePart.getInputStream();
             Workbook wb = new XSSFWorkbook(is)) {

            Sheet sheet = wb.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                if (isRowEmpty(row)) break;
                int rowNum = row.getRowNum() + 1;
                try {
                    String name = getString(row, 0);
                    String description = getString(row, 1);
                    if (name.isBlank()) {
                        errors.add("Dòng " + rowNum + ": Thiếu tên chức danh");
                        continue;
                    }
                    dao.add(new JobPosition(name.trim(), description.isBlank() ? null : description));
                    successCount++;
                } catch (Exception e) {
                    errors.add("Dòng " + rowNum + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            resp.getWriter().write(objectMapper.writeValueAsString(
                    new ApiResponse<>(false, "Lỗi đọc file: " + e.getMessage(), null)));
            return;
        }

        String msg = "Đã import " + successCount + " chức danh";
        if (!errors.isEmpty()) msg += ". Lỗi: " + String.join("; ", errors);
        resp.getWriter().write(objectMapper.writeValueAsString(new ApiResponse<>(true, msg, null)));
    }

    private String getString(Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC) {
            double d = cell.getNumericCellValue();
            return d == (long) d ? String.valueOf((long) d) : String.valueOf(d);
        }
        return cell.toString().trim();
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int i = 0; i <= 1; i++) {
            Cell c = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (c != null && c.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }
}
