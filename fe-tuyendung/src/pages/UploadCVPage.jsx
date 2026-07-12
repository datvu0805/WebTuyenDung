import React, { useState, useEffect, useCallback } from 'react';
import {
  Form, Input, Button, Upload, Typography, message,
  Popconfirm, Empty, Spin, Modal,
} from 'antd';
import {
  UploadOutlined, FilePdfOutlined, DeleteOutlined,
  PlusOutlined, EyeOutlined, CalendarOutlined,
  FileTextOutlined, CloudUploadOutlined,
} from '@ant-design/icons';
import { Worker, Viewer } from '@react-pdf-viewer/core';
import { defaultLayoutPlugin } from '@react-pdf-viewer/default-layout';
import '@react-pdf-viewer/core/lib/styles/index.css';
import '@react-pdf-viewer/default-layout/lib/styles/index.css';
import { cvApi } from '../api/services';
import { useAuth } from '../context/AuthContext';
import AppLayout from '../components/AppLayout';
import dayjs from 'dayjs';
import pdfjsWorkerUrl from 'pdfjs-dist/build/pdf.worker.min.js?url';

const { Title, Text } = Typography;
const GREEN = '#00b14f';
const PDFJS_WORKER_URL = pdfjsWorkerUrl;

export default function UploadCVPage() {
  const [uploading, setUploading]     = useState(false);
  const [cvFile, setCvFile]           = useState(null);
  const [cvList, setCvList]           = useState([]);
  const [listLoading, setListLoading] = useState(true);
  const [showForm, setShowForm]       = useState(false);
  const [viewingCV, setViewingCV]     = useState(null);
  const { user } = useAuth();
  const [form] = Form.useForm();

  const loadCVs = useCallback(() => {
    if (!user?.candidateId) return;
    setListLoading(true);
    cvApi.getByCandidate(user.candidateId)
      .then((res) => {
        if (res.data.success) setCvList(res.data.data?.cvList || []);
      })
      .catch(() => {})
      .finally(() => setListLoading(false));
  }, [user?.candidateId]);

  useEffect(() => { loadCVs(); }, [loadCVs]);

  const onFinish = async (values) => {
    if (!cvFile) { message.error('Vui lòng chọn file CV (PDF)'); return; }
    setUploading(true);
    const fd = new FormData();
    fd.append('candidate_id', user.candidateId);
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
      const res = await cvApi.delete(cvId, user.candidateId);
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

  return (
    <AppLayout>
      {/* Header */}
      <div style={{
        background: 'linear-gradient(135deg, #00b14f 0%, #007a38 100%)',
        padding: '32px 24px 60px',
      }}>
        <div style={{ maxWidth: 900, margin: '0 auto' }}>
          <Title level={3} style={{ color: '#fff', margin: 0 }}>Quản lý CV</Title>
          <Text style={{ color: 'rgba(255,255,255,0.75)', fontSize: 14 }}>
            Tải lên và quản lý hồ sơ ứng tuyển của bạn
          </Text>
        </div>
      </div>

      <div style={{ maxWidth: 900, margin: '-32px auto 0', padding: '0 16px 48px' }}>

        {/* Card danh sách CV */}
        <div style={{
          background: '#fff', borderRadius: 16,
          boxShadow: '0 2px 20px rgba(0,0,0,0.08)',
          overflow: 'hidden', marginBottom: 16,
        }}>
          {/* Header card */}
          <div style={{
            display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            padding: '20px 24px', borderBottom: '1px solid #f0f0f0',
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <FileTextOutlined style={{ color: GREEN, fontSize: 18 }} />
              <span style={{ fontWeight: 600, fontSize: 15 }}>
                CV của tôi
                <span style={{
                  marginLeft: 8, background: '#e8f9ee', color: GREEN,
                  borderRadius: 20, padding: '1px 10px', fontSize: 12, fontWeight: 500,
                }}>
                  {cvList.length}
                </span>
              </span>
            </div>
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => setShowForm(!showForm)}
              style={{
                background: showForm ? '#ff4d4f' : GREEN,
                borderColor: showForm ? '#ff4d4f' : GREEN,
                borderRadius: 8, fontWeight: 600,
              }}
            >
              {showForm ? 'Hủy' : 'Tải lên CV mới'}
            </Button>
          </div>

          {/* Nội dung */}
          <div style={{ padding: '16px 24px' }}>
            {listLoading ? (
              <div style={{ textAlign: 'center', padding: '40px 0' }}><Spin size="large" /></div>
            ) : cvList.length === 0 && !showForm ? (
              <Empty
                image={<CloudUploadOutlined style={{ fontSize: 56, color: '#d9d9d9' }} />}
                imageStyle={{ height: 60 }}
                description={
                  <span style={{ color: '#aaa' }}>
                    Bạn chưa có CV nào. Hãy tải lên CV đầu tiên!
                  </span>
                }
                style={{ padding: '32px 0' }}
              />
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                {cvList.map((cv) => (
                  <CVCard
                    key={cv.id}
                    cv={cv}
                    onView={() => setViewingCV({ cvTitle: cv.cvTitle || cv.cv_title, fileUrl: cv.fileUrl, version: cv.version })}
                    onDelete={() => handleDelete(cv.id)}
                  />
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Form upload */}
        {showForm && (
          <div style={{
            background: '#fff', borderRadius: 16,
            boxShadow: '0 2px 20px rgba(0,0,0,0.08)',
            padding: '28px 32px',
          }}>
            <Title level={5} style={{ marginBottom: 20, color: '#1a1a1a' }}>
              <CloudUploadOutlined style={{ marginRight: 8, color: GREEN }} />
              Thêm CV mới
            </Title>
            <div style={{
              display: 'flex', alignItems: 'center', gap: 12,
              marginBottom: 24, padding: '12px 16px',
              background: '#f0faf4', borderRadius: 10, border: '1px solid #c7f0d6',
            }}>
              <FilePdfOutlined style={{ fontSize: 24, color: GREEN }} />
              <div>
                <Text strong style={{ fontSize: 13 }}>Chỉ hỗ trợ file PDF, tối đa 2MB</Text>
              </div>
            </div>
            <Form form={form} layout="vertical" onFinish={onFinish} size="large">
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0 16px' }}>
                <Form.Item
                  name="cv_title"
                  label={<span style={{ fontWeight: 600 }}>Tiêu đề CV</span>}
                  rules={[{ required: true, message: 'Vui lòng nhập tiêu đề' }]}
                >
                  <Input placeholder="CV Frontend Developer - 2026" style={{ borderRadius: 8 }} />
                </Form.Item>
                <Form.Item
                  name="version"
                  label={<span style={{ fontWeight: 600 }}>Phiên bản</span>}
                >
                  <Input placeholder="1.0" style={{ borderRadius: 8 }} />
                </Form.Item>
              </div>
              <Form.Item
                name="description"
                label={<span style={{ fontWeight: 600 }}>Mô tả</span>}
              >
                <Input.TextArea rows={2} placeholder="Mô tả ngắn..." style={{ borderRadius: 8 }} />
              </Form.Item>
              <Form.Item label={<span style={{ fontWeight: 600 }}>File CV <span style={{ color: '#f00' }}>*</span></span>}>
                <Upload
                  beforeUpload={(file) => { setCvFile(file); return false; }}
                  maxCount={1} accept=".pdf"
                  fileList={cvFile ? [{ uid: '1', name: cvFile.name, status: 'done' }] : []}
                  onRemove={() => setCvFile(null)}
                >
                  <Button icon={<UploadOutlined />} style={{ borderRadius: 8, borderColor: GREEN, color: GREEN }}>
                    Chọn file PDF
                  </Button>
                </Upload>
              </Form.Item>
              <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
                <Button onClick={() => { setShowForm(false); form.resetFields(); setCvFile(null); }} style={{ borderRadius: 8 }}>
                  Hủy
                </Button>
                <Button
                  type="primary" htmlType="submit" loading={uploading}
                  style={{ borderRadius: 8, fontWeight: 600, background: GREEN, borderColor: GREEN }}
                >
                  Tải lên
                </Button>
              </div>
            </Form>
          </div>
        )}
      </div>

      {/* Modal PDF Viewer — tách ra component riêng để plugin hooks chạy đúng */}
      <PdfViewerModal
        viewingCV={viewingCV}
        onClose={() => setViewingCV(null)}
      />
    </AppLayout>
  );
}

// Component riêng — defaultLayoutPlugin() được gọi đúng trong component lifecycle
function PdfViewerModal({ viewingCV, onClose }) {
  const layoutPlugin = defaultLayoutPlugin();
  return (
    <Modal
      open={!!viewingCV}
      onCancel={onClose}
      footer={null}
      title={
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <FilePdfOutlined style={{ color: '#e74c3c', fontSize: 18 }} />
          <div>
            <div style={{ fontWeight: 700, fontSize: 15, color: '#1a1a1a', lineHeight: 1.3 }}>
              {viewingCV?.cvTitle || 'Xem CV'}
            </div>
            {viewingCV?.version && (
              <div style={{ fontSize: 12, color: '#888', fontWeight: 400 }}>
                Phiên bản {viewingCV.version}
              </div>
            )}
          </div>
        </div>
      }
      width="80vw"
      style={{ top: 16 }}
      styles={{ body: { padding: 0, height: '88vh', display: 'flex', flexDirection: 'column', overflow: 'hidden' } }}
      destroyOnClose
    >
      {viewingCV?.fileUrl ? (
        <Worker workerUrl={PDFJS_WORKER_URL}>
          <div style={{ flex: 1, overflow: 'auto' }}>
            <Viewer
              fileUrl={viewingCV.fileUrl}
              plugins={[layoutPlugin]}
              defaultScale={1.2}
            />
          </div>
        </Worker>
      ) : (
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '88vh' }}>
          <Text type="secondary">Không thể tải file PDF</Text>
        </div>
      )}
    </Modal>
  );
}

function CVCard({ cv, onView, onDelete }) {
  const title = cv.cvTitle || cv.cv_title || 'CV không có tiêu đề';
  const date = cv.uploadedAt || cv.createdAt;

  return (
    <div style={{
      display: 'flex', alignItems: 'center', gap: 16,
      padding: '16px 20px', borderRadius: 12,
      border: '1.5px solid #f0f0f0', background: '#fafafa',
      transition: 'all 0.2s',
      cursor: 'default',
    }}
      onMouseEnter={e => {
        e.currentTarget.style.borderColor = '#b7ebc8';
        e.currentTarget.style.background = '#f5fdf8';
        e.currentTarget.style.boxShadow = '0 2px 12px rgba(0,177,79,0.08)';
      }}
      onMouseLeave={e => {
        e.currentTarget.style.borderColor = '#f0f0f0';
        e.currentTarget.style.background = '#fafafa';
        e.currentTarget.style.boxShadow = 'none';
      }}
    >
      {/* Icon */}
      <div style={{
        width: 48, height: 48, borderRadius: 10,
        background: '#fff0ef', display: 'flex',
        alignItems: 'center', justifyContent: 'center', flexShrink: 0,
      }}>
        <FilePdfOutlined style={{ fontSize: 24, color: '#e74c3c' }} />
      </div>

      {/* Info */}
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexWrap: 'wrap' }}>
          <span style={{ fontWeight: 600, fontSize: 14, color: '#1a1a1a' }}>{title}</span>
          {cv.version && (
            <span style={{
              background: '#e6f7ff', color: '#1890ff', border: '1px solid #bae7ff',
              borderRadius: 4, padding: '0 6px', fontSize: 11, fontWeight: 500,
            }}>
              v{cv.version}
            </span>
          )}
        </div>
        {cv.description && (
          <div style={{
            color: '#888', fontSize: 12, marginTop: 2,
            overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
          }}>
            {cv.description}
          </div>
        )}
        {date && (
          <div style={{ color: '#aaa', fontSize: 12, marginTop: 3, display: 'flex', alignItems: 'center', gap: 4 }}>
            <CalendarOutlined style={{ fontSize: 11 }} />
            {dayjs(date).format('DD/MM/YYYY')}
          </div>
        )}
      </div>

      {/* Actions */}
      <div style={{ display: 'flex', gap: 8, flexShrink: 0 }}>
        <Button
          icon={<EyeOutlined />}
          size="small"
          type="primary"
          onClick={onView}
          style={{
            background: GREEN, borderColor: GREEN, borderRadius: 7,
            fontWeight: 500, fontSize: 13,
          }}
        >
          Xem CV
        </Button>
        <Popconfirm
          title="Xóa CV này?"
          onConfirm={onDelete}
          okText="Xóa" cancelText="Hủy"
          okButtonProps={{ danger: true }}
        >
          <Button
            icon={<DeleteOutlined />}
            size="small" danger
            style={{ borderRadius: 7 }}
          />
        </Popconfirm>
      </div>
    </div>
  );
}
