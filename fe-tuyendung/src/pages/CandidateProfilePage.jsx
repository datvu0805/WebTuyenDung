import React, { useEffect, useState } from 'react';
import {
  Avatar, Typography, Spin, message, Button, Form, Input,
  DatePicker, Upload, Tabs, Modal, Table, Space, Popconfirm, Select, Empty, Tag, InputNumber
} from 'antd';
import {
  UserOutlined, MailOutlined, PhoneOutlined, EnvironmentOutlined,
  CalendarOutlined, EditOutlined, CameraOutlined, PlusOutlined, DeleteOutlined,
  SafetyCertificateOutlined, BulbOutlined, StarFilled, DollarOutlined, BankOutlined, ReadOutlined,
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import {
  candidateApi, candidateCertificateApi, certificateApi, candidateSkillApi, skillApi, favoriteJobApi,
  candidateEducationApi, educationLevelApi,
} from '../api/services';
import AppLayout from '../components/AppLayout';
import dayjs from 'dayjs';

const { Title, Text } = Typography;
const { Option } = Select;
const GREEN = '#00b14f';

function ProfileTab({ profile, loading, onEdit, avatarUploading, handleAvatarUpload, candidateId }) {
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
          {
            icon: <DollarOutlined />, label: 'Mức lương mong muốn',
            value: (profile.desiredMinSalary || profile.desiredMaxSalary)
              ? `${(profile.desiredMinSalary || 0).toLocaleString('vi-VN')}${profile.desiredMaxSalary ? ' – ' + profile.desiredMaxSalary.toLocaleString('vi-VN') : ''} VNĐ`
              : null,
          },
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

      <SavedJobsSection candidateId={candidateId} />
    </>
  );
}

function SavedJobsSection({ candidateId }) {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (!candidateId) return;
    setLoading(true);
    favoriteJobApi.getByCandidate(candidateId)
      .then((res) => { if (res.data.success) setJobs(res.data.data || []); })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [candidateId]);

  const formatSalary = (job) => (job.minSalary || job.maxSalary)
    ? `${(job.minSalary || 0).toLocaleString('vi-VN')}${job.maxSalary ? ' – ' + job.maxSalary.toLocaleString('vi-VN') : ''} ${job.currency || 'VNĐ'}`
    : 'Thỏa thuận';

  return (
    <>
      <div
        onClick={() => setModalOpen(true)}
        style={{
          marginTop: 16, display: 'flex', alignItems: 'center', gap: 12,
          padding: '14px 16px', background: '#fafafa', borderRadius: 10, cursor: 'pointer',
        }}
      >
        <span style={{ color: '#faad14', fontSize: 18 }}><StarFilled /></span>
        <div style={{ flex: 1 }}>
          <Text style={{ fontSize: 12, color: '#aaa', display: 'block' }}>Việc làm đã lưu</Text>
          <Text style={{ fontSize: 14, fontWeight: 600 }}>
            {loading ? <Spin size="small" /> : `${jobs.length} công việc`}
          </Text>
        </div>
      </div>

      <Modal
        title={<span style={{ fontWeight: 700 }}>Việc làm đã lưu</span>}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        footer={null}
        width={560}
      >
        {loading ? (
          <div style={{ textAlign: 'center', padding: 32 }}><Spin /></div>
        ) : jobs.length === 0 ? (
          <Empty description="Bạn chưa lưu công việc nào" style={{ padding: 24 }} />
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10, maxHeight: 420, overflowY: 'auto' }}>
            {jobs.map((job) => (
              <div
                key={job.id}
                onClick={() => { setModalOpen(false); navigate(`/jobs/${job.id}`); }}
                style={{
                  padding: '12px 14px', borderRadius: 8, border: '1px solid #eee', cursor: 'pointer',
                }}
                onMouseEnter={(e) => { e.currentTarget.style.borderColor = '#b7ebd0'; }}
                onMouseLeave={(e) => { e.currentTarget.style.borderColor = '#eee'; }}
              >
                <Text strong style={{ fontSize: 14 }}>{job.title}</Text>
                {job.companyName && (
                  <div style={{ marginTop: 4 }}>
                    <Text style={{ fontSize: 12, color: GREEN }}>
                      <BankOutlined style={{ marginRight: 4 }} />{job.companyName}
                    </Text>
                  </div>
                )}
                <div style={{ marginTop: 6, display: 'flex', gap: 12, flexWrap: 'wrap' }}>
                  <Tag color="green" style={{ borderRadius: 4, fontSize: 12 }}>
                    <DollarOutlined style={{ marginRight: 4 }} />{formatSalary(job)}
                  </Tag>
                  {job.location && (
                    <Tag style={{ borderRadius: 4, fontSize: 12 }}>{job.location}</Tag>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </Modal>
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

function EducationTab({ candidateId }) {
  const [list, setList] = useState([]);
  const [allLevels, setAllLevels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [saving, setSaving] = useState(false);
  const [form] = Form.useForm();

  const load = () => {
    if (!candidateId) return;
    setLoading(true);
    Promise.all([
      candidateEducationApi.getByCandidate(candidateId),
      educationLevelApi.getAll(),
    ]).then(([mineRes, allRes]) => {
      if (mineRes.data.success) setList(mineRes.data.data || []);
      if (allRes.data.success) setAllLevels(allRes.data.data || []);
    }).catch(() => message.error('Tải danh sách học vấn thất bại'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, [candidateId]);

  const openCreate = () => { setEditing(null); form.resetFields(); setModalOpen(true); };
  const openEdit = (item) => {
    setEditing(item);
    form.setFieldsValue({
      ...item,
      startDate: item.startDate ? dayjs(item.startDate) : null,
      endDate: item.endDate ? dayjs(item.endDate) : null,
    });
    setModalOpen(true);
  };

  const onSave = async (values) => {
    setSaving(true);
    try {
      const payload = {
        ...values,
        candidateId,
        startDate: values.startDate?.format('YYYY-MM-DD') || null,
        endDate: values.endDate?.format('YYYY-MM-DD') || null,
      };
      const res = editing
        ? await candidateEducationApi.update({ ...payload, id: editing.id })
        : await candidateEducationApi.create(payload);
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
      await candidateEducationApi.delete(id);
      message.success('Đã xóa');
      load();
    } catch {
      message.error('Xóa thất bại');
    }
  };

  const getLevelName = (levelId) => allLevels.find(l => l.id === levelId)?.levelName || `#${levelId}`;

  const columns = [
    { title: 'Trình độ', dataIndex: 'educationLevelId', key: 'educationLevelId', render: (v) => getLevelName(v) },
    { title: 'Trường', dataIndex: 'schoolName', key: 'schoolName' },
    { title: 'Chuyên ngành', dataIndex: 'major', key: 'major', responsive: ['md'] },
    { title: 'GPA', dataIndex: 'gpa', key: 'gpa', width: 70 },
    {
      title: 'Thao tác', key: 'actions', width: 90,
      render: (_, r) => (
        <Space size={4}>
          <Button icon={<EditOutlined />} size="small" onClick={() => openEdit(r)} style={{ borderRadius: 6 }} />
          <Popconfirm title="Xóa học vấn này?" onConfirm={() => onDelete(r.id)} okText="Xóa" cancelText="Hủy">
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
          Thêm học vấn
        </Button>
      </div>
      <Table columns={columns} dataSource={list} rowKey="id" loading={loading}
        pagination={{ pageSize: 10, showSizeChanger: false }} />

      <Modal title={<span style={{ fontWeight: 700 }}>{editing ? 'Chỉnh sửa học vấn' : 'Thêm học vấn'}</span>}
        open={modalOpen} onCancel={() => setModalOpen(false)} footer={null} width={480}>
        <Form form={form} layout="vertical" onFinish={onSave} style={{ marginTop: 16 }}>
          <Form.Item name="educationLevelId" label={<span style={{ fontWeight: 600 }}>Trình độ</span>}
            rules={[{ required: true, message: 'Chọn trình độ' }]}>
            <Select placeholder="Chọn trình độ..." showSearch
              filterOption={(input, opt) => (opt?.children ?? '').toLowerCase().includes(input.toLowerCase())}
              style={{ borderRadius: 8 }}>
              {allLevels.map(l => <Option key={l.id} value={l.id}>{l.levelName}</Option>)}
            </Select>
          </Form.Item>
          <Form.Item name="schoolName" label={<span style={{ fontWeight: 600 }}>Tên trường</span>}>
            <Input placeholder="Đại học Bách Khoa..." style={{ borderRadius: 8 }} />
          </Form.Item>
          <Form.Item name="major" label={<span style={{ fontWeight: 600 }}>Chuyên ngành</span>}>
            <Input placeholder="Công nghệ thông tin..." style={{ borderRadius: 8 }} />
          </Form.Item>
          <Form.Item name="startDate" label={<span style={{ fontWeight: 600 }}>Ngày bắt đầu</span>}>
            <DatePicker style={{ width: '100%', borderRadius: 8 }} format="DD/MM/YYYY" />
          </Form.Item>
          <Form.Item name="endDate" label={<span style={{ fontWeight: 600 }}>Ngày kết thúc</span>}>
            <DatePicker style={{ width: '100%', borderRadius: 8 }} format="DD/MM/YYYY" />
          </Form.Item>
          <Form.Item name="gpa" label={<span style={{ fontWeight: 600 }}>GPA</span>}>
            <Input placeholder="3.5" style={{ borderRadius: 8 }} />
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

function SkillTab({ candidateId }) {
  const [selectedIds, setSelectedIds] = useState([]);
  const [allSkills, setAllSkills] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const load = () => {
    setLoading(true);
    Promise.all([
      candidateSkillApi.getByCandidate(candidateId),
      skillApi.getAll(),
    ]).then(([mineRes, allRes]) => {
      if (mineRes.data.success) setSelectedIds((mineRes.data.data || []).map(s => s.id));
      if (allRes.data.success) setAllSkills(allRes.data.data || []);
    }).catch(() => message.error('Tải danh sách kỹ năng thất bại'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { if (candidateId) load(); }, [candidateId]);

  const onSave = async () => {
    setSaving(true);
    try {
      const res = await candidateSkillApi.replaceBatch(selectedIds);
      if (res.data.success) message.success('Cập nhật kỹ năng thành công');
      else message.error(res.data.message || 'Cập nhật thất bại');
    } catch {
      message.error('Có lỗi xảy ra');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div style={{ textAlign: 'center', padding: '40px 0' }}><Spin size="large" /></div>;

  return (
    <div>
      <Text style={{ color: '#888', display: 'block', marginBottom: 12 }}>
        Chọn các kỹ năng của bạn để nhận gợi ý việc làm phù hợp hơn.
      </Text>
      <Select
        mode="multiple"
        allowClear
        placeholder="Chọn kỹ năng..."
        value={selectedIds}
        onChange={setSelectedIds}
        style={{ width: '100%', marginBottom: 16 }}
        optionFilterProp="children"
      >
        {allSkills.map(s => <Option key={s.id} value={s.id}>{s.skillName}</Option>)}
      </Select>
      <div style={{ textAlign: 'right' }}>
        <Button type="primary" onClick={onSave} loading={saving}
          style={{ borderRadius: 8, background: GREEN, borderColor: GREEN, fontWeight: 600 }}>
          Lưu kỹ năng
        </Button>
      </div>
    </div>
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
      const payload = {
        ...values,
        dateOfBirth: values.dateOfBirth?.format('YYYY-MM-DD') || '',
        desiredMinSalary: values.desiredMinSalary ?? null,
        desiredMaxSalary: values.desiredMaxSalary ?? null,
      };
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
      children: <ProfileTab profile={profile} loading={loading} onEdit={openEdit} avatarUploading={avatarUploading} handleAvatarUpload={handleAvatarUpload} candidateId={profile?.candidateId} />,
    },
    {
      key: 'certificates',
      label: <span><SafetyCertificateOutlined /> Chứng chỉ</span>,
      children: <CertificateTab candidateId={profile?.candidateId} />,
    },
    {
      key: 'education',
      label: <span><ReadOutlined /> Học vấn</span>,
      children: <EducationTab candidateId={profile?.candidateId} />,
    },
    {
      key: 'skills',
      label: <span><BulbOutlined /> Kỹ năng</span>,
      children: <SkillTab candidateId={profile?.candidateId} />,
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
          <Form.Item label={<span style={{ fontWeight: 600 }}>Mức lương mong muốn (VNĐ)</span>} style={{ marginBottom: 8 }}>
            <div style={{ display: 'flex', gap: 8 }}>
              <Form.Item name='desiredMinSalary' noStyle>
                <InputNumber style={{ width: '100%', borderRadius: 8 }} placeholder='Từ' min={0} step={1000000}
                  formatter={(v) => `${v}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                  parser={(v) => v.replace(/,/g, '')} />
              </Form.Item>
              <Form.Item name='desiredMaxSalary' noStyle>
                <InputNumber style={{ width: '100%', borderRadius: 8 }} placeholder='Đến' min={0} step={1000000}
                  formatter={(v) => `${v}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                  parser={(v) => v.replace(/,/g, '')} />
              </Form.Item>
            </div>
          </Form.Item>
          <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
            <Button onClick={() => setEditOpen(false)} style={{ borderRadius: 8 }}>Hủy</Button>
            <Button type='primary' htmlType='submit' loading={saving} style={{ borderRadius: 8, background: GREEN, borderColor: GREEN, fontWeight: 600 }}>Lưu thay đổi</Button>
          </div>
        </Form>
      </Modal>
    </AppLayout>
  );
}
