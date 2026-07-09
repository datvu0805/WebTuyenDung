import React, { useEffect, useState } from 'react';
import { Table, Avatar, Typography, Spin, message, Input } from 'antd';
import { UserOutlined, SearchOutlined } from '@ant-design/icons';
import { candidateApi } from '../api/services';
import AppLayout from '../components/AppLayout';

const { Title, Text } = Typography;
const GREEN = '#00b14f';

export default function CandidateListPage() {
  const [candidates, setCandidates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => {
    candidateApi.getAll()
      .then((res) => {
        if (res.data.success) setCandidates(res.data.data || []);
        else message.error(res.data.message);
      })
      .catch(() => message.error('Không thể tải danh sách'))
      .finally(() => setLoading(false));
  }, []);

  const filtered = candidates.filter((c) => {
    const q = search.toLowerCase();
    return (
      (c.fullName || '').toLowerCase().includes(q) ||
      (c.username || '').toLowerCase().includes(q) ||
      (c.email || '').toLowerCase().includes(q)
    );
  });

  const columns = [
    {
      title: 'Ứng viên',
      key: 'name',
      render: (_, r) => (
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <Avatar src={r.avatarUrl} icon={<UserOutlined />} style={{ background: GREEN }} />
          <div>
            <Text strong>{r.fullName || r.username}</Text>
            <br />
            <Text style={{ color: '#888', fontSize: 12 }}>@{r.username}</Text>
          </div>
        </div>
      ),
    },
    { title: 'Email', dataIndex: 'email', key: 'email', render: (v) => v || <Text type="secondary">—</Text> },
    { title: 'Điện thoại', dataIndex: 'phoneNumber', key: 'phoneNumber', render: (v) => v || <Text type="secondary">—</Text> },
    { title: 'Địa chỉ', dataIndex: 'address', key: 'address', render: (v) => v || <Text type="secondary">—</Text> },
  ];

  return (
    <AppLayout>
      <div style={{ background: 'linear-gradient(135deg, #00b14f 0%, #007a38 100%)', padding: '28px 24px 56px' }}>
        <div style={{ maxWidth: 900, margin: '0 auto' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <div>
              <Title level={3} style={{ color: '#fff', marginBottom: 2 }}>Danh sách ứng viên</Title>
              <Text style={{ color: 'rgba(255,255,255,0.8)' }}>{candidates.length} ứng viên trong hệ thống</Text>
            </div>
          </div>
        </div>
      </div>

      <div style={{ maxWidth: 900, margin: '-28px auto 0', padding: '0 16px 40px' }}>
        <div style={{ background: '#fff', borderRadius: 14, padding: '20px', boxShadow: '0 2px 8px rgba(0,0,0,0.07)' }}>
          <div style={{ marginBottom: 16 }}>
            <Input
              prefix={<SearchOutlined style={{ color: '#bbb' }} />}
              placeholder="Tìm theo tên, username, email..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              style={{ borderRadius: 8, maxWidth: 360 }}
            />
          </div>
          {loading ? (
            <div style={{ textAlign: 'center', padding: '40px 0' }}><Spin size="large" /></div>
          ) : (
            <Table
              columns={columns}
              dataSource={filtered}
              rowKey="candidateId"
              pagination={{ pageSize: 15 }}
            />
          )}
        </div>
      </div>
    </AppLayout>
  );
}
