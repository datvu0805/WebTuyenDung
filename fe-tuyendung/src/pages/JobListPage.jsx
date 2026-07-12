import React, { useEffect, useState, useCallback, useRef } from 'react';
import { Input, Select, Spin, Empty, Tag, Typography, Pagination } from 'antd';
import {
  SearchOutlined, EnvironmentOutlined, DollarOutlined,
  TeamOutlined, ClockCircleOutlined, FireOutlined, BankOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { jobApi } from '../api/services';
import AppLayout from '../components/AppLayout';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/vi';

dayjs.extend(relativeTime);
dayjs.locale('vi');

const { Title, Text } = Typography;
const { Option } = Select;
const GREEN = '#00b14f';

const STATUS_LABELS = ['Đang tuyển', 'Tạm dừng', 'Đã đóng'];
const STATUS_COLORS = ['#e8f9f0', '#fff7e6', '#f5f5f5'];
const STATUS_TEXT = ['#00b14f', '#fa8c16', '#999'];

const LOGO_COLORS = ['#e8f9f0','#e6f4ff','#fff7e6','#f9f0ff','#fff1f0','#f0fff4'];
const LOGO_TEXT   = ['#00b14f','#1677ff','#fa8c16','#722ed1','#f5222d','#389e0d'];

function getCompanyInitials(name = '') {
  const words = name.trim().split(/\s+/);
  if (words.length >= 2) return (words[0][0] + words[1][0]).toUpperCase();
  return name.trim().slice(0, 2).toUpperCase() || 'CT';
}

function colorIndex(id = 0) { return id % LOGO_COLORS.length; }

function JobCard({ job, onClick }) {
  const statusIdx = job.status ?? 0;
  const ci = colorIndex(job.id);
  return (
    <div
      className="job-card"
      onClick={onClick}
      style={{
        background: '#fff', borderRadius: 12, padding: '20px',
        border: '1px solid #eee', cursor: 'pointer',
        display: 'flex', gap: 16, alignItems: 'flex-start',
        transition: 'box-shadow 0.15s, border-color 0.15s',
      }}
      onMouseEnter={(e) => { e.currentTarget.style.boxShadow = '0 4px 16px rgba(0,177,79,0.12)'; e.currentTarget.style.borderColor = '#b7ebd0'; }}
      onMouseLeave={(e) => { e.currentTarget.style.boxShadow = ''; e.currentTarget.style.borderColor = '#eee'; }}
    >
      {/* Logo */}
      <div style={{
        width: 52, height: 52, borderRadius: 10, flexShrink: 0,
        background: LOGO_COLORS[ci], color: LOGO_TEXT[ci],
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        fontWeight: 700, fontSize: 16, border: `1px solid ${LOGO_COLORS[ci]}`,
      }}>
        {getCompanyInitials(job.companyName || job.title)}
      </div>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 8, flexWrap: 'wrap' }}>
          <Text strong style={{ fontSize: 15, color: '#1a1a1a', lineHeight: 1.4 }}>{job.title}</Text>
          <span style={{
            background: STATUS_COLORS[statusIdx], color: STATUS_TEXT[statusIdx],
            borderRadius: 4, padding: '2px 10px', fontSize: 12, fontWeight: 600, flexShrink: 0,
          }}>
            {STATUS_LABELS[statusIdx]}
          </span>
        </div>
        {job.companyName && (
          <div style={{ marginTop: 4 }}>
            <Text style={{ fontSize: 13, color: GREEN, fontWeight: 500 }}>
              <BankOutlined style={{ marginRight: 4 }} />{job.companyName}
            </Text>
          </div>
        )}
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px 16px', marginTop: 8 }}>
          <Text style={{ fontSize: 13, color: '#666' }}>
            <EnvironmentOutlined style={{ marginRight: 4, color: '#aaa' }} />{job.location || 'Chưa cập nhật'}
          </Text>
          <Text style={{ fontSize: 13, color: GREEN, fontWeight: 600 }}>
            <DollarOutlined style={{ marginRight: 4 }} />
            {job.minSalary || job.maxSalary
              ? `${(job.minSalary || 0).toLocaleString('vi-VN')}${job.maxSalary ? ' – ' + job.maxSalary.toLocaleString('vi-VN') : ''} ${job.currency || 'VNĐ'}`
              : 'Thỏa thuận'}
          </Text>
          <Text style={{ fontSize: 13, color: '#666' }}>
            <TeamOutlined style={{ marginRight: 4, color: '#aaa' }} />{job.quantity} người
          </Text>
        </div>
        <div style={{ marginTop: 8, display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 4 }}>
          <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
            {job.experience && <Tag color="blue" style={{ borderRadius: 4, fontSize: 12 }}>{job.experience}</Tag>}
            {job.applicationDeadline && (
              <Tag color="orange" style={{ borderRadius: 4, fontSize: 12 }}>
                Hạn: {dayjs(job.applicationDeadline).format('DD/MM/YYYY')}
              </Tag>
            )}
          </div>
          <Text style={{ fontSize: 12, color: '#bbb' }}>
            <ClockCircleOutlined style={{ marginRight: 4 }} />
            {job.postedAt ? dayjs(job.postedAt).fromNow() : ''}
          </Text>
        </div>
      </div>
    </div>
  );
}

export default function JobListPage() {
  const [jobs, setJobs]         = useState([]);
  const [loading, setLoading]   = useState(true);
  // separate display state so list stays visible while refetching
  const [fetching, setFetching] = useState(false);

  const [title, setTitle]               = useState('');
  const [location, setLocation]         = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [provinces, setProvinces]       = useState([]);

  const [page, setPage]         = useState(1);
  const [pageSize]               = useState(20);
  const [totalItems, setTotalItems] = useState(0);
  const navigate = useNavigate();
  const debounceRef = useRef(null);

  // Load tỉnh/thành từ provinces API
  useEffect(() => {
    fetch('https://provinces.open-api.vn/api/p/')
      .then((r) => r.json())
      .then((data) => setProvinces(data || []))
      .catch(() => {});
  }, []);

  const fetchJobs = useCallback((p, t, loc, st) => {
    setFetching(true);
    const params = { page: p, size: pageSize };
    if (t) params.title = t;
    if (loc) params.location = loc;
    if (st !== '') params.status = st;

    jobApi.search(params)
      .then((res) => {
        if (res.data.success) {
          const pageData = res.data.data;
          setJobs(pageData.content || []);
          setTotalItems(pageData.totalItems || 0);
        } else {
          setJobs([]);
          setTotalItems(0);
        }
      })
      .catch((err) => {
        if (err?.response?.status === 401) window.location.href = '/login';
        setJobs([]);
        setTotalItems(0);
      })
      .finally(() => { setFetching(false); setLoading(false); });
  }, [pageSize]);

  // Initial load
  useEffect(() => { fetchJobs(1, '', '', ''); }, [fetchJobs]);

  // Debounce search inputs (500ms) — no full spinner, just subtle loading indicator
  useEffect(() => {
    clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      setPage(1);
      fetchJobs(1, title, location, statusFilter);
    }, 500);
    return () => clearTimeout(debounceRef.current);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [title, location, statusFilter]);

  const handlePageChange = (p) => {
    setPage(p);
    fetchJobs(p, title, location, statusFilter);
  };

  return (
    <AppLayout>
      {/* Hero */}
      <div style={{ background: 'linear-gradient(135deg, #00b14f 0%, #007a38 100%)', padding: '48px 24px 40px' }}>
        <div style={{ maxWidth: 860, margin: '0 auto', textAlign: 'center' }}>
          <Title level={2} style={{ color: '#fff', marginBottom: 8, fontSize: 28 }}>
            <FireOutlined style={{ marginRight: 8 }} />Khám phá cơ hội việc làm
          </Title>
          <Text style={{ color: 'rgba(255,255,255,0.85)', fontSize: 15 }}>
            {totalItems} tin tuyển dụng đang chờ bạn
          </Text>
          <div style={{ marginTop: 24, display: 'flex', gap: 10, maxWidth: 860, margin: '24px auto 0', flexWrap: 'wrap' }}>
            <Input
              size="large"
              prefix={<SearchOutlined style={{ color: '#aaa' }} />}
              placeholder="Tên vị trí..."
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              style={{ flex: 2, minWidth: 160, borderRadius: 8, height: 48 }}
            />
            <Select
              size="large"
              showSearch
              allowClear
              placeholder="Tỉnh / Thành phố..."
              value={location || undefined}
              onChange={(val) => setLocation(val || '')}
              filterOption={(input, option) =>
                option.label.toLowerCase().includes(input.toLowerCase())
              }
              style={{ flex: 1, minWidth: 160 }}
              options={provinces.map((p) => ({ value: p.name, label: p.name }))}
            />
            <Select
              size="large"
              value={statusFilter}
              onChange={setStatusFilter}
              style={{ width: 150 }}
            >
              <Option value="">Tất cả</Option>
              <Option value="0">Đang tuyển</Option>
              <Option value="1">Tạm dừng</Option>
              <Option value="2">Đã đóng</Option>
            </Select>
          </div>
        </div>
      </div>

      <div style={{ maxWidth: 920, margin: '0 auto', padding: '28px 16px' }}>
        {loading ? (
          <div style={{ textAlign: 'center', padding: 80 }}><Spin size="large" /></div>
        ) : (
          <div>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 16 }}>
              <Text style={{ color: '#888', fontSize: 13 }}>
                Tìm thấy <strong>{totalItems}</strong> việc làm
              </Text>
              {fetching && <Spin size="small" />}
            </div>
            {jobs.length === 0 ? (
              <Empty description="Không tìm thấy việc làm phù hợp" style={{ padding: 60 }} />
            ) : (
              <>
                <div style={{ display: 'flex', flexDirection: 'column', gap: 12, opacity: fetching ? 0.6 : 1, transition: 'opacity 0.2s' }}>
                  {jobs.map((job) => (
                    <JobCard key={job.id} job={job} onClick={() => navigate(`/jobs/${job.id}`)} />
                  ))}
                </div>
                <div style={{ marginTop: 24, textAlign: 'center' }}>
                  <Pagination
                    current={page}
                    pageSize={pageSize}
                    total={totalItems}
                    onChange={handlePageChange}
                    showSizeChanger={false}
                  />
                </div>
              </>
            )}
          </div>
        )}
      </div>
    </AppLayout>
  );
}
