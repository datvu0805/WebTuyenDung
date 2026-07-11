import ExcelJS from 'exceljs';

const COLOR_COMPANY  = '006B3F';
const COLOR_POSITION = '1D4ED8';
const COLOR_JOB      = '7E22CE';

function applyHeaderStyle(row, color) {
  row.eachCell((cell) => {
    cell.fill   = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF' + color } };
    cell.font   = { bold: true, color: { argb: 'FFFFFFFF' }, size: 11 };
    cell.alignment = { vertical: 'middle', horizontal: 'center', wrapText: true };
    cell.border = {
      top:    { style: 'medium', color: { argb: 'FF000000' } },
      left:   { style: 'medium', color: { argb: 'FF000000' } },
      bottom: { style: 'medium', color: { argb: 'FF000000' } },
      right:  { style: 'medium', color: { argb: 'FF000000' } },
    };
  });
  row.height = 30;
}

function applyDataRowStyle(row) {
  row.eachCell({ includeEmpty: true }, (cell) => {
    cell.border = {
      top:    { style: 'thin', color: { argb: 'FFAAAAAA' } },
      left:   { style: 'thin', color: { argb: 'FFAAAAAA' } },
      bottom: { style: 'thin', color: { argb: 'FFAAAAAA' } },
      right:  { style: 'thin', color: { argb: 'FFAAAAAA' } },
    };
    cell.alignment = { vertical: 'middle', wrapText: true };
  });
  row.height = 22;
}

async function downloadWorkbook(workbook, filename) {
  const buffer = await workbook.xlsx.writeBuffer();
  const blob = new Blob([buffer], {
    type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  });
  const url = URL.createObjectURL(blob);
  const a   = document.createElement('a');
  a.href = url; a.download = filename; a.click();
  URL.revokeObjectURL(url);
}

// ── Template CÔNG TY ──────────────────────────────────────────────────────────
export async function downloadCompanyTemplate() {
  const wb = new ExcelJS.Workbook();
  const ws = wb.addWorksheet('Danh sách công ty');

  ws.getRow(1).values = ['Tên công ty (bắt buộc)', 'Mô tả'];
  ws.getColumn(1).width = 35;
  ws.getColumn(2).width = 50;
  applyHeaderStyle(ws.getRow(1), COLOR_COMPANY);

  [
    ['ABC Technology', 'Công ty phần mềm hàng đầu Việt Nam'],
    ['XYZ Corp', 'Công ty thiết kế và truyền thông'],
  ].forEach((s) => applyDataRowStyle(ws.addRow(s)));

  await downloadWorkbook(wb, 'mau_cong_ty.xlsx');
}

// ── Template CHỨC DANH ────────────────────────────────────────────────────────
export async function downloadPositionTemplate() {
  const wb = new ExcelJS.Workbook();
  const ws = wb.addWorksheet('Danh sách chức danh');

  ws.getRow(1).values = ['Tên chức danh (bắt buộc)', 'Mô tả'];
  ws.getColumn(1).width = 35;
  ws.getColumn(2).width = 50;
  applyHeaderStyle(ws.getRow(1), COLOR_POSITION);

  [
    ['Lập trình viên Backend', 'Phát triển API, xử lý logic nghiệp vụ'],
    ['Frontend Developer',     'Xây dựng giao diện người dùng'],
    ['Fullstack Developer',    'Phát triển cả frontend lẫn backend'],
  ].forEach((s) => applyDataRowStyle(ws.addRow(s)));

  await downloadWorkbook(wb, 'mau_chuc_danh.xlsx');
}

// ── Template CÔNG VIỆC ────────────────────────────────────────────────────────
// Cột công ty & chức danh có dropdown dạng "Tên (ID:x)" lấy từ sheet ẩn.
// Backend parse regex để lấy ID.
//
// Layout (14 cột):
//  A  Tiêu đề công việc
//  B  Mô tả
//  C  Địa điểm
//  D  Kinh nghiệm
//  E  Lương tối thiểu
//  F  Lương tối đa
//  G  Tiền tệ          ← dropdown VND/USD/EUR
//  H  Số lượng
//  I  Ngày đăng
//  J  Ngày hết hạn
//  K  Hạn nộp HS
//  L  Trạng thái       ← dropdown 0/1/2
//  M  Công ty          ← dropdown "Tên (ID:x)" từ sheet ẩn _companies
//  N  Chức danh        ← dropdown "Tên (ID:x)" từ sheet ẩn _positions

export async function downloadJobTemplate({ companies = [], jobPositions = [] } = {}) {
  const wb = new ExcelJS.Workbook();

  // ── Sheet ẩn: _companies (cột A = "Tên (ID:x)")
  const wsC = wb.addWorksheet('_companies');
  wsC.state = 'veryHidden';
  companies.forEach((c, i) => {
    wsC.getCell(`A${i + 1}`).value = `${c.companyName || c.name} (ID:${c.id})`;
  });

  // ── Sheet ẩn: _positions (cột A = "Tên (ID:x)")
  const wsP = wb.addWorksheet('_positions');
  wsP.state = 'veryHidden';
  jobPositions.forEach((p, i) => {
    wsP.getCell(`A${i + 1}`).value = `${p.name} (ID:${p.id})`;
  });

  // ── Sheet chính
  const ws = wb.addWorksheet('Danh sách công việc');

  const headers = [
    'Tiêu đề công việc (bắt buộc)',
    'Mô tả công việc',
    'Địa điểm',
    'Kinh nghiệm',
    'Lương tối thiểu',
    'Lương tối đa',
    'Tiền tệ',
    'Số lượng',
    'Ngày đăng (yyyy-MM-dd)',
    'Ngày hết hạn (yyyy-MM-dd)',
    'Hạn nộp HS (yyyy-MM-dd)',
    'Trạng thái',
    'Công ty',
    'Chức danh',
  ];
  const colWidths = [42, 40, 20, 18, 18, 18, 12, 10, 22, 24, 24, 22, 35, 35];

  ws.getRow(1).values = headers;
  colWidths.forEach((w, i) => { ws.getColumn(i + 1).width = w; });
  applyHeaderStyle(ws.getRow(1), COLOR_JOB);

  // ── Dữ liệu mẫu dòng 2
  const now     = new Date();
  const fmt     = (d) => d.toISOString().split('T')[0];
  const expire  = new Date(now); expire.setMonth(expire.getMonth() + 2);
  const deadline = new Date(expire); deadline.setDate(deadline.getDate() - 1);

  const sampleCompany  = companies.length  > 0 ? `${companies[0].companyName || companies[0].name} (ID:${companies[0].id})`  : '';
  const samplePosition = jobPositions.length > 0 ? `${jobPositions[0].name} (ID:${jobPositions[0].id})` : '';

  const sampleRow = ws.addRow([
    'Java Backend Developer',
    'Phát triển REST API, tích hợp PostgreSQL, viết unit test',
    'Hà Nội',
    '2 năm',
    15000000,
    25000000,
    'VND',
    3,
    fmt(now),
    fmt(expire),
    fmt(deadline),
    0,
    sampleCompany,
    samplePosition,
  ]);
  applyDataRowStyle(sampleRow);

  // ── Dropdown validation cho từng dòng dữ liệu (2 → 200)
  const companyCount  = companies.length;
  const positionCount = jobPositions.length;

  for (let r = 2; r <= 200; r++) {
    // Trạng thái (cột L = 12)
    ws.getCell(`L${r}`).dataValidation = {
      type: 'list',
      allowBlank: true,
      formulae: ['"0,1,2"'],
      showInputMessage: true,
      promptTitle: 'Trạng thái',
      prompt: '0 = Đang tuyển  |  1 = Tạm dừng  |  2 = Đã đóng',
      showErrorMessage: true,
      errorTitle: 'Không hợp lệ',
      error: 'Chỉ nhập 0, 1 hoặc 2',
    };

    // Tiền tệ (cột G = 7)
    ws.getCell(`G${r}`).dataValidation = {
      type: 'list',
      allowBlank: false,
      formulae: ['"VND,USD,EUR"'],
      showInputMessage: true,
      promptTitle: 'Tiền tệ',
      prompt: 'Chọn đơn vị tiền tệ',
    };

    // Công ty (cột M = 13) — dropdown từ sheet ẩn _companies
    if (companyCount > 0) {
      ws.getCell(`M${r}`).dataValidation = {
        type: 'list',
        allowBlank: true,
        formulae: [`_companies!$A$1:$A$${companyCount}`],
        showErrorMessage: true,
        errorTitle: 'Công ty không hợp lệ',
        error: 'Vui lòng chọn từ danh sách',
      };
    }

    // Chức danh (cột N = 14) — dropdown từ sheet ẩn _positions
    if (positionCount > 0) {
      ws.getCell(`N${r}`).dataValidation = {
        type: 'list',
        allowBlank: true,
        formulae: [`_positions!$A$1:$A$${positionCount}`],
        showErrorMessage: true,
        errorTitle: 'Chức danh không hợp lệ',
        error: 'Vui lòng chọn từ danh sách',
      };
    }
  }

  await downloadWorkbook(wb, 'mau_cong_viec.xlsx');
}
