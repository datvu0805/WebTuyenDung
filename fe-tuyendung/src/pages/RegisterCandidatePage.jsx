import React, { useState } from 'react';
import { Form, Input, Button, Card, message, Upload, Typography, Row, Col } from 'antd';
import { UploadOutlined, UserOutlined, MailOutlined, PhoneOutlined, ArrowLeftOutlined } from '@ant-design/icons';
import { useNavigate, Link } from 'react-router-dom';
import { authApi } from '../api/services';

const { Title, Text } = Typography;
const GREEN = '#00b14f';

export default function RegisterCandidatePage() {
  const [loading, setLoading] = useState(false);
  const [avatarFile, setAvatarFile] = useState(null);
  const navigate = useNavigate();

  const onFinish = async (values) => {
    if (values.password !== values.confirmPassword) {
      message.error('Mật khẩu xác nhận không khớp');
      return;
    }
    setLoading(true);
    const fd = new FormData();
    Object.entries(values).forEach(([k, v]) => {
      if (k !== 'confirmPassword' && v) fd.append(k, v);
    });
    if (avatarFile) fd.append('avatar', avatarFile);
    try {
      const res = await authApi.registerCandidate(fd);
      if (res.data.success) {
        message.success('Đăng ký thành công! Vui lòng đăng nhập.');
        navigate('/login');
      } else {
        message.error(res.data.message || 'Đăng ký thất bại');
      }
    } catch {
      message.error('Không thể kết nối đến máy chủ');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ minHeight: '100vh', background: '#f4f5f6', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '24px 16px' }}>
      <div style={{ width: '100%', maxWidth: 580 }}>
        <div style={{ textAlign: 'center', marginBottom: 28 }}>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: 8, marginBottom: 8 }}>
            <div style={{ width: 36, height: 36, borderRadius: 10, background: GREEN, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <UserOutlined style={{ color: '#fff', fontSize: 18 }} />
            </div>
            <Text style={{ fontWeight: 800, fontSize: 22, color: GREEN }}>TopJob</Text>
          </div>
          <Title level={3} style={{ marginBottom: 4, color: '#1a1a1a' }}>Đăng ký Ứng viên</Title>
          <Text style={{ color: '#888' }}>Tạo tài khoản để tìm kiếm việc làm</Text>
        </div>

        <Card style={{ borderRadius: 16, boxShadow: '0 2px 16px rgba(0,0,0,0.08)', border: 'none' }} bodyStyle={{ padding: '32px' }}>
          <Form layout="vertical" onFinish={onFinish} size="large">
            <Row gutter={16}>
              <Col xs={24} sm={12}>
                <Form.Item name="username" label={<span style={{ fontWeight: 600 }}>Tên đăng nhập</span>}
                  rules={[{ required: true, message: 'Bắt buộc' }]}>
                  <Input prefix={<UserOutlined style={{ color: '#ccc' }} />} placeholder="username" style={{ borderRadius: 8 }} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12}>
                <Form.Item name="fullName" label={<span style={{ fontWeight: 600 }}>Họ và tên</span>}
                  rules={[{ required: true, message: 'Bắt buộc' }]}>
                  <Input placeholder="Nguyễn Văn A" style={{ borderRadius: 8 }} />
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={16}>
              <Col xs={24} sm={12}>
                <Form.Item name="password" label={<span style={{ fontWeight: 600 }}>Mật khẩu</span>}
                  rules={[{ required: true, min: 6, message: 'Tối thiểu 6 ký tự' }]}>
                  <Input.Password placeholder="••••••••" style={{ borderRadius: 8 }} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12}>
                <Form.Item name="confirmPassword" label={<span style={{ fontWeight: 600 }}>Xác nhận mật khẩu</span>}
                  rules={[{ required: true, message: 'Bắt buộc' }]}>
                  <Input.Password placeholder="••••••••" style={{ borderRadius: 8 }} />
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={16}>
              <Col xs={24} sm={12}>
                <Form.Item name="email" label={<span style={{ fontWeight: 600 }}>Email</span>}
                  rules={[{ type: 'email', message: 'Email không hợp lệ' }]}>
                  <Input prefix={<MailOutlined style={{ color: '#ccc' }} />} placeholder="email@example.com" style={{ borderRadius: 8 }} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12}>
                <Form.Item name="phoneNumber" label={<span style={{ fontWeight: 600 }}>Số điện thoại</span>}>
                  <Input prefix={<PhoneOutlined style={{ color: '#ccc' }} />} placeholder="0901234567" style={{ borderRadius: 8 }} />
                </Form.Item>
              </Col>
            </Row>
            <Form.Item name="address" label={<span style={{ fontWeight: 600 }}>Địa chỉ</span>}>
              <Input placeholder="Hà Nội, Việt Nam" style={{ borderRadius: 8 }} />
            </Form.Item>
            <Form.Item label={<span style={{ fontWeight: 600 }}>Ảnh đại diện</span>}>
              <Upload beforeUpload={(file) => { setAvatarFile(file); return false; }} maxCount={1} accept="image/*">
                <Button icon={<UploadOutlined />} style={{ borderRadius: 8 }}>Chọn ảnh (tối đa 3MB)</Button>
              </Upload>
            </Form.Item>
            <Button type="primary" htmlType="submit" block loading={loading}
              style={{ height: 46, borderRadius: 8, fontWeight: 600, fontSize: 15, background: GREEN, borderColor: GREEN }}>
              Tạo tài khoản
            </Button>
          </Form>
        </Card>

        <div style={{ textAlign: 'center', marginTop: 20 }}>
          <Text style={{ color: '#888' }}>Đã có tài khoản? </Text>
          <Link to="/login" style={{ color: GREEN, fontWeight: 600 }}>Đăng nhập</Link>
        </div>
      </div>
    </div>
  );
}
