import React, { useState, useRef } from 'react';
import { Layout, Menu, Avatar, Dropdown, Button, Typography, Drawer, Modal, Form, Input, DatePicker, message, Spin, Descriptions, Upload } from 'antd';
import {
  BankOutlined, UserOutlined, LogoutOutlined, FileTextOutlined,
  UnorderedListOutlined, MenuOutlined, TeamOutlined, BulbOutlined,
  CaretDownOutlined, EditOutlined, MailOutlined, PhoneOutlined, EnvironmentOutlined, CalendarOutlined, CameraOutlined
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { candidateApi, employerApi } from '../api/services';
import dayjs from 'dayjs';

const { Header, Content, Footer } = Layout;
const { Text } = Typography;
const LOGO_GREEN = '#00b14f';

export default function AppLayout({ children }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);
  const [editOpen, setEditOpen] = useState(false);
  const [profile, setProfile] = useState(null);
  const [profileLoading, setProfileLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [avatarUploading, setAvatarUploading] = useState(false);
  const avatarInputRef = useRef(null);
  const [form] = Form.useForm();

  const handleLogout = () => { logout(); navigate('/login'); };

  const profileApi = user?.role === 'EMPLOYER' ? employerApi : candidateApi;

  const openProfile = () => {
    if (user?.role === 'ADMIN') return;
    setProfileOpen(true);
    setProfileLoading(true);
    profileApi.getProfile()
      .then(res => { if (res.data.success) setProfile(res.data.data); })
      .catch(() => message.error('Không thể tải thông tin'))
      .finally(() => setProfileLoading(false));
  };

  const openEdit = () => {
    form.setFieldsValue({
      fullName: profile?.fullName,
      email: profile?.email,
      phoneNumber: profile?.phoneNumber,
      address: profile?.address,
      dateOfBirth: profile?.dateOfBirth ? dayjs(profile.dateOfBirth) : null,
    });
    setEditOpen(true);
  };

  const handleSave = async (values) => {
    setSaving(true);
    try {
      const data = {
        ...values,
        dateOfBirth: values.dateOfBirth ? values.dateOfBirth.format('YYYY-MM-DD') : '',
      };
      const res = await profileApi.updateProfile(data);
      if (res.data.success) {
        message.success('Cập nhật thành công');
        setEditOpen(false);
        setProfileLoading(true);
        const r = await profileApi.getProfile();
        if (r.data.success) setProfile(r.data.data);
        setProfileLoading(false);
      } else {
        message.error(res.data.message);
      }
    } catch {
      message.error('Lỗi khi cập nhật');
    } finally {
      setSaving(false);
    }
  };

  const handleAvatarChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setAvatarUploading(true);
    try {
      const res = await profileApi.uploadAvatar(file);
      if (res.data.success) {
        message.success('Cập nhật ảnh đại diện thành công');
        const r = await profileApi.getProfile();
        if (r.data.success) setProfile(r.data.data);
      } else {
        message.error(res.data.message);
      }
    } catch {
      message.error('Lỗi khi upload ảnh');
    } finally {
      setAvatarUploading(false);
      e.target.value = '';
    }
  };

  const adminNav = [
    { key: '/admin', icon: <BankOutlined />, label: 'Quản trị hệ thống' },
  ];
  const candidateNav = [
    { key: '/jobs', icon: <UnorderedListOutlined />, label: 'Việc làm' },
    { key: '/cv/upload', icon: <FileTextOutlined />, label: 'Hồ sơ CV' },
  ];
  const employerNav = [
    { key: '/employer/dashboard', icon: <TeamOutlined />, label: 'Tin tuyển dụng' },
    { key: '/employer/applications', icon: <UnorderedListOutlined />, label: 'Đơn ứng tuyển' },
    { key: '/employer/skills', icon: <BulbOutlined />, label: 'Kỹ năng' },
  ];
  const navItems = user?.role === 'ADMIN' ? adminNav : user?.role === 'EMPLOYER' ? employerNav : candidateNav;
  const homeRoute = user?.role === 'ADMIN' ? '/admin' : user?.role === 'EMPLOYER' ? '/employer/dashboard' : '/jobs';

  const userMenuItems = [
    ...(user?.role !== 'ADMIN' ? [{ key: 'profile', icon: <UserOutlined />, label: 'Thông tin cá nhân' }, { type: 'divider' }] : []),
    { key: 'logout', icon: <LogoutOutlined />, label: 'Đăng xuất' },
  ];

  const handleUserMenuClick = ({ key }) => {
    if (key === 'logout') handleLogout();
    if (key === 'profile') openProfile();
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{
        background: '#fff', padding: '0 32px',
        display: 'flex', alignItems: 'center', gap: 32, height: 64,
        borderBottom: '1px solid #e8e8e8', position: 'sticky', top: 0, zIndex: 100,
        boxShadow: '0 1px 4px rgba(0,0,0,0.06)',
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, cursor: 'pointer', flexShrink: 0 }}
          onClick={() => navigate(homeRoute)}>
          <div style={{ width: 32, height: 32, borderRadius: 8, background: LOGO_GREEN, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <BankOutlined style={{ color: '#fff', fontSize: 16 }} />
          </div>
          <Text style={{ fontWeight: 800, fontSize: 20, color: LOGO_GREEN, letterSpacing: '-0.3px' }}>TopJob</Text>
        </div>

        <div className="desktop-menu" style={{ flex: 1 }}>
          <Menu mode="horizontal" selectedKeys={[location.pathname]} items={navItems}
            onClick={({ key }) => navigate(key)}
            style={{ border: 'none', background: 'transparent', fontWeight: 500 }} />
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginLeft: 'auto' }}>
          <Dropdown menu={{ items: userMenuItems, onClick: handleUserMenuClick }} placement="bottomRight" trigger={['click']}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, cursor: 'pointer', padding: '4px 10px', borderRadius: 8 }}
              onMouseEnter={e => e.currentTarget.style.background = '#f4f5f6'}
              onMouseLeave={e => e.currentTarget.style.background = 'transparent'}>
              <Avatar size={34} icon={<UserOutlined />} src={user?.avatarUrl}
                style={{ background: LOGO_GREEN, flexShrink: 0 }} />
              <span className="username-text" style={{ fontWeight: 600, fontSize: 14, color: '#222', maxWidth: 140, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                {user?.fullName || user?.username}
              </span>
              <CaretDownOutlined className="username-text" style={{ fontSize: 10, color: '#999' }} />
            </div>
          </Dropdown>
          <Button type="text" icon={<MenuOutlined />} className="mobile-menu-btn"
            onClick={() => setDrawerOpen(true)} style={{ fontSize: 18 }} />
        </div>
      </Header>

      <Drawer
        title={
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <div style={{ width: 28, height: 28, borderRadius: 6, background: LOGO_GREEN, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <BankOutlined style={{ color: '#fff', fontSize: 14 }} />
            </div>
            <Text style={{ fontWeight: 800, color: LOGO_GREEN }}>TopJob</Text>
          </div>
        }
        placement="left" onClose={() => setDrawerOpen(false)} open={drawerOpen} width={260}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '8px 0 20px', borderBottom: '1px solid #f0f0f0', marginBottom: 16 }}>
          <Avatar size={44} icon={<UserOutlined />} src={user?.avatarUrl} style={{ background: LOGO_GREEN }} />
          <div>
            <div style={{ fontWeight: 600 }}>{user?.fullName || user?.username}</div>
            <div style={{ fontSize: 12, color: '#888' }}>{user?.role === 'EMPLOYER' ? 'Nhà tuyển dụng' : 'Ứng viên'}</div>
          </div>
        </div>
        <Menu mode="inline" selectedKeys={[location.pathname]} items={navItems}
          onClick={({ key }) => { navigate(key); setDrawerOpen(false); }} style={{ border: 'none' }} />
        <div style={{ marginTop: 16 }}>
          <Button icon={<LogoutOutlined />} onClick={handleLogout} danger block>Đăng xuất</Button>
        </div>
      </Drawer>

      <Content style={{ background: '#f4f5f6' }}>{children}</Content>

      <Footer style={{ textAlign: 'center', background: '#fff', borderTop: '1px solid #e8e8e8', color: '#999', fontSize: 13, padding: '16px 24px' }}>
        TopJob © 2024 · Kết nối nhà tuyển dụng và ứng viên Việt Nam
      </Footer>

      {/* Modal xem & sửa profile */}
      <Modal
        title="Thông tin cá nhân"
        open={profileOpen}
        onCancel={() => setProfileOpen(false)}
        footer={profile ? [
          <Button key="edit" type="primary" icon={<EditOutlined />} onClick={openEdit}>Chỉnh sửa</Button>,
          <Button key="close" onClick={() => setProfileOpen(false)}>Đóng</Button>,
        ] : [<Button key="close" onClick={() => setProfileOpen(false)}>Đóng</Button>]}
        width={520}
      >
        {profileLoading ? (
          <div style={{ textAlign: 'center', padding: '32px 0' }}><Spin /></div>
        ) : profile ? (
          <>
            <div style={{ display: 'flex', alignItems: 'center', gap: 16, marginBottom: 20 }}>
              <div style={{ position: 'relative', flexShrink: 0 }}>
                <Avatar size={72} src={profile.avatarUrl} icon={<UserOutlined />} style={{ background: LOGO_GREEN }} />
                <div
                  onClick={() => avatarInputRef.current?.click()}
                  style={{
                    position: 'absolute', bottom: 0, right: 0,
                    width: 24, height: 24, borderRadius: '50%',
                    background: LOGO_GREEN, border: '2px solid #fff',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    cursor: 'pointer',
                  }}
                >
                  {avatarUploading ? <Spin size="small" /> : <CameraOutlined style={{ color: '#fff', fontSize: 11 }} />}
                </div>
                <input
                  ref={avatarInputRef}
                  type="file"
                  accept="image/*"
                  style={{ display: 'none' }}
                  onChange={handleAvatarChange}
                />
              </div>
              <div>
                <div style={{ fontWeight: 700, fontSize: 16 }}>{profile.fullName || profile.username}</div>
                <div style={{ color: '#888', fontSize: 13 }}>@{profile.username}</div>
                <div style={{ color: '#aaa', fontSize: 12, marginTop: 2 }}>Nhấn vào ảnh để thay đổi</div>
              </div>
            </div>
            <Descriptions column={1} bordered size="small" labelStyle={{ fontWeight: 600, width: 140 }}>
              <Descriptions.Item label={<span><MailOutlined /> Email</span>}>{profile.email || <span style={{ color: '#aaa' }}>Chưa cập nhật</span>}</Descriptions.Item>
              <Descriptions.Item label={<span><PhoneOutlined /> Điện thoại</span>}>{profile.phoneNumber || <span style={{ color: '#aaa' }}>Chưa cập nhật</span>}</Descriptions.Item>
              <Descriptions.Item label={<span><EnvironmentOutlined /> Địa chỉ</span>}>{profile.address || <span style={{ color: '#aaa' }}>Chưa cập nhật</span>}</Descriptions.Item>
              <Descriptions.Item label={<span><CalendarOutlined /> Ngày sinh</span>}>{profile.dateOfBirth || <span style={{ color: '#aaa' }}>Chưa cập nhật</span>}</Descriptions.Item>
            </Descriptions>
          </>
        ) : (
          <div style={{ textAlign: 'center', padding: '24px 0', color: '#aaa' }}>Không tìm thấy thông tin</div>
        )}
      </Modal>

      {/* Modal chỉnh sửa profile */}
      <Modal
        title="Chỉnh sửa thông tin"
        open={editOpen}
        onCancel={() => setEditOpen(false)}
        onOk={() => form.submit()}
        okText="Lưu"
        cancelText="Huỷ"
        confirmLoading={saving}
        width={480}
      >
        <Form form={form} layout="vertical" onFinish={handleSave} style={{ marginTop: 12 }}>
          <Form.Item name="fullName" label="Họ và tên" rules={[{ required: true, message: 'Vui lòng nhập họ tên' }]}>
            <Input placeholder="Họ và tên" />
          </Form.Item>
          <Form.Item name="email" label="Email" rules={[{ type: 'email', message: 'Email không hợp lệ' }]}>
            <Input placeholder="Email" />
          </Form.Item>
          <Form.Item name="phoneNumber" label="Số điện thoại">
            <Input placeholder="Số điện thoại" />
          </Form.Item>
          <Form.Item name="address" label="Địa chỉ">
            <Input placeholder="Địa chỉ" />
          </Form.Item>
          <Form.Item name="dateOfBirth" label="Ngày sinh">
            <DatePicker style={{ width: '100%' }} format="DD/MM/YYYY" placeholder="Chọn ngày sinh" />
          </Form.Item>
        </Form>
      </Modal>
    </Layout>
  );
}
