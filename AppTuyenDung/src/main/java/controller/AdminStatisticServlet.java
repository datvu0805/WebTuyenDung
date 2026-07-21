<<<<<<< HEAD
package controller;

import com.google.gson.Gson;
import dto.AdminStatisticDTO;
import dto.ApiResponse;
import service.AdminStatisticService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/statistic")
public class AdminStatisticServlet extends BaseServlet {

    private final Gson gson = new Gson();
    private final AdminStatisticService statisticService =
            new AdminStatisticService();

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        try {
            boolean cacheHit = statisticService.isCached(); // kiểm tra trước

            AdminStatisticDTO data = statisticService.getStatistic(); // sau đó mới lấy dữ liệu

            resp.setHeader("X-Cache", cacheHit ? "HIT" : "MISS");

            resp.setStatus(HttpServletResponse.SC_OK);

            resp.getWriter().write(gson.toJson(
                    new ApiResponse<>(
                            true,
                            "Lấy thống kê thành công",
                            data
                    )
            ));



        } catch (Exception e) {
            e.printStackTrace();

            resp.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            resp.getWriter().write(gson.toJson(
                    new ApiResponse<>(
                            false,
                            e.getClass().getSimpleName()
                                    + ": "
                                    + e.getMessage(),
                            null
                    )
            ));
        }
    }
}
package controller;

import com.google.gson.Gson;
import dto.AdminStatisticDTO;
import dto.ApiResponse;
import dto.CompanyStatisticDTO;
import dto.RecruitmentStatisticDTO;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import service.AdminStatisticService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/admin/statistic", "/admin/statistics", "/admin/statistic/export", "/admin/statistics/export"})
public class AdminStatisticServlet extends BaseServlet {

    private final Gson gson = new Gson();
    private final AdminStatisticService statisticService = new AdminStatisticService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            LocalDate from = parseDate(req.getParameter("from"));
            LocalDate to = parseDate(req.getParameter("to"));
            String report = req.getParameter("report");
            if (req.getServletPath().endsWith("/export")) {
                export(req, resp, report, from, to);
                return;
            }
            if ("companies".equalsIgnoreCase(report)) {
                writeJson(resp, HttpServletResponse.SC_OK, new ApiResponse<>(true, "Lấy thống kê công ty thành công",
                        statisticService.getCompanyStatistics(from, to)));
                return;
            }
            if ("recruitment".equalsIgnoreCase(report)) {
                RecruitmentStatisticDTO recruitment = statisticService.getRecruitmentStatistics(from, to);
                writeJson(resp, HttpServletResponse.SC_OK, new ApiResponse<>(true, "Lấy hiệu quả tuyển dụng thành công", recruitment));
                return;
            }
            AdminStatisticDTO data = statisticService.getStatistic(from, to);
            resp.setContentType("application/json;charset=UTF-8");
            resp.setCharacterEncoding("UTF-8");
            writeJson(resp, HttpServletResponse.SC_OK, new ApiResponse<>(true, "Lấy thống kê thành công", data));
        } catch (IllegalArgumentException e) {
            writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new ApiResponse<>(false, "Không thể tải thống kê", null));
        }
    }

    private void export(HttpServletRequest req, HttpServletResponse resp, String report, LocalDate from, LocalDate to) throws IOException {
        String format = req.getParameter("format");
        if ("companies".equalsIgnoreCase(report)) {
            if ("xlsx".equalsIgnoreCase(format) || "excel".equalsIgnoreCase(format)) {
                sendFile(resp, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "bao-cao-cong-ty.xlsx", createCompanyExcel(statisticService.getCompanyStatistics(from, to)));
            } else if ("pdf".equalsIgnoreCase(format)) {
                sendFile(resp, "application/pdf", "bao-cao-cong-ty.pdf", createCompanyPdf(statisticService.getCompanyStatistics(from, to)));
            } else writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse<>(false, "format phải là xlsx hoặc pdf", null));
            return;
        }
        if ("recruitment".equalsIgnoreCase(report)) {
            RecruitmentStatisticDTO recruitment = statisticService.getRecruitmentStatistics(from, to);
            if ("xlsx".equalsIgnoreCase(format) || "excel".equalsIgnoreCase(format)) {
                sendFile(resp, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "bao-cao-hieu-qua-tuyen-dung.xlsx", createRecruitmentExcel(recruitment));
            } else if ("pdf".equalsIgnoreCase(format)) {
                sendFile(resp, "application/pdf", "bao-cao-hieu-qua-tuyen-dung.pdf", createRecruitmentPdf(recruitment));
            } else writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse<>(false, "format phải là xlsx hoặc pdf", null));
            return;
        }
        AdminStatisticDTO data = statisticService.getStatistic(from, to);
        if ("xlsx".equalsIgnoreCase(format) || "excel".equalsIgnoreCase(format)) {
            sendFile(resp, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "thong-ke.xlsx", createExcel(data));
        } else if ("pdf".equalsIgnoreCase(format)) {
            sendFile(resp, "application/pdf", "thong-ke.pdf", createPdf(data));
        } else writeJson(resp, HttpServletResponse.SC_BAD_REQUEST, new ApiResponse<>(false, "format phải là xlsx hoặc pdf", null));
    }

    private byte[] createCompanyExcel(List<CompanyStatisticDTO> rows) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Theo cong ty");
            CellStyle title = titleStyle(workbook);
            CellStyle header = headerStyle(workbook);
            CellStyle text = borderedStyle(workbook, HorizontalAlignment.LEFT);
            CellStyle number = borderedStyle(workbook, HorizontalAlignment.RIGHT);
            CellStyle percent = borderedStyle(workbook, HorizontalAlignment.RIGHT);
            percent.setDataFormat(workbook.createDataFormat().getFormat("0.0%"));
            Row titleRow = sheet.createRow(0); titleRow.setHeightInPoints(28); titleRow.createCell(0).setCellValue("BÁO CÁO HIỆU QUẢ THEO CÔNG TY"); titleRow.getCell(0).setCellStyle(title);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 7));
            Row noteRow = sheet.createRow(1); noteRow.createCell(0).setCellValue("Tỷ lệ được tính trên các tin và đơn trong khoảng thời gian đã chọn."); noteRow.getCell(0).setCellStyle(text); sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 7));
            String[] headers = {"Công ty", "Tổng tin", "Tin có ứng viên", "Tin có người nhận", "Tổng đơn", "Đơn được nhận", "Tỷ lệ tin có người nhận", "Tỷ lệ đơn được nhận"};
            Row headerRow = sheet.createRow(3);
            for (int i = 0; i < headers.length; i++) { Cell cell = headerRow.createCell(i); cell.setCellValue(headers[i]); cell.setCellStyle(header); }
            int rowIndex = 4; int totalJobs = 0, totalApplicantJobs = 0, totalHiredJobs = 0, totalApplications = 0, totalHiredApplications = 0;
            for (CompanyStatisticDTO item : rows) {
                Row row = sheet.createRow(rowIndex++); setCell(row, 0, item.getCompanyName(), text); setCell(row, 1, item.getTotalJobs(), number); setCell(row, 2, item.getJobsWithApplicants(), number); setCell(row, 3, item.getJobsWithHires(), number); setCell(row, 4, item.getTotalApplications(), number); setCell(row, 5, item.getHiredApplications(), number); setCell(row, 6, item.getTotalJobs() == 0 ? 0 : item.getJobFillRate() / 100, percent); setCell(row, 7, item.getTotalApplications() == 0 ? 0 : item.getApplicationHireRate() / 100, percent);
                totalJobs += item.getTotalJobs(); totalApplicantJobs += item.getJobsWithApplicants(); totalHiredJobs += item.getJobsWithHires(); totalApplications += item.getTotalApplications(); totalHiredApplications += item.getHiredApplications();
            }
            Row total = sheet.createRow(rowIndex); setCell(total, 0, "TỔNG CỘNG", header); setCell(total, 1, totalJobs, header); setCell(total, 2, totalApplicantJobs, header); setCell(total, 3, totalHiredJobs, header); setCell(total, 4, totalApplications, header); setCell(total, 5, totalHiredApplications, header); setCell(total, 6, totalJobs == 0 ? 0 : totalHiredJobs * 1.0 / totalJobs, percent); setCell(total, 7, totalApplications == 0 ? 0 : totalHiredApplications * 1.0 / totalApplications, percent);
            sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(3, rowIndex - 1, 0, 7)); sheet.createFreezePane(1, 4); sheet.setDisplayGridlines(false);
            int[] widths = {30, 14, 18, 20, 14, 16, 22, 22}; for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i] * 256);
            workbook.write(out); return out.toByteArray();
        }
    }

    private byte[] createRecruitmentExcel(RecruitmentStatisticDTO item) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Hieu qua"); CellStyle title = titleStyle(workbook); CellStyle header = headerStyle(workbook); CellStyle label = borderedStyle(workbook, HorizontalAlignment.LEFT); CellStyle number = borderedStyle(workbook, HorizontalAlignment.RIGHT); CellStyle percent = borderedStyle(workbook, HorizontalAlignment.RIGHT); percent.setDataFormat(workbook.createDataFormat().getFormat("0.0%"));
            Row titleRow = sheet.createRow(0); titleRow.setHeightInPoints(28); titleRow.createCell(0).setCellValue("BÁO CÁO HIỆU QUẢ TUYỂN DỤNG"); titleRow.getCell(0).setCellStyle(title); sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 2));
            Row head = sheet.createRow(2); setCell(head, 0, "Chỉ số", header); setCell(head, 1, "Số lượng", header); setCell(head, 2, "Tỷ lệ", header);
            Object[][] values = {{"Tổng tin", item.getTotalJobs(), null}, {"Tin có ứng viên", item.getJobsWithApplicants(), item.getJobApplicantRate() / 100}, {"Tin có người nhận", item.getJobsWithHires(), item.getJobHireRate() / 100}, {"Tổng đơn", item.getTotalApplications(), null}, {"Đơn được nhận", item.getHiredApplications(), item.getApplicationHireRate() / 100}};
            int index = 3; for (Object[] value : values) { Row row = sheet.createRow(index++); setCell(row, 0, value[0], label); setCell(row, 1, value[1], number); if (value[2] != null) setCell(row, 2, value[2], percent); else setCell(row, 2, "—", label); }
            sheet.createFreezePane(0, 3); sheet.setDisplayGridlines(false); sheet.setColumnWidth(0, 30 * 256); sheet.setColumnWidth(1, 16 * 256); sheet.setColumnWidth(2, 16 * 256); workbook.write(out); return out.toByteArray();
        }
    }

    private CellStyle titleStyle(Workbook workbook) { CellStyle style = workbook.createCellStyle(); style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex()); style.setFillPattern(FillPatternType.SOLID_FOREGROUND); style.setAlignment(HorizontalAlignment.CENTER); Font font = workbook.createFont(); font.setBold(true); font.setColor(IndexedColors.WHITE.getIndex()); font.setFontHeightInPoints((short) 15); style.setFont(font); return style; }
    private CellStyle headerStyle(Workbook workbook) { CellStyle style = borderedStyle(workbook, HorizontalAlignment.CENTER); style.setFillForegroundColor(IndexedColors.BLUE.getIndex()); style.setFillPattern(FillPatternType.SOLID_FOREGROUND); Font font = workbook.createFont(); font.setBold(true); font.setColor(IndexedColors.WHITE.getIndex()); style.setFont(font); return style; }
    private CellStyle borderedStyle(Workbook workbook, HorizontalAlignment alignment) { CellStyle style = workbook.createCellStyle(); style.setAlignment(alignment); style.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER); style.setBorderTop(BorderStyle.THIN); style.setBorderBottom(BorderStyle.THIN); style.setBorderLeft(BorderStyle.THIN); style.setBorderRight(BorderStyle.THIN); style.setWrapText(true); return style; }
    private void setCell(Row row, int index, Object value, CellStyle style) { Cell cell = row.createCell(index); if (value instanceof Number number) cell.setCellValue(number.doubleValue()); else cell.setCellValue(value(value)); cell.setCellStyle(style); }


    private byte[] createCompanyPdf(List<CompanyStatisticDTO> rows) {
        StringBuilder body = new StringBuilder();
        body.append("BAO CAO HIEU QUA THEO CONG TY\n");
        body.append("Bang chi tiet theo cong ty\n\n");
        body.append(tableLine("CONG TY", "TIN", "CO UV", "NHAN", "DON", "DON NHAN", "% DON"));
        body.append(tableLine("-", "-", "-", "-", "-", "-", "-"));
        int totalJobs = 0, totalApplicantJobs = 0, totalHiredJobs = 0, totalApplications = 0, totalHiredApplications = 0;
        for (CompanyStatisticDTO item : rows) {
            body.append(tableLine(truncate(toAscii(value(item.getCompanyName())), 22), String.valueOf(item.getTotalJobs()), String.valueOf(item.getJobsWithApplicants()), String.valueOf(item.getJobsWithHires()), String.valueOf(item.getTotalApplications()), String.valueOf(item.getHiredApplications()), formatPercent(item.getApplicationHireRate())));
            totalJobs += item.getTotalJobs(); totalApplicantJobs += item.getJobsWithApplicants(); totalHiredJobs += item.getJobsWithHires(); totalApplications += item.getTotalApplications(); totalHiredApplications += item.getHiredApplications();
        }
        body.append(tableLine("TONG CONG", String.valueOf(totalJobs), String.valueOf(totalApplicantJobs), String.valueOf(totalHiredJobs), String.valueOf(totalApplications), String.valueOf(totalHiredApplications), formatPercent(totalApplications == 0 ? 0 : totalHiredApplications * 100.0 / totalApplications)));
        return simplePdf(body.toString());
    }

    private byte[] createRecruitmentPdf(RecruitmentStatisticDTO item) {
        StringBuilder body = new StringBuilder("BAO CAO HIEU QUA TUYEN DUNG\n\n");
        body.append(tableLine("CHI SO", "SO LUONG", "TY LE"));
        body.append(tableLine("TONG TIN", String.valueOf(item.getTotalJobs()), "-"));
        body.append(tableLine("TIN CO UNG VIEN", String.valueOf(item.getJobsWithApplicants()), formatPercent(item.getJobApplicantRate()) + "%"));
        body.append(tableLine("TIN CO NGUOI NHAN", String.valueOf(item.getJobsWithHires()), formatPercent(item.getJobHireRate()) + "%"));
        body.append(tableLine("TONG DON", String.valueOf(item.getTotalApplications()), "-"));
        body.append(tableLine("DON DUOC NHAN", String.valueOf(item.getHiredApplications()), formatPercent(item.getApplicationHireRate()) + "%"));
        return simplePdf(body.toString());
    }

    private String formatPercent(double value) { return String.format(java.util.Locale.US, "%.1f", value); }
    private String tableLine(String... values) {
        int[] widths = values.length == 3 ? new int[]{24, 12, 14} : new int[]{22, 7, 7, 7, 7, 10, 9};
        StringBuilder line = new StringBuilder("|");
        for (int i = 0; i < values.length; i++) line.append(" ").append(pad(truncate(values[i], widths[i]), widths[i])).append(" |");
        return line.append("\n").toString();
    }

    private String pad(String value, int width) { return String.format("%-" + width + "s", value); }
    private String truncate(String value, int width) { return value.length() <= width ? value : value.substring(0, Math.max(0, width - 3)) + "..."; }


    private byte[] createExcel(AdminStatisticDTO data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Thong ke");
            int rowIndex = 0;
            rowIndex = addRow(sheet, rowIndex, "BÁO CÁO THỐNG KÊ ADMIN", "Giá trị");
            rowIndex = addRow(sheet, rowIndex, "Khoảng từ", value(data.getFromDate()));
            rowIndex = addRow(sheet, rowIndex, "Khoảng đến", value(data.getToDate()));
            rowIndex++;
            rowIndex = addRow(sheet, rowIndex, "Người dùng", data.getTotalUsers());
            rowIndex = addRow(sheet, rowIndex, "Ứng viên", data.getTotalCandidates());
            rowIndex = addRow(sheet, rowIndex, "Nhà tuyển dụng", data.getTotalEmployers());
            rowIndex = addRow(sheet, rowIndex, "Công ty", data.getTotalCompanies());
            rowIndex = addRow(sheet, rowIndex, "Tin tuyển dụng", data.getTotalJobs());
            rowIndex = addRow(sheet, rowIndex, "Đơn ứng tuyển", data.getTotalApplications());
            rowIndex = addRow(sheet, rowIndex, "CV", data.getTotalCvs());
            rowIndex = addRow(sheet, rowIndex, "Tin nhắn", data.getTotalMessages());
            rowIndex = addRow(sheet, rowIndex, "VIP đang hoạt động", data.getActiveVipUsers());
            rowIndex = addRow(sheet, rowIndex, "Doanh thu", data.getTotalRevenue());
            rowIndex++;
            rowIndex = addMap(sheet, rowIndex, "Người dùng theo vai trò", data.getTotalUserByRole());
            rowIndex = addMap(sheet, rowIndex, "Tin tuyển dụng theo trạng thái", data.getJobsByStatus());
            rowIndex = addMap(sheet, rowIndex, "Đơn ứng tuyển theo trạng thái", data.getApplicationsByStatus());
            rowIndex++;
            addMonthly(sheet, rowIndex, "Người dùng theo tháng", data.getMonthlyUsers());
            addMonthly(sheet, rowIndex + data.getMonthlyUsers().size() + 2, "Tin tuyển dụng theo tháng", data.getMonthlyJobs());
            addMonthly(sheet, rowIndex + data.getMonthlyUsers().size() + data.getMonthlyJobs().size() + 4, "Đơn ứng tuyển theo tháng", data.getMonthlyApplications());
            addMonthly(sheet, rowIndex + data.getMonthlyUsers().size() + data.getMonthlyJobs().size() + data.getMonthlyApplications().size() + 6, "Doanh thu theo tháng", data.getMonthlyRevenue());
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private int addRow(Sheet sheet, int index, String label, Object value) {
        Row row = sheet.createRow(index);
        row.createCell(0).setCellValue(label);
        Cell valueCell = row.createCell(1);
        if (value instanceof Number number) valueCell.setCellValue(number.doubleValue());
        else valueCell.setCellValue(value(value));
        return index + 1;
    }

    private int addMap(Sheet sheet, int index, String title, Map<String, Integer> values) {
        index = addRow(sheet, index, title, "Số lượng");
        for (Map.Entry<String, Integer> entry : values.entrySet()) index = addRow(sheet, index, entry.getKey(), entry.getValue());
        return index + 1;
    }

    private void addMonthly(Sheet sheet, int index, String title, List<AdminStatisticDTO.MonthlyCountDTO> values) {
        addRow(sheet, index, title, "Số lượng");
        int rowIndex = index + 1;
        for (AdminStatisticDTO.MonthlyCountDTO value : values) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(value.getMonth());
            row.createCell(1).setCellValue(value.getAmount() == 0 ? value.getCount() : value.getAmount());
        }
    }

    private byte[] createPdf(AdminStatisticDTO data) {
        String newline = Character.toString((char) 10);
        StringBuilder body = new StringBuilder();
        body.append("ADMIN STATISTICS REPORT").append(newline);
        body.append("From: ").append(value(data.getFromDate())).append("  To: ").append(value(data.getToDate())).append(newline).append(newline);
        body.append("Users: ").append(data.getTotalUsers()).append(newline);
        body.append("Candidates: ").append(data.getTotalCandidates()).append(newline);
        body.append("Employers: ").append(data.getTotalEmployers()).append(newline);
        body.append("Companies: ").append(data.getTotalCompanies()).append(newline);
        body.append("Jobs: ").append(data.getTotalJobs()).append(newline);
        body.append("Applications: ").append(data.getTotalApplications()).append(newline);
        body.append("Active VIP users: ").append(data.getActiveVipUsers()).append(newline);
        body.append("Revenue: ").append(data.getTotalRevenue()).append(newline);
        return simplePdf(body.toString());
    }

    private byte[] simplePdf(String text) {
        String newline = Character.toString((char) 10);
        StringBuilder stream = new StringBuilder("BT");
        stream.append(newline).append("/F1 11 Tf").append(newline).append("50 780 Td").append(newline);
        for (String line : text.split(newline, -1)) {
            stream.append("(").append(pdfEscape(toAscii(line))).append(") Tj").append(newline).append("0 -16 Td").append(newline);
        }
        stream.append("ET");
        byte[] streamBytes = stream.toString().getBytes(StandardCharsets.ISO_8859_1);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write("%PDF-1.4".getBytes(StandardCharsets.ISO_8859_1));
            out.write(newline.getBytes(StandardCharsets.ISO_8859_1));
            int[] offsets = new int[6];
            offsets[1] = out.size(); writeAscii(out, "1 0 obj<< /Type /Catalog /Pages 2 0 R >>endobj" + newline);
            offsets[2] = out.size(); writeAscii(out, "2 0 obj<< /Type /Pages /Kids [3 0 R] /Count 1 >>endobj" + newline);
            offsets[3] = out.size(); writeAscii(out, "3 0 obj<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>endobj" + newline);
            offsets[4] = out.size(); writeAscii(out, "4 0 obj<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>endobj" + newline);
            offsets[5] = out.size(); writeAscii(out, "5 0 obj<< /Length " + streamBytes.length + " >>stream" + newline);
            out.write(streamBytes); writeAscii(out, newline + "endstream endobj" + newline);
            int xref = out.size(); writeAscii(out, "xref" + newline + "0 6" + newline + "0000000000 65535 f " + newline);
            for (int i = 1; i <= 5; i++) writeAscii(out, String.format("%010d 00000 n %s", offsets[i], newline));
            writeAscii(out, "trailer<< /Size 6 /Root 1 0 R >>" + newline + "startxref" + newline + xref + newline + "%%EOF");
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Không thể tạo PDF", e);
        }
    }

    private void writeAscii(ByteArrayOutputStream out, String value) throws IOException {
        out.write(value.getBytes(StandardCharsets.ISO_8859_1));
    }

    private String pdfEscape(String value) { return value.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)"); }
    private String toAscii(String value) { return java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "").replace('Đ', 'D').replace('đ', 'd'); }
    private String value(Object value) { return value == null ? "" : String.valueOf(value); }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try { return LocalDate.parse(raw); }
        catch (DateTimeParseException e) { throw new IllegalArgumentException("Ngày phải có định dạng YYYY-MM-DD"); }
    }

    private void sendFile(HttpServletResponse resp, String contentType, String fileName, byte[] content) throws IOException {
        resp.setContentType(contentType);
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        resp.setContentLength(content.length);
        resp.getOutputStream().write(content);
    }

    private void writeJson(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(gson.toJson(body));
    }
}
