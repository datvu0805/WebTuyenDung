import React, { useState, useEffect, useCallback } from 'react';
import { Form, Input, Button, Upload, Typography, message, Table, Popconfirm, Tag, Empty, Spin } from 'antd';
import { UploadOutlined, FilePdfOutlined, DeleteOutlined, PlusOutlined, FileTextOutlined } from '@ant-design/icons';
import { cvApi } from '../api/services';
import { useAuth } from '../context/AuthContext';
import AppLayout from '../components/AppLayout';
import dayjs from 'dayjs';

const { Title, Text } = Typography;
const GREEN = '#00b14f';

export default function UploadCVPage() {
  const [uploading, setUploading] = useState(false);
  const [cvFile, setCvFile] = useState(null);
  const [cvList, setCvList] = useState([]);
  const [listLoading, setListLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const { user } = useAuth();
  const [form] = Form.useForm();

  const loadCVs = useCallback(() => {
    if (!user?.userId) return;
    setListLoading(true);
    cvApi.getByCandidate(user.userId)
      .then((res) => {
        if (res.data.success) setCvList(res.data.data?.cvList || []);
      })
      .catch(() => {})
      .finally(() => setListLoading(false));
  }, [user?.userId]);

  useEffect(() => { loadCVs(); }, [loadCVs]);

  const onFinish = async (values) => {
    if (!cvFile) { message.error('Vui lòng chọn file CV (PDF)'); return; }
    setUploading(true);
    const fd = new FormData();
    fd.append('candidate_id', user.userId);
    fd.append('cv_title', values.cv_title);
    fd.append('description', values.description || '');
    fd.append('version', values.version || '1.0');
    fd.append('file', cvFile);
    try {
      const res = await cvApi.upload(fd);
      if (res.data.success) {
        message.success('Tải lên CV thành công');
        form.resetFields();
        setCvFile(null);
        setShowForm(false);
        loadCVs();
      } else {
        message.error(res.data.message || 'Tải lên thất bại');
      }
    } catch {
      message.error('Có lỗi xảy ra khi tải lên');
    } finally {
      setUploading(false);
    }
  };

  const handleDelete = async (cvId) => {
    try {
      const res = await cvApi.delete(cvId, user.userId);
      if (res.data.success) {
        message.success('Đã xóa CV');
        loadCVs();
      } else {
        message.error(res.data.message || 'Xóa thất bại');
      }
    } catch {
      message.error('Có lỗi khi xóa CV');
    }
  };

  const columns = [
    {
      title: 'Tiêu đề CV', dataIndex: 'cvTitle', key: 'cvTitle',
      render: (v, r) => (
        <div>
          <Text strong>{v || r.cv_title}</Text>
          {(r.version) && <Tag style={{ marginLeft: 8, borderRadius: 4 }} color="blue">v{r.version}</Tag>}
        </div>
      )
    },
    {
      title: 'Mô tả', dataIndex: 'description', key: 'description', responsive: ['md'],
      render: (v) => v ? <Text type="secondary" ellipsis style={{ maxWidth: 200 }}>{v}</Text> : '—'
    },
    {
      title: 'Ngày tải lên', dataIndex: 'uploadedAt', key: 'uploadedAt', responsive: ['lg'],
      render: (v) => v ? dayjs(v).format('DD/MM/YYYY') : '—'
    },
    {
      title: 'File', dataIndex: 'fileUrl', key: 'fileUrl',
      render: (v) => v
        ? <a href={v} target="_blank" rel="noreferrer" style={{ color: GREEN }}>Xem CV</a>
        : '—'
    },
    {
      title: '', key: 'actions', width: 60, align: 'center',
      render: (_, record) => (
        <Popconfirm title="Xóa CV này?" onConfirm={() => handleDelete(record.id)} okText="Xóa" cancelText="Hủy" okButtonProps={{ danger: true }}>
          <Button icon={<DeleteOutlined />} size="small" danger style={{ borderRadius: 6 }} />
        </Popconfirm>
      )
    }
  ];

  return (
    <AppLayout>
      <div style={{ background: 'linear-gradient(135deg, #00b14f 0%, #007a38 100%)', padding: '28px 24px 56px' }}>
        <div style={{ maxWidth: 820, margin: '0 auto' }}>
          <Title level={3} style={{ color: '#fff', marginBottom: 4 }}>Quản lý CV</Title>
          <Text style={{ color: 'rgba(255,255,255,0.8)' }}>Tải lên và quản lý hồ sơ ứng tuyển của bạn</Text>
        </div>
      </div>

      <div style={{ maxWidth: 820, margin: '-28px auto 0', padding: '0 16px 40px' }}>
        {/* CV list card */}
        <div style={{ background: '#fff', borderRadius: 16, padding: '24px', boxShadow: '0 2px 16px rgba(0,0,0,0.08)', marginBottom: 16 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
            <Title level={5} style={{ margin: 0 }}>
              <FileTextOutlined style={{ marginRight: 8, color: GREEN }} />CV đã tải lên ({cvList.length})
            </Title>
            <Button type="primary" icon={<PlusOutlined />} onClick={() => setShowForm(!showForm)}
              style={{ borderRadius: 8, background: GREEN, borderColor: GREEN, fontWeight: 600 }}>
              {showForm ? 'Hủy' : 'Tải lên CV mới'}
            </Button>
          </div>

          {listLoading ? (
            <div style={{ textAlign: 'center', padding: '32px 0' }}><Spin /></div>
          ) : cvList.length === 0 && !showForm ? (
            <Empty description="Bạn chưa có CV nào. Hãy tải lên CV đầu tiên!" style={{ padding: '32px 0' }} />
          ) : (
            <Table columns={columns} dataSource={cvList} rowKey="id" pagination={false} size="middle" />
          )}
        </div>

        {/* Upload form */}
        {showForm && (
          <div style={{ background: '#fff', borderRadius: 16, padding: '28px 32px', boxShadow: '0 2px 16px rgba(0,0,0,0.08)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 24, padding: '14px 16px', background: '#f0faf4', borderRadius: 10, border: '1px solid #c7f0d6' }}>
              <FilePdfOutlined style={{ fontSize: 28, color: GREEN }} />
              <div>
                <Text strong style={{ color: '#1a1a1a' }}>Định dạng PDF</Text><br />
                <Text style={{ fontSize: 13, color: '#888' }}>Hỗ trợ file .pdf, tối đa 2MB</Text>
              </div>
            </div>
            <Form form={form} layout="vertical" onFinish={onFinish} size="large">
              <Form.Item name="cv_title" label={<span style={{ fontWeight: 600 }}>Tiêu đề CV</span>}
                rules={[{ required: true, message: 'Vui lòng nhập tiêu đề CV' }]}>
                <Input placeholder="CV Frontend Developer - 2025" style={{ borderRadius: 8 }} />
              </Form.Item>
              <Form.Item name="version" label={<span style={{ fontWeight: 600 }}>Phiên bản</span>}>
                <Input placeholder="1.0" style={{ borderRadius: 8 }} />
              </Form.Item>
              <Form.Item name="description" label={<span style={{ fontWeight: 600 }}>Mô tả</span>}>
                <Input.TextArea rows={2} placeholder="Mô tả ngắn về CV này..." style={{ borderRadius: 8 }} />
              </Form.Item>
              <Form.Item label={<span style={{ fontWeight: 600 }}>File CV (PDF) <span style={{ color: '#f00' }}>*</span></span>}>
                <Upload beforeUpload={(file) => { setCvFile(file); return false; }} maxCount={1} accept=".pdf"
                  fileList={cvFile ? [{ uid: '1', name: cvFile.name, status: 'done' }] : []}
                  onRemove={() => setCvFile(null)}>
                  <Button icon={<UploadOutlined />} style={{ borderRadius: 8, borderColor: GREEN, color: GREEN }}>
                    Chọn file PDF
                  </Button>
                </Upload>
              </Form.Item>
              <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
                <Button onClick={() => { setShowForm(false); form.resetFields(); setCvFile(null); }} style={{ borderRadius: 8 }}>Hủy</Button>
                <Button type="primary" htmlType="submit" loading={uploading}
                  style={{ borderRadius: 8, fontWeight: 600, background: GREEN, borderColor: GREEN }}>
                  Tải lên CV
                </Button>
              </div>
            </Form>
          </div>
        )}
      </div>
    </AppLayout>
  );
}
