import React, { useEffect, useState, useCallback } from 'react';
import { Input, Select, Spin, Empty, Tag, Typography, Pagination } from 'antd';
import {
  SearchOutlined, EnvironmentOutlined, DollarOutlined,
  TeamOutlined, ClockCircleOutlined, FireOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { jobApi } from '../api/services';
import AppLayout from '../components/AppLayout';
import dayjs from 'dayjs';

const { Title, Text } = Typography;
const { Option } = Select;
const GREEN = '#00b14f';

const STATUS_LABELS = ['Đang tuyển', 'Tạm dừng', 'Đã đóng'];
const STATUS_COLORS = ['#e8f9f0', '#fff7e6', '#f5f5f5'];
const STATUS_TEXT = ['#00b14f', '#fa8c16', '#999'];

function getInitials(title = '') {
  return title.trim().charAt(0).toUpperCase() || 'J';
}

function JobCard({ job, onClick }) {
  const statusIdx = job.status ?? 0;
  return (
    <div className="job-card" onClick={onClick} style={{
      background: '#fff', borderRadius: 12, padding: '20px',
      border: '1px solid #eee', cursor: 'pointer',
      display: 'flex', gap: 16, alignItems: 'flex-start',
    }}>
      <div className="company-logo" style={{ fontSize: 20 }}>
        {getInitials(job.title)}
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
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px 16px', marginTop: 8 }}>
          <Text style={{ fontSize: 13, color: '#666' }}>
            <EnvironmentOutlined style={{ marginRight: 4, color: '#aaa' }} />{job.location || 'Chưa cập nhật'}
          </Text>
          <Text style={{ fontSize: 13, color: GREEN, fontWeight: 600 }}>
            <DollarOutlined style={{ marginRight: 4 }} />
            {job.minSalary || job.maxSalary
              ? `${(job.minSalary || 0).toLocaleString('vi-VN')}${job.maxSalary ? ' – ' + job.maxSalary.toLocaleString('vi-VN') : ''} VNĐ`
              : 'Thỏa thuận'}
          </Text>
          <Text style={{ fontSize: 13, color: '#666' }}>
            <TeamOutlined style={{ marginRight: 4, color: '#aaa' }} />{job.quantity} người
          </Text>
        </div>
        <div style={{ marginTop: 8, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          {job.experience && (
            <Tag color="blue" style={{ borderRadius: 4, fontSize: 12 }}>{job.experience}</Tag>
          )}
          <Text style={{ fontSize: 12, color: '#bbb', marginLeft: 'auto' }}>
            <ClockCircleOutlined style={{ marginRight: 4 }} />
            {job.postedAt ? dayjs(job.postedAt).fromNow() : ''}
          </Text>
        </div>
      </div>
    </div>
  );
}

export default function JobListPage() {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [title, setTitle] = useState('');
  const [location, setLocation] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [page, setPage] = useState(1);
  const [pageSize] = useState(20);
  const [totalItems, setTotalItems] = useState(0);
  const navigate = useNavigate();

  const fetchJobs = useCallback((p = 1, t = title, loc = location, st = statusFilter) => {
    setLoading(true);
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
        }
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [pageSize]);

  useEffect(() => {
    setPage(1);
    fetchJobs(1, title, location, statusFilter);
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
          <div style={{ marginTop: 24, display: 'flex', gap: 10, maxWidth: 780, margin: '24px auto 0', flexWrap: 'wrap' }}>
            <Input size="large" prefix={<SearchOutlined style={{ color: '#aaa' }} />}
              placeholder="Tên vị trí..."
              value={title} onChange={(e) => setTitle(e.target.value)}
              style={{ flex: 2, minWidth: 160, borderRadius: 8, height: 48 }} />
            <Input size="large" prefix={<EnvironmentOutlined style={{ color: '#aaa' }} />}
              placeholder="Địa điểm..."
              value={location} onChange={(e) => setLocation(e.target.value)}
              style={{ flex: 1, minWidth: 120, borderRadius: 8, height: 48 }} />
            <Select size="large" value={statusFilter} onChange={setStatusFilter}
              style={{ width: 160, borderRadius: 8 }}>
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
        ) : jobs.length === 0 ? (
          <Empty description="Không tìm thấy việc làm phù hợp" style={{ padding: 60 }} />
        ) : (
          <div>
            <Text style={{ color: '#888', fontSize: 13, marginBottom: 16, display: 'block' }}>
              Tìm thấy <strong>{totalItems}</strong> việc làm
            </Text>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
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
          </div>
        )}
      </div>
    </AppLayout>
  );
}
