import React, { useEffect, useState, useCallback } from 'react';
import {
  Tag, Typography, message, Select, Avatar, Spin, Empty, Button, Modal, Tooltip,
} from 'antd';
import {
  UserOutlined, FileTextOutlined, CalendarOutlined,
  SolutionOutlined, EyeOutlined,
} from '@ant-design/icons';
import { applicationApi } from '../api/services';
import AppLayout from '../components/AppLayout';
import dayjs from 'dayjs';

const { Title, Text } = Typography;
const { Option } = Select;
const GREEN = '#00b14f';

const STATUS_MAP = {
  0: { label: 'Chờ duyệt',      color: 'processing' },
  1: { label: 'Phỏng vấn',      color: 'warning' },
  2: { label: 'Đạt',            color: 'success' },
  3: { label: 'Từ chối',        color: 'error' },
};

// Flatten DTO from backend: { applicationID: {id, coverLetter...}, candidateName: {id, user: {fullName}}, ... }
function flattenApp(dto) {
  return {
    id:           dto.applicationID?.id,
    coverLetter:  dto.applicationID?.coverLetter || '',
    description:  dto.applicationID?.description || '',
    candidateId:  dto.candidateName?.id,
    candidateName: dto.candidateName?.user?.fullName || dto.candidateName?.fullName || 'Ứng viên',
    jobTitle:     dto.jobTitle || '',
    cvTitle:      dto.cvTitle || '',
    fileUrl:      dto.fileUrl || '',
    appliedAt:    dto.applieAt,
    status:       dto.status ?? 0,
    attachedCertificates: dto.attachedCertificates || [],
    attachedEducations:   dto.attachedEducations || [],
  };
}

export default function ApplicationManagePage() {
  const [apps, setApps]       = useState([]);
  const [loading, setLoading] = useState(true);
  const [viewingCover, setViewingCover] = useState(null);

  const load = useCallback(() => {
    setLoading(true);
    applicationApi.getRecruiterList()
      .then((res) => {
        if (res.data.success) {
          setApps((res.data.data || []).map(flattenApp));
        } else {
          message.error(res.data.message || 'Tải danh sách thất bại');
        }
      })
      .catch(() => message.error('Tải danh sách thất bại'))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => { load(); }, [load]);

  const updateStatus = async (id, status) => {
    try {
      const res = await applicationApi.updateStatus(id, status);
      if (res.data.success) {
        message.success('Cập nhật trạng thái thành công');
        setApps((prev) => prev.map((a) => (a.id === id ? { ...a, status } : a)));
      } else {
        message.error(res.data.message || 'Cập nhật thất bại');
      }
    } catch {
      message.error('Cập nhật thất bại');
    }
  };

  const statusCounts = Object.keys(STATUS_MAP).reduce((acc, k) => {
    acc[k] = apps.filter((a) => a.status === Number(k)).length;
    return acc;
  }, {});

  return (
    <AppLayout>
      {/* Header */}
      <div style={{ background: 'linear-gradient(135deg, #00b14f 0%, #007a38 100%)', padding: '28px 24px 56px' }}>
        <div style={{ maxWidth: 1100, margin: '0 auto' }}>
          <Title level={3} style={{ color: '#fff', marginBottom: 4 }}>Đơn ứng tuyển</Title>
          <Text style={{ color: 'rgba(255,255,255,0.8)' }}>Xem xét và cập nhật trạng thái ứng viên</Text>
        </div>
      </div>

      <div style={{ maxWidth: 1100, margin: '-28px auto 0', padding: '0 16px 40px' }}>
        {/* Status summary cards */}
        <div style={{ display: 'flex', gap: 10, marginBottom: 20, flexWrap: 'wrap' }}>
          {Object.entries(STATUS_MAP).map(([k, v]) => (
            <div key={k} style={{
              background: '#fff', borderRadius: 10, padding: '12px 20px',
              boxShadow: '0 2px 8px rgba(0,0,0,0.07)',
              display: 'flex', alignItems: 'center', gap: 10,
              minWidth: 120,
            }}>
              <Tag color={v.color} style={{ margin: 0, fontWeight: 600 }}>{v.label}</Tag>
              <Text strong style={{ fontSize: 18, color: '#1a1a1a' }}>{statusCounts[k]}</Text>
            </div>
          ))}
          <div style={{
            background: GREEN, borderRadius: 10, padding: '12px 20px',
            boxShadow: '0 2px 8px rgba(0,177,79,0.2)',
            display: 'flex', alignItems: 'center', gap: 10,
            marginLeft: 'auto',
          }}>
            <Text style={{ color: '#fff', fontWeight: 600 }}>Tổng</Text>
            <Text strong style={{ fontSize: 18, color: '#fff' }}>{apps.length}</Text>
          </div>
        </div>

        {/* List */}
        <div style={{ background: '#fff', borderRadius: 14, boxShadow: '0 2px 8px rgba(0,0,0,0.07)', overflow: 'hidden' }}>
          {loading ? (
            <div style={{ textAlign: 'center', padding: 80 }}><Spin size="large" /></div>
          ) : apps.length === 0 ? (
            <Empty description="Chưa có đơn ứng tuyển nào" style={{ padding: 60 }} />
          ) : (
            <div>
              {/* Table header */}
              <div style={{
                display: 'grid',
                gridTemplateColumns: '2fr 2fr 140px 160px 80px',
                gap: 8,
                padding: '12px 20px',
                background: '#fafafa',
                borderBottom: '1px solid #f0f0f0',
                fontWeight: 600, fontSize: 13, color: '#888',
              }}>
                <span>Ứng viên</span>
                <span>Vị trí</span>
                <span>Trạng thái</span>
                <span>Cập nhật</span>
                <span>Ngày</span>
              </div>
              {apps.map((app) => (
                <ApplicationRow
                  key={app.id}
                  app={app}
                  onUpdateStatus={updateStatus}
                  onViewCover={() => setViewingCover(app)}
                />
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Cover letter modal */}
      <Modal
        open={!!viewingCover}
        onCancel={() => setViewingCover(null)}
        footer={null}
        title={
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <FileTextOutlined style={{ color: GREEN }} />
            <span>Thư xin việc — {viewingCover?.candidateName}</span>
          </div>
        }
        width={560}
      >
        {viewingCover && (
          <div style={{ padding: '8px 0' }}>
            <div style={{ marginBottom: 12, display: 'flex', gap: 16, flexWrap: 'wrap' }}>
              <Text type="secondary"><SolutionOutlined style={{ marginRight: 4 }} />{viewingCover.jobTitle}</Text>
              <Text type="secondary"><CalendarOutlined style={{ marginRight: 4 }} />
                {viewingCover.appliedAt ? dayjs(viewingCover.appliedAt).format('DD/MM/YYYY HH:mm') : ''}
              </Text>
            </div>
            <div style={{
              background: '#f9f9f9', borderRadius: 8, padding: '16px',
              border: '1px solid #eee', minHeight: 120,
              color: '#333', fontSize: 14, lineHeight: 1.7, whiteSpace: 'pre-wrap',
            }}>
              {viewingCover.coverLetter || <Text type="secondary">Không có thư xin việc</Text>}
            </div>
            {viewingCover.description && (
              <div style={{ marginTop: 12, color: '#666', fontSize: 13 }}>
                <Text strong>Mô tả thêm:</Text> {viewingCover.description}
              </div>
            )}
          </div>
        )}
      </Modal>
    </AppLayout>
  );
}

function ApplicationRow({ app, onUpdateStatus, onViewCover }) {
  const s = STATUS_MAP[app.status] ?? STATUS_MAP[0];
  return (
    <div style={{
      display: 'grid',
      gridTemplateColumns: '2fr 2fr 140px 160px 80px',
      gap: 8,
      padding: '14px 20px',
      borderBottom: '1px solid #f6f6f6',
      alignItems: 'center',
      transition: 'background 0.15s',
    }}
      onMouseEnter={(e) => { e.currentTarget.style.background = '#fafffe'; }}
      onMouseLeave={(e) => { e.currentTarget.style.background = 'transparent'; }}
    >
      {/* Candidate */}
      <div style={{ display: 'flex', alignItems: 'center', gap: 10, minWidth: 0 }}>
        <Avatar size={36} icon={<UserOutlined />}
          style={{ background: '#e8f9f0', color: GREEN, flexShrink: 0 }} />
        <div style={{ minWidth: 0 }}>
          <Text strong style={{ fontSize: 14, display: 'block',
            overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
            {app.candidateName}
          </Text>
          {app.cvTitle && (
            <Text style={{ fontSize: 11, color: '#aaa',
              overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', display: 'block' }}>
              {app.cvTitle}
            </Text>
          )}
          {(app.attachedCertificates?.length > 0 || app.attachedEducations?.length > 0) && (
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 4, marginTop: 4 }}>
              {app.attachedCertificates?.map((name, i) => (
                <Tag key={`cert-${i}`} color="orange" style={{ margin: 0, fontSize: 10, lineHeight: '16px', padding: '0 6px' }}>{name}</Tag>
              ))}
              {app.attachedEducations?.map((name, i) => (
                <Tag key={`edu-${i}`} color="blue" style={{ margin: 0, fontSize: 10, lineHeight: '16px', padding: '0 6px' }}>{name}</Tag>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Job */}
      <Text style={{ fontSize: 13, color: '#555',
        overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
        {app.jobTitle}
      </Text>

      {/* Status badge */}
      <Tag color={s.color} style={{ margin: 0, fontWeight: 600, width: 'fit-content' }}>{s.label}</Tag>

      {/* Update */}
      <Select
        value={app.status}
        onChange={(val) => onUpdateStatus(app.id, val)}
        style={{ width: 150 }}
        size="small"
      >
        <Option value={0}>Chờ duyệt</Option>
        <Option value={1}>Phỏng vấn</Option>
        <Option value={2}>Đạt</Option>
        <Option value={3}>Từ chối</Option>
      </Select>

      {/* Date + cover letter */}
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: 4 }}>
        <Text style={{ fontSize: 11, color: '#bbb' }}>
          {app.appliedAt ? dayjs(app.appliedAt).format('DD/MM/YY') : ''}
        </Text>
        {app.coverLetter && (
          <Tooltip title="Xem thư xin việc">
            <Button
              size="small" type="text" icon={<EyeOutlined />}
              style={{ color: GREEN, padding: '0 4px', height: 20, fontSize: 12 }}
              onClick={onViewCover}
            />
          </Tooltip>
        )}
      </div>
    </div>
  );
}
