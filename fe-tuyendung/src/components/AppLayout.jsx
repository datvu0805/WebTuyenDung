import React, { useState } from 'react';
import { Layout, Menu, Avatar, Dropdown, Button, Typography, Drawer } from 'antd';
import {
  BankOutlined, UserOutlined, LogoutOutlined, FileTextOutlined,
  UnorderedListOutlined, MenuOutlined, TeamOutlined, BulbOutlined,
  CaretDownOutlined
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const { Header, Content, Footer } = Layout;
const { Text } = Typography;
const LOGO_GREEN = '#00b14f';

export default function AppLayout({ children }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [drawerOpen, setDrawerOpen] = useState(false);

  const handleLogout = () => { logout(); navigate('/login'); };

  const candidateNav = [
    { key: '/jobs', icon: <UnorderedListOutlined />, label: 'Việc làm' },
    { key: '/cv/upload', icon: <FileTextOutlined />, label: 'Hồ sơ CV' },
  ];
  const employerNav = [
    { key: '/employer/dashboard', icon: <TeamOutlined />, label: 'Tin tuyển dụng' },
    { key: '/employer/applications', icon: <UnorderedListOutlined />, label: 'Đơn ứng tuyển' },
    { key: '/employer/skills', icon: <BulbOutlined />, label: 'Kỹ năng' },
  ];
  const navItems = user?.role === 'EMPLOYER' ? employerNav : candidateNav;
  const homeRoute = user?.role === 'EMPLOYER' ? '/employer/dashboard' : '/jobs';

  const userMenuItems = [
    {
      key: 'info', disabled: true,
      label: (
        <div style={{ padding: '4px 0' }}>
          <div style={{ fontWeight: 600 }}>{user?.fullName || user?.username}</div>
          <div style={{ fontSize: 12, color: '#888' }}>
            {user?.role === 'EMPLOYER' ? 'Nhà tuyển dụng' : 'Ứng viên'}
          </div>
        </div>
      ),
    },
    { type: 'divider' },
    { key: 'logout', icon: <LogoutOutlined />, label: 'Đăng xuất', onClick: handleLogout },
  ];

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
          <Dropdown menu={{ items: userMenuItems }} placement="bottomRight" trigger={['click']}>
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
    </Layout>
  );
}
