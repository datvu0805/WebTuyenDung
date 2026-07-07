import React, { useEffect, useState } from 'react';
import { Table, Tag, Typography, message, Select, Avatar } from 'antd';
import { UserOutlined } from '@ant-design/icons';
import { applicationApi } from '../api/services';
import AppLayout from '../components/AppLayout';

const { Title, Text } = Typography;
const { Option } = Select;
const GREEN = '#00b14f';

const STATUS_MAP = {
  0: { label: 'Chờ duyệt', color: 'processing' },
  1: { label: 'Đã duyệt', color: 'success' },
  2: { label: 'Từ chối', color: 'error' },
  3: { label: 'Mời phỏng vấn', color: 'warning' },
};

export default function ApplicationManagePage() {
  const [apps, setApps] = useState([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    setLoading(true);
    applicationApi.getRecruiterList()
      .then((res) => { if (res.data.success) setApps(res.data.data || []); })
      .catch(() => message.error('Tải danh sách thất bại'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const updateStatus = async (id, status) => {
    try {
      const res = await applicationApi.updateStatus(id, status);
      if (res.data.success) {
        message.success('Cập nhật trạng thái thành công');
        setApps((prev) => prev.map((a) => (a.id === id ? { ...a, status } : a)));
      }
    } catch {
      message.error('Cập nhật thất bại');
    }
  };

  const columns = [
    {
      title: 'Ứng viên', dataIndex: 'candidateName', key: 'candidateName',
      render: (v) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <Avatar size={32} icon={<UserOutlined />} style={{ background: '#e8f9f0', color: GREEN, flexShrink: 0 }} />
          <Text strong>{v || 'Ứng viên'}</Text>
        </div>
      )
    },
    {
      title: 'Vị trí ứng tuyển', dataIndex: 'jobTitle', key: 'jobTitle',
      responsive: ['sm'],
      render: (v) => <Text style={{ color: '#555' }}>{v}</Text>
    },
    {
      title: 'Trạng thái', dataIndex: 'status', key: 'status', width: 140,
      render: (v) => <Tag color={STATUS_MAP[v]?.color}>{STATUS_MAP[v]?.label || 'Không rõ'}</Tag>
    },
    {
      title: 'Thư xin việc', dataIndex: 'coverLetter', key: 'coverLetter',
      ellipsis: true, responsive: ['lg'],
      render: (v) => <Text style={{ color: '#777', fontSize: 13 }}>{v}</Text>
    },
    {
      title: 'Cập nhật', key: 'action', width: 160,
      render: (_, record) => (
        <Select value={record.status} onChange={(val) => updateStatus(record.id, val)}
          style={{ width: 150, borderRadius: 8 }} size="small">
          <Option value={0}>Chờ duyệt</Option>
          <Option value={1}>Đã duyệt</Option>
          <Option value={2}>Từ chối</Option>
          <Option value={3}>Mời phỏng vấn</Option>
        </Select>
      ),
    },
  ];

  const statusCounts = Object.keys(STATUS_MAP).reduce((acc, k) => {
    acc[k] = apps.filter(a => a.status === Number(k)).length;
    return acc;
  }, {});

  return (
    <AppLayout>
      <div style={{ background: 'linear-gradient(135deg, #00b14f 0%, #007a38 100%)', padding: '28px 24px 56px' }}>
        <div style={{ maxWidth: 1100, margin: '0 auto' }}>
          <Title level={3} style={{ color: '#fff', marginBottom: 4 }}>Đơn ứng tuyển</Title>
          <Text style={{ color: 'rgba(255,255,255,0.8)' }}>Xem xét và cập nhật trạng thái ứng viên</Text>
        </div>
      </div>

      <div style={{ maxWidth: 1100, margin: '-28px auto 0', padding: '0 16px 32px' }}>
        {/* Status summary */}
        <div style={{ display: 'flex', gap: 10, marginBottom: 16, flexWrap: 'wrap' }}>
          {Object.entries(STATUS_MAP).map(([k, v]) => (
            <div key={k} style={{ background: '#fff', borderRadius: 10, padding: '12px 18px', boxShadow: '0 2px 6px rgba(0,0,0,0.07)', display: 'flex', align: 'center', gap: 8 }}>
              <Tag color={v.color} style={{ margin: 0 }}>{v.label}</Tag>
              <Text strong style={{ fontSize: 16 }}>{statusCounts[k]}</Text>
            </div>
          ))}
        </div>

        <div style={{ background: '#fff', borderRadius: 14, padding: '20px', boxShadow: '0 2px 8px rgba(0,0,0,0.07)' }}>
          <Table columns={columns} dataSource={apps} rowKey="id" loading={loading}
            scroll={{ x: 600 }} pagination={{ pageSize: 10 }} />
        </div>
      </div>
    </AppLayout>
  );
}
