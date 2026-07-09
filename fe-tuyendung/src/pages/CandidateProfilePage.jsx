import React, { useEffect, useState } from 'react';
import { Avatar, Descriptions, Typography, Spin, message } from 'antd';
import { UserOutlined, MailOutlined, PhoneOutlined, EnvironmentOutlined, CalendarOutlined } from '@ant-design/icons';
import { candidateApi } from '../api/services';
import AppLayout from '../components/AppLayout';

const { Title, Text } = Typography;
const GREEN = '#00b14f';

export default function CandidateProfilePage() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    candidateApi.getProfile()
      .then((res) => {
        if (res.data.success) setProfile(res.data.data);
        else message.error(res.data.message);
      })
      .catch(() => message.error('Không thể tải thông tin'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <AppLayout>
      <div style={{ background: 'linear-gradient(135deg, #00b14f 0%, #007a38 100%)', padding: '28px 24px 56px' }}>
        <div style={{ maxWidth: 720, margin: '0 auto' }}>
          <Title level={3} style={{ color: '#fff', marginBottom: 4 }}>Hồ sơ của tôi</Title>
          <Text style={{ color: 'rgba(255,255,255,0.8)' }}>Thông tin cá nhân của bạn</Text>
        </div>
      </div>

      <div style={{ maxWidth: 720, margin: '-28px auto 0', padding: '0 16px 40px' }}>
        <div style={{ background: '#fff', borderRadius: 16, padding: '32px', boxShadow: '0 2px 16px rgba(0,0,0,0.08)' }}>
          {loading ? (
            <div style={{ textAlign: 'center', padding: '40px 0' }}>
              <Spin size="large" />
            </div>
          ) : profile ? (
            <>
              <div style={{ display: 'flex', alignItems: 'center', gap: 20, marginBottom: 32 }}>
                <Avatar
                  src={profile.avatarUrl}
                  size={80}
                  icon={<UserOutlined />}
                  style={{ background: GREEN, flexShrink: 0 }}
                />
                <div>
                  <Title level={4} style={{ margin: 0 }}>{profile.fullName || profile.username}</Title>
                  <Text style={{ color: '#888' }}>@{profile.username}</Text>
                </div>
              </div>

              <Descriptions column={1} bordered size="middle" labelStyle={{ fontWeight: 600, width: 160 }}>
                <Descriptions.Item label={<span><MailOutlined /> Email</span>}>
                  {profile.email || <Text type="secondary">Chưa cập nhật</Text>}
                </Descriptions.Item>
                <Descriptions.Item label={<span><PhoneOutlined /> Số điện thoại</span>}>
                  {profile.phoneNumber || <Text type="secondary">Chưa cập nhật</Text>}
                </Descriptions.Item>
                <Descriptions.Item label={<span><EnvironmentOutlined /> Địa chỉ</span>}>
                  {profile.address || <Text type="secondary">Chưa cập nhật</Text>}
                </Descriptions.Item>
                <Descriptions.Item label={<span><CalendarOutlined /> Ngày sinh</span>}>
                  {profile.dateOfBirth || <Text type="secondary">Chưa cập nhật</Text>}
                </Descriptions.Item>
              </Descriptions>
            </>
          ) : (
            <div style={{ textAlign: 'center', padding: '40px 0' }}>
              <Text type="secondary">Không tìm thấy thông tin</Text>
            </div>
          )}
        </div>
      </div>
    </AppLayout>
  );
}
