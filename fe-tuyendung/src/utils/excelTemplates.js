import ExcelJS from 'exceljs';

// Màu sắc header
const COLOR_COMPANY = '006B3F';   // xanh đậm
const COLOR_POSITION = '1D4ED8';  // xanh dương
const COLOR_JOB = '7E22CE';       // tím

function applyHeaderStyle(row, color) {
  row.eachCell((cell) => {
    cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF' + color } };
    cell.font = { bold: true, color: { argb: 'FFFFFFFF' }, size: 11 };
    cell.alignment = { vertical: 'middle', horizontal: 'center', wrapText: true };
    cell.border = {
      top: { style: 'medium', color: { argb: 'FF000000' } },
      left: { style: 'medium', color: { argb: 'FF000000' } },
      bottom: { style: 'medium', color: { argb: 'FF000000' } },
      right: { style: 'medium', color: { argb: 'FF000000' } },
    };
  });
  row.height = 30;
}

function applyDataRowStyle(row) {
  row.eachCell({ includeEmpty: true }, (cell) => {
    cell.border = {
      top: { style: 'thin', color: { argb: 'FFAAAAAA' } },
      left: { style: 'thin', color: { argb: 'FFAAAAAA' } },
      bottom: { style: 'thin', color: { argb: 'FFAAAAAA' } },
      right: { style: 'thin', color: { argb: 'FFAAAAAA' } },
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
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  a.click();
  URL.revokeObjectURL(url);
}

// ── Template CÔNG TY ──────────────────────────────────────
export async function downloadCompanyTemplate() {
  const wb = new ExcelJS.Workbook();
  const ws = wb.addWorksheet('Danh sách công ty');

  ws.columns = [
    { header: 'Tên công ty (bắt buộc)', key: 'companyName', width: 35 },
    { header: 'Mô tả', key: 'description', width: 50 },
  ];

  applyHeaderStyle(ws.getRow(1), COLOR_COMPANY);

  const samples = [
    ['ABC Technology', 'Công ty phần mềm hàng đầu Việt Nam'],
    ['XYZ Corp', 'Công ty thiết kế và truyền thông'],
  ];
  samples.forEach((s) => {
    const row = ws.addRow(s);
    applyDataRowStyle(row);
  });

  await downloadWorkbook(wb, 'mau_cong_ty.xlsx');
}

// ── Template CHỨC DANH ────────────────────────────────────
export async function downloadPositionTemplate() {
  const wb = new ExcelJS.Workbook();
  const ws = wb.addWorksheet('Danh sách chức danh');

  ws.columns = [
    { header: 'Tên chức danh (bắt buộc)', key: 'name', width: 35 },
    { header: 'Mô tả', key: 'description', width: 50 },
  ];

  applyHeaderStyle(ws.getRow(1), COLOR_POSITION);

  const samples = [
    ['Lập trình viên Backend', 'Phát triển API, xử lý logic nghiệp vụ'],
    ['Frontend Developer', 'Xây dựng giao diện người dùng'],
    ['Fullstack Developer', 'Phát triển cả frontend lẫn backend'],
  ];
  samples.forEach((s) => {
    const row = ws.addRow(s);
    applyDataRowStyle(row);
  });

  await downloadWorkbook(wb, 'mau_chuc_danh.xlsx');
}

// ── Template CÔNG VIỆC ────────────────────────────────────
export async function downloadJobTemplate({ companies = [], jobPositions = [] } = {}) {
  const wb = new ExcelJS.Workbook();
  const ws = wb.addWorksheet('Danh sách công việc');

  // Sheet ẩn chứa danh sách dropdown
  const wsCompany = wb.addWorksheet('_companies');
  wsCompany.state = 'veryHidden';
  companies.forEach((c, i) => {
    wsCompany.getCell(`A${i + 1}`).value = c.id;
    wsCompany.getCell(`B${i + 1}`).value = c.companyName || c.name;
    wsCompany.getCell(`C${i + 1}`).value = `${c.companyName || c.name} (ID:${c.id})`;
  });

  const wsPos = wb.addWorksheet('_positions');
  wsPos.state = 'veryHidden';
  jobPositions.forEach((p, i) => {
    wsPos.getCell(`A${i + 1}`).value = p.id;
    wsPos.getCell(`B${i + 1}`).value = p.name;
    wsPos.getCell(`C${i + 1}`).value = `${p.name} (ID:${p.id})`;
  });

  // Tên cột — 15 cột
  ws.columns = [
    { header: 'Tiêu đề công việc (bắt buộc)', key: 'title', width: 40 },
    { header: 'Mô tả công việc', key: 'description', width: 40 },
    { header: 'Địa điểm', key: 'location', width: 20 },
    { header: 'Kinh nghiệm', key: 'experience', width: 18 },
    { header: 'Lương tối thiểu', key: 'minSalary', width: 18 },
    { header: 'Lương tối đa', key: 'maxSalary', width: 18 },
    { header: 'Tiền tệ (VND/USD)', key: 'currency', width: 16 },
    { header: 'Số lượng', key: 'quantity', width: 12 },
    { header: 'Ngày đăng (yyyy-MM-dd)', key: 'postedAt', width: 22 },
    { header: 'Ngày hết hạn (yyyy-MM-dd)', key: 'expiredAt', width: 24 },
    { header: 'Hạn nộp HS (yyyy-MM-dd)', key: 'applicationDeadline', width: 24 },
    { header: 'Trạng thái (0=Tuyển/1=Tạm dừng/2=Đóng)', key: 'status', width: 36 },
    { header: 'Company ID', key: 'companyId', width: 14 },
    { header: 'Tên công ty (tham khảo)', key: 'companyName', width: 30 },
    { header: 'Chức danh ID', key: 'jobPositionId', width: 14 },
    { header: 'Tên chức danh (tham khảo)', key: 'jobPositionName', width: 30 },
  ];

  ws.columns = ws.columns || [];

  // Tái thiết lập columns rõ ràng
  ws.getRow(1).values = [
    'Tiêu đề công việc (bắt buộc)',
    'Mô tả công việc',
    'Địa điểm',
    'Kinh nghiệm',
    'Lương tối thiểu',
    'Lương tối đa',
    'Tiền tệ (VND/USD)',
    'Số lượng',
    'Ngày đăng (yyyy-MM-dd)',
    'Ngày hết hạn (yyyy-MM-dd)',
    'Hạn nộp HS (yyyy-MM-dd)',
    'Trạng thái (0=Tuyển/1=Tạm dừng/2=Đóng)',
    'Company ID',
    'Tên công ty (tham khảo)',
    'Chức danh ID',
    'Tên chức danh (tham khảo)',
  ];

  // Thiết lập độ rộng cột
  const colWidths = [40, 40, 20, 18, 18, 18, 16, 12, 22, 24, 24, 38, 14, 30, 14, 30];
  colWidths.forEach((w, i) => { ws.getColumn(i + 1).width = w; });

  applyHeaderStyle(ws.getRow(1), COLOR_JOB);

  // Dữ liệu mẫu
  const now = new Date();
  const fmt = (d) => d.toISOString().split('T')[0];
  const expire = new Date(now); expire.setMonth(expire.getMonth() + 2);
  const deadline = new Date(expire); deadline.setDate(deadline.getDate() - 1);

  const sampleCompanyId = companies.length > 0 ? companies[0].id : '';
  const sampleCompanyName = companies.length > 0 ? (companies[0].companyName || companies[0].name) : 'Nhập ID công ty từ cột bên';
  const samplePosId = jobPositions.length > 0 ? jobPositions[0].id : '';
  const samplePosName = jobPositions.length > 0 ? jobPositions[0].name : 'Nhập ID chức danh từ cột bên';

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
    sampleCompanyId,
    sampleCompanyName,
    samplePosId,
    samplePosName,
  ]);
  applyDataRowStyle(sampleRow);

  // Dropdown validation cho trạng thái (cột 12 = L)
  for (let r = 2; r <= 100; r++) {
    ws.getCell(`L${r}`).dataValidation = {
      type: 'list',
      allowBlank: true,
      formulae: ['"0,1,2"'],
      showErrorMessage: true,
      errorTitle: 'Trạng thái không hợp lệ',
      error: 'Chỉ nhập 0 (Đang tuyển), 1 (Tạm dừng), hoặc 2 (Đã đóng)',
    };
  }

  // Dropdown validation cho tiền tệ (cột 7 = G)
  for (let r = 2; r <= 100; r++) {
    ws.getCell(`G${r}`).dataValidation = {
      type: 'list',
      allowBlank: false,
      formulae: ['"VND,USD,EUR"'],
    };
  }

  // Sheet danh sách company và position để tham khảo (visible)
  if (companies.length > 0) {
    const wsRef = wb.addWorksheet('Danh sách công ty');
    wsRef.getRow(1).values = ['ID', 'Tên công ty', 'Mô tả'];
    applyHeaderStyle(wsRef.getRow(1), COLOR_COMPANY);
    wsRef.getColumn(1).width = 10;
    wsRef.getColumn(2).width = 35;
    wsRef.getColumn(3).width = 40;
    companies.forEach((c) => {
      const row = wsRef.addRow([c.id, c.companyName || c.name, c.description || '']);
      applyDataRowStyle(row);
    });
  }

  if (jobPositions.length > 0) {
    const wsPosRef = wb.addWorksheet('Danh sách chức danh');
    wsPosRef.getRow(1).values = ['ID', 'Tên chức danh', 'Mô tả'];
    applyHeaderStyle(wsPosRef.getRow(1), COLOR_POSITION);
    wsPosRef.getColumn(1).width = 10;
    wsPosRef.getColumn(2).width = 35;
    wsPosRef.getColumn(3).width = 40;
    jobPositions.forEach((p) => {
      const row = wsPosRef.addRow([p.id, p.name, p.description || '']);
      applyDataRowStyle(row);
    });
  }

  await downloadWorkbook(wb, 'mau_cong_viec.xlsx');
}
