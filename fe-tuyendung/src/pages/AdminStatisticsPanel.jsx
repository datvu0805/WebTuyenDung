import React, { useEffect, useMemo, useState } from 'react';
import {
  Alert, Button, Card, Col, DatePicker, Empty, message, Progress, Row, Space, Spin,
  Statistic, Table, Tabs, Tag, Typography,
} from 'antd';
import {
  BarChartOutlined, CheckCircleOutlined, ClockCircleOutlined, CloseCircleOutlined,
  DollarOutlined, DownloadOutlined, FileTextOutlined, ReloadOutlined, TeamOutlined,
  UserOutlined,
} from '@ant-design/icons';
import dayjs from 'dayjs';
import { adminStatisticApi } from '../api/services';

const { Text, Title } = Typography;
const { RangePicker } = DatePicker;
const COLORS = ['#1677ff', '#00b14f', '#fa8c16', '#722ed1', '#13c2c2'];

const REPORTS = [
  { value: 'overview', label: 'Tổng quan hệ thống' },
  { value: 'companies', label: 'Thống kê theo công ty' },
  { value: 'recruitment', label: 'Hiệu quả tuyển dụng' },
];

const formatNumber = value => Number(value || 0).toLocaleString('vi-VN');
const formatMoney = value => `${formatNumber(value)} ₫`;
const formatPercent = value => `${Number(value || 0).toFixed(1)}%`;

function getFilename(headers, fallback) {
  const disposition = headers?.['content-disposition'] || '';
  const match = disposition.match(/filename="?([^";]+)"?/i);
  return match?.[1] || fallback;
}

function downloadBlob(response, fallback) {
  const blobUrl = URL.createObjectURL(response.data);
  const link = document.createElement('a');
  link.href = blobUrl;
  link.download = getFilename(response.headers, fallback);
  document.body.appendChild(link);
  link.click();
  link.remove();
  URL.revokeObjectURL(blobUrl);
}

function normalizeReportData(report, payload) {
  if (report === 'companies') {
    if (Array.isArray(payload)) return payload;
    if (Array.isArray(payload?.companies)) return payload.companies;
    if (Array.isArray(payload?.rows)) return payload.rows;
    if (Array.isArray(payload?.data)) return payload.data;
    return [];
  }
  return payload;
}

function Kpi({ title, value, icon, color, formatter = formatNumber }) {
  return <Card size="small"><Statistic title={title} value={value || 0} formatter={formatter} prefix={React.cloneElement(icon, { style: { color } })} /></Card>;
}

function Breakdown({ title, data = {} }) {
  const rows = Object.entries(data).map(([label, value], index) => ({ key: label, label, value: Number(value || 0), color: COLORS[index % COLORS.length] }));
  if (!rows.length) return <Card title={title}><Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="Chưa có dữ liệu" /></Card>;
  const total = rows.reduce((sum, row) => sum + row.value, 0) || 1;
  return <Card title={title} size="small"><Space direction="vertical" style={{ width: '100%' }}>{rows.map(row => <div key={row.key}><div style={{ display: 'flex', justifyContent: 'space-between' }}><Space><Tag color={row.color}>{row.label}</Tag><Text>{formatNumber(row.value)}</Text></Space><Text type="secondary">{formatPercent(row.value * 100 / total)}</Text></div><Progress percent={Math.round(row.value * 100 / total)} showInfo={false} strokeColor={row.color} size="small" /></div>)}</Space><Table size="small" pagination={false} style={{ marginTop: 12 }} dataSource={rows} columns={[{ title: 'Nhãn', dataIndex: 'label' }, { title: 'Số lượng', dataIndex: 'value', render: formatNumber }]} /></Card>;
}

function Trend({ title, data = [], amount = false }) {
  if (!data.length) return <Card title={title} size="small"><Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="Chưa có dữ liệu" /></Card>;
  const max = Math.max(...data.map(item => Number(amount ? item.amount : item.count) || 0), 1);
  return <Card title={title} size="small"><div role="img" aria-label={title} style={{ display: 'flex', alignItems: 'flex-end', gap: 8, height: 170, padding: '12px 4px 0' }}>{data.map(item => { const value = Number(amount ? item.amount : item.count) || 0; return <div key={item.month} title={`${item.month}: ${amount ? formatMoney(value) : formatNumber(value)}`} style={{ flex: 1, height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'flex-end', alignItems: 'center', minWidth: 28 }}><Text type="secondary" style={{ fontSize: 11 }}>{formatNumber(value)}</Text><div style={{ width: '75%', maxWidth: 42, height: `${Math.max(value / max * 125, 4)}px`, background: '#1677ff', borderRadius: '4px 4px 0 0' }} /><Text style={{ fontSize: 11, marginTop: 4 }}>{item.month}</Text></div>; })}</div><Table size="small" pagination={false} style={{ marginTop: 12 }} dataSource={data.map(item => ({ ...item, key: item.month }))} columns={[{ title: 'Tháng', dataIndex: 'month' }, { title: amount ? 'Giá trị' : 'Số lượng', dataIndex: amount ? 'amount' : 'count', render: amount ? formatMoney : formatNumber }]} /></Card>;
}

function OverviewReport({ data }) {
  return <><Row gutter={[12, 12]}><Col xs={12} sm={8} md={6}><Kpi title="Người dùng" value={data.totalUsers} icon={<UserOutlined />} color="#1677ff" /></Col><Col xs={12} sm={8} md={6}><Kpi title="Ứng viên" value={data.totalCandidates} icon={<TeamOutlined />} color="#00b14f" /></Col><Col xs={12} sm={8} md={6}><Kpi title="Tin tuyển dụng" value={data.totalJobs} icon={<FileTextOutlined />} color="#fa8c16" /></Col><Col xs={12} sm={8} md={6}><Kpi title="Đơn ứng tuyển" value={data.totalApplications} icon={<CheckCircleOutlined />} color="#722ed1" /></Col></Row><Row gutter={[12, 12]} style={{ marginTop: 12 }}><Col xs={24} md={12}><Breakdown title="Người dùng theo vai trò" data={data.totalUserByRole} /></Col><Col xs={24} md={12}><Breakdown title="Tin tuyển dụng theo trạng thái" data={data.jobsByStatus} /></Col><Col xs={24} md={12}><Trend title="Người dùng mới theo tháng" data={data.monthlyUsers} /></Col><Col xs={24} md={12}><Trend title="Tin tuyển dụng theo tháng" data={data.monthlyJobs} /></Col></Row></>;
}

function CompanyReport({ rows = [] }) {
  const safeRows = Array.isArray(rows) ? rows : [];
  const columns = [
    { title: 'Công ty', dataIndex: 'companyName', fixed: 'left' },
    { title: 'Tin tuyển dụng', dataIndex: 'totalJobs', render: formatNumber, sorter: (a, b) => a.totalJobs - b.totalJobs },
    { title: 'Tin có ứng viên', dataIndex: 'jobsWithApplicants', render: formatNumber },
    { title: 'Tin có người nhận', dataIndex: 'jobsWithHires', render: formatNumber },
    { title: 'Đơn ứng tuyển', dataIndex: 'totalApplications', render: formatNumber },
    { title: 'Đơn được nhận', dataIndex: 'hiredApplications', render: formatNumber },
    { title: 'Tỷ lệ tin có người nhận', dataIndex: 'jobFillRate', render: formatPercent },
    { title: 'Tỷ lệ đơn được nhận', dataIndex: 'applicationHireRate', render: formatPercent },
  ];
  return <Card title={<Title level={5} style={{ margin: 0 }}>Hiệu quả theo từng công ty</Title>} size="small"><Text type="secondary">Tỷ lệ tin có người nhận = tin có ít nhất một đơn trạng thái “Nhận việc” / tổng tin của công ty.</Text><Table style={{ marginTop: 12 }} rowKey="companyId" dataSource={safeRows} columns={columns} scroll={{ x: 1050 }} pagination={{ pageSize: 10, showSizeChanger: true }} locale={{ emptyText: <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="Chưa có dữ liệu công ty" /> }} /></Card>;
}

function RecruitmentReport({ data }) {
  const metrics = [{ label: 'Tin có ứng viên', value: data.jobApplicantRate, help: `${formatNumber(data.jobsWithApplicants)} / ${formatNumber(data.totalJobs)} tin` }, { label: 'Tin có người nhận', value: data.jobHireRate, help: `${formatNumber(data.jobsWithHires)} / ${formatNumber(data.totalJobs)} tin` }, { label: 'Tỷ lệ đơn được nhận', value: data.applicationHireRate, help: `${formatNumber(data.hiredApplications)} / ${formatNumber(data.totalApplications)} đơn` }];
  return <><Row gutter={[12, 12]}>{metrics.map(metric => <Col xs={24} md={8} key={metric.label}><Card><Statistic title={metric.label} value={metric.value || 0} precision={1} suffix="%" /><Progress percent={Number(metric.value || 0)} showInfo={false} /><Text type="secondary">{metric.help}</Text></Card></Col>)}</Row><Card style={{ marginTop: 12 }}><Row gutter={[12, 12]}><Col xs={12} md={6}><Kpi title="Tổng tin" value={data.totalJobs} icon={<FileTextOutlined />} color="#1677ff" /></Col><Col xs={12} md={6}><Kpi title="Tin có ứng viên" value={data.jobsWithApplicants} icon={<UserOutlined />} color="#00b14f" /></Col><Col xs={12} md={6}><Kpi title="Tin có người nhận" value={data.jobsWithHires} icon={<CheckCircleOutlined />} color="#fa8c16" /></Col><Col xs={12} md={6}><Kpi title="Đơn được nhận" value={data.hiredApplications} icon={<CheckCircleOutlined />} color="#722ed1" /></Col></Row></Card></>;
}

export default function AdminStatisticsPanel() {
  const [report, setReport] = useState('overview');
  const [range, setRange] = useState(null);
  const [dataByReport, setDataByReport] = useState({});
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [exporting, setExporting] = useState('');
  const params = useMemo(() => ({ ...(range?.[0] ? { from: range[0].format('YYYY-MM-DD') } : {}), ...(range?.[1] ? { to: range[1].format('YYYY-MM-DD') } : {}) }), [range]);

  const load = async () => {
    setLoading(true); setError('');
    try {
      const response = await adminStatisticApi.getReport(report === 'overview' ? undefined : report, params);
      if (!response.data.success) throw new Error(response.data.message || 'Không thể tải báo cáo');
      setDataByReport(previous => ({ ...previous, [report]: normalizeReportData(report, response.data.data) }));
      setError('');
    } catch (err) { setError(err.message || 'Không thể tải báo cáo'); setDataByReport(previous => ({ ...previous, [report]: null })); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, [report, params.from, params.to]);

  const exportReport = async format => {
    setExporting(format);
    try { const response = await adminStatisticApi.export(format, { ...params, ...(report === 'overview' ? {} : { report }) }); downloadBlob(response, `bao-cao-${report}.${format}`); }
    catch { message.error(`Xuất ${format.toUpperCase()} thất bại`); }
    finally { setExporting(''); }
  };

  const data = dataByReport[report];
  if (loading && data === undefined) return <div style={{ textAlign: 'center', padding: 80 }}><Spin size="large" /></div>;
  const activeContent = !data ? <Empty description="Không có dữ liệu báo cáo" />
    : report === 'overview' ? <OverviewReport data={data} />
      : report === 'companies' ? <CompanyReport rows={data} />
        : <RecruitmentReport data={data} />;
  return <div><Card size="small" style={{ marginBottom: 16 }}><Space wrap><Text strong>Khoảng thời gian:</Text><RangePicker value={range} onChange={setRange} disabledDate={current => current && current > dayjs().endOf('day')} allowClear /><Button onClick={() => setRange([dayjs().subtract(6, 'month').startOf('month'), dayjs()])}>6 tháng</Button><Button icon={<ReloadOutlined />} onClick={load} loading={loading}>Làm mới</Button><Button icon={<DownloadOutlined />} onClick={() => exportReport('xlsx')} loading={exporting === 'xlsx'}>Excel</Button><Button icon={<DownloadOutlined />} onClick={() => exportReport('pdf')} loading={exporting === 'pdf'}>PDF</Button></Space><div style={{ marginTop: 8 }}><Text type="secondary">{params.from || 'Tất cả'} → {params.to || 'Hôm nay'} · Đang xem: {REPORTS.find(item => item.value === report)?.label}</Text></div></Card>{error && <Alert type="error" showIcon message={error} action={<Button size="small" onClick={load}>Thử lại</Button>} style={{ marginBottom: 16 }} />}<Tabs activeKey={report} onChange={setReport} items={REPORTS.map(item => ({ key: item.value, label: item.label }))} />{activeContent}</div>;
}
