import React, { useState } from 'react';
import { Card, Form, Input, Button, Upload, Typography, message } from 'antd';
import { UploadOutlined, FilePdfOutlined, CheckCircleOutlined } from '@ant-design/icons';
import { cvApi } from '../api/services';
import { useAuth } from '../context/AuthContext';
import AppLayout from '../components/AppLayout';

const { Title, Text } = Typography;
const GREEN = '#00b14f';

export default function UploadCVPage() {
  const [loading, setLoading] = useState(false);
  const [cvFile, setCvFile] = useState(null);
  const [avatarFile, setAvatarFile] = useState(null);
  const [done, setDone] = useState(false);
  const { user } = useAuth();
  const [form] = Form.useForm();

  const onFinish = async (values) => {
    if (!cvFile) { message.error('Vui lòng chọn file CV (PDF)'); return; }
    setLoading(true);
    const fd = new FormData();
    fd.append('candidate_id', user.userId);
    fd.append('cv_title', values.cv_title);
    fd.append('description', values.description || '');
    fd.append('version', values.version || '1.0');
    fd.append('file', cvFile);
    if (avatarFile) fd.append('avatar_url', avatarFile);
    try {
      const res = await cvApi.upload(fd);
      if (res.data.success) {
        setDone(true);
        form.resetFields();
        setCvFile(null);
        setAvatarFile(null);
      } else {
        message.error(res.data.message || 'Tải lên thất bại');
      }
    } catch {
      message.error('Có lỗi xảy ra khi tải lên');
    } finally {
      setLoading(false);
    }
  };

  return (
    <AppLayout>
      <div style={{ background: 'linear-gradient(135deg, #00b14f 0%, #007a38 100%)', padding: '28px 24px 56px' }}>
        <div style={{ maxWidth: 640, margin: '0 auto', textAlign: 'center' }}>
          <Title level={3} style={{ color: '#fff', marginBottom: 4 }}>Tải lên CV</Title>
          <Text style={{ color: 'rgba(255,255,255,0.8)' }}>Upload hồ sơ để ứng tuyển các vị trí</Text>
        </div>
      </div>

      <div style={{ maxWidth: 580, margin: '-28px auto 0', padding: '0 16px 40px' }}>
        <div style={{ background: '#fff', borderRadius: 16, padding: '32px', boxShadow: '0 2px 16px rgba(0,0,0,0.08)' }}>
          {done ? (
            <div style={{ textAlign: 'center', padding: '20px 0' }}>
              <CheckCircleOutlined style={{ fontSize: 56, color: GREEN }} />
              <Title level={4} style={{ marginTop: 16, color: '#1a1a1a' }}>Tải lên thành công!</Title>
              <Text style={{ color: '#888' }}>CV của bạn đã được lưu. Bạn có thể tải lên CV khác nếu muốn.</Text>
              <div style={{ marginTop: 20 }}>
                <Button type="primary" onClick={() => setDone(false)}
                  style={{ borderRadius: 8, background: GREEN, borderColor: GREEN, fontWeight: 600 }}>
                  Tải lên CV khác
                </Button>
              </div>
            </div>
          ) : (
            <>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 24, padding: '14px 16px', background: '#f0faf4', borderRadius: 10, border: '1px solid #c7f0d6' }}>
                <FilePdfOutlined style={{ fontSize: 28, color: GREEN }} />
                <div>
                  <Text strong style={{ color: '#1a1a1a' }}>Định dạng PDF</Text>
                  <br />
                  <Text style={{ fontSize: 13, color: '#888' }}>Hỗ trợ file .pdf, tối đa 10MB</Text>
                </div>
              </div>
              <Form form={form} layout="vertical" onFinish={onFinish} size="large">
                <Form.Item name="cv_title" label={<span style={{ fontWeight: 600 }}>Tiêu đề CV</span>}
                  rules={[{ required: true, message: 'Vui lòng nhập tiêu đề CV' }]}>
                  <Input placeholder="CV Frontend Developer - 2024" style={{ borderRadius: 8 }} />
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
                <Form.Item label={<span style={{ fontWeight: 600 }}>Ảnh đại diện (tuỳ chọn)</span>}>
                  <Upload beforeUpload={(file) => { setAvatarFile(file); return false; }} maxCount={1} accept="image/*"
                    fileList={avatarFile ? [{ uid: '1', name: avatarFile.name, status: 'done' }] : []}
                    onRemove={() => setAvatarFile(null)}>
                    <Button icon={<UploadOutlined />} style={{ borderRadius: 8 }}>Chọn ảnh</Button>
                  </Upload>
                </Form.Item>
                <Button type="primary" htmlType="submit" block loading={loading}
                  style={{ height: 46, borderRadius: 8, fontWeight: 600, fontSize: 15, background: GREEN, borderColor: GREEN }}>
                  Tải lên CV
                </Button>
              </Form>
            </>
          )}
        </div>
      </div>
    </AppLayout>
  );
}
