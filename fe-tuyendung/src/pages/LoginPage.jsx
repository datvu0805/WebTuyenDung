import React, { useState } from 'react';
import { Form, Input, Button, message, Typography, Divider } from 'antd';
import { UserOutlined, LockOutlined, BankOutlined, RocketOutlined, TeamOutlined, SafetyOutlined } from '@ant-design/icons';
import { useNavigate, Link } from 'react-router-dom';
import { authApi } from '../api/services';
import { useAuth } from '../context/AuthContext';

const { Title, Text } = Typography;
const GREEN = '#00b14f';

export default function LoginPage() {
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const onFinish = async (values) => {
    setLoading(true);
    try {
      const res = await authApi.login(values.username, values.password);
      if (res.data.success) {
        login(res.data.data);
        message.success('Đăng nhập thành công!');
        navigate(res.data.data.role === 'EMPLOYER' ? '/employer/dashboard' : '/jobs');
      } else {
        message.error(res.data.message || 'Sai tài khoản hoặc mật khẩu');
      }
    } catch {
      message.error('Không thể kết nối đến máy chủ');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-layout">
      <div className="auth-left">
        <div style={{ textAlign: 'center', maxWidth: 380 }}>
          <div style={{ width: 64, height: 64, borderRadius: 16, background: 'rgba(255,255,255,0.2)', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 24px' }}>
            <BankOutlined style={{ fontSize: 32, color: '#fff' }} />
          </div>
          <Title level={2} style={{ color: '#fff', marginBottom: 8 }}>TopJob</Title>
          <Text style={{ color: 'rgba(255,255,255,0.85)', fontSize: 16, lineHeight: 1.7 }}>
            Nền tảng tuyển dụng hàng đầu Việt Nam.<br />Kết nối hàng nghìn cơ hội việc làm mỗi ngày.
          </Text>
          <div style={{ marginTop: 40, display: 'flex', flexDirection: 'column', gap: 16, textAlign: 'left' }}>
            {[
              { icon: <RocketOutlined />, text: 'Hàng ngàn tin tuyển dụng mới mỗi ngày' },
              { icon: <TeamOutlined />, text: 'Kết nối trực tiếp với nhà tuyển dụng' },
              { icon: <SafetyOutlined />, text: 'Hồ sơ bảo mật, ứng tuyển nhanh chóng' },
            ].map((item, i) => (
              <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                <div style={{ width: 36, height: 36, borderRadius: 8, background: 'rgba(255,255,255,0.2)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                  <span style={{ color: '#fff', fontSize: 16 }}>{item.icon}</span>
                </div>
                <Text style={{ color: 'rgba(255,255,255,0.9)', fontSize: 14 }}>{item.text}</Text>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="auth-right">
        <div style={{ width: '100%', maxWidth: 400 }}>
          <div style={{ marginBottom: 32 }}>
            <Title level={3} style={{ marginBottom: 6, color: '#1a1a1a' }}>Chào mừng trở lại!</Title>
            <Text style={{ color: '#888', fontSize: 15 }}>Đăng nhập để tiếp tục tìm kiếm cơ hội</Text>
          </div>
          <Form layout="vertical" onFinish={onFinish} size="large">
            <Form.Item name="username" label={<span style={{ fontWeight: 600, color: '#333' }}>Tên đăng nhập</span>}
              rules={[{ required: true, message: 'Vui lòng nhập tên đăng nhập' }]}>
              <Input prefix={<UserOutlined style={{ color: '#ccc' }} />} placeholder="Nhập tên đăng nhập"
                style={{ borderRadius: 8, height: 46 }} />
            </Form.Item>
            <Form.Item name="password" label={<span style={{ fontWeight: 600, color: '#333' }}>Mật khẩu</span>}
              rules={[{ required: true, message: 'Vui lòng nhập mật khẩu' }]}>
              <Input.Password prefix={<LockOutlined style={{ color: '#ccc' }} />} placeholder="Nhập mật khẩu"
                style={{ borderRadius: 8, height: 46 }} />
            </Form.Item>
            <Button type="primary" htmlType="submit" block loading={loading}
              style={{ height: 46, borderRadius: 8, fontWeight: 600, fontSize: 15, background: GREEN, borderColor: GREEN, marginTop: 4 }}>
              Đăng nhập
            </Button>
          </Form>
          <Divider style={{ color: '#bbb', fontSize: 13 }}>Chưa có tài khoản?</Divider>
          <div style={{ display: 'flex', gap: 10 }}>
            <Link to="/register/candidate" style={{ flex: 1 }}>
              <Button block size="large" style={{ borderRadius: 8, height: 46, fontWeight: 500, borderColor: GREEN, color: GREEN }}>
                Ứng viên
              </Button>
            </Link>
            <Link to="/register/employer" style={{ flex: 1 }}>
              <Button block size="large" style={{ borderRadius: 8, height: 46, fontWeight: 500 }}>
                Nhà tuyển dụng
              </Button>
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
