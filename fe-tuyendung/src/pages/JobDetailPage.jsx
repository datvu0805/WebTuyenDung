import React, { useEffect, useState } from 'react';
import {
  Card, Tag, Button, Spin, Typography, Space, Divider,
  message, Modal, Form, Input, Descriptions
} from 'antd';
import {
  EnvironmentOutlined, DollarOutlined, TeamOutlined,
  ClockCircleOutlined, ArrowLeftOutlined, SendOutlined,
  CalendarOutlined, UserOutlined
} from '@ant-design/icons';
import { useParams, useNavigate } from 'react-router-dom';
import { jobApi, applicationApi } from '../api/services';
import { useAuth } from '../context/AuthContext';
import AppLayout from '../components/AppLayout';
import dayjs from 'dayjs';

const { Title, Text, Paragraph } = Typography;
const GREEN = '#00b14f';
const STATUS_LABELS = ['Đang tuyển', 'Tạm dừng', 'Đã đóng'];
const STATUS_COLORS = ['#e8f9f0', '#fff7e6', '#f5f5f5'];
const STATUS_TEXT = ['#00b14f', '#fa8c16', '#999'];

export default function JobDetailPage() {
  const { id } = useParams();
  const [job, setJob] = useState(null);
  const [loading, setLoading] = useState(true);
  const [applyOpen, setApplyOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const { user } = useAuth();
  const navigate = useNavigate();
  const [form] = Form.useForm();

  useEffect(() => {
    jobApi.getById(id)
      .then((res) => { if (res.data.success || res.data) setJob(res.data.data || res.data); })
      .catch(() => message.error('Không tìm thấy tin tuyển dụng'))
      .finally(() => setLoading(false));
  }, [id]);

  const handleApply = async (values) => {
    setSubmitting(true);
    try {
      const res = await applicationApi.submit({ candidateID: user.userId, jobID: id, ...values });
      if (res.data.success) {
        message.success('Nộp đơn thành công!');
        setApplyOpen(false);
        form.resetFields();
      } else {
        message.error(res.data.message);
      }
    } catch {
      message.error('Có lỗi xảy ra khi nộp đơn');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <AppLayout><div style={{ textAlign: 'center', padding: 80 }}><Spin size="large" /></div></AppLayout>;
  if (!job) return <AppLayout><div style={{ textAlign: 'center', padding: 40 }}>Không tìm thấy tin tuyển dụng</div></AppLayout>;

  const statusIdx = job.status ?? 0;

  return (
    <AppLayout>
      <div style={{ maxWidth: 860, margin: '0 auto', padding: '28px 16px' }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/jobs')}
          style={{ marginBottom: 20, borderRadius: 8 }}>
          Quay lại danh sách
        </Button>

        {/* Header card */}
        <div style={{ background: '#fff', borderRadius: 14, padding: '28px 28px 24px', marginBottom: 16, border: '1px solid #eee', boxShadow: '0 1px 4px rgba(0,0,0,0.05)' }}>
          <div style={{ display: 'flex', gap: 16, alignItems: 'flex-start', flexWrap: 'wrap' }}>
            <div style={{ width: 64, height: 64, borderRadius: 12, background: '#f0faf4', display: 'flex', alignItems: 'center', justifyContent: 'center', border: '1px solid #e0f3e8', flexShrink: 0, fontSize: 26, fontWeight: 700, color: GREEN }}>
              {job.title?.charAt(0).toUpperCase()}
            </div>
            <div style={{ flex: 1, minWidth: 200 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10, flexWrap: 'wrap', marginBottom: 6 }}>
                <Title level={3} style={{ margin: 0 }}>{job.title}</Title>
                <span style={{ background: STATUS_COLORS[statusIdx], color: STATUS_TEXT[statusIdx], borderRadius: 5, padding: '3px 12px', fontSize: 13, fontWeight: 600 }}>
                  {STATUS_LABELS[statusIdx]}
                </span>
              </div>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px 16px', marginTop: 4 }}>
                <Text style={{ fontSize: 14, color: '#555' }}><EnvironmentOutlined style={{ marginRight: 4, color: '#aaa' }} />{job.location || 'Chưa cập nhật'}</Text>
                <Text style={{ fontSize: 14, color: GREEN, fontWeight: 600 }}><DollarOutlined style={{ marginRight: 4 }} />{job.salary ? job.salary.toLocaleString('vi-VN') + ' VNĐ' : 'Thỏa thuận'}</Text>
                <Text style={{ fontSize: 14, color: '#555' }}><TeamOutlined style={{ marginRight: 4, color: '#aaa' }} />{job.quantity} vị trí</Text>
              </div>
            </div>
            {user?.role === 'CANDIDATE' && job.status === 0 && (
              <Button type="primary" icon={<SendOutlined />} size="large" onClick={() => setApplyOpen(true)}
                style={{ background: GREEN, borderColor: GREEN, borderRadius: 8, fontWeight: 600, height: 44, flexShrink: 0 }}>
                Ứng tuyển ngay
              </Button>
            )}
          </div>
        </div>

        {/* Info grid */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: 12, marginBottom: 16 }}>
          {[
            { icon: <CalendarOutlined />, label: 'Ngày đăng', value: job.postedAt ? dayjs(job.postedAt).format('DD/MM/YYYY') : '—' },
            { icon: <CalendarOutlined />, label: 'Hạn nộp hồ sơ', value: job.applicationDeadline ? dayjs(job.applicationDeadline).format('DD/MM/YYYY') : '—' },
            { icon: <CalendarOutlined />, label: 'Ngày hết hạn', value: job.expiredAt ? dayjs(job.expiredAt).format('DD/MM/YYYY') : '—' },
            { icon: <UserOutlined />, label: 'Kinh nghiệm', value: job.experience || 'Không yêu cầu' },
          ].map((item, i) => (
            <div key={i} style={{ background: '#fff', borderRadius: 10, padding: '14px 18px', border: '1px solid #eee' }}>
              <Text style={{ fontSize: 12, color: '#aaa', display: 'block', marginBottom: 4 }}>
                {item.icon} {item.label}
              </Text>
              <Text strong style={{ fontSize: 14 }}>{item.value}</Text>
            </div>
          ))}
        </div>

        {/* Description */}
        <div style={{ background: '#fff', borderRadius: 14, padding: '24px 28px', border: '1px solid #eee', boxShadow: '0 1px 4px rgba(0,0,0,0.05)' }}>
          <Title level={5} style={{ marginBottom: 16, color: '#333' }}>Mô tả công việc</Title>
          <Paragraph style={{ whiteSpace: 'pre-wrap', lineHeight: 1.8, color: '#444', fontSize: 14 }}>
            {job.description || 'Chưa có mô tả công việc.'}
          </Paragraph>
        </div>
      </div>

      <Modal title="Nộp đơn ứng tuyển" open={applyOpen} onCancel={() => setApplyOpen(false)} footer={null} width={520}>
        <Form form={form} layout="vertical" onFinish={handleApply} style={{ marginTop: 16 }}>
          <Form.Item name="cvID" label={<span style={{ fontWeight: 600 }}>ID CV của bạn</span>}
            rules={[{ required: true, message: 'Vui lòng nhập ID CV' }]}
            extra="Bạn có thể xem ID CV tại trang hồ sơ">
            <Input type="number" placeholder="Nhập ID CV" style={{ borderRadius: 8 }} />
          </Form.Item>
          <Form.Item name="coverLetter" label={<span style={{ fontWeight: 600 }}>Thư xin việc</span>}
            rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input.TextArea rows={4} placeholder="Giới thiệu bản thân và lý do bạn muốn ứng tuyển..." style={{ borderRadius: 8 }} />
          </Form.Item>
          <Form.Item name="description" label={<span style={{ fontWeight: 600 }}>Ghi chú thêm</span>}>
            <Input.TextArea rows={2} placeholder="Thông tin bổ sung (không bắt buộc)" style={{ borderRadius: 8 }} />
          </Form.Item>
          <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
            <Button onClick={() => setApplyOpen(false)} style={{ borderRadius: 8 }}>Hủy</Button>
            <Button type="primary" htmlType="submit" loading={submitting}
              style={{ borderRadius: 8, background: GREEN, borderColor: GREEN, fontWeight: 600 }}>
              Nộp đơn
            </Button>
          </div>
        </Form>
      </Modal>
    </AppLayout>
  );
}
