import React, { useEffect, useState } from 'react';
import {
  Avatar, Typography, Spin, message, Button, Form, Input,
  DatePicker, Upload, Tabs, Modal, Table, Space, Popconfirm, Select
} from 'antd';
import {
  UserOutlined, MailOutlined, PhoneOutlined, EnvironmentOutlined,
  CalendarOutlined, EditOutlined, CameraOutlined, PlusOutlined, DeleteOutlined,
  SafetyCertificateOutlined
} from '@ant-design/icons';
import { candidateApi, candidateCertificateApi, certificateApi } from '../api/services';
import AppLayout from '../components/AppLayout';
import dayjs from 'dayjs';

const { Title, Text } = Typography;
const { Option } = Select;
const GREEN = '#00b14f';

function ProfileTab({ profile, loading, onEdit, avatarUploading, handleAvatarUpload }) {
  if (loading) return <div style={{ textAlign: 'center', padding: '40px 0' }}><Spin size="large" /></div>;
  if (!profile) return <div style={{ textAlign: 'center', padding: '40px 0' }}><Text type="secondary">Không tìm thấy thông tin</Text></div>;

  return (
    <>
      <div style={{ display: 'flex', alignItems: 'center', gap: 20, marginBottom: 28 }}>
        <div style={{ position: 'relative', display: 'inline-block' }}>
          <Avatar src={profile.avatarUrl} size={80} icon={<UserOutlined />}
            style={{ background: GREEN, flexShrink: 0 }} />
          <Upload showUploadList={false} accept="image/*" customRequest={handleAvatarUpload} disabled={avatarUploading}>
            <Button
              shape="circle" size="small" icon={<CameraOutlined />}
              loading={avatarUploading}
              style={{
                position: 'absolute', bottom: 0, right: 0,
                background: '#fff', border: '1px solid #ddd', boxShadow: '0 1px 4px rgba(0,0,0,0.15)'
              }}
            />
          </Upload>
        </div>
        <div style={{ flex: 1 }}>
          <Title level={4} style={{ margin: 0 }}>{profile.fullName || profile.username}</Title>
          <Text style={{ color: '#888' }}>@{profile.username}</Text>
        </div>
        <Button icon={<EditOutlined />} onClick={onEdit} style={{ borderRadius: 8 }}>
          Chỉnh sửa
        </Button>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
        {[
          { icon: <MailOutlined />, label: 'Email', value: profile.email },
          { icon: <PhoneOutlined />, label: 'Số điện thoại', value: profile.phoneNumber },
          { icon: <EnvironmentOutlined />, label: 'Địa chỉ', value: profile.address },
          { icon: <CalendarOutlined />, label: 'Ngày sinh', value: profile.dateOfBirth },
        ].map(({ icon, label, value }) => (
          <div key={label} style={{ display: 'flex', gap: 12, alignItems: 'flex-start', padding: '12px 16px', background: '#fafafa', borderRadius: 10 }}>
            <span style={{ color: GREEN, fontSize: 16, marginTop: 2 }}>{icon}</span>
            <div>
              <Text style={{ fontSize: 12, color: '#aaa', display: 'block' }}>{label}</Text>
              <Text style={{ fontSize: 14 }}>{value || <Text type="secondary">Chưa cập nhật</Text>}</Text>
            </div>
          </div>
        ))}
      </div>
    </>
  );
}

function CertificateTab({ candidateId }) {
  const [list, setList] = useState([]);
  const [allCerts, setAllCerts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [saving, setSaving] = useState(false);
  const [form] = Form.useForm();

  const load = () => {
    setLoading(true);
    Promise.all([
      candidateCertificateApi.getAll(),
      certificateApi.getAll(),
    ]).then(([ccRes, certRes]) => {
      if (ccRes.data.success) {
        const all = ccRes.data.data || [];
        setList(candidateId ? all.filter(c => String(c.candidateId) === String(candidateId)) : all);
      }
      if (certRes.data.success) setAllCerts(certRes.data.data || []);
    }).catch(() => message.error('Tải danh sách thất bại'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, [candidateId]);

  const openCreate = () => { setEditing(null); form.resetFields(); setModalOpen(true); };
  const openEdit = (item) => {
    setEditing(item);
    form.setFieldsValue({
      ...item,
      issueDate: item.issueDate ? dayjs(item.issueDate) : null,
      expiryDate: item.expiryDate ? dayjs(item.expiryDate) : null,
    });
    setModalOpen(true);
  };

  const onSave = async (values) => {
    setSaving(true);
    try {
      const payload = {
        ...values,
        candidateId,
        issueDate: values.issueDate?.format('YYYY-MM-DD') || '',
        expiryDate: values.expiryDate?.format('YYYY-MM-DD') || '',
      };
      const res = editing
        ? await candidateCertificateApi.update({ ...payload, id: editing.id })
        : await candidateCertificateApi.create(payload);
      if (res.data.success) {
        message.success(editing ? 'Cập nhật thành công' : 'Thêm thành công');
        setModalOpen(false);
        load();
      } else {
        message.error(res.data.message);
      }
    } catch {
      message.error('Có lỗi xảy ra');
    } finally {
      setSaving(false);
    }
  };

  const onDelete = async (id) => {
    try {
      await candidateCertificateApi.delete(id);
      message.success('Đã xóa');
      load();
    } catch {
      message.error('Xóa thất bại');
    }
  };

  const getCertName = (certId) => allCerts.find(c => c.id === certId)?.certificateName || `Cert #${certId}`;

  const columns = [
    { title: 'Chứng chỉ', dataIndex: 'certificateId', key: 'certificateId', render: (v) => getCertName(v) },
    { title: 'Điểm', dataIndex: 'score', key: 'score' },
    { title: 'Ngày cấp', dataIndex: 'issueDate', key: 'issueDate' },
    { title: 'Hết hạn', dataIndex: 'expiryDate', key: 'expiryDate' },
    { title: 'Ghi chú', dataIndex: 'description', key: 'description', responsive: ['md'] },
    {
      title: 'Thao tác', key: 'actions', width: 90,
      render: (_, r) => (
        <Space size={4}>
          <Button icon={<EditOutlined />} size="small" onClick={() => openEdit(r)} style={{ borderRadius: 6 }} />
          <Popconfirm title="Xóa chứng chỉ này?" onConfirm={() => onDelete(r.id)} okText="Xóa" cancelText="Hủy">
            <Button icon={<DeleteOutlined />} size="small" danger style={{ borderRadius: 6 }} />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: 12 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}
          style={{ background: GREEN, borderColor: GREEN, borderRadius: 8, fontWeight: 600 }}>
          Thêm chứng chỉ
        </Button>
      </div>
      <Table columns={columns} dataSource={list} rowKey="id" loading={loading}
        pagination={{ pageSize: 10, showSizeChanger: false }} />

      <Modal title={<span style={{ fontWeight: 700 }}>{editing ? 'Chỉnh sửa chứng chỉ' : 'Thêm chứng chỉ'}</span>}
        open={modalOpen} onCancel={() => setModalOpen(false)} footer={null} width={480}>
        <Form form={form} layout="vertical" onFinish={onSave} style={{ marginTop: 16 }}>
          <Form.Item name="certificateId" label={<span style={{ fontWeight: 600 }}>Loại chứng chỉ</span>}
            rules={[{ required: true, message: 'Chọn chứng chỉ' }]}>
            <Select placeholder="Chọn chứng chỉ..." showSearch
              filterOption={(input, opt) => (opt?.children ?? '').toLowerCase().includes(input.toLowerCase())}
              style={{ borderRadius: 8 }}>
              {allCerts.map(c => <Option key={c.id} value={c.id}>{c.certificateName}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item name="score" label={<span style={{ fontWeight: 600 }}>Điểm</span>}>
            <Input placeholder="7.5" style={{ borderRadius: 8 }} />
          </Form.Item>
          <Form.Item name="issueDate" label={<span style={{ fontWeight: 600 }}>Ngày cấp</span>}>
            <DatePicker style={{ width: '100%', borderRadius: 8 }} format="DD/MM/YYYY" />
          </Form.Item>
          <Form.Item name="expiryDate" label={<span style={{ fontWeight: 600 }}>Ngày hết hạn</span>}>
            <DatePicker style={{ width: '100%', borderRadius: 8 }} format="DD/MM/YYYY" />
          </Form.Item>
          <Form.Item name="description" label={<span style={{ fontWeight: 600 }}>Ghi chú</span>}>
            <Input.TextArea rows={2} style={{ borderRadius: 8 }} />
          </Form.Item>
          <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
            <Button onClick={() => setModalOpen(false)} style={{ borderRadius: 8 }}>Hủy</Button>
            <Button type="primary" htmlType="submit" loading={saving}
              style={{ borderRadius: 8, background: GREEN, borderColor: GREEN, fontWeight: 600 }}>
              {editing ? 'Lưu thay đổi' : 'Thêm'}
            </Button>
          </div>
        </Form>
      </Modal>
    </>
  );
}

export default function CandidateProfilePage() {
  const [profile, setProfile] = React.useState(null);
  const [loading, setLoading] = React.useState(true);
  const [editOpen, setEditOpen] = React.useState(false);
  const [saving, setSaving] = React.useState(false);
  const [avatarUploading, setAvatarUploading] = React.useState(false);
  const [form] = Form.useForm();

  const loadProfile = () => {
    candidateApi.getProfile()
      .then((res) => {
        if (res.data.success) setProfile(res.data.data);
        else message.error(res.data.message);
      })
      .catch(() => message.error('Không thể tải thông tin'))
      .finally(() => setLoading(false));
  };

  React.useEffect(() => { loadProfile(); }, []);

  const openEdit = () => {
    form.setFieldsValue({
      ...profile,
      dateOfBirth: profile?.dateOfBirth ? dayjs(profile.dateOfBirth) : null,
    });
    setEditOpen(true);
  };

  const onSave = async (values) => {
    setSaving(true);
    try {
      const payload = { ...values, dateOfBirth: values.dateOfBirth?.format('YYYY-MM-DD') || '' };
      const res = await candidateApi.updateProfile(payload);
      if (res.data.success) { message.success('Cập nhật thành công'); setEditOpen(false); loadProfile(); }
      else message.error(res.data.message || 'Cập nhật thất bại');
    } catch { message.error('Có lỗi xảy ra'); }
    finally { setSaving(false); }
  };

  const handleAvatarUpload = async ({ file }) => {
    setAvatarUploading(true);
    try {
      const res = await candidateApi.uploadAvatar(file);
      if (res.data.success) { message.success('Cập nhật ảnh đại diện thành công'); loadProfile(); }
      else message.error(res.data.message || 'Tải ảnh thất bại');
    } catch { message.error('Có lỗi khi tải ảnh'); }
    finally { setAvatarUploading(false); }
    return false;
  };

  const tabItems = [
    {
      key: 'profile',
      label: <span><UserOutlined /> Thông tin</span>,
      children: <ProfileTab profile={profile} loading={loading} onEdit={openEdit} avatarUploading={avatarUploading} handleAvatarUpload={handleAvatarUpload} />,
    },
    {
      key: 'certificates',
      label: <span><SafetyCertificateOutlined /> Chứng chỉ</span>,
      children: <CertificateTab candidateId={profile?.candidateId} />,
    },
  ];

  return (
    <AppLayout>
      <div style={{ background: 'linear-gradient(135deg, #00b14f 0%, #007a38 100%)', padding: '28px 24px 56px' }}>
        <div style={{ maxWidth: 800, margin: '0 auto' }}>
          <Title level={3} style={{ color: '#fff', marginBottom: 4 }}>Hồ sơ của tôi</Title>
          <Text style={{ color: 'rgba(255,255,255,0.8)' }}>Thông tin cá nhân của bạn</Text>
        </div>
      </div>
      <div style={{ maxWidth: 800, margin: '-28px auto 0', padding: '0 16px 40px' }}>
        <div style={{ background: '#fff', borderRadius: 16, padding: '24px 32px', boxShadow: '0 2px 16px rgba(0,0,0,0.08)' }}>
          <Tabs defaultActiveKey='profile' items={tabItems} />
        </div>
      </div>
      <Modal title={<span style={{ fontWeight: 700 }}>Chỉnh sửa thông tin</span>} open={editOpen} onCancel={() => setEditOpen(false)} footer={null} width={520}>
        <Form form={form} layout='vertical' onFinish={onSave} style={{ marginTop: 16 }}>
          <Form.Item name='fullName' label={<span style={{ fontWeight: 600 }}>Họ và tên</span>}><Input style={{ borderRadius: 8 }} placeholder='Nguyễn Văn A' /></Form.Item>
          <Form.Item name='email' label={<span style={{ fontWeight: 600 }}>Email</span>} rules={[{ type: 'email', message: 'Email không hợp lệ' }]}><Input style={{ borderRadius: 8 }} /></Form.Item>
          <Form.Item name='phoneNumber' label={<span style={{ fontWeight: 600 }}>Số điện thoại</span>}><Input style={{ borderRadius: 8 }} /></Form.Item>
          <Form.Item name='address' label={<span style={{ fontWeight: 600 }}>Địa chỉ</span>}><Input style={{ borderRadius: 8 }} /></Form.Item>
          <Form.Item name='dateOfBirth' label={<span style={{ fontWeight: 600 }}>Ngày sinh</span>}><DatePicker style={{ width: '100%', borderRadius: 8 }} format='DD/MM/YYYY' /></Form.Item>
          <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
            <Button onClick={() => setEditOpen(false)} style={{ borderRadius: 8 }}>Hủy</Button>
            <Button type='primary' htmlType='submit' loading={saving} style={{ borderRadius: 8, background: GREEN, borderColor: GREEN, fontWeight: 600 }}>Lưu thay đổi</Button>
          </div>
        </Form>
      </Modal>
    </AppLayout>
  );
}
